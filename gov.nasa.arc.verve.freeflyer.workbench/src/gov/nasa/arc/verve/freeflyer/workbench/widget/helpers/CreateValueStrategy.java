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
package gov.nasa.arc.verve.freeflyer.workbench.widget.helpers;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class CreateValueStrategy {
	final static int angleMax = 360;
	final static int angleMin = -360;
	final static int lengthMax = 20;
	final static int lengthMin = -20;
	final static int toleranceMin = 0;
	final static int toleranceMax = 10;
	
	public static UpdateValueStrategy getTargetToModelStrategy(String feature){
		if(feature.equals("stopOnArrival")) {
			return null;
		}

		// define a validator to check that only numbers are entered
		IValidator validator = new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value instanceof String) {
					if (((String)value).matches("-?\\d*\\.?\\d*")) {
						float val = Float.valueOf((String)value);
						if( val > toleranceMin && val < toleranceMax ) {
							return ValidationStatus.ok();
						} else {
							return ValidationStatus.error("Enter number between "+toleranceMin+" and "+toleranceMax+".");
						}
					}
					return ValidationStatus.error(value.toString() +" is not a valid number");
				}
				return ValidationStatus.error(value.toString() +" is invalid");
			}
		};

		// define a validator to check that only numbers are entered
		IValidator lengthValidator = new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value instanceof String) {
					if (((String)value).matches("-?\\d*\\.?\\d*")) {
						float val = Float.valueOf((String)value);
						if( val > lengthMin && val < lengthMax  ) {
							return ValidationStatus.ok();
						} else {
							return ValidationStatus.error("Enter number between "+lengthMin+" and "+lengthMax+".");
						}
					}
					return ValidationStatus.error(value.toString() +" is not a valid number");
				}
				return ValidationStatus.error(value.toString() +" is invalid");
			}
		};

		// define a validator to check that only numbers are entered
		IValidator angleValidator = new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value instanceof String) {
					if (((String)value).matches("-?\\d*\\.?\\d*")) {
						float val = Float.valueOf((String)value);
						if( val > angleMin && val < angleMax  ) {
							return ValidationStatus.ok();
						} else {
							return ValidationStatus.error("Enter number between "+angleMin+" and "+angleMax+".");
						}
					}
					return ValidationStatus.error(value.toString() +" is not a valid number");
				}
				return ValidationStatus.error(value.toString() +" is invalid");
			}
		};

		// create UpdateValueStrategy and assign
		// to the binding
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		
		if(feature.equals("coordinate.x") || feature.equals("coordinate.y") || feature.equals("coordinate.z")) {
			strategy.setAfterGetValidator(lengthValidator);
		} else if(feature.equals("coordinate.roll") || feature.equals("coordinate.pitch") || feature.equals("coordinate.yaw")) {
			strategy.setAfterGetValidator(angleValidator);
		} else {
			// create UpdateValueStrategy and assign
			// to the binding
			// Just don't give it a validator, because what is it anyway? 5/11/16
			//strategy.setAfterGetValidator(validator);
			strategy.setAfterGetValidator(null);
		}
		return strategy;
	}
}
