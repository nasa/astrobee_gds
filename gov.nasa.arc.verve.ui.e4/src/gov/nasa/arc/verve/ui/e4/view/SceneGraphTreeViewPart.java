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
import gov.nasa.arc.verve.common.ISceneLoadListener;
import gov.nasa.arc.verve.common.IVerveScene;
import gov.nasa.arc.verve.common.SceneHack;
import gov.nasa.arc.verve.common.VerveTask;
import gov.nasa.arc.verve.common.node.INodeChangedListener;
import gov.nasa.arc.verve.ui.e4.ImageRegistryKeeper;
import gov.nasa.arc.verve.ui.e4.node.WeakNode;
import gov.nasa.arc.verve.ui.e4.node.WeakSpatial;
import gov.nasa.arc.verve.ui.e4.panel.tree.SceneGraphTreePanel;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 * All the extra button actions and things are broken, just the basic tree works
 */
public class SceneGraphTreeViewPart {

	public static String ID = SceneGraphTreeViewPart.class.getName();

	protected SceneGraphTreePanel m_sceneGraphTreePanel;
	protected SceneDetailView m_sceneDetailView;
	protected Action m_refreshAction;
	protected Action m_collapseAllAction;

	protected Text m_filterText;
	protected Action m_filterAction;
	protected Action m_loadAction;

	protected IVerveScene m_scene;

	ImageRegistryKeeper m_irk;
	
	@Inject
	protected EPartService m_partService;
	
	protected static SceneGraphTreeViewPart s_instance;

	/**
	 * The constructor.
	 */
	@Inject 
	public SceneGraphTreeViewPart(Composite parent) {
		m_irk = ImageRegistryKeeper.getInstance();
		createPartControl(parent);
		s_instance = this;
	}

	/**
	 * This is a callback that will allow us
	 * to create the view and initialize it.
	 */
	public void createPartControl(Composite parent) {
		m_sceneGraphTreePanel = new SceneGraphTreePanel(this, m_irk);
		m_sceneGraphTreePanel.createPanel(parent);
		makeToolbar();
		createContextMenu();
		initializeTreeContents();

		// XXX FIXME fix undo/redo later 10/23/14
		//UndoRedoUtil.createViewUndoRedo(getViewSite());
		SceneHack.addSceneLoadListener(new ISceneLoadListener() {
			public void onSceneLoaded(IVerveScene scene) {
				initializeTreeContents();
			}
		});

		initializeDetailView();

		// add a runnable that will get triggered by SceneHack.triggerSceneUpdate()
		SceneHack.addSceneUpdateRunnable(this.getClass().getSimpleName(), new Runnable() {
			@Override
			public void run() {
				SceneGraphTreeViewPart.refreshTree();
			}
		});

		SceneHack.addNodeChangeListener(this.getClass().getSimpleName(), new INodeChangedListener() {

			@Override
			public void nodeChanged(Node node) {
				SceneGraphTreeViewPart.refreshSpatialInTree(node);
			}

		});
	}

	protected void makeToolbar() {
		// XXX FIXME total hack to get it to compile 10/23/14
		IToolBarManager manager = new ToolBarManager();//getViewSite().getActionBars().getToolBarManager();
		addRefreshAction(manager);
		addCollapseAllAction(manager);
		addFilterAction(manager);
		manager.update(true);
	}

	/**
	 * Filter the contents of the tree based on this filter
	 * @param filter
	 */
	public void filter(String filter){
		Set<WeakSpatial> completeSet = null;

		if (m_scene != null && m_scene.getRoot() != null){
			if (!(filter == null || filter.length() == 0 || filter.equalsIgnoreCase("filter"))) {
				completeSet = new HashSet<WeakSpatial>();
				Set<WeakSpatial> result = new HashSet<WeakSpatial>();

				filter(new WeakNode(m_scene.getRoot()), filter, result);
				// add any missing parents in the set
				for (WeakSpatial s : result){
					completeSet.add(s);
					addParent(s, completeSet);
				}
			}
			m_sceneGraphTreePanel.getContentProvider().setFilterMatches(completeSet);
			m_sceneGraphTreePanel.getTreeViewer().refresh();
		}
	}

	/**
	 * Recursively add all the parents of a node to the result
	 * @param node
	 * @param result
	 */
	protected void addParent(WeakSpatial node, Set<WeakSpatial> result){
		if (node.getParent() != null){
			WeakNode parent = node.getParent();
			result.add(parent);
			addParent(parent, result);
		}
	}

	protected void addChildren(WeakSpatial node, Set<WeakSpatial> result){
		result.add(node);
		if (node instanceof WeakNode && ((WeakNode)node).getChildren() != null) {
			for (WeakSpatial child : ((WeakNode)node).getChildren()){
				addChildren(child, result);
			}
		}
	}

	protected void filter(WeakSpatial node, String filter, Set<WeakSpatial> result){
		if (node == null || node.getName() == null){
			return;
		}
		if (node.getName().toLowerCase().contains(filter.toLowerCase())){
			addChildren(node, result);
		} else if (node instanceof WeakNode){
			for (WeakSpatial child : ((WeakNode) node).getChildren()){
				filter(child, filter, result);
			}
		}
	}

	/**
	 * Add the open log action to the tool bar manager.
	 */
	protected void addRefreshAction(IToolBarManager toolBarManager) {
		m_refreshAction = new Action("refresh") {
			@Override
			public void run() {
				scheduleRefresh(true);
			}
		};
		m_refreshAction.setImageDescriptor(m_irk.getImageDescriptorFromRegistry("arrow_circle"));
		toolBarManager.add(m_refreshAction);
	}

	protected void addCollapseAllAction(IToolBarManager toolBarManager){
		m_collapseAllAction = new Action("collapse") {
			@Override
			public void run() {
				m_sceneGraphTreePanel.collapseAll();
			}
		};
		m_collapseAllAction.setImageDescriptor(m_irk.getImageDescriptorFromRegistry("collapseall"));
		toolBarManager.add(m_collapseAllAction);
	}

	protected void addFilterAction(IToolBarManager toolBarManager){
		m_filterAction = new Action("filter") {
			@Override
			public void run() {
				String filter = m_filterText.getText();
				filter(filter);
			}
		};
		toolBarManager.add(new ContributionItem("filter.text") {
			@Override
			public final void fill( ToolBar parent, int index ) {
				m_filterText = new Text( parent, SWT.SINGLE );
				m_filterText.setText( "Filter" );
				ToolItem ti = new ToolItem( parent, SWT.SEPARATOR, index );
				ti.setControl( m_filterText );
				ti.setWidth( 100 );
			}

		});

		m_filterAction.setImageDescriptor(m_irk.getImageDescriptorFromRegistry("filter"));
		toolBarManager.add(m_filterAction);
	}

	private void createContextMenu() {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(false);
		m_sceneGraphTreePanel.fillContextMenu(menuManager);

		Control viewerControl = m_sceneGraphTreePanel.getTreeViewer().getControl();
		Menu menu = menuManager.createContextMenu(viewerControl);
		viewerControl.setMenu(menu);
		//getSite().registerContextMenu("gov.nasa.arc.verve.ui.view.SceneGraphTreeViewPart.popup", menuManager,  m_sceneGraphTreePanel.getTreeViewer());
		// 10/23/14 just killed today, see how bad it is
		//		getSite().registerContextMenu(menuManager, m_sceneGraphTreePanel.getTreeViewer());
		//		getSite().setSelectionProvider(m_sceneGraphTreePanel.getTreeViewer());

	}

	/**
	 * This should only be called by this class or the SceneDetailView
	 */
	public void initializeDetailView() {
		// 10/23/14 just killed today, see how bad it is
		//		IWorkbenchPage page = getSite().getPage();
		//		IViewReference viewRef = page.findViewReference(SceneDetailView.ID);
		//		if (viewRef != null){
		//    		IViewPart foundView = viewRef.getView(false);
		//    		if (foundView != null && foundView instanceof SceneDetailView){
		//    			SceneDetailView sdv = (SceneDetailView)foundView;
		//    			if (getSceneGraphTreePanel() != null){
		//    				sdv.hookViewer(getSceneGraphTreePanel().getSpatialSelectionProvider());
		//    			}
		//    		}
		//		}
	} 

	public SceneGraphTreePanel getSceneGraphTreePanel() {
		return m_sceneGraphTreePanel;
	}

	public IVerveScene getScene() {
		return m_scene;
	}

	public void setScene(IVerveScene scene) {
		m_scene = scene;
	}

	public void initializeTreeContents() {
		setScene(SceneHack.getMainScene());
		if (m_scene != null){
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					m_sceneGraphTreePanel.getTreeViewer().setInput(m_scene);
					scheduleRefresh(true);
				}

			});
		}
	}

	/**
	 * TODO instead of this, make listeners.  have it refresh when something changes.
	 */
	public void scheduleRefresh(final boolean updateCheckboxes, final WeakSpatial root){
		if (m_scene == null){
			initializeTreeContents();
		}
		if (root != null){

			VerveTask.asyncExec(new java.util.concurrent.Callable<Boolean>() {
				public Boolean call() throws Exception {
					final SceneGraphTreePanel sgtp = m_sceneGraphTreePanel;
					if ( sgtp != null && sgtp.getTreeViewer() != null) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								sgtp.refreshExpansion(root);
							}
						});
						Thread.yield();
						return Boolean.TRUE;
					}
					return Boolean.FALSE;
				}
			});
		}
	}

	public void scheduleRefresh(final boolean updateCheckboxes){
		if (m_scene == null){
			initializeTreeContents();
		}
		if (m_sceneGraphTreePanel != null) {
			WeakNode aNode = m_sceneGraphTreePanel.getContentProvider().getWeakRoot();
			scheduleRefresh(updateCheckboxes, aNode);
		}
	}


	public static void refreshSpatial(final Spatial root, boolean updateLabels, boolean expand){
		SceneGraphTreeViewPart treeView = findPart();
		if (treeView != null){
			treeView.scheduleRefresh(root, updateLabels, expand);
		}		
	}

	/**
	 * Schedule a tree refresh of the given root
	 * @param root
	 * @param updateLabels TODO
	 * @param expand TODO
	 */
	public void scheduleRefresh(Spatial root, boolean updateLabels, boolean expand){
		m_sceneGraphTreePanel.refresh(root, updateLabels, expand);
	}

	/**
	 * Refresh the scene graph tree if the view is open.
	 */
	public static void refreshTree() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				SceneGraphTreeViewPart treeView = findPart();
				if (treeView != null){
					treeView.scheduleRefresh(true);
				}				
			}
		});
	}

	public static void refreshSpatialInTree(final Spatial spatial) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				SceneGraphTreeViewPart treeView = findPart();
				if (treeView != null){
					treeView.scheduleRefresh(spatial, true, true);
				}				
			}
		});
	}

	/**
	 * Find the scene graph tree view part.  returns null if not found.
	 * @return
	 */
	public static SceneGraphTreeViewPart findPart() {
		return s_instance;
//		IWorkbench bench = PlatformUI.getWorkbench();
//		if (bench != null){
//			IWorkbenchWindow window = bench.getActiveWorkbenchWindow();
//			if (window != null){
//				IWorkbenchPage page = window.getActivePage();
//				if (page != null){
//					IViewPart part = page.findView(SceneGraphTreeViewPart.ID);
//					if (part != null){
//						return (SceneGraphTreeViewPart)part;
//					}
//				}
//			}
//		}
//		return null;
	}
}
