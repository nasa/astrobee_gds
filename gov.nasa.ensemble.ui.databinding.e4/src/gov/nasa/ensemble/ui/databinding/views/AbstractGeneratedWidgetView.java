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
package gov.nasa.ensemble.ui.databinding.views;

import gov.nasa.ensemble.ui.databinding.databinding.BoundWidgetFactory;
import gov.nasa.ensemble.ui.databinding.status.IStatusListener;
import gov.nasa.ensemble.ui.databinding.util.UndoRedoUtil;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * This is a convenience class for creating simple views that have a generated widget.
 * These are scrollable non-form views.
 * 
 * @author tecohen
 *
 */
public abstract class AbstractGeneratedWidgetView extends ViewPart {
	
	// the actual model we are editing.  Changes to the widget immediately change this model (if they are valid).  
	// If you don't want live editing of the model be sure to pass in a clone.
	// note you do not have to keep a reference to the model here; you can simply pass it directly into the widget.
	protected Object m_model;	
	
	protected AbstractDatabindingWidget m_widget; // the widget which will be auto generated
	
	protected Label m_errorMessage; // the label for the error message
	
	protected ScrolledComposite m_scrolledComposite; // the scrolled composite
	
	protected Composite m_widgetComposite; // the composite which holds the widget
	
	// optional listener if you want to do anything with your error messages such as display them
	protected IStatusListener m_errorStatusListener = new IStatusListener() {

		public void statusChanged(IStatus status) {
			setErrorMessage(null);
			StringBuffer text = new StringBuffer(getDefaultText());
			if (status != null){
				text = new StringBuffer(status.getMessage());
				if (status instanceof MultiStatus){
					MultiStatus ms = (MultiStatus)status;
					for (IStatus s : ms.getChildren()){
						text.append(s.getMessage());
						text.append("\n");
					}
				}
				if (status.getSeverity() == IStatus.WARNING){
					setErrorMessage(text.toString());
				} else {
					setErrorMessage(text.toString());
				}
			}
		}
	};


	protected boolean m_forceAllFields = false;
	
	/**
	 * 
	 */
	public AbstractGeneratedWidgetView() {
		// do nothing
	}
	
	/**
	 * @return the class we will use to generate the widget
	 */
	@SuppressWarnings("unchecked")
	public abstract Class getModelClass();
	
	/**
	 * @return the default text used in the title
	 */
	public String getDefaultText(){
		return "";
	}
	
	/**
	 * Set the model; this will fill the widget if it exists
	 * @param model
	 */
	public void setModel(Object model){
		doSetModel(model);
	}
	
	/**
	 * Asynchronously set the model.
	 * @param model
	 */
	protected void doSetModel(final Object model){
	    Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                m_model = model;
                if (m_widget != null){
                    setErrorMessage("");
                    m_widget.setModel(model);
                }
            }
            
        });
	}
	
	/**
	 * Get the model (from the widget if it exists)
	 * @return
	 */
	public Object getModel(){
		if (m_widget != null){
			return m_widget.getModel();
		}
		return m_model;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		m_scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL|SWT.V_SCROLL );
		m_scrolledComposite.setLayout(new GridLayout());
		m_scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL));
		
		if (m_widgetComposite == null){
			m_widgetComposite = new Composite(m_scrolledComposite, SWT.NONE);
		}
		m_widgetComposite.setLayout(new GridLayout());
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		m_widgetComposite.setLayoutData(gd2);
		
		m_errorMessage = new Label(m_widgetComposite, SWT.NONE);
		m_errorMessage.setText(getDefaultText());
		m_errorMessage.setLayoutData(gd2);
		
		// make anything extra in the UI
		createPreWidgetExtras(m_widgetComposite);
		
		createWidget(m_widgetComposite, isForceAllFields());
		
		// make anything extra in the UI
		createPostWidgetExtras(m_widgetComposite);
		
		// support auto scrolling 
		m_widgetComposite.setSize(m_widgetComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		m_scrolledComposite.setContent(m_widgetComposite);
		
		//initialize the widget with the values of the model
		initializeValues();
		
		// create and register global undo and redo operations
		UndoRedoUtil.createViewUndoRedo(getViewSite());
		
		makeToolbar();
	}
	
	/**
	 * If you want a toolbar with this view implement it here
	 */
	protected void makeToolbar(){
		// do nothing
	}
	
	/**
	 * If you want to construct anything after the autogenerate widget, put it here.
	 */
	protected  void createPreWidgetExtras(Composite container){
		// do nothing
	}
	
	/**
	 * Create the actual widget
	 * @param container
	 */
	protected void createWidget(Composite container, boolean forceAllFields){
		// add the widget on the left
		try {
			m_widget = BoundWidgetFactory.getWidget(getModelClass(), container, SWT.NONE, false, forceAllFields);
			
			// add the listener to update the multi-error display at the top of the view
			m_widget.addListener(m_errorStatusListener);
			
		} catch (NullPointerException e1) {
			// do nothing
		} catch (ClassNotFoundException e1) {
			// do nothing
		} catch (InstantiationException e) {
			// do nothing
		}
	}
	
	/**
	 * If you want to construct anything after the auto-generated widget, put it here.
	 */
	protected  void createPostWidgetExtras(Composite container){
		// do nothing
	}
	
	/**
	 * Initialize the widget with some default values
	 */
	protected void initializeValues() {
		if (m_model != null && m_widget != null){
			m_widget.setModel(m_model);
		} 
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// do nothing
	}
	
	/**
	 * Set the error message for display
	 * @param message
	 */
	protected void setErrorMessage(final String message){
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
			    if (message == null){
			        m_errorMessage.setText("");
			    } else {
			        m_errorMessage.setText(message);
			    }
				m_errorMessage.redraw();
				m_errorMessage.getParent().layout();
			}
			
		});
	}

	public boolean isForceAllFields() {
		return m_forceAllFields;
	}

	public void setForceAllFields(boolean forceAllFields) {
		m_forceAllFields = forceAllFields;
	}
}
