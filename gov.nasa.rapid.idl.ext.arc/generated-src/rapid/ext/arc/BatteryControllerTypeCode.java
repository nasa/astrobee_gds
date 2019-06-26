
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  BatteryControllerTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[2];

        sm[__i]=new  StructMember("batterySlots", false, (short)-1,  false,(TypeCode) new TypeCode(new int[] {8}, rapid.ext.arc.BatterySlotTypeCode.VALUE),0 , false);__i++;
        sm[__i]=new  StructMember("batteries", false, (short)-1,  false,(TypeCode) new TypeCode(new int[] {8}, rapid.ext.arc.BatteryTypeCode.VALUE),1 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::arc::BatteryController",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

