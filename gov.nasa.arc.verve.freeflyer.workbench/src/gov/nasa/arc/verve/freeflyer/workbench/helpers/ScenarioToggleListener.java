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

public interface ScenarioToggleListener {
	void setText(boolean selected);
	void setDefault(boolean selected);
	void setUsos(boolean selected);
	void setPlanTrace(boolean selected);
	void setModule(boolean selected, int i);
	void setCamera(boolean selected, String name);
	
	boolean isTextSelected();
	boolean isDefaultSelected();
	boolean isUsosSelected();
	boolean isPlanTraceSelected();
	boolean isModuleSelected(int i);
	boolean isCameraSelected(String name);
}
