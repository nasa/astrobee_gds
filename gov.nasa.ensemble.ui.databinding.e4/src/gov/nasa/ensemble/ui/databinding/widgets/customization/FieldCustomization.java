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
package gov.nasa.ensemble.ui.databinding.widgets.customization;

import gov.nasa.ensemble.ui.databinding.validation.ISimpleConverter;
import gov.nasa.ensemble.ui.databinding.validation.ISimpleValidator;

import java.util.List;

/**
 * Bundle together descriptive information for a particular field.
 * This will be used when auto generating databound widgets with customization.
 * 
 * @author tecohen
 *
 */
public class FieldCustomization {
	
	public enum WidgetType {
		LABEL, RADIO, SLIDER, SPINNER, TEXT, COMBO, COLORSELECTOR, SYSTEM_COLOR, BUTTON;
	}
	
	protected String m_name;			// the name of the field
	protected String m_label;			// the pretty label for the field
	protected String m_description = "";	// the tooltip / help description for the field
	protected String m_unitsLabel = "";		// the label for the units;
	protected String m_format;			  // settings for formatter for display, see java.util.Formatter
	protected boolean m_advanced = false; // set to true if this is an advanced feature that you want to turn on 
	protected boolean m_readOnly = false;	// set to true if this widget should be read only
	protected boolean m_hidden = false;		// set to true if this widget should be hidden (never ever included)
	protected boolean m_trigger = false;	// set to true if this is a trigger method that has no args and should have a push button.
	protected boolean m_complex	= false;	// set to true if this is a complex child field that should open in a non-modal popup.
	
	protected WidgetType m_widgetType;	// any special info for this widget type if it should not be the default
	protected ISimpleValidator m_validator;	// the validator; always going from the target (ui) to the model
	protected ISimpleConverter m_converter;	// any conversion from the model to the target (ui)
	
	protected List<ComboEntry> m_comboEntries; // if this is a combo widget, provide combo entries
	protected List<String>  m_simpleComboEntries; // string combo entries
	protected String		m_comboEntryMethodName;	// if this is a combo widget and the contents are provided by a method
	
	/**
	 * @param name
	 */
	public FieldCustomization(String name) {
		m_name = name;
	}
	
    /**
     * @param name
     * @param label
     * @param description
     */
    public FieldCustomization(String name, String label, String description){
        this(null, name, label, description);
    }

    /**
     * @param name
     * @param label
     * @param description
     */
    public FieldCustomization(WidgetType widgetType, String name, String label, String description){
        m_name = name;
        m_label = label;
        m_widgetType = widgetType;
        m_description = description;
    }
    
	/**
	 * Create a hidden or not customization
	 * @param name
	 * @param hidden
	 */
	public FieldCustomization(String name, boolean hidden){
		m_name = name;
		m_hidden = hidden;
		
	}
	/**
	 * The name of the field, ie if the method is getQuantity the name is quantity
	 * @return
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * @param name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * The label to be displayed to the left of the field, ie Quantity
	 * @return
	 */
	public String getLabel() {
		return m_label;
	}
	
	/**
	 * @param label
	 */
	public void setLabel(String label) {
		m_label = label;
	}
	
	/**
	 * Tooltip description displayed on hover over the label
	 * @return
	 */
	public String getDescription() {
		return m_description;
	}
	
	/**
	 * @param description
	 */
	public void setDescription(String description) {
		m_description = description;
	}
	
	/**
	 * Any special formatting of the contents of the field.
	 * This is in standard string.format syntax
	 * ie "%3.2f"
	 * @return
	 */
	public String getFormat() {
		return m_format;
	}

	/**
	 * @param format
	 */
	public void setFormat(String format) {
		m_format = format;
	}

	/**
	 * To override the default implemented generated widget type set a widget type here.
	 * For example if normally this would be a text widget but you want a label, set this to WidgetType.LABEL
	 * @return
	 */
	public WidgetType getWidgetType() {
		return m_widgetType;
	}
	
	/**
	 * @param widgetType
	 */
	public void setWidgetType(WidgetType widgetType) {
		m_widgetType = widgetType;
	}
	
	/**
	 * The label displayed to the right of the field.  Usually units but it can be anything.
	 * Used just for display purposes
	 * @return
	 */
	public String getUnitsLabel() {
		return m_unitsLabel;
	}

	/**
	 * @param unitsLabel
	 */
	public void setUnitsLabel(String unitsLabel) {
		m_unitsLabel = unitsLabel;
	}

	/**
	 * True if this field is for advanced users only
	 * @return
	 */
	public boolean isAdvanced() {
		return m_advanced;
	}

	/**
	 * @param advanced
	 */
	public void setAdvanced(boolean advanced) {
		m_advanced = advanced;
	}

	/**
	 * Get the simple validator
	 * @return
	 */
	public ISimpleValidator getValidator() {
		return m_validator;
	}
	
	/**
	 * @param validator
	 */
	public void setValidator(ISimpleValidator validator) {
		m_validator = validator;
	}
	
	/**
	 * Get the simple converter
	 * @return
	 */
	public ISimpleConverter getConverter() {
		return m_converter;
	}

	/**
	 * @param converter
	 */
	public void setConverter(ISimpleConverter converter) {
		m_converter = converter;
	}

	/**
	 * True if this field cannot be edited
	 * @return
	 */
	public boolean isReadOnly() {
		return m_readOnly;
	}

	/**
	 * @param readOnly
	 */
	public void setReadOnly(boolean readOnly) {
		m_readOnly = readOnly;
	}

	public boolean isHidden() {
		return m_hidden;
	}

	public void setHidden(boolean hidden) {
		m_hidden = hidden;
	}

	public boolean isTrigger() {
		return m_trigger;
	}

	public void setTrigger(boolean trigger) {
		m_trigger = trigger;
	}

	public boolean isComplex() {
		return m_complex;
	}

	public void setComplex(boolean complex) {
		m_complex = complex;
	}

	public List<ComboEntry> getComboEntries() {
		return m_comboEntries;
	}

	public void setComboEntries(List<ComboEntry> comboEntries) {
		m_comboEntries = comboEntries;
	}

	public List<String> getSimpleComboEntries() {
		return m_simpleComboEntries;
	}

	public void setSimpleComboEntries(List<String> simpleComboEntries) {
		m_simpleComboEntries = simpleComboEntries;
	}
	
	public void setComboEntryMethodName(String comboEntryMethodName) {
		m_comboEntryMethodName = comboEntryMethodName;
	}
	
	public String getComboEntryMethodName(){
		return m_comboEntryMethodName;
	}

	
}
