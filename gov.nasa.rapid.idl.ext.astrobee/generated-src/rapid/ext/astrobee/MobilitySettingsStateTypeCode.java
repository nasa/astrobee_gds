
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.typecode.*;

public class  MobilitySettingsStateTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[13];

        sm[__i]=new  ValueMember("flightMode", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String32TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("targetLinearVelocity", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,2 , false);__i++;
        sm[__i]=new  ValueMember("targetLinearAccel", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,3 , false);__i++;
        sm[__i]=new  ValueMember("targetAngularVelocity", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,4 , false);__i++;
        sm[__i]=new  ValueMember("targetAngularAccel", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,5 , false);__i++;
        sm[__i]=new  ValueMember("collisionDistance", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,6 , false);__i++;
        sm[__i]=new  ValueMember("enableHolonomic", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,7 , false);__i++;
        sm[__i]=new  ValueMember("checkObstacles", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,8 , false);__i++;
        sm[__i]=new  ValueMember("checkKeepouts", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,9 , false);__i++;
        sm[__i]=new  ValueMember("enableAutoReturn", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,10 , false);__i++;
        sm[__i]=new  ValueMember("timeSyncEnabled", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,11 , false);__i++;
        sm[__i]=new  ValueMember("immediateEnabled", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,12 , false);__i++;
        sm[__i]=new  ValueMember("planner", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) new TypeCode(TCKind.TK_STRING,255),13 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::astrobee::MobilitySettingsState",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

