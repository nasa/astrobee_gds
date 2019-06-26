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

import gov.nasa.rapid.v2.e4.exception.MessageTypeExistsException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import rapid.ACCESSCONTROL_STATE_TOPIC;
import rapid.ACK_TOPIC;
import rapid.AGENT_CONFIG_TOPIC;
import rapid.AGENT_STATE_TOPIC;
import rapid.AccessControlState;
import rapid.Ack;
import rapid.AgentConfig;
import rapid.AgentState;
import rapid.COMMAND_CONFIG_TOPIC;
import rapid.COMMAND_TOPIC;
import rapid.Command;
import rapid.CommandConfig;
import rapid.FILEANNOUNCE_TOPIC;
import rapid.FILEQUEUE_CONFIG_TOPIC;
import rapid.FILEQUEUE_SAMPLE_TOPIC;
import rapid.FRAMESTORE_CONFIG_TOPIC;
import rapid.FileAnnounce;
import rapid.FileQueueConfig;
import rapid.FileQueueSample;
import rapid.FrameStoreConfig;
import rapid.IMAGESENSOR_SAMPLE_TOPIC;
import rapid.IMAGESENSOR_STATE_TOPIC;
import rapid.ImageSensorSample;
import rapid.ImageSensorState;
import rapid.JOINT_CONFIG_TOPIC;
import rapid.JOINT_SAMPLE_TOPIC;
import rapid.JointConfig;
import rapid.JointSample;
import rapid.MACRO_CONFIG_TOPIC;
import rapid.MACRO_STATE_TOPIC;
import rapid.MacroConfig;
import rapid.MacroState;
import rapid.POINTCLOUD_CONFIG_TOPIC;
import rapid.POINTCLOUD_SAMPLE_TOPIC;
import rapid.POSITION_CONFIG_TOPIC;
import rapid.POSITION_SAMPLE_TOPIC;
import rapid.PointCloudConfig;
import rapid.PointCloudSample;
import rapid.PositionConfig;
import rapid.PositionSample;
import rapid.QUEUE_STATE_TOPIC;
import rapid.QueueState;
import rapid.TEXTMESSAGE_TOPIC;
import rapid.TextMessage;

/**
 * MessageType encapsulates all information required to publish/subscribe data 
 * Implements an interface very similar to Enum
 */
@SuppressWarnings("unused")
public class MessageType implements Comparable<MessageType> {
    private static final Logger logger = Logger.getLogger(MessageType.class);
    private static final HashMap<String,MessageType> s_messageMap = new HashMap<String,MessageType>();

    /** topic specialization separator */
    public static final String topicSeparator = "-";
    public static final String tsep = topicSeparator;

    /**
     * Category of RAPID data type; non-RAPID datatypes are considered "Simple"
     * @author mallan
     *
     */
    public enum Category {
        Config,
        Sample, 
        State, 
        Simple
    }

    //-- the core RAPID types
    public static final String ACCESSCONTROL_STATE = "AccessControlState";
    public static final String ACK				   = "Ack";
    public static final String AGENT_CONFIG        = "AgentConfig";
    public static final String AGENT_STATE         = "AgentState";
    public static final String COMMAND             = "Command";
    public static final String COMMAND_CONFIG      = "CommandConfig";
    public static final String FRAMESTORE_CONFIG   = "FrameStoreConfig";
    public static final String FILEANNOUNCE        = "FileAnnounce";
    public static final String FILEQUEUE_SAMPLE    = "FileQueuSample";
    public static final String FILEQUEUE_CONFIG    = "FileQueueConfig";
    public static final String FILEQUEUEENTRY_STATE = "FileQueueEntryState";
    public static final String FILEQUEUERECEIVER_SAMPLE = "FileQueueReceiverSample";
    public static final String IMAGESENSOR_SAMPLE  = "ImageSensorSample";
    public static final String IMAGESENSOR_STATE   = "ImageSensorState";
    public static final String JOINT_CONFIG        = "JointConfig";
    public static final String JOINT_SAMPLE        = "JointSample";
    public static final String POINTCLOUD_CONFIG   = "PointCloudConfig";
    public static final String POINTCLOUD_SAMPLE   = "PointCloudSample";
    public static final String POSITION_CONFIG     = "PositionConfig";
    public static final String POSITION_SAMPLE     = "PositionSample";
    public static final String QUEUE_STATE		   = "QueueState";
    public static final String SUBSYSTEM_STATE     = "SubsystemState";
    public static final String TEXTMESSAGE         = "TextMessage";

    public static MessageType ACK_TYPE                 = new MessageType(Category.Simple, ACK,                 null, Ack.class,                ACK_TOPIC.VALUE      		        );
    public static MessageType TEXTMESSAGE_TYPE         = new MessageType(Category.Simple, TEXTMESSAGE,         null, TextMessage.class,        TEXTMESSAGE_TOPIC.VALUE         );

    public static MessageType AGENT_CONFIG_TYPE        = new MessageType(Category.Config, AGENT_CONFIG,        null, AgentConfig.class,        AGENT_CONFIG_TOPIC.VALUE        );
    public static MessageType COMMAND_CONFIG_TYPE      = new MessageType(Category.Config, COMMAND_CONFIG,      null, CommandConfig.class,      COMMAND_CONFIG_TOPIC.VALUE      );
    public static MessageType FRAMESTORE_CONFIG_TYPE   = new MessageType(Category.Config, FRAMESTORE_CONFIG,   null, FrameStoreConfig.class,   FRAMESTORE_CONFIG_TOPIC.VALUE   );
    public static MessageType JOINT_CONFIG_TYPE        = new MessageType(Category.Config, JOINT_CONFIG,        null, JointConfig.class,        JOINT_CONFIG_TOPIC.VALUE        );
    public static MessageType POINTCLOUD_CONFIG_TYPE   = new MessageType(Category.Config, POINTCLOUD_CONFIG,   null, PointCloudConfig.class,   POINTCLOUD_CONFIG_TOPIC.VALUE   );
    public static MessageType POSITION_CONFIG_TYPE     = new MessageType(Category.Config, POSITION_CONFIG,     null, PositionConfig.class,     POSITION_CONFIG_TOPIC.VALUE     );

    public static MessageType FILEANNOUNCE_TYPE        = new MessageType(Category.Simple, FILEANNOUNCE,        null, FileAnnounce.class,       FILEANNOUNCE_TOPIC.VALUE        );
    public static MessageType FILEQUEUE_CONFIG_TYPE    = new MessageType(Category.Config, FILEQUEUE_CONFIG,    null, FileQueueConfig.class,    FILEQUEUE_CONFIG_TOPIC.VALUE    );    

    public static MessageType ACCESSCONTROL_STATE_TYPE = new MessageType(Category.State,  ACCESSCONTROL_STATE, null,           AccessControlState.class, ACCESSCONTROL_STATE_TOPIC.VALUE );
    public static MessageType AGENT_STATE_TYPE         = new MessageType(Category.State,  AGENT_STATE,         AGENT_CONFIG,   AgentState.class,         AGENT_STATE_TOPIC.VALUE         );
    public static MessageType COMMAND_TYPE             = new MessageType(Category.State,  COMMAND,             COMMAND_CONFIG, Command.class,            COMMAND_TOPIC.VALUE             );
    public static MessageType IMAGESENSOR_STATE_TYPE   = new MessageType(Category.State,  IMAGESENSOR_STATE,   null,           ImageSensorState.class,   IMAGESENSOR_STATE_TOPIC.VALUE   );
    public static MessageType QUEUE_STATE_TYPE         = new MessageType(Category.State,  QUEUE_STATE,         null,           QueueState.class,         QUEUE_STATE_TOPIC.VALUE         );

    // XXX Our Sample-Config and State-Config pairs aren't very clean
    public static MessageType FILEQUEUE_SAMPLE_TYPE    = new MessageType(Category.Sample, FILEQUEUE_SAMPLE,    FILEQUEUE_CONFIG,  FileQueueSample.class,    FILEQUEUE_SAMPLE_TOPIC.VALUE    );
    public static MessageType IMAGESENSOR_SAMPLE_TYPE  = new MessageType(Category.Sample, IMAGESENSOR_SAMPLE,  null,              ImageSensorSample.class,  IMAGESENSOR_SAMPLE_TOPIC.VALUE  );
    public static MessageType JOINT_SAMPLE_TYPE        = new MessageType(Category.Sample, JOINT_SAMPLE,        JOINT_CONFIG,      JointSample.class,        JOINT_SAMPLE_TOPIC.VALUE        );
    public static MessageType POINTCLOUD_SAMPLE_TYPE   = new MessageType(Category.Sample, POINTCLOUD_SAMPLE,   POINTCLOUD_CONFIG, PointCloudSample.class,   POINTCLOUD_SAMPLE_TOPIC.VALUE   );
    public static MessageType POSITION_SAMPLE_TYPE     = new MessageType(Category.Sample, POSITION_SAMPLE,     POSITION_CONFIG,   PositionSample.class,     POSITION_SAMPLE_TOPIC.VALUE     );

    // Some extension types
    public static final String RELATIVE_POSITION_CONFIG     = "RelativePositionConfig";
    public static final String RELATIVE_POSITION_SAMPLE     = "RelativePositionSample";
    public static final String RELATIVE_TOPIC_APPEND        = "relative";
    public static MessageType  RELATIVE_POSITION_SAMPLE_TYPE = new MessageType(Category.Sample, RELATIVE_POSITION_SAMPLE, RELATIVE_POSITION_CONFIG, PositionSample.class, POSITION_SAMPLE_TOPIC.VALUE+topicSeparator+RELATIVE_TOPIC_APPEND, "RapidPositionSampleProfile");
    public static MessageType  RELATIVE_POSITION_CONFIG_TYPE = new MessageType(Category.Config, RELATIVE_POSITION_CONFIG,                     null, PositionConfig.class, POSITION_CONFIG_TOPIC.VALUE+topicSeparator+RELATIVE_TOPIC_APPEND, "RapidPositionConfigProfile" );

    public static final String DEBUG_POINTCLOUD_CONFIG   = "DebugPointCloudConfig";
    public static final String DEBUG_POINTCLOUD_SAMPLE   = "DebugPointCloudSample";
    public static final String DEBUG_POINTCLOUD_APPEND   = "debug";
    public static MessageType  DEBUG_POINTCLOUD_CONFIG_TYPE = new MessageType(Category.Config, DEBUG_POINTCLOUD_CONFIG,                      null, PointCloudConfig.class, POINTCLOUD_CONFIG_TOPIC.VALUE+topicSeparator+DEBUG_POINTCLOUD_APPEND, "RapidPointCloudConfigProfile");
    public static MessageType  DEBUG_POINTCLOUD_SAMPLE_TYPE = new MessageType(Category.Sample, DEBUG_POINTCLOUD_SAMPLE,   DEBUG_POINTCLOUD_CONFIG, PointCloudSample.class, POINTCLOUD_SAMPLE_TOPIC.VALUE+topicSeparator+DEBUG_POINTCLOUD_APPEND, "RapidPointCloudSampleProfile");

    public static final String MACRO_CONFIG             = "MacroConfig";
    public static MessageType  MACRO_CONFIG_TYPE        = new MessageType(Category.Config, MACRO_CONFIG,         null, MacroConfig.class, MACRO_CONFIG_TOPIC.VALUE);
    public static final String MACRO_STATE              = "MacroState";
    public static MessageType  MACRO_STATE_TYPE         = new MessageType(Category.State,   MACRO_STATE, MACRO_CONFIG,  MacroState.class, MACRO_STATE_TOPIC.VALUE);

    public static final String UPLOAD_MACRO_CONFIG      = "UploadMacroConfig";
    public static final String UPLOAD_MACRO_APPEND      = "upload";
    public static MessageType  UPLOAD_MACRO_CONFIG_TYPE = new MessageType(Category.Config, UPLOAD_MACRO_CONFIG, null, MacroConfig.class, MACRO_CONFIG_TOPIC.VALUE+topicSeparator+UPLOAD_MACRO_APPEND, "RapidMacroConfigProfile");

    //-- navigation camera joints
    public static final String NAVCAM_JOINT_CONFIG      = "NavCamJointConfig";
    public static final String NAVCAM_JOINT_SAMPLE      = "NavCamJointSample";
    public static final String NAVCAM_JOINT_APPEND      = "navcam";
    public static MessageType  NAVCAM_JOINT_CONFIG_TYPE = new MessageType(Category.Config, NAVCAM_JOINT_CONFIG,                null, JointConfig.class, JOINT_CONFIG_TOPIC.VALUE+topicSeparator+NAVCAM_JOINT_APPEND, "RapidJointConfigProfile" );
    public static MessageType  NAVCAM_JOINT_SAMPLE_TYPE = new MessageType(Category.Sample, NAVCAM_JOINT_SAMPLE, NAVCAM_JOINT_CONFIG, JointSample.class, JOINT_SAMPLE_TOPIC.VALUE+topicSeparator+NAVCAM_JOINT_APPEND, "RapidJointSampleProfile"  );
    //-- hazard camera joints
    public static final String HAZCAM_JOINT_CONFIG      = "HazCamJointConfig";
    public static final String HAZCAM_JOINT_SAMPLE      = "HazCamJointSample";
    public static final String HAZCAM_JOINT_APPEND      = "hazcam";
    public static MessageType  HAZCAM_JOINT_CONFIG_TYPE = new MessageType(Category.Config, HAZCAM_JOINT_CONFIG,                null, JointConfig.class, JOINT_CONFIG_TOPIC.VALUE+topicSeparator+HAZCAM_JOINT_APPEND, "RapidJointConfigProfile"  );
    public static MessageType  HAZCAM_JOINT_SAMPLE_TYPE = new MessageType(Category.Sample, HAZCAM_JOINT_SAMPLE, HAZCAM_JOINT_CONFIG, JointSample.class, JOINT_SAMPLE_TOPIC.VALUE+topicSeparator+HAZCAM_JOINT_APPEND, "RapidJointSampleProfile" );
    //-- panorama camera joints
    public static final String PANCAM_JOINT_CONFIG      = "PanCamJointConfig";
    public static final String PANCAM_JOINT_SAMPLE      = "PanCamJointSample";
    public static final String PANCAM_JOINT_APPEND      = "pancam";
    public static MessageType  PANCAM_JOINT_CONFIG_TYPE = new MessageType(Category.Config, PANCAM_JOINT_CONFIG,                null, JointConfig.class, JOINT_CONFIG_TOPIC.VALUE+topicSeparator+PANCAM_JOINT_APPEND, "RapidJointConfigProfile"  );
    public static MessageType  PANCAM_JOINT_SAMPLE_TYPE = new MessageType(Category.Sample, PANCAM_JOINT_SAMPLE, PANCAM_JOINT_CONFIG, JointSample.class, JOINT_SAMPLE_TOPIC.VALUE+topicSeparator+PANCAM_JOINT_APPEND, "RapidJointSampleProfile"  );

    protected final Category category;
    protected final String   name;
    protected final Class    dataType;
    protected final String   configName;
    protected String         topic;
    protected String         qosProfile;
    protected String         qosProfileFallback = null;

    private final  int       ordinal;
    private static int       s_ordinalCount = 0;

    public static final String PREFIX  = "Rapid"; 
    public static final String POSTFIX = "Profile"; 

    /**
     * Default RAPID message types are available, and custom types can be created simply 
     * by calling the MessageType constructor. The new type will be inserted into the 
     * type map automatically.
     * 
     * @param name       symbolic name of the MessageType
     * @param dataType   message payload data type
     * @param topic      pub/sub topic
     * @param qosProfile Qos profile used by type
     * @throws MessageTypeExistsException 
     */
    protected MessageType(Category category, 
                          String name, String configName, 
                          Class dataType, 
                          String topic, 
                          String qosProfile) throws MessageTypeExistsException {
        synchronized (MessageType.class) {
            MessageType existing = s_messageMap.get(name);
            if(existing != null) {
                String msg = "*********************************************"+
                        "\nERROR: MessageType "+name+" already exists. "+
                        "\nNew topic is \""+topic+"\", existing topic is \""+existing.getTopicName()+"\"."+
                        "\nIf you want to replace a type, you must remove the existing type first."+
                        "\n*********************************************";
                logger.error(msg);
                throw new MessageTypeExistsException(msg);
            }
            this.category   = category;
            this.configName = configName;
            this.name       = name;
            this.dataType   = dataType;
            this.topic      = topic;
            this.qosProfile = qosProfile;
            this.ordinal    = s_ordinalCount++;
            s_messageMap.put(name, this);
        }
        if(this.configName == null) {
            if(category == Category.Sample || category == Category.State) {
                //logger.debug(name+" has no matching Config type");
            }
        }
    }

    protected MessageType(Category category, 
                          String name, String configName, 
                          Class dataType, 
                          String topic) throws MessageTypeExistsException {
        this(category, name, configName, dataType, topic, PREFIX+name+POSTFIX);
    }

    /**
     * Method to add Simple and Config MessageTypes
     */
    public static MessageType add(Category category, 
                                  String name, String configName, 
                                  Class dataType, 
                                  String topic, 
                                  String qosProfile) throws MessageTypeExistsException {
        return new MessageType(category, name, configName, dataType, topic, qosProfile);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof String) {
            return name.equals(o);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /** 
     * analogous to Enum.ordinal()
     * May be used in switch statements
     */
    public int ordinal() {
        return ordinal;
    }

    /**
     * compare based on ordinal
     */
    public int compareTo(MessageType other) {
        return this.ordinal() - other.ordinal();
    }

    /**
     * @param name
     * @return MessageType corresponding to name if it exists. If name is null, return null
     * without throwing an exception
     * @throws IllegalArgumentException
     */
    public static MessageType valueOf(String name) throws IllegalArgumentException {
        if(name == null) 
            return null;
        MessageType retVal = s_messageMap.get(name);
        if(retVal == null) {
            throw new IllegalArgumentException("MessageType "+name+" does not exist");
        }
        return retVal;
    }

    public static boolean remove(String name) {
        MessageType exists = s_messageMap.remove(name);
        return (exists != null);
    }

    public static boolean contains(String name) {
        return s_messageMap.keySet().contains(name);
    }

    /**
     * @return a sorted list (by ordinal) of the MessageTypes
     */
    public static List<MessageType> values() {
        ArrayList<MessageType> retVal = new ArrayList<MessageType>(s_messageMap.values());
        Collections.sort(retVal);
        return retVal;
    }

    public String getConfigName() {
        return configName;
    }

    public String getTopicName() {
        return topic;
    }

    public void setTopicName(String topic) {
        this.topic = topic;
    }

    public String getQosProfile() {
        return qosProfile;
    }

    public void setQosProfile(String qosProfile) {
        this.qosProfile = qosProfile;
    }

    /**
     * Get the name of a QoS profile to use in case a fallback is necessary. 
     * Unless the fallback is explicitly set, it will be Rapid*Qos,
     * where * is one of Default, Sample, State or Config
     */
    public String getQosProfileFallback() {
        if(qosProfileFallback == null) {
            switch(this.category) {
            case Sample: return "RapidSampleQos";
            case State:  return "RapidStateQos";
            case Config: return "RapidConfigQos";
            case Simple: return "RapidDefaultQos";
            }
        }
        return qosProfileFallback;
    }

    public void setQosProfileFallback(String qosProfileName) {
        qosProfileFallback = qosProfileName;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return String.format("%s[%s profile=%s topic=%s]", name, dataType.getSimpleName(), qosProfile, topic);
    }

    public String name() {
        return name;
    }

    public Class getDataTypeClass() {
        return dataType;
    }

    public Object newDataTypeInstance() {
        try {
            return dataType.newInstance();
        } 
        catch (InstantiationException e) {
            logger.error(e);
        } 
        catch (IllegalAccessException e) {
            logger.error(e);
        }
        return null;
    }

    public static MessageType getTypeFromTopic(String topic) {
        for(MessageType type : MessageType.values()) {
            if(type.topic.equals(topic)) {
                return type;
            }
        }
        return null;
    }
}
