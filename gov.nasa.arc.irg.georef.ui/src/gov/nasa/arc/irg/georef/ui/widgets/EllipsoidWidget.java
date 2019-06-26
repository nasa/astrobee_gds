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

import gov.nasa.arc.irg.georef.coordinates.Ellipsoid;
import gov.nasa.arc.irg.georef.coordinates.EllipsoidReference;
import gov.nasa.util.StrUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Widget to support selection of a reference Ellipsoid
 * @author tecohen
 *
 */
public class EllipsoidWidget extends Composite {
	
	Label m_ellipsoidLabel;
	Combo m_ellipsoidCombo;
	
	boolean m_horizontal = true;
	
	/**
	 * Constructor
	 * @param parent
	 * @param style
	 */
	public EllipsoidWidget(Composite parent, int style){
		super(parent, style);
		createControls(this);
	}
	
	/**
	 * Constructor
	 * @param parent
	 * @param style
	 */
	public EllipsoidWidget(Composite parent, int style, boolean horizontal){
		super(parent, style);
		m_horizontal = horizontal;
		createControls(this);
	}
	
	/**
	 * Actually create the child controls
	 * @param container
	 */
	public void createControls(Composite container){
		final GridLayout gridLayout = new GridLayout();
		if (m_horizontal){
			gridLayout.numColumns = 2;
		}
		setLayout(gridLayout);
		
		m_ellipsoidLabel = new Label(container, SWT.TRAIL);
		m_ellipsoidLabel.setText(" Use " + StrUtil.upperFirstChar(Ellipsoid.class.getSimpleName(), true));
		if (m_horizontal){
			m_ellipsoidLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,true,false));
		}
		m_ellipsoidCombo = new Combo(container, SWT.READ_ONLY);
		
		for (EllipsoidReference ref : EllipsoidReference.values()){
			m_ellipsoidCombo.add(ref.toString());
			m_ellipsoidCombo.setData(ref.toString(), ref.getEllipsoid());
		}
	} 

	/**
	 * @return the selected Ellipsoid, or null if none is selected
	 */
	public Ellipsoid getEllipsoid(){
		if (m_ellipsoidCombo == null){
			return null;
		}
		String selected = m_ellipsoidCombo.getText();
		if (selected == null || selected.length() == 0){
			return null;
		}
		
		return (Ellipsoid)m_ellipsoidCombo.getData(selected);
	}
	
	/**
	 * Set the selected ellipsoid to the one indicated by this name.
	 * @param name
	 */
	public void setEllipsoid(String name){
	    if (name == null || name.length() == 0){
	        return;
	    }
	    try {
    		EllipsoidReference ref = EllipsoidReference.valueOf(name);
    		if (ref != null){
    			m_ellipsoidCombo.select(ref.ordinal());
    		}
	    } catch (Exception ex){
	        // no good.
	    }
	}
	
	/**
	 * Set the selected ellipsoid to the one indicated by the name of the given ellipsoid.
	 * TODO: this does not support ellipsoids that are not in the reference list.
	 * @param ellipsoid
	 */
	public void setEllipsoid(Ellipsoid ellipsoid){
		setEllipsoid(ellipsoid.getName());
	}

}
