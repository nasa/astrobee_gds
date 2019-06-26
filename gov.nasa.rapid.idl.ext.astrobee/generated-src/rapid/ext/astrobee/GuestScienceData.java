

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
* Guest Science Data Type - used to express the type
* of data stored in the data octet
*/
/**
* Guest science data sent to the ground
*/

public class GuestScienceData  extends rapid.Message implements Copyable, Serializable{

    /** Full name of apk */
    public String apkName=  "" ; /* maximum length = (128) */
    /** Type of data being sent */
    public rapid.ext.astrobee.GuestScienceDataType type = (rapid.ext.astrobee.GuestScienceDataType)rapid.ext.astrobee.GuestScienceDataType.create();
    /** String to classify the kind of data */
    public String topic=  "" ; /* maximum length = (32) */
    /** Data from the apk */
    public rapid.OctetSequence2K data = (rapid.OctetSequence2K)rapid.OctetSequence2K.create();

    public GuestScienceData() {

        super();

        /** Full name of apk */
        /** Type of data being sent */
        /** String to classify the kind of data */
        /** Data from the apk */

    }
    public GuestScienceData (GuestScienceData other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GuestScienceData self;
        self = new  GuestScienceData();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Full name of apk */
        apkName=  ""; 
        /** Type of data being sent */
        type = rapid.ext.astrobee.GuestScienceDataType.create();
        /** String to classify the kind of data */
        topic=  ""; 
        /** Data from the apk */
        if (data != null) {
            data.clear();
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

        GuestScienceData otherObj = (GuestScienceData)o;

        /** Full name of apk */
        if(!apkName.equals(otherObj.apkName)) {
            return false;
        }
        /** Type of data being sent */
        if(!type.equals(otherObj.type)) {
            return false;
        }
        /** String to classify the kind of data */
        if(!topic.equals(otherObj.topic)) {
            return false;
        }
        /** Data from the apk */
        if(!data.equals(otherObj.data)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Full name of apk */
        __result += apkName.hashCode(); 
        /** Type of data being sent */
        __result += type.hashCode(); 
        /** String to classify the kind of data */
        __result += topic.hashCode(); 
        /** Data from the apk */
        __result += data.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GuestScienceDataTypeSupport</code>
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

        GuestScienceData typedSrc = (GuestScienceData) src;
        GuestScienceData typedDst = this;
        super.copy_from(typedSrc);
        /** Full name of apk */
        typedDst.apkName = typedSrc.apkName;
        /** Type of data being sent */
        typedDst.type = (rapid.ext.astrobee.GuestScienceDataType) typedDst.type.copy_from(typedSrc.type);
        /** String to classify the kind of data */
        typedDst.topic = typedSrc.topic;
        /** Data from the apk */
        typedDst.data = (rapid.OctetSequence2K) typedDst.data.copy_from(typedSrc.data);

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

        /** Full name of apk */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("apkName: ").append(apkName).append("\n");  
        /** Type of data being sent */
        strBuffer.append(type.toString("type ", indent+1));
        /** String to classify the kind of data */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("topic: ").append(topic).append("\n");  
        /** Data from the apk */
        strBuffer.append(data.toString("data ", indent+1));

        return strBuffer.toString();
    }

}
