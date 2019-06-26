

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

/** Grab control of an agent */
/** Initialize bias. */
/** Command used to load a nodelet in the system. Doesn't work with nodes running on the HLP. */
/** Type of nodelet (namespace/classname). The type is specified in the system monitor config file so you may not need to specify this. */
/** Name of nodelet manager. This should be unnecessary since the system monitor should have received a heartbeat at startup from the node and the nodelet manager name is in the heartbeat. The system monitor saves it and should be able to use it to load nodelets. If commands fails, you may want to try to specify it. */
public class ADMIN_METHOD_LOAD_NODELET_DTYPE_MANAGER_NAME {    

    public static final rapid.DataType VALUE = (rapid.DataType.RAPID_STRING);
}
