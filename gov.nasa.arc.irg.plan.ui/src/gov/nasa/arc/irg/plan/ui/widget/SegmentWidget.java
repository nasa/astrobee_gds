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

import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SegmentWidget extends AbstractDatabindingWidget {
	protected Label titleLabel;
	protected Text speedText, avelText, accelText, aAccelText;
	protected Button faceForwardCheckbox;
	protected float speedMin = 0.001f;
	protected float speedMax = 1;
	protected boolean showEngineeringConfig = false;
	protected Composite top;
	protected Button useCustomSpeedCheckbox;

	public SegmentWidget(Composite parent, int style) {
		super(parent, style);
		setShowEngineeringConfig();
		createControls();
	}

	@Override
	public void setModel(Object obj) {
		if (obj == null ){
			return;
		}

		unbindUI();
		m_model = obj;

		bindUI(getRealm());
		
		if(((Segment)m_model).isUseCustomSpeed()) {
			speedText.setEnabled(true);
		} else {
			speedText.setEnabled(false);
		}
	}

	@Override
	public boolean bindUI(Realm realm) {

		if (getModel() == null){
			setBound(false);
			return false;
		}

		boolean result = true;
		result &= bindBoolean("useCustomSpeed", useCustomSpeedCheckbox);
		result &= bind("speed", speedText);
		if(showEngineeringConfig) {
			result &= bind("maxAVel", avelText);
			result &= bind("maxAccel", accelText);
			result &= bind("maxAAccel", aAccelText);
		}
		result &= bindBoolean("faceForward", faceForwardCheckbox);
		updateNonBoundFields();

		setBound(result);
		layout(true,true);
		return result;
	}

	protected boolean bindBoolean(String feature, final Button buttonWidget) {
		if(buttonWidget == null) {
			return false;
		}

		ISWTObservableValue moduleTarget = WidgetProperties.selection().observe(buttonWidget);
		IObservableValue moduleModel = BeanProperties.value(Segment.class, feature).observe(getModel());
		m_dataBindingContext.bindValue(moduleTarget, moduleModel);

		return true;
	}

	@Override
	protected UpdateValueStrategy getTargetToModelStrategy(String feature){
		if(feature.equals("stopOnArrival")) {
			return null;
		}

		// define a validator to check that only numbers are entered
		IValidator validator = new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value instanceof String) {
					if (((String)value).matches("\\d*\\.?\\d*")) {
						float val = Float.valueOf((String)value);
						if( val > speedMin && val < speedMax ) {
							return ValidationStatus.ok();
						} else {
							return ValidationStatus.error("Enter number between "+speedMin+" and "+speedMax+".");
						}
					}
					return ValidationStatus.error(value.toString() +" is not a valid number");
				}
				return ValidationStatus.error(value.toString() +" is invalid");
			}
		};

		// create UpdateValueStrategy and assign
		// to the binding
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setAfterGetValidator(validator);

		return strategy;
	}
	private void updateNonBoundFields() {
		if(getModel() instanceof Segment) {		
			Segment me = (Segment) getModel();
			titleLabel.setText(me.getName() + " Segment");
		}
	}

	/**
	 * Actually create the UI components
	 * @param container
	 */
	public void createControls() {
		if (m_dataBindingContext == null){
			m_dataBindingContext = new DataBindingContext();
		}

		setupTopComposite(this);
		GridLayout gl = new GridLayout(3, true);
		setLayout(gl);

		createTitles();
		useCustomSpeedCheckbox = new Button(top, SWT.CHECK);
		useCustomSpeedCheckbox.setText("Set Custom Speed");
		useCustomSpeedCheckbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(useCustomSpeedCheckbox.getSelection()) {
					speedText.setEnabled(true);
				} else {
					speedText.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }		
		});
		addChildControl(useCustomSpeedCheckbox);
		new Label(top, SWT.NONE);
		new Label(top, SWT.NONE);

		speedText = createLine("Speed", "m/s");

		if(showEngineeringConfig) {
			createEngineeringConfigsLines();
		}

		faceForwardCheckbox = new Button(top, SWT.CHECK);
		faceForwardCheckbox.setText("Face Forward");
		addChildControl(faceForwardCheckbox);
	}

	protected Composite setupTopComposite(Composite container) {
		top = new Composite(container, SWT.None);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.horizontalSpan = 5;
		top.setLayoutData(gd);
		top.setLayout(new GridLayout(3, false));
		return top;
	}

	private void createEngineeringConfigsLines() {
		avelText = createLine("Angular Velocity", "rad/s");
		accelText = createLine("Acceleration", "m/s/s");
		aAccelText = createLine("Angular Acceleration", "rad/s/s");
	}

	private Text createLine(String name, String units) {
		Label label = new Label(top, SWT.TRAIL);
		label.setText(name);
		label.setLayoutData(makeRightData());
		addChildLabel(label);
		Text inputText = new Text(top, SWT.BORDER);

		inputText.setLayoutData(makeLeftData());
		inputText.setText("DON'T HIDE ME!!");
		addChildControl(inputText);

		label = new Label(top, SWT.NONE);
		label.setLayoutData(makeLeftData());
		label.setText(units);
		addChildUnitLabel(label);

		return inputText;
	}

	private GridData makeLeftData() {
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.widthHint = getFieldWidth();
		return gd;
	}

	private GridData makeRightData() {
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, true, false);
		gd.widthHint = getLabelWidth();
		return gd;
	}

	private void createTitles() {
		GridData titleData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		titleData.horizontalSpan = 3;

		titleLabel = new Label(top, SWT.NONE);
		titleLabel.setLayoutData(titleData);
		LocalResourceManager resManager = 
				new LocalResourceManager(JFaceResources.getResources(), this);

		FontDescriptor bigDescriptor = FontDescriptor.createFrom(titleLabel.getFont()).setHeight(14);
		Font bigFont = resManager.createFont(bigDescriptor);
		titleLabel.setFont( bigFont );
		addChildLabel(titleLabel);
		updateNonBoundFields();
	}

	private void setShowEngineeringConfig() {
		showEngineeringConfig = WorkbenchConstants.isFlagPresent(WorkbenchConstants.SHOW_ENGINEERING_CONFIGURATION_STRING);
	}

}
