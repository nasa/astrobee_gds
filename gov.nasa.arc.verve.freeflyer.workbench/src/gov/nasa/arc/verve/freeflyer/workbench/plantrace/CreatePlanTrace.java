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
package gov.nasa.arc.verve.freeflyer.workbench.plantrace;

import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.SequenceHolder;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.model.TypedObject;
import gov.nasa.arc.irg.plan.model.modulebay.Point3D;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.plan.ui.plancompiler.PlanCompiler;
import gov.nasa.arc.irg.plan.ui.plancompiler.TrajectoryBoundsCheck;
import gov.nasa.arc.verve.ardor3d.e4.util.DeselectListenerRegistry;
import gov.nasa.arc.verve.ardor3d.e4.util.IDeselectListener;
import gov.nasa.arc.verve.common.PickInfo;
import gov.nasa.arc.verve.common.ScenePickListener;
import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.common.ardor3d.interact.VerveInteractManagers;
import gov.nasa.arc.verve.common.ardor3d.interact.VerveInteractWidgets;
import gov.nasa.arc.verve.common.interact.VerveInteractable;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.AddViaMapTypedObject;
import gov.nasa.arc.verve.robot.freeflyer.parts.FreeFlyerBasicModel;
import gov.nasa.arc.verve.robot.freeflyer.plan.AbstractPlanTrace;
import gov.nasa.arc.verve.robot.freeflyer.plan.CollisionMarker;
import gov.nasa.arc.verve.robot.freeflyer.plan.SegmentModel;
import gov.nasa.arc.verve.robot.freeflyer.plan.StationModel;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.scenegraph.hint.CullHint;

public class CreatePlanTrace extends AbstractPlanTrace implements PropertyChangeListener, ScenePickListener, IDeselectListener{
	private static Logger logger = Logger.getLogger(AbstractPlanTrace.class);
	protected static CreatePlanTrace instance;
	//	protected HashSet<Vector3> m_numberedNodes;
	protected SequenceHolder selected;
	protected IEclipseContext applicationContext;
	protected PlanBuilder planBuilder;
	protected FreeFlyerBasicModel draggablePreviewModel;
	protected Node draggablePreviewNode;
	protected Node selectedStationNode;
	boolean draggablePreviewOnRoot;
	protected VerveInteractable moveXYZ, rotXYZ;
	protected boolean showTranslateDraggable = false;
	protected boolean showRotateDraggable = false;
	protected float DEG2RAD = (float) (Math.PI/180.0);
	protected float RAD2DEG = (float) (180.0/Math.PI);
	protected int roundTo = 1;// cm to round to
	protected float halfRoundTo = 0.5f;
	
	protected List<CollisionMarker> collisions = new ArrayList<CollisionMarker>();
	protected Node collisionsNode = new Node("CollisionsNode");
	protected TrajectoryBoundsCheck checker = PlanCompiler.getTrajectoryBoundsCheck();
	
	protected float EPSILON = 0.001f;

	@Inject
	public CreatePlanTrace(MApplication app) {
		super("CreatePlanTrace");
		instance = this;
		applicationContext = app.getContext();
		attachChild(collisionsNode);
		//		m_numberedNodes = new HashSet<Vector3>();
		DeselectListenerRegistry.addListener(this);

		createVerveInteractables();
		setupDraggablePreviewModel();
	}

	private void createVerveInteractables() {
		moveXYZ = createInteractableTranslation();
		rotXYZ = createInteractableRotation();
	}

	public boolean getShowTranslateDraggable() {
		return showTranslateDraggable;
	}
	
	public boolean getShowRotateDraggable() {
		return showRotateDraggable;
	}
	
	protected void resetDraggable() {
		// attach draggable Preview to this Station
		selectedStationNode = (Node) this.getChild(stationPrefix + selected.getName() + "Node");
		selectedStationNode.attachChild(draggablePreviewNode);
		draggablePreviewNode.getSceneHints().setCullHint(CullHint.Always);
		draggablePreviewNode.setTransform(new Transform());

		if(showTranslateDraggable) {
			VerveUserData.setInteractable(draggablePreviewNode, moveXYZ);
		} else if(showRotateDraggable) {
			VerveUserData.setInteractable(draggablePreviewNode, rotXYZ);
		} else {
			return;
		}
		VerveInteractManagers.INSTANCE.setSpatialTarget(draggablePreviewNode);
		markDirty(DirtyType.Attached);
	}

	protected float addAndRoundIfNecessary(float a, float b) {
		if(Math.abs(b)<EPSILON) {
			return a;
		}
		else if (Math.abs(a)<EPSILON) {
			return b;
		}
		return round(a + b);
	}

	protected void updateStationPositionFromRotation(ReadOnlyTransform xfm) {
		if(selected instanceof ModuleBayStation) {
			Point6Dof original = ((Station) selected).getCoordinate();

			Matrix3 originalRot = new Matrix3();
			originalRot.fromAngles(original.getRoll() * DEG2RAD,
					original.getPitch()	* DEG2RAD, 
					original.getYaw()	* DEG2RAD);

			ReadOnlyMatrix3 localRotationMat = xfm.getMatrix();
			ReadOnlyMatrix3 totalRotationMat = originalRot.multiply(localRotationMat, null);

			double[] totalRotationAngles = null;
			totalRotationAngles =  totalRotationMat.toAngles(totalRotationAngles);		

			// round to whole numbers
			float roll = (int) (totalRotationAngles[0] * RAD2DEG);
			float pitch = (int) (totalRotationAngles[1] * RAD2DEG);
			float yaw = (int) (totalRotationAngles[2] * RAD2DEG);

			// if we are rotating by hand, the Station should remember our rotations. no ignoreOrientation.
			Point6Dof p6d = new Point6Dof(original.getX(), original.getY(), original.getZ(), roll, pitch, yaw);

			// DelegateCommandStack should catch and make UpdateValueCommand
			applicationContext.set(ContextNames.NEW_STATION_LOCATION, p6d);

		}
	}

	protected void updateStationPositionFromTranslation(ReadOnlyTransform xfm) {
		if(selected instanceof ModuleBayStation) {
			Point6Dof original = ((Station) selected).getCoordinate();

			Matrix3 originalRot = new Matrix3();
			originalRot.fromAngles(original.getRoll() * DEG2RAD,
					original.getPitch()	* DEG2RAD, 
					original.getYaw()	* DEG2RAD);
			ReadOnlyVector3 localTranslation = xfm.getTranslation();

			Vector3 worldTranslation = originalRot.applyPost(localTranslation, null);

			float x = addAndRoundIfNecessary(original.getX(),worldTranslation.getXf());
			float y = addAndRoundIfNecessary(original.getY(),worldTranslation.getYf());
			float z = addAndRoundIfNecessary(original.getZ(),worldTranslation.getZf());

			Point6Dof p6d = new Point6Dof(x, y, z, original.getRoll(), original.getPitch(), original.getYaw());

			if(original instanceof ModuleBayPoint) {
				if(((ModuleBayPoint) original).isIgnoreOrientation()) {
					ModuleBayPoint newXYZonly = new ModuleBayPoint(p6d);
					newXYZonly.setIgnoreOrientation(true);
					applicationContext.set(ContextNames.NEW_STATION_LOCATION, newXYZonly);
					return;
				}
			}

			// DelegateCommandStack should catch and make UpdateValueCommand
			applicationContext.set(ContextNames.NEW_STATION_LOCATION, p6d);
		}
	}

	public static CreatePlanTrace getStaticInstance() {
		if(instance == null) {
			logger.error("CreatePlanTrace was not created");
		}
		return instance;
	}

	public void showTranslateDraggable() {
		showTranslateDraggable = true;
		showRotateDraggable = false;
		if(selected instanceof Station) {
			// XXX this is probably redundant - attach also detaches from parent
			if(draggablePreviewOnRoot) {
				// detach it from last point
				detachChild(draggablePreviewNode);
				draggablePreviewOnRoot = false;
			}

			// attach draggable Preview to this Station
			selectedStationNode = (Node) this.getChild(stationPrefix + selected.getName() + "Node");
			selectedStationNode.attachChild(draggablePreviewNode);
			draggablePreviewNode.getSceneHints().setCullHint(CullHint.Always);
			markDirty(DirtyType.Attached);
			VerveUserData.setInteractable(draggablePreviewNode, moveXYZ);
			VerveInteractManagers.INSTANCE.setSpatialTarget(draggablePreviewNode);
		}
	}

	public void showRotateDraggable() {
		showTranslateDraggable = false;
		showRotateDraggable = true;
		if(selected instanceof Station) {
			if(draggablePreviewOnRoot) {
				// detach it from last point
				detachChild(draggablePreviewNode);
				draggablePreviewOnRoot = false;
			}

			// attach draggable Preview to this Station
			selectedStationNode = (Node) this.getChild(stationPrefix + selected.getName() + "Node");
			selectedStationNode.attachChild(draggablePreviewNode);
			draggablePreviewNode.getSceneHints().setCullHint(CullHint.Always);
			markDirty(DirtyType.Attached);
			VerveUserData.setInteractable(draggablePreviewNode, rotXYZ);
			VerveInteractManagers.INSTANCE.setSpatialTarget(draggablePreviewNode);
		}
	}

	/** detach the node, but don't set show*Draggable to false */
	private void hideDraggableWithoutClearingMemory() {
		if(selectedStationNode != null) {
			selectedStationNode.detachChild(draggablePreviewNode);
			VerveInteractManagers.INSTANCE.setSpatialTarget(null);
			markDirty(DirtyType.Detached);
		}
	}

	public void hideDraggable() {
		showTranslateDraggable = false;
		showRotateDraggable = false;
		hideDraggableWithoutClearingMemory();
	}

	@Inject @Optional
	public void setPlanBuilder(@Named(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR) PlanBuilder<ModuleBayPlan> builder) {
		this.planBuilder = builder;
		if(builder != null) {	
			planChanged(builder.getPlan());
		} else {
			planChanged(null);
		}
	}
	
	@Inject @Optional
	public void updateCollisions(Vector<double[]> collisions) {
		this.collisions.clear();
		collisionsNode.detachAllChildren();
		if(collisions == null) {
			return;
		}
		
		if(!this.hasChild(collisionsNode)) {
			attachChild(collisionsNode);
		}
		
		int i = 0;
		for(double[] collision : collisions) {	
			Vector3 center = new Vector3(collision[0], collision[1], collision[2]);
			CollisionMarker marker = new CollisionMarker("Collision" + i++, center);
			this.collisions.add(marker);
			collisionsNode.attachChild(marker);
		}
	}

	// this is called by PreviewPlanViewer and RunPlanViewer (and setPlanBuilder)
	public void planChanged(Plan p) {
		eraseTheTrace();
		if(p == null) {
			plan = p;
			return;
		}
		if(plan != null) {
			if(!plan.equals(p)) {
				// ummm... will the propertyChangeListener just still be there?
				//m_plan.removePropertyChangeListener(this);
				plan.populate(p);
				plan.addPropertyChangeListener(this);
			}
		} else {
			plan = p;
			plan.addPropertyChangeListener(this);
		}
		drawTheTrace();
	}

	private void clearSelected() {
		if(selected != null) {
			if(selected instanceof Station) {
				labelUnhighlightedStation((Station)selected);
			} else if(selected instanceof Segment) {
				Node n = (Node) getChild( getLabelName(selected) + "Node" );
				if(n != null) {
					n.detachChildNamed( getLabelName(selected) );
				}
			}
			setUnhighlightedMaterial(selected);
		}
		selected = null;
	}

	@Override
	protected void eraseTheTrace() {
		super.eraseTheTrace();
		VerveInteractManagers.INSTANCE.setSpatialTarget(null);
		selected = null;
		collisions.clear();
		collisionsNode.detachAllChildren();
	}

	public Sequenceable translateNameToSequenceable(String spatialName) {
		String seqName;
		if(spatialName.startsWith(stationPrefix)) {
			seqName = spatialName.substring(stationPrefix.length());
			int seqNameNum;
			try {
				//seqNameNum = Integer.parseInt(seqName);
				seqNameNum = getIntFromString(seqName);
			} catch (Exception e) {
				// trim last letter
				//String trimmed = seqName.substring(0, seqName.length()-1);
				//seqNameNum = Integer.parseInt(trimmed);
				logger.error("Invalid item clicked");
				return null;
			}
			return plan.getSequenceable(seqNameNum*2);
		} 
		else if(spatialName.endsWith(arrowString)) {
			int dashIndex = spatialName.indexOf("-");
			String segmentNumber = spatialName.substring(segmentPrefix.length(), dashIndex);
			int segmentInt = Integer.parseInt(segmentNumber);
			return plan.getSequenceable(segmentInt*2 + 1);
		}
		//logger.error("thing clicked was not a station or segment");
		return null;
	}

	public void processPick(PickInfo pickInfo) {
		boolean ignoreClicksOnCreatePlanTrace = false;
		if(applicationContext.containsKey(ContextNames.ADD_VIA_MAP_ENABLED)) {
			ignoreClicksOnCreatePlanTrace = (boolean) applicationContext.get(ContextNames.ADD_VIA_MAP_ENABLED);
		}
		if(!isVisible || ignoreClicksOnCreatePlanTrace) {
			return;
		}
		Spatial sp = pickInfo.getSpatial();
		Sequenceable picked = translateNameToSequenceable(sp.getName());

		if(picked != null) {
			applicationContext.remove(TypedObject.class);
			applicationContext.set(TypedObject.class, (TypedObject)picked);
		}
	}

	protected void setupDraggablePreviewModel() {
		draggablePreviewModel = new FreeFlyerBasicModel("white");
		draggablePreviewModel.setScale(.99);
		draggablePreviewNode = new Node("DraggablePreview");
		draggablePreviewNode.attachChild(draggablePreviewModel);
		draggablePreviewNode.updateWorldTransform(true);
//		draggablePreviewNode.updateWorldBound(true);
		draggablePreviewNode.getSceneHints().setCullHint(CullHint.Always);
		attachChild(draggablePreviewNode); // attach it so it gets its geometry updated
//		this.updateWorldTransform(false);
		draggablePreviewOnRoot = true;

		VerveUserData.setInteractable(draggablePreviewNode, moveXYZ);
		VerveUserData.setCameraFollowable(draggablePreviewNode, false);
	}

	protected VerveInteractable createInteractableTranslation() {
		return new VerveInteractable() {
			{ 
				m_preferredWidgets = new String[] { VerveInteractWidgets.MoveXYZ }; 
			}

			@Override
			public String getDisplayString(ReadOnlyTransform transform) {
				ReadOnlyVector3 xyz = transform.getTranslation();
				// This gives me coordinates in robot frame. I want them in world frame.
				Quaternion beeRotation = AggregateAstrobeeState.getInstance().getPositionGds().getQuaternion();
				Vector3 rotatedXyz = beeRotation.apply(xyz, null);

				//translateFromDrag(rotatedXyz);
				return String.format("xyz=[ %.2f, %.2f, %.2f]", rotatedXyz.getX(), rotatedXyz.getY(), rotatedXyz.getZ());
			}

			@Override
			public void beginInteractDrag(Spatial spatial) {
				draggablePreviewNode.getSceneHints().setCullHint(CullHint.Inherit);
			}

			@Override
			public void endInteractDrag(Spatial spatial) {
				// set the position of the Station to the dragged position
				updateStationPositionFromTranslation(spatial.getTransform());
				resetDraggable();
			}
		};
	}

	protected VerveInteractable createInteractableRotation() {
		return new VerveInteractable() {
			{ 
				m_preferredWidgets = new String[] { VerveInteractWidgets.RotXYZ }; 
			}

			@Override
			public String getDisplayString(ReadOnlyTransform transform) {
				double[] rotated = null;
				rotated =  transform.getMatrix().toAngles(rotated);		
				return String.format("rpy=[%.2f, %.2f, %.2f]", 
						rotated[0] * RAD2DEG,  
						rotated[1] * RAD2DEG,
						rotated[2] * RAD2DEG);			
			}

			@Override
			public void beginInteractDrag(Spatial spatial) {
				draggablePreviewNode.getSceneHints().setCullHint(CullHint.Inherit);
			}

			@Override
			public void endInteractDrag(Spatial spatial) {
				// set the position of the Station to the dragged position
				updateStationPositionFromRotation(spatial.getTransform());
				resetDraggable();
			}
		};
	}

	@Inject @Optional
	public void setSelectedElement(TypedObject selectedElement) {
		if(selectedElement == null) {
			clearSelected();
			hideDraggable();
			return;
		}
		if(selectedElement instanceof AddViaMapTypedObject) {
			clearSelected();
			hideDraggable();
		}

		if(plan == null || !plan.getSequence().contains(selectedElement)) {
			hideDraggable();
			return;
		}
		clearSelected();
		hideDraggableWithoutClearingMemory();
		if(!(selectedElement instanceof Plan)) {
			selected = (SequenceHolder)selectedElement;
			setHighlightedMaterial(selected);

			if(selectedElement instanceof Segment) {

				labelHighlightedSegment( (Segment) selectedElement );
				// Zoom to the segment
				// find the Spatial
				Spatial selectedSpatial = getChild( getSequenceableName((Segment)selectedElement) + arrowString);
				SegmentModel sm = (SegmentModel) selectedSpatial;

				// Injected in centerOnSpatial() in LiveTelemetryView, at least
				applicationContext.set(Spatial.class, sm.getLine());
			}
			else if(selectedElement instanceof ModuleBayStation) {
				if(showTranslateDraggable) {
					resetDraggable();
				} else {
					if(((ModuleBayStation) selectedElement).getCoordinate().isIgnoreOrientation()) {
						hideDraggable();
					} else {
						resetDraggable();
					}
				}
				// Zoom to the station
				// find the Spatial
				Spatial selectedSpatial = getChild( getSequenceableName((Station)selectedElement) + "Node" );
				applicationContext.set(Spatial.class, selectedSpatial);
			}
		}
		markDirty(DirtyType.Attached);
	}

	@Override
	public String toString() {
		if (plan == null) {
			return null;
		}

		return plan.getName() + " Create Trace, " + plan.getNumStations() + " stations";
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("sequence")) {
			refreshTheTrace();
		} else if(evt.getPropertyName().equals("coord")) {
			Point3D point = (Point3D) evt.getNewValue();
			Vector3 move = new Vector3(point.getX(), point.getY(), point.getZ() );
			applicationContext.set(ReadOnlyVector3.class, move);
		}
	}

	protected void eraseAndRedraw(SequenceHolder savedSelected) {
		eraseTheTrace();
		drawTheTrace();
		setSelectedElement(savedSelected);
	}

	protected void refreshTheTrace() {
		SequenceHolder savedSelected = selected;
		List<Sequenceable> sequence = plan.getSequence();

		int min = Math.min(spatials.size(), sequence.size());
		for(int i = 0; i < min; i ++) {
			Sequenceable seq = sequence.get(i);
			Spatial spat = spatials.get(i);
			if(spat instanceof SegmentModel && seq instanceof Segment) {
				SegmentModel segModel = (SegmentModel) spat;
				Segment seg = (Segment) seq;
				segModel.setEndpoints(seg.getStartPosition(), seg.getEndPosition());
			} else if(spat instanceof StationModel && seq instanceof Station) {
				StationModel statModel = (StationModel) spat;
				Station stat = (Station) seq;
				if(!stat.getName().equals(statModel.getNumberAsString())) {
					eraseAndRedraw(savedSelected);
					return;
				}
				statModel.setStationPosition(stat.getStartPosition());
			} else {
				// significant change or malformed input, redraw
				eraseAndRedraw(savedSelected);
				return;
			}
		}

		int max = Math.max(spatials.size(), sequence.size());
		for(int i = min; i < max; i++) {
			if(spatials.size() > sequence.size()) {
				detachChild(spatials.remove(spatials.size() - 1));

			} else {
				Sequenceable seq = sequence.get(i);
				if(seq instanceof Station) {
					spatials.add(drawBigAndLittleStationMarker((Station) seq));
				} else if(seq instanceof Segment) {
					spatials.add(drawASegment((Segment) seq, getSequenceableName((Segment) seq)));
				}
			}
		}
		
		verifyStations();
	}
	
	public void verifyStations() {		
		for(Node node : spatials) {
			if(node instanceof StationModel) {
				StationModel station = (StationModel) node;
				double[] t = station.getTranslation().toArray(null);
				double[] r = station.getRotation().toAngles(null);
				if( checker.isAstrobeeSafe(t[0], t[1], t[2], r[0], r[1], r[2])) {
					station.hideAlarmColor();
				} else {
					station.showAlarmColor();
				}
			}
		}
	}
	
	public void onDeselect() {
		if(planBuilder == null) {
			return;
		}
		applicationContext.set(TypedObject.class, plan);
		clearSelected();
		hideDraggable();
	}

	protected float round(double d) {
		int bigD = (int) (d * 100);
		int remainder = bigD % roundTo;
		if(0 < remainder) {
			if(remainder < halfRoundTo) {
				bigD -= remainder;
			} else {
				bigD += (roundTo - remainder);
			}
		} else {
			if(remainder < -halfRoundTo) {
				bigD -= (roundTo + remainder);
			} else {
				bigD -= remainder;
			}
		}
		double ret = bigD/100.0;

		return (float) ret;
	}

	protected int getIntFromString(String input) {
		StringBuffer out = new StringBuffer();
		int radix = 10;

		for(int i=0; i<input.length(); i++) {
			if(Character.digit(input.charAt(i),radix) < 0) {
				break;
			} else {
				out.append(input.charAt(i));
			}
		}
		return Integer.parseInt(out.toString());
	}
}
