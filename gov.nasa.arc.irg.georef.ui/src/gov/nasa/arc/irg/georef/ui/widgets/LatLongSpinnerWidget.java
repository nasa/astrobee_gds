/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.irg.georef.ui.widgets;

import gov.nasa.arc.irg.georef.coordinates.LatLong;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;
import gov.nasa.util.StrUtil;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * Custom widget for editing / validating LatLong
 * This one has spinners and is registered as such (keyword spinner)
 * 
 * @author tecohen
 * 
 */
public class LatLongSpinnerWidget extends AbstractDatabindingWidget {

	Label m_latitudeLabel;
	Spinner m_latitudeText;

	Label m_longitudeLabel;
	Spinner m_longitudeText;

	/**
	 * @param parent
	 * @param style
	 */
	public LatLongSpinnerWidget(Composite parent, int style) {
		super(parent, style);
		createControls(this, false);
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param style
	 * @param horizontal true for a horizontal layout
	 */
	public LatLongSpinnerWidget(Composite parent, int style, boolean horizontal) {
		super(parent, style);
		createControls(this, horizontal);
	}

	/**
	 * Actually create the UI components
	 * @param parent
	 */
	public void createControls(Composite container, boolean horizontal) {

		final GridLayout gridLayout = new GridLayout();
		if (horizontal) {
			gridLayout.numColumns = 4;
		} else {
			gridLayout.numColumns = 2;
		}
		setLayout(gridLayout);
		
		m_latitudeLabel = new Label(container, SWT.TRAIL);
		String name = LatLong.LATITUDE;
		name = StrUtil.upperFirstChar(name, true);
		m_latitudeLabel.setText(name);
		m_latitudeLabel.setLayoutData(getRightData());
		addChildLabel(m_latitudeLabel);
		
		m_latitudeText = new Spinner(container, SWT.NONE);
		m_latitudeText.setLayoutData(getLeftData());
		addChildControl(m_latitudeText);
		//m_latitudeText.setMaximum(LatitudeType.)

		m_longitudeLabel = new Label(container, SWT.TRAIL);
		m_longitudeLabel.setText(StrUtil.upperFirstChar(LatLong.LONGITUDE, true));
		m_longitudeLabel.setLayoutData(getRightData());
		addChildLabel(m_longitudeLabel);
		
		m_longitudeText = new Spinner(container, SWT.NONE);
		m_longitudeText.setLayoutData(getLeftData());
		addChildControl(m_longitudeText);
	}

	/**
	 * Bind the user interface to the model.
	 */
	@Override
    public boolean bindUI(Realm realm) {
		if (getModel() == null) {
			return false;
		}

		boolean worked = bind(LatLong.LATITUDE, m_latitudeText);
		if (worked){
			worked = bind(LatLong.LONGITUDE, m_longitudeText);
		}
		
		setBound(worked);
		return worked;
	}

	/**
	 * @return
	 */
	public LatLong getLatLong() {
		return (LatLong)getModel();
	}

	/**
	 * @param latLong
	 */
	public void setLatLong(LatLong latLong) {
		setModel(latLong);
	}

}
