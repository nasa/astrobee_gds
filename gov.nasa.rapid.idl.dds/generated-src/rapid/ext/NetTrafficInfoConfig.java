

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

public class NetTrafficInfoConfig   implements Copyable, Serializable{

    public String name=  "" ; /* maximum length = (32) */
    public int rxBandwidth= 0;
    public int txBandwidth= 0;
    public int rxHigh= 0;
    public int rxCritical= 0;
    public int txHigh= 0;
    public int txCritical= 0;

    public NetTrafficInfoConfig() {

    }
    public NetTrafficInfoConfig (NetTrafficInfoConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        NetTrafficInfoConfig self;
        self = new  NetTrafficInfoConfig();
        self.clear();
        return self;

    }

    public void clear() {

        name=  ""; 
        rxBandwidth= 0;
        txBandwidth= 0;
        rxHigh= 0;
        rxCritical= 0;
        txHigh= 0;
        txCritical= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        NetTrafficInfoConfig otherObj = (NetTrafficInfoConfig)o;

        if(!name.equals(otherObj.name)) {
            return false;
        }
        if(rxBandwidth != otherObj.rxBandwidth) {
            return false;
        }
        if(txBandwidth != otherObj.txBandwidth) {
            return false;
        }
        if(rxHigh != otherObj.rxHigh) {
            return false;
        }
        if(rxCritical != otherObj.rxCritical) {
            return false;
        }
        if(txHigh != otherObj.txHigh) {
            return false;
        }
        if(txCritical != otherObj.txCritical) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += name.hashCode(); 
        __result += (int)rxBandwidth;
        __result += (int)txBandwidth;
        __result += (int)rxHigh;
        __result += (int)rxCritical;
        __result += (int)txHigh;
        __result += (int)txCritical;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>NetTrafficInfoConfigTypeSupport</code>
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

        NetTrafficInfoConfig typedSrc = (NetTrafficInfoConfig) src;
        NetTrafficInfoConfig typedDst = this;

        typedDst.name = typedSrc.name;
        typedDst.rxBandwidth = typedSrc.rxBandwidth;
        typedDst.txBandwidth = typedSrc.txBandwidth;
        typedDst.rxHigh = typedSrc.rxHigh;
        typedDst.rxCritical = typedSrc.rxCritical;
        typedDst.txHigh = typedSrc.txHigh;
        typedDst.txCritical = typedSrc.txCritical;

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
        strBuffer.append("name: ").append(name).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("rxBandwidth: ").append(rxBandwidth).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("txBandwidth: ").append(txBandwidth).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("rxHigh: ").append(rxHigh).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("rxCritical: ").append(rxCritical).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("txHigh: ").append(txHigh).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("txCritical: ").append(txCritical).append("\n");  

        return strBuffer.toString();
    }

}
