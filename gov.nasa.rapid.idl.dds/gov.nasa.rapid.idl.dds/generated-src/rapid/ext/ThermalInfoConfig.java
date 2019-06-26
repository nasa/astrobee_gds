

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

public class ThermalInfoConfig   implements Copyable, Serializable{

    public String location=  "" ; /* maximum length = (32) */
    public float temperatureLow= 0;
    public float temperatureHigh= 0;
    public float temperatureLowCritical= 0;
    public float temperatureHighCritical= 0;

    public ThermalInfoConfig() {

    }
    public ThermalInfoConfig (ThermalInfoConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ThermalInfoConfig self;
        self = new  ThermalInfoConfig();
        self.clear();
        return self;

    }

    public void clear() {

        location=  ""; 
        temperatureLow= 0;
        temperatureHigh= 0;
        temperatureLowCritical= 0;
        temperatureHighCritical= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        ThermalInfoConfig otherObj = (ThermalInfoConfig)o;

        if(!location.equals(otherObj.location)) {
            return false;
        }
        if(temperatureLow != otherObj.temperatureLow) {
            return false;
        }
        if(temperatureHigh != otherObj.temperatureHigh) {
            return false;
        }
        if(temperatureLowCritical != otherObj.temperatureLowCritical) {
            return false;
        }
        if(temperatureHighCritical != otherObj.temperatureHighCritical) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += location.hashCode(); 
        __result += (int)temperatureLow;
        __result += (int)temperatureHigh;
        __result += (int)temperatureLowCritical;
        __result += (int)temperatureHighCritical;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ThermalInfoConfigTypeSupport</code>
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

        ThermalInfoConfig typedSrc = (ThermalInfoConfig) src;
        ThermalInfoConfig typedDst = this;

        typedDst.location = typedSrc.location;
        typedDst.temperatureLow = typedSrc.temperatureLow;
        typedDst.temperatureHigh = typedSrc.temperatureHigh;
        typedDst.temperatureLowCritical = typedSrc.temperatureLowCritical;
        typedDst.temperatureHighCritical = typedSrc.temperatureHighCritical;

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
        strBuffer.append("location: ").append(location).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("temperatureLow: ").append(temperatureLow).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("temperatureHigh: ").append(temperatureHigh).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("temperatureLowCritical: ").append(temperatureLowCritical).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("temperatureHighCritical: ").append(temperatureHighCritical).append("\n");  

        return strBuffer.toString();
    }

}
