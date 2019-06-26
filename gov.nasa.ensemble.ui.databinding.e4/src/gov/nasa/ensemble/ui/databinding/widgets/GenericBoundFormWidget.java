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
package gov.nasa.ensemble.ui.databinding.widgets;

import gov.nasa.ensemble.ui.databinding.util.MethodUtil;
import gov.nasa.ensemble.ui.databinding.util.StringUtil;
import gov.nasa.ensemble.ui.databinding.widgets.customization.FieldCustomization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.GroupCustomization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.ICustomization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.FieldCustomization.WidgetType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * This widget will generate form compliant databound non EMF widgets.
 * @author tecohen
 *
 */
@SuppressWarnings("unchecked")
public class GenericBoundFormWidget extends AbstractGenericWidget {
	
	protected FormToolkit m_toolkit; // the form toolkit we are using to construct our widgets

	/**
	 * Constructor.
	 * @param pClass
	 * @param parent
	 * @param style
	 */
	public GenericBoundFormWidget(Class pClass, FormToolkit toolkit, Composite parent, int style) {
		super(pClass, parent, style, false);
		m_toolkit = toolkit;
		setBackground(m_toolkit.getColors().getBackground());
		setForeground(m_toolkit.getColors().getForeground());
		constructWidget();
	}
	
	/**
	 * Constructor.
	 * @param pClass
	 * @param parent
	 * @param style
	 * @param horizontal
	 */
	public GenericBoundFormWidget(Class pClass, FormToolkit toolkit, Composite parent, int style, boolean horizontal) {
		super(pClass, parent, style, horizontal);
		m_toolkit = toolkit;
		setBackground(m_toolkit.getColors().getBackground());
		setForeground(m_toolkit.getColors().getForeground());
		constructWidget();
	}
	
	/**
	 * Construct the control
	 * @param container
	 * @param controlClass
	 * @param swtOptions
	 * @param text
	 * @return
	 */
	@Override
	protected Control createControl(Composite container, WidgetType wtype, Class controlClass, int swtOptions, String text){
		if (m_toolkit == null){
			return super.createControl(container, wtype, controlClass, swtOptions, text);
		}
		if (wtype != null){
			switch (wtype){
			case LABEL:
				return m_toolkit.createLabel(container, text, swtOptions);
			case TEXT:
				return m_toolkit.createText(container, "", swtOptions);
			default:
				throw new IllegalArgumentException("Unsupported widget type: "+wtype);
			}
		}
		if (controlClass.equals(Label.class)){
			return m_toolkit.createLabel(container, text, swtOptions);
		} else if (controlClass.equals(Text.class)){
			return m_toolkit.createText(container, "", swtOptions);
		} else if (controlClass.equals(Button.class)){
			return m_toolkit.createButton(container, text, swtOptions);
		}
		return super.createControl(container, wtype, controlClass, swtOptions, text);
	}
	
	/**
	 * Create the given field described by the field info (for customization)
	 * @param fieldInfo
	 * @param container
	 */
	protected Button createGroupFlag(String suffix, Composite container){
		if (suffix == null || container == null){
			return null;
		}
		String fieldKey = StringUtil.lowerFirstChar(suffix, false);
		//m_widgetFieldInfoMap.put(fieldKey, fieldInfo);
		
		// check for is for boolean
		try {
			Method isMethod = MethodUtil.getIsMethod(m_class, suffix);
			Method setMethod = MethodUtil.getSetMethod(m_class, suffix, isMethod.getReturnType());
			if (setMethod != null) {
				Button button = m_toolkit.createButton(container, "", SWT.CHECK);
				GridData gridData = new GridData(SWT.RIGHT, SWT.TOP, true, false);
				button.setLayoutData(gridData);
				setToolTipText(button, suffix);
				addChildControl(button);
				m_propertyControlMap.put(fieldKey, button);
				return button;
			} 
		} catch (SecurityException ex) {
			//logger.warn(ex);
		} catch (NoSuchMethodException ex) {
			//logger.warn(ex);
		}
		return null;
	}
	
	/**
	 * Create a group and its child widgets
	 * @param groupCustomization
	 * @param container
	 */
	@Override
	protected Composite createGroup(ICustomization customization, GroupCustomization groupCustomization, Composite parent){
		if (groupCustomization == null || parent == null || customization == null){
			return null;
		}
		
		Composite container = parent;
		int horizSpan = m_numColumns;
		
		Button groupFlag = null;
		if (groupCustomization.hasFlagName() ){
			// we must make a checkbox to the left.  add a new composite for this first
			container = m_toolkit.createComposite(parent);
			container.setLayout(new GridLayout(2, false));
			GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, false);
			gd.horizontalSpan = m_numColumns;
			container.setLayoutData(gd);
			horizSpan = 1;
			groupFlag = createGroupFlag(groupCustomization.getFlagName(), container);
		}
			
		int type =  SWT.None;
		if (groupCustomization.isTitleBar()) {
			type |= ExpandableComposite.TITLE_BAR;
		}
		if (groupCustomization.isTwistie()){
			type |= ExpandableComposite.TWISTIE;
			if (groupCustomization.isExpanded()){
				type |= ExpandableComposite.EXPANDED;
			}
		}
		final Section section = m_toolkit.createSection(container, type);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.horizontalSpan = horizSpan;
		section.setLayoutData(gd);
		section.setText(groupCustomization.getName());
		section.clientVerticalSpacing = 0;
		
		final Composite sectionComposite = m_toolkit.createComposite(section);
		sectionComposite.setLayout(m_masterColumnLayout);
		
		if (groupCustomization.getDescription() != null && groupCustomization.getDescription().length() > 0){
			Label label = m_toolkit.createLabel(sectionComposite, groupCustomization.getDescription());
			GridData labelGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
			labelGridData.horizontalSpan = horizSpan;
			label.setLayoutData(labelGridData);
		}
		
		List<String> fieldKeys = new ArrayList<String>();
		for (String key : groupCustomization.getChildren()){
			GroupCustomization childGroup = customization.getGroup(key);
			if (childGroup != null){
				createGroup(customization, childGroup, sectionComposite);
			} else {
				FieldCustomization childField = customization.getField(key);
				if (childField != null && !childField.isHidden()){
					createField(childField, sectionComposite, groupCustomization.isSkipLabel());
					fieldKeys.add(childField.getName());
				}
			}
		}
		
		if (groupFlag != null){
			groupFlag.addSelectionListener(new SelectionListener() {

				protected void process(SelectionEvent e){
					Button but = (Button)e.widget;
					boolean enabled = but.getSelection();
					//section.setEnabled(enabled);
					for (Control child : sectionComposite.getChildren()) {
						child.setEnabled(enabled);
					}
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {
					process(e);
				}

				public void widgetSelected(SelectionEvent e) {
					process(e);
				}
				
			});
		}
		
		section.setClient(sectionComposite);
		
		return section;
	}

	@Override
	public FormToolkit getToolkit() {
		return m_toolkit;
	}

	public void setToolkit(FormToolkit toolkit) {
		m_toolkit = toolkit;
	}
	
	
	
	
}
