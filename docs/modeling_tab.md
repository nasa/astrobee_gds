# Modeling Tab #

The Modeling tab lets you graphically adjust keepout and handrail files, and use a utility that creates a Plan starting
from a .csv file.

### Edit keepout zones #
The Keepout Editor code is at `gov.nasa.arc.verve.freeflyer.workbench.parts.keepouteditor.KeepoutModelingPart`
* Go to the top menu Modeling->New Keepout File to create a new keepout file, or Modeling->Open Keepout File to open 
an existing one. The keepout file should be in the folder 
`gov.nasa.arc.verve.freeflyer.workbench/resources/{world}/keepouts` for the `{world}` your keepout should be in.
* Add a new keepout by clicking the "Add Keepout" button.
  * It will place a new keepout at the center of where the 3d View is focused.
  * (The 3d view defaults to pivot around the center of the JEM.  You can change the center by clicking on an object in the 3d View, or by panning left or right (right click and drag).  Zooming and pivoting in the 3d view do not change the center).
* Select an existing keepout (gray box) by clicking on it.  It will turn yellow and show a blue sphere on the upper
aft port corner and a green sphere on the lower forward starboard corner.
* When a keepout is selected:
  * Move the entire keepout by changing the text in the text boxes labeled Center.
    * Use the arrow buttons or type directly into the boxes.
  * Resize the keepout (change position of the corners) by changing the text in the text boxes labeled 
  Upper Corner and Lower Corner (text is color-coded to match the spheres in the 3d view)
    * Use the arrow buttons or type directly into the boxes.
  * Delete the keepout by clicking "Delete".
  * Confirm your changes by clicking "Confirm" or clicking on the background
  * Discard your changes by clicking "Cancel"
* Save the keepout file by going to Modeling->Save Keepout File.
* Close the keepout file with Modeling->Close Keepout File.  You must close the keepout file before you can open another one.

### Edit Handrail File #
The handrail editor code is at `gov.nasa.arc.verve.freeflyer.workbench.parts.handraileditor.HandrailModelingPart`
* There is only one handrail file, so you do not need to open or close it. It is at
 `gov.nasa.arc.verve.freeflyer.workbench/resources/HandrailConfig.json`
* Add a new handrail by clicking the "Add Handrail" button.
  * It will place a new handrail at the center of where the 3d View is.
* Select an existing handrail by clicking on it.  It will change turn yellow.
* When a handrail is selected:
  - Move the handrail by changing the text in the text boxes labeled Handrail Position.
    * Use the arrow buttons or type directly into the boxes.
  * Orient the handrail by selecting an Alignment Wall from the dropdown
    * This will put the feet of the handrail toward the selected wall, as if it were attached to that wall.
    * Rotate it by 90 degrees by using the Horizontal checkbox.
      * This checkbox controls whether the handrail is parallel or perpendicular to the vertical direction of the bay.
  * Delete it by clicking "Delete".
  * Confirm your changes by clicking "Confirm" or clicking on the background
  * Discard your changes by clicking "Cancel"
* Save the handrail file by clicking "Save Handrails"

### Make a Plan Starting from a .CSV File #

The code for this section is in `gov.nasa.arc.verve.freeflyer.workbench.parts.CSVConverterPart` and it calls 
`gov.nasa.arc.verve.freeflyer.workbench.utils.CSVPlanConverter` to create the Plan.

1. Write a .csv file listing Station positions
   * Each row is a Station
   * A row can include x, y, z (in meters), and roll, pitch, yaw in DEGREES.<br>
For example:<br>
`-0.5,-0.5,0,0,0,-90 `<br>
`-0.5,0.5,0,0,0,0`<br>
`0.5,0.5,0,0,0,90`<br>
`0.5,-0.5,0,0,0,180`<br>
   * A row can include x, y, z (in meters), and the Station will have 
   orientation = N/A <br>
   For example: <br>
`-0.5,-0.5,`<br>
`-0.5,0.5,0`<br>
`0.5,0.5,0`<br>
`0.5,-0.5,0`<br>
2. In the workbench, open the Modeling Tab
3. At the far right, click the File button and select your .csv file
4. Type what you want your plan to be called in the text box.  You do not need to include an extension.
5. Click the Convert button.  A .fplan file will be created in the same folder as your .csv file and you should receive a success message.
6. Open your new plan in the Plan Editor and validate it before you run it on the robot.

