# Debugging, Advanced, and Advanced 2 tabs

The Debugging, Advanced, and Advanced 2 tabs have subtabs that display detailed debugging information and allow
engineers to send low-level commands.

## Debugging Tab

### Config Commander

The <b>Config Commander subtab</b> automatically generates widgets to send any command listed in the CommandConfig
message sent by the Astrobee. Select the Astrobee, and then select the subsystem to see the command widgets displayed.
There is no error checking for commands sent through the Config Commander.

### Discovered DDS Info

The <b>Discovered DDS Info subtab</b> lists raw DDS information to help debug DDS connections. Click Topics to list the
topics published in each partition on the Discovered Partitions panel. Click Refresh to refresh the information (note, 
refreshing will add new information but it won't delete stale information).

### Received Topics

Click the arrow to the right of the participant/partition to see the list of topics it publishes. Check the checkbox to 
the left of the topic to see the latest message on that topic in the Raw Rapid Telemetry subtab. Click Refresh to 
refresh the list of topics.

### Raw Rapid Telemetry

This subtab displays the latest messages on the topics that are checked in the Received Topics subtab.

## Advanced Tab

### Health subtab

This subtab lists the state information published by Astrobee. Note that Raw Mobility State "Docking" with Sub Mobility
State "0" is "Docked", and Raw Mobility State "Perching" with Sub Mobility State "0" is "Perched".  That is to say,
there are no states named "Docked" or "Perched", only "Docking 0" and "Perching 0".

### Faults subtab

The Faults subtab lists all of the faults that Astrobee publishes in its FaultConfig. When the robot throws a fault, 
that fault is listed under "Triggered" in the table.

### Data to GDS 2
Use this subtab to set the frequency at which Astrobee sends telemetry to the Control Station. You can also
use this tab to configure the cameras on Astrobee, and start or stop them from streaming images. (The Control 
Station is also called "GDS", for "Ground Data System".)

### Power State

This subtab displays the information from EPS message.

### Standard Controls and Operating Limits

Use this subtab to Grab Control or Stop (same as Station Keep on the other tabs) the Astrobee.  The Send Zones button 
sends to the robot the keepin and keepout zones that are used by this Control Station, overwriting the keepin and keepout
zones originally on the robot.  Idle propulsion turns off the propulsion units. To set the Operating Limits, select a
named configuration in the dropdown and click Configure Data.

## Advanced 2 Tab

### Component States

This subtab displays the content of the ComponentState message.

### Data To Disk

This subtab displays the ROS Topics the robot can log, and which topics it is logging

To change which rostopics the Astrobee is logging, select a configuration file from the dropdown and click Configure
Data. You can also send commands to start and stop downloading data, and to clear data, with the labeled buttons.

### Standard Controls

Use this subtab to Grab Control or Stop (same as Station Keep on the other tabs) the Astrobee.  The Send Zones button 
sends to the robot the keepin and keepout zones that are used by this Control Station, overwriting the keepin and keepout
zones originally on the robot.  Idle propulsion turns off the propulsion units. The No-Op and Wipe High Level Processor
buttons are self-explanatory.  The table displays the content of the CommState message (CommState.idl)

### Inertia Properties

This subtab displays the contents of the Inertial Properties message (InertialProperties.idl).  To change the inertia
properties on the robot, select an inertia configuration from the dropdown and click Configure Inertia.