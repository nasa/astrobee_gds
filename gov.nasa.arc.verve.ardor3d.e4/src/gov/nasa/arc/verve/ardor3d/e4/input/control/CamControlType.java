/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.ardor3d.e4.input.control;

public enum CamControlType {
	FollowCam("follow target"),
    NadirCam("top down view"),
    EarthCam("navigate relative to ground"),
	EgoCam("first person view"),
	;

	public final String comment;
	public final String description;

	CamControlType(String comment) {
		this.comment = comment;
		this.description = this.toString()+" ("+comment+")";
	}

	public static CamControlType fromString(String str) {
		if(str != null) {
			for(CamControlType cct : CamControlType.values()) {
				if(cct.toString().equals(str)) {
					return cct;
				}
			}
			for(CamControlType cct : CamControlType.values()) {
				if(cct.description.equals(str)) {
					return cct;
				}
			}
			for(CamControlType cct : CamControlType.values()) {
				if(cct.comment.equals(str)) {
					return cct;
				}
			}
		}
		return null;
	}
}
