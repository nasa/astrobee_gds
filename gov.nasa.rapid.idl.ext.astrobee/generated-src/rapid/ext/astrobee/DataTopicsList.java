

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* Lists rostopics available to save to disk.
*/

public class DataTopicsList  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.astrobee.String128Sequence256 topics = (rapid.ext.astrobee.String128Sequence256)rapid.ext.astrobee.String128Sequence256.create();

    public DataTopicsList() {

        super();

    }
    public DataTopicsList (DataTopicsList other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        DataTopicsList self;
        self = new  DataTopicsList();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (topics != null) {
            topics.clear();
        }
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

        DataTopicsList otherObj = (DataTopicsList)o;

        if(!topics.equals(otherObj.topics)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += topics.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>DataTopicsListTypeSupport</code>
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

        DataTopicsList typedSrc = (DataTopicsList) src;
        DataTopicsList typedDst = this;
        super.copy_from(typedSrc);
        typedDst.topics = (rapid.ext.astrobee.String128Sequence256) typedDst.topics.copy_from(typedSrc.topics);

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

        strBuffer.append(topics.toString("topics ", indent+1));

        return strBuffer.toString();
    }

}
