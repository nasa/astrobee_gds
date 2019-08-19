<<<<<<< HEAD
# Debugging, Advanced, and Advanced 2 tabs

The Debugging, Advanced, and Advanced 2 tabs have subtabs that display detailed debugging information and allow
=======
# Engineering, Other and Debugging tabs

The Engineering, Other and Debugging tabs have subtabs that display detailed debugging information and allow
>>>>>>> master
engineers to send low-level commands.

## Debugging Tab

### 3D View

Shows world model and Astrobees. Code in `gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView.Debug3dView`.

### Config Commander

The <b>Config Commander subtab</b> (`gov.nasa.arc.verve.freeflyer.workbench.parts.engineering.ConfigCommanderPart`)
 automatically generates widgets to send any command listed in the `CommandConfig`
message sent by the Astrobee. Select the Astrobee, and then select the subsystem to see the command widgets displayed.
There is no error checking for commands sent through the Config Commander.

### Top Bar and Standard Controls

Allows user to grab control from the debug tab.
Code is at `gov.nasa.arc.verve.freeflyer.workbench.parts.standard.TopBar` and 
`gov.nasa.arc.verve.freeflyer.workbench.parts.advanced.JustStandardControls`

### Discovered DDS Info

The <b>Discovered DDS Info subtab</b> (`gov.nasa.rapid.v2.ui.e4.parts.DiscoveredDdsInfoPart`) lists raw DDS 
information to help debug DDS connections. Click Topics to list the
topics published in each partition on the Discovered Partitions panel. Click Refresh to refresh the information (note, 
refreshing will add new information but it won't delete stale information).

### Received Topics

Code in `gov.nasa.arc.verve.freeflyer.workbench.parts.engineering.ReceivedTopicsPart`. Click the arrow to the right 
of the participant/partition to see the list of topics it publishes. Check the checkbox to 
the left of the topic to see the latest message on that topic in the Raw Rapid Telemetry subtab. Click Refresh to 
refresh the list of topics.

### Raw Rapid Telemetry

Code in `gov.nasa.arc.verve.freeflyer.workbench.parts.engineering.RawRapidTelemetryPart`.
This subtab displays the latest messages on the topics that are checked in the Received Topics subtab.

## Engineering Tab
### Health subtab

This subtab (`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.DetailedHealthAndStatusPart`) lists the
 state information published by Astrobee.
 
 Note that Raw Mobility State "Docking" with Sub Mobility
State "0" is "Docked", and Raw Mobility State "Perching" with Sub Mobility State "0" is "Perched".  That is to say,
there are no states named "Docked" or "Perched", only "Docking 0" and "Perching 0".

### Faults subtab

The Faults subtab (`gov.nasa.arc.verve.freeflyer.workbench.parts.advanced.FaultsPart`) lists all of the faults that 
Astrobee publishes in its FaultConfig. When the robot throws a fault, that fault is listed under "Triggered" 
in the table.

### Data to GDS 2
Use this subtab (`gov.nasa.arc.verve.freeflyer.workbench.parts.advanced.DataToGdsPart2`) to set the frequency 
at which Astrobee sends telemetry to the Control Station (using the `SETTINGS_METHOD_SET_TELEMETRY_RATE`
command). You can also use this tab to configure the cameras on Astrobee (via `SETTINGS_METHOD_SET_CAMERA`),
 and start or stop them from streaming images (via `SETTINGS_METHOD_SET_CAMERA_STREAMING`) 
 
(Regarding the name of the tab, the Control Station is also called "GDS", for "Ground Data System".)

### Data To Disk

This subtab (`gov.nasa.arc.verve.freeflyer.workbench.parts.advanced.DataToDiskPart`) displays the ROS Topics the 
robot can log (from the DataTopicsList message), and which topics it is logging (from the DataToDiskState message).
Click a column heading in the table to sort by that column.

To change which rostopics the Astrobee will log (but not whether Astrobee is currently logging anything), 
select a configuration file from a dropdown and click the associated "Configure" button. The dropdowns are populated
 with the names of the files in the DataToDisk folder (see [Config Files](config_files.md)). There are multiple 
 Configure dropdown-button pairs to make it faster to change between recording profiles.  When you
click Configure Data, the CompressedFilePublisher compresses and sends the selected json file on topic
`astrobee_compressed_file-data_to_disk`. When the DataToDiskPart receives the CompressedFileAck, it sends 
`DATA_METHOD_SET_DATA_TO_DISK`. 

To actually start or stop recording data to disk, use the buttons labeled Start Recording and Stop Recording,
 which send `DATA_METHOD_START_RECORDING` and `DATA_METHOD_STOP_RECORDING` commands. Start recording takes a 
 string parameter that is used as part of the filename of the recorded data; enter this parameter in the text
 box next to the Start Recording button.

### Localization Commands

Use this tab (`gov.nasa.arc.verve.freeflyer.workbench.parts.advanced.LocalizationCommandsPart`) to switch
 localization methods, reset the EKF, initialize the bias, idle propulsion, or no-op.

## Other Tab

### Standard Controls 

This subtab `gov.nasa.arc.verve.freeflyer.workbench.parts.standard.AdminControlPanel`. Use it to Grab Control 
or Stop (same as Station Keep on the other tabs) the Astrobee. The Send Zones button uses the 
`SendZonesManager` to send the keepin and keepout zones in the current `{world}` folder to the robot. The zones
are first concatenated into one file, and the file is sent as a Compressed file on topic 
`astrobee_compressed_file-zones`. When the `SendZonesManager` receives a `CompressedFileAck`, it sends 
`SETTINGS_METHOD_SET_ZONES`. The set zones command overwrites the keepin and keepout zones originally on 
the robot. 

Idle propulsion turns off the propulsion units by sending `MOBILITY_METHOD_IDLE_PROPULSION`. The No-Op and Wipe 
High Level Processor buttons send `ADMIN_METHOD_NOOP` and `ADMIN_METHOD_WIPE_HLP`, respectively.
The table displays the content of the CommState message.

### Component States

This subtab (`gov.nasa.arc.verve.freeflyer.workbench.parts.advanced.ComponentsPart`) displays the content 
from the ComponentConfig and ComponentState message. For each component, it displays whether or not the component
is present. If it is present, it displays if it is powered, its temperature and its current draw.

### Inertia Properties

This subtab (`gov.nasa.arc.verve.freeflyer.workbench.parts.advanced.InertiaPart`) displays the contents of the 
InertialProperties message.  To change the inertia properties on the robot, select an inertia configuration
 from the dropdown that is populated by `InertiaConfigurations.json`. Click Configure Inertia, which sends
 `SETTINGS_METHOD_SET_INERTIA` with the appropriate parameters.
 
### Operating Limits
 
Code is in `gov.nasa.arc.verve.freeflyer.workbench.parts.advanced.OperatingLimitsPart`; actually, the StandardControls
 are created in this class. To set the Operating
Limits, select a named configuration in the dropdown, which is populated by `OperatingLimitsConfigurations.json`.
Then click Configure Data, which sends `SETTINGS_METHOD_SET_OPERATING_LIMITS` with the appropriate parameters.

### Power State

This subtab (`gov.nasa.arc.verve.freeflyer.workbench.parts.advanced.EpsPart`) displays the information from the 
EpsConfig and EpsState messages.

### Camera Streaming Configuration

Use this tab (`gov.nasa.arc.verve.freeflyer.workbench.parts.advanced.CameraStreamingConfigurationPart`) 
to adjust settings to stream images to the ground. Make sure that you know the effects of these
commands in FSW before using them. (At one point, adjusting streaming settings also changed recording settings.)
