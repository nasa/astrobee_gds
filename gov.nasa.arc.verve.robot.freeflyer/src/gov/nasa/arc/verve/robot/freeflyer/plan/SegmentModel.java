package gov.nasa.arc.verve.robot.freeflyer.plan;

import gov.nasa.arc.irg.plan.model.Position;
import gov.nasa.arc.verve.common.ardor3d.text.BMFontManager;
import gov.nasa.arc.verve.common.ardor3d.text.BMText;

import java.util.List;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.scenegraph.shape.Cone;

public class SegmentModel extends Node {
	String num;
	private Vector3 start;
	private Vector3 end;
	
	private Line line;
	private Cone arrow;
	private BMText label;
	
	private int samples = 10;
	private float radius = .04f;
	private float height = .2f;
	
	protected MaterialState plainMtl, selectedMtl, alarmMtl;
	protected ColorRGBA plainCol, selectedCol, alarmCol;
	
	public SegmentModel (String name, String num, Vector3 start, Vector3 end) {
		super(name);
		this.num = num;
		this.start = start;
		this.end = end;
		setupColorAndMaterials();
		createLine();
		createArrow();
		createLabel();
//		this.updateWorldBound(true);
	}
	
	private void createLine() {
		Vector3[] lineEnds = new Vector3[]{start, end};
		line = new Line(getName() + "Line", lineEnds, null, null, null);
		line.setRenderState(plainMtl);
		line.setLineWidth(2);

		line.setRenderState(plainMtl);

		line.updateModelBound();

		attachChild(line);
	}
	
	public Line getLine() {
		return line;
	}
	
	private void createArrow() {
		Vector3 midpoint = findMidpoint();
		arrow = new Cone( getName()+"Arrow", samples, samples, radius, height );
		arrow.setRenderState(plainMtl);
		arrow.updateModelBound();
		Transform xfmHead = new Transform();
		Matrix3 m33head = new Matrix3();
		Vector3 direction = start.subtract(end, null);
		m33head.fromStartEndLocal(Vector3.UNIT_Z, direction.normalizeLocal());
		xfmHead.setRotation(m33head);
		xfmHead.setTranslation(midpoint);

		arrow.setTransform(xfmHead);
		attachChild(arrow);
	}
	
	public void showColor(ColorRGBA col, MaterialState mtl) {
		line.setRenderState(mtl);
		arrow.setRenderState(mtl);
		label.setTextColor(col);
		markDirty(DirtyType.RenderState);
	}
	
	public void showColor(ColorRGBA col,ColorRGBA numColor, MaterialState mtl) {
		line.setRenderState(mtl);
		arrow.setRenderState(mtl);
		label.setTextColor(numColor);
		markDirty(DirtyType.RenderState);
	}
	
	public void showHighlightColor() {
		showLabel();
		showColor(selectedCol, selectedMtl);
	}
	
	public void showAlarmColor() {
		showLabel();
		showColor(alarmCol, alarmMtl);
	}
	
	public void showDefaultColor() {
		hideLabel();
		showColor(plainCol, plainMtl);
	}
	
	private void createLabel() {
		Vector3 point = findMidpoint().add(radius, radius, -radius, null);
		
		label = new BMText( "label" + num,
				num,
				BMFontManager.sansLarge());
		label.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);

		label.setTextColor(plainCol);
		label.updateModelBound();
		label.setTranslation(point);
	}
	
	public void showLabel() {
		attachChild(label);
	}
	
	public void hideLabel() {
		detachChild(label);
	}
	
	public void setEndpoints(Position startPos, Position endPos) {
		List<Float> startFloats = startPos.getCoordinates();
		start = new Vector3(startFloats.get(0), startFloats.get(1), startFloats.get(2));
		List<Float> endFloats = endPos.getCoordinates();
		end = new Vector3(endFloats.get(0), endFloats.get(1), endFloats.get(2));
		Vector3 midpoint = findMidpoint();
		
		Transform xfmHead = new Transform();
		Matrix3 m33head = new Matrix3();
		Vector3 direction = start.subtract(end, null);
		m33head.fromStartEndLocal(Vector3.UNIT_Z, direction.normalizeLocal());
		xfmHead.setRotation(m33head);
		xfmHead.setTranslation(midpoint);
		
		arrow.setTransform(xfmHead);
		arrow.updateModelBound();
		
		detachChild(line);
		Vector3[] lineEnds = new Vector3[]{start, end};
		line = new Line(getName() + "Line", lineEnds, null, null, null);
		line.setRenderState(plainMtl);
		line.setLineWidth(2);
		line.setRenderState(plainMtl);
		attachChild(line);
		line.updateModelBound();
		
		Vector3 point = findMidpoint().add(radius, radius, -radius, null);
		label.setTranslation(point);
	}
	
	protected Vector3 findMidpoint() {
		Vector3 midpointReal = new Vector3();
		midpointReal.setX((end.getX() + start.getX())/2.0);
		midpointReal.setY((end.getY() + start.getY())/2.0);
		midpointReal.setZ((end.getZ() + start.getZ())/2.0);
		return midpointReal;
	}
	
	private void setupColorAndMaterials() {		
		float[]	plain = new float[] { 0.52f, 0.81f, 0.98f, 1f};
		float[]	selected = new float[] {0.94f, 0.5f, 0.5f, 1f};
		float[] alarm = new float[] { (255f/255f), (133f/255f), (36f/255f), 1f };
		
		plainCol = new ColorRGBA(plain[0], plain[1], plain[2], plain[3]);
		selectedCol = new ColorRGBA(selected[0], selected[1], selected[2], selected[3]);
		alarmCol = new ColorRGBA(alarm[0], alarm[1], alarm[2], alarm[3]);
		
		plainMtl = makeMaterialState(plain[0], plain[1], plain[2], plain[3]);
		selectedMtl = makeMaterialState(selected[0], selected[1], selected[2], selected[3]);
		alarmMtl = makeMaterialState(alarm[0], alarm[1], alarm[2], alarm[3]);
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
}
