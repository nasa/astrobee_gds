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
 * enum representing the DDS StatusKind flags for DataReaders
 * @author mallan
 *
 */
public enum ReaderStatus {
    RequestedDeadlineMissed(StatusKind.REQUESTED_DEADLINE_MISSED_STATUS),
    ReqestedIncompatibleQos(StatusKind.REQUESTED_INCOMPATIBLE_QOS_STATUS),
    SampleRejected         (StatusKind.SAMPLE_REJECTED_STATUS),
    LivelinessChanged      (StatusKind.LIVELINESS_CHANGED_STATUS),
    SampleLost             (StatusKind.SAMPLE_LOST_STATUS),
    SubscriptionMatched    (StatusKind.SUBSCRIPTION_MATCHED_STATUS),
    ;
    
    private final int maskValue;
    
    ReaderStatus(int value) {
        maskValue = value;
    }
    
    public int mask() {
        return maskValue;
    }
    
    public static int mask(EnumSet<ReaderStatus> set) {
        int retVal = 0;
        for(ReaderStatus e : set) {
            retVal |= e.maskValue;
        }
        return retVal;
    }
    
    public static int mask(ReaderStatus... set) {
        int retVal = 0;
        for(ReaderStatus e : set) {
            retVal |= e.maskValue;
        }
        return retVal;
    }
    
    
}
