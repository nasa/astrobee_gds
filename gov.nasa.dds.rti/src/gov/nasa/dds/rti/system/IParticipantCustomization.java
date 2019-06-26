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

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;

public interface IParticipantCustomization {
    /** 
     * hook to modify the factory qos. 
     * @return false if no change was made  
     */
    boolean customizeFactory(String participantId, DomainParticipantFactoryQos factoryQos);
    /** 
     * called immediately before creation of DomainParticipant 
     * @return false if no change was made 
     */
    boolean customizePreCreation(String participantId, DomainParticipantQos participantQos);
    /** 
     * called immediately after creation of DomainParticipant, 
     * but before the DomainParticipant is activated
     * @return false if no change was made 
     */
    boolean customizePostCreation(String participantId, DomainParticipant participant);
}
