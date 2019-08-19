

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
/** Can be left blank. */
/** Reset ekf. */
/** This command is used to switch between localization pipelines. */
/** Specify which pipeline to switch to. */
/** Command used to unload a nodelet in the system. Doesn't work with nodes running on the HLP. With great power comes great responsibility! Don't unload a nodelet crucial to the system!! */
/** This should be unnecessary since the system monitor should have received a heartbeat at startup from the node and the heartbeat contains thenodelet manager name. If the command fails, you may want to try to specify it. */
/** This command is used to unterminate the robot. It will only reset the terminate flag but will not start up the pmcs or repower the payloads. */
/** This command wakes astrobee from a hibernated state into a nominal state. */
/** This command wakes astrobee from a hibernated state into a safe state. */
/** Erases everything on the hlp. */
/** Move arm while perched to control camera angle */
/** Whether to perform a pan, tilt, or both. */
/** Open or close gripper */
/** Clear data */
/** Start downloading data */
/** Set data-to-disk configuration to be the data-to-disk file most recently uplinked; the file specifies which data to save to free flyer onboard storage, and at what rates */
/** Starts the recording of the topics configured with the set data to disk command. */
/** Stop downloading data */
/** Stops the recording of the topics configured with the set data to disk command. */

public class GUESTSCIENCE {    

    public static final String VALUE = "GuestScience";
}
