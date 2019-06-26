
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.typecode.*;

public class  AgentStateTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[18];

        sm[__i]=new  ValueMember("operatingState", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.astrobee.OperatingStateTypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("executionState", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.astrobee.ExecutionStateTypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("mobilityState", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.astrobee.MobilityStateTypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("subMobilityState", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONG,4 , false);__i++;
        sm[__i]=new  ValueMember("guestScienceState", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.astrobee.ExecutionStateTypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  ValueMember("proximity", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,6 , false);__i++;
        sm[__i]=new  ValueMember("profileName", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String32TypeCode.VALUE,7 , false);__i++;
        sm[__i]=new  ValueMember("flightMode", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String32TypeCode.VALUE,8 , false);__i++;
        sm[__i]=new  ValueMember("targetLinearVelocity", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,9 , false);__i++;
        sm[__i]=new  ValueMember("targetLinearAccel", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,10 , false);__i++;
        sm[__i]=new  ValueMember("targetAngularVelocity", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,11 , false);__i++;
        sm[__i]=new  ValueMember("targetAngularAccel", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,12 , false);__i++;
        sm[__i]=new  ValueMember("collisionDistance", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,13 , false);__i++;
        sm[__i]=new  ValueMember("enableHolonomic", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,14 , false);__i++;
        sm[__i]=new  ValueMember("checkObstacles", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,15 , false);__i++;
        sm[__i]=new  ValueMember("checkKeepouts", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,16 , false);__i++;
        sm[__i]=new  ValueMember("enableAutoReturn", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,17 , false);__i++;
        sm[__i]=new  ValueMember("bootTime", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONG,18 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::astrobee::AgentState",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

