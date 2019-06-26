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
package gov.nasa.arc.ff.ocu;

import gov.nasa.dds.rti.util.TypeSupportUtil;
import gov.nasa.rapid.v2.ui.e4.DdsInitializerBase;
import gov.nasa.rapid.v2.ui.e4.RapidV2UiPreferences;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;

import rapid.ext.astrobee.util.RapidExtAstroBeeTypeSupportUtil;

public class LifeCycleManager {


	@PostContextCreate
	public static void startDds(IEclipseContext context) {
		System.out.println("Java Version = " + System.getProperty("java.version"));
		ContextInjectionFactory.make(RapidV2UiPreferences.class, context);
		ContextInjectionFactory.make(DdsInitializerBase.class, context);
		TypeSupportUtil.addImpl(new RapidExtAstroBeeTypeSupportUtil());

	}

}
