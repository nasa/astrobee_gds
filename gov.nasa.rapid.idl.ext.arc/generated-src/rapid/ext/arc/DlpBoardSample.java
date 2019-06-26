

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class DlpBoardSample   implements Copyable, Serializable{

    public rapid.ext.arc.DlpChannelSample [] channels=  new rapid.ext.arc.DlpChannelSample [3];
    public float temp= 0;

    public DlpBoardSample() {

        for(int i1__ = 0; i1__< 3; ++i1__){

            channels[i1__]= (rapid.ext.arc.DlpChannelSample) rapid.ext.arc.DlpChannelSample.create();
        }

    }
    public DlpBoardSample (DlpBoardSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        DlpBoardSample self;
        self = new  DlpBoardSample();
        self.clear();
        return self;

    }

    public void clear() {

        for(int i1__ = 0; i1__< 3; ++i1__){

            if ( channels[i1__] != null) {
                channels[i1__].clear();
            }
        }

        temp= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        DlpBoardSample otherObj = (DlpBoardSample)o;

        for(int i1__ = 0; i1__< 3; ++i1__){

            if(!channels[i1__].equals(otherObj.channels[i1__])) {
                return false;
            }
        }

        if(temp != otherObj.temp) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        for(int i1__ = 0; i1__< 3; ++i1__){

            __result += channels[i1__].hashCode(); 
        }

        __result += (int)temp;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>DlpBoardSampleTypeSupport</code>
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

        DlpBoardSample typedSrc = (DlpBoardSample) src;
        DlpBoardSample typedDst = this;

        for(int i1__ = 0; i1__< 3; ++i1__){

            typedDst.channels[i1__] = (rapid.ext.arc.DlpChannelSample) typedDst.channels[i1__].copy_from(typedSrc.channels[i1__]);
        }

        typedDst.temp = typedSrc.temp;

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
        strBuffer.append("channels:\n");
        for(int i1__ = 0; i1__< 3; ++i1__){

            strBuffer.append(channels[i1__].toString(
                "["+Integer.toString(i1__)+"]",indent+2));
        }

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("temp: ").append(temp).append("\n");  

        return strBuffer.toString();
    }

}
