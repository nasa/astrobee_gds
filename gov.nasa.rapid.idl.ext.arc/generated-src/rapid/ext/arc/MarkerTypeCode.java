
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  MarkerTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[14];

        sm[__i]=new  StructMember("ns", false, (short)-1,  false,(TypeCode) rapid.String32TypeCode.VALUE,0 , false);__i++;
        sm[__i]=new  StructMember("id", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONG,1 , false);__i++;
        sm[__i]=new  StructMember("type", false, (short)-1,  false,(TypeCode) rapid.ext.arc.ShapeTypeTypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  StructMember("action", false, (short)-1,  false,(TypeCode) rapid.ext.arc.ActionTypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  StructMember("pose", false, (short)-1,  false,(TypeCode) rapid.Transform3DTypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  StructMember("scale", false, (short)-1,  false,(TypeCode) rapid.Vec3fTypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  StructMember("color", false, (short)-1,  false,(TypeCode) rapid.Color4fTypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  StructMember("lifetime", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONGLONG,7 , false);__i++;
        sm[__i]=new  StructMember("frame_locked", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,8 , false);__i++;
        sm[__i]=new  StructMember("points", false, (short)-1,  false,(TypeCode) rapid.ext.arc.PointSequence1KTypeCode.VALUE,9 , false);__i++;
        sm[__i]=new  StructMember("colors", false, (short)-1,  false,(TypeCode) rapid.ext.arc.ColorSequence1KTypeCode.VALUE,10 , false);__i++;
        sm[__i]=new  StructMember("text", false, (short)-1,  false,(TypeCode) rapid.String32TypeCode.VALUE,11 , false);__i++;
        sm[__i]=new  StructMember("mesh_resource", false, (short)-1,  false,(TypeCode) rapid.String32TypeCode.VALUE,12 , false);__i++;
        sm[__i]=new  StructMember("mesh_use_embedded_materials", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,13 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::arc::Marker",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

