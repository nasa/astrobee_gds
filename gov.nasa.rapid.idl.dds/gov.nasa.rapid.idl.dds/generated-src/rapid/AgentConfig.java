

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* AgentConfig
*/

public class AgentConfig  extends rapid.Message implements Copyable, Serializable{

    /**
    * tags that describe the Agent
    */
    public rapid.String16Sequence16 agentTags = (rapid.String16Sequence16)rapid.String16Sequence16.create();
    /**
    * List of topic names for messages from Agents that have multiple of a message type.
    * The convention for topic naming is the base topic name, appended by a dash ("-")
    * and a descriptive lower-case string. For example, rapid_position_sample-right_end_effector
    */
    public rapid.String128Sequence64 topics = (rapid.String128Sequence64)rapid.String128Sequence64.create();
    /**
    * Agent-specific state information can be stored in this extras sequence. The Name and Type
    * information is stored in the KeyTypeSequence, and the AgentState message provides the values. The
    * key denotes the name of a piece of information, the type is the DataType of the information, and the
    * Value is the current value of the information upon start-up. This sequence has a counterpart in
    * AgentState, named 'values'.
    *
    * For example,
    *
    * <code>
    *   AgentConfig.valueKeys[0] = 'fooBar', BOOLEAN<br>
    *   AgentConfig.valueKeys[1] = 'driveFace', INT<br>
    *   AgentConfig.valueKeys[2] = 'fluxCapacitance', FLOAT<br>
    * </code>
    *
    * Updates are published as:
    *
    * <code>
    *   AgentState.values[0] = 'false'<br>
    *   AgentState.values[1] = '2'<br>
    *   AgentState.values[2] = '3.14159'<br>
    * </code>
    *
    * The value in the Sequence is the default value for that key.
    */
    public rapid.KeyTypeValueSequence64 valueKeys = (rapid.KeyTypeValueSequence64)rapid.KeyTypeValueSequence64.create();

    public AgentConfig() {

        super();

        /**
        * tags that describe the Agent
        */
        /**
        * List of topic names for messages from Agents that have multiple of a message type.
        * The convention for topic naming is the base topic name, appended by a dash ("-")
        * and a descriptive lower-case string. For example, rapid_position_sample-right_end_effector
        */
        /**
        * Agent-specific state information can be stored in this extras sequence. The Name and Type
        * information is stored in the KeyTypeSequence, and the AgentState message provides the values. The
        * key denotes the name of a piece of information, the type is the DataType of the information, and the
        * Value is the current value of the information upon start-up. This sequence has a counterpart in
        * AgentState, named 'values'.
        *
        * For example,
        *
        * <code>
        *   AgentConfig.valueKeys[0] = 'fooBar', BOOLEAN<br>
        *   AgentConfig.valueKeys[1] = 'driveFace', INT<br>
        *   AgentConfig.valueKeys[2] = 'fluxCapacitance', FLOAT<br>
        * </code>
        *
        * Updates are published as:
        *
        * <code>
        *   AgentState.values[0] = 'false'<br>
        *   AgentState.values[1] = '2'<br>
        *   AgentState.values[2] = '3.14159'<br>
        * </code>
        *
        * The value in the Sequence is the default value for that key.
        */

    }
    public AgentConfig (AgentConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        AgentConfig self;
        self = new  AgentConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /**
        * tags that describe the Agent
        */
        if (agentTags != null) {
            agentTags.clear();
        }
        /**
        * List of topic names for messages from Agents that have multiple of a message type.
        * The convention for topic naming is the base topic name, appended by a dash ("-")
        * and a descriptive lower-case string. For example, rapid_position_sample-right_end_effector
        */
        if (topics != null) {
            topics.clear();
        }
        /**
        * Agent-specific state information can be stored in this extras sequence. The Name and Type
        * information is stored in the KeyTypeSequence, and the AgentState message provides the values. The
        * key denotes the name of a piece of information, the type is the DataType of the information, and the
        * Value is the current value of the information upon start-up. This sequence has a counterpart in
        * AgentState, named 'values'.
        *
        * For example,
        *
        * <code>
        *   AgentConfig.valueKeys[0] = 'fooBar', BOOLEAN<br>
        *   AgentConfig.valueKeys[1] = 'driveFace', INT<br>
        *   AgentConfig.valueKeys[2] = 'fluxCapacitance', FLOAT<br>
        * </code>
        *
        * Updates are published as:
        *
        * <code>
        *   AgentState.values[0] = 'false'<br>
        *   AgentState.values[1] = '2'<br>
        *   AgentState.values[2] = '3.14159'<br>
        * </code>
        *
        * The value in the Sequence is the default value for that key.
        */
        if (valueKeys != null) {
            valueKeys.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if (!super.equals(o)) {
            return false;
        }

        if(getClass() != o.getClass()) {
            return false;
        }

        AgentConfig otherObj = (AgentConfig)o;

        /**
        * tags that describe the Agent
        */
        if(!agentTags.equals(otherObj.agentTags)) {
            return false;
        }
        /**
        * List of topic names for messages from Agents that have multiple of a message type.
        * The convention for topic naming is the base topic name, appended by a dash ("-")
        * and a descriptive lower-case string. For example, rapid_position_sample-right_end_effector
        */
        if(!topics.equals(otherObj.topics)) {
            return false;
        }
        /**
        * Agent-specific state information can be stored in this extras sequence. The Name and Type
        * information is stored in the KeyTypeSequence, and the AgentState message provides the values. The
        * key denotes the name of a piece of information, the type is the DataType of the information, and the
        * Value is the current value of the information upon start-up. This sequence has a counterpart in
        * AgentState, named 'values'.
        *
        * For example,
        *
        * <code>
        *   AgentConfig.valueKeys[0] = 'fooBar', BOOLEAN<br>
        *   AgentConfig.valueKeys[1] = 'driveFace', INT<br>
        *   AgentConfig.valueKeys[2] = 'fluxCapacitance', FLOAT<br>
        * </code>
        *
        * Updates are published as:
        *
        * <code>
        *   AgentState.values[0] = 'false'<br>
        *   AgentState.values[1] = '2'<br>
        *   AgentState.values[2] = '3.14159'<br>
        * </code>
        *
        * The value in the Sequence is the default value for that key.
        */
        if(!valueKeys.equals(otherObj.valueKeys)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /**
        * tags that describe the Agent
        */
        __result += agentTags.hashCode(); 
        /**
        * List of topic names for messages from Agents that have multiple of a message type.
        * The convention for topic naming is the base topic name, appended by a dash ("-")
        * and a descriptive lower-case string. For example, rapid_position_sample-right_end_effector
        */
        __result += topics.hashCode(); 
        /**
        * Agent-specific state information can be stored in this extras sequence. The Name and Type
        * information is stored in the KeyTypeSequence, and the AgentState message provides the values. The
        * key denotes the name of a piece of information, the type is the DataType of the information, and the
        * Value is the current value of the information upon start-up. This sequence has a counterpart in
        * AgentState, named 'values'.
        *
        * For example,
        *
        * <code>
        *   AgentConfig.valueKeys[0] = 'fooBar', BOOLEAN<br>
        *   AgentConfig.valueKeys[1] = 'driveFace', INT<br>
        *   AgentConfig.valueKeys[2] = 'fluxCapacitance', FLOAT<br>
        * </code>
        *
        * Updates are published as:
        *
        * <code>
        *   AgentState.values[0] = 'false'<br>
        *   AgentState.values[1] = '2'<br>
        *   AgentState.values[2] = '3.14159'<br>
        * </code>
        *
        * The value in the Sequence is the default value for that key.
        */
        __result += valueKeys.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>AgentConfigTypeSupport</code>
    * rather than here by using the <code>-noCopyable</code> option
    * to rtiddsgen.
    * 
    * @param src The Object which contains the data to be copied.
    * @return Returns <code>this</code>.
    * @exception NullPointerException If <code>src</code> is null.
    * @exception ClassCastException If <code>src</code> is not the 
    * same type as <code>this</code>.
    * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
    */
    public Object copy_from(Object src) {

        AgentConfig typedSrc = (AgentConfig) src;
        AgentConfig typedDst = this;
        super.copy_from(typedSrc);
        /**
        * tags that describe the Agent
        */
        typedDst.agentTags = (rapid.String16Sequence16) typedDst.agentTags.copy_from(typedSrc.agentTags);
        /**
        * List of topic names for messages from Agents that have multiple of a message type.
        * The convention for topic naming is the base topic name, appended by a dash ("-")
        * and a descriptive lower-case string. For example, rapid_position_sample-right_end_effector
        */
        typedDst.topics = (rapid.String128Sequence64) typedDst.topics.copy_from(typedSrc.topics);
        /**
        * Agent-specific state information can be stored in this extras sequence. The Name and Type
        * information is stored in the KeyTypeSequence, and the AgentState message provides the values. The
        * key denotes the name of a piece of information, the type is the DataType of the information, and the
        * Value is the current value of the information upon start-up. This sequence has a counterpart in
        * AgentState, named 'values'.
        *
        * For example,
        *
        * <code>
        *   AgentConfig.valueKeys[0] = 'fooBar', BOOLEAN<br>
        *   AgentConfig.valueKeys[1] = 'driveFace', INT<br>
        *   AgentConfig.valueKeys[2] = 'fluxCapacitance', FLOAT<br>
        * </code>
        *
        * Updates are published as:
        *
        * <code>
        *   AgentState.values[0] = 'false'<br>
        *   AgentState.values[1] = '2'<br>
        *   AgentState.values[2] = '3.14159'<br>
        * </code>
        *
        * The value in the Sequence is the default value for that key.
        */
        typedDst.valueKeys = (rapid.KeyTypeValueSequence64) typedDst.valueKeys.copy_from(typedSrc.valueKeys);

        return this;
    }

    public String toString(){
        return toString("", 0);
    }

    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        

        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }

        strBuffer.append(super.toString("",indent));

        /**
        * tags that describe the Agent
        */
        strBuffer.append(agentTags.toString("agentTags ", indent+1));
        /**
        * List of topic names for messages from Agents that have multiple of a message type.
        * The convention for topic naming is the base topic name, appended by a dash ("-")
        * and a descriptive lower-case string. For example, rapid_position_sample-right_end_effector
        */
        strBuffer.append(topics.toString("topics ", indent+1));
        /**
        * Agent-specific state information can be stored in this extras sequence. The Name and Type
        * information is stored in the KeyTypeSequence, and the AgentState message provides the values. The
        * key denotes the name of a piece of information, the type is the DataType of the information, and the
        * Value is the current value of the information upon start-up. This sequence has a counterpart in
        * AgentState, named 'values'.
        *
        * For example,
        *
        * <code>
        *   AgentConfig.valueKeys[0] = 'fooBar', BOOLEAN<br>
        *   AgentConfig.valueKeys[1] = 'driveFace', INT<br>
        *   AgentConfig.valueKeys[2] = 'fluxCapacitance', FLOAT<br>
        * </code>
        *
        * Updates are published as:
        *
        * <code>
        *   AgentState.values[0] = 'false'<br>
        *   AgentState.values[1] = '2'<br>
        *   AgentState.values[2] = '3.14159'<br>
        * </code>
        *
        * The value in the Sequence is the default value for that key.
        */
        strBuffer.append(valueKeys.toString("valueKeys ", indent+1));

        return strBuffer.toString();
    }

}
