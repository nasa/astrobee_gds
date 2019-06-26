
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.typecode.*;

public class  ArmStateTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[2];

        sm[__i]=new  ValueMember("jointState", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.astrobee.ArmJointStateTypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("gripperState", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.astrobee.ArmGripperStateTypeCode.VALUE,2 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::astrobee::ArmState",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

