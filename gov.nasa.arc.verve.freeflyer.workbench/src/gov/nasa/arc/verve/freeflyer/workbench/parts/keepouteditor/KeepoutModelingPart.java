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
package gov.nasa.arc.verve.freeflyer.workbench.parts.keepouteditor;

import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.util.bean.IHasPropertyChangeListeners;
import gov.nasa.arc.verve.ardor3d.e4.input.control.FollowCamControl;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontal;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ardor3d.math.type.ReadOnlyVector3;

public class KeepoutModelingPart extends AbstractDatabindingWidget implements PropertyChangeListener {
	
	private Label keepoutName;
	private IncrementableText p1xInput, p1yInput, p1zInput;
	private IncrementableText p2xInput, p2yInput, p2zInput;
	private IncrementableText xInput, yInput, zInput;
	private EnlargeableButton addKeepoutButton;
	private EnlargeableButton deleteButton;
	private EnlargeableButton confirmButton;
	private EnlargeableButton cancelButton;
	private String aftString = "Aft", fwdString = "Fwd";
	private String portString = "Port", stbdString = "Stbd";
	private String ovhdString = "Ovhd", deckString = "Deck";
	
	private KeepoutBuilder keepoutBuilder;
	
	protected DataBindingContext m_dataBindingContext;
	protected double translationRadius = 10;
	private boolean keepoutOpen;
	
	private IEclipseContext context;
	private static KeepoutModelingPart instance;

	private String noKeepoutOpen = "Got to Modeling->Open Keepout File to edit keepouts.\t";

	// to set up the incrementable texts
	private double startValue = 0;
	private double incrementSize = 0.1;
	private int centerPrecision = 2;
	private int cornerPrecision = 1;
	private double centerRoundTo = 0.05;
	private double cornerRoundTo = 0.1;
	
	public KeepoutModelingPart(Composite parent, int style, MApplication app) {
		super(parent, style);
		instance = this;
		context = app.getContext();
		
		m_dataBindingContext = new DataBindingContext();
		
		createControls(this);
		bindUI(getRealm());		
		resetTranslations();
	}
	
	public static KeepoutModelingPart getStaticInstance() {
		if(instance == null) {
			System.err.println("KeepoutModelingPart not created.");
		}
		
		return instance;
	}
	
	public void setKeepoutBuilder(KeepoutBuilder builder) {
		if(builder == null) {
			keepoutName.setText(noKeepoutOpen);
			addKeepoutButton.setEnabled(false);
			deleteButton.setEnabled(false);
			confirmButton.setEnabled(false);
			cancelButton.setEnabled(false);
			p1xInput.setEnabled(false);
			p1yInput.setEnabled(false);
			p1zInput.setEnabled(false);
			p2xInput.setEnabled(false);
			p2yInput.setEnabled(false);
			p2zInput.setEnabled(false);
			xInput.setEnabled(false);
			yInput.setEnabled(false);
			zInput.setEnabled(false);	
			keepoutOpen = false;
		} else {
			keepoutName.setText("Current keepout file: " + builder.getName() + "\t");
			addKeepoutButton.setEnabled(true);
			keepoutOpen = true;
		}
		
		keepoutBuilder = builder;
	}
	
	public void setKeepoutSelected(boolean keepoutSelected) {
		addKeepoutButton.setEnabled(!keepoutSelected && keepoutOpen);
		deleteButton.setEnabled(keepoutSelected);
		confirmButton.setEnabled(keepoutSelected);
		cancelButton.setEnabled(keepoutSelected);

		p1xInput.setEnabled(keepoutSelected);
		p1yInput.setEnabled(keepoutSelected);
		p1zInput.setEnabled(keepoutSelected);
		p2xInput.setEnabled(keepoutSelected);
		p2yInput.setEnabled(keepoutSelected);
		p2zInput.setEnabled(keepoutSelected);
		xInput.setEnabled(keepoutSelected);
		yInput.setEnabled(keepoutSelected);
		zInput.setEnabled(keepoutSelected);			
	}
	
	private void updateTexts(double[] bounds) {
		p1xInput.setTextString(bounds[0]);
		p1yInput.setTextString(bounds[1]);
		p1zInput.setTextString(bounds[2]);
		p2xInput.setTextString(bounds[3]);
		p2yInput.setTextString(bounds[4]);
		p2zInput.setTextString(bounds[5]);
		xInput.setTextString((bounds[0] + bounds[3]) / 2);
		yInput.setTextString((bounds[1] + bounds[4]) / 2);
		zInput.setTextString((bounds[2] + bounds[5]) / 2);
	}
	
	private void createControls(Composite parent) {
		GridLayout glparent = new GridLayout(1, false);
		parent.setLayout(glparent);
		
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("Keepout Editor");
		GridLayout gl = new GridLayout(3, false);
		group.setLayout(gl);
		createTranslationCommandSection(group);
	}
	
	private void createTranslationCommandSection(Composite parent) {		
		parent.setLayout(new GridLayout(3, false));
		Composite controls = new Composite(parent, SWT.NONE);
		controls.setLayout(new GridLayout(3, false));
		
		keepoutName = new Label(controls, SWT.LEFT);
		GridData nameData = new GridData(SWT.LEFT, SWT.LEFT, true, true);
		nameData.horizontalSpan = 3;
		keepoutName.setLayoutData(nameData);
		keepoutName.setText(noKeepoutOpen);

		createAddKeepoutButton(controls);
		Label tipLabel = new Label(controls, SWT.CENTER);
		tipLabel.setText("Click on a keepout to edit it.");
		GridData tipData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		tipData.horizontalSpan = 2;
		tipLabel.setLayoutData(tipData);
		
		createCenterTexts(controls);
		createUpperCornerTexts(controls);
		createLowerCornerTexts(controls);

		createConfirmButton(controls);
		createCancelButton(controls);
		createDeleteButton(controls);
	}
	
	private void createAddKeepoutButton(Composite parent) { 
		addKeepoutButton = new EnlargeableButton(parent, SWT.NONE);
		addKeepoutButton.setText("Add Keepout");
		GridData data = new GridData(SWT.CENTER, SWT.TOP, true, false);
		data.horizontalSpan = 1;
		addKeepoutButton.setLayoutData(data);
		addKeepoutButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		addKeepoutButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FollowCamControl control = context.get(FollowCamControl.class);
				ReadOnlyVector3 center = control.getCenter();
				if(center == null) {
					keepoutBuilder.addKeepout(new double[] {0, 0, 0, 0.5, 0.5, 0.5});
				} else {
					double[] box = {center.getX(), center.getY(), center.getZ(),
									center.getX() + .5, center.getY() + .5, center.getZ() + .5};
					keepoutBuilder.addKeepout(box);
				}
				
				KeepoutFileManager kfm = context.get(KeepoutFileManager.class);
				if(kfm != null) {
					kfm.setKeepoutDirty();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		addKeepoutButton.setEnabled(false);
	}
	
	private void createDeleteButton(Composite parent) { 
		deleteButton = new EnlargeableButton(parent, SWT.NONE);
		deleteButton.setText("Delete");
		deleteButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		deleteButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		deleteButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				KeepoutBox selected = (KeepoutBox) context.get(ContextNames.SELECTED_KEEPOUT);
				if(selected != null) {
					keepoutBuilder.deleteKeepout(selected);
					KeepoutModelingNode.getStaticInstance().onDeselect();
					KeepoutFileManager kfm = context.get(KeepoutFileManager.class);
					if(kfm != null) {
						kfm.setKeepoutDirty();
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }
		});
		deleteButton.setEnabled(false);
	}
	
	private void createConfirmButton(Composite parent) {
		confirmButton = new EnlargeableButton(parent, SWT.NONE);
		confirmButton.setText("Confirm");
		confirmButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		confirmButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		confirmButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				KeepoutModelingNode.getStaticInstance().onDeselect();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
		confirmButton.setEnabled(false);
	}
	
	private void createCancelButton(Composite parent) {
		cancelButton = new EnlargeableButton(parent, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		cancelButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		cancelButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				keepoutBuilder.cancelTransform();
				resetTranslations();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
		cancelButton.setEnabled(false);
	}
	
	private void resetTranslations() {
		xInput.setTextString(0);
		yInput.setTextString(0);
		zInput.setTextString(0);
		p1xInput.setTextString(0);
		p1yInput.setTextString(0);
		p1zInput.setTextString(0);
		p2xInput.setTextString(0);
		p2yInput.setTextString(0);
		p2zInput.setTextString(0);
	}
	
	private void createCenterTexts(Composite parent) {
		Composite xyzComposite = new Composite(parent, SWT.None);
		xyzComposite.setLayout(new GridLayout(3,false));
		
		Label title = new Label(xyzComposite, SWT.NONE);
		title.setText("Center");
		new Label(xyzComposite, SWT.NONE);
		new Label(xyzComposite, SWT.NONE);

		Label xLabel = new Label(xyzComposite, SWT.None);
		xLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		xLabel.setText(aftString);
		xInput = new IncrementableTextHorizontal(xyzComposite, startValue, incrementSize, centerPrecision, centerRoundTo);
		xInput.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelX = new Label(xyzComposite, SWT.NONE);
		mLabelX.setText(fwdString);

		Label yLabel = new Label(xyzComposite, SWT.None);
		yLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		yLabel.setText(portString);
		yInput = new IncrementableTextHorizontal(xyzComposite, startValue, incrementSize, centerPrecision, centerRoundTo);
		yInput.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelY = new Label(xyzComposite, SWT.NONE);
		mLabelY.setText(stbdString);

		Label zLabel = new Label(xyzComposite, SWT.None);
		zLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		zLabel.setText(ovhdString);
		zInput = new IncrementableTextHorizontal(xyzComposite, startValue, incrementSize, centerPrecision, centerRoundTo);
		zInput.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelZ = new Label(xyzComposite, SWT.NONE);
		mLabelZ.setText(deckString);

		new Label(xyzComposite, SWT.None);
		Label mLabel = new Label(xyzComposite, SWT.None);
		mLabel.setText("m");
		mLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		new Label(xyzComposite, SWT.None);
	}
	
	private void createUpperCornerTexts(Composite parent) {
		Composite xyzComposite = new Composite(parent, SWT.None);
		xyzComposite.setLayout(new GridLayout(3,false));
		
		Label title = new Label(xyzComposite, SWT.NONE);
		title.setText("Upper Corner");
		title.setForeground(new Color(Display.getCurrent(), 0, 0, 255));
		new Label(xyzComposite, SWT.NONE);
		new Label(xyzComposite, SWT.NONE);

		Label xLabel = new Label(xyzComposite, SWT.None);
		xLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		xLabel.setText(aftString);
		p1xInput = new IncrementableTextHorizontal(xyzComposite, startValue, incrementSize, cornerPrecision, cornerRoundTo);
		p1xInput.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelX = new Label(xyzComposite, SWT.NONE);
		mLabelX.setText(fwdString);

		Label yLabel = new Label(xyzComposite, SWT.None);
		yLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		yLabel.setText(portString);
		p1yInput = new IncrementableTextHorizontal(xyzComposite, startValue, incrementSize, cornerPrecision, cornerRoundTo);
		p1yInput.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelY = new Label(xyzComposite, SWT.NONE);
		mLabelY.setText(stbdString);

		Label zLabel = new Label(xyzComposite, SWT.None);
		zLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		zLabel.setText(ovhdString);
		p1zInput = new IncrementableTextHorizontal(xyzComposite, startValue, incrementSize, cornerPrecision, cornerRoundTo);
		p1zInput.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelZ = new Label(xyzComposite, SWT.NONE);
		mLabelZ.setText(deckString);

		new Label(xyzComposite, SWT.None);
		Label mLabel = new Label(xyzComposite, SWT.None);
		mLabel.setText("m");
		mLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		new Label(xyzComposite, SWT.None);
	}
	
	private void createLowerCornerTexts(Composite parent) {
		Composite xyzComposite = new Composite(parent, SWT.None);
		xyzComposite.setLayout(new GridLayout(3,false));
		
		Label title = new Label(xyzComposite, SWT.NONE);
		title.setText("Lower Corner");
		title.setForeground(new Color(Display.getCurrent(), 31, 117, 0));
		new Label(xyzComposite, SWT.NONE);
		new Label(xyzComposite, SWT.NONE);

		Label xLabel = new Label(xyzComposite, SWT.None);
		xLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		xLabel.setText(aftString);
		p2xInput = new IncrementableTextHorizontal(xyzComposite, startValue, incrementSize, cornerPrecision, cornerRoundTo);
		p2xInput.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelX = new Label(xyzComposite, SWT.NONE);
		mLabelX.setText(fwdString);

		Label yLabel = new Label(xyzComposite, SWT.None);
		yLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		yLabel.setText(portString);
		p2yInput = new IncrementableTextHorizontal(xyzComposite, startValue, incrementSize, cornerPrecision, cornerRoundTo);
		p2yInput.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelY = new Label(xyzComposite, SWT.NONE);
		mLabelY.setText(stbdString);

		Label zLabel = new Label(xyzComposite, SWT.None);
		zLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		zLabel.setText(ovhdString);
		p2zInput = new IncrementableTextHorizontal(xyzComposite, startValue, incrementSize, cornerPrecision, cornerRoundTo);
		p2zInput.setAllowableRange(-translationRadius, translationRadius);
		Label mLabelZ = new Label(xyzComposite, SWT.NONE);
		mLabelZ.setText(deckString);

		new Label(xyzComposite, SWT.None);
		Label mLabel = new Label(xyzComposite, SWT.None);
		mLabel.setText("m");
		mLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		new Label(xyzComposite, SWT.None);
	}

	@Override
	public boolean bindUI(Realm realm) {
		boolean result = true;
		
		result &= bind("p1x", p1xInput.getTextControl());
		result &= bind("p1y", p1yInput.getTextControl());
		result &= bind("p1z", p1zInput.getTextControl());
		result &= bind("p2x", p2xInput.getTextControl());
		result &= bind("p2y", p2yInput.getTextControl());
		result &= bind("p2z", p2zInput.getTextControl());
		result &= bind("x", xInput.getTextControl());
		result &= bind("y", yInput.getTextControl());
		result &= bind("z", zInput.getTextControl());
		

		return result;
	}
	
	@Override
	public void setModel(final Object obj) {
		super.setModel(obj);
		if(obj instanceof IHasPropertyChangeListeners) {
			((IHasPropertyChangeListeners) obj).addPropertyChangeListener(this);
		}
	}
	
	@Override
	public void unbindUI() {
		super.unbindUI();
		if(m_model instanceof IHasPropertyChangeListeners) {
			((IHasPropertyChangeListeners) m_model).removePropertyChangeListener(this);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("bounds")) {
			if(evt.getNewValue() != null) {
				updateTexts((double[]) evt.getNewValue());
			}
		}
	}
}
