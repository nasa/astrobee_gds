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

import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.plantrace.CreatePlanTrace;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontal;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontalInt;

import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

// This class is a helper class to make the CoordinateWidget in the tab in StationAndPointWidget
// It's not at all independent, but it just splits code into different files to make it
// easier to read.
public class CoordinateWidget {
	private Color colorOne = ColorProvider.get(213, 94, 163);
	private Color colorTwo = ColorProvider.get(59,181,74);
	private Color colorThree = ColorProvider.get(59,82,164);

	private String[] axisLabels = {"X", "Y", "Z", "Roll", "Pitch", "Yaw"};
	private Color[] colors = {colorOne, colorTwo, colorThree, colorOne, colorTwo, colorThree};

	private double defaultNumber = 0;
	private final String lengthUnits = "m";
	private final String angleUnits = "deg";

	private final String TRANSLATION_CHECKBOX_LABEL = "Drag to Translate";
	private final String ROTATION_CHECKBOX_LABEL = "Drag to Rotate";

	private Button dragToTranslateCheckbox, dragToRotateCheckbox;

	private StationAndPointWidget stationAndPointWidget;
	protected DecimalFormat df = new DecimalFormat();

	protected Composite createCoordinateArea(StationAndPointWidget parent, Composite container) {
		df.setMinimumFractionDigits(1);
		df.setMaximumFractionDigits(1);

		Composite top = setUpComposite(container, 1);
		stationAndPointWidget = parent;

		Composite xyzRpyComposite = setUpComposite(top, 7);
		createXyzRpyTextFields(xyzRpyComposite);
		
		Composite orientationComposite = setUpComposite(top, 3);
		createOrientationCheckbox(orientationComposite);

		Composite toleranceComposite = setUpComposite(top, 3);
		createToleranceTextField(toleranceComposite);

		Composite dragComposite = setUpComposite(top, 2);
		createDragToTranslateCheckbox(dragComposite);
		createDragToRotateCheckbox(dragComposite);

		return top;
	}
	
	public void setModel(Object o) {
		if(o instanceof ModuleBayStation) {
			if(((ModuleBayStation) o).getCoordinate().isIgnoreOrientation()) {
				disableRotationWidgets();
			} else {
				enableRotationWidgets();
			}
		}
	}

	protected void createOrientationCheckbox(Composite container) {
		//new Label(container, SWT.NONE).setText("Orientation");
		stationAndPointWidget.orientationCheckbox = new Button(container, SWT.CHECK);
		stationAndPointWidget.orientationCheckbox.setText("Ignore Orientation");
	
		stationAndPointWidget.orientationCheckbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(stationAndPointWidget.orientationCheckbox.getSelection()) {
					disableRotationWidgets();
				} else {
					enableRotationWidgets();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { /**/ }		
		});
		stationAndPointWidget.orientationCheckbox.setSelection(true);
	}
	
	protected void disableRotationWidgets() {
		stationAndPointWidget.disableCoordinateRollPitchYaw();
		dragToRotateCheckbox.setSelection(false);
		dragToRotateCheckbox.setEnabled(false);
	}
	
	protected void enableRotationWidgets() {
		stationAndPointWidget.enableCoordinateRollPitchYaw();
		dragToRotateCheckbox.setEnabled(true);
		if(CreatePlanTrace.getStaticInstance().getShowRotateDraggable()) {
			dragToRotateCheckbox.setSelection(true);
		}
	}

	protected void createToleranceTextField(Composite top) {
		makeALabel(top, "Tolerance", 1);
		stationAndPointWidget.toleranceText = makeATextField(top, SWT.BEGINNING);
		makeALabel(top, "m", 1);
	}

	protected void createXyzRpyTextFields(Composite top) {
		makeColoredLabel(top, 0);
		stationAndPointWidget.xText = makeIncrementableTextField(top, 0.1);
		makeColoredLabel(top, 1);
		stationAndPointWidget.yText = makeIncrementableTextField(top, 0.1);
		makeColoredLabel(top, 2);
		stationAndPointWidget.zText = makeIncrementableTextField(top, 0.1);
		makeALabel(top, lengthUnits, 1);

		makeColoredLabel(top, 3);
		stationAndPointWidget.rollText = makeIncrementableTextIntField(top, 15);
		makeColoredLabel(top, 4);
		stationAndPointWidget.pitchText = makeIncrementableTextIntField(top, 15);
		makeColoredLabel(top, 5);
		stationAndPointWidget.yawText = makeIncrementableTextIntField(top, 15);
		makeALabel(top, angleUnits, 1);
	}

	protected Composite setUpComposite(Composite container, int cellsAcross) {
		Composite top = new Composite(container, SWT.None);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.horizontalSpan = 5;
		top.setLayoutData(gd);
		top.setLayout(new GridLayout(cellsAcross, false));
		return top;
	}

	protected void makeColoredLabel(Composite container, int index) {
		Label label = new Label(container, SWT.NONE);
		label.setText(axisLabels[index]);
		label.setForeground(colors[index]);
		GridData data = new GridData(SWT.END, SWT.BEGINNING, false, false);
		label.setLayoutData(data);
		stationAndPointWidget.addChildLabel(label);
	}

	protected IncrementableText makeIncrementableTextField(Composite container, double incrementSize) {
		IncrementableText text = new IncrementableTextHorizontal(container, defaultNumber, incrementSize, 2, 0.01);
		stationAndPointWidget.addIncrementable(text);
		return text;
	}

	protected IncrementableTextHorizontalInt makeIncrementableTextIntField(Composite container, double incrementSize) {
		IncrementableTextHorizontalInt text = new IncrementableTextHorizontalInt(container, defaultNumber, incrementSize);
		stationAndPointWidget.addIncrementable(text);
		return text;
	}

	protected Text makeATextField(Composite container, int horizontalAlignment) {
		Text text = new Text(container, SWT.BORDER);
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
		text.setLayoutData(data);
		text.setText(df.format(defaultNumber));
		stationAndPointWidget.addChildControl(text);
		return text;
	}

	protected void makeALabel(Composite container, String text, int width) {
		Label label = new Label(container, SWT.None);
		label.setText(text);
		GridData data = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		data.horizontalSpan = width;
		label.setLayoutData(data);
		stationAndPointWidget.addChildLabel(label);
	}

	private void createDragToTranslateCheckbox(Composite parent) {
		dragToTranslateCheckbox = new Button(parent, SWT.CHECK);
		dragToTranslateCheckbox.setText(TRANSLATION_CHECKBOX_LABEL);
		dragToTranslateCheckbox.setSelection(false);
		dragToTranslateCheckbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();
				if(btn.getSelection()) {
					dragToRotateCheckbox.setSelection(false);
					CreatePlanTrace.getStaticInstance().showTranslateDraggable();
				} else {
					CreatePlanTrace.getStaticInstance().hideDraggable();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
		if(CreatePlanTrace.getStaticInstance().getShowTranslateDraggable()) {
			dragToTranslateCheckbox.setSelection(true);
		}
	}

	private void createDragToRotateCheckbox(Composite parent) {
		dragToRotateCheckbox = new Button(parent, SWT.CHECK);
		dragToRotateCheckbox.setText(ROTATION_CHECKBOX_LABEL);
		dragToRotateCheckbox.setSelection(false);
		dragToRotateCheckbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();
				if(btn.getSelection()) {
					dragToTranslateCheckbox.setSelection(false);
					CreatePlanTrace.getStaticInstance().showRotateDraggable();
				} else {
					CreatePlanTrace.getStaticInstance().hideDraggable();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}
}
