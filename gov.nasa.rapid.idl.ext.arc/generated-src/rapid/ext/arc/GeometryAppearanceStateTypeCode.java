
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  GeometryAppearanceStateTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[9];

        sm[__i]=new  ValueMember("geometryId", false, (short)-1, true,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONG,1 , false);__i++;
        sm[__i]=new  ValueMember("hasMaterial", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,2 , false);__i++;
        sm[__i]=new  ValueMember("diffuse", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Color4fTypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("ambient", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Color4fTypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  ValueMember("specular", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Color4fTypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  ValueMember("emissive", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.Color4fTypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  ValueMember("shininess", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,7 , false);__i++;
        sm[__i]=new  ValueMember("hasTexture", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_BOOLEAN,8 , false);__i++;
        sm[__i]=new  ValueMember("texImage0Url", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String128TypeCode.VALUE,9 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::arc::GeometryAppearanceState",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

