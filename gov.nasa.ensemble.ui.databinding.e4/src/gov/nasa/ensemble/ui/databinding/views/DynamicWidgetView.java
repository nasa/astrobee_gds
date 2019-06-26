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

import gov.nasa.ensemble.ui.databinding.util.UndoRedoUtil;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import java.util.HashMap;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Support a view which dynamically flips between the correct widgets based on
 * the model.
 * 
 * @author tecohen
 * 
 */
@SuppressWarnings("unchecked")
public abstract class DynamicWidgetView extends SemiDynamicWidgetView {
	protected HashMap<Class, AbstractDatabindingWidget> m_classWidgetMap = new HashMap<Class, AbstractDatabindingWidget>();
	
	protected StackLayout m_stackLayout;
	protected Composite m_stackLayoutComposite;
	
	@Override
	public void createPartControl(Composite parent) {
		// overall scrolling
		m_scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL|SWT.V_SCROLL );
		m_scrolledComposite.setLayout(new GridLayout());
		m_scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL));
		
		// composite to hold all the scrolling stuff
		m_scrollingContents = new Composite(m_scrolledComposite, SWT.NONE);
		m_scrollingContents.setLayout(new GridLayout());
		GridData gd = new GridData(SWT.FILL, SWT.FILL);
		m_scrollingContents.setLayoutData(gd);
		
		// error message
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		m_errorMessage = new Label(m_scrollingContents, SWT.NONE);
		m_errorMessage.setText(getDefaultText());
		m_errorMessage.setLayoutData(gd2);

		// make anything extra in the UI
		createPreWidgetExtras(m_scrollingContents);
		
		// stack
		m_stackLayoutComposite = new Composite(m_scrollingContents, SWT.NONE);
		m_stackLayoutComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		m_stackLayout = new StackLayout();
		m_stackLayoutComposite.setLayout(m_stackLayout);
		
		// make the widget itself
		m_widgetComposite = m_stackLayoutComposite;
		createWidget(m_widgetComposite, isForceAllFields());
		
		// make anything extra in the UI
		createPostWidgetExtras(m_scrollingContents);
		
		// support auto scrolling 
		m_scrollingContents.setSize(m_scrollingContents.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		m_scrolledComposite.setContent(m_scrollingContents);
		
		//initialize the widget with the values of the model
		initializeValues();
		
		// create and register global undo and redo operations
		UndoRedoUtil.createViewUndoRedo(getViewSite());
		
		makeToolbar();
		
		listenForSelectionProviderView();
		findAndHookViewer();
	}

	@Override
	public Class getModelClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createWidget(Composite container, boolean forceAllFields) {
		// for starters, no widget.
	}

	@Override
	public void setModel(final Object model) {
		if (model == null){
			return;
		}
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				Class modelClass = model.getClass();
				if (m_widget != null && m_widget.getModel() != null && m_widget.getModel().getClass().equals(modelClass)) {
					doSetModel(model);
					return;
				}
				
				AbstractDatabindingWidget newWidget = null;
				if (isForceAllFields() ){
					newWidget = getWidgetFromFactory(modelClass, isForceAllFields());
				} else {
					newWidget = m_classWidgetMap.get(modelClass);
					if (newWidget == null) {
						// look it up from the registered widgets
						newWidget = getWidgetFromFactory(modelClass, isForceAllFields());
						if (newWidget != null) {
							if (!isForceAllFields()){
								m_classWidgetMap.put(modelClass, newWidget);
							}
							newWidget.setSelectionObservableValue(getSelectionValue());
							// add the listener to update the multi-error display at the top of the view
							newWidget.addListener(m_errorStatusListener);
						}
					}
				}

				if (newWidget != null) {
					if (m_widget != null) {
						m_widget.unbindUI();
					}
					setErrorMessage("");
					m_widget = newWidget;
					m_stackLayout.topControl = m_widget;
					m_stackLayoutComposite.layout();
					if (m_scrolledComposite instanceof ScrolledForm){
						((ScrolledForm)m_scrolledComposite).reflow(true);
					} else {
						Point size = m_scrollingContents.computeSize(SWT.DEFAULT, SWT.DEFAULT);
						m_scrollingContents.setSize(size);
						m_scrolledComposite.setMinHeight(size.y + 500);
					}
				}
				
				doSetModel(model);
				
			}
			
		});

	}

	@Override
    public void hookViewer(ISelectionProvider selectionProvider) {
		if (getSelectionProvider() != null) {
			//unbind all the old ones
			for (AbstractDatabindingWidget widget : m_classWidgetMap.values()) {
				if (widget.isBound()){
					widget.unbindUI();
				}
			}
		}
		super.hookViewer(selectionProvider);
		
	}

	
	
}
