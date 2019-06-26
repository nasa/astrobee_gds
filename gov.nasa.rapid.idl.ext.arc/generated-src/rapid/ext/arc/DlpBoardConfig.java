

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

public class DlpBoardConfig   implements Copyable, Serializable{

    public String name=  "" ; /* maximum length = (32) */
    public rapid.ext.arc.DlpChannelConfig [] channels=  new rapid.ext.arc.DlpChannelConfig [3];
    public rapid.ext.arc.DlpWarnCriticalRange tempRange = (rapid.ext.arc.DlpWarnCriticalRange)rapid.ext.arc.DlpWarnCriticalRange.create();

    public DlpBoardConfig() {

        for(int i1__ = 0; i1__< 3; ++i1__){

            channels[i1__]= (rapid.ext.arc.DlpChannelConfig) rapid.ext.arc.DlpChannelConfig.create();
        }

    }
    public DlpBoardConfig (DlpBoardConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        DlpBoardConfig self;
        self = new  DlpBoardConfig();
        self.clear();
        return self;

    }

    public void clear() {

        name=  ""; 
        for(int i1__ = 0; i1__< 3; ++i1__){

            if ( channels[i1__] != null) {
                channels[i1__].clear();
            }
        }

        if (tempRange != null) {
            tempRange.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        DlpBoardConfig otherObj = (DlpBoardConfig)o;

        if(!name.equals(otherObj.name)) {
            return false;
        }
        for(int i1__ = 0; i1__< 3; ++i1__){

            if(!channels[i1__].equals(otherObj.channels[i1__])) {
                return false;
            }
        }

        if(!tempRange.equals(otherObj.tempRange)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += name.hashCode(); 
        for(int i1__ = 0; i1__< 3; ++i1__){

            __result += channels[i1__].hashCode(); 
        }

        __result += tempRange.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>DlpBoardConfigTypeSupport</code>
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

        DlpBoardConfig typedSrc = (DlpBoardConfig) src;
        DlpBoardConfig typedDst = this;

        typedDst.name = typedSrc.name;
        for(int i1__ = 0; i1__< 3; ++i1__){

            typedDst.channels[i1__] = (rapid.ext.arc.DlpChannelConfig) typedDst.channels[i1__].copy_from(typedSrc.channels[i1__]);
        }

        typedDst.tempRange = (rapid.ext.arc.DlpWarnCriticalRange) typedDst.tempRange.copy_from(typedSrc.tempRange);

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
        strBuffer.append("name: ").append(name).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("channels:\n");
        for(int i1__ = 0; i1__< 3; ++i1__){

            strBuffer.append(channels[i1__].toString(
                "["+Integer.toString(i1__)+"]",indent+2));
        }

        strBuffer.append(tempRange.toString("tempRange ", indent+1));

        return strBuffer.toString();
    }

}
