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
package gov.nasa.ensemble.ui.databinding.validation;

/**
 * Simple converter to go from the model to the target and back.
 * Implementations should handle any special units themselves.
 * 
 * @author tecohen
 *
 */
public interface ISimpleConverter {

	/**
	 * Convert the model to the target (ui) format
	 * 
	 * @param model
	 * @return
	 */
	public Object toTarget(Object model);
	
	/**
	 * Convert the target (ui) to the model format
	 * 
	 * @param target
	 * @return
	 */
	public Object toModel(Object target);
	
	/**
	 * Get the class of the model type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class getModelType();
	
	/**
	 * Get the class of the target (ui) type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class getTargetType();

	
}
