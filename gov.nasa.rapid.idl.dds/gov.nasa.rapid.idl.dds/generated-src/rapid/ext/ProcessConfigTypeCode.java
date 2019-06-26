
/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.typecode.*;

/**
* ProcessConfig
*/

public class  ProcessConfigTypeCode {
    public static final TypeCode VALUE = getTypeCode();

    private static TypeCode getTypeCode() {
        TypeCode tc = null;
        int __i=0;
        StructMember sm[]=new StructMember[13];

        sm[__i]=new  StructMember("name", false, (short)-1,  false,(TypeCode) rapid.String64TypeCode.VALUE,0 , false);__i++;
        sm[__i]=new  StructMember("comment", false, (short)-1,  false,(TypeCode) rapid.String64TypeCode.VALUE,1 , false);__i++;
        sm[__i]=new  StructMember("environment", false, (short)-1,  false,(TypeCode) rapid.String256Sequence64TypeCode.VALUE,2 , false);__i++;
        sm[__i]=new  StructMember("workingDirectory", false, (short)-1,  false,(TypeCode) rapid.String256TypeCode.VALUE,3 , false);__i++;
        sm[__i]=new  StructMember("binaryName", false, (short)-1,  false,(TypeCode) rapid.String32TypeCode.VALUE,4 , false);__i++;
        sm[__i]=new  StructMember("commandLineParams", false, (short)-1,  false,(TypeCode) rapid.String256TypeCode.VALUE,5 , false);__i++;
        sm[__i]=new  StructMember("selfTerminating", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,6 , false);__i++;
        sm[__i]=new  StructMember("startOnInit", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,7 , false);__i++;
        sm[__i]=new  StructMember("waitOnInit", false, (short)-1,  false,(TypeCode) TypeCode.TC_BOOLEAN,8 , false);__i++;
        sm[__i]=new  StructMember("startupTimeout", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONG,9 , false);__i++;
        sm[__i]=new  StructMember("runningMatch", false, (short)-1,  false,(TypeCode) rapid.String256TypeCode.VALUE,10 , false);__i++;
        sm[__i]=new  StructMember("aliveInterface", false, (short)-1,  false,(TypeCode) rapid.String32TypeCode.VALUE,11 , false);__i++;
        sm[__i]=new  StructMember("restartsOnFailure", false, (short)-1,  false,(TypeCode) TypeCode.TC_LONG,12 , false);__i++;

        tc = TypeCodeFactory.TheTypeCodeFactory.create_struct_tc("rapid::ext::ProcessConfig",ExtensibilityKind.EXTENSIBLE_EXTENSIBILITY,  sm);        
        return tc;
    }
}

