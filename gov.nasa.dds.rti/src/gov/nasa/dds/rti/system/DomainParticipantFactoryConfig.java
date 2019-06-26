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
package gov.nasa.dds.rti.system;

import gov.nasa.dds.rti.preferences.DdsPreferences;


public class DomainParticipantFactoryConfig {
    public boolean isIgnoreResourceProfile    = false;
    public boolean isIgnoreEnvironmentProfile = false;
    public boolean isIgnoreUserProfile        = false;
    
    public String[]           qosUrlGroups    = null;
    public FlowController[]   flowControllers = null;
    
    public DomainParticipantFactoryConfig() {
        defaults();
    }
    
    public void reset() {
        isIgnoreResourceProfile     = false;
        isIgnoreEnvironmentProfile  = true;
        isIgnoreUserProfile         = true;
        qosUrlGroups                = null;
        flowControllers             = null;
    }
    
    public void defaults() {
        isIgnoreResourceProfile     = false;
        isIgnoreEnvironmentProfile  = DdsPreferences.isIgnoreEnvironmentProfile();
        isIgnoreUserProfile         = DdsPreferences.isIgnoreUserProfile();
        qosUrlGroups                = DdsPreferences.getProfileUrlGroups();
        flowControllers             = null;
    }
    
    
}
