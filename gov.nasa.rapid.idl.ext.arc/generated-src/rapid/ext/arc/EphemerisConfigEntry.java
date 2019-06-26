

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

public class EphemerisConfigEntry   implements Copyable, Serializable{

    public String solutionName=  "" ; /* maximum length = (255) */
    public String referenceFrame=  "" ; /* maximum length = (255) */
    public String sourceBody=  "" ; /* maximum length = (255) */
    public String targetBody=  "" ; /* maximum length = (255) */
    public long timeOffset= 0;
    public long period= 0;

    public EphemerisConfigEntry() {

    }
    public EphemerisConfigEntry (EphemerisConfigEntry other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        EphemerisConfigEntry self;
        self = new  EphemerisConfigEntry();
        self.clear();
        return self;

    }

    public void clear() {

        solutionName=  ""; 
        referenceFrame=  ""; 
        sourceBody=  ""; 
        targetBody=  ""; 
        timeOffset= 0;
        period= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        EphemerisConfigEntry otherObj = (EphemerisConfigEntry)o;

        if(!solutionName.equals(otherObj.solutionName)) {
            return false;
        }
        if(!referenceFrame.equals(otherObj.referenceFrame)) {
            return false;
        }
        if(!sourceBody.equals(otherObj.sourceBody)) {
            return false;
        }
        if(!targetBody.equals(otherObj.targetBody)) {
            return false;
        }
        if(timeOffset != otherObj.timeOffset) {
            return false;
        }
        if(period != otherObj.period) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += solutionName.hashCode(); 
        __result += referenceFrame.hashCode(); 
        __result += sourceBody.hashCode(); 
        __result += targetBody.hashCode(); 
        __result += (int)timeOffset;
        __result += (int)period;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>EphemerisConfigEntryTypeSupport</code>
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

        EphemerisConfigEntry typedSrc = (EphemerisConfigEntry) src;
        EphemerisConfigEntry typedDst = this;

        typedDst.solutionName = typedSrc.solutionName;
        typedDst.referenceFrame = typedSrc.referenceFrame;
        typedDst.sourceBody = typedSrc.sourceBody;
        typedDst.targetBody = typedSrc.targetBody;
        typedDst.timeOffset = typedSrc.timeOffset;
        typedDst.period = typedSrc.period;

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
        strBuffer.append("solutionName: ").append(solutionName).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("referenceFrame: ").append(referenceFrame).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("sourceBody: ").append(sourceBody).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetBody: ").append(targetBody).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("timeOffset: ").append(timeOffset).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("period: ").append(period).append("\n");  

        return strBuffer.toString();
    }

}
