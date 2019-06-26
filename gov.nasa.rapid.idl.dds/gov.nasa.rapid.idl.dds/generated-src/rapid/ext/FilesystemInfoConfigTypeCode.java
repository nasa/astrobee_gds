
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.typecode.*;

public class  FilesystemInfoConfigTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[4];

        sm[__i]=new  StructMember("capacity", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONGLONG,0 , false);__i++;
        sm[__i]=new  StructMember("name", false, (short)-1,  false,(TypeCode) rapid.String64TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  StructMember("availableLow", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONGLONG,2 , false);__i++;
        sm[__i]=new  StructMember("availableCritical", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONGLONG,3 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::FilesystemInfoConfig",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

