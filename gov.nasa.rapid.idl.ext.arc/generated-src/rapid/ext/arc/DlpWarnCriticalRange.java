

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class DlpWarnCriticalRange   implements Copyable, Serializable{

    public float lowCritical= 0;
    public float lowWarn= 0;
    public float highWarn= 0;
    public float highCritical= 0;

    public DlpWarnCriticalRange() {

    }
    public DlpWarnCriticalRange (DlpWarnCriticalRange other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        DlpWarnCriticalRange self;
        self = new  DlpWarnCriticalRange();
        self.clear();
        return self;

    }

    public void clear() {

        lowCritical= 0;
        lowWarn= 0;
        highWarn= 0;
        highCritical= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        DlpWarnCriticalRange otherObj = (DlpWarnCriticalRange)o;

        if(lowCritical != otherObj.lowCritical) {
            return false;
        }
        if(lowWarn != otherObj.lowWarn) {
            return false;
        }
        if(highWarn != otherObj.highWarn) {
            return false;
        }
        if(highCritical != otherObj.highCritical) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)lowCritical;
        __result += (int)lowWarn;
        __result += (int)highWarn;
        __result += (int)highCritical;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>DlpWarnCriticalRangeTypeSupport</code>
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

        DlpWarnCriticalRange typedSrc = (DlpWarnCriticalRange) src;
        DlpWarnCriticalRange typedDst = this;

        typedDst.lowCritical = typedSrc.lowCritical;
        typedDst.lowWarn = typedSrc.lowWarn;
        typedDst.highWarn = typedSrc.highWarn;
        typedDst.highCritical = typedSrc.highCritical;

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
        strBuffer.append("lowCritical: ").append(lowCritical).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("lowWarn: ").append(lowWarn).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("highWarn: ").append(highWarn).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("highCritical: ").append(highCritical).append("\n");  

        return strBuffer.toString();
    }

}
