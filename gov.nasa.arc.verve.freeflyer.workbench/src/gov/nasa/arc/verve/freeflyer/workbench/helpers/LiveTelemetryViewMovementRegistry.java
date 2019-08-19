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
package gov.nasa.arc.verve.freeflyer.workbench.helpers;

import gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView.LiveTelemetryView;

import java.util.ArrayList;
import java.util.List;

public class LiveTelemetryViewMovementRegistry {
	private static List<LiveTelemetryView> s_registry = new ArrayList<LiveTelemetryView>();
	
	public static void addView(LiveTelemetryView v3ve) {
		s_registry.add(v3ve);
	}
	
	public static void resetViews() {
		for(LiveTelemetryView v: s_registry) {
			v.reset();
		}
	}
	
	public static void zoomToBee() {
		for(LiveTelemetryView v: s_registry) {
			v.zoomToBee();
		}
	}
	
	public static void zoomToAbsolutePreview() {
		for(LiveTelemetryView v: s_registry) {
			v.zoomToAbsolutePreview();
		}
	}
	
	public static void zoomToRelativePreview() {
		for(LiveTelemetryView v: s_registry) {
			v.zoomToRelativePreview();
		}
	}
	
	public static void nudgeViewUp() {
		for(LiveTelemetryView v: s_registry) {
			v.nudgeUp();
		}
	}
	
	public static void nudgeViewDown() {
		for(LiveTelemetryView v: s_registry) {
			v.nudgeDown();
		}
	}
	
	public static void nudgeViewLeft() {
		for(LiveTelemetryView v: s_registry) {
			v.nudgeLeft();
		}
	}
	
	public static void nudgeViewRight() {
		for(LiveTelemetryView v: s_registry) {
			v.nudgeRight();
		}
	}
	

	public static void zoomIn() {
		for(LiveTelemetryView v: s_registry) {
			v.zoomIn();
		}
	}
	
	public static void zoomOut() {
		for(LiveTelemetryView v: s_registry) {
			v.zoomOut();
		}
	}
	
}
