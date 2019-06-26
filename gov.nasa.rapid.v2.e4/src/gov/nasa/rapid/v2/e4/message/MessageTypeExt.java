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

import rapid.ACK_TOPIC;
import rapid.Ack;
import rapid.COMMAND_CONFIG_TOPIC;
import rapid.COMMAND_TOPIC;
import rapid.Command;
import rapid.CommandConfig;
import rapid.POINTCLOUD_CONFIG_TOPIC;
import rapid.POINTCLOUD_SAMPLE_TOPIC;
import rapid.POSITION_CONFIG_TOPIC;
import rapid.POSITION_SAMPLE_TOPIC;
import rapid.PointCloudConfig;
import rapid.PointCloudSample;
import rapid.PositionConfig;
import rapid.PositionSample;
import rapid.ext.NAVMAP_CONFIG_TOPIC;
import rapid.ext.NAVMAP_SAMPLE_TOPIC;
import rapid.ext.NavMapConfig;
import rapid.ext.NavMapSample;
import rapid.ext.PROCESSIO_SAMPLE_TOPIC;
import rapid.ext.PROCESSMANAGER_CONFIG_TOPIC;
import rapid.ext.PROCESSMANAGER_STATE_TOPIC;
import rapid.ext.ProcessIoSample;
import rapid.ext.ProcessManagerConfig;
import rapid.ext.ProcessManagerState;
import rapid.ext.RANGESCAN_CONFIG_TOPIC;
import rapid.ext.RANGESCAN_SAMPLE_TOPIC;
import rapid.ext.RangeScanConfig;
import rapid.ext.RangeScanSample;
import rapid.ext.SYSTEMINFO_CONFIG_TOPIC;
import rapid.ext.SYSTEMINFO_SAMPLE_TOPIC;
import rapid.ext.SystemInfoConfig;
import rapid.ext.SystemInfoSample;
import rapid.ext.TRAJECTORY2D_CONFIG_TOPIC;
import rapid.ext.TRAJECTORY2D_SAMPLE_TOPIC;
import rapid.ext.Trajectory2DConfig;
import rapid.ext.Trajectory2DSample;
import rapid.ext.VESTOP_CONFIG_TOPIC;
import rapid.ext.VESTOP_STATE_TOPIC;
import rapid.ext.VEStopConfig;
import rapid.ext.VEStopState;

public class MessageTypeExt extends MessageType {

    public static final String NAVMAP_CONFIG         = "NavMapConfig";
    public static final String NAVMAP_SAMPLE         = "NavMapSample";
    public static MessageType  NAVMAP_CONFIG_TYPE    = new MessageTypeExt(Category.Config,    NAVMAP_CONFIG,               null, NavMapConfig.class, NAVMAP_CONFIG_TOPIC.VALUE);
    public static MessageType  NAVMAP_SAMPLE_TYPE    = new MessageTypeExt(Category.Sample,    NAVMAP_SAMPLE,      NAVMAP_CONFIG, NavMapSample.class, NAVMAP_SAMPLE_TOPIC.VALUE);
    
    public static final String OCCUPANCYGRID_CONFIG       = "OccupancyGridConfig";
    public static final String OCCUPANCYGRID_SAMPLE       = "OccupancyGridSample";
    public static final String OCCUPANCYGRID_TOPIC_APPEND = "occupancy";
    public static MessageType  OCCUPANCYGRID_CONFIG_TYPE  = new MessageTypeExt(Category.Config, OCCUPANCYGRID_CONFIG,                 null, NavMapConfig.class, NAVMAP_CONFIG_TOPIC.VALUE+topicSeparator+OCCUPANCYGRID_TOPIC_APPEND, "RapidNavMapConfigProfile");
    public static MessageType  OCCUPANCYGRID_SAMPLE_TYPE  = new MessageTypeExt(Category.Sample, OCCUPANCYGRID_SAMPLE, OCCUPANCYGRID_CONFIG, NavMapSample.class, NAVMAP_SAMPLE_TOPIC.VALUE+topicSeparator+OCCUPANCYGRID_TOPIC_APPEND, "RapidNavMapSampleProfile");
    
    public static final String PROCESSMANAGER_CONFIG = "ProcessManagerConfig";
    public static final String PROCESSMANAGER_STATE  = "ProcessManagerState";
    public static MessageType  PROCESSMANAGER_CONFIG_TYPE = new MessageTypeExt(Category.Config, PROCESSMANAGER_CONFIG,                  null, ProcessManagerConfig.class, PROCESSMANAGER_CONFIG_TOPIC.VALUE);
    public static MessageType  PROCESSMANAGER_STATE_TYPE  = new MessageTypeExt(Category.State,  PROCESSMANAGER_STATE,  PROCESSMANAGER_CONFIG,  ProcessManagerState.class,  PROCESSMANAGER_STATE_TOPIC.VALUE);

    public static final String PROCESSIO_SAMPLE       = "ProcessIOSample";
    public static MessageType  PROCESSIO_SAMPLE_TYPE  = new MessageTypeExt(Category.Sample, PROCESSIO_SAMPLE, PROCESSMANAGER_CONFIG, ProcessIoSample.class, PROCESSIO_SAMPLE_TOPIC.VALUE);
    
    public static final String PROCESSMANAGER_APPEND              = "rctld";
    public static final String PROCESSMANAGER_COMMAND             = "ProcessManagerCommand";
    public static final String PROCESSMANAGER_COMMAND_CONFIG      = "ProcessManagerCommandConfig";
    public static MessageType  PROCESSMANAGER_COMMAND_CONFIG_TYPE = new MessageTypeExt(Category.Config, PROCESSMANAGER_COMMAND_CONFIG,             null, CommandConfig.class, COMMAND_CONFIG_TOPIC.VALUE+topicSeparator+PROCESSMANAGER_APPEND, "RapidCommandConfigProfile");
    public static MessageType  PROCESSMANAGER_COMMAND_TYPE        = new MessageTypeExt(Category.State,  PROCESSMANAGER_COMMAND, PROCESSMANAGER_COMMAND_CONFIG, Command.class,        COMMAND_TOPIC.VALUE+topicSeparator+PROCESSMANAGER_APPEND, "RapidCommandProfile");

    public static final String RANGESCAN_CONFIG      = "RangeScanConfig";
    public static final String RANGESCAN_SAMPLE      = "RangeScanSample";
    public static MessageType  RANGESCAN_CONFIG_TYPE = new MessageTypeExt(Category.Config, RANGESCAN_CONFIG,               null, RangeScanConfig.class, RANGESCAN_CONFIG_TOPIC.VALUE);
    public static MessageType  RANGESCAN_SAMPLE_TYPE = new MessageTypeExt(Category.Sample, RANGESCAN_SAMPLE,   RANGESCAN_CONFIG, RangeScanSample.class, RANGESCAN_SAMPLE_TOPIC.VALUE);

    public static final String SYSTEMINFO_CONFIG     = "SystemInfoConfig";
    public static final String SYSTEMINFO_SAMPLE     = "SystemInfoSample";
    public static MessageType  SYSTEMINFO_CONFIG_TYPE= new MessageTypeExt(Category.Config, SYSTEMINFO_CONFIG,              null, SystemInfoConfig.class, SYSTEMINFO_CONFIG_TOPIC.VALUE);
    public static MessageType  SYSTEMINFO_SAMPLE_TYPE= new MessageTypeExt(Category.Sample, SYSTEMINFO_SAMPLE, SYSTEMINFO_CONFIG, SystemInfoSample.class, SYSTEMINFO_SAMPLE_TOPIC.VALUE);
    
    public static final String TILED_CSPACEMAP_APPEND= "cspace_tiled";
    public static final String TILED_CSPACEMAP_CONFIG= "TiledCSpaceMapConfig";
    public static final String TILED_CSPACEMAP_SAMPLE= "TiledCSpaceMapSample";
    public static MessageType  TILED_CSPACEMAP_CONFIG_TYPE = new MessageTypeExt(Category.Config, TILED_CSPACEMAP_CONFIG,                   null, NavMapConfig.class, NAVMAP_CONFIG_TOPIC.VALUE+tsep+TILED_CSPACEMAP_APPEND, "RapidNavMapTilesConfigProfile");
    public static MessageType  TILED_CSPACEMAP_SAMPLE_TYPE = new MessageTypeExt(Category.Sample, TILED_CSPACEMAP_SAMPLE, TILED_CSPACEMAP_CONFIG, NavMapSample.class, NAVMAP_SAMPLE_TOPIC.VALUE+tsep+TILED_CSPACEMAP_APPEND, "RapidNavMapTilesSampleProfile");
    
    public static final String TILED_LOCALMAP_CONFIG = "TiledLocalMapConfig";
    public static final String TILED_LOCALMAP_SAMPLE = "TiledLocalMapSample";
    public static final String TILED_LOCALMAP_APPEND = "localmap_tiled";
    public static MessageType  TILED_LOCALMAP_CONFIG_TYPE = new MessageTypeExt(Category.Config, TILED_LOCALMAP_CONFIG,                  null, NavMapConfig.class, NAVMAP_CONFIG_TOPIC.VALUE+tsep+TILED_LOCALMAP_APPEND, "RapidNavMapTilesConfigProfile");
    public static MessageType  TILED_LOCALMAP_SAMPLE_TYPE = new MessageTypeExt(Category.Sample, TILED_LOCALMAP_SAMPLE, TILED_LOCALMAP_CONFIG, NavMapSample.class, NAVMAP_SAMPLE_TOPIC.VALUE+tsep+TILED_LOCALMAP_APPEND, "RapidNavMapTilesSampleProfile");
    
    public static final String TRAJECTORY2D_CONFIG   = "Trajectory2DConfig";
    public static final String TRAJECTORY2D_SAMPLE   = "Trajectory2DSample";
    public static MessageType  TRAJECTORY2D_CONFIG_TYPE = new MessageTypeExt(Category.Config, TRAJECTORY2D_CONFIG,                null, Trajectory2DConfig.class, TRAJECTORY2D_CONFIG_TOPIC.VALUE);
    public static MessageType  TRAJECTORY2D_SAMPLE_TYPE = new MessageTypeExt(Category.Sample, TRAJECTORY2D_SAMPLE, TRAJECTORY2D_CONFIG, Trajectory2DSample.class, TRAJECTORY2D_SAMPLE_TOPIC.VALUE);
    
    public static final String ALLSTOP_APPEND = "allstop";
    public static final String ALLSTOP_TRAJECTORY2D_CONFIG = "AllStopTrajectory2DConfig";
    public static final String ALLSTOP_TRAJECTORY2D_SAMPLE = "AllStopTrajectory2DSample";
    public static MessageType  ALLSTOP_TRAJECTORY2D_CONFIG_TYPE = new MessageTypeExt(Category.Config, ALLSTOP_TRAJECTORY2D_CONFIG,                        null, Trajectory2DConfig.class, TRAJECTORY2D_CONFIG_TOPIC.VALUE+tsep+ALLSTOP_APPEND, "RapidTrajectory2DConfigProfile");
    public static MessageType  ALLSTOP_TRAJECTORY2D_SAMPLE_TYPE = new MessageTypeExt(Category.Sample, ALLSTOP_TRAJECTORY2D_SAMPLE, ALLSTOP_TRAJECTORY2D_CONFIG, Trajectory2DSample.class, TRAJECTORY2D_SAMPLE_TOPIC.VALUE+tsep+ALLSTOP_APPEND, "RapidTrajectory2DSampleProfile");
    
    public static final String VESTOP_CONFIG      = "VEStopConfig";
    public static final String VESTOP_STATE       = "VEStopState";
    public static MessageType  VESTOP_CONFIG_TYPE = new MessageTypeExt(Category.Config, VESTOP_CONFIG,          null, VEStopConfig.class, VESTOP_CONFIG_TOPIC.VALUE, "RapidConfigQos");
    public static MessageType  VESTOP_STATE_TYPE  = new MessageTypeExt( Category.State,  VESTOP_STATE, VESTOP_CONFIG,  VEStopState.class,  VESTOP_STATE_TOPIC.VALUE, "RapidStateQos");
    
    public static final String ALLSTOP_COMMAND_CONFIG      = "AllStopCommandConfig";
    public static final String ALLSTOP_COMMAND             = "AllStopCommand";
    public static final String ALLSTOP_ACK                 = "AllStopAck";
    public static MessageType  ALLSTOP_COMMAND_CONFIG_TYPE = new MessageTypeExt(Category.Config, ALLSTOP_COMMAND_CONFIG,                   null, CommandConfig.class, COMMAND_CONFIG_TOPIC.VALUE+tsep+ALLSTOP_APPEND, "RapidCommandConfigProfile");
    public static MessageType  ALLSTOP_COMMAND_TYPE        = new MessageType   (Category.State,  ALLSTOP_COMMAND,        ALLSTOP_COMMAND_CONFIG,       Command.class,        COMMAND_TOPIC.VALUE+tsep+ALLSTOP_APPEND, "RapidCommandProfile");
    public static MessageType  ALLSTOP_ACK_TYPE            = new MessageType   (Category.Simple, ALLSTOP_ACK,                              null,           Ack.class,            ACK_TOPIC.VALUE+tsep+ALLSTOP_APPEND, "RapidAckProfile");

    //-- Position extensions ----------------------------
    public static final String LOCALMAP_ALIGNED_APPEND               = "localmap_aligned";
    public static final String LOCALMAP_ALIGNED_POSITION_CONFIG      = "LocalMapAlignedPositionConfig";
    public static final String LOCALMAP_ALIGNED_POSITION_SAMPLE      = "LocalMapAlignedPositionSample";
    public static MessageType  LOCALMAP_ALIGNED_POSITION_SAMPLE_TYPE = new MessageType(Category.Sample, LOCALMAP_ALIGNED_POSITION_SAMPLE, LOCALMAP_ALIGNED_POSITION_CONFIG, PositionSample.class, POSITION_SAMPLE_TOPIC.VALUE+tsep+LOCALMAP_ALIGNED_APPEND, "RapidPositionSampleProfile");
    public static MessageType  LOCALMAP_ALIGNED_POSITION_CONFIG_TYPE = new MessageType(Category.Config, LOCALMAP_ALIGNED_POSITION_CONFIG,                             null, PositionConfig.class, POSITION_CONFIG_TOPIC.VALUE+tsep+LOCALMAP_ALIGNED_APPEND, "RapidPositionConfigProfile" );

    public static final String GLOBAL_ESTIMATED_POSITION_CONFIG      = "GlobalEstimatedPositionConfig";
    public static final String GLOBAL_ESTIMATED_POSITION_SAMPLE      = "GlobalEstimatedPositionSample";
    public static final String GLOBAL_ESTIMATED_TOPIC_APPEND         = "global_estimated";
    public static MessageType  GLOBAL_ESTIMATED_POSITION_SAMPLE_TYPE = new MessageType(Category.Sample, GLOBAL_ESTIMATED_POSITION_SAMPLE, GLOBAL_ESTIMATED_POSITION_CONFIG, PositionSample.class, POSITION_SAMPLE_TOPIC.VALUE+topicSeparator+GLOBAL_ESTIMATED_TOPIC_APPEND, "RapidPositionSampleProfile");
    public static MessageType  GLOBAL_ESTIMATED_POSITION_CONFIG_TYPE = new MessageType(Category.Config, GLOBAL_ESTIMATED_POSITION_CONFIG,                             null, PositionConfig.class, POSITION_CONFIG_TOPIC.VALUE+topicSeparator+GLOBAL_ESTIMATED_TOPIC_APPEND, "RapidPositionConfigProfile" );

    public static final String INS_POSITION_CONFIG      = "InsPositionConfig";
    public static final String INS_POSITION_SAMPLE      = "InsPositionSample";
    public static final String INS_TOPIC_APPEND         = "ins";
    public static MessageType  INS_POSITION_SAMPLE_TYPE = new MessageType(Category.Sample, INS_POSITION_SAMPLE, INS_POSITION_CONFIG, PositionSample.class, POSITION_SAMPLE_TOPIC.VALUE+topicSeparator+INS_TOPIC_APPEND, "RapidPositionSampleProfile");
    public static MessageType  INS_POSITION_CONFIG_TYPE = new MessageType(Category.Config, INS_POSITION_CONFIG,                null, PositionConfig.class, POSITION_CONFIG_TOPIC.VALUE+topicSeparator+INS_TOPIC_APPEND, "RapidPositionConfigProfile" );

    //-- PointCloud
    public static final String STEREO_POINTCLOUD_CONFIG      = "StereoPointCloudConfig";
    public static final String STEREO_POINTCLOUD_SAMPLE      = "StereoPointCloudSample";
    public static final String STEREO_POINTCLOUD_APPEND      = "stereo";
    public static MessageType  STEREO_POINTCLOUD_CONFIG_TYPE = new MessageTypeExt(Category.Config, STEREO_POINTCLOUD_CONFIG,                       null, PointCloudConfig.class, POINTCLOUD_CONFIG_TOPIC.VALUE+topicSeparator+STEREO_POINTCLOUD_APPEND, "RapidPointCloudConfigProfile");
    public static MessageType  STEREO_POINTCLOUD_SAMPLE_TYPE = new MessageTypeExt(Category.Sample, STEREO_POINTCLOUD_SAMPLE,   STEREO_POINTCLOUD_CONFIG, PointCloudSample.class, POINTCLOUD_SAMPLE_TOPIC.VALUE+topicSeparator+STEREO_POINTCLOUD_APPEND, "RapidPointCloudSampleProfile");

    public static final String GENARCS_POINTCLOUD_CONFIG      = "GenArcsPointCloudConfig";
    public static final String GENARCS_POINTCLOUD_SAMPLE      = "GenArcsPointCloudSample";
    public static final String GENARCS_POINTCLOUD_APPEND      = "generated_arcs";
    public static MessageType  GENARCS_POINTCLOUD_CONFIG_TYPE = new MessageTypeExt(Category.Config, GENARCS_POINTCLOUD_CONFIG,                        null, PointCloudConfig.class, POINTCLOUD_CONFIG_TOPIC.VALUE+topicSeparator+GENARCS_POINTCLOUD_APPEND, "RapidPointCloudConfigProfile");
    public static MessageType  GENARCS_POINTCLOUD_SAMPLE_TYPE = new MessageTypeExt(Category.Sample, GENARCS_POINTCLOUD_SAMPLE,   GENARCS_POINTCLOUD_CONFIG, PointCloudSample.class, POINTCLOUD_SAMPLE_TOPIC.VALUE+topicSeparator+GENARCS_POINTCLOUD_APPEND, "RapidPointCloudSampleProfile");

    public static final String DEBUGLINES_POINTCLOUD_CONFIG      = "DebugLinesPointCloudConfig";
    public static final String DEBUGLINES_POINTCLOUD_SAMPLE      = "DebugLinesPointCloudSample";
    public static final String DEBUGLINES_POINTCLOUD_APPEND      = "debug_lines";
    public static MessageType  DEBUGLINES_POINTCLOUD_CONFIG_TYPE = new MessageTypeExt(Category.Config, DEBUGLINES_POINTCLOUD_CONFIG,                           null, PointCloudConfig.class, POINTCLOUD_CONFIG_TOPIC.VALUE+topicSeparator+DEBUGLINES_POINTCLOUD_APPEND, "RapidPointCloudConfigProfile");
    public static MessageType  DEBUGLINES_POINTCLOUD_SAMPLE_TYPE = new MessageTypeExt(Category.Sample, DEBUGLINES_POINTCLOUD_SAMPLE,   DEBUGLINES_POINTCLOUD_CONFIG, PointCloudSample.class, POINTCLOUD_SAMPLE_TOPIC.VALUE+topicSeparator+DEBUGLINES_POINTCLOUD_APPEND, "RapidPointCloudSampleProfile");

    public static final String PLANPATH_POINTCLOUD_CONFIG      = "PlanPathPointCloudConfig";
    public static final String PLANPATH_POINTCLOUD_SAMPLE      = "PlanPathPointCloudSample";
    public static final String PLANPATH_POINTCLOUD_APPEND      = "planner_path";
    public static MessageType  PLANPATH_POINTCLOUD_CONFIG_TYPE = new MessageTypeExt(Category.Config, PLANPATH_POINTCLOUD_CONFIG,                         null, PointCloudConfig.class, POINTCLOUD_CONFIG_TOPIC.VALUE+topicSeparator+PLANPATH_POINTCLOUD_APPEND, "RapidPointCloudConfigProfile");
    public static MessageType  PLANPATH_POINTCLOUD_SAMPLE_TYPE = new MessageTypeExt(Category.Sample, PLANPATH_POINTCLOUD_SAMPLE,   PLANPATH_POINTCLOUD_CONFIG, PointCloudSample.class, POINTCLOUD_SAMPLE_TOPIC.VALUE+topicSeparator+PLANPATH_POINTCLOUD_APPEND, "RapidPointCloudSampleProfile");


    protected MessageTypeExt(Category category, String name, String configName, Class dataType, String topic, 
                             String qosProfile) {
        super(category, name, configName, dataType, topic, qosProfile);
    }

    protected MessageTypeExt(Category category, String name, String configName, Class dataType, String topic) {
        this(category, name, configName, dataType, topic, PREFIX+name+POSTFIX);
    }


}
