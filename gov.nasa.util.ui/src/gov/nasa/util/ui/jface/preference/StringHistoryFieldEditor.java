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
package gov.nasa.util.ui.jface.preference;

import gov.nasa.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class StringHistoryFieldEditor extends FieldEditor {
	public static final String OTHER_VALUES = "_OTHER_VALUES";
	protected CCombo m_combo; // combo widget
	protected String m_value;  // current value
	protected List<String> m_entryValues = new ArrayList<String>(); // history
	protected boolean m_spacer = false; // true if there should be a spacer after this field

    /**
     * Construct one of these and populate the values of the dropdown
     * 
     * @param name
     * @param labelText
     * @param values
     * @param parent
     * @param spacer
     */
    public StringHistoryFieldEditor(String name, String labelText, String[] values, Composite parent, boolean spacer) {
        m_spacer = spacer;
        init(name, labelText);
        if (values != null && values.length > 0) {
            for (String s : values) {
                m_entryValues.add(s);
            }
        }
        createControl(parent);
    }
	
	/**
	 * Construct one of these and populate the values of the dropdown
	 * @param name
	 * @param labelText
	 * @param values
	 * @param parent
	 */
	public StringHistoryFieldEditor(String name, String labelText, String[] values, Composite parent) {
		init(name, labelText);
		if (values != null && values.length > 0){
			for (String s : values){
				m_entryValues.add(s);
			}
		}
		createControl(parent);	
	}

	/**
	 * Construct one of these
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public StringHistoryFieldEditor(String name, String labelText, Composite parent) {
		init(name, labelText);
		createControl(parent);	
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(int numColumns) {
		if (numColumns > 1) {
			Control control = getLabelControl();
			int left = numColumns;
			if (control != null) {
				((GridData)control.getLayoutData()).horizontalSpan = 1;
				left = left - 1;
			}
			((GridData)m_combo.getLayoutData()).horizontalSpan = left;
		} else {
			Control control = getLabelControl();
			if (control != null) {
				((GridData)control.getLayoutData()).horizontalSpan = 1;
			}
			((GridData)m_combo.getLayoutData()).horizontalSpan = 1;			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		int comboC = 1;
		if (numColumns > 1) {
			comboC = numColumns - 1;
		}
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = SWT.TRAIL;
		control.setLayoutData(gd);
		
		CCombo combo = getComboBoxControl(parent);
		gd = new GridData(SWT.FILL, SWT.None, true, false);
		gd.horizontalSpan = comboC;
		gd.horizontalAlignment = GridData.FILL;
		combo.setLayoutData(gd);
		combo.setFont(parent.getFont());
		
		if (m_spacer){
			Label label = new Label(parent, SWT.NONE);
			GridData labelData = new GridData();
			labelData.horizontalSpan = comboC + 1;
			label.setLayoutData(labelData);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
		m_value = getPreferenceStore().getString(getPreferenceName());
		m_combo.setText(m_value);
		m_combo.setSelection(new Point(0,0));
		initializeHistory();
		if (m_entryValues.size() > 0){
			for (String s : m_entryValues){
				m_combo.add(s);
			}
		}
	}
	
	protected void initializeHistory() {
		String otherValues = getPreferenceStore().getString(getPreferenceName() + OTHER_VALUES);
		List<String> retrievedHistory = StrUtil.fromSingleString(otherValues, " ");
		for (String s : retrievedHistory){
			if (!m_entryValues.contains(s)) {
				m_entryValues.add(s);
			}
		}
	}
	/**
	 * Add the new value to the history and the stored cache.
	 * @param value
	 */
	protected void addValue(String value){
		if (!m_entryValues.contains(m_value)){
			m_entryValues.add(m_value);
			m_combo.add(m_value);
			storeHistory();
		}
	}
	
	/**
	 * Store the history to the preference store.
	 */
	protected void storeHistory() {
		String history = StrUtil.toSingleString(m_entryValues, " ");
		getPreferenceStore().setValue(getPreferenceName() + OTHER_VALUES, history);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		m_value = getPreferenceStore().getDefaultString(getPreferenceName());
		m_combo.setText(m_value);
		String otherValues = getPreferenceStore().getDefaultString(getPreferenceName() + OTHER_VALUES);
		List<String> defaultHistory = StrUtil.fromSingleString(otherValues, " ");
		for (String s : defaultHistory){
			if (!m_entryValues.contains(s)){
				m_entryValues.add(s);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		m_value = m_combo.getText();
		if (m_value == null) {
			getPreferenceStore().setToDefault(getPreferenceName());
			return;
		}
		addValue(m_value);
		getPreferenceStore().setValue(getPreferenceName(), m_value);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/*
	 * Lazily create and return the Combo control.
	 */
	protected CCombo getComboBoxControl(Composite parent) {
		if (m_combo == null) {
			m_combo = new CCombo(parent, SWT.BORDER);
			m_combo.setFont(parent.getFont());
			m_combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					String oldValue = m_value;
					m_value = m_combo.getText();
					setPresentsDefaultValue(false);
					fireValueChanged(VALUE, oldValue, m_value);					
				}
			});
			
			m_combo.addFocusListener(new FocusListener() {

				public void focusLost(FocusEvent e) {
					String oldValue = m_value;
					m_value = m_combo.getText();
					setPresentsDefaultValue(false);
					fireValueChanged(VALUE, oldValue, m_value);	
				}

				public void focusGained(FocusEvent e) {
					//NOOP
				}
				
			});
		}
		return m_combo;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#setEnabled(boolean,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getComboBoxControl(parent).setEnabled(enabled);
	}

}
