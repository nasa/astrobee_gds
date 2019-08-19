## Requirements ##
* JDK 1.8
* [Eclipse Luna 4.4.2 with Modeling Tools](https://www.eclipse.org/downloads/packages/eclipse-modeling-tools/lunasr2)
* Git
* DDS implementation.
  * The Control Station was developed using the DDS implementation
   from [RTI](https://www.rti.com/products), and the instructions assume you are using that 
   implementation.
   * RTI provides an [Open Source version](https://www.rti.com/free-trial/open-source-projects)
    for Open Source projects. Other projects need to purchase a license to use RTI DDS.
   * If you are part of an educational institution and you do not need to modify the Control
   Station, you can obtain a binary version of the Control Station that includes the RTI DDS 
   libraries.

## Obtain the Source ##
* Clone this repo 

## Setup Eclipse ##
* Start Eclipse
  * Set the workbench location to be where you cloned the source

* Install required plugin
  * Under "Help" click "Install New Software"
  * Under the 'Work with' dropdown, select 'Luna' and wait for it to load available packages
  * Under 'General Purpose Tools' select 'Eclipse Plug-In Development Environment'
    * If 'Eclipse Plug-In Development Environment' is not available, see if "Hide items that are already installed" is
    checked. The plug-in may be installed already.
  * Click 'Next' and install the plug-in, restarting Eclipse

## Import source ##
* Under 'File' click 'Import ...'
* Under 'General' click 'Existing Projects into Workspace'
* Select the location where you checked out the source as the 'Root Directory'
* Select all of the projects it finds
* Uncheck 'Copy projects into workspace'
* Uncheck 'Search for nested projects'
* Click 'Finish'

## Run the Control Station for the first time ##
* In the Package Explorer, open `gov.nasa.arc.ff.ocu.product`, right-click on `ocu.product` and select “Run As”->”Run Configurations”.
  * Set “Program to Run” to “Run a product: gov.nasa.arc.ff.ocu.product”.
  * Click on the 'Environment' Tab
  * If you’re running it on a Mac, set an Environment variable (still under “Run
   Configurations”) `DYLD_LIBRARY_PATH` with the 
   value `${workspace_loc:/com.rti.dds.target/os/macosx/x86_64}` (actually type
    the words “workspace_loc”, do not substitute)
  * On Windows, the variable should be called `PATH` with value `${workspace_loc:/com.rti.dds.target/os/win32/x86}`.  Or substitue x86_64 if you have 64-bit Windows.
  * On Linux, set `LD_LIBRARY_PATH` to  `${workspace_loc:/com.rti.dds.target/os/linux/x86}` 
  or substitute `x86_64` for `x86` if you have 64-bit.
* Click the 'Run' button and the Control Station should start.
* If you have errors when starting the Control Station, often they can be solved by going to the “Run Configurations”->Plug-ins Tab and clicking the “Add Required Plug-ins” button that is all the way on the right (sometime the button is hidden if the window is too small).  Also try clicking “Validate Plug-ins” and that will usually tell you what plug-ins need to be included that aren’t included.

## Using the GDS Simulator ##

The GDS Astrobee simulator is a standalone Eclipse application that sends and receives (some) 
messages as the Astrobee would and can be used for very basic debugging. It does not do 
any kind of physics simulation of robot motion or simulate realistic camera views.  
If you need a software simulator which does run the same code that runs on the robot, one 
is included in the Astrobee Robot Software available [here](https://github.com/nasa/astrobee)

To run the GDS Astrobee Simulator:
* Go to the plugin `gov.nasa.arc.simulator.freeflyer`.  In the `src` folder, right click on `FreeFlyer.java` and run as a 
Java Application.
  * In Run Configurations Environment tab, add `DYLD_LIBRARY_PATH` or `PATH` as described above.
  * In Run Configurations Arguments tab, add `-agent Honey` or whatever name you want the simulator to have.
  * If simulator is on a different computer from the Control Station, run the Control Station with 
  `-peer <ip of simulator>`
  
There is also a GDS Dock Simulator. It sends Dock messages to the Control Station, and it can spawn a GDS Astrobee
simulator.

To run the GDS Dock Simulator:
- Go to the plugin `gov.nasa.arc.simulator.smartdock`. In the `src` folder, right click on `SmartDock.java` and run as a 
Java Application. 
  * In Run Configurations Environment tab, add `DYLD_LIBRARY_PATH` or `PATH` as described above.
  * In Run Configurations Arguments tab, add `-berth1 Honey` or whatever name you want the simulator to have. Alternatively,
  you can run with `-berth2 <robot>`. If nothing is specified for a berth, it will be reported as Vacant. You can 
  start a GDS Astrobee simulator by sending the Dock simulator a wake command for an occupied berth, but the Dock
  simulator cannot spawn two  Astrobee simulators.
  * If simulator is on a different computer from the Control Station, run the Control Station with 
  `-peer <ip of simulator>`
