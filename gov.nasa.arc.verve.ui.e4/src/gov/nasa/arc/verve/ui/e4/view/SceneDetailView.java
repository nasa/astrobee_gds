package gov.nasa.arc.verve.ui.e4.view;
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

import gov.nasa.arc.irg.util.ui.ViewID;
import gov.nasa.arc.verve.ui.e4.ImageRegistryKeeper;
import gov.nasa.arc.verve.ui.e4.widget.SpatialWidget;
import gov.nasa.ensemble.ui.databinding.views.SemiDynamicWidgetView;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;
import gov.nasa.ensemble.ui.databinding.databinding.BoundWidgetFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.inject.Inject;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.IPropertyObservable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Spatial;

/**
 * Custom dynamic non-form widget view for spatials.
 * This includes a toolbar and switching between debug (all fields) and nicer spatial widget.
 * 
 * @author tecohen
 *
 */
public class SceneDetailView extends SemiDynamicWidgetView  {
	
	public static final String ID = SceneDetailView.class.getName();
	
	protected SceneGraphTreeViewPart m_treeView;
	
	protected Action m_refreshAction;
	protected Action m_pinAction;
	protected Action m_newViewAction;
	
	protected boolean m_pinned = false;
	
	// the last selected model's class
	protected Class m_previousModelClass;
	
    // The composite that contains all the render states
    protected Composite m_renderStatesContainer;

    // map of each renderState composite to all of its children for hiding
	protected Map<Composite, List<AbstractDatabindingWidget>> m_compositeWidgetMap = new WeakHashMap<Composite, List<AbstractDatabindingWidget>>();
	
	// map of each renderstate type to its composite
	protected Map<RenderState.StateType, Composite> m_renderStateComposites = new WeakHashMap<RenderState.StateType, Composite>();
	
	protected List<RenderState.StateType> m_lastActiveRenderStates = new ArrayList<RenderState.StateType>();
	
	protected List<IChangeListener> m_extraChangeListeners = new ArrayList<IChangeListener>();
	{
		m_extraChangeListeners.add(new IChangeListener() {
			public void handleChange(ChangeEvent arg0) {
				
				// figure out what is the new model
				Object source = arg0.getSource();
				Object observed = null;
				if (source instanceof IPropertyObservable) {
					IPropertyObservable propertyObservable = (IPropertyObservable) source;
					observed = propertyObservable.getObserved();
					if (observed instanceof ISelectionProvider) {
						ISelectionProvider sp = (ISelectionProvider) observed;
						IStructuredSelection sel = (IStructuredSelection) sp.getSelection();
						observed = sel.getFirstElement();
					}
				} else if (source instanceof IObservableValue){
					IObservableValue iov = (IObservableValue)source;
					observed = iov.getValue();
				}
				
				if (observed != null) {
					// do the update of the extras
					updatePostWidgetExtras(observed);
				}
			}
		});
	}
	
	{
	    m_updateSelectionListener = new UpdateSelectionListener() {
	        @Override
	        protected boolean onlyChangeOnClassChange() {
	            return false;
	        }
	    };
	}
	
	/* 
	 * In this case, the SpatialWidget is always the same so we don't want to change that widget based on model type.
	 * The render states are handled as extras.
	 * 
	 * (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.views.DynamicWidgetView#setModel(java.lang.Object)
	 */
	@Override
    public void setModel(final Object model) {
        if (model == null){
            return;
        }
        Display.getCurrent().asyncExec(new Runnable() {

            public void run() {
                Class modelClass = Spatial.class; 
                
                // we have already created the spatial widget; set its model.
                if (m_widget != null && m_widget.getModel() != null) {
                    doSetModel(model);
                    return;
                }
                
                // we have not yet created the spatial widget; create it.
                AbstractDatabindingWidget newWidget = null;
                if (newWidget == null) {
                    // look it up from the registered widgets
                    newWidget = getWidgetFromFactory(modelClass, false);
                    if (newWidget != null) {
                        newWidget.setSelectionObservableValue(getSelectionValue());
                        // add the listener to update the multi-error display at the top of the view
                        newWidget.addListener(m_errorStatusListener);
                    }
                }

                if (newWidget != null) {
                    if (m_widget != null) {
                        m_widget.unbindUI();
                    }
                    m_widget = newWidget;
                    if (m_scrolledComposite instanceof ScrolledForm){
                        ((ScrolledForm)m_scrolledComposite).reflow(true);
                    } else {
                        Point size = m_scrollingContents.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                        m_scrollingContents.setSize(size);
                        m_scrolledComposite.setMinHeight(size.y);// + 500);
                    }
                    
                }
                
                doSetModel(model);
            }
            
        });

    }

	
	@Override
	public ISelectionProvider getSelectionProvider(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(false);
		if (part instanceof SceneGraphTreeViewPart) {
			SceneGraphTreeViewPart tce = (SceneGraphTreeViewPart) part;
			return tce.getSceneGraphTreePanel().getSpatialSelectionProvider();
		}
		return null;
	}

	@Override
	public void findAndHookViewer() {
		if (m_treeView == null){
			IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (activePage != null){
				IViewPart vp = activePage.findView(SceneGraphTreeViewPart.ID);
				if (vp != null) {
					m_treeView = (SceneGraphTreeViewPart)vp;
					IWorkbenchPartReference partRef = activePage.getReference(vp);
					hookViewer(getSelectionProvider(partRef));
				}
			}
		}
	}
	
	/**
	 * If you want to construct anything after the auto-generated widget, put it here.
	 */
	@Override
	protected  void createPostWidgetExtras(Composite container){
		m_renderStatesContainer = container;
	}
	
	@Override
	protected List<IChangeListener> getExtraChangeListeners() {
		return m_extraChangeListeners;
	} 
	
	/**
	 * Update the extras after the widget when the selection changes.
	 * (only if this view is not pinned)
	 */
	protected synchronized void updatePostWidgetExtras(Object model) {
	    if (isPinned()){
	        return;
	    }
		if (model instanceof Spatial){
			Spatial s = (Spatial)model;
			EnumMap<StateType, RenderState> renderStates =  s.getLocalRenderStates();
			
			boolean matchesPrevious = false;
			if (m_previousModelClass != null && m_previousModelClass.equals(model.getClass())){
				matchesPrevious = true;
			}
			//hide them all first
			if (!matchesPrevious){
			    for (RenderState.StateType rs : m_lastActiveRenderStates){
			        if (!renderStates.containsKey(rs)){
    			        Composite c = m_renderStateComposites.get(rs);
    			        if (c != null){
    	                    if (c instanceof Section){
    	                        Section section = (Section)c;
    	                        section.setExpanded(false);
    	                    }
    	                    
        					List<AbstractDatabindingWidget> list = m_compositeWidgetMap.get(c);
        					if (list != null){
        						for (AbstractDatabindingWidget child : list){
        							child.unbindUI();
        						}
        					}
        					c.setVisible(false);
    			        }
			        }
				}
			    m_lastActiveRenderStates.clear();
			}
			for (StateType stateType : renderStates.keySet()){
				RenderState state = renderStates.get(stateType);
				m_lastActiveRenderStates.add(stateType);
				Composite found = m_renderStateComposites.get(stateType);
				if (found == null){
					// create a new one
					GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
					gd.horizontalSpan = 2;
					
					Section section = new Section(m_renderStatesContainer, ExpandableComposite.TWISTIE | ExpandableComposite.CLIENT_INDENT);
					section.setText(stateType.toString());
					section.setLayoutData(gd);
					
					Composite composite = new Composite(section, SWT.NONE);
					composite.setLayout(new GridLayout(3, false));
					
					List<AbstractDatabindingWidget> widgetList = new ArrayList<AbstractDatabindingWidget>();
					try {
						AbstractDatabindingWidget widget = BoundWidgetFactory.getWidget(state.getClass(), composite, SWT.NONE, false, false);
						GridData contentGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
						widget.setLayoutData(contentGridData);
						if (m_widget != null){
							widget.setDataBindingContext(m_widget.getDataBindingContext());
						}
						widget.setModel(state);
						widgetList.add(widget);
					} catch (NullPointerException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
					
					m_compositeWidgetMap.put(section, widgetList);
					
					section.setClient(composite);
					section.addExpansionListener(new ExpansionAdapter() {
						@Override
						public void expansionStateChanged(ExpansionEvent e) {
							Point size = m_scrollingContents.computeSize(SWT.DEFAULT, SWT.DEFAULT);
							m_scrollingContents.setSize(size);
							m_scrollingContents.getParent().layout(true, true);
						}
					});
					
					m_renderStateComposites.put(stateType, section);
				} else {
					found.redraw();
					found.layout(true, true);
					found.setVisible(true);
					
					List<AbstractDatabindingWidget> list = m_compositeWidgetMap.get(found);
					if (list != null){
						for (AbstractDatabindingWidget widget : list){
						    if (widget.getModel() == null || !widget.getModel().equals(state)) {
						        widget.unbindUI();
						        widget.setModel(state);
						    }
						}
					}
					Control[] kids = found.getChildren();
					for (Control k : kids){
						k.setVisible(true);
					}
				}
				m_renderStatesContainer.pack(true);
				m_renderStatesContainer.layout(true, true);
				m_previousModelClass = model.getClass();
			}
		}
	}
	@Override
	public String getSelectionProviderPartID() {
		return SceneGraphTreeViewPart.ID;
	}
	
	@Override
	public void setFocus() {
		super.setFocus();
		if (m_treeView == null ){
			findAndHookViewer();
		} 
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class getModelClass() {
		return Spatial.class;
	}
	
	@Override
	protected void makeToolbar() {
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.removeAll();
		addRefreshAction(manager);
		addPinAction(manager);
		addNewViewAction(manager);
		manager.update(true);
	}
	
	/**
	 * Add the refresh action to the tool bar manager.
	 */
	protected void addRefreshAction(IToolBarManager toolBarManager) {
		m_refreshAction = new Action("Refresh") {
			@Override
			public void run() {
				setModel(getModel());
			}
		};
		m_refreshAction.setImageDescriptor(ImageRegistryKeeper.getInstance().getImageDescriptorFromRegistry("arrow_circle"));
		toolBarManager.add(m_refreshAction);
	}
	
	/**
	 * @return true if this view is pinned and should not listen to selection changes
	 */
	public boolean isPinned() {
		return m_pinned;
	}

	/**
	 * @param mPinned
	 */
	public void setPinned(boolean mPinned) {
		m_pinned = mPinned;
	}

	/**
	 * Add the pin action to the tool bar manager.
	 */
	protected void addPinAction(IToolBarManager toolBarManager) {
		m_pinAction = new Action("Pin", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				if (m_widget != null && getModel() != null){
					m_pinned = !m_pinned;
					((SpatialWidget)m_widget).setPinned(m_pinned);
					setChecked(m_pinned);
				}
			}
		
		};
		m_pinAction.setImageDescriptor(ImageRegistryKeeper.getInstance().getImageDescriptorFromRegistry("pin"));
		toolBarManager.add(m_pinAction);
	}
	
	/**
	 * Add the new view action to the tool bar manager.
	 */
	protected void addNewViewAction(IToolBarManager toolBarManager) {
		m_newViewAction = new Action("Create New SceneDetail View") {
			@Override
			public void run() {
				try {
					String secondaryID = ViewID.getUniqueSecondaryID(ID);
					getSite().getPage().showView(ID, secondaryID, IWorkbenchPage.VIEW_CREATE);
				} catch (PartInitException e) {
					//
				}
			}
		
		};
		m_newViewAction.setImageDescriptor(ImageRegistryKeeper.getInstance().getImageDescriptorFromRegistry("new_detail_view"));
		toolBarManager.add(m_newViewAction);
	}
	
	/**
	 * Create or return an unpinned scene detail view
	 * @return
	 */
	public static synchronized void showUnpinnedView(final Spatial selection) {
		
		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				try {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IViewPart foundView = page.findView(ID);
					if (foundView != null){
						SceneDetailView sdv = (SceneDetailView)foundView;
						if (!sdv.isPinned()){
							page.showView(ID);
							return;
						} else if (sdv.getModel().equals(selection)){
							page.showView(ID);
							return;
						}
					} else {
						page.showView(SceneDetailView.ID);
						return;
					}
					// no good; either it was not found or it was pinned.  
					// See if we have an unpinned one.
					for (IViewReference vw : page.getViewReferences()){
						if (vw.getId().equals(ID)){
							SceneDetailView sdv = (SceneDetailView)vw.getView(false);
							if (!sdv.isPinned()){
								page.showView(ID, vw.getSecondaryId(), IWorkbenchPage.VIEW_VISIBLE);
								return;
							} else if (sdv.getModel().equals(selection)){
								page.showView(ID, vw.getSecondaryId(), IWorkbenchPage.VIEW_VISIBLE);
								return;
							}
						}
					}

					// nothing doing.  make a new one
					String secondaryID = "" + new Date().getTime();
					SceneDetailView mvp = (SceneDetailView)page.showView(SceneDetailView.ID, secondaryID, IWorkbenchPage.VIEW_VISIBLE);
					mvp.initializeValues();
					
					//TODO the view is not always on top.  How do you get a view to go on top?
				} catch (PartInitException e) {
					//
				}
			}
		});
	}
	
}
