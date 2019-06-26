

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

public class BerthState   implements Copyable, Serializable{

    /** Whether this berth is occupied or not */
    public boolean occupied= false;
    /**
    * Name of Astrobee. If there is no Astrobee in
    * the berth or if the dock doesn't know what the
    * name of the Astrobee on it is, the name will
    * be blank.
    */
    public String astrobeeName=  "" ; /* maximum length = (32) */
    /** Whether the freeflyer is awake or hibernated */
    public boolean awake= false;
    /** Number of batteries plugged into the astrobee */
    public short numBatteries= 0;
    /** Maximum capacity of the batteries plugged in */
    public int maxCapacity= 0;
    /** Current capacity of the batteries plugged in */
    public int currentCapacity= 0;

    public BerthState() {

        /** Whether this berth is occupied or not */
        /**
        * Name of Astrobee. If there is no Astrobee in
        * the berth or if the dock doesn't know what the
        * name of the Astrobee on it is, the name will
        * be blank.
        */
        /** Whether the freeflyer is awake or hibernated */
        /** Number of batteries plugged into the astrobee */
        /** Maximum capacity of the batteries plugged in */
        /** Current capacity of the batteries plugged in */

    }
    public BerthState (BerthState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        BerthState self;
        self = new  BerthState();
        self.clear();
        return self;

    }

    public void clear() {

        /** Whether this berth is occupied or not */
        occupied= false;
        /**
        * Name of Astrobee. If there is no Astrobee in
        * the berth or if the dock doesn't know what the
        * name of the Astrobee on it is, the name will
        * be blank.
        */
        astrobeeName=  ""; 
        /** Whether the freeflyer is awake or hibernated */
        awake= false;
        /** Number of batteries plugged into the astrobee */
        numBatteries= 0;
        /** Maximum capacity of the batteries plugged in */
        maxCapacity= 0;
        /** Current capacity of the batteries plugged in */
        currentCapacity= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        BerthState otherObj = (BerthState)o;

        /** Whether this berth is occupied or not */
        if(occupied != otherObj.occupied) {
            return false;
        }
        /**
        * Name of Astrobee. If there is no Astrobee in
        * the berth or if the dock doesn't know what the
        * name of the Astrobee on it is, the name will
        * be blank.
        */
        if(!astrobeeName.equals(otherObj.astrobeeName)) {
            return false;
        }
        /** Whether the freeflyer is awake or hibernated */
        if(awake != otherObj.awake) {
            return false;
        }
        /** Number of batteries plugged into the astrobee */
        if(numBatteries != otherObj.numBatteries) {
            return false;
        }
        /** Maximum capacity of the batteries plugged in */
        if(maxCapacity != otherObj.maxCapacity) {
            return false;
        }
        /** Current capacity of the batteries plugged in */
        if(currentCapacity != otherObj.currentCapacity) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Whether this berth is occupied or not */
        __result += (occupied == true)?1:0;
        /**
        * Name of Astrobee. If there is no Astrobee in
        * the berth or if the dock doesn't know what the
        * name of the Astrobee on it is, the name will
        * be blank.
        */
        __result += astrobeeName.hashCode(); 
        /** Whether the freeflyer is awake or hibernated */
        __result += (awake == true)?1:0;
        /** Number of batteries plugged into the astrobee */
        __result += (int)numBatteries;
        /** Maximum capacity of the batteries plugged in */
        __result += (int)maxCapacity;
        /** Current capacity of the batteries plugged in */
        __result += (int)currentCapacity;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>BerthStateTypeSupport</code>
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

        BerthState typedSrc = (BerthState) src;
        BerthState typedDst = this;

        /** Whether this berth is occupied or not */
        typedDst.occupied = typedSrc.occupied;
        /**
        * Name of Astrobee. If there is no Astrobee in
        * the berth or if the dock doesn't know what the
        * name of the Astrobee on it is, the name will
        * be blank.
        */
        typedDst.astrobeeName = typedSrc.astrobeeName;
        /** Whether the freeflyer is awake or hibernated */
        typedDst.awake = typedSrc.awake;
        /** Number of batteries plugged into the astrobee */
        typedDst.numBatteries = typedSrc.numBatteries;
        /** Maximum capacity of the batteries plugged in */
        typedDst.maxCapacity = typedSrc.maxCapacity;
        /** Current capacity of the batteries plugged in */
        typedDst.currentCapacity = typedSrc.currentCapacity;

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

        /** Whether this berth is occupied or not */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("occupied: ").append(occupied).append("\n");  
        /**
        * Name of Astrobee. If there is no Astrobee in
        * the berth or if the dock doesn't know what the
        * name of the Astrobee on it is, the name will
        * be blank.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("astrobeeName: ").append(astrobeeName).append("\n");  
        /** Whether the freeflyer is awake or hibernated */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("awake: ").append(awake).append("\n");  
        /** Number of batteries plugged into the astrobee */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("numBatteries: ").append(numBatteries).append("\n");  
        /** Maximum capacity of the batteries plugged in */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("maxCapacity: ").append(maxCapacity).append("\n");  
        /** Current capacity of the batteries plugged in */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("currentCapacity: ").append(currentCapacity).append("\n");  

        return strBuffer.toString();
    }

}
