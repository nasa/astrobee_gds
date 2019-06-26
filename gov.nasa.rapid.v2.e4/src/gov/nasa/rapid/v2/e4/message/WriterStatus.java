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
package gov.nasa.rapid.v2.e4.message;

import java.util.EnumSet;

import com.rti.dds.infrastructure.StatusKind;

/**
 * enum representing the DDS StatusKind flags for DataWriters
 * @author mallan
 *
 */
public enum WriterStatus {
    OfferedDeadlineMissed  (StatusKind.OFFERED_DEADLINE_MISSED_STATUS),
    OfferedIncompatibleQos (StatusKind.OFFERED_INCOMPATIBLE_QOS_STATUS),
    LivelinessLost         (StatusKind.LIVELINESS_LOST_STATUS),
    PublicationMatched     (StatusKind.PUBLICATION_MATCHED_STATUS),
    //InstanceReplaced       (0)
    ;
    
    private final int maskValue;
    
    WriterStatus(int value) {
        maskValue = value;
    }
    
    public int mask() {
        return maskValue;
    }
    
    public static int mask(EnumSet<WriterStatus> set) {
        int retVal = 0;
        for(WriterStatus e : set) {
            retVal |= e.maskValue;
        }
        return retVal;
    }
    
    public static int mask(WriterStatus... set) {
        int retVal = 0;
        for(WriterStatus e : set) {
            retVal |= e.maskValue;
        }
        return retVal;
    }
    
    
}
