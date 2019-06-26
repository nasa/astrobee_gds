

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

public class SaveSetting   implements Copyable, Serializable{

    public String topicName=  "" ; /* maximum length = (128) */
    public rapid.ext.astrobee.DownlinkOption downlinkOption = (rapid.ext.astrobee.DownlinkOption)rapid.ext.astrobee.DownlinkOption.create();
    public float frequency= 0;

    public SaveSetting() {

    }
    public SaveSetting (SaveSetting other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        SaveSetting self;
        self = new  SaveSetting();
        self.clear();
        return self;

    }

    public void clear() {

        topicName=  ""; 
        downlinkOption = rapid.ext.astrobee.DownlinkOption.create();
        frequency= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        SaveSetting otherObj = (SaveSetting)o;

        if(!topicName.equals(otherObj.topicName)) {
            return false;
        }
        if(!downlinkOption.equals(otherObj.downlinkOption)) {
            return false;
        }
        if(frequency != otherObj.frequency) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += topicName.hashCode(); 
        __result += downlinkOption.hashCode(); 
        __result += (int)frequency;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>SaveSettingTypeSupport</code>
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

        SaveSetting typedSrc = (SaveSetting) src;
        SaveSetting typedDst = this;

        typedDst.topicName = typedSrc.topicName;
        typedDst.downlinkOption = (rapid.ext.astrobee.DownlinkOption) typedDst.downlinkOption.copy_from(typedSrc.downlinkOption);
        typedDst.frequency = typedSrc.frequency;

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

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("topicName: ").append(topicName).append("\n");  
        strBuffer.append(downlinkOption.toString("downlinkOption ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("frequency: ").append(frequency).append("\n");  

        return strBuffer.toString();
    }

}
