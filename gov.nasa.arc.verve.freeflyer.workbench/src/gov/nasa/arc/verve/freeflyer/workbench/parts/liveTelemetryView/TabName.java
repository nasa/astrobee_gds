/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView;

import java.util.Vector;

public enum TabName {
	// this rigamarole is necessary because, documentation to the contrary,
	// IPartListeners do not always get called when they are shown/hidden
	// so we have to listen to EVERYTHING
	OVERVIEW ("gov.nasa.arc.ff.ocu.compositepart.overview.tab",
			"gov.nasa.arc.verve.freeflyer.workbench.part.liveMap"),
	PLAN_EDITOR ("gov.nasa.arc.ff.ocu.compositepart.planEditor.tab"),
	RUN_PLAN ("gov.nasa.arc.ff.ocu.compositepart.run.tab",
			"gov.nasa.arc.verve.freeflyer.workbench.part.runPlan3dView"),
	TELEOP ("gov.nasa.arc.ff.ocu.compositepart.teleop.tab",
			"gov.nasa.arc.verve.freeflyer.workbench.part.teleop3dView"),
	GUEST_SCIENCE ("gov.nasa.arc.ff.ocu.compositepart.guestScience.tab",
			"gov.nasa.arc.verve.freeflyer.workbench.part.simplifiedGuestScience3dView"),
	ADVANCED_GUEST_SCIENCE ("gov.nasa.arc.ff.ocu.compositepart.advancedGuestScience.tab",
			"gov.nasa.arc.verve.freeflyer.workbench.part.advancedGuestScience3dView"),
	ADVANCED ("gov.nasa.arc.ff.ocu.compositepart.engineering.tab",
			"gov.nasa.arc.ff.ocu.compositepart.other.tab"),
	MODELING ("gov.nasa.arc.ff.ocu.compositepart.modeling.tab"),
	DEBUGGING ("gov.nasa.arc.ff.ocu.compositepart.debug.tab"),
	VIDEO ("gov.nasa.arc.ff.ocu.compositepart.video.tab",
			"gov.nasa.arc.verve.freeflyer.workbench.part.sciCam",
			"gov.nasa.arc.verve.freeflyer.workbench.part.camera2",
			"gov.nasa.arc.verve.freeflyer.workbench.part.camera3"),
	CUSTOM_ENGINEERING ("gov.nasa.arc.ff.ocu.compositepart.customEngineeringTab"),
	// these are nested tabs that cover a 3d view even if its main tab is showing
	DAMPERS("gov.nasa.arc.verve.freeflyer.workbench.part.runPlanImages",
			"gov.nasa.arc.verve.freeflyer.workbench.part.runPlanVideo",
			"gov.nasa.arc.verve.freeflyer.workbench.part.teleopImages",
			"gov.nasa.arc.verve.freeflyer.workbench.part.teleopVideo",
			"gov.nasa.arc.verve.freeflyer.workbench.part.guestScienceImages",
			"gov.nasa.arc.verve.freeflyer.workbench.part.guestScienceVideo",
			"gov.nasa.arc.verve.freeflyer.workbench.part.advancedGuestScienceImages",
			"gov.nasa.arc.verve.freeflyer.workbench.part.advancedGuestScienceVideo",
			"gov.nasa.arc.verve.freeflyer.workbench.part.guestScienceTelemetry",
			"gov.nasa.arc.verve.freeflyer.workbench.compositepart.guestScienceTelemetry",
			"gov.nasa.arc.verve.freeflyer.workbench.part.sciCam"
			),
	UNKNOWN ("");

	private final Vector<String> keyPartName;

	TabName(String... args) {
		keyPartName = new Vector<String>();

		for(String a : args) {
			keyPartName.add(a);
		}
	}

	public boolean matches(String partId) {
		for(String name : keyPartName) {
			if(name.equals(partId)) {
				return true;
			}
		}
		return false;
	}

	public Vector<String> getKeyPartName(){
		return keyPartName;
	}
	
	public boolean conflictsWith(String partId) {
		if(matches(partId)) {
			return false; // if I match, I don't conflict
		}
		for(TabName tn : TabName.values()) {
			if(tn.matches(partId)) {
				return true; // if I match another guy, I conflict
			} 
		}
		return false; // it was not a part we know about
	}
}
