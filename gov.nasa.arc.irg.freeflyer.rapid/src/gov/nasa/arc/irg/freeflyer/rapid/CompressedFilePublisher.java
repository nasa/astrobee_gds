/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.irg.freeflyer.rapid;

import gov.nasa.arc.irg.plan.freeflyer.config.KeepoutConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.ZonesConfig;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;
import gov.nasa.arc.irg.plan.ui.io.KeepoutConfigLoaderAndWriter;
import gov.nasa.arc.irg.plan.ui.io.ZonesConfigLoaderAndWriter;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.WriterStatus;
import gov.nasa.rapid.v2.e4.message.publisher.RapidMessagePublisher;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import rapid.ext.astrobee.CompressedFile;
import rapid.ext.astrobee.FileCompressionType;

import com.rti.dds.infrastructure.ByteSeq;
import com.rti.dds.publication.PublicationMatchedStatus;

/**
 * To publish keepouts, dataToDisk, and plans.
 * Supersedes CompressPublisher and children
 * @author ddwheele
 *
 */
public class CompressedFilePublisher {
	private static final Logger logger = Logger.getLogger(CompressedFilePublisher.class);

	protected RapidMessagePublisher rapidMessagePublisher;
	protected static HashMap<Agent,CompressedFilePublisher> instances = new HashMap<Agent,CompressedFilePublisher>();
	protected Agent        	self;
	protected Agent			freeFlyerAgent;
	protected String       	partition;
	protected String        participant;
	protected final String srcName = CompressedFilePublisher.class.getSimpleName();
	protected ZonesConfig zonesConfig;

	private final String SEND_ZONES_LOG_STRING = "Upload Keepout Zone Files";

	public static CompressedFilePublisher getInstance(Agent agent) {
		if(instances.get(agent) == null) {
			instances.put(agent, new CompressedFilePublisher(agent));
		}
		return instances.get(agent);
	}

	private CompressedFilePublisher(Agent agent) {
		partition = agent.name();
		participant = Rapid.PrimaryParticipant;
		self = Agent.getEgoAgent();
		freeFlyerAgent = agent;
		rapidMessagePublisher = RapidMessagePublisher.get(agent);
	}

	protected void addKeepoutFilesToZonesConfig(File[] files) {
		if(files == null) {
			return;
		}
		long lastModified = -1;
		try {
			lastModified = Long.parseLong(zonesConfig.getTimestamp());
		} catch (NumberFormatException e) {
		}

		// for all the keepout files
		for(File f : files) {
			try {
				long mod = f.lastModified();
				if(mod > lastModified) {
					zonesConfig.setTimestamp(Long.toString(mod));
					lastModified = mod;
				}

				KeepoutConfig kconf = KeepoutConfigLoaderAndWriter.loadFromFile(f.getAbsolutePath());
				zonesConfig.addZone(kconf);

			} catch (Exception e) {
				System.out.println("Problem loading keepout/keepin files");
				e.printStackTrace();
			}
		}
	}

	protected void makeTheZonesConfig() {
		zonesConfig = new ZonesConfig();
		addKeepoutFilesToZonesConfig(ConfigFileWrangler.getInstance().getKeepoutFiles());
		addKeepoutFilesToZonesConfig(ConfigFileWrangler.getInstance().getKeepinFiles());
	}


	public void sendKeepoutZones() throws IOException {	
		makeTheZonesConfig();

		// write the file
		String zonesFilename = ConfigFileWrangler.getInstance().getConfigDirPath() + File.separator + "ComputedZonesConfig.json";
		try {
			ZonesConfigLoaderAndWriter.write(zonesFilename, zonesConfig);
		} catch(Exception e) {
			System.out.println("ERROR WRITING ZONES CONFIG FILE");
		}

		// send the file
		compressAndSendFile(SEND_ZONES_LOG_STRING, MessageTypeExtAstro.ZONES_COMPRESSED_TYPE, new File(zonesFilename));

	}

	public int compressAndSendFile(String logMsg, MessageType msgType, File uncompressedFile) {
		if(!rapidMessagePublisher.createWriter(participant, msgType)) {
			logger.info("Failed to create writer");
			return -1;
		}
		
		waitForMatch(participant, msgType);
		
		final File compressedFile = compressOneFile(uncompressedFile);
		final CompressedFile cf = new CompressedFile();

		RapidUtil.setHeader(cf.hdr, freeFlyerAgent, srcName, 0);
		cf.compressionType = FileCompressionType.COMPRESSION_TYPE_DEFLATE;
		cf.id = -1;

		try {
			cf.compressedFile.userData.addAllByte(IOUtils.toByteArray(new FileInputStream(compressedFile)));
			cf.id = (int) System.currentTimeMillis();
			if(!rapidMessagePublisher.writeMessage(participant, msgType, cf)) {
				logger.info("writeMessage returned false");
			}
			LogPoster.postToLog(LogEntry.FILE, logMsg + " " + cf.id, freeFlyerAgent.name());
		} catch (Exception e) {
			logger.error("Unable to compress or send CompressedFile", e);
		}

		return cf.id;
	}

	public void compressAndSendFile(String logMsg, MessageType msgType, File[] uncompressedFiles) {
		final File compressedFile = compress(uncompressedFiles);
		final CompressedFile cf = new CompressedFile();

		RapidUtil.setHeader(cf.hdr, freeFlyerAgent, srcName, 0);
		cf.compressionType = FileCompressionType.COMPRESSION_TYPE_DEFLATE;

		try {
			cf.id = (int) System.currentTimeMillis();
			cf.compressedFile.userData.addAllByte(IOUtils.toByteArray(new FileInputStream(compressedFile)));
			rapidMessagePublisher.writeMessage(participant, msgType, cf);
		} catch (Exception e) {
			logger.error("Unable to compress or send CompressedFile", e);
		}

		LogPoster.postToLog(LogEntry.FILE, logMsg, freeFlyerAgent.name());
	}

	private File compressOneFile(File file) {
		// old way before we switched to zipping. 
		final ByteArrayOutputStream baos;
		File deflateFile = null;
		try {
			final byte[] byteArray = IOUtils.toByteArray(new FileInputStream(file));

			final Deflater deflater = new Deflater();
			deflater.setInput(byteArray);
			deflater.finish();

			baos = new ByteArrayOutputStream(byteArray.length);
			final byte[] buffer = new byte[1024];
			while(!deflater.finished()){
				baos.write(buffer,0,deflater.deflate(buffer));
			}
			deflater.end();

			deflateFile = File.createTempFile("plan", ".dfz");

			FileUtils.writeByteArrayToFile(deflateFile, baos.toByteArray());
			baos.close();
		} catch (IOException e) {
			System.err.println(e);
		}

		return deflateFile;
	}

	private File compress(File[] files){
		try{
			final File zipFile = File.createTempFile("plan", ".zip");

			final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
			for(File f : files){
				zos.putNextEntry(new ZipEntry(f.getName()));
				zos.write(IOUtils.toByteArray(new FileInputStream(f)));
				zos.closeEntry();
			}
			zos.close();
			return zipFile;

		}catch(Exception e){
			System.err.println(e);
		}
		return null;
	}
	
	public static ModuleBayPlan uncompressCurrentPlanCompressedFile(CompressedFile compressedFile) {
		ModuleBayPlan plan = null;

		try{
			final ByteSeq seq = compressedFile.compressedFile.userData;

			final Inflater inflater = new Inflater();
			inflater.setInput(seq.toArrayByte(new byte[seq.size()]));

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final byte[] buf = new byte[1024];
			while(!inflater.finished()){
				baos.write(buf,0,inflater.inflate(buf));
			}
			inflater.end();
			final File recievedFile = File.createTempFile("tmpPlan-"+System.currentTimeMillis(), ".fplan");
			FileUtils.writeByteArrayToFile(recievedFile, baos.toByteArray());

			final PlanBuilder<ModuleBayPlan> m_planBuilder = PlanBuilder.getPlanBuilder(recievedFile, ModuleBayPlan.class,true);
			plan = m_planBuilder.getPlan();
			recievedFile.delete();
			
		}catch(Exception e){
			System.err.println(e);
		}
		return plan;
	}
	
	public static ModuleBayPlan uncompressCurrentZonesCompressedFile(CompressedFile compressedFile) {
		ModuleBayPlan plan = null;

		try{
			final ByteSeq seq = compressedFile.compressedFile.userData;

			final Inflater inflater = new Inflater();
			inflater.setInput(seq.toArrayByte(new byte[seq.size()]));

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final byte[] buf = new byte[1024];
			while(!inflater.finished()){
				baos.write(buf,0,inflater.inflate(buf));
			}
			inflater.end();
			final File recievedFile = File.createTempFile("tmpPlan-"+System.currentTimeMillis(), ".fplan");
			FileUtils.writeByteArrayToFile(recievedFile, baos.toByteArray());

			final PlanBuilder<ModuleBayPlan> m_planBuilder = PlanBuilder.getPlanBuilder(recievedFile,ModuleBayPlan.class,true);
			plan = m_planBuilder.getPlan();
			recievedFile.delete();
			
		}catch(Exception e){
			System.err.println(e);
		}
		return plan;
	}

	protected void waitForMatch(String participant, MessageType mt) {
		boolean waitingForMatch = true;
		while(waitingForMatch) {
			PublicationMatchedStatus matchedStatus = 
					(PublicationMatchedStatus) rapidMessagePublisher.getWriterStatus(
							WriterStatus.PublicationMatched, 
							participant, 
							mt,  
							new PublicationMatchedStatus());

			if(matchedStatus != null && matchedStatus.current_count > 0) {
				waitingForMatch = false;
//				logger.info("\tGot matched readers for " + mt.toString() + " on " + rapidMessagePublisher.getAgent());
			} else {
				logger.info("\tNo matched readers for " + mt.toString() + " on " + rapidMessagePublisher.getAgent());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
