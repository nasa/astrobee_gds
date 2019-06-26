

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class ProcessIoSample  extends rapid.Message implements Copyable, Serializable{

    public int processIdx= 0;
    public rapid.ext.ProcessIoStream stream = (rapid.ext.ProcessIoStream)rapid.ext.ProcessIoStream.create();
    public int lineNumber= 0;
    public String line=  "" ; /* maximum length = (1024) */

    public ProcessIoSample() {

        super();

    }
    public ProcessIoSample (ProcessIoSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ProcessIoSample self;
        self = new  ProcessIoSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        processIdx= 0;
        stream = rapid.ext.ProcessIoStream.create();
        lineNumber= 0;
        line=  ""; 
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if (!super.equals(o)) {
            return false;
        }

        if(getClass() != o.getClass()) {
            return false;
        }

        ProcessIoSample otherObj = (ProcessIoSample)o;

        if(processIdx != otherObj.processIdx) {
            return false;
        }
        if(!stream.equals(otherObj.stream)) {
            return false;
        }
        if(lineNumber != otherObj.lineNumber) {
            return false;
        }
        if(!line.equals(otherObj.line)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += (int)processIdx;
        __result += stream.hashCode(); 
        __result += (int)lineNumber;
        __result += line.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ProcessIoSampleTypeSupport</code>
    * rather than here by using the <code>-noCopyable</code> option
    * to rtiddsgen.
    * 
    * @param src The Object which contains the data to be copied.
    * @return Returns <code>this</code>.
    * @exception NullPointerException If <code>src</code> is null.
    * @exception ClassCastException If <code>src</code> is not the 
    * same type as <code>this</code>.
    * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
    */
    public Object copy_from(Object src) {

        ProcessIoSample typedSrc = (ProcessIoSample) src;
        ProcessIoSample typedDst = this;
        super.copy_from(typedSrc);
        typedDst.processIdx = typedSrc.processIdx;
        typedDst.stream = (rapid.ext.ProcessIoStream) typedDst.stream.copy_from(typedSrc.stream);
        typedDst.lineNumber = typedSrc.lineNumber;
        typedDst.line = typedSrc.line;

        return this;
    }

    public String toString(){
        return toString("", 0);
    }

    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        

        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }

        strBuffer.append(super.toString("",indent));

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("processIdx: ").append(processIdx).append("\n");  
        strBuffer.append(stream.toString("stream ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("lineNumber: ").append(lineNumber).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("line: ").append(line).append("\n");  

        return strBuffer.toString();
    }

}
