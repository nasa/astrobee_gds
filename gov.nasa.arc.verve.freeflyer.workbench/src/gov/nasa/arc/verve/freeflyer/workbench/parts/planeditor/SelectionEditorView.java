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
package gov.nasa.arc.verve.freeflyer.workbench.parts.planeditor;

import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.TypedObject;
import gov.nasa.ensemble.ui.databinding.databinding.BoundWidgetFactory;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class SelectionEditorView  {
	private static Logger logger = Logger.getLogger(SelectionEditorView.class);

	protected Sequenceable m_selected;
	protected AbstractDatabindingWidget m_currentCommandWidget;
	protected Composite m_parent;

	@Inject
	protected MApplication m_application;
	@Inject
	protected EPartService m_partService;
	private ClickToAddAdjustmentDialog clickToAddDialog;

	@Inject 
	public SelectionEditorView(Composite parent) {
		m_parent = parent;
	}

	@PostConstruct
	public void putClassInContext() {
		IEclipseContext context = m_application.getContext();
		context.set(SelectionEditorView.class, this);
	}

	public AbstractDatabindingWidget getCurrentCommandWidget() {
		return m_currentCommandWidget;
	}
	
	@PostConstruct
	public void hideClickToAddAdjustmentWidget() {
		if(clickToAddDialog != null){
			clickToAddDialog.close();
		}
	}

	public void showClickToAddAdjustmentWidget() {
		IEclipseContext context = m_application.getContext();
		clickToAddDialog = ContextInjectionFactory.make(ClickToAddAdjustmentDialog.class, context);
		clickToAddDialog.create();
		clickToAddDialog.setBlockOnOpen(false);
		clickToAddDialog.open();
	}

	@Inject @Optional
	public void setSelectedElement(TypedObject selected) {
		try {
			if (selected == null){
				if (m_currentCommandWidget != null){
					m_currentCommandWidget.dispose();
					m_currentCommandWidget = null;
					return;
				}
			} else {
				if (m_currentCommandWidget != null) {
					if (m_currentCommandWidget.isBound() && m_currentCommandWidget.getModel().getClass().equals(selected.getClass())){
						if (m_currentCommandWidget.getModel() != selected){
							m_currentCommandWidget.unbindUI();
							m_currentCommandWidget.setModel(selected);
						}
						return;
					} else {
						m_currentCommandWidget.dispose();
					}
				} 

				Class<? extends TypedObject> currentClass = selected.getClass();

				m_currentCommandWidget = BoundWidgetFactory.getWidget(currentClass, m_parent, SWT.NONE, false, false);
				m_currentCommandWidget.setVisible(true);
				m_parent.layout(true);
				m_currentCommandWidget.setModel(selected);
				m_parent.pack();
				m_parent.update();

				//			m_currentCommandWidget.addListener(m_widgetChangedListener);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
