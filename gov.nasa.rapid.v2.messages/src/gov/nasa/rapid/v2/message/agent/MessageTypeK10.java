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
import gov.nasa.rapid.v2.e4.message.MessageType.Category;
import rapid.POSITION_CONFIG_TOPIC;
import rapid.POSITION_SAMPLE_TOPIC;
import rapid.PositionConfig;
import rapid.PositionSample;
import rapid.ext.TRAJECTORY2D_CONFIG_TOPIC;
import rapid.ext.TRAJECTORY2D_SAMPLE_TOPIC;
import rapid.ext.Trajectory2DConfig;
import rapid.ext.Trajectory2DSample;
import rapid.ext.arc.FLOAT32_CONFIG_TOPIC;
import rapid.ext.arc.FLOAT32_SAMPLE_TOPIC;
import rapid.ext.arc.Float32Config;
import rapid.ext.arc.Float32Sample;

public class MessageTypeK10 extends MessageTypeExt {
    
    public static final String DSTAR_APPEND = "dstar";
    public static final String DSTAR_TRAJECTORY2D_CONFIG = "DStarTrajectory2DConfig";
    public static final String DSTAR_TRAJECTORY2D_SAMPLE = "DStarTrajectory2DSample";
    public static MessageType  DSTAR_TRAJECTORY2D_CONFIG_TYPE = new MessageTypeK10(Category.Config, DSTAR_TRAJECTORY2D_CONFIG,                      null, Trajectory2DConfig.class, TRAJECTORY2D_CONFIG_TOPIC.VALUE+tsep+DSTAR_APPEND, "RapidTrajectory2DConfigProfile");
    public static MessageType  DSTAR_TRAJECTORY2D_SAMPLE_TYPE = new MessageTypeK10(Category.Sample, DSTAR_TRAJECTORY2D_SAMPLE, DSTAR_TRAJECTORY2D_CONFIG, Trajectory2DSample.class, TRAJECTORY2D_SAMPLE_TOPIC.VALUE+tsep+DSTAR_APPEND, "RapidTrajectory2DSampleProfile");
            
    public static final String HMR_APPEND = "hmr";
    public static final String HMR_FLOAT32_CONFIG = "HmrFloat32Config";
    public static final String HMR_FLOAT32_SAMPLE = "HmrFloat32Sample";
    public static MessageType  HMR_FLOAT32_CONFIG_TYPE = new MessageTypeK10(Category.Config, HMR_FLOAT32_CONFIG,               null, Float32Config.class, FLOAT32_CONFIG_TOPIC.VALUE+tsep+HMR_APPEND, "RapidFloat32ConfigProfile");
    public static MessageType  HMR_FLOAT32_SAMPLE_TYPE = new MessageTypeK10(Category.Sample, HMR_FLOAT32_SAMPLE, HMR_FLOAT32_CONFIG, Float32Sample.class, FLOAT32_SAMPLE_TOPIC.VALUE+tsep+HMR_APPEND, "RapidFloat32SampleProfile");
    
    public static final String DEAD_RECKONING_APPEND = "deadReckoning";
    public static final String DEAD_RECKONING_FLOAT32_CONFIG = "DeadReckoningFloat32Config";
    public static final String DEAD_RECKONING_FLOAT32_SAMPLE = "DeadReckoningFloat32Sample";
    public static MessageType  DEAD_RECKONING_FLOAT32_CONFIG_TYPE = new MessageTypeK10(Category.Config, DEAD_RECKONING_FLOAT32_CONFIG,                          null, Float32Config.class, FLOAT32_CONFIG_TOPIC.VALUE+tsep+DEAD_RECKONING_APPEND, "RapidFloat32ConfigProfile");
    public static MessageType  DEAD_RECKONING_FLOAT32_SAMPLE_TYPE = new MessageTypeK10(Category.Sample, DEAD_RECKONING_FLOAT32_SAMPLE, DEAD_RECKONING_FLOAT32_CONFIG, Float32Sample.class, FLOAT32_SAMPLE_TOPIC.VALUE+tsep+DEAD_RECKONING_APPEND, "RapidFloat32SampleProfile");
    
    public static final String FILM_DEPLOYER_APPEND = "deployer";
    public static final String FILM_DEPLOYER_FLOAT32_CONFIG = "FilmDeployerFloat32Config";
    public static final String FILM_DEPLOYER_FLOAT32_SAMPLE = "FilmDeployerFloat32Sample";
    public static MessageType  FILM_DEPLOYER_FLOAT32_CONFIG_TYPE = new MessageTypeK10(Category.Config, FILM_DEPLOYER_FLOAT32_CONFIG,                         null, Float32Config.class, FLOAT32_CONFIG_TOPIC.VALUE+tsep+FILM_DEPLOYER_APPEND, "RapidFloat32ConfigProfile");
    public static MessageType  FILM_DEPLOYER_FLOAT32_SAMPLE_TYPE = new MessageTypeK10(Category.Sample, FILM_DEPLOYER_FLOAT32_SAMPLE, FILM_DEPLOYER_FLOAT32_CONFIG, Float32Sample.class, FLOAT32_SAMPLE_TOPIC.VALUE+tsep+FILM_DEPLOYER_APPEND, "RapidFloat32SampleProfile");
    
    //-- Position extensions ----------------------------
    public static final String POSE_eigen_APPEND               = "eigen";
    public static final String POSE_eigen_POSITION_CONFIG      = "EigenPositionConfig";
    public static final String POSE_eigen_POSITION_SAMPLE      = "EigenPositionSample";
    public static MessageType  POSE_eigen_POSITION_SAMPLE_TYPE = new MessageTypeK10(Category.Sample, POSE_eigen_POSITION_SAMPLE, POSE_eigen_POSITION_CONFIG, PositionSample.class, POSITION_SAMPLE_TOPIC.VALUE+tsep+POSE_eigen_APPEND, "RapidPositionSampleProfile");
    public static MessageType  POSE_eigen_POSITION_CONFIG_TYPE = new MessageTypeK10(Category.Config, POSE_eigen_POSITION_CONFIG,                             null, PositionConfig.class, POSITION_CONFIG_TOPIC.VALUE+tsep+POSE_eigen_APPEND, "RapidPositionConfigProfile" );

    public static final String POSE_relative_eigen_APPEND               = "relative_eigen";
    public static final String POSE_relative_eigen_POSITION_CONFIG      = "RelativeEigenPositionConfig";
    public static final String POSE_relative_eigen_POSITION_SAMPLE      = "RelativeEigenPositionSample";
    public static MessageType  POSE_relative_eigen_POSITION_SAMPLE_TYPE = new MessageTypeK10(Category.Sample, POSE_relative_eigen_POSITION_SAMPLE, POSE_relative_eigen_POSITION_CONFIG, PositionSample.class, POSITION_SAMPLE_TOPIC.VALUE+tsep+POSE_relative_eigen_APPEND, "RapidPositionSampleProfile");
    public static MessageType  POSE_relative_eigen_POSITION_CONFIG_TYPE = new MessageTypeK10(Category.Config, POSE_relative_eigen_POSITION_CONFIG,                             null, PositionConfig.class, POSITION_CONFIG_TOPIC.VALUE+tsep+POSE_relative_eigen_APPEND, "RapidPositionConfigProfile" );

    public static final String POSE_eigenImproved_APPEND               = "eigenImproved";
    public static final String POSE_eigenImproved_POSITION_CONFIG      = "EigenImprovedPositionConfig";
    public static final String POSE_eigenImproved_POSITION_SAMPLE      = "EigenImprovedPositionSample";
    public static MessageType  POSE_eigenImproved_POSITION_SAMPLE_TYPE = new MessageTypeK10(Category.Sample, POSE_eigenImproved_POSITION_SAMPLE, POSE_eigenImproved_POSITION_CONFIG, PositionSample.class, POSITION_SAMPLE_TOPIC.VALUE+tsep+POSE_eigenImproved_APPEND, "RapidPositionSampleProfile");
    public static MessageType  POSE_eigenImproved_POSITION_CONFIG_TYPE = new MessageTypeK10(Category.Config, POSE_eigenImproved_POSITION_CONFIG,                             null, PositionConfig.class, POSITION_CONFIG_TOPIC.VALUE+tsep+POSE_eigenImproved_APPEND, "RapidPositionConfigProfile" );

    public static final String POSE_relative_eigenImproved_APPEND               = "relative_eigenImproved";
    public static final String POSE_relative_eigenImproved_POSITION_CONFIG      = "RelativeEigenImprovedPositionConfig";
    public static final String POSE_relative_eigenImproved_POSITION_SAMPLE      = "RelativeEigenImprovedPositionSample";
    public static MessageType  POSE_relative_eigenImproved_POSITION_SAMPLE_TYPE = new MessageTypeK10(Category.Sample, POSE_relative_eigenImproved_POSITION_SAMPLE, POSE_relative_eigenImproved_POSITION_CONFIG, PositionSample.class, POSITION_SAMPLE_TOPIC.VALUE+tsep+POSE_relative_eigenImproved_APPEND, "RapidPositionSampleProfile");
    public static MessageType  POSE_relative_eigenImproved_POSITION_CONFIG_TYPE = new MessageTypeK10(Category.Config, POSE_relative_eigenImproved_POSITION_CONFIG,                             null, PositionConfig.class, POSITION_CONFIG_TOPIC.VALUE+tsep+POSE_relative_eigenImproved_APPEND, "RapidPositionConfigProfile" );

    public static final String POSE_eigenAdvNav_APPEND               = "eigenAdvNav";
    public static final String POSE_eigenAdvNav_POSITION_CONFIG      = "EigenAdvNavPositionConfig";
    public static final String POSE_eigenAdvNav_POSITION_SAMPLE      = "EigenAdvNavPositionSample";
    public static MessageType  POSE_eigenAdvNav_POSITION_SAMPLE_TYPE = new MessageTypeK10(Category.Sample, POSE_eigenAdvNav_POSITION_SAMPLE, POSE_eigenAdvNav_POSITION_CONFIG, PositionSample.class, POSITION_SAMPLE_TOPIC.VALUE+tsep+POSE_eigenAdvNav_APPEND, "RapidPositionSampleProfile");
    public static MessageType  POSE_eigenAdvNav_POSITION_CONFIG_TYPE = new MessageTypeK10(Category.Config, POSE_eigenAdvNav_POSITION_CONFIG,                             null, PositionConfig.class, POSITION_CONFIG_TOPIC.VALUE+tsep+POSE_eigenAdvNav_APPEND, "RapidPositionConfigProfile" );

    public static final String POSE_advNav_APPEND               = "advNav";
    public static final String POSE_advNav_POSITION_CONFIG      = "AdvNavPositionConfig";
    public static final String POSE_advNav_POSITION_SAMPLE      = "AdvNavPositionSample";
    public static MessageType  POSE_advNav_POSITION_SAMPLE_TYPE = new MessageTypeK10(Category.Sample, POSE_advNav_POSITION_SAMPLE, POSE_advNav_POSITION_CONFIG, PositionSample.class, POSITION_SAMPLE_TOPIC.VALUE+tsep+POSE_advNav_APPEND, "RapidPositionSampleProfile");
    public static MessageType  POSE_advNav_POSITION_CONFIG_TYPE = new MessageTypeK10(Category.Config, POSE_advNav_POSITION_CONFIG,                        null, PositionConfig.class, POSITION_CONFIG_TOPIC.VALUE+tsep+POSE_advNav_APPEND, "RapidPositionConfigProfile" );


    protected MessageTypeK10(Category category, String name, String configName, Class dataType, String topic, 
                             String qosProfile) {
        super(category, name, configName, dataType, topic, qosProfile);
    }
    protected MessageTypeK10(Category category, String name, String configName, Class dataType, String topic) {
        this(category, name, configName, dataType, topic, PREFIX+name+POSTFIX);
    }


}
