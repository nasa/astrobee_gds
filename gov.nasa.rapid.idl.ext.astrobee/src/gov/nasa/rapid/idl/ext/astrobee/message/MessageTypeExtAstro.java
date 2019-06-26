package gov.nasa.rapid.idl.ext.astrobee.message;

import gov.nasa.rapid.v2.e4.message.MessageType;
import rapid.COMMAND_TOPIC;
import rapid.Command;
import rapid.IMAGESENSOR_SAMPLE_TOPIC;
import rapid.ImageSensorSample;
import rapid.ext.astrobee.AGENT_STATE_TOPIC;
import rapid.ext.astrobee.ARM_STATE_TOPIC;
import rapid.ext.astrobee.AgentState;
import rapid.ext.astrobee.ArmState;
import rapid.ext.astrobee.COMM_STATE_TOPIC;
import rapid.ext.astrobee.COMPONENT_CONFIG_TOPIC;
import rapid.ext.astrobee.COMPONENT_STATE_TOPIC;
import rapid.ext.astrobee.COMPRESSED_FILE_ACK_TOPIC;
import rapid.ext.astrobee.COMPRESSED_FILE_TOPIC;
import rapid.ext.astrobee.CPU_CONFIG_TOPIC;
import rapid.ext.astrobee.CPU_STATE_TOPIC;
import rapid.ext.astrobee.CommState;
import rapid.ext.astrobee.ComponentConfig;
import rapid.ext.astrobee.ComponentState;
import rapid.ext.astrobee.CompressedFile;
import rapid.ext.astrobee.CompressedFileAck;
import rapid.ext.astrobee.CpuConfig;
import rapid.ext.astrobee.CpuState;
import rapid.ext.astrobee.DATA_TOPICS_LIST_TOPIC;
import rapid.ext.astrobee.DATA_TO_DISK_STATE_TOPIC;
import rapid.ext.astrobee.DISK_CONFIG_TOPIC;
import rapid.ext.astrobee.DISK_STATE_TOPIC;
import rapid.ext.astrobee.DOCK_STATE_TOPIC;
import rapid.ext.astrobee.DataToDiskState;
import rapid.ext.astrobee.DataTopicsList;
import rapid.ext.astrobee.DiskConfig;
import rapid.ext.astrobee.DiskState;
import rapid.ext.astrobee.DockState;
import rapid.ext.astrobee.EKF_STATE_TOPIC;
import rapid.ext.astrobee.EPS_CONFIG_TOPIC;
import rapid.ext.astrobee.EPS_STATE_TOPIC;
import rapid.ext.astrobee.EkfState;
import rapid.ext.astrobee.EpsConfig;
import rapid.ext.astrobee.EpsState;
import rapid.ext.astrobee.FAULT_CONFIG_TOPIC;
import rapid.ext.astrobee.FAULT_STATE_TOPIC;
import rapid.ext.astrobee.FaultConfig;
import rapid.ext.astrobee.FaultState;
import rapid.ext.astrobee.GNC_CONTROL_STATE_TOPIC;
import rapid.ext.astrobee.GNC_FAM_CMD_STATE_TOPIC;
import rapid.ext.astrobee.GUEST_SCIENCE_CONFIG_TOPIC;
import rapid.ext.astrobee.GUEST_SCIENCE_DATA_TOPIC;
import rapid.ext.astrobee.GUEST_SCIENCE_STATE_TOPIC;
import rapid.ext.astrobee.GncControlState;
import rapid.ext.astrobee.GncFamCmdState;
import rapid.ext.astrobee.GuestScienceConfig;
import rapid.ext.astrobee.GuestScienceData;
import rapid.ext.astrobee.GuestScienceState;
import rapid.ext.astrobee.INERTIAL_PROPERTIES_TOPIC;
import rapid.ext.astrobee.InertialProperties;
import rapid.ext.astrobee.MOBILITY_SETTINGS_STATE_TOPIC;
import rapid.ext.astrobee.MobilitySettingsState;
import rapid.ext.astrobee.PLAN_STATUS_TOPIC;
import rapid.ext.astrobee.PlanStatus;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_DOCK;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_HAZ;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_NAV;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_PERCH;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_SCI;
import rapid.ext.astrobee.TELEMETRY_CONFIG_TOPIC;
import rapid.ext.astrobee.TELEMETRY_STATE_TOPIC;
import rapid.ext.astrobee.TelemetryConfig;
import rapid.ext.astrobee.TelemetryState;

public class MessageTypeExtAstro extends MessageType {
	public static final String AGENT_STATE = "AstroAgentState";
	public static final String ARM_GRIPPER = "ArmGripper";
	public static final String ARM_MOBILITY = "ArmMobility";
	public static final String ARM_STATE = "ArmState";
	public static final String COMM_STATE = "CommState";
	public static final String COMMAND_ECHO = "CommandEcho";
	public static final String COMPONENT_CONFIG = "ComponentConfig";
	public static final String COMPONENT_STATE = "ComponentState";
	public static final String COMPRESSEDFILE = "CompressedFile";
	public static final String COMPRESSEDFILEACK = "CompressedFileAck";
	
	public static final String CPU_CONFIG = "CpuConfig";
	public static final String CPU_STATE = "CpuState";
	
	public static final String CURRENT_PLAN_COMPRESSEDFILE = "CurrentPlanCompressedFile";
	public static final String DATA_TO_DISK_COMPRESSEDFILE = "DataToDiskCompressedFile";
	public static final String DATA_TOPICS_LIST = "DataTopicsList";
	public static final String DATA_TO_DISK_STATE = "DataToDiskState";
	public static final String DISK_CONFIG = "DiskConfig";
	public static final String DISK_STATE = "DiskState";
	public static final String DOCK_STATE = "Dock";
	
	public static final String EKF_STATE = "EkfState";
	
	public static final String EPS_STATE = "EpsState";
	public static final String EPS_CONFIG = "EpsConfig";
	public static final String FAULT_CONFIG = "FaultConfig";
	public static final String FAULT_STATE = "FaultState";
	public static final String GUEST_SCIENCE_CONFIG = "GuestScienceConfig";
	public static final String GUEST_SCIENCE_STATE = "GuestScienceState";
	public static final String GUEST_SCIENCE_DATA = "GuestScienceData";
	public static final String GNC_CONTROL_STATE = "GncControlState";
	public static final String GNC_FAM_CMD_STATE = "GncFamCmdState";
	
	public static final String INERTIAL_PROPERTIES = "Inertial_Properties";
	public static final String KEEPOUTS_COMPRESSEDFILE = "KeepoutsCompressedFile";
	public static final String CURRENT_ZONES_COMPRESSEDFILE = "CurrentZonesCompressedFile";
	public static final String MOBILITY_SETTINGS_STATE = "MobilitySettingsState";
	public static final String PAYLOAD_MESSAGE_FLOAT = "PayloadMessageFloat";
	public static final String PAYLOAD_MESSAGE_INT = "PayloadMessageInt";
	public static final String PAYLOAD_MESSAGE_OCTET = "PayloadMessageOctet";
	public static final String PAYLOAD_CONFIG = "PayloadConfig";
	public static final String PAYLOAD_STATE = "PayloadState";
	public static final String PLAN_STATUS = "PlanStatus";
	public static final String TELEMETRY_CONFIG = "TelemetryConfig";
	public static final String TELEMETRY_STATE = "TelemetryState";

	public static MessageType AGENT_STATE_TYPE				= new MessageTypeExtAstro(Category.Simple, AGENT_STATE,				null, AgentState.class,		AGENT_STATE_TOPIC.VALUE,"AstrobeeAgentStateProfile");
	public static MessageType ARM_STATE_TYPE 				= new MessageTypeExtAstro(Category.Simple, ARM_STATE, 				null, ArmState.class, 		ARM_STATE_TOPIC.VALUE,	"AstrobeeArmStateProfile");
	public static MessageType COMM_STATE_TYPE 				= new MessageTypeExtAstro(Category.Simple, COMM_STATE, 				null, CommState.class, 		COMM_STATE_TOPIC.VALUE,	"AstrobeeCommStateProfile");
	public static MessageType COMMAND_ECHO_TYPE             = new MessageTypeExtAstro(Category.State,  COMMAND_ECHO,       COMMAND_CONFIG, Command.class,        COMMAND_TOPIC.VALUE+topicSeparator+"echo",	"RapidCommandProfile");
	public static MessageType COMPONENT_CONFIG_TYPE 		= new MessageTypeExtAstro(Category.Config, COMPONENT_CONFIG, 		null, 	  ComponentConfig.class, COMPONENT_CONFIG_TOPIC.VALUE, 	"AstrobeeComponentConfigProfile");
	public static MessageType COMPONENT_STATE_TYPE 			= new MessageTypeExtAstro(Category.Simple, COMPONENT_STATE, COMPONENT_CONFIG, ComponentState.class,	 COMPONENT_STATE_TOPIC.VALUE,	"AstrobeeComponentStateProfile");

	public static MessageType COMPRESSED_FILE_TYPE			= new MessageTypeExtAstro(Category.Simple, COMPRESSEDFILE, 			null, CompressedFile.class,		COMPRESSED_FILE_TOPIC.VALUE,"AstrobeeCompressedFileProfile");
	public static MessageType COMPRESSED_FILE_ACK_TYPE		= new MessageTypeExtAstro(Category.Simple, COMPRESSEDFILEACK, 		null, CompressedFileAck.class,  COMPRESSED_FILE_ACK_TOPIC.VALUE, "AstrobeeCompressedFileAckProfile");

	public static MessageType CPU_CONFIG_TYPE				= new MessageTypeExtAstro(Category.Config, CPU_CONFIG, 		null, CpuConfig.class,  CPU_CONFIG_TOPIC.VALUE, "AstrobeeCpuConfigProfile");
	public static MessageType CPU_STATE_TYPE				= new MessageTypeExtAstro(Category.Simple, CPU_STATE, 		null, CpuState.class,  CPU_STATE_TOPIC.VALUE, "AstrobeeCpuStateProfile");

	
	public static MessageType CURRENT_PLAN_COMPRESSED_TYPE	= new MessageTypeExtAstro(Category.Simple, CURRENT_PLAN_COMPRESSEDFILE, null, CompressedFile.class,	COMPRESSED_FILE_TOPIC.VALUE+topicSeparator+"current_plan","AstrobeeCurrentCompressedPlanProfile");

	public static MessageType DATA_TO_DISK_COMPRESSED_TYPE	= new MessageTypeExtAstro(Category.Simple, DATA_TO_DISK_COMPRESSEDFILE, null, CompressedFile.class,	COMPRESSED_FILE_TOPIC.VALUE+topicSeparator+"data_to_disk","AstrobeeCompressedFileProfile");
	public static MessageType ZONES_COMPRESSED_TYPE			= new MessageTypeExtAstro(Category.Simple, KEEPOUTS_COMPRESSEDFILE, 	null, CompressedFile.class,	COMPRESSED_FILE_TOPIC.VALUE+topicSeparator+"zones","AstrobeeCompressedFileProfile");
	public static MessageType CURRENT_ZONES_COMPRESSED_TYPE	= new MessageTypeExtAstro(Category.Simple, CURRENT_ZONES_COMPRESSEDFILE,null, CompressedFile.class, COMPRESSED_FILE_TOPIC.VALUE+topicSeparator+"current_zones","AstrobeeCompressedFileProfile");
	
	public static MessageType DATA_TOPICS_LIST_TYPE 		= new MessageTypeExtAstro(Category.Config, DATA_TOPICS_LIST, 		null, 		 			DataTopicsList.class,		DATA_TOPICS_LIST_TOPIC.VALUE, 	"AstrobeeDataTopicsListProfile");

	public static MessageType DATA_TO_DISK_STATE_TYPE 		= new MessageTypeExtAstro(Category.Simple, DATA_TO_DISK_STATE, 		DATA_TOPICS_LIST,	 	DataToDiskState.class,		DATA_TO_DISK_STATE_TOPIC.VALUE, 	"AstrobeeDataToDiskStateProfile");
	public static MessageType DISK_CONFIG_TYPE 				= new MessageTypeExtAstro(Category.Config, DISK_CONFIG, 			null, 		 			DiskConfig.class,			DISK_CONFIG_TOPIC.VALUE, 			"AstrobeeDiskConfigProfile");
	public static MessageType DISK_STATE_TYPE 				= new MessageTypeExtAstro(Category.Simple, DISK_STATE, 				DISK_CONFIG, 			DiskState.class,			DISK_STATE_TOPIC.VALUE,				"AstrobeeDiskStateProfile");
	public static MessageType DOCK_STATE_TYPE				= new MessageTypeExtAstro(Category.Simple, DOCK_STATE, 				null, 					DockState.class, 			DOCK_STATE_TOPIC.VALUE,				"AstrobeeDockStateProfile");
	
	public static MessageType EKF_STATE_TYPE 				= new MessageTypeExtAstro(Category.Simple, EKF_STATE, 				null,	EkfState.class,			EKF_STATE_TOPIC.VALUE,		"AstrobeeEkfStateProfile");

	public static MessageType EPS_CONFIG_TYPE 				= new MessageTypeExtAstro(Category.Config, EPS_CONFIG, 				null, 		EpsConfig.class,		EPS_CONFIG_TOPIC.VALUE, 	"AstrobeeEpsConfigProfile");
	public static MessageType EPS_STATE_TYPE 				= new MessageTypeExtAstro(Category.Simple, EPS_STATE, 				EPS_CONFIG,	EpsState.class,			EPS_STATE_TOPIC.VALUE,		"AstrobeeEpsStateProfile");
	
	public static MessageType FAULT_CONFIG_TYPE 			= new MessageTypeExtAstro(Category.Config, FAULT_CONFIG, 			null, FaultConfig.class, 	 		FAULT_CONFIG_TOPIC.VALUE,	"AstrobeeFaultConfigProfile");
	public static MessageType FAULT_STATE_TYPE 				= new MessageTypeExtAstro(Category.Simple, FAULT_STATE, 			null, FaultState.class, 			FAULT_STATE_TOPIC.VALUE,	"AstrobeeFaultStateProfile");
	
	public static MessageType GUEST_SCIENCE_CONFIG_TYPE		= new MessageTypeExtAstro(Category.Config, GUEST_SCIENCE_CONFIG,	null, GuestScienceConfig.class, GUEST_SCIENCE_CONFIG_TOPIC.VALUE,"AstrobeeGuestScienceConfigProfile");
	public static MessageType GUEST_SCIENCE_STATE_TYPE  	= new MessageTypeExtAstro(Category.Simple, GUEST_SCIENCE_STATE,		null, GuestScienceState.class, 	GUEST_SCIENCE_STATE_TOPIC.VALUE, "AstrobeeGuestScienceStateProfile");
	public static MessageType GUEST_SCIENCE_DATA_TYPE 		= new MessageTypeExtAstro(Category.Sample, GUEST_SCIENCE_DATA, 		null, GuestScienceData.class, 	GUEST_SCIENCE_DATA_TOPIC.VALUE,  "AstrobeeGuestScienceDataProfile");

	public static MessageType GNC_CONTROL_STATE_SHAPER_TYPE = new MessageTypeExtAstro(Category.Simple, GNC_CONTROL_STATE+topicSeparator+"shaper", null, GncControlState.class, 	GNC_CONTROL_STATE_TOPIC.VALUE+topicSeparator+"shaper", "AstrobeeGncControlStateProfile");
	public static MessageType GNC_CONTROL_STATE_TRAJ_TYPE  	= new MessageTypeExtAstro(Category.Simple, GNC_CONTROL_STATE+topicSeparator+"traj",	null, GncControlState.class, 	GNC_CONTROL_STATE_TOPIC.VALUE+topicSeparator+"traj", "AstrobeeGncControlStateProfile");
	public static MessageType GNC_FAM_CMD_STATE_TYPE 		= new MessageTypeExtAstro(Category.Sample, GNC_FAM_CMD_STATE, 		null, GncFamCmdState.class, 	GNC_FAM_CMD_STATE_TOPIC.VALUE,  "AstrobeeGncFamCmdStateProfile");

	public static MessageType INERTIAL_PROPERTIES_TYPE		= new MessageTypeExtAstro(Category.Simple, INERTIAL_PROPERTIES, 	null, InertialProperties.class, 	INERTIAL_PROPERTIES_TOPIC.VALUE, "AstrobeeInertiaProfile");
	
	public static MessageType IMAGESENSOR_SAMPLE_SCI_TYPE = new MessageTypeExtAstro(Category.Simple, IMAGESENSOR_SAMPLE+topicSeparator+SETTINGS_CAMERA_NAME_SCI.VALUE, 	null, ImageSensorSample.class, 	IMAGESENSOR_SAMPLE_TOPIC.VALUE+topicSeparator+SETTINGS_CAMERA_NAME_SCI.VALUE, "RapidImageSensorSampleProfile");
	public static MessageType IMAGESENSOR_SAMPLE_NAV_TYPE = new MessageTypeExtAstro(Category.Simple, IMAGESENSOR_SAMPLE+topicSeparator+SETTINGS_CAMERA_NAME_NAV.VALUE, 	null, ImageSensorSample.class, 	IMAGESENSOR_SAMPLE_TOPIC.VALUE+topicSeparator+SETTINGS_CAMERA_NAME_NAV.VALUE, "RapidImageSensorSampleProfile");
	public static MessageType IMAGESENSOR_SAMPLE_HAZ_TYPE = new MessageTypeExtAstro(Category.Simple, IMAGESENSOR_SAMPLE+topicSeparator+SETTINGS_CAMERA_NAME_HAZ.VALUE, 	null, ImageSensorSample.class, 	IMAGESENSOR_SAMPLE_TOPIC.VALUE+topicSeparator+SETTINGS_CAMERA_NAME_HAZ.VALUE, "RapidImageSensorSampleProfile");
	public static MessageType IMAGESENSOR_SAMPLE_DOCK_TYPE = new MessageTypeExtAstro(Category.Simple, IMAGESENSOR_SAMPLE+topicSeparator+SETTINGS_CAMERA_NAME_DOCK.VALUE, 	null, ImageSensorSample.class, 	IMAGESENSOR_SAMPLE_TOPIC.VALUE+topicSeparator+SETTINGS_CAMERA_NAME_DOCK.VALUE, "RapidImageSensorSampleProfile");
	public static MessageType IMAGESENSOR_SAMPLE_PERCH_TYPE = new MessageTypeExtAstro(Category.Simple, IMAGESENSOR_SAMPLE+topicSeparator+SETTINGS_CAMERA_NAME_PERCH.VALUE, null, ImageSensorSample.class, 	IMAGESENSOR_SAMPLE_TOPIC.VALUE+topicSeparator+SETTINGS_CAMERA_NAME_PERCH.VALUE, "RapidImageSensorSampleProfile");
	
	public static MessageType MOBILITY_SETTINGS_STATE_TYPE 	= new MessageTypeExtAstro(Category.Simple, MOBILITY_SETTINGS_STATE,	null, MobilitySettingsState.class,	MOBILITY_SETTINGS_STATE_TOPIC.VALUE, "AstrobeeMobilitySettingsStateProfile");	
	
//	public static MessageType PAYLOAD_MESSAGE_FLOAT_TYPE 	= new MessageTypeExtAstro(Category.Simple, PAYLOAD_MESSAGE_FLOAT, 	null, 			PayloadMessageFloat.class, 		PAYLOAD_MESSAGE_FLOAT_TOPIC.VALUE,	"AstrobeePayloadMessageFloat");
//	public static MessageType PAYLOAD_MESSAGE_INT_TYPE 	    = new MessageTypeExtAstro(Category.Simple, PAYLOAD_MESSAGE_INT, 	null, 			PayloadMessageInt.class, 		PAYLOAD_MESSAGE_INT_TOPIC.VALUE,	"AstrobeePayloadMessageInt");
//	public static MessageType PAYLOAD_MESSAGE_OCTET_TYPE 	= new MessageTypeExtAstro(Category.Simple, PAYLOAD_MESSAGE_OCTET, 	null, 			PayloadMessageOctet.class, 		PAYLOAD_MESSAGE_OCTET_TOPIC.VALUE,	"AstrobeePayloadMessageOctet");
//	public static MessageType PAYLOAD_CONFIG_TYPE 			= new MessageTypeExtAstro(Category.Config, PAYLOAD_CONFIG, 			null, 			PayloadConfig.class, 			PAYLOAD_CONFIG_TOPIC.VALUE,			"AstrobeePayloadConfig");
//	public static MessageType PAYLOAD_STATE_TYPE 			= new MessageTypeExtAstro(Category.Simple, PAYLOAD_STATE, 			PAYLOAD_CONFIG, PayloadState.class, 			PAYLOAD_STATE_TOPIC.VALUE,			"AstrobeePayloadState");
	public static MessageType PLAN_STATUS_TYPE 				= new MessageTypeExtAstro(Category.Simple, PLAN_STATUS, 			null, 			PlanStatus.class, 				PLAN_STATUS_TOPIC.VALUE,			"AstrobeePlanStatusProfile");

//	public static MessageType SAVE_SETTING_TYPE	 			= new MessageTypeExtAstro(Category.Simple, SAVE_SETTING, 			null, 			SaveSetting.class, 			SAVE_SETTING_TOPIC.VALUE,	"AstrobeeSaveSetting");
//	public static MessageType STATUS_TYPE	 				= new MessageTypeExtAstro(Category.Simple, STATUS, 					null, 			Status.class, 				STATUS_TOPIC.VALUE,			"AstrobeeStatus");

	public static MessageType TELEMETRY_CONFIG_TYPE			= new MessageTypeExtAstro(Category.Config, TELEMETRY_CONFIG,		null,				TelemetryConfig.class,	TELEMETRY_CONFIG_TOPIC.VALUE, "AstrobeeTelemetryConfigProfile");					
	public static MessageType TELEMETRY_STATE_TYPE			= new MessageTypeExtAstro(Category.Simple, TELEMETRY_STATE,			TELEMETRY_CONFIG,	TelemetryState.class,	TELEMETRY_STATE_TOPIC.VALUE, "AstrobeeTelemetryStateProfile");					

	protected MessageTypeExtAstro(Category category, String name, String configName, Class dataType, String topic, 
			String qosProfile) {

		super(category, name, configName, dataType, topic, qosProfile);
	}

	protected MessageTypeExtAstro(Category category, String name, String configName, Class dataType, String topic) {
		this(category, name, configName, dataType, topic, PREFIX+name+POSTFIX);
	}


}
