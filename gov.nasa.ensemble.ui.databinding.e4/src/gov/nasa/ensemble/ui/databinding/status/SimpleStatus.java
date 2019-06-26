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
package gov.nasa.ensemble.ui.databinding.status;


public class SimpleStatus implements ISimpleStatus {

	public static final ISimpleStatus OK_STATUS = new SimpleStatus(OK, "ok", null);

	protected int m_severity = OK;
	protected String m_message;
	protected Throwable m_exception = null;
	
	/**
	 * Creates a new status object.  The created status has no children.
	 *
	 * @param severity the severity; one of <code>OK</code>, <code>ERROR</code>, 
	 * <code>INFO</code>, <code>WARNING</code>,  or <code>CANCEL</code>
	 * @param pluginId the unique identifier of the relevant plug-in
	 * @param code the plug-in-specific status code, or <code>OK</code>
	 * @param message a human-readable message, localized to the
	 *    current locale
	 * @param exception a low-level exception, or <code>null</code> if not
	 *    applicable 
	 */
	public SimpleStatus(int severity, String message, Throwable exception) {
		setSeverity(severity);
		setMessage(message);
		setException(exception);
	}
	
	/**
	 * Construct an error status with the given message
	 * @param message
	 * @return
	 */
	public static ISimpleStatus error(String message){
		return new SimpleStatus(ERROR, message, null);
	}
	
	/**
	 * Construct an error status with the given exception
	 * @param exception
	 * @return
	 */
	public static ISimpleStatus error(Throwable exception){
		return new SimpleStatus(ERROR, exception.getMessage(), exception);
	}
	
	public Throwable getException() {
		return m_exception;
	}

	public String getMessage() {
		return m_message;
	}

	public int getSeverity() {
		return m_severity;
	}
	
	public void setSeverity(int severity) {
		m_severity = severity;
	}

	public void setMessage(String message) {
		m_message = message;
	}

	public void setException(Throwable exception) {
		m_exception = exception;
	}
	

}
