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

import org.apache.log4j.Logger;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * field editor to modify floating point values
 */
public class DoubleFieldEditor extends StringFieldEditor 
{
    private static final Logger logger = Logger.getLogger(DoubleFieldEditor.class);

    protected double m_minValidValue = -Double.MAX_VALUE;
    protected double m_maxValidValue =  Double.MAX_VALUE;

    private static final int DEFAULT_TEXT_LIMIT = 10;

    /**
     * disabled ctor
     */
    protected DoubleFieldEditor() {
    }

    /**
     * @param name preference name that we are controlling
     * @param labelText
     * @param parent
     */
    public DoubleFieldEditor(String name, String labelText, Composite parent) {
        this(name, labelText, parent, DEFAULT_TEXT_LIMIT);
    }

    /**
     * @param name preference name that we are controlling
     * @param labelText
     * @param parent
     * @param textLimit Sets this text field's text limit.
     */
    public DoubleFieldEditor(String name, String labelText, Composite parent, int textLimit) {
        init(name, labelText); // field editor init
        setEmptyStringAllowed(false);
        setTextLimit(textLimit);
        createControl(parent);
    }

    /**
     * 
     * @param min minimum double value allowed by widget
     * @param max maximum double value allowed by widget
     */
    public void setValidRange(double min, double max) {
        m_minValidValue = min;
        m_maxValidValue = max;
    }

    /**
     * @return this field editor's value as double.
     * @throws NumberFormatException if text string cannot be parsed as double
     */
    public double getDoubleValue() throws NumberFormatException {
        final String str = getStringValue();
        return Double.valueOf(str);
    }  

    /**
     * @return this field editor's value as float.
     * @throws NumberFormatException if text string cannot be parsed as double
     */
    public float getFloatValue() throws NumberFormatException {
        final String str = getStringValue();
        return Float.valueOf(str);
    }  

    @Override
    protected boolean checkState() {
        final Text text = getTextControl();
        if (text == null) {
            return false;
        }
        try {
            final String str = text.getText();
            double val = Double.valueOf(str);
            if ( (val >= m_minValidValue && val <= m_maxValidValue) || Double.isNaN(val) ) {
                clearErrorMessage();
                return true;
            }
            else {
                showErrorMessage();
                return false;
            }
        } 
        catch (NumberFormatException e) {
            showErrorMessage();
        }
        return false;
    }

    @Override
    protected void doLoad() {
        final Text text = getTextControl();
        if (text != null) {
            String strVal = getPreferenceStore().getString(getPreferenceName());
            Double value = 0.0;
            if(strVal.length() > 0) {
                try {
                    value = Double.valueOf(strVal);
                }
                catch(NumberFormatException nfe) {
                    value = Double.NaN;
                }
            }
            text.setText(value.toString());
        }
    }

    @Override
    protected void doLoadDefault() {
        final Text text = getTextControl();
        if (text != null) {
            String strVal = getPreferenceStore().getDefaultString(getPreferenceName());
            if(strVal.length() < 1 ) {
                strVal = "0.0";
            }
            text.setText(Double.valueOf(strVal).toString());
        }
        valueChanged();
    }

    @Override
    protected void doStore() {
        final Text text = getTextControl();
        if (text != null) {
            try {
                Double d = new Double(text.getText());
                getPreferenceStore().setValue(getPreferenceName(), d.toString());
            }
            catch(Throwable t) {
                logger.error("cannot store value", t);
            }
        }
    }

}
