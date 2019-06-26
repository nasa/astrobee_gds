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

import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.IPropertyObservable;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * This view has ONE main widget based on selection but the extras can change.
 * This is simpler than the DynamicWidgetView in that it does not use the stack layout or cache types per widget.
 * 
 * 
 * @author tecohen
 * 
 */
@SuppressWarnings("unchecked")
public abstract class SemiDynamicWidgetView extends AbstractGeneratedWidgetView {
	protected ISelectionProvider m_selectionProvider;
	protected IObservableValue m_selectionValue;
	
	protected Composite m_scrollingContents;
	
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

		// make anything extra in the UI before the widget
		createPreWidgetExtras(m_scrollingContents);
		
		// make the widget composite
		if (m_widgetComposite == null){
            m_widgetComposite = new Composite(m_scrollingContents, SWT.NONE);
        }
        m_widgetComposite.setLayout(new GridLayout());
        GridData gd3 = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_widgetComposite.setLayoutData(gd3);
		
		// make the widget itself
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
					SemiDynamicWidgetView.super.setModel(model);
					return;
				}
				
				AbstractDatabindingWidget newWidget = null;
				if (isForceAllFields() ){
					newWidget = getWidgetFromFactory(modelClass, isForceAllFields());
				} else {
					if (newWidget == null) {
						// look it up from the registered widgets
						newWidget = getWidgetFromFactory(modelClass, isForceAllFields());
						if (newWidget != null) {
							newWidget.setSelectionObservableValue(getSelectionValue());
							// add the listener to update the multi-error display at the top
							// of the view
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
					if (m_scrolledComposite instanceof ScrolledForm){
						((ScrolledForm)m_scrolledComposite).reflow(true);
					} else {
						Point size = m_scrollingContents.computeSize(SWT.DEFAULT, SWT.DEFAULT);
						m_scrollingContents.setSize(size);
						m_scrolledComposite.setMinHeight(size.y + 500);
					}
				}
				
				SemiDynamicWidgetView.super.setModel(model);
				
			}
			
		});

	}

	protected AbstractDatabindingWidget getWidgetFromFactory(Class modelClass, boolean forceAllFields) {
		try {
			return BoundWidgetFactory.getWidget(modelClass, m_widgetComposite, SWT.NONE, false, forceAllFields);
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
	 * @param selectionProvider
	 */
	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		m_selectionProvider = selectionProvider;
	}

	/**
	 * Properly update the current model when the selection changes.
	 */
	protected IChangeListener m_updateSelectionListener = new UpdateSelectionListener();
	
	/**
	 * Since the selection provider only supports binding for top-level children, we have to force update of more complex child / grandchildren binding.
	 * @param model
	 */
	protected void handleChildUpdates(Object model){
		if (m_widget != null){
			m_widget.handleChildUpdates(model);
		}
	}

	public void hookViewer(ISelectionProvider selectionProvider) {
		if (getSelectionProvider() != null) {
			//TODO unbind all the old ones
		    
			if (getSelectionValue() != null){
				m_selectionValue.removeChangeListener(getUpdateSelectionListener());
				
				for (IChangeListener listener : getExtraChangeListeners()){
					m_selectionValue.removeChangeListener(listener);
				}
			}
		}
		
		// set up the new ones
		setSelectionProvider(selectionProvider);
		m_selectionValue = ViewersObservables.observeSingleSelection(getSelectionProvider());
		m_selectionValue.addChangeListener(getUpdateSelectionListener());
		for (IChangeListener listener : getExtraChangeListeners()){
			m_selectionValue.addChangeListener(listener);
		}
		
		// bootstrap the selection
		ISelection selection = selectionProvider.getSelection();
		if (selection != null && selection instanceof IStructuredSelection){
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object el = sel.getFirstElement();
			setModel(el);
		}
		
	}

	protected void listenForSelectionProviderView() {
		IWorkbenchPage page = getSite().getPage();
		page.addPartListener(new IPartListener2() {
			public void partVisible(IWorkbenchPartReference partRef) {
				// do nothing
			}
			public void partHidden(IWorkbenchPartReference partRef) {
				// do nothing
			}
			public void partActivated(IWorkbenchPartReference partRef) {
				if (getSelectionProvider() != null){
					if (partRef.getId().equals(getSelectionProviderPartID())){
						findAndHookViewer();
					}
				}
			}
			public void partBroughtToTop(IWorkbenchPartReference partRef) { /* do nothing*/ }
			public void partClosed(IWorkbenchPartReference partRef) { /* do nothing*/ }
			public void partDeactivated(IWorkbenchPartReference partRef) { /* do nothing*/ }
			public void partInputChanged(IWorkbenchPartReference partRef) { /* do nothing*/ }
			public void partOpened(IWorkbenchPartReference partRef) { /* do nothing*/ }
		});
		
	}
	
	public abstract void findAndHookViewer();
	
	public abstract String getSelectionProviderPartID();
	
	public ISelectionProvider getSelectionProvider() {
		return m_selectionProvider;
	}

	public ISelectionProvider getSelectionProvider(IWorkbenchPartReference partRef) {
		return null;
	}

	public IObservableValue getSelectionValue() {
		if (m_selectionValue == null) {
			findAndHookViewer();
		}
		return m_selectionValue;
	}

	public IChangeListener getUpdateSelectionListener() {
		return m_updateSelectionListener;
	}
	
	/**
	 * This will be called when setting selection provider.
	 */
	protected List<IChangeListener> getExtraChangeListeners() {
		return Collections.EMPTY_LIST;
	}
	
	protected class UpdateSelectionListener implements IChangeListener {
	    
        public void handleChange(final ChangeEvent arg0) {
            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    Object source = arg0.getSource();
                    Object observed = null;
                    if (source instanceof IPropertyObservable) {
                        IPropertyObservable propertyObservable = (IPropertyObservable) source;
                        observed = propertyObservable.getObserved();
                        if (observed instanceof ISelectionProvider) {
                            ISelectionProvider sp = (ISelectionProvider) observed;
                            IStructuredSelection sel = (IStructuredSelection) sp.getSelection();
                            Object el = sel.getFirstElement();
                            if (el != null) {
                                if (getModel() == null){
                                    setModel(el);
                                } else {
                                    if (onlyChangeOnClassChange()) {
                                        if (!getModel().getClass().equals(el.getClass())){
                                            setModel(el);
                                        }
                                    } else {
                                        setModel(el);
                                    }
                                }
                            }
                        }
                    } else if (source instanceof IObservableValue){
                        IObservableValue iov = (IObservableValue)source;
                        observed = iov.getValue();
                        if (observed != null) {
                            if (getModel() == null){
                                setModel(observed);
                            } else {
                                if (onlyChangeOnClassChange()) {
                                    if (!getModel().getClass().equals(observed.getClass())){
                                        setModel(observed);
                                    } else {
                                        // same type of selected object, we have to handle children.
                                        handleChildUpdates(observed);
                                    }
                                } else {
                                    setModel(observed);
                                }
                            }
                        }
                    }
                }
                
            });
        }
        
        protected boolean onlyChangeOnClassChange() {
            return true;
        }
	}
}
