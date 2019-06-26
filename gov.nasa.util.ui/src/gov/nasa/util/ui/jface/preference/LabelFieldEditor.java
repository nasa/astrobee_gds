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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A field editor for displaying labels not associated with other widgets.
 * From Simplifying Preference Pages with Field Editors by Ryan Cooper
 */
public class LabelFieldEditor extends FieldEditor {

    private Label  label;

    // All labels can use the same preference name since they don't
    // store any preference.
    public LabelFieldEditor(String value, Composite parent) {
        super("label", value, parent);
    }

    // Adjusts the field editor to be displayed correctly
    // for the given number of columns.
    @Override
    protected void adjustForNumColumns(int numColumns) {
        ((GridData) label.getLayoutData()).horizontalSpan = numColumns;
    }

    // Fills the field editor's controls into the given parent.
    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        label = getLabelControl(parent);

        GridData gridData = new GridData();
        gridData.horizontalSpan = numColumns;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessVerticalSpace = false;

        label.setLayoutData(gridData);
    }

    public void setBold(boolean state) {
        try { // unbelievable how much work it takes just to tweak the font a bit...
            FontData fontData = label.getFont().getFontData()[0];
            String name   = fontData.getName();
            int    style  = state ? SWT.BOLD : SWT.NORMAL;
            int    height = fontData.getHeight();
            fontData = new FontData(name, height, style);
            label.setFont(new Font(label.getDisplay(), fontData));
        }
        catch(Throwable t) {
            //
        }
    }
    
    // Returns the number of controls in the field editor.
    @Override
    public int getNumberOfControls() {
        return 1;
    }

    // Labels do not persist any preferences, so these methods are empty.
    @Override
    protected void doLoad() {
        //do nothing
    }
    @Override
    protected void doLoadDefault() {
        //do nothing
    }
    @Override
    protected void doStore() {
        //do nothing
    }
}
