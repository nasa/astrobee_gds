
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.typecode.*;

public class  FileQueueEntryStateTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        ValueMember sm[]=new ValueMember[7];

        sm[__i]=new  ValueMember("fileUuid", false, (short)-1, true,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.String64TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  ValueMember("status", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) rapid.FileTransferStatusTypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  ValueMember("chunksSent", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONG,3 , false);__i++;
        sm[__i]=new  ValueMember("numChunks", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONG,4 , false);__i++;
        sm[__i]=new  ValueMember("submissionTime", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_LONGLONG,5 , false);__i++;
        sm[__i]=new  ValueMember("priority", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_FLOAT,6 , false);__i++;
        sm[__i]=new  ValueMember("channelId", false, (short)-1,  false,PUBLIC_MEMBER.VALUE,(TypeCode) TypeCode.TC_SHORT,7 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_value_tc("rapid::FileQueueEntryState",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY, VM_NONE.VALUE,rapid.MessageTypeCode.VALUE, sm);        
        return tc;
    }
}

