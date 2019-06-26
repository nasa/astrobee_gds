

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
* Network state of Astrobee
*/

public class CommState  extends rapid.Message implements Copyable, Serializable{

    /** Whether the MLP wireless is connected */
    public boolean wirelessConnected= false;
    /** Name of the AP Astrobee is connected to */
    public String apName=  "" ; /* maximum length = (64) */
    /** BSSID of the connected AP */
    public String bssid=  "" ; /* maximum length = (32) */
    /** RSSI to the connected AP */
    public float rssi= 0;
    /** What frequency (2.4/5.8) we are connected at */
    public float frequency= 0;
    /** Which channel within the frequency? */
    public int channel= 0;
    /** Whether we are connected to the LAN as well */
    public boolean lanConnected= false;

    public CommState() {

        super();

        /** Whether the MLP wireless is connected */
        /** Name of the AP Astrobee is connected to */
        /** BSSID of the connected AP */
        /** RSSI to the connected AP */
        /** What frequency (2.4/5.8) we are connected at */
        /** Which channel within the frequency? */
        /** Whether we are connected to the LAN as well */

    }
    public CommState (CommState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CommState self;
        self = new  CommState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Whether the MLP wireless is connected */
        wirelessConnected= false;
        /** Name of the AP Astrobee is connected to */
        apName=  ""; 
        /** BSSID of the connected AP */
        bssid=  ""; 
        /** RSSI to the connected AP */
        rssi= 0;
        /** What frequency (2.4/5.8) we are connected at */
        frequency= 0;
        /** Which channel within the frequency? */
        channel= 0;
        /** Whether we are connected to the LAN as well */
        lanConnected= false;
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

        CommState otherObj = (CommState)o;

        /** Whether the MLP wireless is connected */
        if(wirelessConnected != otherObj.wirelessConnected) {
            return false;
        }
        /** Name of the AP Astrobee is connected to */
        if(!apName.equals(otherObj.apName)) {
            return false;
        }
        /** BSSID of the connected AP */
        if(!bssid.equals(otherObj.bssid)) {
            return false;
        }
        /** RSSI to the connected AP */
        if(rssi != otherObj.rssi) {
            return false;
        }
        /** What frequency (2.4/5.8) we are connected at */
        if(frequency != otherObj.frequency) {
            return false;
        }
        /** Which channel within the frequency? */
        if(channel != otherObj.channel) {
            return false;
        }
        /** Whether we are connected to the LAN as well */
        if(lanConnected != otherObj.lanConnected) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Whether the MLP wireless is connected */
        __result += (wirelessConnected == true)?1:0;
        /** Name of the AP Astrobee is connected to */
        __result += apName.hashCode(); 
        /** BSSID of the connected AP */
        __result += bssid.hashCode(); 
        /** RSSI to the connected AP */
        __result += (int)rssi;
        /** What frequency (2.4/5.8) we are connected at */
        __result += (int)frequency;
        /** Which channel within the frequency? */
        __result += (int)channel;
        /** Whether we are connected to the LAN as well */
        __result += (lanConnected == true)?1:0;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CommStateTypeSupport</code>
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

        CommState typedSrc = (CommState) src;
        CommState typedDst = this;
        super.copy_from(typedSrc);
        /** Whether the MLP wireless is connected */
        typedDst.wirelessConnected = typedSrc.wirelessConnected;
        /** Name of the AP Astrobee is connected to */
        typedDst.apName = typedSrc.apName;
        /** BSSID of the connected AP */
        typedDst.bssid = typedSrc.bssid;
        /** RSSI to the connected AP */
        typedDst.rssi = typedSrc.rssi;
        /** What frequency (2.4/5.8) we are connected at */
        typedDst.frequency = typedSrc.frequency;
        /** Which channel within the frequency? */
        typedDst.channel = typedSrc.channel;
        /** Whether we are connected to the LAN as well */
        typedDst.lanConnected = typedSrc.lanConnected;

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

        /** Whether the MLP wireless is connected */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("wirelessConnected: ").append(wirelessConnected).append("\n");  
        /** Name of the AP Astrobee is connected to */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("apName: ").append(apName).append("\n");  
        /** BSSID of the connected AP */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("bssid: ").append(bssid).append("\n");  
        /** RSSI to the connected AP */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("rssi: ").append(rssi).append("\n");  
        /** What frequency (2.4/5.8) we are connected at */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("frequency: ").append(frequency).append("\n");  
        /** Which channel within the frequency? */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("channel: ").append(channel).append("\n");  
        /** Whether we are connected to the LAN as well */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("lanConnected: ").append(lanConnected).append("\n");  

        return strBuffer.toString();
    }

}
