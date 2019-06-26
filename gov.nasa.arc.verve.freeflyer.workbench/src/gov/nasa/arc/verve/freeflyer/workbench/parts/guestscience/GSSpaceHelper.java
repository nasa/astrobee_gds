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
package gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience;

public class GSSpaceHelper {

	public static final int selection_width = 2;
	public static final int control_width = 5;
	public static final int batt_width = 1;
	public static final int summary_width = 6;
	public static final int plan_width = 5;

	public static final int plan_status_width = 2;
	public static final int plan_step_width = 2;
	public static final int apk_width = 9;
	public static final int apk_status_width = 2;
	public static final int health_width = 1;
	
	public static final int comm_width = 1;
	public static final int title_width = 1;
	public static final int control_width_for_summary = 12;
	
	public static final int advancedCellsAcross = selection_width + control_width 
			+ batt_width + summary_width + plan_width + plan_status_width 
			+ plan_step_width + apk_width + apk_status_width + health_width + comm_width;
	
	public static final int simpleCellsAcross =  selection_width + control_width 
			+ batt_width + summary_width + plan_width + plan_status_width 
			+ health_width + comm_width;
	
	public static final int overviewCellsAcross = title_width + comm_width
			+ batt_width + control_width_for_summary + health_width;
	
	public static final int overviewDetailsButtonWidth = title_width + comm_width + health_width;
	
	public static final int detailsButtonWidth = selection_width + comm_width + health_width;
	
}
