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
package gov.nasa.ensemble.ui.databinding.widgets.customization.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Simple converter from degrees to radians or vice versa
 * WARNING: you can use this OR the Multiplier annotation but not both.
 * @author tecohen
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Angle {
	public enum AngleType {DEGREES, RADIANS}
	
	/**
	 * The AngleType to display in the UI (note you must provide @UnitsLabel separately
	 * @return
	 */
	AngleType displayType();
	
	/**
	 * The AngleType to store in the model
	 * @return
	 */
	AngleType storedType();
	
	/**
	 * The class type of the stored value (ie Double)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	Class type();
	
}
