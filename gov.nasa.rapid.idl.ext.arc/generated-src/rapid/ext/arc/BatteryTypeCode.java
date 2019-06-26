
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  BatteryTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[12];

        sm[__i]=new  StructMember("serialNumber", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONG,0 , false);__i++;
        sm[__i]=new  StructMember("voltage", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,1 , false);__i++;
        sm[__i]=new  StructMember("averageCurrent", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,2 , false);__i++;
        sm[__i]=new  StructMember("temperature", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,3 , false);__i++;
        sm[__i]=new  StructMember("relativeState", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,4 , false);__i++;
        sm[__i]=new  StructMember("absoluteState", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,5 , false);__i++;
        sm[__i]=new  StructMember("remainingCapacity", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,6 , false);__i++;
        sm[__i]=new  StructMember("fullChargeCapacity", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,7 , false);__i++;
        sm[__i]=new  StructMember("remainingTime", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONGLONG,8 , false);__i++;
        sm[__i]=new  StructMember("averageRemainingTime", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONGLONG,9 , false);__i++;
        sm[__i]=new  StructMember("averageRemainingChargeTime", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONGLONG,10 , false);__i++;
        sm[__i]=new  StructMember("numberOfChargeCycles", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONG,11 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::arc::Battery",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

