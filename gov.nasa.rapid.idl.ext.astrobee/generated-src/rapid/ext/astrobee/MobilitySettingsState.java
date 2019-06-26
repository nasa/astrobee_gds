

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
* The mobility settings state of Astrobee.
*/

public class MobilitySettingsState  extends rapid.Message implements Copyable, Serializable{

    /** Defines PMC gains, hard limits, and tolerances
    * i.e. Nominal, quiet, aggressive, etc. */
    public String flightMode=  "" ; /* maximum length = (32) */
    /**  Target speed in m/s */
    public float targetLinearVelocity= 0;
    /**  Target acceleration in m/s/s */
    public float targetLinearAccel= 0;
    /**  Target turning speed in rad/s */
    public float targetAngularVelocity= 0;
    /**  Target turning acceleration in rad/s/s */
    public float targetAngularAccel= 0;
    /**  The operational hard limit on the collision distance. */
    public float collisionDistance= 0;
    /**  Allow Astrobee to fly in a direction it does not have cameras pointing. */
    public boolean enableHolonomic= false;
    /**  Set to false to disable checking for obstacles. */
    public boolean checkObstacles= false;
    /** Set to false to disable keepout checks. */
    public boolean checkKeepouts= false;
    /** Setting this to true allows Astrobee to
    * auto return when battery gets low and there is
    * a LOS. */
    public boolean enableAutoReturn= false;
    /** Set this to true to allow mobility to possibly
    * help sync moves between multiple Astrobees. */
    public boolean timeSyncEnabled= false;
    /** Set this to false if mobility should start the
    * a segment based on its time stamp. */
    public boolean immediateEnabled= false;
    /** Sets the planner mobility uses. */
    public String planner=  "" ; /* maximum length = (255) */

    public MobilitySettingsState() {

        super();

        /** Defines PMC gains, hard limits, and tolerances
        * i.e. Nominal, quiet, aggressive, etc. */
        /**  Target speed in m/s */
        /**  Target acceleration in m/s/s */
        /**  Target turning speed in rad/s */
        /**  Target turning acceleration in rad/s/s */
        /**  The operational hard limit on the collision distance. */
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing. */
        /**  Set to false to disable checking for obstacles. */
        /** Set to false to disable keepout checks. */
        /** Setting this to true allows Astrobee to
        * auto return when battery gets low and there is
        * a LOS. */
        /** Set this to true to allow mobility to possibly
        * help sync moves between multiple Astrobees. */
        /** Set this to false if mobility should start the
        * a segment based on its time stamp. */
        /** Sets the planner mobility uses. */

    }
    public MobilitySettingsState (MobilitySettingsState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MobilitySettingsState self;
        self = new  MobilitySettingsState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Defines PMC gains, hard limits, and tolerances
        * i.e. Nominal, quiet, aggressive, etc. */
        flightMode=  ""; 
        /**  Target speed in m/s */
        targetLinearVelocity= 0;
        /**  Target acceleration in m/s/s */
        targetLinearAccel= 0;
        /**  Target turning speed in rad/s */
        targetAngularVelocity= 0;
        /**  Target turning acceleration in rad/s/s */
        targetAngularAccel= 0;
        /**  The operational hard limit on the collision distance. */
        collisionDistance= 0;
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing. */
        enableHolonomic= false;
        /**  Set to false to disable checking for obstacles. */
        checkObstacles= false;
        /** Set to false to disable keepout checks. */
        checkKeepouts= false;
        /** Setting this to true allows Astrobee to
        * auto return when battery gets low and there is
        * a LOS. */
        enableAutoReturn= false;
        /** Set this to true to allow mobility to possibly
        * help sync moves between multiple Astrobees. */
        timeSyncEnabled= false;
        /** Set this to false if mobility should start the
        * a segment based on its time stamp. */
        immediateEnabled= false;
        /** Sets the planner mobility uses. */
        planner=  ""; 
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

        MobilitySettingsState otherObj = (MobilitySettingsState)o;

        /** Defines PMC gains, hard limits, and tolerances
        * i.e. Nominal, quiet, aggressive, etc. */
        if(!flightMode.equals(otherObj.flightMode)) {
            return false;
        }
        /**  Target speed in m/s */
        if(targetLinearVelocity != otherObj.targetLinearVelocity) {
            return false;
        }
        /**  Target acceleration in m/s/s */
        if(targetLinearAccel != otherObj.targetLinearAccel) {
            return false;
        }
        /**  Target turning speed in rad/s */
        if(targetAngularVelocity != otherObj.targetAngularVelocity) {
            return false;
        }
        /**  Target turning acceleration in rad/s/s */
        if(targetAngularAccel != otherObj.targetAngularAccel) {
            return false;
        }
        /**  The operational hard limit on the collision distance. */
        if(collisionDistance != otherObj.collisionDistance) {
            return false;
        }
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing. */
        if(enableHolonomic != otherObj.enableHolonomic) {
            return false;
        }
        /**  Set to false to disable checking for obstacles. */
        if(checkObstacles != otherObj.checkObstacles) {
            return false;
        }
        /** Set to false to disable keepout checks. */
        if(checkKeepouts != otherObj.checkKeepouts) {
            return false;
        }
        /** Setting this to true allows Astrobee to
        * auto return when battery gets low and there is
        * a LOS. */
        if(enableAutoReturn != otherObj.enableAutoReturn) {
            return false;
        }
        /** Set this to true to allow mobility to possibly
        * help sync moves between multiple Astrobees. */
        if(timeSyncEnabled != otherObj.timeSyncEnabled) {
            return false;
        }
        /** Set this to false if mobility should start the
        * a segment based on its time stamp. */
        if(immediateEnabled != otherObj.immediateEnabled) {
            return false;
        }
        /** Sets the planner mobility uses. */
        if(!planner.equals(otherObj.planner)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Defines PMC gains, hard limits, and tolerances
        * i.e. Nominal, quiet, aggressive, etc. */
        __result += flightMode.hashCode(); 
        /**  Target speed in m/s */
        __result += (int)targetLinearVelocity;
        /**  Target acceleration in m/s/s */
        __result += (int)targetLinearAccel;
        /**  Target turning speed in rad/s */
        __result += (int)targetAngularVelocity;
        /**  Target turning acceleration in rad/s/s */
        __result += (int)targetAngularAccel;
        /**  The operational hard limit on the collision distance. */
        __result += (int)collisionDistance;
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing. */
        __result += (enableHolonomic == true)?1:0;
        /**  Set to false to disable checking for obstacles. */
        __result += (checkObstacles == true)?1:0;
        /** Set to false to disable keepout checks. */
        __result += (checkKeepouts == true)?1:0;
        /** Setting this to true allows Astrobee to
        * auto return when battery gets low and there is
        * a LOS. */
        __result += (enableAutoReturn == true)?1:0;
        /** Set this to true to allow mobility to possibly
        * help sync moves between multiple Astrobees. */
        __result += (timeSyncEnabled == true)?1:0;
        /** Set this to false if mobility should start the
        * a segment based on its time stamp. */
        __result += (immediateEnabled == true)?1:0;
        /** Sets the planner mobility uses. */
        __result += planner.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MobilitySettingsStateTypeSupport</code>
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

        MobilitySettingsState typedSrc = (MobilitySettingsState) src;
        MobilitySettingsState typedDst = this;
        super.copy_from(typedSrc);
        /** Defines PMC gains, hard limits, and tolerances
        * i.e. Nominal, quiet, aggressive, etc. */
        typedDst.flightMode = typedSrc.flightMode;
        /**  Target speed in m/s */
        typedDst.targetLinearVelocity = typedSrc.targetLinearVelocity;
        /**  Target acceleration in m/s/s */
        typedDst.targetLinearAccel = typedSrc.targetLinearAccel;
        /**  Target turning speed in rad/s */
        typedDst.targetAngularVelocity = typedSrc.targetAngularVelocity;
        /**  Target turning acceleration in rad/s/s */
        typedDst.targetAngularAccel = typedSrc.targetAngularAccel;
        /**  The operational hard limit on the collision distance. */
        typedDst.collisionDistance = typedSrc.collisionDistance;
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing. */
        typedDst.enableHolonomic = typedSrc.enableHolonomic;
        /**  Set to false to disable checking for obstacles. */
        typedDst.checkObstacles = typedSrc.checkObstacles;
        /** Set to false to disable keepout checks. */
        typedDst.checkKeepouts = typedSrc.checkKeepouts;
        /** Setting this to true allows Astrobee to
        * auto return when battery gets low and there is
        * a LOS. */
        typedDst.enableAutoReturn = typedSrc.enableAutoReturn;
        /** Set this to true to allow mobility to possibly
        * help sync moves between multiple Astrobees. */
        typedDst.timeSyncEnabled = typedSrc.timeSyncEnabled;
        /** Set this to false if mobility should start the
        * a segment based on its time stamp. */
        typedDst.immediateEnabled = typedSrc.immediateEnabled;
        /** Sets the planner mobility uses. */
        typedDst.planner = typedSrc.planner;

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

        /** Defines PMC gains, hard limits, and tolerances
        * i.e. Nominal, quiet, aggressive, etc. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("flightMode: ").append(flightMode).append("\n");  
        /**  Target speed in m/s */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetLinearVelocity: ").append(targetLinearVelocity).append("\n");  
        /**  Target acceleration in m/s/s */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetLinearAccel: ").append(targetLinearAccel).append("\n");  
        /**  Target turning speed in rad/s */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetAngularVelocity: ").append(targetAngularVelocity).append("\n");  
        /**  Target turning acceleration in rad/s/s */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetAngularAccel: ").append(targetAngularAccel).append("\n");  
        /**  The operational hard limit on the collision distance. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("collisionDistance: ").append(collisionDistance).append("\n");  
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("enableHolonomic: ").append(enableHolonomic).append("\n");  
        /**  Set to false to disable checking for obstacles. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("checkObstacles: ").append(checkObstacles).append("\n");  
        /** Set to false to disable keepout checks. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("checkKeepouts: ").append(checkKeepouts).append("\n");  
        /** Setting this to true allows Astrobee to
        * auto return when battery gets low and there is
        * a LOS. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("enableAutoReturn: ").append(enableAutoReturn).append("\n");  
        /** Set this to true to allow mobility to possibly
        * help sync moves between multiple Astrobees. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("timeSyncEnabled: ").append(timeSyncEnabled).append("\n");  
        /** Set this to false if mobility should start the
        * a segment based on its time stamp. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("immediateEnabled: ").append(immediateEnabled).append("\n");  
        /** Sets the planner mobility uses. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("planner: ").append(planner).append("\n");  

        return strBuffer.toString();
    }

}
