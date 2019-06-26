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

import java.lang.reflect.Method;

import gov.nasa.ensemble.ui.databinding.util.MethodUtil;
import gov.nasa.ensemble.ui.databinding.util.StringUtil;
import gov.nasa.ensemble.ui.databinding.widgets.customization.FieldCustomization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.GroupCustomization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.ICustomization;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * This is a generic widget for databinding. Currently it supports the following:
 * get/set methods will produce a labeled bound text widget
 * is/set methods will produce a checkbox with label
 * 
 * This uses reflection to construct the widget.
 * @author tecohen
 *
 */
@SuppressWarnings("unchecked")
public class GenericBoundWidget extends AbstractGenericWidget {

	/**
	 * Constructor.
	 * @param pClass
	 * @param parent
	 * @param style
	 */
	public GenericBoundWidget(Class pClass, Composite parent, int style) {
		super(pClass, parent, style);
		constructWidget();
	}
	
	/**
	 * Constructor.
	 * @param pClass
	 * @param parent
	 * @param style
	 * @param horizontal
	 */
	public GenericBoundWidget(Class pClass, Composite parent, int style, boolean horizontal) {
		super(pClass, parent, style, horizontal);
		constructWidget();
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
		
		// check for is for boolean
		try {
			Method isMethod = MethodUtil.getIsMethod(m_class, suffix);
			Method setMethod = MethodUtil.getSetMethod(m_class, suffix, isMethod.getReturnType());
			if (setMethod != null) {
				Button button = new Button(container, SWT.CHECK); 
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
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.widgets.AbstractGenericWidget#createGroup(gov.nasa.ensemble.ui.databinding.databinding.WidgetFieldInfo, org.eclipse.swt.widgets.Composite)
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
			container = new Composite(parent, SWT.NONE);
			container.setLayout(new GridLayout(2, false));
			GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, false);
			gd.horizontalSpan = m_numColumns;
			container.setLayoutData(gd);
			horizSpan = 1;
			groupFlag = createGroupFlag(groupCustomization.getFlagName(), container);
		}
		
		final Group group = new Group(container, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.horizontalSpan = horizSpan;
		group.setLayout(m_masterColumnLayout);
		group.setLayoutData(gd);
		group.setText(groupCustomization.getName());
		if (groupCustomization.getDescription() != null && groupCustomization.getDescription().length() > 0){
			Label label = new Label(group, SWT.NONE);
			label.setText(groupCustomization.getDescription());
			GridData labelGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
			labelGridData.horizontalSpan = horizSpan;
			label.setLayoutData(labelGridData);
		}
		
		if (groupCustomization.getChildren() != null){
			for (String key : groupCustomization.getChildren()){
				GroupCustomization childGroup = customization.getGroup(key);
				if (childGroup != null){
					createGroup(customization, childGroup, group);
				} else {
					FieldCustomization childField = customization.getField(key);
					if (childField != null && !childField.isHidden()){
						createField(childField, group, groupCustomization.isSkipLabel());
					}
				}
			}
		}
		
		if (groupFlag != null){
			groupFlag.addSelectionListener(new SelectionListener() {

				protected void process(SelectionEvent e){
					Button but = (Button)e.widget;
					boolean enabled = but.getSelection();
					//section.setEnabled(enabled);
					for (Control child : group.getChildren()) {
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
		
		return group;
	}
	
}
