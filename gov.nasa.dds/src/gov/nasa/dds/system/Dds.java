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
package gov.nasa.dds.system;

import gov.nasa.util.IProgressUpdater;

import java.util.LinkedList;
import java.util.List;

public class Dds {
    private static IDds ddsImpl;
    
    private static List<IDdsRestartListener> restartListeners = new LinkedList<IDdsRestartListener>();

    /**
     * Set IDds implementation
     * @param dds
     */
    public static synchronized void setDdsImpl(IDds dds) {
        ddsImpl = dds;
        // add any stored restart listeners
        for(IDdsRestartListener listener : restartListeners) {
            ddsImpl.addRestartListener(listener);
        }
        restartListeners.clear();
    }

    public static synchronized void addRestartListener(final IDdsRestartListener listener) {
        // if implementation hasn't been set yet, hold  
        // listeners until setDdsImpl is  called
        if(ddsImpl == null) {
            restartListeners.add(listener);
            return;
        }
        ddsImpl.addRestartListener(listener);
    }

    public static synchronized boolean removeRestartListener(IDdsRestartListener listener) {
        if(ddsImpl == null) {
            return restartListeners.remove(listener);
        }
        return ddsImpl.removeRestartListener(listener);
    }

    public static int getRestartCount() {
        return ddsImpl.getRestartCount();
    }

    public static int getNumRestartSteps() {
        return ddsImpl.getNumRestartSteps();
    }

    public static boolean restart() throws Exception {
        return ddsImpl.restart();
    }

    /**
     * restart the DDS subsystem. The following will occur:
     * 1) inform all listeners that DDS is going down. 
     * 2) destroy all participants and contained entities. 
     * 3) inform listeners that DDS has gone down. 
     * 4) update fields in ParticipantCreators from DdsPreferences
     * 5) create new participants. 
     * 6) inform listeners that DDS has been restarted. 
     * @throws Exception 
     */
    public static boolean restart(IProgressUpdater progress) throws Exception {
        return ddsImpl.restart(progress);
    }

    public static boolean stop(IProgressUpdater progress) {
        return ddsImpl.stop(progress);
    }

    public static boolean start(IProgressUpdater progress) throws Exception {
        return ddsImpl.start(progress);
    }
}
