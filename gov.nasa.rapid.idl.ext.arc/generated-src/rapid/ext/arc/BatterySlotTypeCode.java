
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  BatterySlotTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[6];

        sm[__i]=new  StructMember("present", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,0 , false);__i++;
        sm[__i]=new  StructMember("charging", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,1 , false);__i++;
        sm[__i]=new  StructMember("supplyingPower", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,2 , false);__i++;
        sm[__i]=new  StructMember("chargePowerPresent", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,3 , false);__i++;
        sm[__i]=new  StructMember("powerNoGood", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,4 , false);__i++;
        sm[__i]=new  StructMember("chargeInhibited", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,5 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::arc::BatterySlot",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

