

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

public class GpsConfig  extends rapid.Message implements Copyable, Serializable{

    public String sensorFrame=  "" ; /* maximum length = (32) */
    public String referenceFrame=  "" ; /* maximum length = (32) */
    public String datum=  "" ; /* maximum length = (16) */
    public String undulationDatum=  "" ; /* maximum length = (16) */

    public GpsConfig() {

        super();

    }
    public GpsConfig (GpsConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GpsConfig self;
        self = new  GpsConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        sensorFrame=  ""; 
        referenceFrame=  ""; 
        datum=  ""; 
        undulationDatum=  ""; 
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

        GpsConfig otherObj = (GpsConfig)o;

        if(!sensorFrame.equals(otherObj.sensorFrame)) {
            return false;
        }
        if(!referenceFrame.equals(otherObj.referenceFrame)) {
            return false;
        }
        if(!datum.equals(otherObj.datum)) {
            return false;
        }
        if(!undulationDatum.equals(otherObj.undulationDatum)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += sensorFrame.hashCode(); 
        __result += referenceFrame.hashCode(); 
        __result += datum.hashCode(); 
        __result += undulationDatum.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GpsConfigTypeSupport</code>
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

        GpsConfig typedSrc = (GpsConfig) src;
        GpsConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.sensorFrame = typedSrc.sensorFrame;
        typedDst.referenceFrame = typedSrc.referenceFrame;
        typedDst.datum = typedSrc.datum;
        typedDst.undulationDatum = typedSrc.undulationDatum;

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
        strBuffer.append("sensorFrame: ").append(sensorFrame).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("referenceFrame: ").append(referenceFrame).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("datum: ").append(datum).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("undulationDatum: ").append(undulationDatum).append("\n");  

        return strBuffer.toString();
    }

}
