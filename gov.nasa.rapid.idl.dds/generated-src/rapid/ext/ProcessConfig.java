

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

/**
* ProcessConfig
*/

public class ProcessConfig   implements Copyable, Serializable{

    public String name=  "" ; /* maximum length = (64) */
    public String comment=  "" ; /* maximum length = (64) */
    public rapid.String256Sequence64 environment = (rapid.String256Sequence64)rapid.String256Sequence64.create();
    public String workingDirectory=  "" ; /* maximum length = (256) */
    public String binaryName=  "" ; /* maximum length = (32) */
    public String commandLineParams=  "" ; /* maximum length = (256) */
    public boolean selfTerminating= false;
    public boolean startOnInit= false;
    public boolean waitOnInit= false;
    public int startupTimeout= 0;
    public String runningMatch=  "" ; /* maximum length = (256) */
    public String aliveInterface=  "" ; /* maximum length = (32) */
    /** Number of times of automatic restart on unexpected shutdown */
    public int restartsOnFailure= 0;

    public ProcessConfig() {

        /** Number of times of automatic restart on unexpected shutdown */

    }
    public ProcessConfig (ProcessConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ProcessConfig self;
        self = new  ProcessConfig();
        self.clear();
        return self;

    }

    public void clear() {

        name=  ""; 
        comment=  ""; 
        if (environment != null) {
            environment.clear();
        }
        workingDirectory=  ""; 
        binaryName=  ""; 
        commandLineParams=  ""; 
        selfTerminating= false;
        startOnInit= false;
        waitOnInit= false;
        startupTimeout= 0;
        runningMatch=  ""; 
        aliveInterface=  ""; 
        /** Number of times of automatic restart on unexpected shutdown */
        restartsOnFailure= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        ProcessConfig otherObj = (ProcessConfig)o;

        if(!name.equals(otherObj.name)) {
            return false;
        }
        if(!comment.equals(otherObj.comment)) {
            return false;
        }
        if(!environment.equals(otherObj.environment)) {
            return false;
        }
        if(!workingDirectory.equals(otherObj.workingDirectory)) {
            return false;
        }
        if(!binaryName.equals(otherObj.binaryName)) {
            return false;
        }
        if(!commandLineParams.equals(otherObj.commandLineParams)) {
            return false;
        }
        if(selfTerminating != otherObj.selfTerminating) {
            return false;
        }
        if(startOnInit != otherObj.startOnInit) {
            return false;
        }
        if(waitOnInit != otherObj.waitOnInit) {
            return false;
        }
        if(startupTimeout != otherObj.startupTimeout) {
            return false;
        }
        if(!runningMatch.equals(otherObj.runningMatch)) {
            return false;
        }
        if(!aliveInterface.equals(otherObj.aliveInterface)) {
            return false;
        }
        /** Number of times of automatic restart on unexpected shutdown */
        if(restartsOnFailure != otherObj.restartsOnFailure) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += name.hashCode(); 
        __result += comment.hashCode(); 
        __result += environment.hashCode(); 
        __result += workingDirectory.hashCode(); 
        __result += binaryName.hashCode(); 
        __result += commandLineParams.hashCode(); 
        __result += (selfTerminating == true)?1:0;
        __result += (startOnInit == true)?1:0;
        __result += (waitOnInit == true)?1:0;
        __result += (int)startupTimeout;
        __result += runningMatch.hashCode(); 
        __result += aliveInterface.hashCode(); 
        /** Number of times of automatic restart on unexpected shutdown */
        __result += (int)restartsOnFailure;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ProcessConfigTypeSupport</code>
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

        ProcessConfig typedSrc = (ProcessConfig) src;
        ProcessConfig typedDst = this;

        typedDst.name = typedSrc.name;
        typedDst.comment = typedSrc.comment;
        typedDst.environment = (rapid.String256Sequence64) typedDst.environment.copy_from(typedSrc.environment);
        typedDst.workingDirectory = typedSrc.workingDirectory;
        typedDst.binaryName = typedSrc.binaryName;
        typedDst.commandLineParams = typedSrc.commandLineParams;
        typedDst.selfTerminating = typedSrc.selfTerminating;
        typedDst.startOnInit = typedSrc.startOnInit;
        typedDst.waitOnInit = typedSrc.waitOnInit;
        typedDst.startupTimeout = typedSrc.startupTimeout;
        typedDst.runningMatch = typedSrc.runningMatch;
        typedDst.aliveInterface = typedSrc.aliveInterface;
        /** Number of times of automatic restart on unexpected shutdown */
        typedDst.restartsOnFailure = typedSrc.restartsOnFailure;

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
        strBuffer.append("comment: ").append(comment).append("\n");  
        strBuffer.append(environment.toString("environment ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("workingDirectory: ").append(workingDirectory).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("binaryName: ").append(binaryName).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("commandLineParams: ").append(commandLineParams).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("selfTerminating: ").append(selfTerminating).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("startOnInit: ").append(startOnInit).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("waitOnInit: ").append(waitOnInit).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("startupTimeout: ").append(startupTimeout).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("runningMatch: ").append(runningMatch).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("aliveInterface: ").append(aliveInterface).append("\n");  
        /** Number of times of automatic restart on unexpected shutdown */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("restartsOnFailure: ").append(restartsOnFailure).append("\n");  

        return strBuffer.toString();
    }

}
