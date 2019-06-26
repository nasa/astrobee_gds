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
package gov.nasa.rapid.v2.e4.preferences;

import gov.nasa.rapid.v2.e4.agent.Agent;

import java.io.File;

import org.apache.log4j.Logger;

public final class RapidPreferences {
    private static final Logger logger = Logger.getLogger(RapidPreferences.class);
    
    private static IRapidPreferences s_impl = null;

    public static void setImpl(IRapidPreferences impl) {
        s_impl = impl;
    }

    /**
     * @return default log dir (guaranteed to include trailing separator)
     */
    public static String getDefaultLogDir() {
        String retVal = assertImpl().getDefaultLogDir();
        if(!retVal.endsWith(File.separator)) {
            retVal = retVal + File.separator;
        }
        return retVal;
    }

    /**
     * @return preferred agent of interest
     */
    public static Agent getAgentOfInterest() {
        return assertImpl().getAgentOfInterest();
    }

    /**
     * @return preferred agent of interest
     */
    public static void setAgentOfInterest(Agent agent) {
        assertImpl().setAgentOfInterest(agent);
    }

    protected static IRapidPreferences assertImpl() {
        if(s_impl != null) {
            return s_impl;
        }
        else {
            // note that when running in Eclipse RCP, the gov.nasa.rapid.v2.ui plugin must 
            // have activated before this is called for the Eclipse RCP preferences to be set
            System.err.println("*ERROR* RapidPreferences implementation is null. Creating fallback implementation.");
            logger.error("RapidPreferences implementation is null. Creating fallback implementation.");
            s_impl = new FallbackRapidPreferences();
            return s_impl;
        }
    }
    
}
