

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* DownlinkOption indicates if and when the data in this rostopic is downlinked
* <ul>
*   <li>NONE: topic is not saved to disk
*   <li>IMMEDIATE: topic saved to disk; upon docking it is downlinked
*   <li>DELAYED: topic saved to disk; upon docking it is transferred to ISS server for later downlink
* </ul>
*/
/** Whether and how to save data from a rostopic
* - topicName: Name of rostopic
* - downlinkOption: when and if the topic should be downlinked
* - frequency: times per second to save the data (Hz)
*/
/**
* Save and downlink settings for all available rostopics
*/

public class DataToDiskState  extends rapid.Message implements Copyable, Serializable{

    public String name=  "" ; /* maximum length = (32) */
    public boolean recording= false;
    public rapid.ext.astrobee.SaveSettingSequence64 topicSaveSettings = (rapid.ext.astrobee.SaveSettingSequence64)rapid.ext.astrobee.SaveSettingSequence64.create();

    public DataToDiskState() {

        super();

    }
    public DataToDiskState (DataToDiskState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        DataToDiskState self;
        self = new  DataToDiskState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        name=  ""; 
        recording= false;
        if (topicSaveSettings != null) {
            topicSaveSettings.clear();
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

        DataToDiskState otherObj = (DataToDiskState)o;

        if(!name.equals(otherObj.name)) {
            return false;
        }
        if(recording != otherObj.recording) {
            return false;
        }
        if(!topicSaveSettings.equals(otherObj.topicSaveSettings)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += name.hashCode(); 
        __result += (recording == true)?1:0;
        __result += topicSaveSettings.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>DataToDiskStateTypeSupport</code>
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

        DataToDiskState typedSrc = (DataToDiskState) src;
        DataToDiskState typedDst = this;
        super.copy_from(typedSrc);
        typedDst.name = typedSrc.name;
        typedDst.recording = typedSrc.recording;
        typedDst.topicSaveSettings = (rapid.ext.astrobee.SaveSettingSequence64) typedDst.topicSaveSettings.copy_from(typedSrc.topicSaveSettings);

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

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("recording: ").append(recording).append("\n");  
        strBuffer.append(topicSaveSettings.toString("topicSaveSettings ", indent+1));

        return strBuffer.toString();
    }

}
