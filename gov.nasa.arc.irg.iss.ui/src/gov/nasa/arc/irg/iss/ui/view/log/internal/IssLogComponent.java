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
package gov.nasa.arc.irg.iss.ui.view.log.internal;

import org.osgi.service.log.LogReaderService;

public class IssLogComponent {
	public void setLog(IssLogService log) {
		IssLogService.getInstance().setLog(log);
	}
	
	public void clearLog(IssLogService log) {
		IssLogService.getInstance().setLog(null);
	}
	
	public void setLogReader(LogReaderService value) {
		IssLogService.getInstance().setLogReader(value);
	}
}
