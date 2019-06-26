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
package gov.nasa.arc.simulator.freeflyer;

import gov.nasa.dds.rti.preferences.IDdsPreferences;
import gov.nasa.util.PlatformInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.rti.dds.infrastructure.TransportBuiltinKind;

/**
 * simple, hard-coded examples of DdsPreferences. Eclipse RCP 
 * applications will use the implementation provided in gov.nasa.dds.rti.ui
 * The preferences are read on a DDS restart to set Pariticpant properties. 
 * @author mallan
 */
public class FreeFlyerPreferences implements IDdsPreferences {
	private static final Logger  logger        = Logger.getLogger(FreeFlyerPreferences.class);
	
    public static final int      DOMAIN_ID   = 37;
    public String   qosLibrary = "RapidQosLibrary";
    public String   qosProfile = "RapidDefaultQos";
    public String[] urlGroups  = new String[0];
    // carrot, carrot wifi, dandelion, pow
    //public String[] peersList  = new String[] { "127.0.0.1", "128.102.109.81", "128.102.242.154", "128.102.106.15", "128.102.106.20","128.102.109.81"};

    public String[] peersList  = new String[] { "127.0.0.1", "192.168.58.1" };
    
    
	public void setPeersFile(String filename) {
		ArrayList<String> retVal = new ArrayList<String>();
		InputStream is = null;
		try {
			File peerFile = new File(filename);
			if (peerFile != null && peerFile.canRead()) {
				is = peerFile.toURI().toURL().openStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					int comment = line.indexOf(';');
					if (comment >= 0) {
						line = line.substring(0, comment);
					}
					line = line.trim();
					if (line.length() > 1) {
						retVal.add(line);
					}
				}
			}
		} catch (Throwable t) {
			logger.error("error reading peers file " + filename, t);
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("error closing file", e);
				}
			}
		}
		peersList = retVal.toArray(new String[retVal.size()]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("peersList = ");
		for(String peer : peersList) {
			sb.append(peer + ", ");
		}
		logger.info(sb.toString());
	}
	
    @Override
    public String[] getProfileUrlGroups() {
        return urlGroups;
    }

    @Override
    public String[] getPeersList() {
        return peersList;
    }

    @Override
    public String[] getIpv4DenyList() {
        return null;
    }

    @Override
    public boolean isIgnoreUserProfile() {
        return false;
    }

    @Override
    public boolean isIgnoreEnvironmentProfile() {
        return false;
    }

    @Override
    public int getDomainId(String participantId) {
        return DOMAIN_ID;
    }
    
    public int getDomainId(){
    	return DOMAIN_ID;
    }

    @Override
    public String getQosLibrary(String participantId) {
        return qosLibrary;
    }

    @Override
    public String getQosProfile(String participantId) {
        return qosProfile;
    }

    @Override
    public boolean isTransportDisabled(String transportName) {
        // disable shared memory transport on OSX because it is broken
        if(PlatformInfo.getOS() == PlatformInfo.OS.Mac) {
            if(transportName.equals(TransportBuiltinKind.SHMEM_ALIAS)) {
                return true;
            }
        }
        return false;
    }

}
