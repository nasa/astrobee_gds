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

public interface IDds {
    public void addRestartListener(final IDdsRestartListener listener);

    public boolean removeRestartListener(IDdsRestartListener listener);
    
    public int getRestartCount();

    public int getNumRestartSteps();

    public boolean restart() throws Exception;
    
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
    public boolean restart(IProgressUpdater progress) throws Exception;

    public boolean stop(IProgressUpdater progress);

    public boolean start(IProgressUpdater progress) throws Exception;
}
