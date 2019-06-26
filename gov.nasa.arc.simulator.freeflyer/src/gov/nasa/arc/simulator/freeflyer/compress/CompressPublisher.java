package gov.nasa.arc.simulator.freeflyer.compress;

/*******************************************************************************
 * Copyright (c) 2014 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import gov.nasa.arc.irg.freeflyer.rapid.LogEntry;
import gov.nasa.arc.irg.freeflyer.rapid.LogPoster;
import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.Deflater;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import rapid.ext.astrobee.CompressedFile;
import rapid.ext.astrobee.CompressedFileDataWriter;
import rapid.ext.astrobee.FileCompressionType;

import com.rti.dds.infrastructure.InstanceHandle_t;

/**
 * Publishes PositionConfig and PositionSamples
 */
public class CompressPublisher {
	private static final Logger logger = Logger.getLogger(CompressPublisher.class);
	protected static HashMap<String,CompressPublisher> instances = new HashMap<String,CompressPublisher>();
	
	protected int sleepTime           = 100;
	public static final int MESSAGE_LIFECYCLE_LIMIT = 100; 

	protected final String srcName    = CompressPublisher.class.getSimpleName();
	public String PLUGIN_PATH;

	protected CompressedFile           sample;
	protected CompressedFileDataWriter sampleWriter;
	protected InstanceHandle_t         sampleInstance;

	public static CompressPublisher getInstance(String agent) {
		if(instances.get(agent) == null) {
			CompressPublisher newbie = new CompressPublisher();

			try {
				newbie.createWriters(FreeFlyer.PARTICIPANT_ID,
						MessageTypeExtAstro.CURRENT_PLAN_COMPRESSED_TYPE,
						 FreeFlyer.getPartition());
				newbie.initializeDataTypes(agent);
				newbie.constructPluginPath();
			} catch (DdsEntityCreationException e) {
				e.printStackTrace();
			}
			instances.put(agent, newbie);
		}
		return instances.get(agent);
	}

	protected CompressPublisher() {
	}

	public void createWriters(String participantID, MessageType messageType, String partition) throws DdsEntityCreationException {
		sampleWriter = (CompressedFileDataWriter)
				RapidEntityFactory.createDataWriter(participantID,
						messageType,
						partition);  
	}

	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes(String agent) {
		final int serialId = 0;
		sample = new CompressedFile();
		RapidUtil.setHeader(sample.hdr, agent, srcName, serialId);
		sample.compressionType = FileCompressionType.COMPRESSION_TYPE_DEFLATE;
		
		//-- register the data instances *after* we have set
		//   assetName and participantName in headers (i.e. the keyed fields)
		if(sampleWriter != null)
			sampleInstance = sampleWriter.register_instance(sample);
	}

	public File compress(File file){
		try{

			final byte[] byteArray = IOUtils.toByteArray(new FileInputStream(file));

			final Deflater deflater = new Deflater();
			deflater.setInput(byteArray);
			deflater.finish();

			final ByteArrayOutputStream baos = new ByteArrayOutputStream(byteArray.length);
			final byte[] buffer = new byte[1024];
			while(!deflater.finished()){
				baos.write(buffer,0,deflater.deflate(buffer));
			}
			deflater.end();

			final File deflateFile = File.createTempFile("plan", ".dfz");
			FileUtils.writeByteArrayToFile(deflateFile, baos.toByteArray());
			baos.close();

			return deflateFile;

		}catch(Exception e){
			System.err.println(e);
		}
		return null;
	}
	
	public void compressAndPublishSample(String logMsg, File unCompressedFile){
		publishSamples(compress(unCompressedFile));
		LogPoster.postToLog(LogEntry.FILE, logMsg, sample.hdr.assetName);
	}
	
	public void republishCompressedFile(CompressedFile compressedFile) {
		sample.compressedFile.userData.clear();
		sample.compressedFile.userData.add(compressedFile);

		if (sampleWriter != null && sample != null) {
			sampleWriter.write(sample, sampleInstance);
		} else {
			logger.info("sampleWriter is null");
		}
	}
	
	public void publishSamples(File compressedFile) {

		try {
			sample.compressedFile.userData.clear();
			sample.compressedFile.userData.addAllByte(IOUtils.toByteArray(new FileInputStream(compressedFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (sampleWriter != null && sample != null) {
			sampleWriter.write(sample, sampleInstance);
		} else {
			logger.info("sampleWriter is null");
		}
	}

	private void constructPluginPath() {
		final String pluginPath = getClass().getResource("").getPath();
		final String locationMask = getClass().getPackage().getName().replace(".", File.separator);
		PLUGIN_PATH = pluginPath.replace("bin"+File.separator+locationMask+File.separator,"");
	}
}
