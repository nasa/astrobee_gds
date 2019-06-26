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
package gov.nasa.arc.irg.georef.ui.views;

import gov.nasa.arc.irg.georef.coordinates.EllipsoidReference;
import gov.nasa.arc.irg.georef.coordinates.LatLong;
import gov.nasa.arc.irg.georef.coordinates.UTM;
import gov.nasa.arc.irg.georef.coordinates.util.UtmLatLongConverter;
import gov.nasa.arc.irg.georef.ui.widgets.EllipsoidWidget;
import gov.nasa.arc.irg.georef.ui.widgets.LatLongWidget;
import gov.nasa.ensemble.ui.databinding.databinding.BoundWidgetFactory;
import gov.nasa.ensemble.ui.databinding.status.IStatusListener;
import gov.nasa.ensemble.ui.databinding.util.UndoRedoUtil;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class LatLongUtmView extends ViewPart {

	public static final String DEFAULT_TEXT = "Convert between Latitude / Longitude and UTM";
	
	private AbstractDatabindingWidget m_latLongWidget;
	private LatLong m_latLong;
	
	private EllipsoidWidget m_ellipsoidWidget;
	
	private Button m_latLong2UTMButton;
	private Button m_UTM2LatLongButton;
	
	private AbstractDatabindingWidget m_utmWidget;
	private UTM m_utm;

	private IStatusListener m_errorStatusListener = new IStatusListener() {

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
				//	image = getWarningImage();
					setErrorMessage(text.toString());
				} else {
				//	image = getErrorImage();
					setErrorMessage(text.toString());
				}
			}
			/*
			imageLabel.setImage(image);
			imageLabel.redraw();
			imageLabel.update();
			*/
			
		}
	};

	public LatLongUtmView() {
	    //
	}
	
	protected void setErrorMessage(String message){
		//TODO
	}

	@Override
	public void createPartControl(Composite parent) {
		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL|SWT.V_SCROLL);
		scrolledComposite.setLayout(new GridLayout());
		
		Composite container = new Composite(scrolledComposite, SWT.NONE);
		container.setLayout(new GridLayout(3, false));

		// add the lat / long widget on the left
		m_latLongWidget = new LatLongWidget(container, SWT.NONE);
		m_latLongWidget.setLayoutData(new GridData(SWT.LEFT, SWT.TOP,false,false));
		m_latLongWidget.addListener(m_errorStatusListener);
		m_latLongWidget.addListener(new IStatusListener() {
			public void statusChanged(IStatus status) {
				enableToUTM(m_latLongWidget.isValid());
			}
		});

		Composite middle = new Composite(container, SWT.NONE);
		middle.setLayout(new GridLayout(1, false));
		
		GridData middleData = new GridData(SWT.CENTER, SWT.TOP, true, false);
		middle.setLayoutData(middleData);
		
		m_ellipsoidWidget = new EllipsoidWidget(middle, SWT.NONE, false);
		m_ellipsoidWidget.setEllipsoid(EllipsoidReference.WGS_84.toString());
		m_ellipsoidWidget.setLayoutData(middleData);
		
		m_latLong2UTMButton = new Button(middle, SWT.NONE);
		m_latLong2UTMButton.setText("To UTM >>");

		GridData buttonData = new GridData(SWT.CENTER, SWT.TOP, true, false);
		buttonData.widthHint = 120;
		m_latLong2UTMButton.setLayoutData(buttonData);
		
		m_latLong2UTMButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			    //
			}

			public void widgetSelected(SelectionEvent e) {
				UTM utm = UtmLatLongConverter.toUTM((LatLong)m_latLongWidget.getModel(), m_ellipsoidWidget.getEllipsoid());
				if (getUTM() == null) {
					setUTM(utm);
				} else {
					UTM oldUTM = getUTM();
					oldUTM.setEasting(utm.getEasting());
					oldUTM.setNorthing(utm.getNorthing());
					oldUTM.setZone(utm.getZone());
					oldUTM.setNorthernHemisphere(utm.isNorthernHemisphere());
					oldUTM.setLetterDesignator(utm.getLetterDesignator());
				}
			}
			
		});
		
		m_UTM2LatLongButton = new Button(middle, SWT.NONE);
		m_UTM2LatLongButton.setText("<< To Lat/Long");
		m_UTM2LatLongButton.setLayoutData(buttonData);
		m_UTM2LatLongButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			    //
			}

			public void widgetSelected(SelectionEvent e) {
				LatLong latLong = UtmLatLongConverter.toLatLong((UTM)m_utmWidget.getModel(), m_ellipsoidWidget.getEllipsoid());
				if (getLatLong() == null){
					setLatLong(latLong);
				} else {
					LatLong oldLatLong = getLatLong();
					oldLatLong.setLatitude(latLong.getLatitude());
					oldLatLong.setLongitude(latLong.getLongitude());
				}
			}
			
		});
		
		try {
			m_utmWidget = BoundWidgetFactory.getWidget(UTM.class, container, SWT.NONE, false, false);
		} catch (NullPointerException e1) {
		    //
		} catch (ClassNotFoundException e1) {
		    //
		} catch (InstantiationException e) {
		    //
		}
		final GridData topData2 =  new GridData(SWT.RIGHT, SWT.TOP,false,false);
		m_utmWidget.setLayoutData(topData2);
		m_utmWidget.addListener(m_errorStatusListener);
		m_utmWidget.addListener(new IStatusListener() {
			public void statusChanged(IStatus status) {
				enableToLatLong(m_utmWidget.isValid());
			}
		});
		
		// support auto scrolling 
		container.setSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite.setContent(container);

		initializeValues();
		
		UndoRedoUtil.createViewUndoRedo(getViewSite());
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * @param enabled
	 */
	protected void enableToUTM(boolean enabled){
		m_latLong2UTMButton.setEnabled(enabled);
	}
	
	/**
	 * @param enabled
	 */
	protected void enableToLatLong(boolean enabled){
		m_UTM2LatLongButton.setEnabled(enabled);
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

}
