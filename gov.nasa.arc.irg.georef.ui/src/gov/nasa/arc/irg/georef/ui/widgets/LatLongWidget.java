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

import java.text.NumberFormat;
import java.text.ParseException;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Custom widget for editing / validating LatLong
 * 
 * @author tecohen
 * 
 */
public class LatLongWidget extends AbstractDatabindingWidget {

	Label m_latitudeLabel;
	Text m_latitudeText;

	Label m_longitudeLabel;
	Text m_longitudeText;
	

	protected UpdateValueStrategy m_modelToTargetStrategy = new UpdateValueStrategy(){
		
		@Override
        public Object convert(Object value) {
			if (value != null && value instanceof Number) {
				String result =  LatLong.s_decimalFormat.format(value);
				return result;
			}
			return super.convert(value);
		}
		
		@Override
		protected IStatus doSet(IObservableValue observableValue, Object value) {
		    
            try {
                if (getModel() != null && value != null){
                    Number num = NumberFormat.getNumberInstance().parse((String)value);
                    observableValue.setValue(LatLong.s_decimalFormat.format(num));
                }
                return Status.OK_STATUS;
            } catch (NumberFormatException nfe){
                return new Status(Status.ERROR, "Bad Value", value + " is not a double.", nfe);
            } catch (ParseException e) {
                return new Status(Status.ERROR, "Bad Value", value + " is not a double.", e);
            }
  
		}
	};
	
	/**
	 * @param parent
	 * @param style
	 */
	public LatLongWidget(Composite parent, int style) {
		super(parent, style);
		m_numColumns = 2;
		m_fieldWidth = 150;
		createControls(this, false);
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param style
	 * @param horizontal true for a horizontal layout
	 */
	public LatLongWidget(Composite parent, int style, boolean horizontal) {
		super(parent, style);
		if (horizontal){
			m_numColumns = 4;
		} else {
			m_numColumns = 2;
		}
		m_fieldWidth = 150;
		createControls(this, horizontal);
	}

	/**
	 * Actually create the UI components
	 * @param parent
	 */
	public void createControls(Composite container, boolean horizontal) {
		setLayout(new GridLayout(m_numColumns, false));
		
		m_latitudeLabel = new Label(this, SWT.TRAIL);
		m_latitudeLabel.setText(StrUtil.upperFirstChar(LatLong.LATITUDE+":", true));
		m_latitudeLabel.setLayoutData(getRightData());
		addChildLabel(m_latitudeLabel);
		
		m_latitudeText = new Text(this, SWT.BORDER);
		m_latitudeText.setLayoutData(getLeftData());
		addChildControl(m_latitudeText);

		m_longitudeLabel = new Label(this, SWT.TRAIL);
		m_longitudeLabel.setText(StrUtil.upperFirstChar(LatLong.LONGITUDE +":", true));
		m_longitudeLabel.setLayoutData(getRightData());
		addChildLabel(m_longitudeLabel);
		
		m_longitudeText = new Text(this, SWT.BORDER);
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

	
	@Override
	protected UpdateValueStrategy getModelToTargetStrategy(String feature) {
		return m_modelToTargetStrategy;
	}
	
	/**
	 * @return
	 */
	public LatLong getLatLong() {
		return (LatLong) getModel();
	}

	/**
	 * @param latLong
	 */
	public void setLatLong(LatLong latLong) {
		setModel(latLong);
	}
}
