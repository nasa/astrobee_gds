# Overview Tab

![alt text](https://github.com/nasa/astrobee_gds/gov.nasa.arc.ff.ocu/helpfiles/Figure1.png 
"Overview Tab")

<p>
The <b>Overview Tab</b> summarizes the states of known Astrobees, and sends wake commands to the Docking Station.
It includes the following unique subtabs:
</p>

<h3 id="AstrobeeStatus"> Bee Status </h3>
<p>
The <b>Bee Status subtab </b> displays a status summary
for each connected Astrobee.  Each row has the following items:
</p>
<ul>
<li>  <b> Name</b> is the name of the Astrobee described by the row.</li> 
<li> The <b> Comm light</b> is green if that Astrobee is connected and cyan otherwise.</li> 
<li> The <b>Health light</b> is green if all Astrobee systems are functional and orange
if any subsystems are disabled. The health light is cyan if the Astrobee is not connected.</li>
<li> <b>Est Batt</b> displays the approximate number of minutes left 
that the Astrobee can operate before needing to recharge. 
If more than one hour is left, time is displayed as hours and minutes.</li>
<li> <b>Control</b> shows the ID of the Control Station that has access control on the Astrobee.
An Astrobee
rejects most commands from Control Stations that do not have access control on it.
 Only one Control Station can have control of each Astrobee at one time. </li> 

</ul>

<h3 id="DockingStationStatus"> Docking Station Status and Commanding </h3>

<p>
The <b>Docking Station Status and Commanding subtab </b> displays the status of 
any Astrobees on the Docking Station, and it sends wake commands to Astrobees hibernating
on the Docking Station.
</p>

<p>
The Docking Station has two berths. Each berth accommodates one
Astrobee, and that Astrobee may be awake, hibernating, or unpowered. The <b>Berth One and Berth Two subpanels</b> 
display the name of the Occupant of that berth (or "Vacant") and the Status of the occupant if the berth is occupied.
 If the Astrobee on a berth is powered off, the Docking Station 
reports the Occupant of that berth as "Unknown", with Status "Unpowered". An unpowered Astrobee cannot be woken
 up by the Control Station.
</p>

<p>
The <b>Wake Commanding section</b>  lists the Astrobees that are hibernating on the Docking Station,
and lets the user select one to wake.
</p>

<p>
The <b>Hibernate Commanding section</b> lists the Astrobees that are awake on the Docking Station,
and lets the user grab control of or hibernate the selected Astrobee. The <b> Hibernate button </b>
 puts the selected Astrobee into a powered down state in which it can accept only 
the Wake command. 
</p>

<p style="border:3px; border-style:solid; text-align:center; padding: 1em;">
NOTE<br>
If an Astrobee is hibernated while it is not docked at the Docking Station, it will not be 
able to be awakened via the Control Station. It will need to be awakened via the hardware Wake button,
 or docked manually.
</p>


