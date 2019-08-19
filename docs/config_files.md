# Control Station Config Files

There are several config files that can be used with the workbench. Original copies of the config files are in
`gov.nasa.arc.verve.freeflyer.workbench/resources`. The first time the Control Station is executed, it creates
a `ControlStationConfig` folder in the folder it was started from and copies the files into it.
For instance, if you are running from source, `ControlStationConfig` is in the workspace folder. If you are running
 from a binary that was unzipped into `AstroBeeWB.r612.20160127-131345`, the config folder will be
  `AstroBeeWB.r612.20160127-131345/ControlStationConfig`.
  
Change the files in `ControlStationConfig` to affect the operation of the Control Station. To reset to the default
config files, delete the `ControlStationConfig` folder and restart the Control Station; it will create a new copy
of the folder with the original files. To change the default files, edit the copies in 
`gov.nasa.arc.verve.freeflyer.workbench/resources`. Then delete `ControlStationConfig`, and restart the Control Station.
 
Following is a list of all the config files used by the Control Station.


* `AllInertiaConfig.json`
  * Lists options for inertia parameters, used in Plan Editor and in Inertia Properties part on Other tab (thus 
  'All' in the name).
  * Edit the arrays inside `inertiaOptions`; each must have a unique name
  * Mass of Astrobee in kg, matrix represents the 3x3 inertia matrix (let's say row major order, 
  though currently this is all ignored)
  * Example file:
```
{	
	"type" : "InertiaConfigurationFile",
	"inertiaConfigs" : [ {
    	"name" : "UnloadedAstrobee",
    	"mass" : 5.0,
    	"matrix" : [ 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0 ]
  	}, {
    	"name" : "OffbalanceAstrobee",
    	"mass" : 7.2,
    	"matrix" : [ 1.0, 0.0, 0.0, 0.0, 0.5, 0.5, 0.0, 0.5, 0.5  ]
  	}, {
      	"name" : "Heavy",
    	"mass" : 15.0,
  	  	"matrix" : [ 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0  ]
 	} ]
}
```

* `AllOperatingLimitsConfig.json`
  * Populates the options for Operating Limits in both the Plan Editor and on the Operating Limits 
  section on the Other tab (thus "All").
  * Edit the arrays inside `operatingLimitsConfigs`; each set of operating limits must have a unique profileName
  * Can adjust `flightMode`, `targetLinearVelocity`, `targetLinearAccel`, `targetAngularVelocity`, 
  `targetAngularAccel`, and `collisionDistance` (all units are in meters and seconds).
  * Some options that used to be in here (allow blind flying, check keepouts, etc) became 
  separate commands because they will change more often.
  * Example file:
```
{	
	"type" : "OperatingLimitsConfigurationFile",
	"operatingLimitsConfigs" : [ {
    	"profileName" : "Conservative",
    	"flightMode" : "Flight Mode One",
    	"targetLinearVelocity" : 0.1,
    	"targetLinearAccel" : 0.03,
    	"targetAngularVelocity" : 0.02,
    	"targetAngularAccel" : 0.01,
    	"collisionDistance" : 0.1
  	}, {
    	"profileName" : "Speedy",
    	"flightMode" : "Flight Mode Two",
    	"targetLinearVelocity" : 5.0,
    	"targetLinearAccel" : 0.05,
    	"targetAngularVelocity" : 0.1,
    	"targetAngularAccel" : 0.1,
    	"collisionDistance" : 0.01
  	}  ]
}
```

* Data to Disk Configuration files
  * Set of files in `ControlStationConfig/DataToDisk`.
  * Sent to Astrobee as a `CompressedFile` on topic `astrobee_compressed_file-data_to_disk`, 
  then set with `setDataToDisk` command (so user has feedback if bad syntax)
  * Each file of type `DataConfigurationFile` lists which RosTopics (onboard telemetry streams) should be saved 
  to disk, either for immediate or delayed download, and at what frequency.
  * As of Release 0.0.4, to start or stop recording you must send `DATA_METHOD_START_RECORDING` and
   `DATA_METHOD_STOP_RECORDING` commands to start and stop recording after configuring settings.
    * For Release 0.0.3 and prior, if you want to stop recording a topic, send another `DataConfigurationFile` that
     does not include that topic.
   So, to stop recording all topics, send a file that has an empty `topicSettings` value.  If you are recording 
   10 topics and want to stop listening to one, send a `DataConfigurationFile` listing the 9 you are still interested
    in.
  * Example file:
```
{
  "type" : "DataConfigurationFile",
  "name" : "default",
   "topicSettings": [ 
    {
	  "topicName" : "RosTopic0",
	  "downlinkOption" : "immediate",
	  "frequency" : 5.0
    },
     {
	  "topicName" : "RosTopic3",
	  "downlinkOption" : "delayed",
	  "frequency" : 10
    }
    ]
}
```

* `global.properties`
  * Lists the RTSP urls that the Astrobees will stream SciCam video from, by Astrobee name
  * If your Astrobee has a different name (ie, you are using ground unit Bsharp), change the name in this file to match
  * Example file for on-orbit Astrobees:
```
Honey.vlc.stream.url=rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov
Bumble.vlc.stream.url=http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
Queen.vlc.stream.url=rtsp://127.0.0.1:1234/ch0
```

* `GraphicsCameraFovs.json` 
  * For camera resolutions, etc, use `PlanEditorSetCameraConfig.json` or `PlanEditorSetCameraConfig.json`
  * The Scene Graph Controls dialog (accessed from the View menu) lets you see where the FOVs of the various cameras are.  This file is a 
  list of the cameras, their positions, directions, FOVs, and whether they are depth cameras or 2D cameras.
  * I assume it uses Astrobee robot frame coordinates.
  * Do not edit unless the Astrobee cameras change location (or you don't want to be able to view all the camera
   FOV, for instance if it is confusing).
  * Read in by `gov.nasa.arc.verve.robot.freeflyer.camera.CameraConfigListLoader`
   and used by `gov.nasa.arc.verve.robot.freeflyer.RapidFreeFlyerRobot`
  * Example entry: 
```
{	
	"type" : "CameraConfigurationFile",
	"cameraConfigs" : [ {
	    "name" : "NavCam",
	    "position" : [0.12517, 0.0381, 0.12039],
	    "rotation" : [0, -0.5, 0, -0.8660],
	    "horiz_fov" : 40,
	    "vert_fov" : 130,
	    "type" : "2D"
	}
]}
```

* `HealthAndStatusConfig.txt`
  * List the name of the telemetry data you want to appear in the Health and Status view, with one name per line.
    No commas.
  * To see the possible telemetry names, click the details button in the Health and Status view.
   Any piece of telemetry data in the popup dialog can be put into the config file to display in the standard Health 
   and Status view.  Options are reprinted here for your convenience (do not include text inside brackets):
```
Control [who has access control]
Operating State
Plan Execution State
Raw Mobility State
Sub Mobility State
Mobility State [combines previous two]
Operating Limits
Plan Name
Plan State
Arm Mobility
Arm Gripper
Recording Data to Disk
Data to Disk Filename
Data to Disk [combines previous two]
```

* helpfiles folder
  * not related to configuration but it is in this folder so it can be opened from a web browser from the build.  **Do not modify.**

* `PlanEditorGuestScience.json`
  * Lists available Guest Science APKs and their associated commands for use **ONLY** in the Plan Editor 
  (The dropdowns in the Guest Science views are populated by the `GuestScienceConfig` message).
  * Mimics the Astrobee RAPID message `GuestScienceConfig`.
  * `power` and `duration` parameters are to help the Plan Compiler estimate plan duration and power consumption.
    Both fields are optional.
  * Example file:
```
{	
	"type" : "GuestScienceConfigurationFile",
	"guestScienceConfigs" : [ {
    	"apkName" : "gov.nasa.arc.irg.astrobee.Turbo",
    	"shortName" : "Turbo",
    	"primary" : true,
    	"guestScienceCommands" : [ {
    			"name" : "Mode Power+Duration",
    			"command" : "command body for command PD",
    			"power" : 100,
    			"duration" : 20
    		}
    	    ]
  	}, {
    	"apkName" : "gov.nasa.arc.irg.astrobee.GrapplingHook",
    	"shortName" : "Grappling Hook",
    	"primary" : true,
    	"power" : 12,
    	"duration" : 19
  	},  {
    	"apkName" : "gov.nasa.arc.irg.astrobee.GeigerCounter",
    	"shortName" : "Geiger Counter",
    	"primary" : false,
    	"power" : 11,
    	"guestScienceCommands" : [ {
    			"name" : "Run Test",
    			"command" : "command body for Run Test"
    		} ]
  	} 
  ]
}
```

* `PlanEditorPayloadConfig.json`
  * Can include built-in items (laser pointer, speaker?) as long as flight software can understand the command
  * Populates the Power On Item and Power Off Item drop-downs in the Plan Editor Command widget 
  * Currently lists the name of the item and optional power consumption
  * Example file:
```
{
	"type" : "PlanPayloadConfigurationFile",
	"planPayloadConfigs" : [ {
	  "name": "Laser Pointer",
	  "power": 1.0
	},
	{
	  "name": "Payload Top Aft",
	  "power": 25
	},
	{
	  "name": "Payload Bottom Aft"
	},
	{
	  "name": "Payload Bottom Front",
	  "power": 27
	}
	]
}
```

* `PlanEditorSetCameraConfig.json`
  * Populates the widget for the Set Camera Command in the Plan Editor. 
  * (The Teleop Set Camera command in 
  the Miscellaneous Commands panel is populated by the `TeleopCommandsConfiguration.json` file.)
  * Lists available cameras, and preset settings for each.
  * Example file:
```
{
  "type" : "CameraPresets",
  "optionsForOneCamera": [ 
  	{
  	"cameraName": "Dock",
  	"preset": [ {
		"presetName": "High Def Stream",
		"resolution" : "1024_768",
		"cameraMode" : "Streaming",
		"frameRate" : 5,
		"bandwidth" : 640
  		},
  		{
		"presetName": "Low Def Rec",
		"resolution" : "640_480",
		"cameraMode" : "Recording",
		"frameRate" : 4,
		"bandwidth" : 92
  		} 
  	]
  	},{
  	"cameraName": "Navigation",
  	"preset": [ {
		"presetName": "High Def Stream+Rec",
		"resolution" : "1920_1080",
		"cameraMode" : "Both",
		"frameRate" : 5,
		"bandwidth" : 100
  		},
  		{
		"presetName": "Low Def Rec",
		"resolution" : "640_480",
		"cameraMode" : "Recording",
		"frameRate" : 25,
		"bandwidth" : 300
  		} 
  	]
  	}
  	]
}
```

* `TeleopCommandsConfiguration.json`
  * Controls what commands are available in the Miscellaneous Commands panel
  * The commands can be in different formats, which each have different widgets. Each widget is a child class of
   `gov.nasa.arc.verve.freeflyer.workbench.parts.teleop.AbstractTeleopCommandConfig`
  * Available types are:
    * CameraPresetTeleopCommand - specifies camera presets
	* DataManagementTeleopCommand - controls download, stop download, and clear data commands
	* FlashlightTeleopCommand - specifies front or back flashlight, and provides presets for low, med, high brightness
	* HorizontalSeparatorTeleopCommand - just draws a line, to group commands in the subtab
	* NoParamsTeleopCommand - sends a command with no parameters
	* OperatingLimitsTeleopCommand - specify set of operating limits (from list in 
	`OperatingLimitsConfigurations.json`) and send as parameters to
	 `SETTINGS_METHOD_SET_OPERATING_LIMITS` command
	* OppositeCommandsTeleopCommand - send one command, then change the button text and send the opposite command the
	next time the button is clicked (for instance, to turn laser pointer on and off). Does not read state of instrument
	to determine which command is available (ie, can get out of sync if another Control Station turns off the laser
	pointer)
	* StringBooleanTeleopCommand - sends a command that takes a string and a boolean as parameters. For instance,
	`setCameraStreaming` and `setCameraRecording` both take a camera name and boolean for on/off.
	* StringListIntTeleopCommand - select a string from a dropdown, then send a command with an int corresponding to 
	that string as the parameter.  This is done by splitting the string on ' ' (space) and looking for a numeral. 
	For instance, for sending `dock`, select "Berth 1" and the widget will send "1" as the parameter.
	* StringListTeleopCommand
	* ToggleBooleanTeleopCommand - sends a command with a boolean parameter. Widget is a latching toggle button, sends
	true on click and false on unclick. Changes button label when clicked. For instance, used to send 
	`gripperControl` command and toggle the parameter `open` between true and false. Can get out of sync with
	 robot state.
	* TwoFloatsTeleopCommand - sends two floats within a set range, first converting the floats from degrees to 
	radians. Originally intended to send attitude command, but currently not used. 
  * Example file:
```
{
	"type" : "TeleopCommandsConfigurationFile",
	"teleopCommandConfigs" : [ {
	 "type" : "CameraPresetTeleopCommand",
	  "label": "Camera to Configure",
	  "secondColumnLabel": "Configuration",
	  "buttonText": "Set",
	  "optionsForOneCamera": [ 
	  	{
	  	"cameraName": "Dock",
	  	"preset": [ {
			"presetName": "High Definition Stream",
			"resolution" : "1024_768",
			"cameraMode" : "Streaming",
			"frameRate" : 5,
			"bandwidth" : 640
	  		},
	  		{
			"presetName": "Low Definition Record",
			"resolution" : "640_480",
			"cameraMode" : "Recording",
			"frameRate" : 4,
			"bandwidth" : 92
	  		} 
	  	]
	  	},{
	  	"cameraName": "Navigation",
	  	"preset": [ {
			"presetName": "High Definition",
			"resolution" : "1920_1080",
			"cameraMode" : "Both",
			"frameRate" : 5,
			"bandwidth" : 100
	  		},
	  		{
			"presetName": "Low Definition",
			"resolution" : "640_480",
			"cameraMode" : "Both",
			"frameRate" : 25,
			"bandwidth" : 300
	  		} 
	  	]
	  	}
	  	],
	  "subsystem": "Camera"
	},{
	  "type" : "DataManagementTeleopCommand",
	  "label": "Data Type",
	  "secondColumnLabel": "Action",
	  "buttonText": "Send",
	  "paramName": "dataMethod",
	  "names":["Immediate", "Delayed"],
	  "actionNames":["Download", "Stop Download", "Clear"],
	  "actionCommands":["downloadData", "stopDownload", "clearData"],
	  "subsystem": "Data"
    },{
	  "type" : "FlashlightTeleopCommand",
	  "label": "Flashlight",
	  "secondColumnLabel": "Brightness",
	  "command": "setFlashlightBrightness",
	  "buttonText": "Set",
	  "names":["Front", "Back"],
	  "brightnessNames":["High", "Medium", "Low", "Off"],
	  "brightnessValues":[1.0, 0.6, 0.3, 0.0],
	   "subsystem": "Settings"
    },{
	  "type" : "HorizontalSeparatorTeleopCommand"
	},{
	  "type" : "NoParamsTeleopCommand",
	  "label": "Stop Arm",
	  "buttonText": "Stop Arm",
	  "command": "stopArm",
	  "subsystem": "Arm"
    },{
	  "type" : "NoParamsTeleopCommand",
	  "label": "Idle Propulsion",
	  "buttonText": "Idle",
	  "command": "idlePropulsion",
	  "subsystem": "Mobility"
	},{
	  "type" : "OperatingLimitsTeleopCommand",
	  "label": "Operating Limits",
	  "buttonText": "Set"
	},{
	  "type" : "OppositeCommandsTeleopCommand",
	  "label": "Laser Pointer",
	  "command": "powerOnItem",
	  "oppositeCommand": "powerOffItem",
	  "paramValue":"Laser Pointer",
	  "paramName": "which",
	  "buttonText": "Power On",
	  "buttonPushedText": "Power Off"
	},{
	 "type" : "StringBooleanTeleopCommand",
	  "label": "Camera to Stream",
	  "secondColumnLabel": "Streaming",
	  "command": "setCameraStreaming",
	  "stringParamName": "cameraName",
	  "stringOptions": ["Dock", "Navigation", "Science"],
	  "boolParamName": "stream",
	  "buttonText": "Send",
	  "subsystem": "Camera"
	},{
	  "type" : "StringBooleanTeleopCommand",
	  "label": "Camera Name",
	  "secondColumnLabel": "Recording",
	  "command": "setCameraRecording",
	  "stringParamName": "cameraName",
	  "stringOptions": ["Dock", "Navigation", "Science"],
	  "boolParamName": "record",
	  "buttonText": "Send",
	  "subsystem": "Camera"
	},{
	  "type" : "StringListIntTeleopCommand",
	  "label": "Manual Dock",
	  "command": "dock",
	  "paramName": "berthMethod",
	  "buttonText": "Dock",
	  "paramOptions": ["Berth 1","Berth 2"],
	  "subsystem": "Mobility"
	},{
	  "type" : "ToggleBooleanTeleopCommand",
	  "label": "Gripper",
	  "command": "gripperControl",
	  "paramName": "open",
	  "buttonText": "Open",
	  "buttonPushedText": "Close",
	  "subsystem": "Arm"
	}
	]
}
```
 
The following config files are in 
"the `{world}` folder". `{world}` is the name of the folder that contains a 3d model that displays in the 3d view. 
The model is in COLLADA format. The default world is a simplified
model of the ISS, its corresponding world folder is `IssWorld`. The Control Station also supports two other worlds:
`DetailIssWorld` is a model of the ISS with photorealistic textures 
(from ​https://nasa3d.arc.nasa.gov/detail/iss-internal).
`GraniteLab` is a world that represents the granite table at Ames that is used for Astrobee testing. It has a table and
other features drawn programmatically instead of being loaded from a COLLADA model.

To start the Control Station with a world other than IssWorld, run with the command line argument `-world 
{world}`. The files in a `{world}` folder only apply to that world (ie, ISS and the Granite Lab have
different keepouts).

* `BookmarksList.json` *(inside `{world}` folder)*
  * List of bookmarks available in the Plan Editor and the Teleop Tab.
  * Don't edit by hand, use the Bookmarks Manager (explained on [this](plan_editor_tab.md) page)

* `GraphicsColoredBoxes.json` *(inside `{world}` folder)*
  * Draws an axis-aligned box of a specified color at a specified point in the view.
  * `position` specifies two opposite corners of the box.  Uses ISS (global) coordinates.
  * `name` specifies the identifier in the scene graph.
  * Color is 0-1 RGB.
  * Control Station shipped with this file empty, but parsing code is still there so boxes can be added later.
  * Read into `gov.nasa.arc.verve.freeflyer.workbench.scenario.ColoredBoxList` in 
  `package gov.nasa.arc.verve.freeflyer.workbench.scenario.FreeFlyerScenario`
  * Example entry:
```
{	
	"type" : "ColoredBoxesConfigurationFile",
	"coloredBoxes" : [ {
    	"name" : "MockDock",
    	"position" :[ 5.878, -1.099, 0.356, 6.878, -0.899, 1.056 ],
    	"color" : [ 0.82, 0.71, 0.55 ]
  	} ]
}
```

* `GraphicsLightsCamera.properties` *(inside `{world}` folder)* 
  * Relates to the lighting/camera on the graphics in the 3D view ("Live Map")
  * All units in meters
  * Controls camera center (where the virtual camera is looking in the 3D scene)
  * Controls camera location (where the virtual camera is located in the 3D scene)
  * Also can move some lights around (was needed because the Space Station Analysis coordinate frame, which has the
   origin far outside the volume available to Astrobee, was imposed as the world frame very late in development)


* Handrail Config *(inside `{world}` folder)*
  * File to control positions of handrail models in the 3D view
  * File at `ControlStationConfig/{world}/HandrailConfig.json`
  * Instructions for editing are [this](modeling_tab.md) page

* `IssConfiguration.json` *(inside `{world}` folder)*
  * Relative locations and orientations of the models of the ISS modules are described in `IssConfiguration.json`. 
  This file is used to position the model files that make up the 3d view 
  *   From `IssConfiguration-README`:
    * The `IssConfiguration.json` file is set up as a JSON array such that each object describes a single module.
     A single module consists of the following fields:
       - `name` - the name of the module, must match the value in the ModuleName enum found in 
       `gov.nasa.arc.irg.plan.model.modulebay.Module`.
       - `file` - the name of the `.dae` file located in `gov.nasa.arc.verve.robot.freeflyer/models/`
       - `offset` - the absolute location and orientation of the model formatted as: `[x offset,
        y offset, z offset, roll, pitch, yaw]`. The origin of a model can be arbitrarily 
        defined if the following fields are correct relative to the defined origin.
       - `radii` - a 2D array containing the distance to each wall from the center of a given bay. The row number 
       corresponds to the bay number and by convention the wall is specified by the column number in accordance with 
       the ordering of the Wall enum in `gov.nasa.arc.irg.plan.model.modulebay.LocationMap`. The order is as
        follows: [FWD, AFT, STBD, PORT, DECK, OVHD]. An entry containing all -1 represents a bay which does not exist.
       - `dividers` - an array of relative 3D points specifying the bay dividers. That is, where the midpoints of the 
       lines separating two bays are. These are 3D points in order to represent orientation. For example,
        a module that has bays along the y-axis will have non-zero divider coordinates in the y component of the
         coordinate only, thus the endpoints of the lines can be inferred from the radii. 
       - `keepin` - an array of "box arrays" in relative coordinates that signify the keepin zones of the given module.
        The position in the array generally is ordered according to increasing bay number, but this is not guaranteed or 
        required. The format of a "box array" is: [low x, low y, low z, high x, high y, high z]. The number of keepin
         boxes is in no way related to the number of bays.  
         
  * This file is used to generate `keepins.json` (offline). (`gov.nasa.arc.verve.freeflyer.workbench.locations.KeepinGenerator.java` is a standalone program reads the keepin boxes, transforms them to global
          coordinates, and emits a keepin.json file that should be put into the 
          `ControlStationConfig/{world}/keepin` folder). It also generates the clickable plane
   in the Plan Editor used with the "Add via 3d Map" option. The "dividers" are used to draw lines
   on the clickable plane to denote the bays.

* Keepouts/Keepins  *(inside `{world)` folder)*
  * Files inside `ControlStationConfig/{world)/keepins` and `ControlStationConfig/{world)/keepouts`
  * Edit the arrays inside "sequence".  Each array denotes the corners of an axis-aligned box in global coordinates
  `[low x, low y, low z, high x, high y, high z]`
  * Units are meters
  * `"safe" : false` is for keepouts, ` "safe" : true` is for keepins
  * The Modeling tab has a GUI to adjust keepouts.  Go [here](modeling_tab.md)  for instructions.
  * If you have the workbench source code, at `gov.nasa.arc.verve.freeflyer.workbench.locations` there is a
   `KeepinGenerator.java` that parses the `IssConfiguration.json` and generates a corresponding keepin file.
  * Keepins and keepouts are sent to the Astrobee by clicking the "Send Zones" buttons on the Standard Controls section
  of the Engineering or Other tabs. "Send Zones" calls `startSendingZones()` on the `AstrobeeStateManager`
   (`gov.nasa.arc.irg.freeflyer.rapid.state`). The `AstrobeeStateManager` calls the `SendZonesManager`, which tells the 
   `CompressedFilePublisher` (`gov.nasa.arc.irg.freeflyer.rapid`) to concatenate all the keepin and keepout files into one
   compressed file and send it as `ZONES_COMPRESSED_TYPE`. When the Astrobee receives the message, it sends a 
   `COMPRESSED_FILE_ACK`. The `SendZonesManager` waits for that ack and then sends a `SETTINGS_METHOD_SET_ZONES` command, which
   tells the Astrobee to update its zones to the file it just received.
  * Example Keepout file:
```
{
  "sequence" : [ [ -2.25, -0.95, 0.85, -1.15, 1.05, 1.05 ], [ -7.55, 0.05, -1.05, -6.35, 1.05, 1.05 ] ],
  "dateCreated" : "1456251156239",
  "notes" : "Don't go here.",
  "author" : null,
  "name" : "LabKeepouts",
  "safe" : false
}
```

* Models *(inside `{world}` folder)*
  * This folder holds the `.dae` files of the ISS modules, the SmartDock, any handrails, and 
  the Astrobees (in the `astrobeeModel` folder).  Put any new models here.


