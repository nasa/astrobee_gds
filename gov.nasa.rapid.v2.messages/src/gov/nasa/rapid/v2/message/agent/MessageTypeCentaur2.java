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
package gov.nasa.rapid.v2.message.agent;

import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.MessageTypeExt;
import rapid.JOINT_CONFIG_TOPIC;
import rapid.JOINT_SAMPLE_TOPIC;
import rapid.JointConfig;
import rapid.JointSample;
import rapid.ext.TrajectoryConfig;
import rapid.ext.TrajectorySample;

public class MessageTypeCentaur2  extends MessageTypeExt {
    public static final String DIGGER_JOINT_CONFIG       = "DiggerJointConfig";
    public static final String DIGGER_JOINT_SAMPLE       = "DiggerJointSample";
    public static final String DIGGER_TOPIC_APPEND       = "digger";
    
    public static final String DIGGER_JOINT_CONFIG_TOPIC = JOINT_CONFIG_TOPIC.VALUE+topicSeparator+DIGGER_TOPIC_APPEND;
    public static final String DIGGER_JOINT_SAMPLE_TOPIC = JOINT_SAMPLE_TOPIC.VALUE+topicSeparator+DIGGER_TOPIC_APPEND;

    public static final String GEOMTRAJECTORY_CONFIG = "GeometricTrajectoryConfig";
    public static final String GEOMTRAJECTORY_SAMPLE = "GeometricTrajectorySample";
    public static final String GEOMTRAJECTORY_CONFIG_TOPIC = "rapid_trajectory_config";
    public static final String GEOMTRAJECTORY_SAMPLE_TOPIC = "rapid_trajectory_sample";

    public static MessageType  DIGGER_JOINT_CONFIG_TYPE  = new MessageTypeCentaur2(Category.Config, DIGGER_JOINT_CONFIG,                null, JointConfig.class, DIGGER_JOINT_CONFIG_TOPIC, "RapidJointConfigProfile");
    public static MessageType  DIGGER_JOINT_SAMPLE_TYPE  = new MessageTypeCentaur2(Category.Sample, DIGGER_JOINT_SAMPLE, DIGGER_JOINT_CONFIG, JointSample.class, DIGGER_JOINT_SAMPLE_TOPIC, "RapidJointSampleProfile");

    public static MessageType  GEOMTRAJECTORY_CONFIG_TYPE  = new MessageTypeCentaur2(Category.Config, GEOMTRAJECTORY_CONFIG,                  null, TrajectoryConfig.class, GEOMTRAJECTORY_CONFIG_TOPIC, "RapidTrajectoryConfigProfile");
    public static MessageType  GEOMTRAJECTORY_SAMPLE_TYPE  = new MessageTypeCentaur2(Category.Sample, GEOMTRAJECTORY_SAMPLE, GEOMTRAJECTORY_CONFIG, TrajectorySample.class, GEOMTRAJECTORY_SAMPLE_TOPIC, "RapidTrajectorySampleProfile");

    protected MessageTypeCentaur2(Category category, String name, String configName, Class dataType, String topic, 
                             String qosProfile) {
        super(category, name, configName, dataType, topic, qosProfile);
    }
}
