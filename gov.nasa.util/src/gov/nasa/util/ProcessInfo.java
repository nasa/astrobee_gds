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

import java.lang.management.ManagementFactory;
import java.net.InetAddress;

public class ProcessInfo {

    /**
     * get the current user's username. Any spaces in the username will be removed. 
     * @return username
     */
    public static String username() {
        return username("");
    }

    /**
     * @param spaceReplace if spaces exist in the username, replace 
     * them with this value
     * @return username
     */
    public static String username(String spaceReplace) {
        String username = System.getProperty("user.name");
        username = username.replace(" ", spaceReplace);
        return username;
    }

    /**
     * @return hostname
     */
    public static String hostname() {
        String hostname = "localhost";
        try { // get simple hostname
            hostname = InetAddress.getLocalHost().getHostName();
            int dot = hostname.indexOf('.');
            if (dot > 0) {
                hostname = hostname.substring(0, dot);
            }
        } 
        catch (Throwable t) {
            // ignore
        }
        return hostname;
    }

    /**
     * This call is not guaranteed to work. There is no platform-independent
     * way to get the process id from Java, but by convention in the 
     * Sun JVMs, ManagementFactory returns a string that is pid@hostname. 
     * @return pid if it works, -1 if not
     */
    public static long processId() {
        return processId(-1);
    }
    
    public static long processId(long fallback) {
        // for Sun JVM, this returns pid@hostname. NOT GUARANTEED TO WORK
        final String rtName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = rtName.indexOf('@');
        if (index > 1) {
            try {
                return Long.parseLong(rtName.substring(0, index));
            } 
            catch(Throwable t) {
                // ignore
            }
        }
        return fallback;
    }
}
