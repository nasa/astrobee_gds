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


/**
 * Callback to get status information from the DDS reader
 * @see ReaderStatus
 * @author mallan
 * @TODO add a readerStatusSet() method, similar to IDdsWriterStatusListener 
 */
public interface IDdsReaderStatusListener {

    /**
     * callback when new status message is received by the data reader
     * @param partition
     * @param msgType
     * @param type
     * @param status object to be cast to DDS status type. Class name is the ReaderStatus
     * enum name + "Status", e.g. the object for ReaderStatus.SampleLost would be an instance
     * of SampleLostStatus
     */
    void onReaderStatusReceived(String partition, MessageType msgType, ReaderStatus statusType, Object statusObj);

}
