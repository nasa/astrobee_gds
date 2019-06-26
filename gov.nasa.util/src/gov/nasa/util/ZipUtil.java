/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
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
package gov.nasa.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class ZipUtil {
	
	private static Logger logger = Logger.getLogger(ZipUtil.class);
	
	public static final int BUFFER = 2048;
	   

	/**
	 * Given the full input path, unzip it to the file specified in the output path.
	 * @param outputZipFile The directory which will contain the contents of the zip file
	 * @param inputFilePath The path to the (non-remote) zip file
	 * @return
	 * @throws IOException 
	 * TODO add support for a generic progress feedback
	 */
	public static File doUnzip(String outputZipFile, File inputFile) throws IOException{
		
		String root = outputZipFile;
		if (!root.endsWith(File.separator)){
			root = root.concat(File.separator);
		}

		// make the destination directory
		File outFile = new File(outputZipFile);
		FileUtils.forceMkdir(outFile);
		
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream(inputFile);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		while((entry = zis.getNextEntry()) != null) {
			//System.out.println("Extracting: " +entry);
			int count;
			byte data[] = new byte[BUFFER];
			
			if (entry.isDirectory()){
				File newFile = new File(root + entry.getName());
				FileUtils.forceMkdir(newFile);
			} else {
				// make sure the director exists, sometimes there is not an entry for a directory
				String newDirectory = entry.getName();
				int lastSlash = newDirectory.lastIndexOf("/");
				if (lastSlash > 0){
					newDirectory = newDirectory.substring(0, lastSlash);
					File newFile = new File(root + newDirectory);
					FileUtils.forceMkdir(newFile);
				}
				
				// write the files to the disk
				FileOutputStream fos = new FileOutputStream(root + entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
		}
		zis.close();
		
		return outFile;
		
	}

	/**
	 * FIXME: this method looks like it will fail on a file of any reasonable size
	 * 
	 * given the full input path, provide the zip file specified in the full
	 * output path
	 * 
	 * @param outputZipFile
	 *            full output file path, including filename
	 * @param inputFilePath
	 *            full input file path, specific filename optional
	 * @return
	 */
	public static File doZip(String outputZipFile, String inputFilePath) {
		try {
			File outputFile = new File(outputZipFile);
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
			ZipEntry entry = new ZipEntry(inputFilePath);

			// FIXME need to loop this and check that the files have completely been
			// read in and processed
			byte[] buf = new byte[1024];
			FileInputStream fis = new FileInputStream(inputFilePath);
			fis.read(buf, 0, buf.length);

			CRC32 crc = new CRC32();
			ZipOutputStream s = new ZipOutputStream(outputStream);

			s.setLevel(6);

			entry.setSize(buf.length);
			crc.reset();
			crc.update(buf);

			// stick CRC into file for data checking
			entry.setCrc(crc.getValue());

			s.putNextEntry(entry);
			s.write(buf, 0, buf.length);
			s.finish();
			s.close();
			
			fis.close();

			return outputFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Get an InputStream from a local or remote zipped file.
	 * uri could be something like:
	 * archive:http://bla.bla/bla.zip!/nested/path/within/file.bla
	 * 
	 * or
	 * archive:file://local/path/bla.zip!/file.bla
	 * 
	 * extension does not have to be zip, it can be kmz for example, or anything.
	 * @param uri the uri for the zip file we are reading from
	 * 
	 */
	public static  InputStream createInputStream(URI uri) throws IOException{
		// Construct the input stream in a special efficient way for case of a file scheme.
		//
		String urlString = uri.toString();
		String nestedURL = urlString;
		if (nestedURL.indexOf("archive:") == 0){
			nestedURL = nestedURL.substring(8);
			if (nestedURL.indexOf("!/") >= 0){
				nestedURL = nestedURL.substring(0, nestedURL.indexOf("!/"));
			}
		}
		
		// The cutoff point to the next archive.
	    //
	    int archiveSeparator = urlString.indexOf(nestedURL) + nestedURL.length();
	    int nextArchiveSeparator = urlString.indexOf("!/", archiveSeparator + 2);
	   
		InputStream inputStream;
		ZipEntry inputZipEntry = null;
		if ( !nestedURL.startsWith("file:"))
		{
			// Just get the stream from the URL.
			//
			try
		    {
		      URL url = new URL(nestedURL);
		      final URLConnection urlConnection = url.openConnection();
		      inputStream = urlConnection.getInputStream();
		    }
		    catch (RuntimeException exception)
		    {
		      throw new IOException(exception);
		    }
		}
		else
		{
			
			// For local zip files, we can use the zipFile to get an input stream for the particular entry.
			
			// The name to be used for the entry.
			//
			String entry = 
				URIUtil.decode(nextArchiveSeparator < 0 ?
						urlString.substring(archiveSeparator + 2) :
							urlString.substring(archiveSeparator + 2, nextArchiveSeparator));

			// Skip over this archive path to the next one, since we are handling this one specially.
			//
			archiveSeparator = nextArchiveSeparator;
			nextArchiveSeparator = urlString.indexOf("!/", archiveSeparator + 2);

			// Go directly to the right entry in the zip file, 
			// get the stream, and wrap it so that closing it closes the zip file.
			//
			final ZipFile zipFile = new ZipFile(URIUtil.decode(nestedURL.substring(5)));
			inputZipEntry = zipFile.getEntry(entry);
			InputStream zipEntryInputStream = inputZipEntry == null ? null : zipFile.getInputStream(inputZipEntry);
			if (zipEntryInputStream == null)
			{
				throw new IOException("Archive entry not found " + urlString);
			}
			inputStream = new FilterInputStream(zipEntryInputStream)
			{
				// Special close to close the file and the entry so we don't have to worry about open stream.
				@Override
				public void close() throws IOException
				{
					super.close();
					zipFile.close();
				}
			};
		}

		// If we do NOT have a local file we have to work harder.
		//
		// Loop over the archive paths.
		//
		LOOP:
			while (archiveSeparator > 0)
			{
				inputZipEntry = null;

				// The entry name to be matched.
				//
			 String entry = 
					URIUtil.decode(nextArchiveSeparator < 0 ?
							urlString.substring(archiveSeparator + 2) :
								urlString.substring(archiveSeparator + 2, nextArchiveSeparator));

				// Wrap the input stream as a zip stream to scan it's contents for a match.
				//
				ZipInputStream zipInputStream = new ZipInputStream(inputStream);
				ZipEntry zipEntry = null;
				while (zipInputStream.available() >= 0)
				{
					zipEntry = zipInputStream.getNextEntry();
					if (zipEntry == null)
					{
						break;
					}
					else if (zipEntry.getName().endsWith(entry)) 
					{
						inputZipEntry = zipEntry;
						inputStream = zipInputStream;
						
						// Skip to the next archive path and continue the loop.
						//
						archiveSeparator = nextArchiveSeparator;
						nextArchiveSeparator = urlString.indexOf("!/", archiveSeparator + 2);
						continue LOOP;
					}
				}

				zipInputStream.close();
				throw new IOException("Archive entry not found " + urlString);
			}

		
		return inputStream;
	}

	/*
	 * Extract a file from a local or remote zip file
	 * Fill the outFile with the contents of this file
	 * @param uri to the local or remote zip file
	 * @param outFile where you want the contents to go (this should be legal, ie in a real writable place)
	 * @return true if this worked
	 */
	public static boolean getFileFromZip(URI uri, File outFile){
		ByteArrayOutputStream bs = null;
		FileOutputStream os = null;
		InputStream is = null;
		try {
			
			is = createInputStream(uri);
			
			if (is != null){
				bs = new ByteArrayOutputStream();
				byte buffer[] = new byte[8192];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					bs.write(buffer, 0, bytesRead);
				} 
				os = new FileOutputStream(outFile);
				bs.writeTo(os);
				
			}
			return true;
			
		} catch (MalformedURLException e2){
			logger.error(e2);
		} catch (IOException e) {
			logger.error(e);
		} finally{
			if (bs != null){
				try {
					bs.close();
				} catch (IOException e) {
					logger.debug(e);
				}
			}
			if (os != null){
				try {
					os.close();
				} catch (IOException e) {
					logger.debug(e);
				}
			}
			if (is != null){
				try {
					is.close();
				} catch (IOException e) {
					logger.debug(e);
				}
			}
		}
		return false;
	}
	
}
