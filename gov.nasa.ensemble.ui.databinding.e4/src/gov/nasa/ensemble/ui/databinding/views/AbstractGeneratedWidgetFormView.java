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
import gov.nasa.ensemble.ui.databinding.util.UndoRedoUtil;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * Convenience class for creating views that have generated widgets.
 * These are scrolling form views.
 * 
 * @author tecohen
 *
 */
public abstract class AbstractGeneratedWidgetFormView extends AbstractGeneratedWidgetView {

	protected ScrolledForm m_form;
	protected FormToolkit m_toolkit;
	

	@Override
	public void createPartControl(Composite parent) {
		
		m_toolkit = new FormToolkit(parent.getDisplay());
		m_form = m_toolkit.createScrolledForm(parent);
		
		m_form.setText(getDefaultText());
		
		TableWrapLayout tableLayout = new TableWrapLayout();
		tableLayout.numColumns = 3;
		m_form.getBody().setLayout(tableLayout);
		
		// make anything extra in the UI
		createPreWidgetExtras( m_form.getBody());
		
		// add the widget on the left
		createWidget(m_form.getBody(), isForceAllFields());
		
		// make anything extra in the UI
		createPostWidgetExtras( m_form.getBody());
		
		//initialize the widget with the values of the model
		initializeValues();
		
		// create and register global undo and redo operations
		UndoRedoUtil.createViewUndoRedo(getViewSite());

	}
	
	@Override
	protected void createWidget(Composite container, boolean forceAllFields) {
		try {
			m_widget = BoundWidgetFactory.getWidget(getModelClass(), m_toolkit, container, SWT.NONE, false, forceAllFields);
			
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
	 * Set the error message for display
	 * @param message
	 */
	@Override
	protected void setErrorMessage(final String message) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				if (message != null && message.length() > 0 && !message.equals(getDefaultText())) {
					m_form.setMessage(message, IMessageProvider.ERROR);
				} else {
					m_form.setMessage(null, IMessageProvider.NONE);
				}
			}

		});
	}

	public FormToolkit getToolkit() {
		return m_toolkit;
	}

	public void setToolkit(FormToolkit toolkit) {
		m_toolkit = toolkit;
	}
}
