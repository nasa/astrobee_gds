

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* QueueAction
* <ul>
*   <li> BYPASS: the cmd is acted upon immediately.
*   <li> APPEND: the cmd is appended to the tail of the Pending queue. This is the normal operation for commanding.
*   <li> DELETE: the cmd with the ID specified in targetCmdID is removed from the queue.
*   <li> INSERT: the cmd is inserted immediately following the cmd with the ID specified in targetCmdID.
*		      If targetCmdID is "head", then the cmd is inserted at the head of the queue.
*   <li> REPLACE: cmd replaces the ID specified in targetCmdID. This cmd will have a new ID
* <ul>
*/
/**
* Command is a message used to send directives to an Agent.
*/

public class Command  extends rapid.Message implements Copyable, Serializable{

    /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
    public String cmdName=  "" ; /* maximum length = (64) */
    /** Unique identifier for command, = username+timestamp. */
    public String cmdId=  "" ; /* maximum length = (64) */
    /** Source that generated the command. */
    public String cmdSrc=  "" ; /* maximum length = (32) */
    /** Subsystem name if command is being sent to subsystem of an Agent. */
    public String subsysName=  "" ; /* maximum length = (32) */
    /** Value of the arguments for this particular command. */
    public rapid.ParameterSequence16 arguments = (rapid.ParameterSequence16)rapid.ParameterSequence16.create();
    /** For queue manipulation. Normally "QUEUE_APPEND". */
    public rapid.QueueAction cmdAction = (rapid.QueueAction)rapid.QueueAction.create();
    /**
    * targetCmdId:
    * <ul>
    *   <li> if QUEUE_BYPASS, targetCmdId is not used
    *   <li> if QUEUE_APPEND, targetCmdId is not used
    *   <li> if QUEUE_DELETE, targetCmdId identifies the cmdId of the cmd to be removed
    *   <li> if QUEUE_INSERT, targetCmdId identifies the cmdId of the cmd immediately preceding the target slot. If "head", then the head of the queue.
    *   <li> if QUEUE_REPLACE, targetCmdId identifies the cmdId of the cmd to be replaced
    * <ul>
    */
    public String targetCmdId=  "" ; /* maximum length = (64) */

    public Command() {

        super();

        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        /** Unique identifier for command, = username+timestamp. */
        /** Source that generated the command. */
        /** Subsystem name if command is being sent to subsystem of an Agent. */
        /** Value of the arguments for this particular command. */
        /** For queue manipulation. Normally "QUEUE_APPEND". */
        /**
        * targetCmdId:
        * <ul>
        *   <li> if QUEUE_BYPASS, targetCmdId is not used
        *   <li> if QUEUE_APPEND, targetCmdId is not used
        *   <li> if QUEUE_DELETE, targetCmdId identifies the cmdId of the cmd to be removed
        *   <li> if QUEUE_INSERT, targetCmdId identifies the cmdId of the cmd immediately preceding the target slot. If "head", then the head of the queue.
        *   <li> if QUEUE_REPLACE, targetCmdId identifies the cmdId of the cmd to be replaced
        * <ul>
        */

    }
    public Command (Command other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Command self;
        self = new  Command();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        cmdName=  ""; 
        /** Unique identifier for command, = username+timestamp. */
        cmdId=  ""; 
        /** Source that generated the command. */
        cmdSrc=  ""; 
        /** Subsystem name if command is being sent to subsystem of an Agent. */
        subsysName=  ""; 
        /** Value of the arguments for this particular command. */
        if (arguments != null) {
            arguments.clear();
        }
        /** For queue manipulation. Normally "QUEUE_APPEND". */
        cmdAction = rapid.QueueAction.create();
        /**
        * targetCmdId:
        * <ul>
        *   <li> if QUEUE_BYPASS, targetCmdId is not used
        *   <li> if QUEUE_APPEND, targetCmdId is not used
        *   <li> if QUEUE_DELETE, targetCmdId identifies the cmdId of the cmd to be removed
        *   <li> if QUEUE_INSERT, targetCmdId identifies the cmdId of the cmd immediately preceding the target slot. If "head", then the head of the queue.
        *   <li> if QUEUE_REPLACE, targetCmdId identifies the cmdId of the cmd to be replaced
        * <ul>
        */
        targetCmdId=  ""; 
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

        Command otherObj = (Command)o;

        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        if(!cmdName.equals(otherObj.cmdName)) {
            return false;
        }
        /** Unique identifier for command, = username+timestamp. */
        if(!cmdId.equals(otherObj.cmdId)) {
            return false;
        }
        /** Source that generated the command. */
        if(!cmdSrc.equals(otherObj.cmdSrc)) {
            return false;
        }
        /** Subsystem name if command is being sent to subsystem of an Agent. */
        if(!subsysName.equals(otherObj.subsysName)) {
            return false;
        }
        /** Value of the arguments for this particular command. */
        if(!arguments.equals(otherObj.arguments)) {
            return false;
        }
        /** For queue manipulation. Normally "QUEUE_APPEND". */
        if(!cmdAction.equals(otherObj.cmdAction)) {
            return false;
        }
        /**
        * targetCmdId:
        * <ul>
        *   <li> if QUEUE_BYPASS, targetCmdId is not used
        *   <li> if QUEUE_APPEND, targetCmdId is not used
        *   <li> if QUEUE_DELETE, targetCmdId identifies the cmdId of the cmd to be removed
        *   <li> if QUEUE_INSERT, targetCmdId identifies the cmdId of the cmd immediately preceding the target slot. If "head", then the head of the queue.
        *   <li> if QUEUE_REPLACE, targetCmdId identifies the cmdId of the cmd to be replaced
        * <ul>
        */
        if(!targetCmdId.equals(otherObj.targetCmdId)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        __result += cmdName.hashCode(); 
        /** Unique identifier for command, = username+timestamp. */
        __result += cmdId.hashCode(); 
        /** Source that generated the command. */
        __result += cmdSrc.hashCode(); 
        /** Subsystem name if command is being sent to subsystem of an Agent. */
        __result += subsysName.hashCode(); 
        /** Value of the arguments for this particular command. */
        __result += arguments.hashCode(); 
        /** For queue manipulation. Normally "QUEUE_APPEND". */
        __result += cmdAction.hashCode(); 
        /**
        * targetCmdId:
        * <ul>
        *   <li> if QUEUE_BYPASS, targetCmdId is not used
        *   <li> if QUEUE_APPEND, targetCmdId is not used
        *   <li> if QUEUE_DELETE, targetCmdId identifies the cmdId of the cmd to be removed
        *   <li> if QUEUE_INSERT, targetCmdId identifies the cmdId of the cmd immediately preceding the target slot. If "head", then the head of the queue.
        *   <li> if QUEUE_REPLACE, targetCmdId identifies the cmdId of the cmd to be replaced
        * <ul>
        */
        __result += targetCmdId.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CommandTypeSupport</code>
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

        Command typedSrc = (Command) src;
        Command typedDst = this;
        super.copy_from(typedSrc);
        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        typedDst.cmdName = typedSrc.cmdName;
        /** Unique identifier for command, = username+timestamp. */
        typedDst.cmdId = typedSrc.cmdId;
        /** Source that generated the command. */
        typedDst.cmdSrc = typedSrc.cmdSrc;
        /** Subsystem name if command is being sent to subsystem of an Agent. */
        typedDst.subsysName = typedSrc.subsysName;
        /** Value of the arguments for this particular command. */
        typedDst.arguments = (rapid.ParameterSequence16) typedDst.arguments.copy_from(typedSrc.arguments);
        /** For queue manipulation. Normally "QUEUE_APPEND". */
        typedDst.cmdAction = (rapid.QueueAction) typedDst.cmdAction.copy_from(typedSrc.cmdAction);
        /**
        * targetCmdId:
        * <ul>
        *   <li> if QUEUE_BYPASS, targetCmdId is not used
        *   <li> if QUEUE_APPEND, targetCmdId is not used
        *   <li> if QUEUE_DELETE, targetCmdId identifies the cmdId of the cmd to be removed
        *   <li> if QUEUE_INSERT, targetCmdId identifies the cmdId of the cmd immediately preceding the target slot. If "head", then the head of the queue.
        *   <li> if QUEUE_REPLACE, targetCmdId identifies the cmdId of the cmd to be replaced
        * <ul>
        */
        typedDst.targetCmdId = typedSrc.targetCmdId;

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

        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("cmdName: ").append(cmdName).append("\n");  
        /** Unique identifier for command, = username+timestamp. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("cmdId: ").append(cmdId).append("\n");  
        /** Source that generated the command. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("cmdSrc: ").append(cmdSrc).append("\n");  
        /** Subsystem name if command is being sent to subsystem of an Agent. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("subsysName: ").append(subsysName).append("\n");  
        /** Value of the arguments for this particular command. */
        strBuffer.append(arguments.toString("arguments ", indent+1));
        /** For queue manipulation. Normally "QUEUE_APPEND". */
        strBuffer.append(cmdAction.toString("cmdAction ", indent+1));
        /**
        * targetCmdId:
        * <ul>
        *   <li> if QUEUE_BYPASS, targetCmdId is not used
        *   <li> if QUEUE_APPEND, targetCmdId is not used
        *   <li> if QUEUE_DELETE, targetCmdId identifies the cmdId of the cmd to be removed
        *   <li> if QUEUE_INSERT, targetCmdId identifies the cmdId of the cmd immediately preceding the target slot. If "head", then the head of the queue.
        *   <li> if QUEUE_REPLACE, targetCmdId identifies the cmdId of the cmd to be replaced
        * <ul>
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetCmdId: ").append(targetCmdId).append("\n");  

        return strBuffer.toString();
    }

}
