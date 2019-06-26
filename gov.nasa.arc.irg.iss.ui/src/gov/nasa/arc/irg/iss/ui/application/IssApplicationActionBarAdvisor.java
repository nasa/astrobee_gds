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
package gov.nasa.arc.irg.iss.ui.application;

import gov.nasa.arc.irg.iss.ui.control.LogComboControl;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class IssApplicationActionBarAdvisor extends ActionBarAdvisor {
	
	protected LogComboControl m_logComboControl = null;
	//ConnectionAndTimeContribution m_connectionAndTimeControl = null;
	
    public IssApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }
    
    @Override
    protected void fillCoolBar(ICoolBarManager coolBar) {
    	coolBar.setLockLayout(true);
    	
    	m_logComboControl = new LogComboControl("logStatus");
    	coolBar.add(m_logComboControl);
    	
    	//m_connectionAndTimeControl = new ConnectionAndTimeContribution("gpsTimeConnection");
    	//coolBar.add(m_connectionAndTimeControl);
    }
    
    
	public LogComboControl getLogComboControl() {
		return m_logComboControl;
	}

	public void setLogComboControl(LogComboControl logComboControl) {
		m_logComboControl = logComboControl;
	}

//	public ConnectionAndTimeContribution getConnectionAndTimeControl() {
//		return m_connectionAndTimeControl;
//	}
//
//	public void setConnectionAndTimeControl(
//			ConnectionAndTimeContribution connectionAndTimeControl) {
//		m_connectionAndTimeControl = connectionAndTimeControl;
//	}
	
}
