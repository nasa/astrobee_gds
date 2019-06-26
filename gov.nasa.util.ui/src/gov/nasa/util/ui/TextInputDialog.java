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
package gov.nasa.util.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author mallan
 *
 */
public class TextInputDialog extends Dialog {
	Label m_label;
	Combo m_textCombo;
	final String m_dialogTitle;
	final String m_labelText;
	final String m_initialText;
	final String [] m_textOptions;
	boolean m_trimWhitespace = true;

	public int dialogWidth = 600;
	
	String m_value = null;

	public TextInputDialog(String title, String prompt) {
		this(title, prompt, "");
	}
	public TextInputDialog(String title, String prompt, String initialText) {
		this(title, prompt, initialText, null);
	}
	public TextInputDialog(String title, String prompt, String initialText, String[] textOptions) {
		super(Display.getDefault().getActiveShell());
		m_dialogTitle = title;
		m_labelText = prompt;
		m_initialText = initialText;
		m_textOptions = textOptions;
	}

	/**
	 * get the string that was in the input dialog when Ok was pressed. If
	 * user pressed Cancel instead, this will return null
	 * @param trimWhitespace remove leading and trailing whitespace from value
	 */
	public String getValue(boolean trimWhitespace) {
		if(m_value != null && trimWhitespace) {
			return m_value.trim();
		}
		return m_value;
	}

	/**
	 * get the string that was in the input dialog when Ok was pressed. If
	 * user pressed Cancel instead, this will return null
	 */
	public String getValue() {
		return m_value;
	}


	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		GridData gridData;
		m_label = new Label(composite, SWT.NONE);
		m_label.setText(m_labelText);
		m_label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		m_textCombo = new Combo(composite, SWT.DROP_DOWN | SWT.BORDER);
		if(m_textOptions != null) {
			m_textCombo.setItems(m_textOptions);
		}
		if(m_initialText != null) {
			m_textCombo.setText(m_initialText);
		}
		m_textCombo.setVisibleItemCount(20);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = dialogWidth;
		m_textCombo.setLayoutData(gridData);

		return composite;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(m_dialogTitle);
	}


	@Override
	protected void okPressed() {
		m_value = m_textCombo.getText();
		super.okPressed();
	}

}
