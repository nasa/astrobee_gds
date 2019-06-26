/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.verve.freeflyer.workbench.parts.engineering;

import gov.nasa.arc.verve.freeflyer.workbench.utils.DiscoveredTopicsTree;
import gov.nasa.arc.verve.freeflyer.workbench.utils.DiscoveredTopicsTree.DiscoveredTopicsElement;

import java.util.Collections;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.TableWrapData;

// TODO make this into a checkbox tree like the SceneGraphPart
public class ReceivedTopicsPart {
	private CheckboxTreeViewer m_treeViewer;
	private DiscoveredTopicsTree m_discoveredTopicsTree = null;
	@Inject @Optional
	private RawRapidTelemetryPart m_rtdp;

	@Inject 
	public ReceivedTopicsPart(Composite parent, IEclipseContext iec) {
		createTreeSection(parent);
		iec.set(ReceivedTopicsPart.class, this);
	}

	public void refresh() {
		getTopicsTree();
		if(m_rtdp != null) {
			m_rtdp.clear();
		}
		m_treeViewer.refresh();
		m_treeViewer.expandToLevel(3);
	}

	private DiscoveredTopicsTree getTopicsTree() {
		if(m_discoveredTopicsTree == null) {
			m_discoveredTopicsTree = new DiscoveredTopicsTree();
		}
		m_discoveredTopicsTree.buildTheTree();
		return m_discoveredTopicsTree;
	}

	public void createTreeSection(Composite parent) {
		TableWrapData ld;

		m_treeViewer = new CheckboxTreeViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | 
				SWT.FULL_SELECTION);
		TreeViewerColumn tvc = new TreeViewerColumn(m_treeViewer, SWT.NONE);
		tvc.getColumn().setWidth(250);
		ld = new TableWrapData(TableWrapData.LEFT);
		ld.grabVertical = true;
		ld.grabHorizontal = true;
		ld.rowspan = 9;
		ld.heightHint = 350;
		ld.maxWidth = 200;
		m_treeViewer.getTree().setLayoutData(ld);

		m_treeViewer.setContentProvider( new CreateTreeContentProvider());
		m_treeViewer.setLabelProvider(new CreateTreeLabelProvider());
		m_treeViewer.setInput(getTopicsTree());
		m_treeViewer.expandToLevel(3);
		m_treeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				// If the item is checked . . .
				if (event.getChecked()) {
					if( event.getElement() instanceof DiscoveredTopicsElement) {
						DiscoveredTopicsElement el = (DiscoveredTopicsElement)event.getElement();
						if(m_rtdp != null) {
							boolean ans = m_rtdp.addTopic(el.getName(), el.getParent());
							if(!ans) {
								// we don't know about the MessageType so gray it out
								m_treeViewer.setGrayed(el, true);
							}
						}
					}
				} else {
					if( event.getElement() instanceof DiscoveredTopicsElement) {
						DiscoveredTopicsElement el = (DiscoveredTopicsElement)event.getElement();
						if(m_rtdp != null) {
							m_rtdp.removeTopic(el.getName(), el.getParent());
						}
					}
				}
			}
		});
	}

	protected class CreateTreeContentProvider implements ITreeContentProvider {
		@Override
		public void dispose() {
			// do nothing 
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput) {
			// do nothing 
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof DiscoveredTopicsTree){
				return ((DiscoveredTopicsTree)inputElement).getElements();
			} 
			// do we need for lower level stuff???
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof DiscoveredTopicsElement){
				return ((DiscoveredTopicsElement)parentElement).getChildren();
			}
			return Collections.EMPTY_LIST.toArray();
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof DiscoveredTopicsElement){
				return ((DiscoveredTopicsElement)element).getParent();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof DiscoveredTopicsElement){
				return ((DiscoveredTopicsElement)element).hasChildren();
			}
			return false;
		}
	}

	protected class CreateTreeLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			if( element instanceof DiscoveredTopicsElement) 
			{
				return ((DiscoveredTopicsElement)element).getName();
			} else if( element instanceof String) 
			{
				return ((String)element);
			} 
			return "--";
		}
	}
}
