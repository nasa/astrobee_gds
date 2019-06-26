

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

public class HydraSample  extends rapid.Message implements Copyable, Serializable{

    public short soh= 0;
    public short sns= 0;
    public short cds= 0;
    public short cmr= 0;
    public ShortSeq sn =  new ShortSeq(32);
    public ShortSeq cd =  new ShortSeq(32);
    public ShortSeq reading =  new ShortSeq(89);
    public rapid.ValueSequence32 values = (rapid.ValueSequence32)rapid.ValueSequence32.create();

    public HydraSample() {

        super();

    }
    public HydraSample (HydraSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        HydraSample self;
        self = new  HydraSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        soh= 0;
        sns= 0;
        cds= 0;
        cmr= 0;
        if (sn != null) {
            sn.clear();
        }
        if (cd != null) {
            cd.clear();
        }
        if (reading != null) {
            reading.clear();
        }
        if (values != null) {
            values.clear();
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

        HydraSample otherObj = (HydraSample)o;

        if(soh != otherObj.soh) {
            return false;
        }
        if(sns != otherObj.sns) {
            return false;
        }
        if(cds != otherObj.cds) {
            return false;
        }
        if(cmr != otherObj.cmr) {
            return false;
        }
        if(!sn.equals(otherObj.sn)) {
            return false;
        }
        if(!cd.equals(otherObj.cd)) {
            return false;
        }
        if(!reading.equals(otherObj.reading)) {
            return false;
        }
        if(!values.equals(otherObj.values)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += (int)soh;
        __result += (int)sns;
        __result += (int)cds;
        __result += (int)cmr;
        __result += sn.hashCode(); 
        __result += cd.hashCode(); 
        __result += reading.hashCode(); 
        __result += values.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>HydraSampleTypeSupport</code>
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

        HydraSample typedSrc = (HydraSample) src;
        HydraSample typedDst = this;
        super.copy_from(typedSrc);
        typedDst.soh = typedSrc.soh;
        typedDst.sns = typedSrc.sns;
        typedDst.cds = typedSrc.cds;
        typedDst.cmr = typedSrc.cmr;
        typedDst.sn.copy_from(typedSrc.sn);
        typedDst.cd.copy_from(typedSrc.cd);
        typedDst.reading.copy_from(typedSrc.reading);
        typedDst.values = (rapid.ValueSequence32) typedDst.values.copy_from(typedSrc.values);

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
        strBuffer.append("soh: ").append(soh).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("sns: ").append(sns).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("cds: ").append(cds).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("cmr: ").append(cmr).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("sn: ");
        for(int i__ = 0; i__ < sn.size(); ++i__) {
            if (i__!=0) strBuffer.append(", ");
            strBuffer.append(sn.get(i__));
        }
        strBuffer.append("\n"); 
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("cd: ");
        for(int i__ = 0; i__ < cd.size(); ++i__) {
            if (i__!=0) strBuffer.append(", ");
            strBuffer.append(cd.get(i__));
        }
        strBuffer.append("\n"); 
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("reading: ");
        for(int i__ = 0; i__ < reading.size(); ++i__) {
            if (i__!=0) strBuffer.append(", ");
            strBuffer.append(reading.get(i__));
        }
        strBuffer.append("\n"); 
        strBuffer.append(values.toString("values ", indent+1));

        return strBuffer.toString();
    }

}
