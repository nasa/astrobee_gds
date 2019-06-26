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
package gov.nasa.arc.verve.ui.e4.panel.tree;

import gov.nasa.arc.irg.util.ui.IrgUI;
import gov.nasa.arc.irg.util.ui.job.JobTimeout;
import gov.nasa.arc.verve.common.VerveDebug;
import gov.nasa.arc.verve.common.VervePreferences;
import gov.nasa.arc.verve.common.VerveTask;
import gov.nasa.arc.verve.common.interest.CenterOfInterest;
import gov.nasa.arc.verve.common.interest.CenterOfInterestCamera;
import gov.nasa.arc.verve.ui.e4.ImageRegistryKeeper;
import gov.nasa.arc.verve.ui.e4.node.WeakNode;
import gov.nasa.arc.verve.ui.e4.node.WeakSpatial;
import gov.nasa.arc.verve.ui.e4.view.SceneDetailView;
import gov.nasa.arc.verve.ui.e4.view.SceneGraphTreeViewPart;
import gov.nasa.arc.viz.scenegraph.task.CallbackCallable;
import gov.nasa.arc.viz.scenegraph.task.DetachAllChildrenTask;
import gov.nasa.arc.viz.scenegraph.task.DetachChildTask;
import gov.nasa.arc.viz.scenegraph.visitor.CheckBoundsVisitor;
import gov.nasa.ensemble.ui.databinding.util.MethodUtil;
import gov.nasa.util.ui.LastPath;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PerformanceStats;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;

public class SceneGraphTreePanel {
	private static Logger logger = Logger.getLogger(SceneGraphTreePanel.class);

	public static final int EXPAND_LIMIT = 500;
	protected static int s_expandCount = 0;

	protected Composite m_composite;
	protected SpatialCheckboxTreeViewer m_treeViewer; 
	
	 ImageRegistryKeeper m_irk;
	
	protected ICheckStateProvider m_checkStateProvider = new ICheckStateProvider() {
		
		@Override
		public boolean isChecked(Object element) {

			//System.out.println("CHECKING " + element.toString() + " THREAD: " + Thread.currentThread().getName());
			if (element instanceof WeakSpatial){
				return !((WeakSpatial)element).isCullAlways();
			} else if (element instanceof Spatial){
				return !WeakSpatial.isCullAlways((Spatial)element);
			}
			return false;
		}

		@Override
		public boolean isGrayed(Object element) {
			return false;
		}

	};

	protected SceneGraphTreeContentProvider m_contentProvider;

	protected FileDialog m_fileDialog;
	protected DirectoryDialog m_directoryDialog;
	protected String lastFilePath = LastPath.get(SceneGraphTreePanel.class);

	protected Action m_showAction;
	protected Action m_hideAction;
	protected Action m_seekAction;
	protected Action m_expandAction;
	protected Action m_refreshAction;
	protected Action m_collapseAction;
	protected Action m_viewBoundsAction;
	protected Action m_showDetailViewAction;

	protected SceneGraphTreeViewPart m_viewPart;
	protected SpatialSelectionProvider m_spatialSelectionProvider;

	protected boolean m_showBoundsOnSelect = false;

	public SceneGraphTreePanel(SceneGraphTreeViewPart viewPart, ImageRegistryKeeper irk) {
		m_viewPart  = viewPart;
		m_irk = irk;
		createActions();
	}

	//---------------------------------------------------------------
	public void createPanel(Composite parent) {
		m_showBoundsOnSelect = VervePreferences.isShowBoundsOnSelect();
		initializePropertyChangeListeners();

		m_composite = new Composite(parent, SWT.NONE);
		m_composite.setLayout(new FillLayout());
		m_treeViewer = new SpatialCheckboxTreeViewer(m_composite, SWT.MULTI);

		m_treeViewer.setComparer(new IElementComparer() {

			@Override
			public boolean equals(Object a, Object b) {
				if(a != null && b != null) {
					if (a instanceof WeakSpatial){
						return a.equals(b);
					} else {
						return b.equals(a);
					}
				}
				return false;
			}

			@Override
			public int hashCode(Object element) {
				if (element != null){
					return element.hashCode();
				}
				return 0;
			}

		});
		m_treeViewer.setUseHashlookup(true);
		m_treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		m_contentProvider =  new SceneGraphTreeContentProvider(m_treeViewer);
		m_treeViewer.setContentProvider(m_contentProvider);
		m_treeViewer.setLabelProvider(new SceneGraphTreeLabelProvider());
		m_treeViewer.setCheckStateProvider(m_checkStateProvider);

//		m_treeViewer.setSorter(new ViewerSorter());
		m_treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent arg0) {
				IStructuredSelection iss = (IStructuredSelection)arg0.getSelection();
				CheckboxTreeViewer tv = (CheckboxTreeViewer)arg0.getViewer();
				Object element = iss.getFirstElement();
				tv.setExpandedState(element, !tv.getExpandedState(element));
			}

		});
		m_treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateActionEnablement();
				showSelectionProperties(event);
			}
		});

		m_treeViewer.addCheckStateListener(new org.eclipse.jface.viewers.ICheckStateListener() {
			public void checkStateChanged(org.eclipse.jface.viewers.CheckStateChangedEvent event) {		          
				//if checked item is not the same as selected item, change selected item and set m_spatial 
				if(getCurrentSelection() == null || !getCurrentSelection().equals(event.getElement())){
					m_treeViewer.setSelection(new StructuredSelection(event.getElement()), true ); 
				}
				showSelection(event.getChecked());
			}
		});

		m_treeViewer.getTree().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent arg0) {
				//
			}

			public void keyReleased(KeyEvent arg0) {
				if (arg0.keyCode == SWT.DEL) {
					deleteSelection();
				}

			}

		});
		parent.layout();
		m_treeViewer.setAutoExpandLevel(3);

		createToolTipListener();

		m_spatialSelectionProvider = new SpatialSelectionProvider();
		m_treeViewer.addSelectionChangedListener(m_spatialSelectionProvider);
	}

	protected void createToolTipListener() {

		final Tree tree = m_treeViewer.getTree();
		final Display display = Display.getCurrent();

		// Implement a "fake" tooltip
		final Listener labelListener = new Listener () {
			public void handleEvent (Event event) {
				Label label = (Label)event.widget;
				Shell shell = label.getShell ();
				switch (event.type) {
				case SWT.MouseDown:
					Event e = new Event ();
					e.item = (TreeItem) label.getData ("_TABLEITEM");
					// Assuming table is single select, set the selection as if
					// the mouse down event went through to the table
					tree.setSelection (new TreeItem [] {(TreeItem) e.item});
					tree.notifyListeners (SWT.Selection, e);
					shell.dispose ();
					tree.setFocus();
					break;
				case SWT.MouseExit:
					shell.dispose ();
					break;
				}
			}
		};


		Listener tableListener = new Listener () {
			Shell tip = null;
			Label label = null;
			public void handleEvent (Event event) {
				switch (event.type) {
				case SWT.Dispose:
				case SWT.KeyDown:
				case SWT.MouseMove: {
					if (tip == null) break;
					tip.dispose ();
					tip = null;
					label = null;
					break;
				}
				case SWT.MouseHover: {
					TreeItem item = tree.getItem (new Point (event.x, event.y));
					if (item != null ) {

						if (tip != null  && !tip.isDisposed ()) tip.dispose ();
						Object data = item.getData();
						if (data != null){
							try {
								Method method = MethodUtil.getGetMethod(data.getClass(), "description");
								Object result = method.invoke(data, (Object[])null);
								if (result != null && result instanceof String && ((String)result).length() > 0) {
									tip = new Shell (m_composite.getShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
									tip.setBackground (display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
									FillLayout layout = new FillLayout ();
									layout.marginWidth = 2;
									tip.setLayout (layout);
									label = new Label (tip, SWT.NONE);
									label.setForeground (display.getSystemColor (SWT.COLOR_INFO_FOREGROUND));
									label.setBackground (display.getSystemColor (SWT.COLOR_INFO_BACKGROUND));
									label.setData ("_TABLEITEM", item);
									label.setText ((String)result);

									label.addListener (SWT.MouseExit, labelListener);
									label.addListener (SWT.MouseDown, labelListener);
									Point size = tip.computeSize (SWT.DEFAULT, SWT.DEFAULT);
									Rectangle rect = item.getBounds (0);
									Point pt = tree.toDisplay (rect.x, rect.y);
									tip.setBounds (pt.x, pt.y, size.x, size.y);
									tip.setVisible (true);
								}
							} catch (NoSuchMethodException nsme){
								//ignored
							} catch (IllegalArgumentException e) {
								//ignored
							} catch (IllegalAccessException e) {
								//ignored
							} catch (InvocationTargetException e) {
								//ignored
							}

						}
					}
				}
				}
			}
		};
		tree.addListener (SWT.Dispose, tableListener);
		tree.addListener (SWT.KeyDown, tableListener);
		tree.addListener (SWT.MouseMove, tableListener);
		tree.addListener (SWT.MouseHover, tableListener);

	}

	protected void updateActionEnablement() {
		List<WeakSpatial> spatials =  getCurrentSelection();
		boolean hasNode = false;
		boolean hasHidden = false;
		boolean hasVisible = false;
		for (WeakSpatial s : spatials){
			if (s.isNode() && s.hasChildren()) {
				hasNode = true;
				break;
			}
		}
		for (WeakSpatial node : spatials){
			if (!node.isCullAlways()) {
				hasVisible = true;
			} else {
				hasHidden = true;
			}
		}
		m_expandAction.setEnabled(hasNode);
		m_refreshAction.setEnabled(hasNode);
		m_showAction.setEnabled(hasHidden);
		m_hideAction.setEnabled(hasVisible);
	}

	/**
	 * TODO implement
	 * @param event
	 */
	protected void showSelectionProperties(SelectionChangedEvent event) {
		//IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		//InfoPanel.setSpatial(spatial);
		if(m_showBoundsOnSelect) {
			viewSelectionBounds();
		}
	}

	public void refreshExpansion(final WeakSpatial root){

		final Job refreshExpansionJob = new Job("Refreshing Expansion"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Display.getDefault().asyncExec(new Runnable(){
					public void run() {
						Object[] expanded =  getTreeViewer().getExpandedElements();
						if(expanded.length != 0) {
							getTreeViewer().setExpandedElements(expanded);
						} else {
							getTreeViewer().expandToLevel(getTreeViewer().getAutoExpandLevel());
						}
						getTreeViewer().refresh(root);

						done(Status.OK_STATUS);
					}
				});
				return Job.ASYNC_FINISH; 
			}
		};
		refreshExpansionJob.setPriority(Job.LONG);
		refreshExpansionJob.setSystem(true);
		refreshExpansionJob.setUser(false);
		refreshExpansionJob.schedule();
	}




	//	public static void refresh() {
	//		if (s_instance == null)
	//			return;
	//		if (Thread.currentThread() == Display.getDefault().getThread()) {
	//			s_instance.m_treeViewer.setInput(s_instance);
	//		}
	//		else {
	//			Display.getDefault().asyncExec(new Runnable() {
	//				public void run() {
	//					s_instance.m_treeViewer.setInput(s_instance);
	//				}
	//			});
	//			Thread.yield();
	//		}
	//	}

	//need to implement the method since viewer.getVisibleExpandedElements() 
	//won't return all visible elements (it returns only parents
	//and not the children of the parents)
	//method returns parents and their children if they are visible.  
	@SuppressWarnings("unused")
    private List<WeakSpatial> getAllVisibleTreeItems() {
		List<WeakSpatial> result = new ArrayList<WeakSpatial>();   
		fillVisibleSpatials(result, m_treeViewer.getTree());
		return result;
	}

	@SuppressWarnings("null")
    private void fillVisibleSpatials(List<WeakSpatial> result, Widget widget ) {	    
		TreeItem[] items = 
			widget instanceof TreeItem ? 
					((TreeItem)widget).getItems() : widget instanceof Tree ? 
							((Tree)widget).getItems() : null ;
							TreeItem item; 
							for(int i=0; i<items.length; i++) {
								item = items[i];
								if(item.getData() != null) {
									result.add((WeakSpatial)item.getData());              
								}           
								if( item.getExpanded() ) {
									fillVisibleSpatials(result, item);
								}
							}
	}

	public void seekSelection() {
		WeakSpatial spatial = getFirstSelection();
		Spatial contents = spatial.getSpatial();
		if (contents != null){
			CenterOfInterestCamera c = CenterOfInterest.getCamera();
			BoundingVolume b = contents.getWorldBound();
			ReadOnlyVector3 ctr = contents.getWorldTranslation();
			if (b != null) {
				Vector3 off = b.getCenter().subtract(ctr, new Vector3());
				c.setCenterOfInterest(contents, off);
			} else {
				c.setCenterOfInterest(contents, new Vector3());
			}
		}
	}

	public void viewSelectionBounds() {
		List<WeakSpatial> selection = getCurrentSelection();
		if(selection.size() > 0) {
			HashSet<Spatial> selectSet = new HashSet<Spatial>();
			for (WeakSpatial ws : selection){
				Spatial spatial = ws.getSpatial();
				if (spatial != null){
					selectSet.add(spatial);
				}
			}
			boolean sameSet = selectSet.equals(VerveDebug.getDebugBoundList());
			VerveDebug.getDebugBoundList().clear();
			if(!sameSet) {
				VerveDebug.getDebugBoundList().addAll(selectSet);
			}
		}
	}

	public void showSelection( boolean show) {
		for (WeakSpatial spatial : getCurrentSelection()){
			if (show){
				spatial.setCullHint(CullHint.Inherit);      
				// XXX FIXME XXX
				logger.debug("warning: blindly setting pick hints to enabled");
			} else {
				spatial.setCullHint(CullHint.Always);
			}
			spatial.setAllPickingHints(show);
			m_treeViewer.setChecked(spatial, show); 
		}
	}


	public void deleteSelection() {
		final List<WeakSpatial> spatials = getCurrentSelection();
		StringBuffer message = new StringBuffer(
				"Are you sure you want to delete the following: \n");
		for (WeakSpatial spatial : spatials) {
			message.append(spatial.getClass().getSimpleName());
			message.append(" \'");
			message.append(spatial.getName());
			message.append("\'\n");
		}

		if (IrgUI.confirmDialog("Confirm Delete", message.toString())) {

			for (WeakSpatial s : spatials){
				final Spatial childNode = s.getSpatial();
				if (childNode != null){
					final WeakNode parent = s.getParent();
					final Node parentNode = parent.getNode();
					if (parentNode != null){
						VerveTask.asyncExec(new CallbackCallable<Integer>(
								new DetachChildTask(parentNode, childNode)) {
							@Override
							protected void done() {
								SceneGraphTreePanel.this.refresh(parentNode, false, false);
							}
						});
					}
				}
			}
		}

		spatials.clear();

	}

	/**
	 * After confirm, immediately delete children of all selected nodes.
	 * This is not undoable.
	 */
	public void deleteSelectionChildren() {
		final List<WeakSpatial> spatials = getCurrentSelection();
		final List<WeakNode> nodes = new ArrayList<WeakNode>();
		StringBuffer message = new StringBuffer("Are you sure you want to delete all children of the following: \n");
		for (WeakSpatial spatial : spatials){
			if (spatial instanceof WeakNode) {
				message.append(spatial.getClass().getSimpleName());
				message.append(" \'");
				message.append(spatial.getName());
				message.append("\'\n");
				nodes.add((WeakNode)spatial);
			}
		}

		if(IrgUI.confirmDialog("Confirm Delete", message.toString())) {
			for (final WeakNode node : nodes){
				Node theNode = node.getNode();
				if (theNode != null){
					VerveTask.asyncExec(new CallbackCallable<Boolean>(new DetachAllChildrenTask(theNode)){
						@Override
						protected void done() {
							SceneGraphTreePanel.this.refresh(node.getNode(), false, false);
						}
					});
				}
			}
		}
	}

	/**
	 * Expand the selected children.
	 * This runs a job which in turn calls an async exec.   
	 * @param expanded
	 */
	public void expandSelectionChildren(final boolean expanded, final int depth) {

		final IStructuredSelection iss = (IStructuredSelection)getTreeViewer().getSelection();

		final Job expandChildrenJob = new Job("Expanding Children"){

			@Override
			public IStatus run(final IProgressMonitor monitor) {
				for (final Object node : iss.toList()) {
					if (expanded){
						m_treeViewer.expandToLevel(node, TreeViewer.ALL_LEVELS, monitor, this);
					} else {
						Display.getDefault().asyncExec(new Runnable(){
							public void run() {
								m_treeViewer.collapseToLevel(node, 0);
								done(Status.OK_STATUS);
							}
						});
					}
				}

				return Job.ASYNC_FINISH;
			}
		};

		expandChildrenJob.setPriority(Job.LONG);
		expandChildrenJob.setUser(true);
		expandChildrenJob.schedule();

		JobTimeout.killAfterTimeout(expandChildrenJob, 4000);
	}




	/**
	 * Collapse all children.
	 */
	public void collapseAll(){

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Object root = m_treeViewer.getVisibleExpandedElements()[0];
				m_treeViewer.collapseAll();
				m_treeViewer.expandToLevel(root, 2);
			}
		});
	}

	/**
	 * XXX THIS IS A QUICK HACK FOR DEBUGGING PURPOSES ONLY
	 */
	public void printSelection() {
		List<WeakSpatial> spatials = getCurrentSelection();
		for (WeakSpatial ws : spatials){
			Spatial spatial = ws.getSpatial();
			if (spatial != null){
				CheckBoundsVisitor cbv = new CheckBoundsVisitor();
				spatial.acceptVisitor(cbv, false);
				ReadOnlyTransform t = spatial.getTransform();
				BoundingVolume wb = spatial.getWorldBound();
				String msg = String.format(
						"Name = %s\n"+
						"transform = %s\n"+
						"world bound center = [X=%.2f Y=%.2f Z=%.2f], volume = %.2f",
						spatial.getName(), t.toString(), 
						wb.getCenter().getX(), wb.getCenter().getY(), wb.getCenter().getZ(), wb.getVolume());
				System.err.println(msg);
			}
		}
	}

	/**
	 * 
	 * @return a new list of currently selected items
	 */
	public List<WeakSpatial> getCurrentSelection() {
		IStructuredSelection iss = (IStructuredSelection)getTreeViewer().getSelection();
		List<WeakSpatial> result = new ArrayList<WeakSpatial>();
		for (Object o : iss.toList()){
			if (o instanceof WeakSpatial){
				result.add((WeakSpatial)o);
			} else {
				System.out.println("SELECTED CLASS " + o.getClass().getSimpleName());
			}
		}
		return result;
	}

	/**
	 * @return the first selected weak spatial
	 */
	public WeakSpatial getFirstSelection() {
		IStructuredSelection iss = (IStructuredSelection)getTreeViewer().getSelection();
		for (Object o : iss.toList()){
			if (o instanceof WeakSpatial){
				return (WeakSpatial)o;
			}
		}
		return null;
	}

	public TreeViewer getTreeViewer() {
		return m_treeViewer;
	}

	/**
	 * Fill the context menu
	 * @param mgr
	 */
	//---------------------------------------------------------------
	public void fillContextMenu(IMenuManager mgr) {
		try {

			mgr.add(m_showAction);
			mgr.add(m_hideAction);

			mgr.add(m_seekAction);
			mgr.add(new Separator());


			mgr.add(new Separator());
			mgr.add(m_refreshAction);
			mgr.add(m_expandAction);
			mgr.add(m_collapseAction);

			mgr.add(new Separator());
			mgr.add(m_viewBoundsAction);
			mgr.add(m_showDetailViewAction);

			mgr.add(new Separator());
			// 10/23/14 just killed today, see how bad it is
			//mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		}
		catch(Throwable t) {
			t.printStackTrace();
		}
	}

	protected void createActions() {
		m_showAction = new Action("Show", m_irk.getImageDescriptorFromRegistry("eyeball")) {
			@Override
			public void run() {
				showSelection(true);
			}
		};
		m_hideAction = new Action("Hide", m_irk.getImageDescriptorFromRegistry("eyeball_disabled")) {
			@Override
			public void run() {
				showSelection(false);
			}
		};
		m_seekAction = new Action("Seek", m_irk.getImageDescriptorFromRegistry("target")) {
			@Override
			public void run() {
				seekSelection();
			}
		};

		
		
//		m_deleteAction = new Action("Delete", 
//				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)) {
//			@Override
//			public void run() {
//				deleteSelection();
//			}
//
//		};
//		m_deleteChildrenAction = new Action("Delete All Children", 
//				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE)) {
//			@Override
//			public void run() {
//				deleteSelectionChildren();
//			}
//		};
//		m_printAction = new Action("Print (dbug)", 
//				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT)) {
//			@Override
//			public void run() {
//				printSelection();
//			}
//		};
		m_expandAction = new Action("Expand All Children", m_irk.getImageDescriptorFromRegistry("expandall")) {
			@Override
			public void run() {

				expandSelectionChildren(true,  AbstractTreeViewer.ALL_LEVELS);
			}
		};
		m_collapseAction = new Action("Collapse All Children", m_irk.getImageDescriptorFromRegistry("collapseall")) {
			@Override
			public void run() {
				expandSelectionChildren(false, AbstractTreeViewer.ALL_LEVELS);
			}
		};

		m_refreshAction = new Action("Refresh Children", m_irk.getImageDescriptorFromRegistry("arrow_circle")) {

			@Override
			public void run() {
				final IStructuredSelection iss = (IStructuredSelection)getTreeViewer().getSelection();
				refresh(iss.toList(), true, false);
			}
		};

		m_viewBoundsAction = new Action("View Bounds", m_irk.getImageDescriptorFromRegistry("bounds")) {
			@Override
			public void run() {
				viewSelectionBounds();
			}
		};

		m_showDetailViewAction = new Action("Show Detail View", m_irk.getImageDescriptorFromRegistry("detail_view")){
			@Override
			public void run() {
				WeakSpatial first = getFirstSelection();
				if (first != null){
					Spatial spatial = first.getSpatial();
					if (spatial != null){
						SceneDetailView.showUnpinnedView(spatial);
					}
				}
			}
		};

	}

	/**
	 * Look up a WeakSpatial that is in the tree
	 * @param spatial the spatial contained in the weak spatial
	 * @return the weak spatial, or null if not found
	 */
	public WeakSpatial getWeakSpatial(Spatial spatial){
		Object found = m_treeViewer.getMappedElement(spatial);
		if (found != null && found instanceof WeakSpatial){
			return (WeakSpatial)found;
		}
		return null;
	}

	public void refresh(Spatial spatial, boolean updateLabels, boolean expand){
		List<WeakSpatial> rootlist = new ArrayList<WeakSpatial>();
		rootlist.add(getWeakSpatial(spatial));
		refresh(rootlist, updateLabels, expand);
	}

	/**
	 * Update the checks of the selected elements
	 * This runs a job which in turn calls an async exec.   
	 * @param expand TODO
	 */
	public void refresh(final List<WeakSpatial> selected, final boolean updateLabels, final  boolean expand) {

		final Job expandChildrenJob = new Job("Expanding Children"){

			@Override
			public IStatus run(final IProgressMonitor monitor) {
				Display.getDefault().asyncExec(new Runnable(){
					public void run() {
						for (final WeakSpatial ws : selected) {
							if (ws != null){
								m_treeViewer.refresh(ws, updateLabels);
								if (expand) {
									m_treeViewer.setExpandedState(ws, true);
								}
							}
						}
						done(Status.OK_STATUS);
					}
				});

				return Job.ASYNC_FINISH;
			}
		};

		expandChildrenJob.setPriority(Job.LONG);
		expandChildrenJob.setUser(true);
		expandChildrenJob.schedule();
	}

	protected void saveColladaFile() {
		if (m_directoryDialog == null) {
			m_directoryDialog = new DirectoryDialog(m_composite.getShell(), SWT.OPEN | SWT.SINGLE);
			m_directoryDialog.setText("COLLADA Directory Selection");
		}
		m_directoryDialog.setFilterPath(lastFilePath);
		if (m_directoryDialog.open() != null) {
			lastFilePath = m_directoryDialog.getFilterPath();
			//File file = new File(lastPath, fileDialog.getFileName());
			try {
				//				System.out.println("Saving files "+spatial.getName()+".kml and "+spatial.getName()+".dae . . .");
				//				Map<String, Object> params = new HashMap<String, Object>();
				//				params.put("Label", spatial.getName());
				//				PrintStream pStream = new PrintStream(new File(lastPath+"/"+spatial.getName()+".kml"));
				//				ColladaExporter ce = new ColladaExporter();
				//				KMLWriter kmlWriter = new KMLWriter();
				//				kmlWriter.beginKML(pStream, spatial.getName(), -1);
				//				ce.save((Node)spatial, lastPath+"/"+spatial.getName()+".dae", params);
				//				kmlWriter.writeModel(pStream, spatial.getName(), new Vector3d(0, 0, 1000), spatial.getName()+".dae", null);
				//				kmlWriter.endKML(pStream);
				//				System.out.println(". . . complete.");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public SceneGraphTreeContentProvider getContentProvider() {
		return m_contentProvider;
	}

	public ISelectionProvider getSpatialSelectionProvider() {
		return m_spatialSelectionProvider;
	}

	protected class SpatialSelectionProvider implements ISelectionProvider, ISelectionChangedListener {

		protected List<ISelectionChangedListener> m_selectionChangedListeners = new ArrayList<ISelectionChangedListener>();
		protected List<Spatial> m_contents = new ArrayList<Spatial>();
		protected StructuredSelection m_realSelection = null;

		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
			m_selectionChangedListeners.add(listener);
		}

		public ISelection getSelection() {
			if (m_treeViewer == null){
				return null;
			}
			return m_realSelection;
		}

		/**
		 * Update the current corrected selection of this tree viewer because its selection has changed
		 */
		protected void updateSelection() {
			m_contents.clear();
			IStructuredSelection tvSelection = (IStructuredSelection)m_treeViewer.getSelection();

			for (Object o : tvSelection.toList()){
				if (o instanceof WeakSpatial){
					WeakSpatial weak = (WeakSpatial)o;
					Spatial s = weak.getSpatial();
					if (s != null){
						m_contents.add(s);
					}
				}
			}

			m_realSelection =  new StructuredSelection(m_contents);
		}

		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			m_selectionChangedListeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			//
		}

		public void selectionChanged(SelectionChangedEvent event) {
			updateSelection();
			for (ISelectionChangedListener l : m_selectionChangedListeners){
				l.selectionChanged(event);
			}
		}

	}

	protected void initializePropertyChangeListeners() {
		IPropertyChangeListener listener; 
		listener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getProperty().equals(VervePreferences.P_DEBUG_SHOW_BOUNDS_ON_SELECT)) {
					m_showBoundsOnSelect = VervePreferences.isShowBoundsOnSelect();
					VerveDebug.getDebugBoundList().clear();
				}
			}
		};
//		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(listener);
	}

	/*
	 * 
	 * 
     public void refreshChecks(final WeakSpatial root){

        final Job refreshChecksJob = new Job("Refreshing Checks"){

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Display.getDefault().asyncExec(new Runnable(){
                    public void run() {
                    	updateChecks(root, true);
                        done(Status.OK_STATUS);
                    }
                });
                return Job.ASYNC_FINISH; 
            }
        };
        refreshChecksJob.setPriority(Job.LONG);
        refreshChecksJob.setSystem(true);
        refreshChecksJob.setUser(false);
        refreshChecksJob.schedule();
    }

    public void refreshChecks(final Spatial root){

        final Job refreshChecksJob = new Job("Refreshing Checks"){

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                Display.getDefault().asyncExec(new Runnable(){
                    public void run() {
                    	updateChecks(root, true);
                        done(Status.OK_STATUS);
                    }
                });
                return Job.ASYNC_FINISH; 
            }
        };
        refreshChecksJob.setPriority(Job.LONG);
        refreshChecksJob.setSystem(true);
        refreshChecksJob.setUser(false);
        refreshChecksJob.schedule();
    }
	 */

	/**
	 * Update checkmarks of a node and all its child spatials.
	 * Update one level, optionally recurse further.
	 * @param node
	 */
	/*
    protected void updateChecks(WeakSpatial node, boolean recursive){
        if (node == null){
            return;
        }
        m_treeViewer.setChecked(node, !node.isCullAlways());
        for (WeakSpatial child : node.getChildren()) {
            if (recursive){
                updateChecks(child, recursive);
            } else {
                // always do one level.
                m_treeViewer.setChecked(child, !child.isCullAlways());
            }
        }
    }
	 */

	/**
	 * Update checkmarks of a node and all its child spatials.
	 * Update one level, optionally recurse further.
	 * @param spatial
	 */
	/*
    protected void updateChecks(Spatial spatial, boolean recursive){
        if (spatial == null){
            return;
        }
        m_treeViewer.setChecked(spatial, !WeakSpatial.isCullAlways(spatial));
        if (spatial instanceof Node){
	        for (Spatial child : ((Node)spatial).getChildren()) {
	            if (recursive){
	                updateChecks(child, recursive);
	            } else {
	                // always do one level.
	                m_treeViewer.setChecked(child, !WeakSpatial.isCullAlways(child));
	            }
	        }
        }
    }*/

	/**
	 * Update the checks of the selected elements
	 * This runs a job which in turn calls an async exec.   
	 */
	/*
    public void updateChecks() {

        final IStructuredSelection iss = (IStructuredSelection)getTreeViewer().getSelection();

        final Job expandChildrenJob = new Job("Expanding Children"){

            @Override
            public IStatus run(final IProgressMonitor monitor) {
                for (final Object node : iss.toList()) {
                	Display.getDefault().asyncExec(new Runnable(){
                        public void run() {
                        	updateChecks((WeakSpatial)node, true);
                            done(Status.OK_STATUS);
                        }
                    });
                }

                return Status.OK_STATUS;
            }
        };

        expandChildrenJob.setPriority(Job.LONG);
        expandChildrenJob.setUser(true);
        expandChildrenJob.schedule();
    }
	 */

	/**
	 *  SpatialCheckboxTreeViewer exposes a way to look up the mapped element so we can resolve a WeakSpatial from a Spatial.
	 */
	protected class SpatialCheckboxTreeViewer extends CheckboxTreeViewer {

		public SpatialCheckboxTreeViewer(Composite parent, int style) {
			super(parent, style);
		}

		/**
		 * Get the mapped element that the tree holds based on comparer provided.
		 * @param element
		 * @return
		 */
		public Object getMappedElement(Object element) {
			Widget widget = findItem(element);
			if (widget instanceof Item) {
				return ((Item) widget).getData();
			}
			return null;
		}

		private static final String SCENE_GRAPH_VIEWER = "gov.nasa.arc.verve.ui/scene_graph_viewer";


		@Override
		protected void handleTreeExpand(TreeEvent event) {
			final PerformanceStats stats = PerformanceStats.getStats(SCENE_GRAPH_VIEWER, this);
			if (PerformanceStats.ENABLED){
				PerformanceStats.clear();
				stats.startRun();
			}
			// TODO Auto-generated method stub
			super.handleTreeExpand(event);

			// benchmarking: end gathering statistics
			if (PerformanceStats.ENABLED) {
				stats.endRun();
				System.out.println("simple expand time:" + stats.getRunningTime() + "ms");
			}
		}

		public void expandToLevel(final Object elementOrTreePath, final int level, final IProgressMonitor monitor, final Job job) {
			// benchmarking: gather statistics

			Display.getDefault().asyncExec(new Runnable(){
				public void run() {
					final PerformanceStats stats = PerformanceStats.getStats(SCENE_GRAPH_VIEWER, this);
					if (PerformanceStats.ENABLED){
						PerformanceStats.clear();
						stats.startRun();
					}

					//IStatus result = job.getResult();
					if (monitor.isCanceled() || job.getResult() != null){
						return;
					}
					if (checkBusy())
						return;
					Widget w = internalExpand(elementOrTreePath, true);
					if (monitor.isCanceled() ||  job.getResult() != null){
						return;
					}
					if (w != null) {
						internalExpandToLevel(w, level, monitor, job);
					}

					// benchmarking: end gathering statistics
					if (PerformanceStats.ENABLED) {
						stats.endRun();
						System.out.println(elementOrTreePath.toString() + " expand time:" + stats.getRunningTime() + "ms to level " + level);
					}
				}
			});

			//super.expandToLevel(elementOrTreePath, level);


		}

		protected void internalExpandToLevel(Widget widget, int level, IProgressMonitor monitor, Job job) {
			if (monitor.isCanceled() ||  job.getResult() != null){
				return;
			}
			// expand this widget
			internalExpandToLevel(widget, 1);
			if (monitor.isCanceled() ||  job.getResult() != null){
				return;
			}

			if (level == ALL_LEVELS || level > 1) {
				Item[] children = getChildren(widget);
				if (children != null) {
					int newLevel = (level == ALL_LEVELS ? ALL_LEVELS : level - 1);
					for (int i = 0; i < children.length; i++) {
						if (monitor.isCanceled() ||  job.getResult() != null){
							return;
						}
						internalExpandToLevel(children[i], newLevel, monitor, job);
					}
				}
			}
		}
	}
}
