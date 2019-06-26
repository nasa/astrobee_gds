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
package gov.nasa.arc.irg.georef.ui.dialogs;

import gov.nasa.arc.irg.georef.coordinates.EllipsoidReference;
import gov.nasa.arc.irg.georef.coordinates.LatLong;
import gov.nasa.arc.irg.georef.coordinates.UTM;
import gov.nasa.arc.irg.georef.coordinates.util.UtmLatLongConverter;
import gov.nasa.ensemble.ui.databinding.databinding.BoundWidgetFactory;
import gov.nasa.ensemble.ui.databinding.status.IStatusListener;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A Dialog that supports editing lat/long or utm
 * 
 * TODO: right now this just uses one reference ellipsoid which is not changeable.
 * 
 * @author tecohen
 *
 */
public class EditLatLongUtmDialog extends TitleAreaDialog {
	
	public static final String DEFAULT_TEXT = "Edit Latitude /Longitude or UTM";

	protected Button m_editLatLongButton;
	protected AbstractDatabindingWidget m_latLongWidget;
	protected LatLong m_latLong;
	protected boolean m_latLongValid = false;
	
	protected Button m_editUTMButton;
	protected AbstractDatabindingWidget m_utmWidget;
	protected UTM m_utm;
	protected boolean m_utmValid = false;
	
	protected boolean m_editingLatLong = true;
	
	// semaphores for changing lat long and utm values
	protected volatile boolean m_updatingLatLong = false;
	protected volatile boolean m_updatingUTM = false;
	
	protected SelectionListener m_editingSelectionListener = new SelectionListener() {

        public void widgetDefaultSelected(SelectionEvent e) {
            // 
		}

        public void widgetSelected(SelectionEvent e) {
			m_editingLatLong = m_editLatLongButton.getSelection();
			m_latLongWidget.setEnabled(m_editingLatLong);
			m_utmWidget.setEnabled(!m_editingLatLong);
		}
		
	};
	
	protected IStatusListener m_errorStatusListener = new IStatusListener() {

        public void statusChanged(IStatus status) {
			setErrorMessage(null);
			StringBuffer text = new StringBuffer(DEFAULT_TEXT);
			//Image image = getInfoImage();
			if (status != null){
				text = new StringBuffer(status.getMessage());
				if (status instanceof MultiStatus){
					MultiStatus ms = (MultiStatus)status;
					for (IStatus s : ms.getChildren()){
						text.append(s.getMessage());
						text.append("\n");
					}
				}
				if (status.getSeverity() == IStatus.WARNING){
					setErrorMessage(text.toString());
				} else {
					setErrorMessage(text.toString());
				}
			}
		}
	};

	public EditLatLongUtmDialog(Shell parentShell) {
		super(parentShell);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
    protected Control createDialogArea(Composite parent) {
		
		Composite control = (Composite)super.createDialogArea(parent);
		
		Composite container = new Composite(control, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		container.setLayout(gridLayout);
		
		m_editLatLongButton = new Button(container, SWT.RADIO);
		m_editLatLongButton.setText("Edit Latitude / Longitude");
		m_editLatLongButton.setSelection(m_editingLatLong);
		m_editLatLongButton.addSelectionListener(m_editingSelectionListener);
		
		// add the lat / long widget
		try {
			m_latLongWidget = BoundWidgetFactory.getWidget(LatLong.class, container, SWT.NONE, false, false);
		} catch (NullPointerException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		
		
		final GridData topData =  new GridData(SWT.LEAD, SWT.TOP,false,true);
		topData.grabExcessHorizontalSpace = false;
		topData.grabExcessVerticalSpace = true;
		m_latLongWidget.setLayoutData(topData);
		m_latLongWidget.addListener(m_errorStatusListener);
		m_latLongWidget.setEnabled(m_editingLatLong);

		m_editUTMButton = new Button(container, SWT.RADIO);
		m_editUTMButton.setText("Edit UTM");
		m_editUTMButton.setSelection(!m_editingLatLong);
		m_editUTMButton.addSelectionListener(m_editingSelectionListener);
		
		
		try {
			m_utmWidget = BoundWidgetFactory.getWidget(UTM.class, container, SWT.NONE, false, false);
		} catch (NullPointerException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		final GridData topData2 =  new GridData(SWT.RIGHT, SWT.TOP,false,true);
		topData2.grabExcessHorizontalSpace = false;
		topData2.grabExcessVerticalSpace = true;
		m_utmWidget.setLayoutData(topData2);
		m_utmWidget.addListener(m_errorStatusListener);
		
		initializeValues();
		m_utmWidget.setEnabled(!m_editingLatLong);
		
		setMessage(DEFAULT_TEXT);
		setTitle("Edit Lat/Long/UTM");
		
		m_latLongWidget.addListener(new IChangeListener() {
            public void handleChange(ChangeEvent event) {
				if (m_editingLatLong && !m_updatingUTM){
					m_latLongValid = m_latLongWidget.isValid();
					if (m_latLongValid){
						UTM convertedUTM =UtmLatLongConverter.toUTM(m_latLong, EllipsoidReference.WGS_84.getEllipsoid());
						updateUTM(convertedUTM);
					}
				}
			}
		});
		
		m_utmWidget.addListener(new IChangeListener() {
            public void handleChange(ChangeEvent event) {
				if (!m_editingLatLong && !m_updatingLatLong){
					m_utmValid = m_utmWidget.isValid();
					if (m_utmValid){
						LatLong convertedLatLong = UtmLatLongConverter.toLatLong(m_utm, EllipsoidReference.WGS_84.getEllipsoid());
						updateLatLong(convertedLatLong);
					}
				}
			}
		});

		
		return container;
	}
	
	
	/**
	 * Initialize the lat long and utm widgets with some default values
	 */
	protected void initializeValues() {
		if (m_latLong != null){
			setLatLong(m_latLong);
		} else {
			setLatLong(new LatLong(0.0, 0.0));
		}
		if (m_utm != null){
			setUTM(m_utm);
		} else {
			setUTM(new UTM());
		}
	}
	
	/**
	 * @param ll
	 */
	public void setLatLong(LatLong ll){
		m_latLong = ll;
		if (m_latLongWidget != null){
			m_latLongWidget.setModel(ll);
		}
	}
	
	/**
	 * This gets the lat long out of the widget
	 * @return
	 */
	public LatLong getLatLong(){
		if (m_latLongWidget != null){
			return (LatLong)m_latLongWidget.getModel();
		}
		return null;
	}
	
	/**
	 * Set or update the latlong values.
	 * Set the semaphore during update.
	 * @param newLatLong
	 */
	public synchronized void updateLatLong(LatLong newLatLong){
		if (m_latLong == null){
			setLatLong(newLatLong);
			return;
		}
		m_updatingLatLong = true;
		m_latLong.fillValues(newLatLong);
		m_updatingLatLong = false;
	}
	
	/**
	 * @param utm
	 */
	public void setUTM(UTM utm){
		m_utm = utm;
		if (m_utmWidget != null){
			m_utmWidget.setModel(utm);
		}
	}
	
	/**
	 * This gets the UTM out of the widget
	 * @return
	 */
	public UTM getUTM() {
		if (m_utmWidget != null){
			return (UTM) m_utmWidget.getModel();
		}
		return null;
	}
	
	/**
	 * Set or update the UTM values.
	 * Set the semaphore during update.
	 * @param newUTM
	 */
	public synchronized void updateUTM(UTM newUTM){
		if (m_utm == null){
			setUTM(newUTM);
			return;
		}
		m_updatingUTM = true;
		m_utm.fillValues(newUTM);
		m_updatingUTM = false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#setErrorMessage(java.lang.String)
	 */
	@Override
    public void setErrorMessage(String newErrorMessage) {
		super.setErrorMessage(newErrorMessage);
		getButton(IDialogConstants.OK_ID).setEnabled(newErrorMessage == null);
	}
	
}
