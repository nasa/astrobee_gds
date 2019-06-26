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

public interface ISimpleStatus {
	
	/** Status severity constant (value 0) indicating this status represents the nominal case.
	 * This constant is also used as the status code representing the nominal case.
	 * @see #getSeverity()
	 * @see #isOK()
	 */
	public static final int OK = 0;

	/** Status type severity (bit mask, value 1) indicating this status is informational only.
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int INFO = 0x01;

	/** Status type severity (bit mask, value 2) indicating this status represents a warning.
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int WARNING = 0x02;

	/** Status type severity (bit mask, value 4) indicating this status represents an error.
	 * @see #getSeverity()
	 * @see #matches(int)
	 */
	public static final int ERROR = 0x04;

	/** Status type severity (bit mask, value 8) indicating this status represents a
	 * cancelation
	 * @see #getSeverity()
	 * @see #matches(int)
	 * @since 3.0
	 */
	public static final int CANCEL = 0x08;


	/**
	 * Returns the relevant low-level exception, or <code>null</code> if none. 
	 * For example, when an operation fails because of a network communications
	 * failure, this might return the <code>java.io.IOException</code>
	 * describing the exact nature of that failure.
	 *
	 * @return the relevant low-level exception, or <code>null</code> if none
	 */
	public Throwable getException();

	/**
	 * Returns the message describing the outcome.
	 * The message is localized to the current locale.
	 *
	 * @return a localized message
	 */
	public String getMessage();
	
	/**
	 * Returns the severity. The severities are as follows (in
	 * descending order):
	 * <ul>
	 * <li><code>CANCEL</code> - cancelation occurred</li>
	 * <li><code>ERROR</code> - a serious error (most severe)</li>
	 * <li><code>WARNING</code> - a warning (less severe)</li>
	 * <li><code>INFO</code> - an informational ("fyi") message (least severe)</li>
	 * <li><code>OK</code> - everything is just fine</li>
	 * </ul>
	 * <p>
	 * The severity of a multi-status is defined to be the maximum
	 * severity of any of its children, or <code>OK</code> if it has
	 * no children.
	 * </p>
	 *
	 * @return the severity: one of <code>OK</code>, <code>ERROR</code>, 
	 * <code>INFO</code>, <code>WARNING</code>,  or <code>CANCEL</code>
	 * @see #matches(int)
	 */
	public int getSeverity();

}
