package gov.nasa.arc.verve.ui.e4.panel.tree;
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

import gov.nasa.arc.verve.common.IVerveScene;
import gov.nasa.arc.verve.common.node.INodeChangedListener;
import gov.nasa.arc.verve.ui.e4.node.WeakNode;
import gov.nasa.arc.verve.ui.e4.node.WeakSpatial;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import com.ardor3d.scenegraph.Node;


/**
 * Tree content provider for the scene graph.  This expects a verve scene as a root and you can also set a set of nodes to filter (show)
 * @author tecohen
 *
 */
public class SceneGraphTreeContentProvider implements ITreeContentProvider, INodeChangedListener {
    @SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SceneGraphTreeContentProvider.class);

    IVerveScene m_scene;
    WeakNode m_weakRoot = null;
    Set<WeakSpatial> m_filterMatches = null;
    TreeViewer m_viewer;
    
    public SceneGraphTreeContentProvider(TreeViewer viewer){
    	super();
    	m_viewer = viewer;
    }
    
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof WeakNode) {
			List<WeakSpatial> allChildren = ((WeakNode)parentElement).getChildren();
			if (m_filterMatches != null) {
				List<WeakSpatial> result = new ArrayList<WeakSpatial>();
				for (WeakSpatial s : allChildren){
					if (m_filterMatches.contains(s) && !s.equals(parentElement)){
						result.add(s);
					}
					//s.addNodeChangeListener(this);
				}
				return result.toArray();
			}
			/*
			for (WeakSpatial s : allChildren){
				s.addNodeChangeListener(this);
			} */
			return allChildren.toArray();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
	    /*if (element instanceof Node) {
			return ((Node)element).getParent();
	    }*/
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if(element instanceof WeakNode)  {
			return ((WeakNode)element).hasChildren();
		} else if (element instanceof IVerveScene && ((IVerveScene)element).getRoot() != null) {
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (m_scene != null){
			if (m_weakRoot == null){
				m_weakRoot = new WeakNode(m_scene.getRoot());
			}
			return new Object[]{m_weakRoot};
		}
		return new Object[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof IVerveScene){
			m_scene = (IVerveScene)newInput;
		} 
	}

	/**
	 * @return the list of matches to the filter
	 */
	public Set<WeakSpatial> getFilterMatches() {
		return m_filterMatches;
	}

	/**
	 * set the list of filter matches
	 * @param filterMatches
	 */
	public void setFilterMatches(Set<WeakSpatial> filterMatches) {
		m_filterMatches = filterMatches;
	}

	// XXX FIXME XXX mallan 9/15/2010 is this supposed to be empty?
	public void dispose() {
		//
	}

	public void nodeChanged(final Node node) {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				if (m_viewer != null && node != null){
					m_viewer.refresh(node);
				}
			}
			
		});
		
	}

	public WeakNode getWeakRoot() {
		return m_weakRoot;
	}

	public void setWeakRoot(WeakNode weakRoot) {
		m_weakRoot = weakRoot;
	}

}
