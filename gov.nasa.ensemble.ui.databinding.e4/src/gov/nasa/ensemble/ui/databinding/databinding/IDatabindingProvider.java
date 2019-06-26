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
package gov.nasa.ensemble.ui.databinding.databinding;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Control;

/**
 * Generic databinding provider interface.
 * 
 * @author tecohen
 *
 */
public interface IDatabindingProvider {
	
	/**
	 * Clear any errors in this UI 
	 */
	public void clearErrors();
	
	/**
	 * Clear a given error
	 * @param control
	 * @return true if something was cleared
	 */
	public boolean clearError(Control control);
	
	/**
	 * Show all errors in this UI
	 */
	public void showErrors();
	
	/**
	 * Get the model affiliated with this UI
	 * @return
	 */
	public Object getModel();
	
	/**
	 * Set the model affiliated with the UI
	 * @param model
	 */
	public void setModel(Object model);
	
	/**
	 * Set a validation status on a given control
	 * @param control -- 
	 * @param status
	 */
	public void setStatus(Control control, IStatus status);
	
	/**
	 * @return true if the controls have valid values
	 */
	public boolean isValid();

}
