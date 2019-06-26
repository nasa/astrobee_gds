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

import gov.nasa.ensemble.ui.databinding.databinding.IDatabindingProvider;
import gov.nasa.ensemble.ui.databinding.databinding.UndoablePOJOSetOperation;
import gov.nasa.ensemble.ui.databinding.status.IStatusListener;
import gov.nasa.ensemble.ui.databinding.util.MethodUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IObserving;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


/**
 * Generic abstract databinding widget; models that use this widget should follow JavaBean patterns (ie get/set)
 *  
 * @author tecohen
 *
 */
//TODO attach a multivalidator to the databinding context for non emf validation

public abstract class AbstractDatabindingWidget extends Composite implements
		IDatabindingProvider {
	
	Logger logger = Logger.getLogger(AbstractDatabindingWidget.class);

	public static final int DELAY = 800;	// how long to delay after text entry before starting the validation / data update process

	protected Object m_model; // the model you are editing
	
	protected DataBindingContext m_dataBindingContext;	
	
	protected List<Control> m_controls = new ArrayList<Control>();	// list of child controls
	protected List<Label> m_labels = new ArrayList<Label>();		// list of child labels
	protected List<Label> m_units = new ArrayList<Label>();			// list of unit labels
	protected HashMap<ColorSelector, IPropertyChangeListener> m_colorSelectors = new HashMap<ColorSelector, IPropertyChangeListener>();	// map of color selectors to listeners
	
	protected Map<Control, ControlDecoration> m_decorations = new HashMap<Control, ControlDecoration>(); // these link together error decorators with controls
	protected Map<Control, IStatus> m_status = new HashMap<Control, IStatus>(); // these link together error status with controls; status NOT from EMF validation
	protected ArrayList<IStatusListener> m_statusListeners = new ArrayList<IStatusListener>(); // registered value change listeners from outside this widget.
	protected final IUndoContext m_undoContext = IOperationHistory.GLOBAL_UNDO_CONTEXT;

	private boolean m_bound = false;
	
	protected int m_labelWidth = 100;	// default width for labels
	protected int m_fieldWidth = 80;	// default width for fields
	
	// support for toggling advanced controls
	protected boolean m_advancedVisible = false;
	
	// layout support
	protected GridData m_rightData;
	protected GridData m_leftData;
	protected GridData m_leftComboData;
	
	protected int m_numColumns = 3; // label, field, units
	protected Layout m_masterColumnLayout; // main layout for this widget
	
	protected Realm m_realm; // the databinding realm
	
	protected IObservableValue m_selectionObservableValue;  // if this is being driven by an external selection observable.
	protected IObservableValue m_parentSelectionObservableValue; // if this is a parent-child-detail, set the parent's selection observable value here.
	
	protected HashMap<Button, SelectionListener> m_selectionListenerMap = new HashMap<Button, SelectionListener>();
	
	/**
	 * Constructor
	 * @param parent
	 * @param style
	 */
	public AbstractDatabindingWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * Constructor
	 * @param parent
	 * @param style
	 */
	public AbstractDatabindingWidget(Composite parent, int style, boolean horizontal) {
		super(parent, style);
	}
	
	/**
	 * Add a label to the list of child labels
	 * @param label
	 */
	protected void addChildLabel(Label label){
		m_labels.add(label);
	}
	
	/**
	 * Remove a label from the list of child labels
	 * @param label
	 */
	protected void removeChildLabel(Label label){
		m_labels.remove(label);
	}

	/**
	 * Add a label to the list of child labels for units
	 * @param label
	 */
	protected void addChildUnitLabel(Label label){
		m_units.add(label);
	}
	
	/**
	 * Remove a label from the list of child labels for units
	 * @param label
	 */
	protected void removeChildUnitLabel(Label label){
		m_units.remove(label);
	}

	/**
	 * Add a control to the list of child controls
	 * @param control
	 */
	protected void addChildControl(Control control){
		m_controls.add(control);
	}
	
	/**
	 * Remove a control from the list of child controls
	 * @param control
	 */
	protected void removeChildControl(Control control){
		m_controls.remove(control);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (Control control : m_controls) {
			control.setEnabled(enabled);
		}
		for (Label label : m_labels) {
			label.setEnabled(enabled);
		}
		for (Label label : m_units) {
			label.setEnabled(enabled);
		}
	}
	
	/**
	 * @return the data binding context (may be null
	 */
	public DataBindingContext getDataBindingContext() {
		return m_dataBindingContext;
	}

	/**
	 * Set the data binding context
	 * @param dbc (the context)
	 */
	public void setDataBindingContext(DataBindingContext dbc) {
		m_dataBindingContext = dbc;
	}

	/**
	 * @return the EObject being edited.  May be null.
	 */
	public Object getModel(){
		return m_model;
	}
	
	/**
	 * Set the model you plan to edit.
	 * This should be set after the UI exists, ideally.
	 * @param obj
	 */
	public void setModel(final Object obj){
		if (m_model != null && m_model.equals(obj)){
			return;
		}
		//unbindUI();
		m_model = obj;
		bindUI(getRealm());
	}
	
	public void unbindUI(){
		DataBindingContext dbc = getDataBindingContext();
		if (dbc != null){
			IObservableList observableList = dbc.getBindings();
			Object[] contents = observableList.toArray();
			for (Object bObject : contents){
				if (bObject instanceof Binding){
					Binding binding = (Binding)bObject;
					dbc.removeBinding(binding);
					binding.dispose();
				}
			}
			
			// unbind children
			for (Control control : getChildren()) {
				if (control instanceof AbstractDatabindingWidget){
					((AbstractDatabindingWidget)control).unbindUI();
				}
			}
			
			for (ColorSelector cs : m_colorSelectors.keySet()){
				cs.removeListener(m_colorSelectors.get(cs));
			}
			m_colorSelectors.clear();
		}
		m_bound = false;
	}
	
	/**
	 * Actually do the binding of the structural elements to the UI widgets
	 * @param realm
	 */
	public abstract boolean bindUI(Realm realm);
	
	public void handleChildUpdates(Object model){
		//noop
	}

	/**
	 * Set the realm
	 * @param realm
	 */
	public void setRealm(Realm realm){
		m_realm = realm;
	}
	
	/**
	 * @return the default realm by default.
	 */
	public Realm getRealm() {
		if (m_realm == null){
			m_realm = Realm.getDefault();
		}
		return m_realm;
	}

	/**
	 * Retrieves or creates a decoration for the given control
	 * Default has the error icon
	 * @param control
	 * @return
	 */
	protected ControlDecoration getControlDecoration(Control control) {
		ControlDecoration decoration = m_decorations.get(control);
		if (decoration == null){
			decoration = new ControlDecoration(control,	SWT.LEFT | SWT.TOP);
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
			decoration.setImage(fieldDecoration.getImage());
			decoration.hide();
			m_decorations.put(control, decoration);
		}
		return decoration;
	}
	
	/**
	 * Prepare the given feature for binding
	 * 
	 * @param feature
	 * @return null if anything goes wrong; else returns the observable value
	 */
	protected IObservableValue prepBinding(String feature){
		if (m_dataBindingContext == null){
			m_dataBindingContext = new DataBindingContext();
		}
		
		/*
		if (m_undoableOperation == null){
			m_undoableOperation = new UndoableDatabindingOperation(m_dataBindingContext, "Edit "+feature);
		} */
		
		if (getModel() == null){
			return null;
		}
		
		if (feature == null){
			return null;
		}
		
		IObservableValue observable;
		IObservableValue selection = getParentSelectionObservableValue();
		if (selection == null) {
			selection = getSelectionObservableValue();
		}
		if (selection != null){
			try {
				@SuppressWarnings("rawtypes")
				Class returnType = MethodUtil.getReturnType(getModel().getClass(), feature);
				if (returnType == int.class){
					returnType = Integer.TYPE;
				}
				observable =  getSelectionObservableValue(selection, feature, returnType);
				//observable.addValueChangeListener(new UndoableValueChangedListener(feature));
				return observable;
			} catch (SecurityException e) {
				// do nothing
			} catch (NoSuchMethodException e) {
				// do nothing
			} 
		} else {
			observable =  getObservableValue(feature);
			//observable.addValueChangeListener(new UndoableValueChangedListener(feature));
			return observable;
		}
		return null;
	}
	
	/**
	 * Get the observable value given a master-detail scenario with a selection provider.
	 * @param selection
	 * @param feature
	 * @param returnType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected IObservableValue getSelectionObservableValue(IObservableValue selection, String feature, Class returnType){
		if (selection.getValueType() instanceof Class) {
			Class modelClass = (Class) selection.getValueType();
			if (MethodUtil.isBean(modelClass)) {
				return BeansObservables.observeDetailValue(selection, feature, returnType);
			}
		}

		return PojoObservables.observeDetailValue(selection, feature, returnType);
	}
	
	
	/**
	 * Get the observable value for a straight up binding, not master-detail.
	 * @param feature
	 * @return
	 */
	protected IObservableValue getObservableValue(String feature){
		Object model = getModel();
		if (MethodUtil.isBean(model.getClass())) {
			return BeansObservables.observeValue(getRealm(), getModel(), feature);
		} else {
			// try pojo
			return PojoObservables.observeValue(getRealm(), getModel(), feature);
		}
	}
	
	public void setSelectionObservableValue(IObservableValue selectionObservableValue) {
		m_selectionObservableValue = selectionObservableValue;
	}
	
	/**
	 * If this widgets contents are informed via some selection, use this to return the selection observable value.
	 * for example, ViewersObservables.observeSingleSelection(viewer)
	 * @return
	 */
	public IObservableValue getSelectionObservableValue() {
		return m_selectionObservableValue;
	}
	
	public IObservableValue getParentSelectionObservableValue() {
		return m_parentSelectionObservableValue;
	}

	public void setParentSelectionObservableValue(
			IObservableValue parentSelectionObservableValue) {
		m_parentSelectionObservableValue = parentSelectionObservableValue;
	}

	/**
	 * Retrieve a custom update value strategy to use when setting the contents of the model from the target (ui)
	 * @param feature
	 * @return
	 */
	protected UpdateValueStrategy getTargetToModelStrategy(String feature){
		return null;
	}
	
	/**
	 * Retrieve a custom update value strategy to use when setting the contents of the target (ui) from the model
	 * @param feature
	 * @return
	 */
	protected UpdateValueStrategy getModelToTargetStrategy(String feature){
		return null;
	}

	/**
	 * Generic binding method; look up correct method.
	 * Important: when adding types of controls to binding support they must be added here.
	 * 
	 * @param feature
	 * @param control
	 * @return
	 */
	protected boolean bind(String feature, Object control){
		if (control instanceof Text){
			return bind(feature, (Text)control);
		} else if (control instanceof Button){
			if ((((Button) control).getStyle() & SWT.PUSH) == SWT.PUSH){
				return hookTriggerButton(feature, (Button)control);
			}
			return bind(feature, (Button)control);
		} else if (control instanceof Combo){
			return bind(feature, (Combo)control);
		} else if (control instanceof Spinner){
			return bind(feature, (Spinner)control);
		} else if (control instanceof ComboViewer){
			return bind(feature, (ComboViewer)control);
		} else if (control instanceof ColorSelector){
			return bind(feature, (ColorSelector)control);
		} else if (control instanceof AbstractFieldWidget){
			try {
				AbstractFieldWidget afw = (AbstractFieldWidget)control;
				Object model = getModel();
				Method getMethod = MethodUtil.getGetMethod(model.getClass(), feature);
				if (getMethod != null){
					afw.setModel(model);
					return afw.isBound();
				}
				} catch (NoSuchMethodException e){
					logger.error(e);
				} catch (IllegalArgumentException e) {
					logger.error(e);
				}
				return false;
		} else if (control instanceof AbstractDatabindingWidget){
			try {
				AbstractDatabindingWidget afw = (AbstractDatabindingWidget)control;
				Object model = getModel();
				Method getMethod = MethodUtil.getGetMethod(model.getClass(), feature);
				if (getMethod != null){
					Object childModel = getMethod.invoke(model, new Object[]{});
					afw.setModel(childModel);
					return afw.isBound();
				}
			} catch (NoSuchMethodException e){
				logger.error(e);
			} catch (IllegalArgumentException e) {
				logger.error(e);
			} catch (IllegalAccessException e) {
				logger.error(e);
			} catch (InvocationTargetException e) {
				logger.error(e);
			}
			return false;
			
		}
		return false;
	}
	
	protected boolean hookTriggerButton(String methodName, final Button buttonWidget){
		if (buttonWidget == null){
			return false;
		}
		
		Object model = getModel();
		if (model == null){
			return false;
		}
		
		try {
			final Method method = MethodUtil.getMethod(model.getClass(), methodName);
			if (method != null){
				
				// see if this already had a selection listener
				SelectionListener oldSL = m_selectionListenerMap.get(buttonWidget);
				if (oldSL != null){
					buttonWidget.removeSelectionListener(oldSL);
				}
				SelectionListener newSL = new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							method.invoke(getModel(),  (Object[])null);
						} catch (IllegalArgumentException e1) {
							logger.error(e1);
						} catch (IllegalAccessException e1) {
							logger.error(e1);
						} catch (InvocationTargetException e1) {
							logger.error(e1);
						}
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) { /**/
					}
					
				};
				
				buttonWidget.addSelectionListener(newSL);
				m_selectionListenerMap.put(buttonWidget, newSL);
				return true;
			}
		} catch (SecurityException e1) {
			logger.warn(e1);
		} catch (NoSuchMethodException e) {
			logger.warn(e);
		}
		
		return false;
	}
	
	/**
	 * 
	 * This method does the actual work of binding together the text widget, and observer
	 * @param feature
	 * @param textWidget
	 * @return true if bound; false if something went wrong
	 */
	protected boolean bind(String feature,  final Text textWidget){
		if (textWidget == null){
			return false;
		}
		
		IObservableValue modelObservableValue = prepBinding(feature);
		
		if (modelObservableValue == null){
			return false;
		}
		
		//TODO verify that lose focus still works even with this binding
		ISWTObservableValue targetObservableValue = WidgetProperties.text(SWT.Modify).observeDelayed(DELAY, textWidget);
		
		Binding binding = m_dataBindingContext.bindValue(targetObservableValue, modelObservableValue, getTargetToModelStrategy(feature), getModelToTargetStrategy(feature));
		IObservableValue validationStatus = binding.getValidationStatus();
		validationStatus.addValueChangeListener(new DecoratingValueChangedListener(textWidget));		
		
		return true;
	}
	
	/**
	 * 
	 * This method does the actual work of binding together the button widget, observer
	 * @param feature
	 * @param buttonWidget
	 * @return true if bound; false if something went wrong
	 */
	protected boolean bind(String feature,  Button buttonWidget){
		if (buttonWidget == null){
			return false;
		}
		
		IObservableValue observable = prepBinding(feature);
		
		if (observable == null){
			return false;
		}
		
		m_dataBindingContext.bindValue(WidgetProperties.selection().observe(buttonWidget), observable,  getTargetToModelStrategy(feature), getModelToTargetStrategy(feature));
		
		return true;
	}


	/**
	 * This method does the actual work of binding together the combo widget, observer. 
	 * @param feature
	 * @param comboWidget
	 * @return true if bound; false if something went wrong
	 */
	protected boolean bind(String feature,  final Combo comboWidget){
		if (comboWidget == null){
			return false;
		}
		
		IObservableValue observable = prepBinding(feature);
		
		if (observable == null){
			return false;
		}
		
		Binding binding = m_dataBindingContext.bindValue( WidgetProperties.singleSelectionIndex().observe(comboWidget), observable, getTargetToModelStrategy(feature), getModelToTargetStrategy(feature));
		IObservableValue validationStatus = binding.getValidationStatus();
		validationStatus.addValueChangeListener(new DecoratingValueChangedListener(comboWidget));	
		
		return true;
	}
	
	/**
	 * This method does the actual work of binding together the colorSelector, observer. 
	 * @param feature
	 * @param colorSelector
	 * @return true if bound; false if something went wrong
	 */
	protected boolean bind(String feature,  final ColorSelector colorSelector){
		if (colorSelector == null){
			return false;
		}
		
		final IObservableValue modelObservableValue = prepBinding(feature);
		
		if (modelObservableValue == null){
			return false;
		}
		
		@SuppressWarnings("unused")
		Binding binding = m_dataBindingContext.bindValue( WidgetProperties.selection().observe(colorSelector.getButton()), modelObservableValue,
				new UpdateValueStrategy() {
			@Override
			protected IStatus doSet(IObservableValue observableValue, Object value) {
				return super.doSet(observableValue, colorSelector.getColorValue());
			}
		},
		
		new UpdateValueStrategy() {
			@Override
			protected IStatus doSet(IObservableValue observableValue, Object value) {
				if (value instanceof RGB){
					colorSelector.setColorValue((RGB)value);
				}
				return super.doSet(observableValue, value);
			}
		});
		
		IPropertyChangeListener listener = new IPropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				modelObservableValue.setValue(colorSelector.getColorValue());
				
			}
		};
		colorSelector.addListener(listener);
		m_colorSelectors.put(colorSelector, listener);
		
		//TODO: viewers are not controls.  This viewer therefore cannot display error decorations at all.
		//IObservableValue validationStatus = binding.getValidationStatus();
		//validationStatus.addValueChangeListener(new DecoratingValueChangedListener(comboWidget));	
		
		return true;
	}
	
	/**
	 * This method does the actual work of binding together the comboViewer, observer. 
	 * @param feature
	 * @param comboViewer
	 * @return true if bound; false if something went wrong
	 */
	protected boolean bind(String feature,  final ComboViewer comboViewer){
		if (comboViewer == null){
			return false;
		}
		
		IObservableValue modelObservableValue = prepBinding(feature);
		
		if (modelObservableValue == null){
			return false;
		}
		
		@SuppressWarnings("unused")
		Binding binding = m_dataBindingContext.bindValue( ViewerProperties.singleSelection().observe(comboViewer), modelObservableValue, getTargetToModelStrategy(feature), getModelToTargetStrategy(feature));
		//TODO: viewers are not controls.  This viewer therefore cannot display error decorations at all.
		//IObservableValue validationStatus = binding.getValidationStatus();
		//validationStatus.addValueChangeListener(new DecoratingValueChangedListener(comboWidget));	
		
		return true;
	}
	
	/**
	 * This method does the actual work of binding together the spinnerWidget, observer
	 * @param feature
	 * @param spinnerWidget
	 * @return true if bound; false if something went wrong
	 */
	protected boolean bind(String feature,  final Spinner spinnerWidget ){
		if (spinnerWidget == null){
			return false;
		}
		
		IObservableValue observable = prepBinding(feature);
		
		if (observable == null){
			return false;
		}
		
		Binding binding = m_dataBindingContext.bindValue( WidgetProperties.selection().observeDelayed(DELAY, spinnerWidget), observable, getTargetToModelStrategy(feature), getModelToTargetStrategy(feature));
		IObservableValue validationStatus = binding.getValidationStatus();
		validationStatus.addValueChangeListener(new DecoratingValueChangedListener(spinnerWidget));	
		
		return true;
	}
	
	/**
	 * Clear all errors from messages and decorators in preparation for a new validation.
	 */
	public void clearErrors() {
		if (!m_status.isEmpty()){
			for (Control control : m_status.keySet()){
				ControlDecoration cd = m_decorations.get(control);
				cd.setDescriptionText("");
				cd.hide();
			}
			m_status.clear();
			
			updateMultiStatus();
		}
	} 
	
	/**
	 * Show all errors via decorators on widgets.  
	 * If there are no errors none will show.
	 */
	public void showErrors() {
		boolean hasErrors = false;
		for (Control control : m_status.keySet()){
			IStatus status = m_status.get(control);
			if (status != null && !status.isOK()) {
				ControlDecoration cd = getControlDecoration(control);
				cd.setDescriptionText(status.getMessage());
				cd.setImage(getDecorationImage(status));
				cd.show();
				hasErrors = true;
			}
		}
		
		if (hasErrors){
			updateMultiStatus();
		} 
	}
	

	/**
	 * Show all errors via decorators on widgets.  
	 * If there are no errors none will show.
	 */
	public boolean clearError(Control control) {
		IStatus oldError = m_status.remove(control);
		if (oldError != null){
			ControlDecoration cd = getControlDecoration(control);
			cd.setDescriptionText("");
			cd.hide();
	
			updateMultiStatus();
		}
		return (oldError != null);
	}
	
	/**
	 * Update anything that listens to the multi status
	 */
	public void updateMultiStatus(){

		MultiStatus multiStatus = getErrorStatus();
		//notify any listeners
		for (IStatusListener listener : m_statusListeners){
			listener.statusChanged(multiStatus);
		}
	}
	
	/**
	 * @return true if the UI has been bound to the model
	 */
	public boolean isBound() {
		return m_bound;
	}
	
	/**
	 * set the bound flag.
	 * @param bound
	 */
	protected void setBound(boolean bound){
		m_bound = bound;
	}
	
	/**
	 * Returns an aggregate error status for this widget.
	 * @return
	 */
	public MultiStatus getErrorStatus(){
		MultiStatus result = null;
		for (IStatus status: m_status.values()){
			if (status != null && !status.isOK()){
				if (result == null){
					result = new MultiStatus(getPluginID(), status.getSeverity(), "Problems Found:", null);
				} 
				result.merge(status);
			}
		}
		return result;
	}
	
	/**
	 * Add a status listener to the list to be notified
	 * @param listener
	 */
	public void addListener(IStatusListener listener){
		m_statusListeners.add(listener);
	}
	
	/**
	 * Remove a given status listener from the list.
	 * @param listener
	 */
	public void removeListener(IStatusListener listener){
		m_statusListeners.remove(listener);
	}
	
	public void addListener(IChangeListener listener){
		if (m_dataBindingContext == null){
			return;
		}
		IObservableList observableList = m_dataBindingContext.getBindings();
		for (Object o : observableList) {
			if (o instanceof Binding){
				Binding binding = (Binding)o;
				IObservable observable = binding.getModel();
				observable.removeChangeListener(listener); // in case we already had this one added
				observable.addChangeListener(listener);
			}
		}
	}
	
	public void addListener(IValueChangeListener listener){
		if (m_dataBindingContext == null){
			return;
		}
		IObservableList observableList = m_dataBindingContext.getBindings();
		for (Object o : observableList) {
			if (o instanceof Binding){
				Binding binding = (Binding)o;
				IObservable observable = binding.getModel();
				IObservableValue ov = (IObservableValue)observable;
				ov.removeValueChangeListener(listener);
				ov.addValueChangeListener(listener);
			}
		}
	}
	
	/*
	public void addListener(IValueChangeListener listener){
		if (m_dataBindingContext == null){
			return;
		}
		IObservableList observableList = m_dataBindingContext.getBindings();
		for (Object o : observableList) {
			if (o instanceof Binding){
				Binding binding = (Binding)o;
				IObservable observable = binding.getModel();
				observable.removeChangeListener(listener); // in case we already had this one added
				observable.addChangeListener(listener);
			}
		}
	} */
	
	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#removeListener(int, org.eclipse.swt.widgets.Listener)
	 */
	public void removeListener(IChangeListener listener){
		if (m_dataBindingContext == null){
			return;
		}
		IObservableList observableList = m_dataBindingContext.getBindings();
		for (Object o : observableList) {
			if (o instanceof Binding){
				Binding binding = (Binding)o;
				binding.getModel().removeChangeListener(listener);
			}
		}
	}
	
	
	/**
	 * This supports a generic undo/redo operation on a generic object.
	 * @author tecohen
	 *
	 */
	protected class UndoableValueChangedListener implements IValueChangeListener {
		protected String m_feature;
		
		public UndoableValueChangedListener(String feature) {
			super();
			m_feature = feature;
		}
		
		public void handleValueChange(ValueChangeEvent event) {
			Object sourceObject = event.getSource();
			if (sourceObject instanceof IObserving){
				// add to undo/redo stack
				UndoablePOJOSetOperation operation = new UndoablePOJOSetOperation(getModel(), m_feature, event.diff);
				operation.addContext(m_undoContext);
				// TODO Make undo work. Breaking this now  DW 10/6/14
				//PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().add(operation);
			}
		}
	}
	
	/**
	 * When the value changes, update errors
	 * @author tecohen
	 *
	 */
	protected class DecoratingValueChangedListener implements IValueChangeListener {
		
		protected Control m_control; // the control or viewer we are listening to / decorating
		
		/**
		 * Constructor
		 * @param control the control we are listening to / decorating
		 */
		public DecoratingValueChangedListener(Control control){
			super();
			m_control = control;
		}
		
		/* 
		 * Decorate or undecorate.
		 * 
		 * (non-Javadoc)
		 * @see org.eclipse.core.databinding.observable.value.IValueChangeListener#handleValueChange(org.eclipse.core.databinding.observable.value.ValueChangeEvent)
		 */
		public void handleValueChange(ValueChangeEvent event) {
			Object sourceObject = event.getSource();
			if (sourceObject instanceof WritableValue){
				WritableValue wv = (WritableValue)sourceObject;
				if (wv.getValueType().equals(IStatus.class)) {
					IStatus status = (IStatus)wv.getValue();
					if (!status.isOK()){
						setStatus(m_control, status);
						showErrors();
					} else {
						clearError(m_control);
						showErrors();
					} 
				}
			}
		}

	}

	/**
	 * Default label width
	 * @return
	 */
	protected int getLabelWidth() {
		return m_labelWidth;
	}
	
	/**
	 * @param labelWidth
	 */
	protected void setLabelWidth(int labelWidth){
		m_labelWidth = labelWidth;
		if(m_rightData != null) {
		    m_rightData.widthHint = labelWidth;
		    getParent().pack();
		}
	}
	
	/**
	 * Default field width
	 * @return
	 */
	protected int getFieldWidth() {
		return m_fieldWidth;
	}
	
	/**
	 * Set the default field width
	 * @param fieldWidth
	 */
	protected void setFieldWidth(int fieldWidth) {
		m_fieldWidth = fieldWidth;
		if(m_leftData != null) {
            m_leftData.widthHint = fieldWidth;
            getParent().pack();
		}
	}
	
	/**
	 * Get the field decoration image for a warning or an error.
	 * @param status
	 * @return
	 */
	protected Image getDecorationImage(IStatus status){
		if (status == null){
			return null;
		}
		if (status.getSeverity() == IStatus.WARNING) {
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
			return fieldDecoration.getImage();
		} else if (status.getSeverity() >= IStatus.WARNING){
			FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
			return fieldDecoration.getImage();
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.databinding.IDatabindingProvider#isValid()
	 */
	public boolean isValid() {
		for (IStatus status : m_status.values()){
			if (!status.isOK()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @return
	 */
	public String getPluginID(){
		//return EnsembleUIDatabindingActivator.PLUGIN_ID;
		logger.error("Using the Broken Functionality getPluginID");
		return this.getClass().getName();
	}
	

	/* (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.databinding.IDatabindingProvider#setStatus(org.eclipse.swt.widgets.Control, org.eclipse.core.runtime.IStatus)
	 */
	public void setStatus(Control control, IStatus status){
		m_status.put(control, status);
	}
	
	/**
	 * @return
	 */
	public boolean isAdvancedVisible() {
		return m_advancedVisible;
	}

	/**
	 * @param advancedVisible
	 */
	public void setAdvancedVisible(boolean advancedVisible) {
		m_advancedVisible = advancedVisible;
	}
	
	/**
	 * constructs if necessary
	 * @return grid data for fields
	 */
	protected GridData getLeftData() {
		if (m_leftData == null){
			m_leftData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
			m_leftData.widthHint = getFieldWidth();
		}
		return m_leftData;
	}
	
	/**
	 * constructs if necessary
	 * @return grid data for labels
	 */
	protected GridData getRightData() {
		if (m_rightData == null){
			m_rightData = new GridData(SWT.RIGHT, SWT.CENTER,false,false);
			m_rightData.widthHint = getLabelWidth();
		}
		return m_rightData;
	}
	
	/**
	 * constructs if necessary
	 * @return grid data for combo boxes
	 */
	protected GridData getLeftComboData() {
		if (m_leftComboData == null){
			m_leftComboData = new GridData(SWT.LEFT, SWT.TOP, false, false);
			m_leftComboData.widthHint = getFieldWidth();
		}
		return m_leftComboData;
	}
	
	@Override
	public void dispose() {
		for(Control c : m_controls) {
			c.dispose();
		}
		for(Label l : m_labels) {
			l.dispose();
		}
		for(Label l : m_units) {
			l.dispose();
		}
		super.dispose();
	}
}
