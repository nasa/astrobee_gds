# Overview Tab

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure1.PNG
"Overview Tab")

The <b>Overview Tab</b> summarizes the states of known Astrobees, and sends wake commands to the Docking Station.
It includes the following unique subtabs:


### Bee Status

The <b>Bee Status subtab </b> displays a status summary
for each connected Astrobee. Code is in `OverviewTopPart.java` and `OverviewAstrobeeRow`
 (`gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience`)
  Each row has the following items:

*  <b> Name</b> is the name of the Astrobee described by the row.
* The <b> Comm light</b> is green if that Astrobee is connected and cyan otherwise.
* The <b>Health light</b> is green if all Astrobee systems are functional and orange
if any subsystems are disabled. The health light is cyan if the Astrobee is not connected.
* <b>Est Batt</b> displays the approximate number of minutes left 
that the Astrobee can operate before needing to recharge. 
If more than one hour is left, time is displayed as hours and minutes.
* <b>Control</b> shows the ID of the Control Station that has access control on the Astrobee.
An Astrobee
rejects most commands from Control Stations that do not have access control on it.
 Only one Control Station can have control of each Astrobee at one time.


### Docking Station Status and Commanding </h3>


The <b>Docking Station Status and Commanding subtab </b> 
(`gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience.DockingStationStatusAndCommandingPart.java`) 
displays the status of any Astrobees on the Docking Station, and it sends wake commands to Astrobees hibernating
on the Docking Station.

The Docking Station has two berths. Each berth accommodates one
Astrobee, and that Astrobee may be awake, hibernating, or unpowered. The <b>Berth One and Berth Two subpanels</b> 
display the name of the Occupant of that berth (or "Vacant") and the Status of the occupant if the berth is occupied.
 If the Astrobee on a berth is powered off, the Docking Station 
reports the Occupant of that berth as "Unknown", with Status "Unpowered". An unpowered Astrobee cannot be woken
 up by the Control Station. This section is populated by the DockState message.

The <b>Wake Commanding section</b>  lists the Astrobees that are hibernating on the Docking Station,
and lets the user select one to wake. The wake command (`ADMIN_METHOD_WAKE` or `ADMIN_METHOD_WAKE_SAFE`) takes the berth
number (1 or 2) as a parameter and is sent to the Dock.

The <b>Hibernate Commanding section</b> lists the Astrobees that are awake on the Docking Station,
and lets the user grab control of or hibernate the selected Astrobee. The <b> Hibernate button </b> sends the command
`ADMIN_METHOD_SHUTDOWN`, which puts the selected Astrobee into a powered down state in which it can accept only 
the Wake command. 


NOTE<br>
> If an Astrobee is hibernated while it is not docked at the Docking Station, it will not be 
able to be awakened via the Control Station. It will need to be awakened via the hardware Wake button,
 or docked manually.




