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

import gov.nasa.arc.irg.plan.converters.IntToPlannerConverter;
import gov.nasa.arc.irg.plan.converters.PlannerToIntConverter;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import rapid.ext.astrobee.SETTINGS_PLANNER_TYPE_QUADRATIC_PROGRAM;
import rapid.ext.astrobee.SETTINGS_PLANNER_TYPE_TRAPEZOIDAL;

public class SetPlannerWidget extends AbstractFreeFlyerWidget {
	private Combo plannerCombo;

	public SetPlannerWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	void setupCustomControls() {
		plannerCombo = new Combo(this, SWT.READ_ONLY);

		plannerCombo.setItems(new String[]{
				SETTINGS_PLANNER_TYPE_TRAPEZOIDAL.VALUE,
				SETTINGS_PLANNER_TYPE_QUADRATIC_PROGRAM.VALUE});
		

		addChildControl(plannerCombo);
		new Label(this, SWT.NONE);
	}

	@Override
	public boolean bindUI(Realm realm) {
		if (getModel() == null){
			setBound(false);
			return false;
		}

		updateNonBoundFields();

		boolean result = true;
		result &= bindSpecial("planner", plannerCombo, new IntToPlannerConverter(), new PlannerToIntConverter());
		return result;
	}
}
