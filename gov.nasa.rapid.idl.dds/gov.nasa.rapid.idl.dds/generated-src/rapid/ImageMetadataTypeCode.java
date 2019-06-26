
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.typecode.*;

public class  ImageMetadataTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[8];

        sm[__i]=new  StructMember("sensorName", false, (short)-1,  false,(TypeCode) rapid.String32TypeCode.VALUE,0 , false);__i++;
        sm[__i]=new  StructMember("sensorType", false, (short)-1,  false,(TypeCode) rapid.String32TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  StructMember("offset", false, (short)-1,  false,(TypeCode) rapid.Transform3DTypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  StructMember("width", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONG,3 , false);__i++;
        sm[__i]=new  StructMember("height", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONG,4 , false);__i++;
        sm[__i]=new  StructMember("rangeSettings", false, (short)-1,  false,(TypeCode) rapid.NamedFloatRangeValueSequence16TypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  StructMember("optionSettings", false, (short)-1,  false,(TypeCode) rapid.NamedOptionSetValueSequence16TypeCode.VALUE,6 , false);__i++;
        sm[__i]=new  StructMember("extras", false, (short)-1,  false,(TypeCode) rapid.KeyTypeValueSequence16TypeCode.VALUE,7 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ImageMetadata",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

