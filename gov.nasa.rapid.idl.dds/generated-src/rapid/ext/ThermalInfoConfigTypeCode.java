
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.typecode.*;

public class  ThermalInfoConfigTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[5];

        sm[__i]=new  StructMember("location", false, (short)-1,  false,(TypeCode) rapid.String32TypeCode.VALUE,0 , false);__i++;
        sm[__i]=new  StructMember("temperatureLow", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,1 , false);__i++;
        sm[__i]=new  StructMember("temperatureHigh", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,2 , false);__i++;
        sm[__i]=new  StructMember("temperatureLowCritical", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,3 , false);__i++;
        sm[__i]=new  StructMember("temperatureHighCritical", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,4 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::ThermalInfoConfig",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

