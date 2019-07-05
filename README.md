# Astrobee Ground Data System - Control Station software
Astrobee is a free-flying robot designed to operate as a payload inside the International 
Space Station. (Source code for Astrobee Flight software is available
 [here](https://github.com/nasa/astrobee))

The **Astrobee Control Station** is an Eclipse RCP application that can command and monitor up to three 
Astrobee robots or simulators. Astrobees are commanded via prewritten 
 **Plans**, or manually via single commands in **Teleoperate** mode.  Plans consist 
 of locations called Stations, tasks done at each Station, and Segments that connect the Stations.
  The Control Station also commands the Astrobee Docking Station to wake up Astrobees that are hibernating.

The Astrobee Control Station is an extension of the Visual Environment for Remote Virtual Exploration (VERVE) that has been customized to operate the Astrobee robot on the International Space Station (ISS). 

Astrobee Control Station Copyright © 2019, United States Government, as represented by the Administrator of the National Aeronautics and Space Administration. All rights reserved.

Verve Copyright © 2011, United States Government, as represented by the Administrator of the National Aeronautics and Space Administration. All rights reserved.

The Astrobee Control Station platform is licensed under the Apache License, Version 2.0 (the 
"License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

Astrobee Control Station includes a number of third party open source software listed below.  Find the complete listing of third-party software notices and licenses, see the separate “Astrobee Control Station Listing of Notices/Licenses, Including Third Party Software” pdf document in the LICENSE folder.
1.	A) Eclipse Core Runtime, B) Eclipse Core Filesystem, C) Eclipse Modeling Framework (EMF) ECore, and D) Eclipse Modeling Framework XMI, http://www.eclipse.org  
2.	Ardor3D , https://github.com/Renanse/Ardor3D  
3.	JOGL (Java OpenGL), http://jogamp.org/ 
4.	Apache Log4j, http://logging.apache.org/log4j/ 
5.	Codehaus Jackson https://github.com/codehaus/jackson  


The **Overview** tab summarizes the states of known Astrobees, and sends wake, grab control, and hibernate commands. The **Guest Science** tab sends Plans and some teleoperate commands to as many as three Astrobees simultaneously. The **Run Plan** and **Teleoperate** tabs allow detailed control and monitoring of a single Astrobee. These four tabs are also available to astronauts as the Crew Control Station.

The Engineering Control Station includes six additional tabs. The **Plan Editor** provides 
a graphical interface to create a Plan for one Astrobee. The **Advanced Guest Science** tab,
 like the simplified Guest Science tab available to crewmembers, commands and monitors three 
 Astrobees simultaneously, but it also allows the user to customize commands and to see Guest
  Science telemetry in greater detail. The **Advanced** and **Advanced 2** tabs display 
  detailed engineering telemetry from one Astrobee, including faults, operating limits, 
  power and disk usage, etc. The **Modeling** tab provides a graphical interface to edit 
  keepout/keepin files, which are sent to the robot and displayed in the Control Station 3d 
  view, and handrail files, which position handrail models within the Control Station 3d 
  view. The Modeling tab also includes a CSV to Fplan Converter to facilitate the construction 
  of repetitive plans with known coordinates. The **Debugging** tab displays engineering data 
  helpful for debugging DDS communication with the robot.

[Install instructions](docs/SETUP.md)

[Usage instructions and documentation](docs/USAGE.md)
