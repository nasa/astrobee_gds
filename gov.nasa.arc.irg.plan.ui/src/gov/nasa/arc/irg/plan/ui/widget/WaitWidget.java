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
package gov.nasa.arc.irg.plan.ui.widget;

import gov.nasa.arc.irg.plan.freeflyer.command.FreeFlyerCommand;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WaitWidget extends AbstractDatabindingWidget {
	protected Label m_titleLabel;
	protected Text m_durationText;
	
	public WaitWidget(Composite parent, int style) {
		super(parent, style);
		createControls(parent);
	}

	@Override
	public void setModel(Object obj) {
		if (obj == null ){
			return;
		}

		unbindUI();
		m_model = obj;

		bindUI(getRealm());
	}

	@Override
	public boolean bindUI(Realm realm) {

		if (getModel() == null){
			setBound(false);
			return false;
		}

		boolean result = true;
		result &= bind("duration", m_durationText);

		updateNonBoundFields();
		
		setBound(result);
		layout(true,true);
		return result;
	}
	
	private void updateNonBoundFields() {
		if(getModel() instanceof FreeFlyerCommand) {		
			FreeFlyerCommand me = (FreeFlyerCommand) getModel();
			m_titleLabel.setText(me.getName());
		}
	}
	
	/**
	 * Actually create the UI components
	 * @param container
	 */
	public void createControls(Composite container) {
		if (m_dataBindingContext == null){
			m_dataBindingContext = new DataBindingContext();
		}

		GridLayout gl = new GridLayout(3, false);
		setLayout(gl);

		GridData titleData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		titleData.horizontalSpan = 2;
	
		m_titleLabel = new Label(this, SWT.NONE);
		m_titleLabel.setLayoutData(titleData);
		LocalResourceManager resManager = 
				  new LocalResourceManager(JFaceResources.getResources(), this);

		FontDescriptor bigDescriptor = FontDescriptor.createFrom(m_titleLabel.getFont()).setHeight(14);
		Font bigFont = resManager.createFont(bigDescriptor);
		m_titleLabel.setFont( bigFont );
		addChildLabel(m_titleLabel);
		updateNonBoundFields();
		
		Label label = new Label(this, SWT.NONE);
		
		label = new Label(this, SWT.TRAIL);
		label.setText("Duration");
		label.setLayoutData(getRightData());
		addChildLabel(label);

		m_durationText = new Text(this, SWT.BORDER);
		m_durationText.setLayoutData(getLeftData());
		addChildControl(m_durationText);
		label = new Label(this, SWT.NONE);
		label.setText("s");
		addChildUnitLabel(label);
	}
}