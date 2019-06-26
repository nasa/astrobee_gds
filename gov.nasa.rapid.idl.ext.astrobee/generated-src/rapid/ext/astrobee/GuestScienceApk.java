

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

public class GuestScienceApk   implements Copyable, Serializable{

    /** Full apk name */
    public String apkName=  "" ; /* maximum length = (128) */
    /** Short name of apk */
    public String shortName=  "" ; /* maximum length = (32) */
    /** Whether the apk is primary or secondary */
    public boolean primary= false;
    /** A list of commands the apk will accept */
    public rapid.ext.astrobee.GuestScienceCommandSequence32 commands = (rapid.ext.astrobee.GuestScienceCommandSequence32)rapid.ext.astrobee.GuestScienceCommandSequence32.create();

    public GuestScienceApk() {

        /** Full apk name */
        /** Short name of apk */
        /** Whether the apk is primary or secondary */
        /** A list of commands the apk will accept */

    }
    public GuestScienceApk (GuestScienceApk other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GuestScienceApk self;
        self = new  GuestScienceApk();
        self.clear();
        return self;

    }

    public void clear() {

        /** Full apk name */
        apkName=  ""; 
        /** Short name of apk */
        shortName=  ""; 
        /** Whether the apk is primary or secondary */
        primary= false;
        /** A list of commands the apk will accept */
        if (commands != null) {
            commands.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        GuestScienceApk otherObj = (GuestScienceApk)o;

        /** Full apk name */
        if(!apkName.equals(otherObj.apkName)) {
            return false;
        }
        /** Short name of apk */
        if(!shortName.equals(otherObj.shortName)) {
            return false;
        }
        /** Whether the apk is primary or secondary */
        if(primary != otherObj.primary) {
            return false;
        }
        /** A list of commands the apk will accept */
        if(!commands.equals(otherObj.commands)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Full apk name */
        __result += apkName.hashCode(); 
        /** Short name of apk */
        __result += shortName.hashCode(); 
        /** Whether the apk is primary or secondary */
        __result += (primary == true)?1:0;
        /** A list of commands the apk will accept */
        __result += commands.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GuestScienceApkTypeSupport</code>
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

        GuestScienceApk typedSrc = (GuestScienceApk) src;
        GuestScienceApk typedDst = this;

        /** Full apk name */
        typedDst.apkName = typedSrc.apkName;
        /** Short name of apk */
        typedDst.shortName = typedSrc.shortName;
        /** Whether the apk is primary or secondary */
        typedDst.primary = typedSrc.primary;
        /** A list of commands the apk will accept */
        typedDst.commands = (rapid.ext.astrobee.GuestScienceCommandSequence32) typedDst.commands.copy_from(typedSrc.commands);

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

        /** Full apk name */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("apkName: ").append(apkName).append("\n");  
        /** Short name of apk */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("shortName: ").append(shortName).append("\n");  
        /** Whether the apk is primary or secondary */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("primary: ").append(primary).append("\n");  
        /** A list of commands the apk will accept */
        strBuffer.append(commands.toString("commands ", indent+1));

        return strBuffer.toString();
    }

}
