

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

/** The configuation of a disk
* - name: displayable name for the filesystem
* - capacity: total capacity in bytes of the disk
*/
/**
* Config for the DiskState message.
*/

public class DiskConfig  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.astrobee.DiskInfoConfigSequence8 filesystems = (rapid.ext.astrobee.DiskInfoConfigSequence8)rapid.ext.astrobee.DiskInfoConfigSequence8.create();

    public DiskConfig() {

        super();

    }
    public DiskConfig (DiskConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        DiskConfig self;
        self = new  DiskConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (filesystems != null) {
            filesystems.clear();
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

        DiskConfig otherObj = (DiskConfig)o;

        if(!filesystems.equals(otherObj.filesystems)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += filesystems.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>DiskConfigTypeSupport</code>
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

        DiskConfig typedSrc = (DiskConfig) src;
        DiskConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.filesystems = (rapid.ext.astrobee.DiskInfoConfigSequence8) typedDst.filesystems.copy_from(typedSrc.filesystems);

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

        strBuffer.append(filesystems.toString("filesystems ", indent+1));

        return strBuffer.toString();
    }

}
