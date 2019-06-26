
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.typecode.*;

public class  CameraInfoConfigTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[4];

        sm[__i]=new  StructMember("name", false, (short)-1,  false,(TypeCode) rapid.String32TypeCode.VALUE,0 , false);__i++;
        sm[__i]=new  StructMember("availResolutions", false, (short)-1,  false,(TypeCode) rapid.ext.astrobee.CameraResolutionSequence8TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  StructMember("mode", false, (short)-1,  false,(TypeCode) rapid.ext.astrobee.CameraModeTypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  StructMember("maxFrameRate", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,3 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::astrobee::CameraInfoConfig",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

