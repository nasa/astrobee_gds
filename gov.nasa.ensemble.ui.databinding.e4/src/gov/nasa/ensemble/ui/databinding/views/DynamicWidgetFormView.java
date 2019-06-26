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
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public abstract class DynamicWidgetFormView extends DynamicWidgetView {

	protected ScrolledForm m_form;
	protected FormToolkit m_toolkit;

	@Override
	public void createPartControl(Composite parent) {
		m_toolkit = new FormToolkit(parent.getDisplay());
		parent.setBackground(m_toolkit.getColors().getBackground());

		m_form = m_toolkit.createScrolledForm(parent);
		m_form.setText(getDefaultText());
		m_scrolledComposite = m_form;
		m_scrolledComposite.setLayout(new GridLayout());
		m_scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL));
		
		m_scrollingContents = m_form.getBody();
		m_scrollingContents.setLayout(new GridLayout(1, false));
		m_scrollingContents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		m_stackLayoutComposite = m_toolkit.createComposite(m_scrollingContents);
		m_stackLayoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		m_stackLayout = new StackLayout();
		m_stackLayoutComposite.setLayout(m_stackLayout);

		m_widgetComposite = m_stackLayoutComposite;// m_form.getBody();
		
		// make anything extra in the UI
		createPreWidgetExtras(m_widgetComposite);

		// add the widget on the left
		createWidget(m_widgetComposite, isForceAllFields());

		// make anything extra in the UI
		createPostWidgetExtras(m_widgetComposite);
		
		// support auto scrolling 
		m_scrollingContents.setSize(m_scrollingContents.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		m_scrolledComposite.setContent(m_scrollingContents);

		// initialize the widget with the values of the model
		initializeValues();

		// create and register global undo and redo operations
		UndoRedoUtil.createViewUndoRedo(getViewSite());

		// connect with the viewer if it exists
		findAndHookViewer();

	}

	@SuppressWarnings("unchecked")
	@Override
	protected AbstractDatabindingWidget getWidgetFromFactory(Class modelClass, boolean forceAllFields) {
		try {
			return BoundWidgetFactory.getWidget(modelClass, m_toolkit, m_widgetComposite, SWT.NONE, false, forceAllFields);
		} catch (NullPointerException e) {
			// do nothing
		} catch (ClassNotFoundException e) {
			// do nothing
		} catch (InstantiationException e) {
			// do nothing
		}
		return null;
	}

	/**
	 * Set the error message for display
	 * 
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
