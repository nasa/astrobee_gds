package gov.nasa.arc.verve.ui.e4.widget;
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

import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * This is a widget for editing a Vector3.
 * It actually works instead with a Vector3Model which provides basic bean notification when modifications are made.
 * This allows undo/redo to work.
 * 
 * @author tecohen
 *
 */
public class Vector3Widget extends AbstractDatabindingWidget {

	protected Text m_xText;
	protected Text m_yText;
	protected Text m_zText;
	
	public Vector3Widget(Composite parent, int style) {
		super(parent, style);
		createControls(parent);
	}
	
	/**
	 * Actually create the UI components
	 * @param container
	 */
	public void createControls(Composite contents) {
		GridLayout gl = new GridLayout(3, true);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		setLayout(gl);
		
		final GridData leftData = new GridData(SWT.FILL, SWT.FILL, true, false);
		leftData.widthHint = 60;
		
		m_xText = new Text(this, SWT.BORDER);
		m_xText.setLayoutData(leftData);
		addChildControl(m_xText);
		
		m_yText = new Text(this, SWT.BORDER);
		m_yText.setLayoutData(leftData);
		addChildControl(m_yText);
		
		m_zText = new Text(this, SWT.BORDER);
		m_zText.setLayoutData(leftData);
		addChildControl(m_zText);
	}

	/**
	 * Set tooltip hints on the fields
	 * @param hints
	 */
	public void setHints(List<String> hints){
		if (hints == null || hints.isEmpty() || m_xText == null){
			return;
		}
		
		if (hints.get(0) != null) {
			m_xText.setToolTipText(hints.get(0));
		}
		if (hints.get(1) != null) {
			m_yText.setToolTipText(hints.get(1));
		}
		if (hints.get(2) != null) {
			m_zText.setToolTipText(hints.get(2));
		}
	}
	
	/**
     * Set the model you plan to edit.
     * This should be set after the UI exists, ideally.
     * always unbind before binding these
     * @param obj
     */
    @Override
    public void setModel(final Object obj){
        if (m_model != null && m_model.equals(obj)){
            return;
        }
        unbindUI();
        m_model = obj;
        bindUI(getRealm());
    }
	
	@Override
	public boolean bindUI(Realm realm) {
		if (getModel() == null){
			return false;
		}
		
		boolean worked = true;
		while (worked){
			worked = bind("x", m_xText);
			worked = bind("y", m_yText);
			worked = bind("z", m_zText);
			setBound(worked);
			return worked;
		}
		setBound(worked);
		return worked;
	}

}
