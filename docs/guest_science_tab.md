# Guest Science Tab and Advanced Guest Science Tab 
![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/GuestScienceTab-annotated.png 
"Guest Science Tab")


The <b>Guest Science Tab</b>  and <b> Advanced Guest Science Tab</b> control up to three Astrobees 
simultaneously. The Advanced Guest Science tab has more features because it is intended to be used by 
engineers on the ground. The Guest Science tab has fewer features because it is intended to be used by 
crew on the ISS. 

>NOTE <br>
>Selecting an Astrobee in the Guest Science Tab will also select it in the Advanced Guest Science Tab, and
vice versa, even though the GUI may not indicate that the Astrobee is selected

The Guest Science tabs have the following unique subtabs:

<h3 id="AstrobeeSelection"> Bee Selection and Status </h3>

The <b>Bee Selection and Status subtab </b> displays a status summary
for each connected Astrobee. On the Advanced Guest Science tab the class is
`gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience.AdvancedGuestScienceTopPart`
 and on the Advanced Guest Science tab it is `SimplifiedGuestScienceTopPart`.
 
 Each row (`AdvancedAstrobeeRow` or `SimplifiedAstrobeeRow`, which ) has the following fields:

* <b> Name</b> shows the name of the Astrobee described by the row. Check the 
<b>checkbox</b> to the left of the name to send commands to that Astrobee.
* The <b>Comm light</b> is green if the Control Station has a DDS connection to that Astrobee.
Otherwise the Comm light is cyan.
* The <b>Health light</b> is green if all the Astrobee's systems are functional and orange
if any subsystems are disabled. The health light is cyan if the Astrobee is not connected.
* <b>Est Batt</b> displays the approximate number of minutes remaining 
that the Astrobee can operate before needing to recharge. 
If more than one hour is left, the time is displayed as hours and minutes.
* <b>Control</b> shows the ID of the Control Station that has access control on that Astrobee.
 Astrobees reject commands other than Grab Control and Stop from entities who
do not have control. Only one entity can have control of each Astrobee at
one time.
*  The<b> Summary </b> field displays `GUEST_SCIENCE_DATA` messages from Guest Science
Applications that are running on the Astrobee. To show in the summary field, the
 `GUEST_SCIENCE_DATA` message must be in json format with the key `Summary`. The 
 `GUEST_SCIENCE_DATA` messages are parsed by 
 `gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience.GuestScienceStateManager`.
 
 If Guest Science has not sent a message, and the Astrobee is running a Plan, the Summary field displays
the title of the current step in the Plan. If no Plan is running and no message has been sent, the field is blank. </li>
<li> <b>Plan</b> shows the name of the Plan currently loaded on the 
Astrobee. Each Astrobee can load one Plan at a time. </li>
<li> <b> Plan Status </b> may be Idle, Executing, Paused, or Error. When a Plan is initially loaded, the Plan
Status is Paused. When the Plan finishes executing, the Plan Status is Idle and a new Plan must be loaded.</li>

Additional status items on the Advanced Guest Science Astrobee Selection and Status subtab:

* <b>APK</b> is a dropdown that lets the user select from the APKs that are
 loaded on the Astrobee (info from `GUEST_SCIENCE_CONFIG`)
* <b>APK Status</b> shows the status of the APK that is selected in the dropdown (info from
`GUEST_SCIENCE_STATE`). The status can be Idle or Running.

The <b>Health Details button</b> opens a dialog that displays a Health 
listing for each of the selected Astrobees.

<h3 id="CommandingFor"> Commanding for (Selected Astrobees) </h3>

The <b>Commanding for (Selected Astrobees) subtab </b> sends commands to the 
Astrobees that are selected in the Bee Selection and Status subtab. It is `AdvancedGuestScienceSidePart` on the 
Advanced Guest Science tab and `SimplifiedGuestScienceSidePart` on the Guest Science tab.
 
 The names of the Astrobees it will send commands to are listed in the title. The
following commands are available:

* <b>Grab Control</b> grabs control of the selected Astrobees. If the Control Station 
does not have access control on all the selected Astrobees, only the Grab Control
button is enabled. The details of grabbing control are discussed on [this](USAGE.md) page. 
* <b>Select and Load Plans ...</b> opens a dialog box from which to select Plan files from the
file system, to load onto the selected Astrobees. When you have selected a Plan file, click the Load Plans button
 to send the Plans to the Astrobees. The details of loading a Plan are discussed on
  [this](run_plan_tab.md) page.
* <b>Run</b> starts execution of the Plans that are loaded on the selected Astrobees. If not
all selected Astrobees have Plans that are currently Paused, this command is not
available. The Run button sends `PLAN_METHOD_RUN_PLAN`.
* <b>Station Keep Bee(s)</b> commands the selected Astrobees to cancel any movement commands
and station keep at their current positions.  An awake Astrobee always executes a command to station keep
even if the command comes from a Crew Control Station that does not have access control. This button sends
 `MOBILITY_METHOD_STOPALLMOTION`.
* The <b>Guest Science Application dropdown</b>  selects an application that is
loaded on the selected Astrobee (info from `GUEST_SCIENCE_CONFIG`).
* The <b>Command dropdown</b> selects a Command to send to the selected
Guest Science application (info from `GUEST_SCIENCE_CONFIG`).
* <b>Send Command</b> sends the selected command to the selected Guest Science
application on the selected Astrobees.

Additional items in the Commanding For section on the Advanced Guest Science tab:

* The <b>APKs</b> section lets the user select an APK from the dropdown and start or stop that APK manually.
The dropdown lists only APKs that are on all of the selected Astrobees, so if more than one Astrobee is selected
and they do not have the same APKs on them, no APKs will be listed in the dropdown. The list of APK names comes
from `GUEST_SCIENCE_CONFIG` and the commands sent are `GUESTSCIENCE_METHOD_START_GUEST_SCIENCE` and 
`GUESTSCIENCE_METHOD_STOP_GUEST_SCIENCE`.
* In the <b>Manual Commanding</b> section, the APK dropdown lists the APKs common to the selected Astrobee.
The Template dropdown lists the commands available for the selected APK. When a command is selected from the
Template dropdown, it is displayed in the Command text box. The user can modify the command in the Command
text box before sending the command to the APK using the Send Command button. The options are populated by the
`GUEST_SCIENCE_CONFIG` the command sent is `GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE`.


### Guest Science Telemetry ###

The Guest Science Telemetry subtab is behind the Live Map subtab on the Guest Science tab or the Live
Telemetry subtab on the Advanced Guest Science tab. The Guest Science Telemetry subtab displays the status 
of all the APKs on an Astrobee (info from `GUEST_SCIENCE_STATE`) and it displays the `GUEST_SCIENCE_DATA`
 messages sent by each APK, as long as the `GuestScienceDataType` is `GUEST_SCIENCE_JSON`.
  The Guest Science Data messages can be sorted by APK name, topic, or label.
