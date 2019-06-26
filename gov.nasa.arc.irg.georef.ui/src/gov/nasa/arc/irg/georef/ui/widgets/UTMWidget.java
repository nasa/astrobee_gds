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

import java.text.NumberFormat;
import java.text.ParseException;

import gov.nasa.arc.irg.georef.coordinates.Letter;
import gov.nasa.arc.irg.georef.coordinates.UTM;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;
import gov.nasa.util.StrUtil;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Widget to support editing of a UTM
 * @author tecohen
 *
 */
public class UTMWidget extends AbstractDatabindingWidget {
	
	Label m_eastingLabel;
	Text m_eastingText;
	
	Label m_northingLabel;
	Text m_northingText;
	
	Label m_letterDesignatorLabel;
	ComboViewer m_letterDesignatorComboViewer;
	
	Label m_zoneLabel;
	Text m_zoneText;
	
	protected UpdateValueStrategy m_modelToTargetStrategy = new UpdateValueStrategy(){
        @Override
        protected IStatus doSet(IObservableValue observableValue, Object value) {
            
            try {
                if (getModel() != null && value != null){
                    Number num = NumberFormat.getNumberInstance().parse((String)value);
                    observableValue.setValue(UTM.s_decimalFormat.format(num));
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
	 * Generic constructor
	 * @param parent
	 * @param style
	 */
	public UTMWidget(Composite parent, int style){
		super(parent,style);
		m_numColumns = 2;
		createControls(this, false);
	}
	
	/**
	 * 
	 * @param parent
	 * @param style
	 * @param horizontal If you want a more horizontal layout, pass true.
	 */
	public UTMWidget(Composite parent, int style, boolean horizontal){
		super(parent, style);
		if (horizontal){
			m_numColumns = 4;
		} else {
			m_numColumns = 2;
		}
		createControls(this, horizontal);
	}
	
	/**
	 * Actually create the controls of the widget
	 * @param container
	 * @param horizontal
	 */
	public void createControls(Composite container, boolean horizontal){
		setLayout(new GridLayout(m_numColumns, false));
		
		m_eastingLabel = new Label(this, SWT.TRAIL);
		m_eastingLabel.setText(StrUtil.upperFirstChar(UTM.EASTING + ":", true));
		m_eastingLabel.setLayoutData(getRightData());
		addChildLabel(m_eastingLabel);
		
		m_eastingText = new Text(this, SWT.BORDER);
		m_eastingText.setLayoutData(getLeftData());
		addChildControl(m_eastingText);
		
		m_northingLabel = new Label(this, SWT.TRAIL);
		m_northingLabel.setText(StrUtil.upperFirstChar(UTM.NORTHING + ":", true));
		m_northingLabel.setLayoutData(getRightData());
		addChildLabel(m_northingLabel);
		
		m_northingText = new Text(this, SWT.BORDER);
		m_northingText.setLayoutData(getLeftData());
		addChildControl(m_northingText);
		
		m_zoneLabel = new Label(this, SWT.TRAIL);
		m_zoneLabel.setText(StrUtil.upperFirstChar(UTM.ZONE + ":", true));
		m_zoneLabel.setLayoutData(getRightData());
		addChildLabel(m_zoneLabel);
		
		m_zoneText = new Text(this, SWT.BORDER);
		m_zoneText.setLayoutData(getLeftData());
		addChildControl(m_zoneText);
		
		m_letterDesignatorLabel = new Label(this, SWT.TRAIL);
		m_letterDesignatorLabel.setText("Zone Letter:");
		m_letterDesignatorLabel.setLayoutData(getRightData());
		addChildLabel(m_letterDesignatorLabel);
		
		m_letterDesignatorComboViewer = new ComboViewer(this, SWT.READ_ONLY);
		m_letterDesignatorComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		m_letterDesignatorComboViewer.setInput(Letter.VALUES);
		m_letterDesignatorComboViewer.getControl().setLayoutData(getLeftComboData());
		addChildControl(m_letterDesignatorComboViewer.getControl());
		
	}

	/* (non-Javadoc)
	 * @see gov.nasa.arc.irg.coordinates.ui.widgets.AbstractEMFDatabindingWidget#bindUI(org.eclipse.core.databinding.observable.Realm)
	 */
	@Override
	public boolean bindUI(Realm realm) {
		if (getModel() == null) {
			return false;
		}
		
		boolean worked = bind(UTM.EASTING, m_eastingText);
		if (worked){
			worked = bind(UTM.NORTHING, m_northingText);
		}
		if (worked){
			worked = bind(UTM.ZONE, m_zoneText);
		}
		if (worked){
			worked = bind(UTM.LETTER_DESIGNATOR, m_letterDesignatorComboViewer);
		}
		setBound(worked);
		return worked;
	}

	/**
	 * Set the UTM
	 * @param utm
	 */
	public void setUTM(UTM utm){
		setModel(utm);
	}
	
	/**
	 * Get the UTM
	 * @return
	 */
	public UTM getUTM() {
		return (UTM) getModel();
	}
	
	@Override
    protected UpdateValueStrategy getModelToTargetStrategy(String feature) {
	    if (feature.equals(UTM.NORTHING) || feature.equals(UTM.EASTING)){
	        return m_modelToTargetStrategy;
	    }
	    return null;
    }
	

}
