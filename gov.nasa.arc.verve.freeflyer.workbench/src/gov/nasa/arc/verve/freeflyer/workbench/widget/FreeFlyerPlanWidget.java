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
package gov.nasa.arc.verve.freeflyer.workbench.widget;

import gov.nasa.arc.irg.plan.converters.InertiaConfigToIntConverter;
import gov.nasa.arc.irg.plan.converters.IntToInertiaConfigConverter;
import gov.nasa.arc.irg.plan.converters.IntToOperatingLimitsConfigConverter;
import gov.nasa.arc.irg.plan.converters.OperatingLimitsConfigToIntConverter;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList;
import gov.nasa.arc.irg.plan.ui.io.InertiaConfigListLoader;
import gov.nasa.arc.irg.plan.ui.io.OperatingLimitsConfigListLoader;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FreeFlyerPlanWidget extends AbstractFreeFlyerWidget {
	private Combo inertiaCombo;
	private InertiaConfigList inertiaConfigList;
	private Combo operatingLimitsCombo;
	private OperatingLimitsConfigList operatingLimitsConfigList;
	private Text notesText;

	public FreeFlyerPlanWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	void setupCustomControls() {
		Label l = new Label(this, SWT.NONE);
		l.setText("Inertia File");
		addChildLabel(l);

		try { 
			inertiaConfigList = InertiaConfigListLoader.getStandardConfig();

			inertiaCombo = new Combo(this, SWT.READ_ONLY);
			inertiaCombo.setItems(inertiaConfigList.getArrayOfNames());	
			addChildControl(inertiaCombo);
		} catch (Exception e) {
			showErrorDialog(shell, "Error Reading Inertia Config File", e.getMessage());
		}
			new Label(this, SWT.NONE);

			l = new Label(this, SWT.NONE);
			l.setText("Operating Limits");
			addChildLabel(l);
		try {
			operatingLimitsConfigList = OperatingLimitsConfigListLoader.getStandardConfig();

			operatingLimitsCombo = new Combo(this, SWT.READ_ONLY);
			operatingLimitsCombo.setItems(operatingLimitsConfigList.getArrayOfNames());	
			addChildControl(operatingLimitsCombo);

		} catch (Exception e) {
			showErrorDialog(shell, "Error Reading Operating Limits Config File", e.getMessage());
		}

		new Label(this, SWT.NONE);

		l = new Label(this, SWT.None);
		l.setText("Description");
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING).grab(true, true).applyTo(l);
		addChildLabel(l);

		notesText = new Text(this,  SWT.MULTI | SWT.BORDER | SWT.WRAP );
		GridDataFactory.fillDefaults().grab(true, true).span(2,1).applyTo(notesText);
		addChildControl(notesText);
	}

	@Override
	public boolean bindUI(Realm realm) {
		if (getModel() == null){
			setBound(false);
			return false;
		}

		updateNonBoundFields();

		boolean result = true;
		result &= bindSpecial("inertiaConfiguration", inertiaCombo, new IntToInertiaConfigConverter(), new InertiaConfigToIntConverter());
		result &= bindSpecial("operatingLimits", operatingLimitsCombo, new IntToOperatingLimitsConfigConverter(), new OperatingLimitsConfigToIntConverter());
		result &= bind("notes", notesText);

		return result;
	}

}
