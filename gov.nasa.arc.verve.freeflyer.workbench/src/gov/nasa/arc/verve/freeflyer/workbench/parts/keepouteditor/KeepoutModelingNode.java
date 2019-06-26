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
package gov.nasa.arc.verve.freeflyer.workbench.parts.keepouteditor;

import gov.nasa.arc.verve.ardor3d.e4.util.DeselectListenerRegistry;
import gov.nasa.arc.verve.ardor3d.e4.util.IDeselectListener;
import gov.nasa.arc.verve.common.PickInfo;
import gov.nasa.arc.verve.common.ScenePickListener;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.Sphere;

public class KeepoutModelingNode extends Node implements ScenePickListener, IDeselectListener, PropertyChangeListener {

	private static Logger logger = Logger.getLogger(KeepoutModelingNode.class);
	protected static KeepoutModelingNode instance;
	protected IEclipseContext context;
	
	protected KeepoutBuilder keepoutBuilder;
	protected String name;
	
	private Node keepoutNode;
	private boolean isActive;
	private KeepoutBox prevKeepout;
		
	private Sphere upperCorner, lowerCorner;
	private MaterialState blueMat, greenMat;
	private float[] blue = new float[] { 0.0f, 0.0f, 1.0f };
	private float[] green = new float[] { (31f/255f), (117f/255f), 0.0f };
	
	private float alpha = 1.0f;
	private float diff = 0.5f; // was 0.2
	private float spec = 0.0f; // was 1.0
	private float emis = 0.5f; // was 0.3
	private float ambt = (spec+diff)/4;
	
	private final double r = .0625;
	private final int zSamples = 10;
	private final int rSamples = 30;
	
	@Inject @Optional
	protected KeepoutFileManager keepoutFileManager;
		
	@Inject
	public KeepoutModelingNode(MApplication app) {
		super("KeepoutModeler");
		instance = this;
		
		keepoutNode = new Node("KeepoutBoxesNode");
		attachChild(keepoutNode);
		context = app.getContext();
		
		makeBlueMaterial();
		upperCorner = new Sphere("UpperCornerSphere", zSamples, rSamples, r);
		upperCorner.setRenderState(blueMat);
		
		makeGreenMaterial();
		lowerCorner = new Sphere("LowerCornerSphere", zSamples, rSamples, r);
		lowerCorner.setRenderState(greenMat);
		
		DeselectListenerRegistry.addListener(this);
	}
	
	public static KeepoutModelingNode getStaticInstance() {
		if(instance == null) {
			logger.error("KeepoutModeler not created.");
		}
		return instance;
	}
	
	@Inject @Optional
	public void onTabChanged(@Active MCompositePart part) {
		if(part.getLabel() != null && part.getLabel().equals("Modeling")) {
			setActive(true);
		} else {
			setActive(false);
		}
	}
	
	@Inject @Optional
	public void setKeepoutBuilder(@Named(ContextNames.KEEPOUT_BUILDER_FOR_KEEPOUT_EDITOR) KeepoutBuilder builder) {
		this.keepoutBuilder = builder;
		keepoutChanged();
	}
	
	public void keepoutChanged() {
		eraseTheBoxes();
		makeTheBoxes();
	}
	
	private void eraseTheBoxes() {
		keepoutNode.detachAllChildren();
	}
	
	private void makeTheBoxes() {
		if(keepoutBuilder == null) {
			return;
		}
		
		name = keepoutBuilder.getName();
		List<KeepoutBox> keepouts = keepoutBuilder.getKeepouts();
		for(KeepoutBox keepout : keepouts) {
			keepoutNode.attachChild(keepout);
		}
	}
	
	public void addBox(KeepoutBox keepout) {
		keepoutNode.attachChild(keepout);
	}
	
	public void deleteBox(KeepoutBox keepout) {
		for(Spatial spat : keepoutNode.getChildren()) {
			if(spat instanceof KeepoutBox) {
				KeepoutBox kb = (KeepoutBox) spat;
				if(kb.equals(keepout)) {
					keepoutNode.detachChild(kb);
					break;
				}
			}
		}
	}
	
	public void hide() {
		if(keepoutNode.getParent() != null) {
			detachChild(keepoutNode);
		}
	}
	
	public void show() {
		if(keepoutNode.getParent() == null) {
			attachChild(keepoutNode);
		}
	}
	
	public void setActive(boolean active) {
		isActive = active;
		if(!isActive) {
			hide();
			cleanupLastSelection();
		} else {
			show();
		}
	}
	
	public KeepoutBox[] cancelChanges() {
		KeepoutBox[] transform = new KeepoutBox[2];
		KeepoutBox selectedBox = (KeepoutBox) context.get(ContextNames.SELECTED_KEEPOUT);
		transform[0] = selectedBox;
		transform[1] = prevKeepout;
		cleanupLastSelection();
		return transform;
	}
	
	private void cleanupLastSelection() {
		KeepoutBox oldBox = (KeepoutBox) context.get(ContextNames.SELECTED_KEEPOUT);
		if(oldBox != null) {
			oldBox.setHighlight(false);
			oldBox.removePropertyChangeListener(keepoutFileManager);
			oldBox.removePropertyChangeListener(this);
			KeepoutModelingPart kmp = context.get(KeepoutModelingPart.class);
			if(kmp != null) {
				kmp.unbindUI();
				kmp.setModel(null);
			}
		}
		
		detachChild(upperCorner);
		detachChild(lowerCorner);
		prevKeepout = null;
		context.set(ContextNames.SELECTED_KEEPOUT, null);
	}
	
	public Node getBoxesNode() {
		return keepoutNode;
	}
	
	public void updateCorners(double[] bounds) {
		upperCorner.setTranslation(bounds[0], bounds[1], bounds[2]);
		lowerCorner.setTranslation(bounds[3], bounds[4], bounds[5]);
	}

	@Override
	public void processPick(PickInfo pickInfo) {
		if(!isActive) {
			return;
		}
		
		if(pickInfo.getSpatial() instanceof KeepoutBox) {
			cleanupLastSelection();
			
			KeepoutBox newBox = (KeepoutBox) pickInfo.getSpatial();
			KeepoutModelingPart.getStaticInstance().setModel(newBox);
			prevKeepout = newBox.clone();
			newBox.addPropertyChangeListener(keepoutFileManager);
			newBox.addPropertyChangeListener(this);
			attachChild(upperCorner);
			attachChild(lowerCorner);
			updateCorners(newBox.getBounds());
			
			newBox.setHighlight(true);
			context.set(ContextNames.SELECTED_KEEPOUT, newBox);
		}
	}
	
	@Override
	public void onDeselect() {
		cleanupLastSelection();
	}
	
	private void makeBlueMaterial() {			
		blueMat = new MaterialState();
		blueMat.setShininess(5); // was 5
		blueMat.setDiffuse (new ColorRGBA(diff*blue[0], diff*blue[1], diff*blue[2], alpha ));
		blueMat.setSpecular(new ColorRGBA(spec*blue[0], spec*blue[1], spec*blue[2], alpha ));
		blueMat.setEmissive(new ColorRGBA(emis*blue[0], emis*blue[1], emis*blue[2], alpha ));
		blueMat.setAmbient (new ColorRGBA(ambt*blue[0], ambt*blue[1], ambt*blue[2], alpha ));
	}
	
	private void makeGreenMaterial() {			
		greenMat = new MaterialState();
		greenMat.setShininess(5); // was 5
		greenMat.setDiffuse (new ColorRGBA(diff*green[0], diff*green[1], diff*green[2], alpha ));
		greenMat.setSpecular(new ColorRGBA(spec*green[0], spec*green[1], spec*green[2], alpha ));
		greenMat.setEmissive(new ColorRGBA(emis*green[0], emis*green[1], emis*green[2], alpha ));
		greenMat.setAmbient (new ColorRGBA(ambt*green[0], ambt*green[1], ambt*green[2], alpha ));
	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if(arg0.getPropertyName().equals("bounds")) {
			updateCorners((double[]) arg0.getNewValue());
		}
	}

}
