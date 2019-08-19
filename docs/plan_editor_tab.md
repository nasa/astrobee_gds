# Plan Editor tab #

The Plan Editor tab  allows you to create or edit a .fplan file to send to Astrobee. In the upper left the Plan
Editor shows a table view of the Plan you are editing (`gov.nasa.arc.verve.freeflyer.workbench.parts.planeditor.PlanEditorPart`).
On the right is a 3d view (`gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView.PlanEditor3dView`). The
section in the lower left shows a widget that corresponds to the Plan element that is selected in the Plan Editor.
All the widgets are subclasses of `gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget`.
When the Plan name is selected, the `gov.nasa.arc.verve.freeflyer.workbench.widget.FreeFlyerPlanWidget` shows. When 
you select a Station, `StationAndPointWidget` shows, and when you select a Segment, 
`gov.nasa.arc.irg.plan.ui.widget.SegmentWidget` shows. The other widgets, which are for commands, are all in the 
`gov.nasa.arc.verve.freeflyer.workbench.widget` package.

## Create a Plan ##
* From the Plan Editor Tab, go to File -> New Plan
  * Known bug: If you are on a Mac and the top menu bar does not say "FreeFlyer OCU", click on another 
  window (for instance a Finder window) and then select the Control Station again and the menu will appear 
  correctly. This happens when the Control Station is first started.
* Enter a name for the plan and click Save
  - Known bug: error about SWT key 13 when you hit enter instead of clicking Save.  Ignore the error and continue.
* Set inertia and operating limits configuration files for the plan by clicking on the plan name in the 
table and then selecting from the drop-down menus in the `FreeFlyerPlanWidget` in the lower left corner.
 These menus are populated by `InertiaConfigurations.json` and `OperatingLimitsConfigurations.json` (See 
 [Config Files](config_files.md) for information about the json files.)
* Add plan elements (see below)
* When you have finished making the plan, click the Validate button
  * This button calls `compilePlan()` in `gov.nasa.arc.irg.plan.ui.plancompiler.PlanCompiler`.
  * `compilePlan()` checks that the plan obeys keep-out and keep-in zones.
  * For each Segment, `compilePlan()` computes "waypoints" that the robot's control system needs to
  in order to execute the trajectory of the Segment
* When the plan has been validated, go to File -> Save Plan.  Saving the plan will also validate it, if it is not already validated.

## Add Stations ##
* Click the "Add" button.  A new Station will be created.
  * If the Plan is selected or the last Station is selected, the new Station will be created at the position of the last Station in the plan.
  * If a Station that is not the last Station is selected, the new Station will be between the selected Station and the next Station
  * If a Segment is selected, the new Station will be created in the middle of the Segment

* Right-click on an item in the Plan Editor
  * When the Plan is selected, you can "Append Station" to the end of the plan
  * When a Station is selected, you can "Append Station" to the end of the plan or "Insert Station" before this Station
  * When a Segment is selected, you can "Insert Station" in this Segment
* Click "Add via 3d View" button.  A translucent plane will show in the 3d view.
  * Click on the plane to add Stations where you click.  New Stations are appended to the end of the Plan.
  * Change the height of the plane using the "Plane Adjustment: Z" text box.
  *  When you are done adding Stations, click "Exit Add via 3d View" button.

* Segments are created automatically when you add Stations.

## Edit Plan Elements ##
### Plan ###
* Select the Plan itself by clicking the plan name in the Plan Editor table.
  * When the Plan is selected, you can change the inertia and operating limits configurations

### Stations ###
* Select a Station by clicking on the box in the 3D view or on the row in the table in the Plan Editor
* When a Station is selected, you can do the following:
  * Move the station
    * "Location Based" tab: Set position according to ISS location coding.
    * "Coordinate Based" tab: use up and down arrows to adjust x, y, z, roll, pitch, yaw, or type numbers directly into the text boxes
    * "Coordinate Based" tab: select Drag to Translate or Drag to Rotate checkbox to display draggable arrows on the selected station. 
    * "Bookmarks" tab: select the name of a bookmark from the dropdown menu and the Station will jump to the location of the Bookmark.
  * Add commands to the station
    * Click on "Commands" tab.
    * Select a command from the "Select command" dropdown.  The selected command will be added to the plan.
    * You can delete commands with the "Delete" button, and you can reorder or delete them by right-clicking on them in the table.
  * Delete the Station
    * Click the "Delete" button
    * Right click on the row in the Plan Editor, and choose "Delete"
  * Reorder the station (if the plan has multiple stations)
    * Right click on the row in the Plan Editor, and choose "Reorder Down" or "Reorder Up"
  * Insert a Station halfway between the selected and the previous station
    * Right click on the row in the Plan Editor and choose "Insert Station"
  * Insert a Station halfway between the selected and the next station
    * Select the Station and click the "Add" button

### Segments ###
* Select a Segment by clicking on the cone in the 3D view or on the row in the table in the Plan Editor
* When a Segment is selected, you can do the following:
  * Change the maximum speed of the segment in the Plan Editor
  * Uncheck Face Forward to tell the robot it doesn't need to face the direction of motion during this Segment
  * If you run with -engineering, you can also change acceleration, and angular velocity and acceleration limits.

### Commands ###
* Select a command by clicking on the line in the table in the Plan Editor

### Undo ###
* Some actions can be reversed by going to Edit -> Undo
  * Adding a Station and deleting a Station can be undone
  * Moving a Station via the Coordinate Based tab can be undone.
  
### Preview ###
* To preview the Plan, click Preview and a green preview model will follow the Plan trajectory at approximately
ten times speed.
  * The preview cannot be paused.
  * If a Segment translates and rotates simultaneously, the preview will interpolate 
  the translation only (the rotation will jump).

## Manage Bookmarks ##
* Under View menu, select Open Bookmark Manager. This opens 
`gov.nasa.arc.verve.freeflyer.workbench.dialog.BookmarkManagerDialog.java`
* To delete a bookmark:
   * Select the bookmark from the drop-down labeled Delete Bookmark
   * Click Delete.
* To create a new Bookmark:
   * Open a plan and select a Station in the plan.
   * Adjust the position of the current station using the Location Based or Coordinate Based tabs
   * Click on the Bookmarks tab and click "Make Bookmark..."
   * Enter a name for the new bookmark and click OK.
* Bookmarks are called Locations on the Teleoperate Tab.
