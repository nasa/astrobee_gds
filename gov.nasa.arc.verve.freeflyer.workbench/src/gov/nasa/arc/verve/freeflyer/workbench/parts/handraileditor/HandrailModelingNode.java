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
package gov.nasa.arc.verve.freeflyer.workbench.parts.handraileditor;

import gov.nasa.arc.verve.ardor3d.e4.util.DeselectListenerRegistry;
import gov.nasa.arc.verve.ardor3d.e4.util.IDeselectListener;
import gov.nasa.arc.verve.common.PickInfo;
import gov.nasa.arc.verve.common.ScenePickListener;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.GlobalProperties;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

public class HandrailModelingNode extends Node implements ScenePickListener, IDeselectListener {
	
	private static Logger logger = Logger.getLogger(HandrailModelingNode.class);
	protected static HandrailModelingNode instance;
	protected IEclipseContext context;
	
	protected HandrailBuilder handrailBuilder;
	protected String name;
	protected List<HandrailModel> handrails;
	private HandrailModel prevHandrail;
	
	private Node handrailNode;
	private boolean isActive;
	
	@Inject @Optional
	protected HandrailFileManager handrailFileManager;
	
	@Inject
	public HandrailModelingNode (MApplication app) {
		super("HandrailModeler");
		instance = this;
		
		handrailNode = new Node("HandrailModelNode");
		attachChild(handrailNode);
		
		context = app.getContext();
		
		DeselectListenerRegistry.addListener(this);
	}
	
	public static HandrailModelingNode getStaticInstance() {
		if(instance == null) {
			logger.error("Handrail Modeler not created.");
		}
		return instance;
	}
	
	@Inject @Optional
	public void setHandrailBuilder(@Named(ContextNames.HANDRAIL_BUILDER) HandrailBuilder builder) {
		this.handrailBuilder = builder;
		handrailsChanged();
	}
	
	@Inject @Optional
	public void onTabChanged(@Active MCompositePart part) {
		if(part.getLabel() != null && part.getLabel().equals("Modeling")) {
			setActive(true);
		} else {
			setActive(false);
		}
	}
	
	private void handrailsChanged() {
		eraseTheHandrails();
		makeTheHandrails();
	}
	
	private void eraseTheHandrails() {
		handrailNode.detachAllChildren();
	}
	
	private void makeTheHandrails() {
		List<HandrailModel> handrails = handrailBuilder.getHandrails();
		for(HandrailModel handrail : handrails) {
			handrailNode.attachChild(handrail);
		}
	}
	
	public void addHandrail(HandrailModel handrail) {
		handrailNode.attachChild(handrail);
	}
	
	public void deleteHandrail(HandrailModel handrail) {
		for(Spatial spat : handrailNode.getChildren()) {
			if(spat instanceof HandrailModel) {
				HandrailModel hm = (HandrailModel) spat;
				if(hm.equals(handrail)) {
					handrailNode.detachChild(hm);
					break;
				}
			}
		}
		cleanupLastSelection();
	}
	
	public void setActive(boolean active) {
		isActive = active;
		if(!isActive) {
			cleanupLastSelection();
		}
	}
	
	private void setSelection(HandrailModel hm) {
		prevHandrail = hm.clone();
		
		HandrailModelingPart.getStaticInstance().setModel(hm);
		hm.addPropertyChangeListener(handrailFileManager);
		
		hm.setHighlighted(true);
		context.set(ContextNames.SELECTED_HANDRAIL, hm);
	}
	
	public HandrailModel[] cancelChanges() {
		HandrailModel[] transform = new HandrailModel[2];
		HandrailModel hm = (HandrailModel) context.get(ContextNames.SELECTED_HANDRAIL);
		transform[0] = hm;
		transform[1] = prevHandrail;
		cleanupLastSelection();
		return transform;
	}
	
	private void cleanupLastSelection() {
		HandrailModel oldModel = (HandrailModel) context.get(ContextNames.SELECTED_HANDRAIL);
		if(oldModel != null) {
			oldModel.setHighlighted(false);
			oldModel.removePropertyChangeListener(handrailFileManager);
			HandrailModelingPart hmp = context.get(HandrailModelingPart.class);
			if(hmp != null) {
				hmp.unbindUI();
				hmp.setModel(null);
			}
		}
		
		prevHandrail = null;
		context.set(ContextNames.SELECTED_HANDRAIL, null);
	}

	@Override
	public void processPick(PickInfo pickInfo) {
		if(!isActive) {
			return;
		}
		
		if(pickInfo.getSpatial() instanceof HandrailModel) {
			cleanupLastSelection();
			
			HandrailModel newModel = (HandrailModel) pickInfo.getSpatial();
			setSelection(newModel);
		}
	}

	@Override
	public void onDeselect() {
		cleanupLastSelection();
	}
	
}
