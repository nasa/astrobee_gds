
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.typecode.*;

public class  FaultInfoTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[5];

        sm[__i]=new  StructMember("subsystem", false, (short)-1,  false,(TypeCode) TypeCode.TC_SHORT,0 , false);__i++;
        sm[__i]=new  StructMember("node", false, (short)-1,  false,(TypeCode) TypeCode.TC_SHORT,1 , false);__i++;
        sm[__i]=new  StructMember("faultId", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONG,2 , false);__i++;
        sm[__i]=new  StructMember("warning", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,3 , false);__i++;
        sm[__i]=new  StructMember("faultDescription", false, (short)-1,  false,(TypeCode) rapid.String64TypeCode.VALUE,4 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::astrobee::FaultInfo",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

