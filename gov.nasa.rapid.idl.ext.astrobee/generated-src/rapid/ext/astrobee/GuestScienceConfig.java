

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
* State message for guest science apk
*/

public class GuestScienceConfig  extends rapid.Message implements Copyable, Serializable{

    /** List of apk states */
    public rapid.ext.astrobee.GuestScienceApkSequence32 apkStates = (rapid.ext.astrobee.GuestScienceApkSequence32)rapid.ext.astrobee.GuestScienceApkSequence32.create();

    public GuestScienceConfig() {

        super();

        /** List of apk states */

    }
    public GuestScienceConfig (GuestScienceConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GuestScienceConfig self;
        self = new  GuestScienceConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** List of apk states */
        if (apkStates != null) {
            apkStates.clear();
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

        GuestScienceConfig otherObj = (GuestScienceConfig)o;

        /** List of apk states */
        if(!apkStates.equals(otherObj.apkStates)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** List of apk states */
        __result += apkStates.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GuestScienceConfigTypeSupport</code>
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

        GuestScienceConfig typedSrc = (GuestScienceConfig) src;
        GuestScienceConfig typedDst = this;
        super.copy_from(typedSrc);
        /** List of apk states */
        typedDst.apkStates = (rapid.ext.astrobee.GuestScienceApkSequence32) typedDst.apkStates.copy_from(typedSrc.apkStates);

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

        /** List of apk states */
        strBuffer.append(apkStates.toString("apkStates ", indent+1));

        return strBuffer.toString();
    }

}
