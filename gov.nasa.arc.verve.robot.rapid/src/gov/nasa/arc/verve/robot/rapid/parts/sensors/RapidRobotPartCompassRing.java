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
package gov.nasa.arc.verve.robot.rapid.parts.sensors;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexRing;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.helpers.KeyTypeValueHelper;
import gov.nasa.rapid.v2.e4.message.helpers.UncheckedInvalidParameterException;
import gov.nasa.rapid.v2.message.agent.MessageTypeK10;
import rapid.ext.arc.Float32Config;
import rapid.ext.arc.Float32Sample;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.rti.dds.infrastructure.FloatSeq;

public class RapidRobotPartCompassRing extends RapidRobotPartOrientationSensor {
    protected double    m_declination = 0;
    protected String    m_frame       = null;

    public RapidRobotPartCompassRing(String partId, RapidRobot parent, String participantId, float declination) {
        this(partId, parent, participantId, declination, 1.0f, 0.5f, 0.5f);
    }

    public RapidRobotPartCompassRing(String partId, RapidRobot parent, String participantId, float declination, float radius, float thickness, float zOff) {
        super(partId, parent, participantId, radius, thickness, zOff);
        setDeclinationDegrees(declination);
    }

    @Override
    protected TexRing createTexRing(float radius, float thickness, float zOff) {
        TexRing ring = new TexRing(this.getClass().getSimpleName(), 4, 40);
        ring.setTexture(s_tex);
        ring.setDefaultColor( new ColorRGBA(0.5f, 1.0f, 0.5f, 1.0f) );
        ring.setRadius(radius, radius+thickness);
        ring.setConcave(-0.5f);
        ring.initialize();
        ring.createDefaultRenderStates();
        ring.getSceneHints().setLightCombineMode(LightCombineMode.CombineFirst);
        ring.setTranslation(m_xyz);

        return ring;
    }

    @Override
    public MessageType[] rapidMessageTypes() {
        return new MessageType[] { MessageTypeK10.HMR_FLOAT32_SAMPLE_TYPE, MessageTypeK10.HMR_FLOAT32_CONFIG_TYPE };
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object msgObj, Object cfgObj) {
        if(msgType.equals(MessageTypeK10.HMR_FLOAT32_SAMPLE_TYPE)) {
            final Float32Sample sample = (Float32Sample)msgObj;
            final FloatSeq fs = sample.data.userData;
            final double r = fs.getFloat(0);
            final double p = fs.getFloat(1);
            final double y = fs.getFloat(2)+m_declination;
            final double check = r + p + y;
            if(check == check) {
                m_rpy.set(fs.getFloat(0), fs.getFloat(1), fs.getFloat(2)+m_declination);
                setDirty(true);
            }
            else {
                //logger.debug(String.format("NaN detected: r=%f p=%f y=%f", r, p, y));
            }
        }
        else if(msgType.equals(MessageTypeK10.HMR_FLOAT32_CONFIG_TYPE)) {
            final Float32Config config = (Float32Config)msgObj;
            KeyTypeValueHelper ktvh = new KeyTypeValueHelper(config.metaData.userData);
            setMetaData(ktvh);
        }
    }

    protected void setMetaData(KeyTypeValueHelper ktvh) {
        try { 
            setDeclinationDegrees(ktvh.getFloat("magDeclination"));
        }
        catch(UncheckedInvalidParameterException e) { /*ignore*/ }
        try {
            m_frame = ktvh.getString("sensorFrame");
        }
        catch(UncheckedInvalidParameterException e) { /*ignore*/ }
    }

    /**
     * Get declination in degrees
     * @return
     */
    public double getDeclinationDegrees() {
        return MathUtils.RAD_TO_DEG * m_declination;
    }

    /**
     * Set declination in degrees
     * @param declination
     */
    public void setDeclinationDegrees(double declination) {
        m_declination = MathUtils.DEG_TO_RAD * declination;
    }


}



