# Run Plan Tab #

![alt text](https://github.com/nasa/astrobee_gds/blob/master/gov.nasa.arc.ff.ocu/helpfiles/Figure10.PNG 
"Run Plan Tab")


The <b>Run Plan Tab</b> controls and monitors the execution of Plans on one Astrobee, which is selected from the 
dropdown in the upper left. The Run Plan Tab has the following unique subtabs:


Bee Commanding

The <b>Bee Commanding subtab</b> (`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.BeeCommandingOnRunPlanTab.java`)
 has the following items:

*  <b> Select Plan ...  </b> opens a file dialog from which to choose a 
 Plan file to load to the selected Astrobee. When a `.fplan` file has been selected, the description box
  is populated
  with the description field from the plan file and a colored word displays if the plan is Valid or Invalid.
   <i>The
  Plan is not yet loaded on the Astrobee, so it is not displayed in the Live Map (3d view) or 
  Live Plan subtabs.</i>  
* When a valid Plan is selected,  <b>Load  </b> uploads the Plan file to the selected
Astrobee. Loading a Plan happens in two steps: first the BeeCommanding part tells the
 `CompressedFilePublisher` to send the Plan as a `CompressedFile` and the Astrobee returns a 
 `CompressedFileAck`. When the BeeCommanding part receives the  `CompressedFileAck`, it sends 
 a `PLAN_METHOD_SET_PLAN` command that tells Astrobee to update its plan to the latest one it 
 received.

Once a Plan is loaded, the Load button disables, the Live Plan subtab populates with 
a list of the steps in the Plan, and the Live Map (3d view) displays a model of the Plan. 
* <b> Run</b> (`PLAN_METHOD_RUN_PLAN`) starts or resumes execution of the loaded Plan on the selected Astrobee when the
 Plan Status is Paused. When a Plan is first loaded to the Astrobee, its Plan Status is Paused.
* <b> Pause</b> (`PLAN_METHOD_PAUSE_PLAN`) pauses execution o f the Plan on the selected Astrobee.
* <b> Skip Step  </b> (`PLAN_METHOD_SKIP_PLAN_STEP`) skips the next Command or Segment in the Plan when the 
Plan Status is Paused. You cannot skip.
past the end of the Plan. When you click <b>Run</b>, execution resumes at the step you have skipped to. </li>
* The <b> Description box </b> displays a short summary of the selected Plan.


### Live Plan 

The <b>Live Plan subtab</b> (`gov.nasa.arc.verve.freeflyer.workbench.parts.standard.LivePlanPart.java`)
 displays details of the Plan that the selected Astrobee is executing. The information comes from the 
 `PlanStatus` message.
* The <b>Plan Name</b> field shows the name of the plan that is currently loaded on the Astrobee. 
* The <b>Plan Status</b> field shows the current status of the plan on the Astrobee. If no Plan is loaded,
the Plan Status is Idle.
* The <b>Total Elapsed Time</b> field counts the hours, minutes, and seconds that the Plan
has been executing. The duration in the Total Elapsed Time field and the durations displayed for
 each step in the table below are measured by a timer in the Control Station, not reported by the
  Astrobee, so if communication is interrupted the durations displayed may be inaccurate.

The <b>Plan Table</b> displays the contents of the Plan currently loaded on the selected Astrobee.
The Duration column lists the hours, minutes, and seconds each step took to complete.  If the 
step was momentary, for instance turning on a camera, no duration is listed. The duration of
 individual steps is computed by the robot, so it is accurate despite LOS (unlike the duration shown for
 the entire plan.) The Success
column lists Complete, Failed, or Skipped for each step in the Plan as the Plan is executed.


