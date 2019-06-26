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
package gov.nasa.arc.verve.robot.rapid.parts;

import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.parts.AbstractRobotPart;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import org.apache.log4j.Logger;

import com.ardor3d.scenegraph.hint.CullHint;

public abstract class RapidRobotPart extends AbstractRobotPart implements IRapidMessageListener
{
    private final Logger logger = Logger.getLogger(RapidRobotPart.class);

    protected final RapidRobot m_rapidRobot;
    protected String           m_participantId;

    public RapidRobotPart(String partName, RapidRobot parent, String participantId) {
        super(partName, parent);
        m_rapidRobot  = parent;
        m_participantId = participantId;
    }

    public RapidRobot getRapidRobot() {
        return m_rapidRobot;
    }

    @Override
    public void setVisible(boolean visible) {
        m_isVisible = visible;
        if(m_node != null) {
            if(visible) {
                m_node.getSceneHints().setCullHint(CullHint.Inherit);
            }
            else {
                m_node.getSceneHints().setCullHint(CullHint.Always);
            }
        }
        if(getRobot().isTelemetryEnabled()) {                
            try {
                if(visible) {
                    connectTelemetry();
                    //logger.debug("### "+getPartName()+".connectTelemetry()");
                }
                else {
                    disconnectTelemetry();
                    //logger.debug("### "+getPartName()+".disconnectTelemetry()");
                }
            }
            catch(TelemetryException t) {
                logger.warn("Could not set "+getPartName()+" telemetry enabled state to "+visible);
            }
        }
    }

    public abstract MessageType[] rapidMessageTypes(); //{ return new MessageType[0]; }

    /**
     * IMPORTANT: remember to guard overrides of connectTelemetry() with 
     * the isTelemetryEnabled() call. If the part is not visible, we don't
     * want to subscribe by adding listeners
     */
    @Override
    public void connectTelemetry() throws TelemetryException {
        if(isTelemetryEnabled()) {
            try {
                for(MessageType msgType : rapidMessageTypes()) {
                    RapidMessageCollector.instance().addRapidMessageListener(getParticipantId(),
                                                                             getRapidRobot().getAgent(), 
                                                                             msgType, 
                                                                             this );
                }
            }
            catch(Throwable t) {
                String msg = "Error connecting to telemetry in "+this.getClass().getSimpleName()+": ";
                logger.debug(msg+t.getClass().getSimpleName());
                throw new TelemetryException(msg, t);
            }
        }
    }

    @Override
    public void disconnectTelemetry() throws TelemetryException {
        try {
            for(MessageType msgType : rapidMessageTypes()) {
                RapidMessageCollector.instance().removeRapidMessageListener(getParticipantId(), 
                                                                            getRapidRobot().getAgent(), 
                                                                            msgType, 
                                                                            this );
            }
        }
        catch(Throwable t) {
            throw new TelemetryException("Error disconnecting from telemetry", t);
        }
    }

    /**
     * 
     * @param participantId
     */
    public synchronized void setParticipantId(String participantId) {
        // don't do unnecessary unsubscribe/subscribes
        if(!participantId.equals(m_participantId)) {
            // make sure participantId is valid
            if(DdsEntityFactory.isValidParticipantId(participantId)) {
                //logger.debug("-- disconnect telemetry : "+getPartName()+" on "+m_participantId);
                try { disconnectTelemetry(); } catch (TelemetryException e) { logger.error(e); }
                m_participantId = participantId;
                //logger.debug("++ connect telemetry : "+getPartName()+" on "+m_participantId);
                try { connectTelemetry(); } catch (TelemetryException e) { logger.error(e); }
                //logger.debug("  done.\n");
            }
            else {
                logger.warn(participantId+" is not a valid participant identifier");
            }
        }
    }

    /**
     * 
     * @return
     */
    public String getParticipantId() {
        return m_participantId;
    }
}
