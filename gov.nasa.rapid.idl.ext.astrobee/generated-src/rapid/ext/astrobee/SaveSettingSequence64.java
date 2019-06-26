

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

public class SaveSettingSequence64   implements Copyable, Serializable{

    public rapid.ext.astrobee.SaveSettingSeq userData =  new rapid.ext.astrobee.SaveSettingSeq(64);

    public SaveSettingSequence64() {

    }
    public SaveSettingSequence64 (SaveSettingSequence64 other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        SaveSettingSequence64 self;
        self = new  SaveSettingSequence64();
        self.clear();
        return self;

    }

    public void clear() {

        if (userData != null) {
            userData.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        SaveSettingSequence64 otherObj = (SaveSettingSequence64)o;

        if(!userData.equals(otherObj.userData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += userData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>SaveSettingSequence64TypeSupport</code>
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

        SaveSettingSequence64 typedSrc = (SaveSettingSequence64) src;
        SaveSettingSequence64 typedDst = this;

        typedDst.userData.copy_from(typedSrc.userData);

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
        strBuffer.append("userData:\n");
        for(int i__ = 0; i__ < userData.size(); ++i__) {
            strBuffer.append(((rapid.ext.astrobee.SaveSetting)userData.get(i__)).toString(Integer.toString(i__),indent+2));
        }

        return strBuffer.toString();
    }

}
