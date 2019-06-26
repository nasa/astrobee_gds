package gov.nasa.rapid.v2.message;

import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.MessageTypeExt;
import rapid.ext.arc.BATTERYPACK_CONFIG_TOPIC;
import rapid.ext.arc.BATTERYPACK_SAMPLE_TOPIC;
import rapid.ext.arc.BatteryPackConfig;
import rapid.ext.arc.BatteryPackSample;
import rapid.ext.arc.DLP_CONFIG_TOPIC;
import rapid.ext.arc.DLP_SAMPLE_TOPIC;
import rapid.ext.arc.DlpConfig;
import rapid.ext.arc.DlpSample;
import rapid.ext.arc.EPHEMERIS_CONFIG_TOPIC;
import rapid.ext.arc.EPHEMERIS_SAMPLE_TOPIC;
import rapid.ext.arc.EphemerisConfig;
import rapid.ext.arc.EphemerisSample;
import rapid.ext.arc.FLOAT32_CONFIG_TOPIC;
import rapid.ext.arc.FLOAT32_SAMPLE_TOPIC;
import rapid.ext.arc.Float32Config;
import rapid.ext.arc.Float32Sample;
import rapid.ext.arc.GEOMETRYMESH_SAMPLE_TOPIC;
import rapid.ext.arc.GEOMETRY_CONFIG_TOPIC;
import rapid.ext.arc.GPS_CONFIG_TOPIC;
import rapid.ext.arc.GPS_SAMPLE_TOPIC;
import rapid.ext.arc.GeometryConfig;
import rapid.ext.arc.GeometryMeshSample;
import rapid.ext.arc.GpsConfig;
import rapid.ext.arc.GpsSample;
import rapid.ext.arc.MOBILITY_SAMPLE_TOPIC;
import rapid.ext.arc.MobilitySample;
import rapid.ext.arc.STATEMACHINE_CONFIG_TOPIC;
import rapid.ext.arc.STATEMACHINE_STATE_TOPIC;
import rapid.ext.arc.StateMachineConfig;
import rapid.ext.arc.StateMachineState;

/**
 * 
 */
public class MessageTypeExtArc extends MessageTypeExt {
    public static final String BATTERYPACK_CONFIG = "BatteryPackConfig";
    public static final String BATTERYPACK_SAMPLE = "BatteryPackSample";
    public static MessageType  BATTERYPACK_CONFIG_TYPE = new MessageTypeExtArc(Category.Config, BATTERYPACK_CONFIG,               null, BatteryPackConfig.class, BATTERYPACK_CONFIG_TOPIC.VALUE, "RapidBatteryPackConfigProfile");
    public static MessageType  BATTERYPACK_SAMPLE_TYPE = new MessageTypeExtArc(Category.Sample, BATTERYPACK_SAMPLE, BATTERYPACK_CONFIG, BatteryPackSample.class, BATTERYPACK_SAMPLE_TOPIC.VALUE, "RapidBatteryPackSampleProfile");
    
    public static final String DLP_CONFIG = "DlpConfig";
    public static final String DLP_SAMPLE = "DlpSample";
    public static MessageType  DLP_CONFIG_TYPE = new MessageTypeExtArc(Category.Config, DLP_CONFIG,       null, DlpConfig.class, DLP_CONFIG_TOPIC.VALUE, "RapidDlpConfigProfile");
    public static MessageType  DLP_SAMPLE_TYPE = new MessageTypeExtArc(Category.Sample, DLP_SAMPLE, DLP_CONFIG, DlpSample.class, DLP_SAMPLE_TOPIC.VALUE, "RapidDlpSampleProfile");
    
    public static final String EPHEMERIS_CONFIG = "EphemerisConfig";
    public static final String EPHEMERIS_SAMPLE = "EphemerisSample";
    public static MessageType  EPHEMERIS_CONFIG_TYPE = new MessageTypeExtArc(Category.Config, EPHEMERIS_CONFIG,             null, EphemerisConfig.class, EPHEMERIS_CONFIG_TOPIC.VALUE, "RapidEphemerisConfigProfile");
    public static MessageType  EPHEMERIS_SAMPLE_TYPE = new MessageTypeExtArc(Category.Sample, EPHEMERIS_SAMPLE, EPHEMERIS_CONFIG, EphemerisSample.class, EPHEMERIS_SAMPLE_TOPIC.VALUE, "RapidEphemerisSampleProfile");
    
    public static final String FLOAT32_CONFIG = "Float32Config";
    public static final String FLOAT32_SAMPLE = "Float32Sample";
    public static MessageType  FLOAT32_CONFIG_TYPE = new MessageTypeExtArc(Category.Config, FLOAT32_CONFIG,           null, Float32Config.class, FLOAT32_CONFIG_TOPIC.VALUE, "RapidFloat32ConfigProfile");
    public static MessageType  FLOAT32_SAMPLE_TYPE = new MessageTypeExtArc(Category.Sample, FLOAT32_SAMPLE, FLOAT32_CONFIG, Float32Sample.class, FLOAT32_SAMPLE_TOPIC.VALUE, "RapidFloat32SampleProfile");
    
    public static final String GEOMETRY_CONFIG     = "GeometryConfig";
    public static final String GEOMETRYMESH_SAMPLE = "GeometryMeshSample";
    public static final String GEOMETRY_APPEARANCE_STATE = "GeometryAppearanceState";
    public static MessageType  GEOMETRY_CONFIG_TYPE      = new MessageTypeExtArc(Category.Config,     GEOMETRY_CONFIG,            null,     GeometryConfig.class,     GEOMETRY_CONFIG_TOPIC.VALUE, "RapidGeometryConfigProfile");
    public static MessageType  GEOMETRYMESH_SAMPLE_TYPE  = new MessageTypeExtArc(Category.Config, GEOMETRYMESH_SAMPLE, GEOMETRY_CONFIG, GeometryMeshSample.class, GEOMETRYMESH_SAMPLE_TOPIC.VALUE, "RapidGeometryMeshSampleProfile");
    
    public static final String GPS_CONFIG = "GpsConfig";
    public static final String GPS_SAMPLE = "GpsSample";
    public static MessageType  GPS_CONFIG_TYPE = new MessageTypeExtArc(Category.Config, GPS_CONFIG,       null, GpsConfig.class, GPS_CONFIG_TOPIC.VALUE, "RapidGpsConfigProfile");
    public static MessageType  GPS_SAMPLE_TYPE = new MessageTypeExtArc(Category.Sample, GPS_SAMPLE, GPS_CONFIG, GpsSample.class, GPS_SAMPLE_TOPIC.VALUE, "RapidGpsSampleProfile");
    
    public static final String MOBILITY_SAMPLE = "MobilitySample";
    public static MessageType  MOBILITY_SAMPLE_TYPE = new MessageTypeExtArc(Category.Sample, MOBILITY_SAMPLE, null, MobilitySample.class, MOBILITY_SAMPLE_TOPIC.VALUE, "RapidMobilitySampleProfile");
    
    public static final String STATEMACHINE_CONFIG = "StateMachineConfig";
    public static final String STATEMACHINE_STATE  = "StateMachineState";
    public static MessageType  STATEMACHINE_CONFIG_TYPE = new MessageTypeExtArc(Category.Config, STATEMACHINE_CONFIG,                null, StateMachineConfig.class, STATEMACHINE_CONFIG_TOPIC.VALUE, "RapidStateMachineConfigProfile");
    public static MessageType  STATEMACHINE_STATE_TYPE  = new MessageTypeExtArc(Category.State,  STATEMACHINE_STATE , STATEMACHINE_CONFIG,  StateMachineState.class,  STATEMACHINE_STATE_TOPIC.VALUE, "RapidStateMachineStateProfile");
    
    protected MessageTypeExtArc(Category category, String name, String configName, Class dataType, String topic, 
                             String qosProfile) {
        super(category, name, configName, dataType, topic, qosProfile);
    }

    protected MessageTypeExtArc(Category category, String name, String configName, Class dataType, String topic) {
        this(category, name, configName, dataType, topic, PREFIX+name+POSTFIX);
    }


}
