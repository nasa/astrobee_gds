# Running the Astrobee Control Station #

This page assumes you have followed the [setup instructions](SETUP.md)

## Set the Robot's IP Address in the Control Station
* To connect the Control Station to the robot or to a simulator, you need to give the Control Station the IP address of the robot or simulator. There are three ways to do this:
  * Run the Control Station with the command line argument `-peer <ip of robot>`
  * While the Control Station is running, go to **View->Edit Discovery Peers**.  Add the robot's IP address, click Save, and then restart the Control Station.
  * Edit the `NDDS_DISCOVERY_PEERS` file directly
    * If you are running the Control Station from inside Eclipse, it is found at `gov.nasa.rapid.v2.ui.e4/NDDS_DISCOVERY_PEERS`
    * If you are running the Control Station from a binary:
      * Start and quit the Control Station at least once
      * Navigate to `<binary install location>/configuration/org.eclipse.osgi/57/0/.cp/NDDS_DISCOVERY_PEERS`  (The folder 57 might also be 56 or 58 or some other nearby number.  It changes slightly with every build.)
* You should not have to put the IP address of the Control Station into the robot software, 
but sometimes it helps to do this if the robot and the Control Station are not connecting.
      
## Control Station Command Line arguments
The Control Station takes various command-line parameters to customize it. If you are running
 a binary executable on Mac, open the .app folder to run from the command line. Here are some parameters
 you can use.<br>
 Frequently used:
* `-agent[1|2|3]  <agent name>` Connect to an Astrobee with a name other than Honey, Bumble, or Queen
* `-domainId` Set the DDS Domain Id.  If this flag is not set, the domain defaults to 37.
* `-engineering` Creates fields in the Plan Editor tab to specify angular velocity, acceleration, and 
angular acceleration for each Plan Segment.  A negative value in one of those fields means that the default 
value for the plan will be used.
* `-peer <ip of robot>` Add the robot's IP address to `NDDS_DISCOVERY_PEERS`
* `-world [DetailIssWorld|GraniteLab]` Set the model in the 3d view inside the Control 
Station. If this parameter is not set, the Control Station defaults to a plain model of 
the ISS. `DetailIssWorld` displays an ISS model with photorealistic textures.
 `GraniteLab` displays a model of the granite table at Ames. `-world` also affects keep-in and keep-out zones for 
 the robot. See more details on the Config Files page.

Less frequently used:
* `-absoluteCrab` Adjust the sensitivity of the movement of the 3D view. Defaults to false.
* `-crab <#>` Adjust the sensitivity of the movement of the 3D view. Default is 0.0025.
* `-dolly <#>` Adjust the sensitivity of the movement of the 3D view. Default is 0.05.
* `-enlargeButtons` Enlarge the buttons in the Control Station for accessibility.
* `-fakeConnection` For debugging: do not report the robot as disconnected when the robot doesn't send pose telemetry
* `-noImages` Disable image and video streaming. Used to satisfy ISS imagery constraints.
* `-noZoomToClick` Do not recenter the 3d view when you click in it.
* `-precision <#>` Specify number of decimal places you want for the Stations in the plan.  For instance, for
 millimeter accuracy, run with `-precision 3`
* `-swing <#>` Adjust the sensitivity of the movement of the 3D view. Default is 0.0075.

Parameter strings are defined in `gov.nasa.arc.irg.plan.WorkbenchConstants.java ` and  
`gov.nasa.arc.irg.freeflyer.utils.VerveConstants.java`.

### Access Control ###
Control Stations require access control on an Astrobee to send commands to that Astrobee.
An Astrobee that is awake rejects commands sent by Control Stations that do not have access
control on it.   When an Astrobee first wakes up, nobody has access control on it.

> 1. Only one Control Station at a time can have access control on an Astrobee. <br>
> 2. The Control Station must "grab control" of an Astrobee before sending any commands.
 
Astrobee executes Grab Control and Station Keep commands regardless of whether the 
sender has access control. The Docking Station does not track access control, so you do not
need access control to send a Wake command.

### Tabs
Like all Eclipse 4 applications, the Control Station has an `Application.e4xmi` file 
(`gov.nasa.arc.ff.ocu`) that defines the layout of the application. Other plug-ins have a fragment.e4xmi
file that defines how classes "plug in" to the Application model. (If you are not familiar with Eclipse 4
there are several web tutorials about it; Lars Vogel's are very good.) Most of the classes are assigned
to a slot in the Application model in `gov.nasa.arc.verve.freeflyer.workbench/fragment.e4xmi`.

The Control Station has nine tabs that are represented in the Application model as Composite Parts in 
a Part Stack. Here is a list of tabs.  The Crew Control Station that is loaded on an ELC on the ISS for use
by crew, has only the first four tabs (not the Advanced Guest Science tab).

[Overview Tab](overview_tab.md) <br>
[Run Plan Tab](run_plan_tab.md) <br>
[Teleoperate Tab](teleoperate_tab.md) <br>
[Guest Science/Advanced Guest Science Tabs](guest_science_tab.md) <br>
[Engineering, Other, and Debugging Tabs](debugging_tab.md) <br>
[Video Tab](video_tab.md) <br>
[Plan Editor Tab](plan_editor_tab.md) <br>
[Modeling Tab](modeling_tab.md) <br>
[Config Files](config_files.md) <br>


# Items Common to Multiple Tabs #
## Live Map ##
![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure3.png
"Live Map subtab")

The **Live Map subtab** displays a 3D model of the position of the selected Astrobee 
in the USOS, according to the latest telemetry. The Live Map subtabs on various tabs are children of the
 parent class `gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView.LiveTelemetryView.java`.
  They display a world that is set up in 
  `gov.nasa.arc.verve.freeflyer.workbench.scenario.FreeFlyerScenario.java`.
  The Live Map subtab includes:

* A **model of the USOS**, with all rack locations labeled. Model files are in
 `gov.nasa.arc.verve.freeflyer.workbench/resources/{world}`, where `{world}` can be `IssWorld`, `GraniteLab`,
 or `DetailIssWorld`.
* **Astrobee models** at their currently reported locations. Each model has set of red,
green, and blue axes designating the X, Y, and Z directions, respectively, of the Astrobee itself.
Red, or X, designates the front of the Astrobee, and blue, or Z, designates the bottom of the Astrobee.
The Live Map shows a model of each Astrobee that is awake. Astrobee model files are in 
`gov.nasa.arc.verve.robot.freeflyer/models/astrobeeModel`.
* **X, Y, and Z coordinate axes** designating the orientation of the ISS-fixed 
coordinate system that the Astrobees use. The "world" frame for the Astrobees is the Space Station Analysis 
coordinate frame, which has its origin at the truss segment S0. When the Astrobees are on the Granite table at 
Ames, the world frame is defined to be in the center of the top surface of the table. The coordinate axes are
`gov.nasa.arc.verve.freeflyer.workbench.scenario.CoordinateFrame.java`.

The Live Map subtab may also display:
* Gray **keepout zones** designating areas Astrobees are not allowed to enter. Keepouts are defined in json files in
`gov.nasa.arc.verve.freeflyer.workbench/resources/{world}/keepouts` and read in to the 3D model by 
`gov.nasa.arc.verve.freeflyer.workbench.scenario.ZonesNodesHelper.java`.
* Blue **handrail** models. Model files are in the `{world}` folder that holds the ISS model. Their location is defined
by the HandrailConfig.json file in the {world} folder.
* A model of the Astrobee **Docking Station**.  The Docking Station model is in the `{world}` folder. The Granite Lab
 world has programmatically drawn boxes instead of the SmartDock model.
* A pastel **trace** showing the previous path of the Astrobee. To clear the trace, go to View->Scene Graph Controls in
the top menu bar and click "Reset Pose History."  The trace is drawn by 
`gov.nasa.arc.verve.robot.parts.concepts.RobotPartPoseHistory.java`.

When a Plan is loaded on the selected Astrobee, the Live Map subtab displays a plan trace. This is drawn by 
`gov.nasa.arc.verve.freeflyer.workbench.plantrace.RunPlanTrace.java`. The plan trace in the Interactive Plan Viewer
on the Plan Editor tab is drawn by `CreatePlanTrace.java` in the same package.  A PlanTrace incldes:
* Round **Station markers**.
* **Station numbers** above each Station. Stations are numbered 0, 1, 2, etc.
* Station color codes:
  * **Black**: pending 
  * **Bright green**: active 
  * **Gray**: completed 
  * **Blue**: skipped

* Lines representing **Segments** connect the Stations. An arrow along the line
points in the direction of motion. When the Segment is executing, the ID of the
Segment is displayed.  Segment IDs list the Stations they connect, for example, Segment 1-2
 connects Station 1 to Station 2. 

A **Camera Controls** box floats above the Live Map subtab. It is drawn by the inner class `ArrowsDialog` inside the
`LiveTelemetryView` class. If the Control Station window is moved or resized, you must click on another Control Station
subtab and click again on the Live Map subtab to position the Camera Controls box correctly.

* The **arrow buttons** in the Camera Controls box and on the keyboard 
move the camera in the Live Map view.
* The **magnifying glass buttons** zoom in and out.
* **Reset View** returns to the view of the JPM.
* **Zoom to Bee** centers the map on the selected Astrobee.

Click on the Camera Controls box and leave the cursor over it
 to use the keyboard. During camera movement, the camera stays pointed at the center
 of the Live Map view. You can also move the camera by clicking and dragging inside
 the Live Map view.  To change the center of the view, click and drag using the right
 mouse button.

## TopBar ##

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/TopBar.png 
"Top Bar")


On the Run Plan and Teleoperation tabs, the <b>Top Bar</b> under the Tab Panel allows the user to select 
an Astrobee to monitor and control. The Top Bar also displays summary information. Code is at
 `gov.nasa.arc.irg.freeflyer.workbench.parts.standard.TopBar.java`.

* The <b>Select Astrobee combo</b> displays the names of Astrobees that are awake. Select a name to
connect the Crew Control Station to that Astrobee. This dropdown depends on 
`gov.nasa.rapid.v2.e4.agent.ActiveAgentSet.java`
* The <b>Comm light</b> is green if the selected Astrobee has communication with the Crew Control Station.
Otherwise the light is cyan, indicating that no telemetry is being received and no commands can be sent.
This light is governed by `gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedRegistry.java`, which
depends on the `ActiveAgentSet`. Connection is determined by settings in `gov.nasa.rapid.v2.ui.e4.RAPID_QOS_PROFILES.xml`. 
Relevant settings include `participant_liveliness_assert_period` (on the robot), `participant_liveliness_lease_duration`,
and `max_liveliness_loss_detection_period`.
* <b>Est Batt</b> displays the approximate number of minutes that the selected Astrobee
can operate before needing to recharge. If more than one hour is left, the time is displayed as hours and minutes.</li>
* <b>Control</b> displays the ID of the Control Station that currently has access control
on the selected robot. When an Astrobee first wakes up, nobody has control on it. If the Control field does not match the
 id of the user of the Control Station, only Grab Control and Stop commands can be sent from the Control Station 
 to that Astrobee.
* The <b>Grab Control button</b> grabs access control on the selected Astrobee(s) if the Crew Control
Station does not already have access control.
  * When the Grab Control button is clicked, the Control Station sends an `ACCESSCONTROL_METHOD_REQUESTCONTROL`
message to the selected Astrobee.  The Astrobee sends back a `ACCESSCONTROL_STATE` message with a cookie, and
the Control Station then sends an `ACCESSCONTROL_METHOD_GRABCONTROL` with the cookie. If the cookie
does not match the last cookie the Astrobee sent, the GrabControl command will fail. This interaction
is mediated by the `GrabControlManager` in `gov.nasa.arc.irg.freeflyer.rapid.state` on the Control Station.
* The <b>Station Keep</b> button commands the selected Astrobee to cancel any movement commands and
station keep at its current position. (`MOBILITY_METHOD_STOPALLMOTION`)
* The <b>Stop Recording</b> button tells the selected Astrobee to stop recording data to disk 
(`DATA_METHOD_STOP_RECORDING`)
* The <b>Docking Station light</b> is green if the Docking Station is powered and connected.
It is cyan otherwise.</li>
* <b>GPS </b> displays the date and time. </li>

 
## Health  ##
![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure5.PNG 
"Health subtab")


The <b>Health subtab</b>, on the Run Plan and Teleoperate tabs, displays status information
 about the Astrobee selected in the Top Bar. The information comes from the 
 `AgentState`, `PlanStatus`, `ArmState`, and `FaultState`
  parts, as governed by the `AstrobeeStateAdapter` in the `AstrobeeStateManager` (`gov.nasa.arc.irg.freeflyer.rapid.state`).
 The code for the Health subtab is at `gov.nasa.arc.verve.freeflyer.workbench.parts.standard.HealthPart.java`.
  
  Which status information is included is defined in `HealthAndStatusConfig.txt`. It may include the following:

* The <b>Operating State</b> of the Astrobee may be Ready, Plan Execution, Teleoperation,
Auto Return, or Fault. In some Operating States, some commands are not available.
* The <b>Mobility State </b> of the Astrobee may be Drifting (propulsion off), Stopping, Stopped (station keeping),
Flying, Docking, Docked, Undocking, Perching, Perched, or Unperching. Docking, Undocking, Perching, and Unperching states include 
a number to indicate the progress of the Astrobee through the process. In some Mobility States, some commands are not available.
* <b>Plan Name</b> is the name of the Plan currently loaded on the selected Astrobee. Each Astrobee can load
one Plan at a time.
* <b> Plan Status </b> may be Idle, Executing, Paused, or Error. When a Plan is initially loaded, the Plan
Status is Paused. When the Plan finishes executing, the Plan Status is Idle and a new Plan must be loaded.
* Any fault reported in the FaultState that has an associated subsystem and is not a warning is displayed as a
 row, highlighted in orange, indicating if a particular subsystem is <b>disabled</b>. Each system that is unable to function is listed
on a separate row. Fault rows do not have to be specified in `HealthAndStatusConfig.txt`, they are always included. 


## Live Images ##
<p>
The <b>Live Images subtab</b> displays a succession of images from the selected Astrobee's Dock camera or
Navigation camera. Cameras can be commanded to stream images via Plan or via Teleoperate commands.

The info label on the Live Images subtab displays the sequence number of the image currently displayed. An icon
at the left of the subtab indicates whether the chosen camera points forward (Navigation camera)
or backward (Dock camera). On the Guest Science tab, the Live Images subtab includes a dropdown menu to select
an Astrobee from which to view images. Code is at 
`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.LiveImagesPart.java`
(for Run Plan, Teleoperate, and Video tabs), or `LiveImagesSelectAstrobeePart.java` (for GuestScience Tab).
</p>

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure6.PNG 
"Live Images subtab")


## Live Video ##
The <b>Live Video subtab</b> streams video from the Science camera
on the selected Astrobee.  The Science camera can be commanded to stream images via a Plan or via
 Teleoperate commands.  On the Guest Science tab, the Live Video subtab includes a dropdown menu to select
an Astrobee from which to view images. Code is in subclasses of
 `gov.nasa.arc.verve.freeflyer.workbench.parts.engineering.LiveVideoPart.java` and `LiveVideoComboPart.java`.
</p>

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure7.PNG 
"Live Video subtab")

## Status Bar ##

The <b>Status Bar</b> at the bottom of the Crew Control Station displays the latest message
sent or received from any Astrobee or the Docking Station. Code is at 
`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.LogButtonBar.java`.












