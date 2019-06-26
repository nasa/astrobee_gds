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
package gov.nasa.arc.verve.robot.rapid;

import com.ardor3d.math.Vector3;

import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import rapid.PositionConfig;
import rapid.PositionSample;

/**
 * 
 * @author mallan
 *
 */
public class PositionSampleSource implements IRapidMessageListener {
    //private static final Logger logger = Logger.getLogger(PositionSampleSource.class);

    protected final Agent       agent; // not necessary, just for debugging
    protected final String      name;
    protected final MessageType msgType;
    protected PositionConfig    config = null;
    protected PositionSample    sample = null;
    protected String            participantId; 

    /**
     * 
     * @param name
     * @param msgType
     */
    public PositionSampleSource(MessageType msgType, Agent agent) {
        this.name    = msgType.name();
        this.msgType = msgType;
        this.agent   = agent;
    }

    public String name() { return name; }
    public MessageType msgType() { return msgType; }

    public void setData(Object sampleObj, Object configObj) {
        this.sample = (PositionSample)sampleObj;
        this.config = (PositionConfig)configObj;
    }

    public String debugFileHeader() {
        return "#x,y,z,poseEncoding,rot[9]";
    }

    public String debugFileRow() {
        StringBuilder sb = new StringBuilder();
        if(sample != null) {
            sb.append(sample.pose.xyz.userData[0]).append(",");
            sb.append(sample.pose.xyz.userData[1]).append(",");
            sb.append(sample.pose.xyz.userData[2]).append(",");
            if(config == null) {
                sb.append("NULL").append(",");
            }
            else {
                sb.append(config.poseEncoding.ordinal()).append(",");
            }
            for(int i = 0; i < 9; i++) {
                if(i > 0)
                    sb.append(",");
                sb.append(sample.pose.rot.userData[i]);
            }
        }
        return sb.toString();
    }

    public void connectTelemetry(String participantId) throws TelemetryException {
        this.participantId = participantId;
        //logger.debug(this.getClass().getSimpleName()+".connectTelemetry("+participantId+") - "+msgType.name());
        try {
            RapidMessageCollector.instance().addRapidMessageListener(participantId, agent, msgType, this );
        }
        catch(Throwable t) {
            throw new TelemetryException("Could not connect "+agent.name()+" "+msgType.name(), t);
        }
    }

    public void disconnectTelemetry(String participantId) throws TelemetryException {
        //logger.debug(this.getClass().getSimpleName()+".disconnectTelemetry("+participantId+") - "+msgType.name());
        try {
            RapidMessageCollector.instance().removeRapidMessageListener(participantId, agent, msgType, this );
        }
        catch(Throwable t) {
            throw new TelemetryException("Could not connect "+agent.name()+" "+msgType.name(), t);
        }
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object sampleObj, Object configObj) {
        if(msgType.equals(msgType) ) {
            //logger.debug(agent.name()+" : received "+msgType.name());
            setData(sampleObj, configObj);
        }
    }

    public Vector3 getXyz(Vector3 xyz) {
        if(sample != null) {
            final double[] d = sample.pose.xyz.userData;
            xyz.set(d[0], d[1], d[2]);
        }
        return xyz;
    }


}
