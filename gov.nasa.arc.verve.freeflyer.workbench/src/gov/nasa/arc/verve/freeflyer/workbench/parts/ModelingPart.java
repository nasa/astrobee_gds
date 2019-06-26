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
package gov.nasa.arc.verve.freeflyer.workbench.parts;

import gov.nasa.arc.verve.freeflyer.workbench.parts.handraileditor.HandrailBuilder;
import gov.nasa.arc.verve.freeflyer.workbench.parts.handraileditor.HandrailModel;
import gov.nasa.arc.verve.freeflyer.workbench.parts.handraileditor.HandrailModelingPart;
import gov.nasa.arc.verve.freeflyer.workbench.parts.keepouteditor.KeepoutBox;
import gov.nasa.arc.verve.freeflyer.workbench.parts.keepouteditor.KeepoutBuilder;
import gov.nasa.arc.verve.freeflyer.workbench.parts.keepouteditor.KeepoutModelingPart;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ardor3d.math.Vector3;

public class ModelingPart {

	private KeepoutModelingPart kmp;
	private HandrailModelingPart hmp;
	
	@Inject
	public ModelingPart(Composite parent, MApplication app) {
		Composite modelers = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		modelers.setLayout(layout);
		kmp = new KeepoutModelingPart(modelers, SWT.NONE, app);
		hmp = new HandrailModelingPart(modelers, SWT.NONE, app);
		app.getContext().set(KeepoutModelingPart.class, kmp);
		app.getContext().set(HandrailModelingPart.class, hmp);
	}
	
	@Inject @Optional
	public void setHandrailBuilder(@Named(ContextNames.HANDRAIL_BUILDER) HandrailBuilder builder) {
		hmp.setHandrailBuilder(builder);
	}
	
	@Inject @Optional
	public void setKeepoutBuilder(@Named(ContextNames.KEEPOUT_BUILDER_FOR_KEEPOUT_EDITOR) KeepoutBuilder builder) {
		kmp.setKeepoutBuilder(builder);
	}
	
	@Inject @Optional
	public void setHandrailsDirty(@Named(ContextNames.SAVE_HANDRAILS_ENABLED) boolean isDirty) {
		hmp.setDirty(isDirty);
	}
	
	@Inject @Optional
	public void updateKeepoutSelection(@Named(ContextNames.SELECTED_KEEPOUT) KeepoutBox keepoutBox) {
		if(keepoutBox == null) {
			kmp.setKeepoutSelected(false);
		} else {
			kmp.setKeepoutSelected(true);
		}
	}
	
	@Inject @Optional
	public void updateHandrailSelection(@Named(ContextNames.SELECTED_HANDRAIL) HandrailModel handrail) {
		if(handrail == null) {
			hmp.deactivate();
		} else {
			hmp.activate();
		}
	}
	
}
