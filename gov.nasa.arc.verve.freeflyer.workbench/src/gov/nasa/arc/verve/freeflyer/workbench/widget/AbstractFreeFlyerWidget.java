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

import gov.nasa.arc.irg.plan.freeflyer.command.FreeFlyerCommand;
import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractFreeFlyerWidget extends AbstractDatabindingWidget {
	protected Label titleLabel;
	protected Shell shell;
	
	public AbstractFreeFlyerWidget(Composite parent, int style) {
		super(parent, style);
		shell = parent.getShell();
		createControls();
	}

	public void createControls() {
		if (m_dataBindingContext == null){
			m_dataBindingContext = new DataBindingContext();
		}
		GuiUtils.giveGridLayout(this, 3);
		
		setupTitleData();
		
		setupCustomControls();
	}
	
	abstract void setupCustomControls();
	
	protected boolean bindSpecial(String feature, final Combo comboWidget, 
			IConverter targetConverter, IConverter modelConverter) {
		if (comboWidget == null){
			return false;
		}

		ISWTObservableValue moduleTarget = WidgetProperties.singleSelectionIndex().observe(comboWidget);
		
		IObservableValue moduleModel = BeanProperties.value(feature).observe(getModel());
		
		UpdateValueStrategy moduleTargetStrategy = new UpdateValueStrategy();
		moduleTargetStrategy.setConverter(targetConverter);

		UpdateValueStrategy moduleModelStrategy = new UpdateValueStrategy();
		moduleModelStrategy.setConverter(modelConverter);

		m_dataBindingContext.bindValue(moduleTarget, moduleModel, moduleTargetStrategy, moduleModelStrategy);

		return true;
	}
	
	protected void setupTitleData() {
		GridData titleData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		titleData.horizontalSpan = 3;

		titleLabel = new Label(this, SWT.BORDER);
		titleLabel.setLayoutData(titleData);
		titleLabel.setText("Even More Sufficiently Longer Placeholder");

		Font bigFont = GuiUtils.makeBigFont(this, titleLabel, 14);
		titleLabel.setFont( bigFont );
		addChildLabel(titleLabel);
		updateNonBoundFields();
	}
	
	protected void updateNonBoundFields() {
		if(getModel() instanceof FreeFlyerCommand) {		
			// fix the title
			FreeFlyerCommand me = (FreeFlyerCommand) getModel();
			titleLabel.setText(me.getName());
		}
		else if(getModel() instanceof Plan) {
			Plan me = (Plan) getModel();
			titleLabel.setText(me.getName());
		}
	}

	protected void showErrorDialog(Shell shell, final String title, final String errorMsg)  {
		// create a dialog with ok and cancel buttons and a question icon
		if(shell == null) {
			return;
		}

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageBox dialog = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
				dialog.setText(title);
				dialog.setMessage(errorMsg);

				// open dialog and await user selection
				int returnCode = dialog.open(); 

			}
		});
	}
}
