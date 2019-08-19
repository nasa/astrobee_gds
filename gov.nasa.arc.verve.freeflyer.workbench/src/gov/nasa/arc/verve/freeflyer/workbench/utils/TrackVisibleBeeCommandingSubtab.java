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

package gov.nasa.arc.verve.freeflyer.workbench.utils;

import java.util.Vector;

/**
 * This class keeps track of whether BeeCommanding, RelativeCommanding, or neither, is visible.
 * 
 * Not exactly the same as gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView.TabName
 * because that one helps track which 3d view is visible.
 * 
 * @author ddwheele
 *
 */
public class TrackVisibleBeeCommandingSubtab {
	final String OVERVIEW = "gov.nasa.arc.ff.ocu.compositepart.overview.tab";
	final String GUEST_SCIENCE = "gov.nasa.arc.ff.ocu.compositepart.guestScience.tab";
	final String RUN_PLAN = "gov.nasa.arc.ff.ocu.compositepart.run.tab";
	final String TELEOP = "gov.nasa.arc.ff.ocu.compositepart.teleop.tab";
	final String ADVANCED_GUEST_SCIENCE = "gov.nasa.arc.ff.ocu.compositepart.advancedGuestScience.tab";
	final String ENGINEERING = "gov.nasa.arc.ff.ocu.compositepart.engineering.tab";
	final String OTHER = "gov.nasa.arc.ff.ocu.compositepart.other.tab";
	final String DEBUGGING = "gov.nasa.arc.ff.ocu.compositepart.debug.tab";
	final String VIDEO = "gov.nasa.arc.ff.ocu.compositepart.video.tab";
	final String PLAN_EDITOR  ="gov.nasa.arc.ff.ocu.compositepart.planEditor.tab";
	final String MODELING = "gov.nasa.arc.ff.ocu.compositepart.modeling.tab";
	
	private final Vector<String> parents; // main tabs
	
	final String BEE_COMMANDING = "gov.nasa.arc.verve.freeflyer.workbench.part.manualCommanding";
	final String PERCHING = "gov.nasa.arc.verve.freeflyer.workbench.part.perchingArm";
	final String DOCKING = "gov.nasa.arc.verve.freeflyer.workbench.part.docking";
	final String RELATIVE_COMMANDING = "gov.nasa.arc.verve.freeflyer.workbench.part.relativeCommanding";
	final String RELATIVE_COMMANDING_TEXT = "gov.nasa.arc.verve.freeflyer.workbench.part.relativeCommandingText";
	
	private final Vector<String> siblings; // subtabs of interest
	
	String topParent; // currently selected tab
	String topSibling; // subtab of interest that is on top
	boolean absolutePreviewShowing = false;
	boolean relativePreviewShowing = false;
	boolean absoluteWasLastOnTop = true; 
	// true if we last saw BeeCommanding, false if we last saw RelativeCommanding
	
	public static TrackVisibleBeeCommandingSubtab INSTANCE = new TrackVisibleBeeCommandingSubtab();
	
	private TrackVisibleBeeCommandingSubtab() {
		parents = new Vector<String>();
		parents.add(OVERVIEW);
		parents.add(GUEST_SCIENCE);
		parents.add(RUN_PLAN);
		parents.add(TELEOP);
		parents.add(ADVANCED_GUEST_SCIENCE);
		parents.add(ENGINEERING);
		parents.add(OTHER);
		parents.add(DEBUGGING);
		parents.add(VIDEO);
		parents.add(PLAN_EDITOR);
		parents.add(MODELING);
		
		siblings = new Vector<String>();
		siblings.add(BEE_COMMANDING);
		siblings.add(PERCHING);
		siblings.add(DOCKING);
		siblings.add(RELATIVE_COMMANDING);
		siblings.add(RELATIVE_COMMANDING_TEXT);
		
		topParent = OVERVIEW;
		topSibling = BEE_COMMANDING;
	}
	
	public void ingestPartBroughtToTop(String onTop) {
		if(parents.contains(onTop)) {
			topParent = onTop;
			return;
		}
		if(siblings.contains(onTop)) {
			topSibling = onTop;
			if(onTop.equals(BEE_COMMANDING)) {
				absoluteWasLastOnTop = true;
			}
			else if(onTop.equals(RELATIVE_COMMANDING)) {
				absoluteWasLastOnTop = false;
			}
		}
	}
	
	public boolean isBeeCommandingOnTop() {
		if(topParent.equals(TELEOP)) {
			if(topSibling.equals(BEE_COMMANDING)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isRelativeCommandingOnTop() {
		if(topParent.equals(TELEOP)) {
			if(topSibling.equals(RELATIVE_COMMANDING)) {
				return true;
			}
		}
		return false;
	}
	
	public void setAbsolutePreviewShowing(boolean showing) {
		absolutePreviewShowing = showing;
	}
	
	public void setRelativePreviewShowing(boolean showing) {
		relativePreviewShowing = showing;
	}
	
	public boolean isAbsolutePreviewShowing() {
		if(topParent.equals(TELEOP)) {
			if(absoluteWasLastOnTop) {
				return absolutePreviewShowing;
			}
		}
		return false;
	}
	
	public boolean isRelativePreviewShowing() {
		if(topParent.equals(TELEOP)) {
			if(!absoluteWasLastOnTop) {
				return relativePreviewShowing;
			}
		}
		return false;
	}
}
