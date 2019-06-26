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

import gov.nasa.ensemble.ui.databinding.databinding.BoundWidgetFactory;
import gov.nasa.ensemble.ui.databinding.status.ISimpleStatus;
import gov.nasa.ensemble.ui.databinding.util.MethodUtil;
import gov.nasa.ensemble.ui.databinding.util.StringUtil;
import gov.nasa.ensemble.ui.databinding.util.SystemColorProvider;
import gov.nasa.ensemble.ui.databinding.validation.ISimpleConverter;
import gov.nasa.ensemble.ui.databinding.validation.ISimpleValidator;
import gov.nasa.ensemble.ui.databinding.widgets.customization.Customization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.FieldCustomization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.FieldCustomization.WidgetType;
import gov.nasa.ensemble.ui.databinding.widgets.customization.GroupCustomization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.ICustomization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.annotations.AnnotationReader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nasa.arc.irg.util.ui.ColorProvider;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * This is a generic widget for databinding. Currently it supports the following:
 * get/set methods will produce a labeled bound text widget
 * is/set methods will produce a checkbox with label
 * 
 * This uses reflection to construct the widget.
 * This also supports using annotations to describe the customization.
 * @author tecohen
 *
 */
@SuppressWarnings("unchecked")
public abstract class AbstractGenericWidget extends AbstractDatabindingWidget {

	final private static Logger logger = Logger.getLogger(AbstractGenericWidget.class);

	protected Class m_class;	// the class we are binding

	protected Map<String, Object> m_propertyControlMap = new HashMap<String, Object>();	// the properties and the controls or viewers they will bind to.

	protected Map<String, FieldCustomization> m_widgetFieldInfoMap = new HashMap<String, FieldCustomization>(); // map between controls / views and widget field infos

	protected Map<String, String> m_comboMethodMap = new HashMap<String, String>(); // map between properties and providers of selections in a combo, for when a ComboEntryMethod is defined on a FieldCustomization

	// support for toggling advanced controls
	protected List<Control> m_advancedControls = new ArrayList<Control>();

	/**
	 * Constructor.
	 * @param pClass
	 * @param parent
	 * @param style
	 */
	public AbstractGenericWidget(Class pClass, Composite parent, int style) {
		super(parent, style);
		m_class = pClass;
	}

	/**
	 * Constructor.
	 * @param pClass
	 * @param parent
	 * @param style
	 * @param horizontal
	 */
	public AbstractGenericWidget(Class pClass, Composite parent, int style, boolean horizontal) {
		super(parent, style);
		m_class = pClass;
		if (horizontal){
			m_numColumns = 6;
		}
	}


	/**
	 * Create the overall layout for the widget
	 */
	protected void createMasterLayout() {
		GridLayout layout = new GridLayout();
		layout.numColumns = m_numColumns;
		layout.makeColumnsEqualWidth = false;
		m_masterColumnLayout = layout;
	}

	/**
	 * Call the correct construction of a widget.
	 * @param pClass
	 * @param horizontal
	 */
	protected void constructWidget(){
		ICustomization customization = Customization.getCustomization(m_class);
		if (customization != null){
			createControls(this, customization);
		} else {
			createControls(this);
		}
	}

	/**
	 * This is called when the widget is customized with an ICustomization.
	 * This ICustomization can either be retrieved from the class or associated
	 * @param container
	 * @param customization
	 */
	public void createControls(Composite container, ICustomization customization){

		createMasterLayout();
		setLayout(m_masterColumnLayout);

		createNameOnlyHeaderField(container);

		if (customization.getOrderedWidgets() != null && !customization.getOrderedWidgets().isEmpty()){
			// use ordering from children list
			for (String child : customization.getOrderedWidgets()){
				GroupCustomization group = customization.getGroup(child);
				if (group != null){
					createGroup(customization, group, container);
				} else {
					FieldCustomization field = customization.getField(child);
					if (field != null && !field.isHidden()){
						createField(field, container, false);
					}
				}
			}
		} else if (customization.getGroups() != null && !customization.getGroups().isEmpty()){
			// no ordering, go through groups first
			for (GroupCustomization group : customization.getGroups()){
				createGroup(customization, group, container);
			}
		} else {
			// just use fields
			for (FieldCustomization field : customization.getFields()){
				createField(field, container, false);
			}
		}
		
		if(customization.getFields().size() < 2) {
			createNoFieldsToEditLabel(container);
		}
	}

	/**
	 * Create a group and its child widgets
	 * @param groupCustomization
	 * @param container
	 */
	protected abstract Composite createGroup(ICustomization customization, GroupCustomization groupCustomization, Composite container);

	/* (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget#bindUI(org.eclipse.core.databinding.observable.Realm)
	 */
	@Override
	public boolean bindUI(Realm realm) {
		if (getModel() == null){
			return false;
		}

		if (m_propertyControlMap.isEmpty()){
			return false;
		}

		setRealm(realm);

		if (!isBound()){

			for (String property : m_propertyControlMap.keySet()){
				Object control = m_propertyControlMap.get(property);
				String comboMethodName = m_comboMethodMap.get(property);
				if (comboMethodName != null && control instanceof ComboViewer){
					Method method;
					try {
						method = getModel().getClass().getMethod(comboMethodName, (Class[]) null);
						Object result = method.invoke(getModel(), (Object[]) null);
						if (result instanceof String[]){
							((ComboViewer)control).setInput((String[])result);
						}
					} catch (SecurityException e) {
						logger.error("Problem populating combo for " + property, e);
					} catch (NoSuchMethodException e) {
						logger.error("Problem populating combo for " + property, e);
					} catch (IllegalArgumentException e) {
						logger.error("Problem populating combo for " + property, e);
					} catch (IllegalAccessException e) {
						logger.error("Problem populating combo for " + property, e);
					} catch (InvocationTargetException e) {
						logger.error("Problem populating combo for " + property, e);
					}

				}
				boolean worked = bind(property, control);

				if (!worked){
					logger.error(property + " did not bind");
					setBound(false);
					return false;
				}
			}
			setBound(true);
			return true;
		}

		return false;
	}

	/**
	 * this is for use with dynamic widgets where  the selection provider changes content of widget without rebinding.
	 * @param model
	 */
	@Override
	public void handleChildUpdates(Object model){

		if (!isBound() || m_propertyControlMap.isEmpty() || getRealm() == null){
			return;
		}

		for (String property : m_propertyControlMap.keySet()){
			Object control = m_propertyControlMap.get(property);
			if (control instanceof AbstractDatabindingWidget){
				AbstractDatabindingWidget adw = (AbstractDatabindingWidget)control;
				adw.unbindUI();
				adw.setModel(model);
			}

		}
	}

	/**
	 * Use either the given label or name.
	 * @param fieldInfo
	 * @return
	 */
	protected String findLabel(FieldCustomization fieldInfo){
		String label = fieldInfo.getLabel();
		if (label == null || label.length() == 0){
			label = fieldInfo.getName();
		}
		return label;
	}

	/**
	 * Append a colon if necessary, and capitalize
	 * @param label
	 * @return
	 */
	protected String prepareLabel(String label){
		if (label == null){
			return "";
		}
		if (!label.endsWith(":")){
			label += ":";
		}
		return StringUtil.upperFirstChar(label, false);
	}

	/* 
	 * This currently is only supporting conversion; we are assuming if the model has the value already then it is a valid value
	 * (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget#getModelToTargetStrategy(java.lang.String)
	 */
	@Override
	protected UpdateValueStrategy getModelToTargetStrategy(final String feature) {
		FieldCustomization wfi = this.m_widgetFieldInfoMap.get(feature);
		if (wfi == null){
			return super.getModelToTargetStrategy(feature);
		}
		final String format = wfi.getFormat();
		final ISimpleConverter converter = wfi.getConverter();
		if (converter == null && format == null){
			return super.getModelToTargetStrategy(feature);
		}
		UpdateValueStrategy result = new UpdateValueStrategy();
		result.setConverter(new IConverter() {

			public Object convert(Object fromObject) {
				if (getToType().equals(String.class) && format != null){
					if (converter != null){
						Object result = converter.toTarget(fromObject);
						return String.format(format, result);
					}
					return String.format(format, fromObject);
				}
				return (converter != null) ? converter.toTarget(fromObject) : null;
			}

			public Object getFromType() {
				if (converter != null){
					return converter.getModelType();
				}
				try {
					return MethodUtil.getReturnType(m_class, feature);
				} catch (SecurityException e) {
					//logger.warn(e);
				} catch (NoSuchMethodException e) {
					//logger.warn(e);
				}
				return Object.class;

			}

			public Object getToType() {
				if (converter != null){
					return converter.getTargetType();
				}
				return String.class;
			}

		});

		return result;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget#getTargetToModelStrategy(java.lang.String)
	 */
	@Override
	protected UpdateValueStrategy getTargetToModelStrategy(String feature) {
		FieldCustomization wfi = this.m_widgetFieldInfoMap.get(feature);
		if (wfi == null){
			return super.getTargetToModelStrategy(feature);
		}
		UpdateValueStrategy result = new UpdateValueStrategy();

		final ISimpleValidator validator = wfi.getValidator();
		final ISimpleConverter converter = wfi.getConverter();

		if (validator == null && converter == null){
			return super.getTargetToModelStrategy(feature);
		}

		if (validator != null){
			result.setAfterGetValidator(new IValidator() {

				public IStatus validate(Object value) {
					ISimpleStatus ss = validator.validate(value);
					if (ss == null){
						return Status.OK_STATUS;
					}
					if (ss.getException() != null){
						logger.error("Using Broken Functionality - class name instead of plugin id");
						return new Status(ss.getSeverity(), this.getClass().getName(), ss.getMessage(), ss.getException());
						//return new Status(ss.getSeverity(), EnsembleUIDatabindingActivator.PLUGIN_ID, ss.getMessage(), ss.getException());
					}
					logger.error("Using Broken Functionality - class name instead of plugin id");
					return new Status(ss.getSeverity(), this.getClass().getName(), ss.getMessage());
					//return new Status(ss.getSeverity(), EnsembleUIDatabindingActivator.PLUGIN_ID, ss.getMessage());
				}

			});
		}
		if (converter != null){
			result.setConverter(new IConverter() {

				public Object convert(Object fromObject) {
					return converter.toModel(fromObject);
				}

				public Object getFromType() {
					return converter.getTargetType();
				}

				public Object getToType() {
					return converter.getModelType();
				}

			});
		}

		return result;
	}

	/**
	 * Set the tool tip text on a control.
	 * @param control
	 * @param description
	 */
	protected void setToolTipText(Control control, String description){
		if (control == null || description == null || description.length() == 0){
			return;
		}
		control.setToolTipText(description);
	}

	/**
	 * Construct the control
	 * @param container
	 * @param controlClass
	 * @param swtOptions
	 * @param text
	 * @return
	 */
	protected Control createControl(Composite container, WidgetType wtype, Class controlClass, int swtOptions, String text){
		if (wtype != null){
			switch (wtype){
			case LABEL:
				Label label = new Label(container, swtOptions);
				if (text != null){
					label.setText(text);
					label.setToolTipText(text);
				}
				return label;
			case TEXT:
				Text control = new Text(container, swtOptions);
				if (text != null){
					control.setText(text);
				}
				return control;
			case COLORSELECTOR:
				ColorSelector colorSelector = new ColorSelector(container);
				return colorSelector.getButton();
			case SYSTEM_COLOR:
				ComboViewer comboViewer = new ComboViewer(container, SWT.READ_ONLY);
				comboViewer.setContentProvider(ArrayContentProvider.getInstance());
				Set<String> cnames = SystemColorProvider.getColorNames();
				comboViewer.setInput(cnames.toArray(new String[cnames.size()]));
				comboViewer.getControl().setLayoutData(getLeftComboData());
				return comboViewer.getControl();
			case BUTTON:
				Button button = new Button(container, swtOptions);
				return button;
			case COMBO:
			case SLIDER:
			case SPINNER:
			case RADIO:
				throw new IllegalArgumentException("Unsupported widget type: "+wtype);
			}
		}
		if (controlClass.equals(Label.class)){
			Label label = new Label(container, swtOptions);
			if (text != null){
				label.setText(text);
				label.setToolTipText(text);
			}
			return label;
		} else if (controlClass.equals(Text.class)){
			Text control = new Text(container, swtOptions);
			if (text != null){
				control.setText(text);
			}
			return control;
		} else if (controlClass.equals(Button.class)){
			Button button = new Button(container, swtOptions);
			if (text != null){
				button.setText(text);
			}
			return button;
		} else if (controlClass.equals(Spinner.class)){
			//TODO not setting value
			Spinner spinner = new Spinner(container, swtOptions);
			return spinner;
		}
		return null;
	}

	/**
	 * This attempts to create a customization from annotations.
	 * If it fails it will generically create the widget.
	 * @param container
	 */
	public void createControls(Composite container){
		if (container == null){
			return;
		}

		ICustomization customization = AnnotationReader.createCustomization(m_class);
		if (customization != null){
			createControls(container, customization);
			return;
		}

		createMasterLayout();
		setLayout(m_masterColumnLayout);

		// just use fields
		for (Method method : m_class.getMethods()){
			if (MethodUtil.isGetMethod(method) || MethodUtil.isIsMethod(method)){
				createField(method, container);
			}
		}
	}

	/**
	 * Construct and set up a label
	 * @param container
	 * @param fieldInfo
	 */
	protected Label createLabel(Composite container, FieldCustomization fieldInfo, String value) {
		if (value == null){
			value = prepareLabel(findLabel(fieldInfo));
		}
		Label label  = (Label)createControl(container, null, Label.class, SWT.TRAIL, value);
		label.setLayoutData(getRightData());
		addChildLabel(label);

		if (fieldInfo != null){
			String descrip = fieldInfo.getDescription();
			if(descrip != null && descrip.length() > 0)
				setToolTipText(label, descrip);
			else 
				setToolTipText(label, value);
			if (fieldInfo.isAdvanced()){
				m_advancedControls.add(label);
				label.setVisible(isAdvancedVisible());
			}
		}
		else {
			setToolTipText(label, value);
		}

		return label;
	}

	AbstractDatabindingWidget setupFieldWidget(AbstractDatabindingWidget registeredWidget, String fieldKey) {
		if(registeredWidget != null) {
			if (registeredWidget instanceof AbstractFieldWidget){
				((AbstractFieldWidget)registeredWidget).setFeature(fieldKey);
			}
			addChildControl(registeredWidget);
			m_propertyControlMap.put(fieldKey, registeredWidget);
		}
		return registeredWidget;
	}

	protected Button createComplexButton(final Composite container, String text, final Method method, final Class returnType, String fieldKey){
		Button button = null;
		if (getToolkit() != null){
			button = getToolkit().createButton(container, text, SWT.PUSH);
		} else {
			button = new Button(container, SWT.PUSH);
			button.setText(text);
		}
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getModel() == null){
					return;
				}
				try {
					Object childModel = method.invoke(getModel(),  new Object[] {});
					ComplexFieldDialog cfd = new ComplexFieldDialog(container.getShell(), childModel);
					cfd.open();
				} catch (IllegalArgumentException e1) {
					logger.error(e1);
				} catch (IllegalAccessException e1) {
					logger.error(e1);
				} catch (InvocationTargetException e1) {
					logger.error(e1);
				} 


			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				//NOOP
			}

		});
		return button;
	}

	/**
	 * Construct and set up a registered field widget
	 * @param container
	 * @param returnType
	 * @param fieldKey
	 * @return true if one was found, false otherwise
	 */
	protected AbstractDatabindingWidget createRegisteredFieldWidget(Composite container, Class returnType, String fieldKey) {
		AbstractDatabindingWidget registeredWidget;
		try {
			if (getToolkit() != null){
				registeredWidget = BoundWidgetFactory.getRegisteredFieldWidget(returnType, getToolkit(), container, SWT.NONE, null, false);
			} else {
				registeredWidget = BoundWidgetFactory.getRegisteredFieldWidget(returnType, container, SWT.NONE, null, false);
			}
			return setupFieldWidget(registeredWidget, fieldKey);
		} 
		catch (Throwable t) {
			logger.warn(t);
		} 
		return null;
	}

	protected Control createButton(Composite container, FieldCustomization fieldInfo, String method){
		WidgetType wt = WidgetType.BUTTON;
		String name = MethodUtil.getTriggerSuffix(method);
		if (fieldInfo != null){
			wt = fieldInfo.getWidgetType();
			if (fieldInfo.getLabel() != null){
				name = fieldInfo.getLabel();
			}
		}
		Button button = (Button)createControl(container, wt, Button.class, SWT.PUSH, null);
		button.setText(name);
		button.setLayoutData(getLeftData());
		addChildControl(button);
		m_propertyControlMap.put(method, button);
		if (fieldInfo != null){
			if (fieldInfo.isAdvanced()){
				m_advancedControls.add(button);
				button.setVisible(isAdvancedVisible());
			}
			button.setEnabled(!fieldInfo.isReadOnly());
		}

		return button;
	}

	/**
	 * Construct and set up a text box
	 * @param container
	 * @param fieldInfo
	 * @param fieldKey
	 */
	protected Control createText(Composite container, FieldCustomization fieldInfo, String fieldKey) {
		WidgetType wt = WidgetType.TEXT;
		if (fieldInfo != null){
			wt = fieldInfo.getWidgetType();
		}
		Control text = createControl(container, wt, Text.class, SWT.BORDER, null); 
		text.setLayoutData(getLeftData());
		addChildControl(text);
		m_propertyControlMap.put(fieldKey, text);
		if (fieldInfo != null){
			if (fieldInfo.isAdvanced()){
				m_advancedControls.add(text);
				text.setVisible(isAdvancedVisible());
			}
			text.setEnabled(!fieldInfo.isReadOnly());
		}

		return text;
	}


	/**
	 * Construct and set up a combo.  This is the default for an enumeration
	 * @param container
	 * @param fieldInfo
	 * @param returnType
	 * @param fieldKey
	 */
	protected ComboViewer createCombo(Composite container, final FieldCustomization fieldInfo, Class returnType, String fieldKey){
		final ComboViewer comboViewer = new ComboViewer(container, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		if (fieldInfo != null && fieldInfo.getComboEntries() != null){
			comboViewer.setInput(fieldInfo.getComboEntries().toArray());
		} else if (fieldInfo != null && fieldInfo.getSimpleComboEntries() != null){
			comboViewer.setInput(fieldInfo.getSimpleComboEntries().toArray());
		} else if (fieldInfo != null && fieldInfo.getComboEntryMethodName() != null){
			m_comboMethodMap.put(fieldKey, fieldInfo.getComboEntryMethodName());
		} else {
			Object[] values = returnType.getEnumConstants();
			comboViewer.setInput(values);
		}
		comboViewer.getControl().setLayoutData(getLeftComboData());
		addChildControl(comboViewer.getControl());
		m_propertyControlMap.put(fieldKey, comboViewer);
		if (fieldInfo != null){
			if (fieldInfo.isAdvanced()){
				m_advancedControls.add(comboViewer.getControl());
				comboViewer.getControl().setVisible(isAdvancedVisible());
			}
			comboViewer.getCombo().setEnabled(!fieldInfo.isReadOnly());
		}

		return comboViewer;
	}

	/**
	 * Construct and set up a combo for system colors
	 * @param container
	 * @param fieldInfo
	 * @param fieldKey
	 */
	protected ComboViewer createSystemColorCombo(Composite container, FieldCustomization fieldInfo, String fieldKey){
		ComboViewer comboViewer = new ComboViewer(container, SWT.READ_ONLY);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		Set<String> cnames = SystemColorProvider.getColorNames();
		Object[] contents = cnames.toArray();
		comboViewer.setInput(contents);
		comboViewer.getControl().setLayoutData(getLeftComboData());
		addChildControl(comboViewer.getControl());
		m_propertyControlMap.put(fieldKey, comboViewer);
		if (fieldInfo != null){
			if (fieldInfo.isAdvanced()){
				m_advancedControls.add(comboViewer.getControl());
				comboViewer.getControl().setVisible(isAdvancedVisible());
			}
			comboViewer.getCombo().setEnabled(!fieldInfo.isReadOnly());
		}

		return comboViewer;
	}

	/**
	 * Construct and set up a color selector button
	 * @param container
	 * @param fieldInfo
	 * @param fieldKey
	 */
	protected ColorSelector createColorSelector(Composite container, FieldCustomization fieldInfo, String fieldKey) {
		ColorSelector colorSelector = new ColorSelector(container);
		addChildControl(colorSelector.getButton());
		m_propertyControlMap.put(fieldKey, colorSelector);
		if (fieldInfo != null){
			if (fieldInfo.isAdvanced()){
				m_advancedControls.add(colorSelector.getButton());
				colorSelector.getButton().setVisible(isAdvancedVisible());
			}
			colorSelector.setEnabled(!fieldInfo.isReadOnly());
		}

		return colorSelector;
	}

	/**
	 * Construct and set up a checkbox for a boolean field
	 * @param container
	 * @param fieldInfo
	 * @param fieldKey
	 */
	protected Button createCheckbox(Composite container, FieldCustomization fieldInfo, String fieldKey ) {
		WidgetType widgetType = null;
		if (fieldInfo != null){
			widgetType = fieldInfo.getWidgetType();
		}
		final String text = (fieldInfo==null) ? fieldKey : StringUtil.upperFirstChar(findLabel(fieldInfo), false);
		Button button = (Button)createControl(container, widgetType, Button.class, SWT.CHECK, text);
		GridData gridData = new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
		button.setLayoutData(gridData);
		if (fieldInfo != null){
			setToolTipText(button, fieldInfo.getDescription());
		}
		addChildControl(button);
		m_propertyControlMap.put(fieldKey, button);
		if (fieldInfo != null){
			if (fieldInfo.isAdvanced()){
				m_advancedControls.add(button);
				button.setVisible(isAdvancedVisible());
			}
			button.setEnabled(!fieldInfo.isReadOnly());
		}
		return button;
	}
	
	protected Control createNoFieldsToEditLabel(Composite container){
		Control result = null;
		if (container == null){
			return result;
		}

		try {
			Label text = new Label(container, SWT.None);
			text.setText("This command has no parameters to edit");
			GridData nameGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
			nameGridData.horizontalSpan = 3;
			text.setLayoutData(nameGridData);

			addChildControl(text);
			result = text;
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Create Text that looks like a Label with the name of the Command in it
	 * @param container
	 */
	protected Control createNameOnlyHeaderField(Composite container){
		Control result = null;
		if (container == null){
			return result;
		}

		String fieldKey = "name";
		m_widgetFieldInfoMap.put(fieldKey, null);

		try {
			Text text = new Text(container, SWT.None);
			text.setText("Name field");
			GridData nameGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
			nameGridData.horizontalSpan = 3;
			text.setLayoutData(nameGridData);
			text.setEditable(false);
			LocalResourceManager resManager = 
					  new LocalResourceManager(JFaceResources.getResources(), this);

			FontDescriptor bigDescriptor = FontDescriptor.createFrom(text.getFont()).setHeight(14);
			Font bigFont = resManager.createFont(bigDescriptor);
			text.setFont( bigFont );
			text.setBackground(ColorProvider.INSTANCE.WIDGET_BACKGROUND);

			addChildControl(text);
			m_propertyControlMap.put(fieldKey, text);
			result = text;
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Create the given field described by the field info (for customization)
	 * @param fieldInfo
	 * @param container
	 */
	protected Control createField(FieldCustomization fieldInfo, Composite container, boolean skipLabel){

		Control result = null;
		if (fieldInfo == null || container == null){
			return result;
		}

		String suffix = fieldInfo.getName();
		String fieldKey = StringUtil.lowerFirstChar(suffix, false);
		m_widgetFieldInfoMap.put(fieldKey, fieldInfo);

		try {
			Method getMethod = MethodUtil.getGetMethod(m_class,suffix);
			Class returnType = getMethod.getReturnType();
			Method setMethod = MethodUtil.getSetMethod(m_class, suffix, returnType);
			if (setMethod != null) {

				boolean complex = false;
				if (!MethodUtil.isReturnTypeSimple(returnType)) {
					if (fieldInfo.isComplex()){
						complex = true;
						// can get and set, go forward
						if (!returnType.equals(Boolean.TYPE) && !skipLabel){
							createLabel(container, fieldInfo, null);
						}
					}
				} else {
					// can get and set, go forward
					if (!returnType.equals(Boolean.TYPE) && !skipLabel){
						createLabel(container, fieldInfo, null);
					}
				}

				AbstractDatabindingWidget registeredFieldWidget = createRegisteredFieldWidget(container, returnType, fieldKey);
				result = registeredFieldWidget;
				if (registeredFieldWidget == null){
					if (!MethodUtil.isReturnTypeSimple(returnType)) {
						if (fieldInfo.isComplex()){
							Button but = createComplexButton(container, "Edit", getMethod, returnType, fieldKey);
						} else {
							return null;
						}
					} else {
						//ENUM
						if (returnType.isEnum()){
							if(fieldInfo.getWidgetType() != null){
								switch (fieldInfo.getWidgetType()){
								case TEXT:
								case LABEL:
									result = createText(container, fieldInfo, fieldKey);
									break;
								case COMBO:
									ComboViewer cv = createCombo(container, fieldInfo, returnType, fieldKey);
									result = cv.getControl();
									break;
								default:
									throw new IllegalArgumentException("Unsupported widget type: "+fieldInfo.getWidgetType());
								}
							} else {
								// make a combo DEFAULT FOR ENUM

								ComboViewer cv = createCombo(container, fieldInfo, returnType, fieldKey);
								result = cv.getControl();
							}
						} else {
							// NOT ENUM
							if(fieldInfo.getWidgetType() != null) {
								//CUSTOM WIDGET
								if (fieldInfo.getWidgetType().equals(WidgetType.SYSTEM_COLOR)){
									ComboViewer cv = createSystemColorCombo(container, fieldInfo, fieldKey);
									result = cv.getControl();
								} else if (fieldInfo.getWidgetType().equals(WidgetType.COLORSELECTOR)){
									ColorSelector cs = createColorSelector(container, fieldInfo, fieldKey);
									result = cs.getButton();
								} else if (fieldInfo.getWidgetType().equals(WidgetType.COMBO)){
									ComboViewer cv = createCombo(container, fieldInfo, returnType, fieldKey);
									result = cv.getControl();
								}
							} else {
								// see if it's a boolean
								if (returnType.equals(Boolean.TYPE)){
									// spacer for label
									Label label2 = new Label(container, SWT.TRAIL);
									if (fieldInfo.isAdvanced()){
										m_advancedControls.add(label2);
										label2.setVisible(isAdvancedVisible());
									}
									result = createCheckbox(container, fieldInfo, fieldKey);
								} else {
									result = createText(container, fieldInfo, fieldKey);
								}
							}
						}
					}
				}

				Label unitsLabel = (Label)createControl(container, null, Label.class, SWT.NONE, fieldInfo.getUnitsLabel());
				addChildLabel(unitsLabel);
				if (fieldInfo.isAdvanced()){
					m_advancedControls.add(unitsLabel);
					unitsLabel.setVisible(isAdvancedVisible());
				}

				return result;
			}
		} catch (SecurityException e) {
			//logger.warn(e);
		} catch (NoSuchMethodException e) {
			// check for is for boolean
			try {
				Method isMethod = MethodUtil.getIsMethod(m_class, suffix);
				Method setMethod = MethodUtil.getSetMethod(m_class, suffix, isMethod.getReturnType());
				if (setMethod != null) {
					// spacer for label
					Label label = new Label(container, SWT.TRAIL);
					if (fieldInfo.isAdvanced()){
						m_advancedControls.add(label);
						label.setVisible(isAdvancedVisible());
					}

					result = createCheckbox(container, fieldInfo, fieldKey);

					// spacer for units
					label = new Label(container, SWT.TRAIL);
					if (fieldInfo.isAdvanced()){
						m_advancedControls.add(label);
						label.setVisible(isAdvancedVisible());
					}
				} 
			} catch (SecurityException ex) {
				//logger.warn(ex);
			} catch (NoSuchMethodException ex) {
				//logger.warn(ex);

				try { 
					// check for trigger
					Method triggerMethod = MethodUtil.getMethod(m_class, suffix);

					if (triggerMethod != null){
						// spacer for label
						Label label = new Label(container, SWT.TRAIL);
						if (fieldInfo.isAdvanced()){
							m_advancedControls.add(label);
							label.setVisible(isAdvancedVisible());
						}

						result = createButton(container, fieldInfo, fieldKey);

						// spacer for units
						label = new Label(container, SWT.TRAIL);
						if (fieldInfo.isAdvanced()){
							m_advancedControls.add(label);
							label.setVisible(isAdvancedVisible());
						}
					}
				}catch (SecurityException ex1) {
					//logger.warn(ex);
				} catch (NoSuchMethodException ex1) {

				}
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Go through and create the controls when there is no ICustomization defined
	 * @param container
	 */
	public boolean createField(Method method, Composite container) {
		// TODO expand for lists
		AbstractDatabindingWidget registeredFieldWidget = null;
		boolean hasRegisteredWidget = false;
		boolean complex = false;
		if (MethodUtil.isGetMethod(method)) {
			String suffix = MethodUtil.getSuffix(method);
			Class returnType = method.getReturnType();
			if (!MethodUtil.isReturnTypeSimple(returnType)){
				// mallan 1/11/2012: Changed behavior from failing if not simple return type 
				// to checking the BoundWidgetFactory to see if there is an appropriate widget registered
				hasRegisteredWidget = BoundWidgetFactory.hasRegisteredFieldWidget(returnType, null);
				if(!hasRegisteredWidget) {
					if (AnnotationReader.isComplex(method) || MethodUtil.hasGetterSetter(returnType)){
						complex = true;
					}
				}
			}
			String fieldKey =StringUtil.lowerFirstChar(suffix, false);
			try {
				Method setMethod = MethodUtil.getSetMethod(m_class, suffix, returnType);
				if (setMethod != null) {
					// can get and set, go forward
					if (!returnType.equals(Boolean.TYPE)){
						String labelText = AnnotationReader.getFieldLabel(method);
						if (labelText == null) {
							labelText = StringUtil.upperFirstChar(suffix, false);
						}
						createLabel(container, null, labelText);
					}

					boolean created = false;
					if(registeredFieldWidget == null) {
						if (complex){
							Button but = createComplexButton(container, "Edit", method, returnType, fieldKey);
							created = but != null;
						} else {
							registeredFieldWidget = createRegisteredFieldWidget(container, returnType, fieldKey);
							created = registeredFieldWidget != null;
						}
					}
					if (!created){
						if (returnType.isEnum()) {
							// make a combo
							createCombo(container, null, returnType, fieldKey);
						} // see if it's a boolean
						else if (returnType.equals(Boolean.TYPE)){
							// spacer for label
							Label spacer = new Label(container, SWT.TRAIL);
							addChildLabel(spacer);
							createCheckbox(container, null, fieldKey);
						} else {
							createText(container, null, fieldKey);
						}
					}

					// spacer for units
					String unitsText = AnnotationReader.getUnitsLabel(method);
					Label unitsLabel = (Label) createControl(container, null, Label.class, SWT.NONE, unitsText);
					addChildLabel(unitsLabel);
					return true;

				}
			} catch (SecurityException e) {
				logger.warn(e);
			} catch (NoSuchMethodException e) {
				//logger.warn(e);
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (MethodUtil.isIsMethod(method)) {
			String suffix = MethodUtil.getSuffix(method);
			try {
				Method setMethod = MethodUtil.getSetMethod(m_class, suffix, method.getReturnType());
				if (setMethod != null) {
					// can get and set, go forward
					// spacer for label
					Label spacer = new Label(container, SWT.TRAIL);
					addChildLabel(spacer);

					String fieldKey = StringUtil.lowerFirstChar(suffix, false);
					createCheckbox(container, new FieldCustomization(suffix), fieldKey);

					// spacer for units
					Label unitsLabel = new Label(container, SWT.NONE);
					addChildLabel(unitsLabel);
					return true;
				}
			} catch (SecurityException e) {
				//logger.warn(e);
			} catch (NoSuchMethodException e) {
				//logger.warn(e);
			}
		} else if (MethodUtil.isTriggerMethod(method)){
			// spacer for label
			Label spacer = new Label(container, SWT.TRAIL);
			addChildLabel(spacer);

			String suffix = MethodUtil.getTriggerSuffix(method.getName());
			String fieldKey = StringUtil.lowerFirstChar(suffix, false);
			createButton(container, new FieldCustomization(method.getName(), suffix, ""), method.getName());

			// spacer for units
			Label unitsLabel = new Label(container, SWT.NONE);
			addChildLabel(unitsLabel);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget#isAdvancedVisible()
	 */
	@Override
	public boolean isAdvancedVisible() {
		return m_advancedVisible;
	}

	/* 
	 * 
	 * (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget#setAdvancedVisible(boolean)
	 */
	//TODO this leaves holes.  unsightly holes.
	@Override
	public void setAdvancedVisible(boolean advancedVisible) {
		super.setAdvancedVisible(advancedVisible);
		for (Control control : m_advancedControls){
			control.setVisible(advancedVisible);
		}

		redraw();
		pack(true);
		layout(true, true);

		Composite parent = this.getParent();
		parent.redraw();
		parent.pack(true);
		parent.layout(true, true);
	}


	/**
	 * The toolkit will not be null for a form widget.
	 * @return
	 */
	public FormToolkit getToolkit() {
		return null;
	}

}
