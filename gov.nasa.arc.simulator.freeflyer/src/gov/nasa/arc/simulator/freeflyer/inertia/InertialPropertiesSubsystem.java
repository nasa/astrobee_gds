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
package gov.nasa.arc.simulator.freeflyer.inertia;

import rapid.Command;
import rapid.Mat33f;
import rapid.ParameterUnion;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_INERTIA;


public class InertialPropertiesSubsystem {
	private static InertialPropertiesSubsystem INSTANCE;
	private InertialPropertiesPublisher inertialPropertiesPublisher;
	private String configName = "Default Inertia Config";
	private float mass = 5f;
	private Mat33f inertiaMatrix;

	public static InertialPropertiesSubsystem getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new InertialPropertiesSubsystem();
		}
		return INSTANCE;
	}

	private InertialPropertiesSubsystem() {
		inertialPropertiesPublisher = InertialPropertiesPublisher.getInstance();
		inertiaMatrix = new Mat33f();
		inertiaMatrix.userData = new float[] {1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};

		publish();
	}

	public void updateInertialProperties(Command cmd) {
		if(SETTINGS_METHOD_SET_INERTIA.VALUE.equals(cmd.cmdName)) {
			configName = ((ParameterUnion)cmd.arguments.userData.get(0)).s();
			mass = ((ParameterUnion)cmd.arguments.userData.get(1)).f();
			inertiaMatrix = ((ParameterUnion)cmd.arguments.userData.get(2)).mat33f();
			publish();
		}
	}
	
	
	private void publish() {
		inertialPropertiesPublisher.publishSample(configName, mass, inertiaMatrix);
	}
}
