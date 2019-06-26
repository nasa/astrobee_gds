# Run Plan Tab #

![alt text](https://github.com/nasa/astrobee_gds/gov.nasa.arc.ff.ocu/helpfiles/Figure10.png 
"Run Plan Tab")

<p>
The <b>Run Plan Tab</b> controls and monitors the execution of Plans on one Astrobee, which is selected from the 
dropdown in the upper left. The Run Plan Tab has the following unique subtabs:
</p>

<h3 id="RunRobotCommanding"> Bee Commanding </h3>
<p>
The <b>Bee Commanding subtab</b> has the following items:
</p>

<ul>
<li>  <b> Select Plan ...  </b> opens a file dialog from which to choose a 
 Plan file to load to the selected Astrobee. </li> 
<li> When a valid Plan is selected,  <b>Load  </b> uploads the Plan file to the selected
Astrobee. Once a Plan is loaded, the Load button disables and the Live Plan subtab populates with 
a list of the steps in the Plan. </li> 
<li>   <b> Run  </b> starts or resumes execution of the loaded Plan on the selected Astrobee when the
 Plan Status is Paused. When a Plan is first loaded to the Astrobee, its Plan Status is Paused.</li>
<li> <b> Pause   </b> pauses execution of the Plan on the selected Astrobee.</li>
<li>  <b> Skip Step  </b> skips a step in the Plan when the Plan Status is Paused. Selecting
Skip Step multiple times skips multiple steps in the Plan.  When you click <b>Run</b>, execution
resumes at the step you have skipped to. </li>
<li> The <b> Description box </b> displays a short summary of the selected Plan. </li>

</ul>


<h3 id="LivePlan"> Live Plan </h3>
<p>
The <b>Live Plan subtab</b> displays details of the Plan that the selected Astrobee is executing. 
The <b>Plan Name</b> field shows the name of the plan that is currently loaded on the Astrobee. 
The <b>Plan Status</b> field shows the current status of the plan on the Astrobee. If no Plan is loaded,
the Plan Status is Idle.
The <b>Total Elapsed Time</b> field counts the hours, minutes, and seconds that the Plan
has been executing.

The <b>Plan Table</b> displays the contents of the Plan currently loaded on the selected Astrobee.
The Duration column lists the hours, minutes, and seconds each step took to complete.  If the 
step was momentary, for instance turning on a camera, no duration is listed. The Success
column lists Complete, Failed, or Skipped for each step in the Plan as the Plan is executed.
</p>

