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

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * A field editor for a string type preference.
 * <p>
 * This class may be used as is, or subclassed as required.
 * </p>
 */
public class InfoTextFieldEditor extends FieldEditor {
    protected String  textString = "";
    protected Text    textField;
    protected boolean fixedWidthFont = false;

    public InfoTextFieldEditor(String labelText, Composite parent) {
        init(this.getClass().getSimpleName(), labelText);
        createControl(parent);
    }

    public void updateInfoText() {
        // to be implemented
    }

    @Override
    protected void adjustForNumColumns(int numColumns) {
        GridData gd = (GridData) textField.getLayoutData();
        gd.grabExcessVerticalSpace = true;
        gd.horizontalSpan = numColumns - 1;
        // We only grab excess space if we have to
        // If another field editor has more columns then
        // we assume it is setting the width.
        gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
    }


    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        control.setLayoutData(gd);


        textField = getTextControl(parent);
        gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.verticalAlignment = GridData.FILL;
        gd.grabExcessVerticalSpace = true;
        textField.setLayoutData(gd);
    }

    @Override
    protected void doLoad() {
        // nada
    }

    @Override
    protected void doLoadDefault() {
        // nada
    }

    @Override
    protected void doStore() {
        // nada
    }

    @Override
    public int getNumberOfControls() {
        return 2;
    }

    public void setFixedWidthFont(boolean fixed) {
        fixedWidthFont = fixed;
        updateFont();
    }
    
    protected void updateFont() {
        if(textField != null) {
            Font font;
            if(fixedWidthFont) {
                font = JFaceResources.getTextFont();
            }
            else {
                font = JFaceResources.getDefaultFont();
            }
            textField.setFont(font);
        }
    }
    
    /**
     * Returns this field editor's text control.
     */
    protected Text getTextControl() {
        return textField;
    }

    /**
     *
     */
    public Text getTextControl(Composite parent) {
        if (textField == null) {
            textField = new Text(parent, SWT.MULTI | SWT.BORDER);
            textField.setText(textString);
            textString = null;
            textField.setEditable(false);
            updateFont();
        }
        return textField;
    }

    @Override
    public void setFocus() {
        if (textField != null) {
            textField.setFocus();
        }
    }

    public void setStringValue(String value) {
        if (textField == null) {
            textString = value;
        }
        else {
            if (value == null) {
                value = "";
            }
            textField.setText(value);
        }
    }

    @Override
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        getTextControl(parent).setEnabled(enabled);
    }
}
