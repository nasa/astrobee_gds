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
package gov.nasa.arc.irg.iss.ui;

import gov.nasa.arc.irg.util.log.IrgLoggingConfigurator;
import gov.nasa.util.StrUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class IssLoggingConfigurator extends IrgLoggingConfigurator {
	
	protected static String m_logDirectory = "";

	protected static  String s_rootLogFileName = "ISSWorkbench";
	protected static  String s_directoryName = "Logs_Runtime";
	protected static  String s_propertiesFileName = "issLog4j.properties";

	/**
	 * Force initialization always, we don't need the other init.
	 * @param args  Filename, directory name, properties file name.
	 */
	public static synchronized void init(String ... args) {
		if(INSTANCE == null) {
			INSTANCE = new IssLoggingConfigurator();
			if (args.length > 0){
				s_rootLogFileName = args[0];
				if (args.length > 1){
					s_directoryName = args[1];
				}
				if (args.length > 2){
					s_propertiesFileName = args[2];
				}
			}
			
			File logsRuntimeDirectory = new File(s_directoryName);
			try {
				FileUtils.forceMkdir(logsRuntimeDirectory);
			} 
			catch (IOException e) {
			    System.err.println("Error creating logsRuntimeDirectory: "+e.getMessage());
			    e.printStackTrace();
			}
			
			if (logsRuntimeDirectory.exists()){
				try {
					m_logDirectory = logsRuntimeDirectory.getCanonicalPath();
				} 
				catch (IOException e) {
	                System.err.println("Error getting path of logsRuntimeDirectory: "+e.getMessage());
	                e.printStackTrace();
				}
			} 
			
			INSTANCE.setLogFileName(s_rootLogFileName + getDateString() + ".log");
			INSTANCE.configureLogging(true);
			
		}
	}
	

	@Override
    public String getLogFileDirectoryName() {
		return m_logDirectory;
	}

	
	@Override
	protected String getPropertiesFilePath() {
		IPath path  = new Path(File.separator + "src" + File.separator + StrUtil.getClasspathAsDirectoryName(this.getClass()) + File.separator + s_propertiesFileName); 

		URL entryPath = FileLocator.find(IssUiActivator.getDefault().getBundle(), path,	Collections.EMPTY_MAP);
		if (entryPath != null){
			URL fileURL;
			try {
				fileURL = FileLocator.toFileURL(entryPath);
				return fileURL.getPath();
			} 
			catch (IOException e) {
				System.err.println("Could not load Log4j properties file at " + path.toString());
			}

		}
		return super.getPropertiesFilePath();
	}

	/**
	 * @return date string for filename
	 */
	protected static String getDateString() {
		SimpleDateFormat sdf = new SimpleDateFormat("_yyyy.MM.dd-HH.mm");
		return sdf.format(new Date());
	}

}
