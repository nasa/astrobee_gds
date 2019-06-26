
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.typecode.*;

public class  ImageSensorSampleTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[3];

        sm[__i]=new  ValueMember("meta", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ImageMetadataTypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("mimeType", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String32TypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("data", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.ImageDataTypeCode.VALUE,3 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::ImageSensorSample",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

