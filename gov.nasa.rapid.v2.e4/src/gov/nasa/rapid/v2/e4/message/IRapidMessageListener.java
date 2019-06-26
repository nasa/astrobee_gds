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

import gov.nasa.rapid.v2.e4.agent.Agent;

public interface IRapidMessageListener {
    /**
     * Callback for when a subscribed RAPID message arrives. 
     * @param agent Agent from which the event originated
     * @param messageType MessageType corresponding to type, topic, etc
     * @param msgObj event payload
     * @param cfgObj FIXME: this <i>should</i> be the Config which matches the 
     *        delivered sample, if it exists. However, at the present time, it 
     *        is simply the last config received which <b>may not have a matching 
     *        serial id</b>. [Config payload corresponding to the serial id in the event. If 
     *        no matching Config has been received, this will be null. For Config and Simple
     *        message categories, this will be null.]
     */
    void onRapidMessageReceived(Agent agent, MessageType msgType, Object msgObj, Object cfgObj);
}
