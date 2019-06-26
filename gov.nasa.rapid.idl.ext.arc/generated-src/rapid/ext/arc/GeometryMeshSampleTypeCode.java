
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.typecode.*;

public class  GeometryMeshSampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[12];

        sm[__i]=new  ValueMember("geometryId", false, (short)-1, true,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONG,1 , false);__i++;
        sm[__i]=new  ValueMember("indexModes", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.arc.GeometryIndexModeSequence32TypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("indexLengths", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.LongSequence32TypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  ValueMember("indexData", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.arc.IndexSequence256KTypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  ValueMember("vertexScale", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,5 , false);__i++;
        sm[__i]=new  ValueMember("vertexData", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ShortSequence128KTypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  ValueMember("normalScale", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,7 , false);__i++;
        sm[__i]=new  ValueMember("normalData", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.OctetSequence128KTypeCode.VALUE,8 , false);__i++;
        sm[__i]=new  ValueMember("colorScale", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,9 , false);__i++;
        sm[__i]=new  ValueMember("colorData", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ext.arc.OctetSequence170KTypeCode.VALUE,10 , false);__i++;
        sm[__i]=new  ValueMember("texCoord0Scale", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,11 , false);__i++;
        sm[__i]=new  ValueMember("texCoord0Data", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ShortSequence96KTypeCode.VALUE,12 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ext::arc::GeometryMeshSample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

