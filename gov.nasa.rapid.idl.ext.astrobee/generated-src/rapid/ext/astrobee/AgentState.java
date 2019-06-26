

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
* Operating state of Astrobee
* <ul>
*   <li>OPERATING_STATE_READY: Robot is ready to take commands.
*   <li>OPERATING_STATE_FAULT: Robot is executing a fault response
*   <li>OPERATING_STATE_PLAN_EXECUTION: Robot is executing a loaded plan
*   <li>OPERATING_STATE_TELEOPERATION: Robot is executing a teleop command
*   <li>OPERATING_STATE_AUTO_RETURN: Robot returns to dock
*   <li>OPERATING_STATE_GUEST_SCIENCE: Guest science has control.
* </ul>
*/
/**
* Executing state of Astrobee
* <ul>
*   <li>EXECUTION_STATE_IDLE: Robot does not have a plan loaded.
*   <li>EXECUTION_STATE_EXECUTING: Robot is executing a plan
*   <li>EXECUTION_STATE_PAUSED: Robot is not executing a plan, but has a plan loaded and ready to resume
*   <li>EXECUTION_STATE_ERROR: Robot was unable to load the plan
* </ul>
*/
/**
* Mobility state of Astrobee
* <ul>
*   <li>MOBILITY_STATE_DRIFTING: Robot is floating around in spaaaaace
*   <li>MOBILITY_STATE_STOPPING: Robot is either stopping or stopped
*   <li>MOBILITY_STATE_FLYING: Robot is flying around in spaaaaace
*   <li>MOBILITY_STATE_DOCKING: Robot is either docking, docked, or undocking.
*   <li>MOBILITY_STATE_PERCHING: Robot is either perching, perched, or unperching via the arm.
* </ul>
*/
/**
* The state of Astrobee.
*/

public class AgentState  extends rapid.Message implements Copyable, Serializable{

    /**  @see OperatingState */
    public rapid.ext.astrobee.OperatingState operatingState = (rapid.ext.astrobee.OperatingState)rapid.ext.astrobee.OperatingState.create();
    /**  @see ExecutionState */
    public rapid.ext.astrobee.ExecutionState executionState = (rapid.ext.astrobee.ExecutionState)rapid.ext.astrobee.ExecutionState.create();
    /**  @see MobilityState */
    public rapid.ext.astrobee.MobilityState mobilityState = (rapid.ext.astrobee.MobilityState)rapid.ext.astrobee.MobilityState.create();
    /**  For MOBILITY_STATE_PERCHING,
    *  MOBILITY_STATE_STOPPING, and
    *  MOBILITY_STATE_DOCKING, this specifies the
    *  progress of the action. For docking, this value
    *  can be N to -N where N through 1 is docking,
    *  0 is docked, and -1 through -N is undocking.
    *  For stopping, this value can be 1 or where 1 is
    *  stopping and 0 is stopped. For perching, this
    *  value can be N to -N where N through 1 is
    *  perching, 0 is perched, and -1 through -N is
    *  unperching. In other states this field's value
    *  SHOULD be 0.
    */
    public int subMobilityState= 0;
    /**  The Executing State of Guest Science mode of Astrobee,
    *  this is only valid if the Operating State is
    *  OPERATING_STATE_GUEST_SCIENCE
    */
    public rapid.ext.astrobee.ExecutionState guestScienceState = (rapid.ext.astrobee.ExecutionState)rapid.ext.astrobee.ExecutionState.create();
    /**  For MOBILITY_STATE_PERCHING and MOBILITY_STATE_DOCKING,
    *  this specifies the proximity to the target.  In other states
    *  this field's value SHOULD be 0.
    */
    public float proximity= 0;
    /**  Name of configuration, i.e. Nominal,
    *  IgnoreObstacles, Faceforward, Quiet, etc.*/
    public String profileName=  "" ; /* maximum length = (32) */
    /**  Defines OMC gains, hard limits, and tolerances
    *  i.e. Nominal, quiet, aggressive, etc. */
    public String flightMode=  "" ; /* maximum length = (32) */
    /**  Target speed in m/s */
    public float targetLinearVelocity= 0;
    /**  Target acceleration in m/s/s */
    public float targetLinearAccel= 0;
    /**  Target turning speed in rad/s   */
    public float targetAngularVelocity= 0;
    /**  Target turning acceleration in rad/s/s  */
    public float targetAngularAccel= 0;
    /**  The operational hard limit on the collision distance.  */
    public float collisionDistance= 0;
    /**  Allow Astrobee to fly in a direction it does not have cameras pointing  */
    public boolean enableHolonomic= false;
    /**  Set to false to disable checking for obstacles  */
    public boolean checkObstacles= false;
    /**  Set to false to disable keepout checks  */
    public boolean checkKeepouts= false;
    /**  Setting this to true allows Astrobee to
    *  auto return when battery gets low and there is
    *  a LOS */
    public boolean enableAutoReturn= false;
    /**  Number of seconds since the Unix Epoch.  */
    public int bootTime= 0;

    public AgentState() {

        super();

        /**  @see OperatingState */
        /**  @see ExecutionState */
        /**  @see MobilityState */
        /**  For MOBILITY_STATE_PERCHING,
        *  MOBILITY_STATE_STOPPING, and
        *  MOBILITY_STATE_DOCKING, this specifies the
        *  progress of the action. For docking, this value
        *  can be N to -N where N through 1 is docking,
        *  0 is docked, and -1 through -N is undocking.
        *  For stopping, this value can be 1 or where 1 is
        *  stopping and 0 is stopped. For perching, this
        *  value can be N to -N where N through 1 is
        *  perching, 0 is perched, and -1 through -N is
        *  unperching. In other states this field's value
        *  SHOULD be 0.
        */
        /**  The Executing State of Guest Science mode of Astrobee,
        *  this is only valid if the Operating State is
        *  OPERATING_STATE_GUEST_SCIENCE
        */
        /**  For MOBILITY_STATE_PERCHING and MOBILITY_STATE_DOCKING,
        *  this specifies the proximity to the target.  In other states
        *  this field's value SHOULD be 0.
        */
        /**  Name of configuration, i.e. Nominal,
        *  IgnoreObstacles, Faceforward, Quiet, etc.*/
        /**  Defines OMC gains, hard limits, and tolerances
        *  i.e. Nominal, quiet, aggressive, etc. */
        /**  Target speed in m/s */
        /**  Target acceleration in m/s/s */
        /**  Target turning speed in rad/s   */
        /**  Target turning acceleration in rad/s/s  */
        /**  The operational hard limit on the collision distance.  */
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing  */
        /**  Set to false to disable checking for obstacles  */
        /**  Set to false to disable keepout checks  */
        /**  Setting this to true allows Astrobee to
        *  auto return when battery gets low and there is
        *  a LOS */
        /**  Number of seconds since the Unix Epoch.  */

    }
    public AgentState (AgentState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        AgentState self;
        self = new  AgentState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /**  @see OperatingState */
        operatingState = rapid.ext.astrobee.OperatingState.create();
        /**  @see ExecutionState */
        executionState = rapid.ext.astrobee.ExecutionState.create();
        /**  @see MobilityState */
        mobilityState = rapid.ext.astrobee.MobilityState.create();
        /**  For MOBILITY_STATE_PERCHING,
        *  MOBILITY_STATE_STOPPING, and
        *  MOBILITY_STATE_DOCKING, this specifies the
        *  progress of the action. For docking, this value
        *  can be N to -N where N through 1 is docking,
        *  0 is docked, and -1 through -N is undocking.
        *  For stopping, this value can be 1 or where 1 is
        *  stopping and 0 is stopped. For perching, this
        *  value can be N to -N where N through 1 is
        *  perching, 0 is perched, and -1 through -N is
        *  unperching. In other states this field's value
        *  SHOULD be 0.
        */
        subMobilityState= 0;
        /**  The Executing State of Guest Science mode of Astrobee,
        *  this is only valid if the Operating State is
        *  OPERATING_STATE_GUEST_SCIENCE
        */
        guestScienceState = rapid.ext.astrobee.ExecutionState.create();
        /**  For MOBILITY_STATE_PERCHING and MOBILITY_STATE_DOCKING,
        *  this specifies the proximity to the target.  In other states
        *  this field's value SHOULD be 0.
        */
        proximity= 0;
        /**  Name of configuration, i.e. Nominal,
        *  IgnoreObstacles, Faceforward, Quiet, etc.*/
        profileName=  ""; 
        /**  Defines OMC gains, hard limits, and tolerances
        *  i.e. Nominal, quiet, aggressive, etc. */
        flightMode=  ""; 
        /**  Target speed in m/s */
        targetLinearVelocity= 0;
        /**  Target acceleration in m/s/s */
        targetLinearAccel= 0;
        /**  Target turning speed in rad/s   */
        targetAngularVelocity= 0;
        /**  Target turning acceleration in rad/s/s  */
        targetAngularAccel= 0;
        /**  The operational hard limit on the collision distance.  */
        collisionDistance= 0;
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing  */
        enableHolonomic= false;
        /**  Set to false to disable checking for obstacles  */
        checkObstacles= false;
        /**  Set to false to disable keepout checks  */
        checkKeepouts= false;
        /**  Setting this to true allows Astrobee to
        *  auto return when battery gets low and there is
        *  a LOS */
        enableAutoReturn= false;
        /**  Number of seconds since the Unix Epoch.  */
        bootTime= 0;
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

        AgentState otherObj = (AgentState)o;

        /**  @see OperatingState */
        if(!operatingState.equals(otherObj.operatingState)) {
            return false;
        }
        /**  @see ExecutionState */
        if(!executionState.equals(otherObj.executionState)) {
            return false;
        }
        /**  @see MobilityState */
        if(!mobilityState.equals(otherObj.mobilityState)) {
            return false;
        }
        /**  For MOBILITY_STATE_PERCHING,
        *  MOBILITY_STATE_STOPPING, and
        *  MOBILITY_STATE_DOCKING, this specifies the
        *  progress of the action. For docking, this value
        *  can be N to -N where N through 1 is docking,
        *  0 is docked, and -1 through -N is undocking.
        *  For stopping, this value can be 1 or where 1 is
        *  stopping and 0 is stopped. For perching, this
        *  value can be N to -N where N through 1 is
        *  perching, 0 is perched, and -1 through -N is
        *  unperching. In other states this field's value
        *  SHOULD be 0.
        */
        if(subMobilityState != otherObj.subMobilityState) {
            return false;
        }
        /**  The Executing State of Guest Science mode of Astrobee,
        *  this is only valid if the Operating State is
        *  OPERATING_STATE_GUEST_SCIENCE
        */
        if(!guestScienceState.equals(otherObj.guestScienceState)) {
            return false;
        }
        /**  For MOBILITY_STATE_PERCHING and MOBILITY_STATE_DOCKING,
        *  this specifies the proximity to the target.  In other states
        *  this field's value SHOULD be 0.
        */
        if(proximity != otherObj.proximity) {
            return false;
        }
        /**  Name of configuration, i.e. Nominal,
        *  IgnoreObstacles, Faceforward, Quiet, etc.*/
        if(!profileName.equals(otherObj.profileName)) {
            return false;
        }
        /**  Defines OMC gains, hard limits, and tolerances
        *  i.e. Nominal, quiet, aggressive, etc. */
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
        /**  Target turning speed in rad/s   */
        if(targetAngularVelocity != otherObj.targetAngularVelocity) {
            return false;
        }
        /**  Target turning acceleration in rad/s/s  */
        if(targetAngularAccel != otherObj.targetAngularAccel) {
            return false;
        }
        /**  The operational hard limit on the collision distance.  */
        if(collisionDistance != otherObj.collisionDistance) {
            return false;
        }
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing  */
        if(enableHolonomic != otherObj.enableHolonomic) {
            return false;
        }
        /**  Set to false to disable checking for obstacles  */
        if(checkObstacles != otherObj.checkObstacles) {
            return false;
        }
        /**  Set to false to disable keepout checks  */
        if(checkKeepouts != otherObj.checkKeepouts) {
            return false;
        }
        /**  Setting this to true allows Astrobee to
        *  auto return when battery gets low and there is
        *  a LOS */
        if(enableAutoReturn != otherObj.enableAutoReturn) {
            return false;
        }
        /**  Number of seconds since the Unix Epoch.  */
        if(bootTime != otherObj.bootTime) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /**  @see OperatingState */
        __result += operatingState.hashCode(); 
        /**  @see ExecutionState */
        __result += executionState.hashCode(); 
        /**  @see MobilityState */
        __result += mobilityState.hashCode(); 
        /**  For MOBILITY_STATE_PERCHING,
        *  MOBILITY_STATE_STOPPING, and
        *  MOBILITY_STATE_DOCKING, this specifies the
        *  progress of the action. For docking, this value
        *  can be N to -N where N through 1 is docking,
        *  0 is docked, and -1 through -N is undocking.
        *  For stopping, this value can be 1 or where 1 is
        *  stopping and 0 is stopped. For perching, this
        *  value can be N to -N where N through 1 is
        *  perching, 0 is perched, and -1 through -N is
        *  unperching. In other states this field's value
        *  SHOULD be 0.
        */
        __result += (int)subMobilityState;
        /**  The Executing State of Guest Science mode of Astrobee,
        *  this is only valid if the Operating State is
        *  OPERATING_STATE_GUEST_SCIENCE
        */
        __result += guestScienceState.hashCode(); 
        /**  For MOBILITY_STATE_PERCHING and MOBILITY_STATE_DOCKING,
        *  this specifies the proximity to the target.  In other states
        *  this field's value SHOULD be 0.
        */
        __result += (int)proximity;
        /**  Name of configuration, i.e. Nominal,
        *  IgnoreObstacles, Faceforward, Quiet, etc.*/
        __result += profileName.hashCode(); 
        /**  Defines OMC gains, hard limits, and tolerances
        *  i.e. Nominal, quiet, aggressive, etc. */
        __result += flightMode.hashCode(); 
        /**  Target speed in m/s */
        __result += (int)targetLinearVelocity;
        /**  Target acceleration in m/s/s */
        __result += (int)targetLinearAccel;
        /**  Target turning speed in rad/s   */
        __result += (int)targetAngularVelocity;
        /**  Target turning acceleration in rad/s/s  */
        __result += (int)targetAngularAccel;
        /**  The operational hard limit on the collision distance.  */
        __result += (int)collisionDistance;
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing  */
        __result += (enableHolonomic == true)?1:0;
        /**  Set to false to disable checking for obstacles  */
        __result += (checkObstacles == true)?1:0;
        /**  Set to false to disable keepout checks  */
        __result += (checkKeepouts == true)?1:0;
        /**  Setting this to true allows Astrobee to
        *  auto return when battery gets low and there is
        *  a LOS */
        __result += (enableAutoReturn == true)?1:0;
        /**  Number of seconds since the Unix Epoch.  */
        __result += (int)bootTime;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>AgentStateTypeSupport</code>
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

        AgentState typedSrc = (AgentState) src;
        AgentState typedDst = this;
        super.copy_from(typedSrc);
        /**  @see OperatingState */
        typedDst.operatingState = (rapid.ext.astrobee.OperatingState) typedDst.operatingState.copy_from(typedSrc.operatingState);
        /**  @see ExecutionState */
        typedDst.executionState = (rapid.ext.astrobee.ExecutionState) typedDst.executionState.copy_from(typedSrc.executionState);
        /**  @see MobilityState */
        typedDst.mobilityState = (rapid.ext.astrobee.MobilityState) typedDst.mobilityState.copy_from(typedSrc.mobilityState);
        /**  For MOBILITY_STATE_PERCHING,
        *  MOBILITY_STATE_STOPPING, and
        *  MOBILITY_STATE_DOCKING, this specifies the
        *  progress of the action. For docking, this value
        *  can be N to -N where N through 1 is docking,
        *  0 is docked, and -1 through -N is undocking.
        *  For stopping, this value can be 1 or where 1 is
        *  stopping and 0 is stopped. For perching, this
        *  value can be N to -N where N through 1 is
        *  perching, 0 is perched, and -1 through -N is
        *  unperching. In other states this field's value
        *  SHOULD be 0.
        */
        typedDst.subMobilityState = typedSrc.subMobilityState;
        /**  The Executing State of Guest Science mode of Astrobee,
        *  this is only valid if the Operating State is
        *  OPERATING_STATE_GUEST_SCIENCE
        */
        typedDst.guestScienceState = (rapid.ext.astrobee.ExecutionState) typedDst.guestScienceState.copy_from(typedSrc.guestScienceState);
        /**  For MOBILITY_STATE_PERCHING and MOBILITY_STATE_DOCKING,
        *  this specifies the proximity to the target.  In other states
        *  this field's value SHOULD be 0.
        */
        typedDst.proximity = typedSrc.proximity;
        /**  Name of configuration, i.e. Nominal,
        *  IgnoreObstacles, Faceforward, Quiet, etc.*/
        typedDst.profileName = typedSrc.profileName;
        /**  Defines OMC gains, hard limits, and tolerances
        *  i.e. Nominal, quiet, aggressive, etc. */
        typedDst.flightMode = typedSrc.flightMode;
        /**  Target speed in m/s */
        typedDst.targetLinearVelocity = typedSrc.targetLinearVelocity;
        /**  Target acceleration in m/s/s */
        typedDst.targetLinearAccel = typedSrc.targetLinearAccel;
        /**  Target turning speed in rad/s   */
        typedDst.targetAngularVelocity = typedSrc.targetAngularVelocity;
        /**  Target turning acceleration in rad/s/s  */
        typedDst.targetAngularAccel = typedSrc.targetAngularAccel;
        /**  The operational hard limit on the collision distance.  */
        typedDst.collisionDistance = typedSrc.collisionDistance;
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing  */
        typedDst.enableHolonomic = typedSrc.enableHolonomic;
        /**  Set to false to disable checking for obstacles  */
        typedDst.checkObstacles = typedSrc.checkObstacles;
        /**  Set to false to disable keepout checks  */
        typedDst.checkKeepouts = typedSrc.checkKeepouts;
        /**  Setting this to true allows Astrobee to
        *  auto return when battery gets low and there is
        *  a LOS */
        typedDst.enableAutoReturn = typedSrc.enableAutoReturn;
        /**  Number of seconds since the Unix Epoch.  */
        typedDst.bootTime = typedSrc.bootTime;

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

        /**  @see OperatingState */
        strBuffer.append(operatingState.toString("operatingState ", indent+1));
        /**  @see ExecutionState */
        strBuffer.append(executionState.toString("executionState ", indent+1));
        /**  @see MobilityState */
        strBuffer.append(mobilityState.toString("mobilityState ", indent+1));
        /**  For MOBILITY_STATE_PERCHING,
        *  MOBILITY_STATE_STOPPING, and
        *  MOBILITY_STATE_DOCKING, this specifies the
        *  progress of the action. For docking, this value
        *  can be N to -N where N through 1 is docking,
        *  0 is docked, and -1 through -N is undocking.
        *  For stopping, this value can be 1 or where 1 is
        *  stopping and 0 is stopped. For perching, this
        *  value can be N to -N where N through 1 is
        *  perching, 0 is perched, and -1 through -N is
        *  unperching. In other states this field's value
        *  SHOULD be 0.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("subMobilityState: ").append(subMobilityState).append("\n");  
        /**  The Executing State of Guest Science mode of Astrobee,
        *  this is only valid if the Operating State is
        *  OPERATING_STATE_GUEST_SCIENCE
        */
        strBuffer.append(guestScienceState.toString("guestScienceState ", indent+1));
        /**  For MOBILITY_STATE_PERCHING and MOBILITY_STATE_DOCKING,
        *  this specifies the proximity to the target.  In other states
        *  this field's value SHOULD be 0.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("proximity: ").append(proximity).append("\n");  
        /**  Name of configuration, i.e. Nominal,
        *  IgnoreObstacles, Faceforward, Quiet, etc.*/
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("profileName: ").append(profileName).append("\n");  
        /**  Defines OMC gains, hard limits, and tolerances
        *  i.e. Nominal, quiet, aggressive, etc. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("flightMode: ").append(flightMode).append("\n");  
        /**  Target speed in m/s */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetLinearVelocity: ").append(targetLinearVelocity).append("\n");  
        /**  Target acceleration in m/s/s */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetLinearAccel: ").append(targetLinearAccel).append("\n");  
        /**  Target turning speed in rad/s   */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetAngularVelocity: ").append(targetAngularVelocity).append("\n");  
        /**  Target turning acceleration in rad/s/s  */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetAngularAccel: ").append(targetAngularAccel).append("\n");  
        /**  The operational hard limit on the collision distance.  */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("collisionDistance: ").append(collisionDistance).append("\n");  
        /**  Allow Astrobee to fly in a direction it does not have cameras pointing  */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("enableHolonomic: ").append(enableHolonomic).append("\n");  
        /**  Set to false to disable checking for obstacles  */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("checkObstacles: ").append(checkObstacles).append("\n");  
        /**  Set to false to disable keepout checks  */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("checkKeepouts: ").append(checkKeepouts).append("\n");  
        /**  Setting this to true allows Astrobee to
        *  auto return when battery gets low and there is
        *  a LOS */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("enableAutoReturn: ").append(enableAutoReturn).append("\n");  
        /**  Number of seconds since the Unix Epoch.  */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("bootTime: ").append(bootTime).append("\n");  

        return strBuffer.toString();
    }

}
