

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class FilesystemInfoConfig   implements Copyable, Serializable{

    public long capacity= 0;
    public String name=  "" ; /* maximum length = (64) */
    public long availableLow= 0;
    public long availableCritical= 0;

    public FilesystemInfoConfig() {

    }
    public FilesystemInfoConfig (FilesystemInfoConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FilesystemInfoConfig self;
        self = new  FilesystemInfoConfig();
        self.clear();
        return self;

    }

    public void clear() {

        capacity= 0;
        name=  ""; 
        availableLow= 0;
        availableCritical= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        FilesystemInfoConfig otherObj = (FilesystemInfoConfig)o;

        if(capacity != otherObj.capacity) {
            return false;
        }
        if(!name.equals(otherObj.name)) {
            return false;
        }
        if(availableLow != otherObj.availableLow) {
            return false;
        }
        if(availableCritical != otherObj.availableCritical) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)capacity;
        __result += name.hashCode(); 
        __result += (int)availableLow;
        __result += (int)availableCritical;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FilesystemInfoConfigTypeSupport</code>
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

        FilesystemInfoConfig typedSrc = (FilesystemInfoConfig) src;
        FilesystemInfoConfig typedDst = this;

        typedDst.capacity = typedSrc.capacity;
        typedDst.name = typedSrc.name;
        typedDst.availableLow = typedSrc.availableLow;
        typedDst.availableCritical = typedSrc.availableCritical;

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
        strBuffer.append("capacity: ").append(capacity).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("availableLow: ").append(availableLow).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("availableCritical: ").append(availableCritical).append("\n");  

        return strBuffer.toString();
    }

}
