# Teleoperate Tab #

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure11.PNG 
"Teleoperate Tab")

<p>
The <b> Teleoperation Tab </b> sends single commands to the selected Astrobee. It contains the
following unique subtabs:
</p>


## Bee Commanding ##

The <b>Bee Commanding subtab</b> (`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.BeeCommandingPartOnTeleoperateTab.java`)
 sends commands that move the position and orientation of the selected Astrobee, using "world" coordinates
 (ie, the Space Station Analysis coordinate frame). Widgets
  in this subtab are created in `BeeCommandingPartOnTeleoperateTabCreator.java`.
  The Bee Commanding subtab has the following sections:

### Preview

"Preview" refers to the draggable white "Astrobee" model (`RobotPartDraggablePreview`) in the Interactive Map. The 
Preview shows where the current values in the Manual Move Inputs section will send the Astrobee. The **Show/Hide Preview button** 
toggles the visibility of the Preview in the Interactive Map.  The **Snap Preview to Bee button** puts the current position
of the Astrobee into the boxes in the Manual Move Inputs section (which then moves the Preview to that location
 via databinding).

### Location Bookmarks
The <b> Locations Bookmarks section</b> includes a **Create Location Bookmark** button. If you click it, you will be 
prompted for a name to save the current location and orientation of draggable Preview. Location bookmarks are stored in 
the `BookmarksList.json` config file. When you select a bookmark from the <b>locations dropdown</b>, the Control Station
puts the saved position and orientation into the boxes in the Manual Move Inputs section (which then moves the 
Preview to that location via databinding).

### Manual Move Inputs

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


### Options 
The <b>Options section</b> in the Bee Commanding subtab sets the list of options to send to the
 Astrobee when the
Apply Options button is clicked. If the checkbox to the left of the option is 
checked, the Apply Options button will send a command to turn on that option.
If there is a green checkmark to the right of the option, the Astrobee already has
that option turned on.  Options include:

* <b> Face Forward </b> (`SETTINGS_METHOD_SET_HOLONOMIC_MODE`) requires the Astrobee to face the direction of motion.
Unchecking this option allows the Astrobee to fly sideways or backwards, which may be
desirable to collect specific video or science data. However, the Astrobee does not 
have cameras pointing in all directions, so it must be monitored closely when
flying with Face Forward off so that it does not collide with an obstacle.
* <b>Check Obstacles</b> (`SETTINGS_METHOD_SET_CHECK_OBSTACLES`) requires the Astrobee to stop moving and station keep if
it detects an object in its planned flight path. It may be necessary to turn off
this option if the Astrobee detects a ghost obstacle and gets stuck.
*  <b>Check Keepouts</b> (`SETTINGS_METHOD_SET_CHECK_ZONES`) requires the Astrobee to stop before entering 
any keepout zone (keepout zones are designated with gray boxes in the Live Map subtab).
  It may be necessary to turn this option off if the Astrobee accidentally gets
knocked into a keepout zone.

<b>Apply Options </b> commands the Astrobee to turn on the options that 
are checked in the Options section and turn off the options that are not checked in
the Options section. This is done by sending the associated commands with the appropriate parameters. The checkmarks are updated by the 
`AgentState` message cached by the `AggregateAstrobeeState`.

> NOTE<br>
> The Face Forward, Check Obstacles, and Check Keepouts options should usually be turned on.

### Commands

The <b> Commands section</b> in the Bee Commanding subtab has buttons to send commands to the selected Astrobee.
It includes these buttons:

* <b>Station Keep </b> commands the Astrobee to cancel any movement commands and
station keep at its current position by sending the `MOBILITY_METHOD_STOPALLMOTION` command.

* <b> Move </b> commands the Astrobee to go to the position specified 
in the Manual Move Inputs section by sending the `MOBILITY_METHOD_SIMPLEMOVE6DOF` command.

## Perching Arm

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure12.PNG 
"Perching Arm subtab")

The <b>Perching Arm subtab</b> 
(`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.PerchingArmPart.java`)
sends commands related to the Astrobee's Perching 
Arm. The widgets on this subtab are created in `PerchingArmPartCreator.java`. The Perching Arm is designed to grasp a handrail
and secure the Astrobee while it conserves power by turning off its propulsion 
system. The Perching Arm subtab includes the following sections:


### Initialization
The <b> Initialization section</b> in the Perching Arm subtab
includes the Grab Control button discussed above.

### Pan and Tilt
The <b>Pan and Tilt section</b> in the Perching Arm subtab contains <b>text fields</b>
to enter angles for the pan and tilt joints of the arm. 
The <b>Pan and Tilt button</b> commands the Astrobee perching arm to move to the 
angles specified. The Pan and Tilt command (`ARM_METHOD_ARM_PAN_AND_TILT`) is intended
 to be sent only when the Astrobee is perched on a
handrail and providing video.  The input may be moved in 10 degree increments by
the arrows on either side of the text field, or the desired angle may be typed into the text field
directly.  When pan and tilt are both zero, the arm is fully extended.

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

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure13.PNG 
"Docking subtab")

The <b>Docking subtab</b> (`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.DockingPart.java`)
sends commands related to Docking. Widgets on this subtab are created in `DockingPartCreator.java`.
It includes these sections: 

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

* The <b>Station Keep Bee</b> button commands the Astrobee to cancel any movement commands and
station keep at its current position. (`MOBILITY_METHOD_STOPALLMOTION`)
* The <b> Send Bee to Docking Station </b> button commands the Astrobee to immediately return
to the Docking Station and dock itself (`SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN`). 
 The Astrobee will be able to execute the Dock Automatically command from
anywhere in the US section of the ISS.  The Astrobee will automatically choose an unoccupied berth
on the Docking Station.  The Astrobee plans a trajectory assuming the
modules are unobstructed.  If it detects an obstacle in its planned path, it will
stop and wait for the obstacle to move. (NB: As of July 2019, this command is not implemented in Astrobee
 Robot Software and there is no scheduled implementation date. To command Astrobee to dock, please use the manual
 Dock command on the Miscellaneous Commands tab, or on the Debugging tab.)

## Relative Commanding ##

The **Relative Commanding subtab** (`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.RelativeCommandingPart.java`)
 sends commands that move the position and orientation of the selected Astrobee, using relative coordinates (framename = 
 body). Widgets in this subtab are created in `RelativeCommandingPartCreator.java`.
 
  The Relative Commanding subtab is the same as the Bee Commanding subtab, except that it is bound to 
`gov.nasa.arc.verve.robot.freeflyer.parts.RobotPartRelativeDraggablePreview` and the **Move Relative button** sends  
`MOBILITY_METHOD_SIMPLEMOVE6DOF` with frame set to "body".

## Relative Commanding Text

The <b>Relative Commanding</b> subtab 
(`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.RelativeCommandingText.java`) 
is not included in the Crew Control Station; it is intended for use by engineers only.
It has ten commanding textbox-button pairs. There are ten pairs to allow the user to queue up relative commands for an 
efficient activity.

Each **textbox** allows the user to input a position relative to the robot's current position. The input must take 
the form "x, y, z, qx, qy, qz, qw". x, y, z are distances in meters along the robot's forward, right, and down axes, 
and qx, qy, qz, qw are components of a quaternion relative to Astrobee's current position.

Each **Move Relative** button commands the Astrobee to move to the position specified in its corresponding textbox. 
(`MOBILITY_METHOD_SIMPLEMOVE6DOF` with frame set to "body")

## Interactive Map

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

 The Preview has color coded arrows and rings that can be dragged to move it to
the desired position.  Red indicates Astrobee's forward direction and blue indicates Astrobee's
down direction. Interaction is done by creating a `VerveInteractable` and calling `VerveUserData` 
and `VerveInteractManager`. The Manual Move Inputs fields update automatically
when the Preview is dragged by its arrows, by the BeeCommandingPartOnTeleoperateTab having 
`ContextNames.TELEOP_TRANSLATION` and `TELEOP_ROTATION_RADIANS` enabled.

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure14.png 
"Interactive Map")

The <b>Camera Controls</b> box in the Interactive Map (`ArrowsDialogWithPreviewButton` class inside
 `Teleop3dView`) has these additional buttons:

* <b>Show</b> or <b>Hide Preview</b> displays or hides the white Preview model</li>
* <b>Zoom to Preview</b> centers the map on the Preview model. </li>
* <b>Snap Preview to Bee</b> moves the Preview to the current position of the selected Astrobee.
The coordinates in the Manual Move Inputs section will update accordingly.

## Miscellaneous Commands

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/MiscellaneousCommandsSubtab.png 
"Miscellaneous Commands subtab")

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
See [this](config_files.md) page under TeleopCommandsConfiguration.json
 for examples of items in the json file.
