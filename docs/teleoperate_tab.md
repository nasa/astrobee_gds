# Teleoperate Tab #

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure11.png 
"Teleoperate Tab")

<p>
The <b> Teleoperation Tab </b> sends single commands to the selected Astrobee. It contains the
following unique subtabs:
</p>


## Bee Commanding ##

<p>
The <b>Bee Commanding subtab</b> (`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.BeeCommandingPartOnTeleoperateTab.java`)
 sends commands that move the position and orientation of the selected Astrobee. Widgets
  in this subtab are created in `BeeCommandingPartOnTeleoperateTabCreator.java`.
  It has the following sections:
</p>

### Initialization

The <b> Initialization section</b> in the Bee Commanding subtab includes the Grab Control button.

### Locations 
The <b> Locations section</b> includes a <b>locations dropdown</b>, from which 
to select a named location (coordinate and orientation) in the ISS. (Locations are specified as Bookmarks on the Plan
 Editor tab and stored in the `BookmarksList.json` config file). When you select a Location, the Control Station
  sets the position and  orientation of the white Preview 
  (`gov.nasa.arc.verve.robot.freeflyer.parts.RobotPartDraggablePreview.java`) in the Interactive Map subtab to that
   location. The Manual Move Inputs update from the position of the Preview.

### Manual Move Inputs
<p>
The <b>Manual Move Inputs section</b> in the Bee Commanding subtab contains text fields to enter coordinates 
of where to send Astrobee when the Move button is clicked. Aft-Forward, Port-Starboard, and 
Overhead-Deck are fixed to the ISS directions. The Roll, Pitch, and Yaw inputs also use the ISS coordinate
 frame (not the robot's body frame). The Astrobees use the Space Station Analysis coordinate frame, the origin of which 
 is at truss segment S0 (above the US lab). Use the arrow buttons on either side of each text box to
  make small changes to the position. Linear measurements are incremented by 0.05 meters and angles are 
  incremented by 15 degrees. To enter a large number, type the number of meters into the box and adjust the 
  tenths of meters using the arrow buttons.

The Manual Move Inputs fields (`translationInput` and `rotationInput` arrays in
  `BeeCommandingPartOnTeleoperateTab`) are bound to the `RobotPartDraggablePreview`. Binding ensures that when the preview
   moves, the input fields are automatically populated with the coordinates and orientation of the preview. 

When the Move button is clicked, the values in the Manual Move Inputs fields are used to construct the move command.
</p>

### Options 
The <b>Options section</b> in the Bee Commanding subtab sets the list of options to send to the
 Astrobee when the
Apply Options button is clicked. If the checkbox to the left of the option is 
checked, the Apply Options button will send a command to turn on that option.
If there is a green checkmark to the right of the option, the Astrobee already has
that option turned on.  Options include:

<ul>
<li>  <b> Face Forward </b> (`SETTINGS_METHOD_SET_HOLONOMIC_MODE`) requires the Astrobee to face the direction of motion.
Unchecking this option allows the Astrobee to fly sideways or backwards, which may be
desirable to collect specific video or science data. However, the Astrobee does not 
have cameras pointing in all directions, so it must be monitored closely when
flying with Face Forward off so that it does not collide with an obstacle.</li> 

<li>  <b>Check Obstacles</b> (`SETTINGS_METHOD_SET_CHECK_OBSTACLES`) requires the Astrobee to stop moving and station keep if
it detects an object in its planned flight path. It may be necessary to turn off
this option if the Astrobee detects a ghost obstacle and gets stuck.</li> 

<li>  <b>Check Keepouts</b> (`SETTINGS_METHOD_SET_CHECK_ZONES`) requires the Astrobee to stop before entering 
any keepout zone (keepout zones are designated with gray boxes in the Live Map subtab).
  It may be necessary to turn this option off if the Astrobee accidentally gets
knocked into a keepout zone. </li> 
</ul>

<b>Apply Options </b> commands the Astrobee to turn on the options that 
are checked in the Options section and turn off the options that are not checked in
the Options section. This is done by sending the associated commands with the appropriate parameters. The checkmarks are updated by the 
`AgentState` message cached by the `AggregateAstrobeeState`.

> NOTE<br>
> The Face Forward, Check Obstacles, and Check Keepouts options should usually be turned on.

### Commands

The <b> Commands section</b> in the Bee Commanding subtab has buttons to send commands to the selected Astrobee.
It includes these buttons:

<ul>
<li>  <b>Station Keep Bee </b> commands the Astrobee to cancel any movement commands and
station keep at its current position by sending the `MOBILITY_METHOD_STOPALLMOTION` command.</li>

<li>  <b> Move </b> commands the Astrobee to go to the position specified 
in the Manual Move Inputs section by sending the `MOBILITY_METHOD_SIMPLEMOVE6DOF` command </li> 
</ul>

## Perching Arm

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure12.png 
"Perching Arm subtab")

<p>
The <b>Perching Arm subtab</b> 
(`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.PerchingArmPart.java`)
sends commands related to the Astrobee's Perching 
Arm. The widgets on this subtab are created in `PerchingArmPartCreator.java`. The Perching Arm is designed to grasp a handrail
and secure the Astrobee while it conserves power by turning off its propulsion 
system. The Perching Arm subtab includes the following sections:
</p>

### Initialization
The <b> Initialization section</b> in the Perching Arm subtab
includes the Grab Control button discussed above.

### Pan and Tilt
<p>
The <b>Pan and Tilt section</b> in the Perching Arm subtab contains <b>text fields</b>
to enter angles for the pan and tilt joints of the arm. 
The <b>Pan and Tilt button</b> commands the Astrobee perching arm to move to the 
angles specified. The Pan and Tilt command (`ARM_METHOD_ARM_PAN_AND_TILT`) is intended
 to be sent only when the Astrobee is perched on a
handrail and providing video.  The input may be moved in 10 degree increments by
the arrows on either side of the text field, or the desired angle may be typed into the text field
directly.  When pan and tilt are both zero, the arm is fully extended.
</p>

### Options
The <b>Options section</b> in the Perching Arm subtab includes the <b>Reacquire 
Position button</b>, which sends `ADMIN_METHOD_REACQUIRE_POSITION`.
If the Astrobee is accidentally knocked off its perch on a handrail, it may lose its
internal location state. In that case, the Reacquire Position command tells the
Astrobee to reset the EKF and recalculate its location from scratch.

### Commands

The <b> Commands section</b> in the Perching Arm subtab has buttons to send commands to the selected Astrobee.
It includes:

<ul>
<li>  <b>Station Keep Bee</b> (`MOBILITY_METHOD_STOPALLMOTION`) commands the Astrobee to cancel any movement commands and
station keep at its current position .</li>
<li>  <b> Perch </b> (`ARM_METHOD_ARM_PAN_AND_TILT`) commands the Astrobee to deploy its perching arm and
grasp a handrail that is directly in front of it.  The command
will fail if the Astrobee is not lined up correctly with the handrail.</li> 
</ul>

## Docking

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure13.png 
"Docking subtab")

<p>
The <b>Docking subtab</b> (`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.DockingPart.java`)
sends commands related to Docking. Widgets on this subtab are created in `DockingPartCreator.java`.
It includes these sections: 
</p>


### Initialization
The <b> Initialization section</b> in the Docking subtab
includes the Grab Control button discussed above.


### Options
The <b>Options section</b> in the Docking subtab includes the <b>Automatically Return to Docking
Station when Battery Low checkbox</b> (controls `SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN`).
This option causes Astrobee to automatically return to the Docking Station when the following
 conditions are met: the Astrobee is out of contact with a Control Station, it
is not perched, and it is running low on power. Disable this behavior by unchecking Automatically
 Return to Docking Station when Battery Low
 and clicking the <b>Apply Option button</b>.  If the Astrobee has the option turned on, 
there is a green checkmark to the right of the option title.

### Commands

The <b> Commands section</b> in the Docking subtab has buttons to send commands to the selected Astrobee.
It includes:

<ul>
<li> The <b>Station Keep Bee</b> button commands the Astrobee to cancel any movement commands and
station keep at its current position. (`MOBILITY_METHOD_STOPALLMOTION`) </li>
<li> The <b> Send Bee to Docking Station </b> button commands the Astrobee to immediately return
to the Docking Station and dock itself (`SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN`). 
 The Astrobee will be able to execute the Dock Automatically command from
anywhere in the US section of the ISS.  The Astrobee will automatically choose an unoccupied berth
on the Docking Station.  The Astrobee plans a trajectory assuming the
modules are unobstructed.  If it detects an obstacle in its planned path, it will
stop and wait for the obstacle to move. (NB: As of July 2019, this command is not implemented in Astrobee
 Robot Software and there is no scheduled implementation date. To command Astrobee to dock, please use the manual
 Dock command on the Miscellaneous Commands tab, or on the Debugging tab.)</li> 
</ul>

## Relative Commanding

The <b>Relative Commanding</b> subtab 
(`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.RelativeCommanding.java`) 
is not included in the Crew Control Station; it is intended for use by engineers only.
It has these sections:

### Initialization
The <b> Initialization section</b> includes the Grab Control button discussed above.


### Input
The <b>Input section</b> in the Relative Commanding subtab has a textbox that allows the user to input a position relative
to the robot's current position. The input needs to take the form "x, y, z, qx, qy, qz, qw". x, y, z are distances in meters
along the robot's forward, right, and down axes, and qx, qy, qz, qw are components of a quaternion relative to Astrobee's
current position.

### Commands

The <b> Commands section</b> in the Docking subtab has buttons to send commands to the selected Astrobee.
It includes:

<ul>
<li> The <b>Station Keep Bee</b> button commands the Astrobee to cancel any movement commands and
station keep at its current position. (`MOBILITY_METHOD_STOPALLMOTION`) </li>
<li> The <b> Move </b> button commands the Astrobee to move to the position specified in the 
Input textbox. (`MOBILITY_METHOD_SIMPLEMOVE6DOF` with frame set to "body")</li> 
</ul>

## Interactive Map

<p>
The <b>Interactive Map subtab</b> 
(`gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView.Teleop3dView`), like the 
Live Map subtab on other tabs,
 displays a 3D model of the selected Astrobee in the US Operating Segment.  
 The Interactive Map also includes a white Preview model with 
draggable arrows (`gov.nasa.arc.verve.robot.freeflyer.parts.RobotPartDraggablePreview`).
 The Preview shows the pose that is indicated by the Manual Move Inputs section
of the Bee Commanding subtab; i.e., the Preview shows where the Move button will send the Astrobee.
If the Preview intersects a keepout or a wall, and the Check Keepouts 
option is checked, the Preview turns orange to indicate a potential collision. Checking is done by
`gov.nasa.arc.verve.robot.freeflyer.parts.LiveTeleopVerifier`, which knows what to check by having
 `ContextNames.TELEOP_TRANSLATION`, `TELEOP_ROTATION_RADIANS`, and `CHECK_KEEPOUTS_ENABLED` injected.
</p>

<p>
 The Preview has color coded arrows and rings that can be dragged to move it to
the desired position.  Red indicates Astrobee's forward direction and blue indicates Astrobee's
down direction. Interaction is done by creating a `VerveInteractable` and calling `VerveUserData` 
and `VerveInteractManager`. The Manual Move Inputs fields update automatically
when the Preview is dragged by its arrows, by the BeeCommandingPartOnTeleoperateTab having 
`ContextNames.TELEOP_TRANSLATION` and `TELEOP_ROTATION_RADIANS` enabled.
</p>

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure14.png 
"Interactive Map")

The <b>Camera Controls</b> box in the Interactive Map (`ArrowsDialogWithPreviewButton` class inside
 `Teleop3dView`) has these additional buttons:

<ul>
<li> <b>Show</b> or <b>Hide Preview</b> displays or hides the white Preview model</li>
<li> <b>Zoom to Preview</b> centers the map on the Preview model. </li>
<li> <b>Snap Preview to Bee</b> moves the Preview to the current position of the selected Astrobee.
The coordinates in the Manual Move Inputs section will update accordingly.
 </ul>


<h3 id="MiscCommands"> Miscellaneous Commands</h3>

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure15.png 
"Miscellaneous Commands subtab")

<p>
The <b>Miscellaneous Commands subtab</b> 
(`gov.nasa.arc.verve.freeflyer.workbench.parts.teleop.MiscellaneousCommandsPart`) 
may have different command options, depending
on the activity. The default commands control the camera. Select
the name of the camera (Dock, Navigation, or Science) in the first
dropdown, the desired option in the second dropdown, and send
the command by clicking the Send button at the right of the
row.

The widgets in this tab are specified by 
`TeleopCommandsConfiguration.json` and read into 
`gov.nasa.arc.verve.freeflyer.workbench.parts.teleop.TeleopCommandsConfigList` as
a `List` of `AbstractTeleopCommandConfig`s. Each subclass of `AbstractTeleopCommandConfig`
holds the information to create one type of widget, including 
- widget label
- command to send
- button text
- parameters for the command, if needed
- options for parameters, if needed
See [this](docs/config_files.md) page under TeleopCommandsConfiguration.json
 for examples of items in the json file.
</p>