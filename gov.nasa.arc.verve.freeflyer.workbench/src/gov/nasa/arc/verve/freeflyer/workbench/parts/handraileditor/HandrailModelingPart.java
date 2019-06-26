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
package gov.nasa.arc.verve.freeflyer.workbench.parts.handraileditor;

import gov.nasa.arc.irg.freeflyer.utils.converters.IntToWallConverter;
import gov.nasa.arc.irg.freeflyer.utils.converters.WallToIntConverter;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.verve.ardor3d.e4.input.control.FollowCamControl;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontal;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ardor3d.math.type.ReadOnlyVector3;

public class HandrailModelingPart extends AbstractDatabindingWidget {
	
	protected Button verticalCheckbox;
	protected Combo orientationCombo;
	protected IncrementableText xText, yText, zText;
	protected EnlargeableButton saveButton, addButton, deleteButton, confirmButton, cancelButton;
	protected double translationRadius = 10;
	
	private static HandrailModelingPart instance;
	private boolean isDirty;
	private IEclipseContext context;
	
	private HandrailBuilder handrailBuilder;
	
	public HandrailModelingPart(Composite parent, int style, MApplication app) {
		super(parent, style);
		instance = this;
		context = app.getContext();
		
//		Composite handrailComposite = new Composite(parent, SWT.NONE);
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Handrail Editor");
		
		GridLayout gl = new GridLayout(3, false);
		group.setLayout(gl);
		
		initAddSaveRow(group);		
		initIncrementables(group);
		initComboAndCheckbox(group);
		initOptionsRow(group);
		
		group.pack();
		
		bindUI(getRealm());
		deactivate();
	}
	
	public static HandrailModelingPart getStaticInstance() {
		if(instance == null) {
			System.err.println("HandrailModelingPart not created.");
		}
		
		return instance;
	}
	
	public void setHandrailBuilder(HandrailBuilder builder) {
		handrailBuilder = builder;
	}
	
	public void activate() {
		xText.setEnabled(true);
		yText.setEnabled(true);
		zText.setEnabled(true);
		orientationCombo.setEnabled(true);
		verticalCheckbox.setEnabled(true);
		addButton.setEnabled(false);
		deleteButton.setEnabled(true);
		confirmButton.setEnabled(true);
		cancelButton.setEnabled(true);
		saveButton.setEnabled(isDirty);
	}
	
	public void deactivate() {
		xText.setEnabled(false);
		yText.setEnabled(false);
		zText.setEnabled(false);
		orientationCombo.setEnabled(false);
		verticalCheckbox.setEnabled(false);
		addButton.setEnabled(true);
		deleteButton.setEnabled(false);
		confirmButton.setEnabled(false);
		cancelButton.setEnabled(false);
		saveButton.setEnabled(isDirty);
	}
	
	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		saveButton.setEnabled(isDirty);
	}
	
	private void initLabel(Composite parent) {
		Label handrailLabel = new Label(parent, SWT.NONE);
		handrailLabel.setText("Click on a handrail to edit it.");
	}
	
	private void initAddSaveRow(Composite parent) {
		Composite firstRow = new Composite(parent, SWT.NONE);
		GridLayout firstRowGl = new GridLayout(3, false);
		firstRow.setLayout(firstRowGl);
		GridData firstRowData = new GridData(SWT.LEFT, SWT.LEFT, true, false);
		firstRowData.horizontalSpan = 3;
		firstRow.setLayoutData(firstRowData);
		
		initLabel(firstRow);
		initAddButton(firstRow);
		initSaveButton(firstRow);
	}
	
	private void initComboAndCheckbox(Composite parent) {
		Composite comboAndCheckbox = new Composite(parent, SWT.NONE);
		GridLayout cacLayout = new GridLayout(5, false);
		comboAndCheckbox.setLayout(cacLayout);
		GridData cacData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		cacData.horizontalSpan = 3;
		comboAndCheckbox.setLayoutData(cacData);
		
		initOrientationCombo(comboAndCheckbox);
		Label orientationLabel = new Label(comboAndCheckbox, SWT.NONE);
		orientationLabel.setText("Alignment Wall");
		Label filler = new Label(comboAndCheckbox, SWT.NONE);
		filler.setText("\t");
		filler.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		initVerticalCheckbox(comboAndCheckbox);
		new Label(comboAndCheckbox, SWT.NONE).setText("Horizontal");
	}
	
	private void initOptionsRow(Composite parent) {
		initConfirmButton(parent);
		initCancelButton(parent);
		initDeleteButton(parent);
	}
	
	private void initSaveButton(Composite parent) {
		saveButton = new EnlargeableButton(parent, SWT.NONE);
		saveButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		saveButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		saveButton.setText("Save Handrails");
		
		saveButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HandrailFileManager hfm = context.get(HandrailFileManager.class);
				if(hfm != null) {
					hfm.onSaveHandrailCommand();
				}
				HandrailModelingNode.getStaticInstance().onDeselect();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}
	
	private void initAddButton(Composite parent) {
		addButton = new EnlargeableButton(parent, SWT.NONE);
		addButton.setText("Add Handrail");
		addButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		addButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		addButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FollowCamControl control = context.get(FollowCamControl.class);
				ReadOnlyVector3 center = control.getCenter();
				if(center == null) {
					handrailBuilder.addGenericHandrail(new double[] {0, 0, 0});
				} else {
					double[] pos = {center.getX(), center.getY(), center.getZ()};
					handrailBuilder.addGenericHandrail(pos);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}
	
	private void initDeleteButton(Composite parent) {
		deleteButton = new EnlargeableButton(parent, SWT.NONE);
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		deleteButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		deleteButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HandrailModel handrail = (HandrailModel) context.get(ContextNames.SELECTED_HANDRAIL);
				if(handrail != null) {
					handrailBuilder.deleteHandrail(handrail);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}
	
	private void initConfirmButton(Composite parent) {
		confirmButton = new EnlargeableButton(parent, SWT.NONE);
		confirmButton.setText("Confirm");
		confirmButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		confirmButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		confirmButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HandrailModel handrail = (HandrailModel) context.get(ContextNames.SELECTED_HANDRAIL);
				if(handrail != null) {
					HandrailModelingNode.getStaticInstance().onDeselect();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}
	
	private void initCancelButton(Composite parent) {
		cancelButton = new EnlargeableButton(parent, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		cancelButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		cancelButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HandrailModel handrail = (HandrailModel) context.get(ContextNames.SELECTED_HANDRAIL);
				if(handrail != null) {
					handrailBuilder.cancelTransform();
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
	}
	
	private void initOrientationCombo(Composite parent) {
		orientationCombo = new Combo(parent, SWT.READ_ONLY);
		orientationCombo.setItems(LocationMap.getInstance().allWallsAsStringArray());
		orientationCombo.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}
	
	private void initVerticalCheckbox(Composite parent) {
		verticalCheckbox = new Button(parent, SWT.CHECK);
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		verticalCheckbox.setLayoutData(gd);
	}
	
	private void initIncrementables(Composite parent) {
		Composite xyzComposite = new Composite(parent, SWT.None);
		xyzComposite.setLayout(new GridLayout(3,false));
		GridData gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gd.horizontalSpan = 3;
		xyzComposite.setLayoutData(gd);
		
		Label title = new Label(xyzComposite, SWT.NONE);
		title.setText("Handrail Position");
		new Label(xyzComposite, SWT.NONE);
		new Label(xyzComposite, SWT.NONE);

		Label xLabel = new Label(xyzComposite, SWT.None);
		xLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		xLabel.setText("Aft");
		xText = new IncrementableTextHorizontal(xyzComposite, 0.00, 0.1, 2, 1);
		xText.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelX = new Label(xyzComposite, SWT.NONE);
		mLabelX.setText("Fwd");

		Label yLabel = new Label(xyzComposite, SWT.None);
		yLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		yLabel.setText("Port");
		yText = new IncrementableTextHorizontal(xyzComposite, 0.00, 0.1, 2, 1);
		yText.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelY = new Label(xyzComposite, SWT.NONE);
		mLabelY.setText("Stbd");

		Label zLabel = new Label(xyzComposite, SWT.None);
		zLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		zLabel.setText("Ovhd");
		zText = new IncrementableTextHorizontal(xyzComposite, 0.0, 0.1, 2, 1);
		zText.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelZ = new Label(xyzComposite, SWT.NONE);
		mLabelZ.setText("Deck");

		new Label(xyzComposite, SWT.None);
		Label mLabel = new Label(xyzComposite, SWT.None);
		mLabel.setText("m");
		mLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		new Label(xyzComposite, SWT.None);
	}

	@Override
	public boolean bindUI(Realm realm) {
		boolean result = true;
		
		result &= bind("x", xText.getTextControl());
		result &= bind("y", yText.getTextControl());
		result &= bind("z", zText.getTextControl());
		
		result &= bind("wall", orientationCombo);
		result &= bindBoolean("isHorizontal", verticalCheckbox);
		
		return result;
	}
	
	protected boolean bindBoolean(String feature, final Button buttonWidget) {
		if(buttonWidget == null) {
			return false;
		}
		
		ISWTObservableValue moduleTarget = WidgetProperties.selection().observe(buttonWidget);
		IObservableValue moduleModel = BeanProperties.value(HandrailModel.class, feature).observe(getModel());
		m_dataBindingContext.bindValue(moduleTarget, moduleModel);

		return true;
	}
	
	@Override
	protected UpdateValueStrategy getModelToTargetStrategy(String feature){
		if(feature.equals("wall")) {
			return getUpdateValueStrategyFromConverter(new WallToIntConverter());
		}
		
		return null;
	}
	
	@Override
	protected UpdateValueStrategy getTargetToModelStrategy(String feature){
		if(feature.equals("wall")) {
			return getUpdateValueStrategyFromConverter(new IntToWallConverter());
		}
		
		return null;
	}
	
	protected UpdateValueStrategy getUpdateValueStrategyFromConverter(IConverter converter) {
		UpdateValueStrategy moduleTargetStrategy = new UpdateValueStrategy();
		return moduleTargetStrategy.setConverter(converter);
	}
	
}
