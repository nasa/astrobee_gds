# Video Tab 

The <b> Video tab </b> displays all of the images streaming from one Astrobee, and allows the user to stop streaming from
all the cameras with one click.

## Video Control Part subtab

The <b> Video Control Part</b> (`gov.nasa.arc.verve.freeflyer.workbench.parts.engineering.VideoControlPart`)
 subtab has a Stop Camera Streaming button.  The Stop Camera Streaming button grabs
 control of Astrobee and sends commands to stop the NavCam, DockCam, and SciCam, from streaming video/images 
 (sends `setCameraStreaming` with parameter false, for each camera).

## Camera subtabs
The <b>SciCam subtab</b> is the same as the Live Video subtab on the Run Plan and Teleoperate tabs
(`gov.nasa.arc.verve.freeflyer.workbench.parts.engineering.LiveVideoPart.java`);
 it automatically displays video from the SciCam when the SciCam is
streaming.  The <b>Camera 2</b> and <b> Camera 3 subtabs</b> are the same as the Live Images subtab
 (`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.LiveImagesPart.java`) on the Run Plan and
Teleoperate tabs. You can select the Dock or the Navigation camera from the dropdown and it will show the images
 from those cameras when they are streaming.
