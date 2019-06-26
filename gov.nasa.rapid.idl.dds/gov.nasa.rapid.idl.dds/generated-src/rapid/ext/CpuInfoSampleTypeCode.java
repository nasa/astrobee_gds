
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.typecode.*;

public class  CpuInfoSampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[5];

        sm[__i]=new  StructMember("busy", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,0 , false);__i++;
        sm[__i]=new  StructMember("idle", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,1 , false);__i++;
        sm[__i]=new  StructMember("nice", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,2 , false);__i++;
        sm[__i]=new  StructMember("system", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,3 , false);__i++;
        sm[__i]=new  StructMember("user", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,4 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::CpuInfoSample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

