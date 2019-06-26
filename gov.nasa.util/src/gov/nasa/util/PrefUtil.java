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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * utility for creating/managing a preferences dir
 * @author mallan
 *
 */
public class PrefUtil {
    private static final Logger logger = Logger.getLogger(PrefUtil.class);
    
    public final static String PREF_DIR_NAME=".nasa";
    
    /**
     * 
     */
    public static File getPrefDir(String relativePath) throws IOException {
        File prefRoot = getRootPrefDir();
        String prefSubDirPath = prefRoot.getAbsolutePath() + File.separator + relativePath;
        File   prefSubDir = new File(prefSubDirPath);
        if(!prefSubDir.exists()) {
            logger.debug("Creating preferences dir: "+prefSubDirPath);
            FileUtils.forceMkdir(prefSubDir);
        }
        if(!prefSubDir.isDirectory()) {
            throw new IOException("Preference root exists, but it is not a directory: "+prefSubDirPath);
        }
        if(!prefSubDir.canWrite()) {
            throw new IOException("Preference root exists, but it is not writable: "+prefSubDirPath);
        }
        return prefSubDir;  
    }
    
    /**
     * Get the root preference dir (~/.nasa). If it does not
     * exist, it will be created. 
     * @return
     * @throws IOException 
     */
    public static File getRootPrefDir() throws IOException {
        File homeDir = getHomeDir();
        String prefRootPath = homeDir.getAbsolutePath() + File.separator + PREF_DIR_NAME;
        File prefRoot = new File(prefRootPath);
        if(!prefRoot.exists()) {
            logger.debug("Creating preferences dir: "+prefRootPath);
            FileUtils.forceMkdir(prefRoot);
        }
        if(!prefRoot.isDirectory()) {
            throw new IOException("Preference root exists, but it is not a directory: "+prefRootPath);
        }
        if(!prefRoot.canWrite()) {
            throw new IOException("Preference root exists, but it is not writable: "+prefRootPath);
        }
        return prefRoot;  
    }
    
    /**
     * Get the home directory of the user. The home directory is
     * determined by the HOME environment variable, or if that 
     * is empty, the java property "user.home" is used
     */
    public static File getHomeDir() throws IOException {
        File   homeDir = null;
        String home;
        home = System.getenv("HOME");
        if(home != null && home.length() > 0) {
            homeDir = new File(home);
            if(homeDir.isDirectory() && homeDir.canWrite()) {
                return homeDir;
            }
        }
        home = System.getProperty("user.home");
        if(home != null && home.length() > 0) {
            homeDir = new File(home);
            if(homeDir.isDirectory() && homeDir.canWrite()) {
                return homeDir;
            }
        }
        throw new IOException("Could not determine home directory for user (or it is not writable)");
    }
}
