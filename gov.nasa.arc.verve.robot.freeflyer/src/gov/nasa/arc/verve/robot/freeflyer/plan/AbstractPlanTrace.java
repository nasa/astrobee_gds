package gov.nasa.arc.verve.robot.freeflyer.plan;

import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Position;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.SequenceHolder;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.rapid.v2.framestore.ConvertUtils;
import gov.nasa.rapid.v2.framestore.EulerAngles;
import gov.nasa.rapid.v2.framestore.ReadOnlyEulerAngles;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.scenegraph.hint.CullHint;

public abstract class AbstractPlanTrace extends Node   {
	private static Logger logger = Logger.getLogger(AbstractPlanTrace.class);
	protected static AbstractPlanTrace instance;
	protected Plan plan = null;
	//	protected HashSet<Vector3> m_numberedNodes;
	protected ColorRGBA colorInitial;
	protected MaterialState mtl;
	protected ColorRGBA colorCompleted;
	protected MaterialState highlightedMtl;
	protected MaterialState xMaterial, yMaterial, zMaterial;
	protected double markerRadius = 0.05;
	protected double bigMarkerRadius = 0.2;
	protected double boxRadius = 0.15;
	protected double notch = 0.01; // for the colored direction indicators
	protected String segmentPrefix = "segment";
	protected String stationPrefix = "station";
	protected String labelPrefix = "label";
	protected String arrowString = "Arrow";
	protected String stationString = "Node";
	protected String littleNodeString = "LittleNode";
	protected boolean isVisible = false;
	protected float coneRadius = 0.04f;
	protected float coneHeight = 0.2f;
	protected int coneAxisSamples = 10;
	protected int coneRadiusSamples = 10;
	protected double littleMarkerRadius = 0.05;
	protected float RAD2DEG = (float)(180.0 / Math.PI);
	protected float DEG2RAD = (float)(Math.PI / 180.0);
	
	protected List<Node> spatials = new ArrayList<Node>();

	protected AbstractPlanTrace(String nodeName){
		super(nodeName);
	}

	public static AbstractPlanTrace getStaticInstance() {
		if(instance == null) {
			logger.error("AbstractPlanTrace was not created");
		}
		return instance;
	}

	public void hide() {
		getSceneHints().setCullHint(CullHint.Always);
		isVisible = false;
	}

	public void show() {
		getSceneHints().setCullHint(CullHint.Inherit);
		isVisible = true;
	}

	protected synchronized void drawTheTrace() {
		if(plan == null || plan.getSequence().size() < 1)
			return;

		for (Sequenceable current : plan.getSequence()){
			// check to see if current is a traverse
			if(current instanceof Station) {
				if (current.getEndPosition() != null && current.getEndPosition().hasCoordinates()){
					spatials.add(drawBigAndLittleStationMarker( (Station) current ));
				}
			} else if(current instanceof Segment) {
				spatials.add(drawASegment((Segment)current, getSequenceableName((Segment)current)));
			}
		}
		markDirty(DirtyType.Attached);
		show();
	}

	protected Node drawBigAndLittleStationMarker( Station current ) {
		Vector3 verveCurrPos = changePositionToVector3( current.getEndPosition() );
		Point6Dof endPt = current.getCoordinate();
		StationModel stationNode = new StationModel(getSequenceableName(current) + stationString, current.getName());
		EulerAngles ea = EulerAngles.fromDegrees(ReadOnlyEulerAngles.Type.ZYXr, endPt.getYaw(), endPt.getPitch(), endPt.getRoll());
		Matrix3 rot = ConvertUtils.toRotationMatrix(ea, null);
		stationNode.setStationRotation(rot);
		stationNode.setTranslation(verveCurrPos);
		stationNode.hideBigMarker();
		attachChild(stationNode);
		labelUnhighlightedStation( current );
		
		return stationNode;
	}
	


	// takes roll, pitch, yaw as degrees
	protected StationModel makeRotatedNode(Station seq, Vector3 point, double roll, double pitch, double yaw) {
		StationModel forMarker = new StationModel(getSequenceableName(seq) + stationString, seq.getName());
		forMarker.setTranslation(point);
		EulerAngles ea = EulerAngles.fromDegrees(ReadOnlyEulerAngles.Type.ZYXr, yaw, pitch, roll);
		Matrix3 rot = ConvertUtils.toRotationMatrix(ea, null);
		forMarker.setRotation(rot);
		return forMarker;
	}

	protected synchronized void eraseTheTrace() {
		if(plan == null) {
			return;
		}
		spatials.clear();
		this.detachAllChildren();
		markDirty(DirtyType.Detached);
	}
	
	protected Node drawASegment(Segment segment, String name) {
		Vector3 start = changePositionToVector3(segment.getStartPosition());
		Vector3 end = changePositionToVector3(segment.getEndPosition());
		SegmentModel segmentModel = new SegmentModel(name + arrowString, segment.getName(), start, end);
		attachChild(segmentModel);
		return segmentModel;
	}
	
	protected synchronized void labelUnhighlightedStation(Station station) {
		String name = getSequenceableName(station);
		Spatial spat = getChild(name + stationString);
		if(spat != null && spat instanceof StationModel) {
			StationModel statModel = (StationModel) spat;
			statModel.showLittleLabel();
		}
	}

	protected synchronized void labelHighlightedStation(Station station) {
		String name = getSequenceableName(station);
		Spatial spat = getChild(name + stationString);
		if(spat != null && spat instanceof StationModel) {
			StationModel statModel = (StationModel) spat;
			statModel.showBigLabel();
		}
	}

	protected synchronized void labelHighlightedSegment(Segment seg) {
		String name = getSequenceableName(seg);
		Spatial spat = getChild(name + arrowString);
		if(spat != null && spat instanceof SegmentModel) {
			SegmentModel segModel = (SegmentModel) spat;
			segModel.showLabel();
		}
	}
	
	protected synchronized void hideSegmentLabel(Segment seg) {
		String name = getSequenceableName(seg);
		Spatial spat = getChild(name + arrowString);
		if(spat != null && spat instanceof SegmentModel) {
			SegmentModel segModel = (SegmentModel) spat;
			segModel.hideLabel();
		}
	}
	
/*	protected void changeStationLabelColor(Station station, ColorRGBA newColor) {
		changeStationLabelColor(station, newColor, markerRadius);
	}
	
	protected void changeStationLabelColor(Station station, ColorRGBA newColor, double radius) {
		String num = station.getName();
		Vector3 stationPosition = changePositionToVector3(station.getEndPosition());
		Vector3 labelPosition = stationPosition.add(radius, radius, -radius, null);
		
		Node labelNode = ensureNodeForLabel( getLabelName(num) , null);
		labelNode.detachChildNamed( getLabelName(num) );
		BMText commandNumberText = makeCommandNumber(num, newColor, labelPosition);
		labelNode.attachChild(commandNumberText);
	}
	
	protected BMText makeCommandNumber(String name, ReadOnlyColorRGBA color, Vector3 offset) {
		BMText commandNumber = new BMText( getLabelName(name),
				name,
				BMFontManager.sansLarge());
		commandNumber.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);
		commandNumber.setTextColor(color);
		if(offset != null) {
			commandNumber.setTranslation(offset);
		}

		return commandNumber;
	}
*/
	protected Vector3 findMidpoint(Segment segment) {
		List<Float> start = segment.getStartPosition().getCoordinates();
		List<Float> end = segment.getEndPosition().getCoordinates();
		Vector3 midpointReal = new Vector3();
		midpointReal.setX((end.get(0) + start.get(0))/2.0);
		midpointReal.setY((end.get(1) + start.get(1))/2.0);
		midpointReal.setZ((end.get(2) + start.get(2))/2.0);
		return midpointReal;
	}

	protected Vector3 changePositionToVector3(Position pos) {
		List<Float> oldCoords = pos.getCoordinates();
		Vector3 posReal = new Vector3();
		posReal.setX(oldCoords.get(0));
		posReal.setY(oldCoords.get(1));
		posReal.setZ(oldCoords.get(2));
		return posReal;
	}

	protected void setUnhighlightedMaterial(SequenceHolder seq) {
		hideChild(seq, stationString);
		
		if(seq instanceof Segment) {
			unhighlightSegment((Segment)seq);
		}
	}

	protected void setHighlightedMaterial(SequenceHolder seq) {
		showChild(seq, stationString);
		
		if(seq instanceof Segment) {
			//highlightSegment((Segment)seq);
		}

		if(seq instanceof Station) {
			labelHighlightedStation( (Station) seq );
		} 
	}

	protected void hideChild(SequenceHolder seq, String postfix) {
		Spatial child = getChild( getSequenceableName(seq) + postfix );
		if(child != null) {
			if(child instanceof StationModel) {
				StationModel box = (StationModel) child;
				box.hideBigMarker();
			} else {
				child.getSceneHints().setCullHint(CullHint.Always);
			}
		}
	}
	
	protected void showChild(SequenceHolder seq, String postfix) {
		Spatial child = getChild( getSequenceableName(seq) + postfix );
		if(child != null) {
			if(child instanceof StationModel) {
				StationModel box = (StationModel) child;
				box.showBigMarker();
			} else {
				child.getSceneHints().setCullHint(CullHint.Inherit);
			}
		}
	}

	protected void highlightSegment(Segment segment) {
		Spatial child = getChild( getSequenceableName(segment) + arrowString );
		if(child instanceof SegmentModel) {
			((SegmentModel) child).showHighlightColor();
		}
	}

	protected void unhighlightSegment(Segment segment) {
		Spatial child = getChild( getSequenceableName(segment) + arrowString );
		if(child instanceof SegmentModel) {
			((SegmentModel) child).showDefaultColor();
		}
	}
	
	protected Node ensureNodeForLabel(String name, Vector3 point) {
		Node forLabel = (Node) getChild( name + stationString );
		if(forLabel == null) {
			forLabel = new Node(name + stationString);
			if(point != null) {
				forLabel.setTranslation(point);
			}
			forLabel.getSceneHints().setCullHint(CullHint.Never);
			attachChild(forLabel);
		}
		return forLabel;
	}

	protected String getSequenceableName(SequenceHolder seq) {
		if(seq == null) {
			return "";
		}
		if (seq instanceof Station) {
			return stationPrefix + seq.getName();
		}
		if (seq instanceof Segment) {
			return segmentPrefix + seq.getName();
		}
		else {
			return seq.getName();
		}
	}

	protected String getLabelName(String num) {
		return labelPrefix + num;
	}

	protected String getLabelName(SequenceHolder seq) {
		return labelPrefix + seq.getName();
	}
	
	protected MaterialState makeMaterialState(float red, float green, float blue, float alpha) {
		return makeMaterialState(red, green, blue, alpha, false);
	}
	
	protected MaterialState makeMaterialState(float red, float green, float blue, float alpha, boolean high_emis) {
		float diff = 0.5f;
		float spec = 0.0f;
		float emis = high_emis ? 0.8f : 0.2f;
		float ambt = (spec+diff)/4;
		float shininess = 5f;
		
		MaterialState mtl;
		mtl = new MaterialState();
		mtl.setShininess(shininess); 
		mtl.setDiffuse (new ColorRGBA(diff*red, diff*green, diff*blue, alpha ));
		mtl.setSpecular(new ColorRGBA(spec*red, spec*green, spec*blue, alpha ));
		mtl.setEmissive(new ColorRGBA(emis*red, emis*green, emis*blue, alpha ));
		mtl.setAmbient (new ColorRGBA(ambt*red, ambt*green, ambt*blue, alpha ));
		return mtl;
	}

	@Override
	public String toString() {
		if (plan == null) {
			return null;
		}
		return plan.getName() + " Trace, " + plan.getNumStations() + " stations";
	}

}
