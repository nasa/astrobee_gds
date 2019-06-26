

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

public class NetTrafficInfoSample   implements Copyable, Serializable{

    public int rx= 0;
    public int tx= 0;
    public int rxErrors= 0;
    public int txErrors= 0;

    public NetTrafficInfoSample() {

    }
    public NetTrafficInfoSample (NetTrafficInfoSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        NetTrafficInfoSample self;
        self = new  NetTrafficInfoSample();
        self.clear();
        return self;

    }

    public void clear() {

        rx= 0;
        tx= 0;
        rxErrors= 0;
        txErrors= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        NetTrafficInfoSample otherObj = (NetTrafficInfoSample)o;

        if(rx != otherObj.rx) {
            return false;
        }
        if(tx != otherObj.tx) {
            return false;
        }
        if(rxErrors != otherObj.rxErrors) {
            return false;
        }
        if(txErrors != otherObj.txErrors) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)rx;
        __result += (int)tx;
        __result += (int)rxErrors;
        __result += (int)txErrors;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>NetTrafficInfoSampleTypeSupport</code>
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

        NetTrafficInfoSample typedSrc = (NetTrafficInfoSample) src;
        NetTrafficInfoSample typedDst = this;

        typedDst.rx = typedSrc.rx;
        typedDst.tx = typedSrc.tx;
        typedDst.rxErrors = typedSrc.rxErrors;
        typedDst.txErrors = typedSrc.txErrors;

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

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("rx: ").append(rx).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("tx: ").append(tx).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("rxErrors: ").append(rxErrors).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("txErrors: ").append(txErrors).append("\n");  

        return strBuffer.toString();
    }

}
