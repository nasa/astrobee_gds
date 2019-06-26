
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  MotorProfileTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[5];

        sm[__i]=new  StructMember("ctrlMode", false, (short)-1,  false,(TypeCode) rapid.ext.arc.CtrlModeTypeCode.VALUE,0 , false);__i++;
        sm[__i]=new  StructMember("posMode", false, (short)-1,  false,(TypeCode) rapid.ext.arc.PositionModeTypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  StructMember("position", false, (short)-1,  false,(TypeCode) TypeCode.TC_DOUBLE,2 , false);__i++;
        sm[__i]=new  StructMember("speed", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,3 , false);__i++;
        sm[__i]=new  StructMember("acc", false, (short)-1,  false,(TypeCode) TypeCode.TC_FLOAT,4 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::arc::MotorProfile",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

