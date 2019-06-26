package gov.nasa.arc.verve.robot.freeflyer.plan;

import gov.nasa.arc.irg.plan.model.Position;
import gov.nasa.arc.verve.common.ardor3d.text.BMFontManager;
import gov.nasa.arc.verve.common.ardor3d.text.BMText;
import gov.nasa.arc.verve.robot.freeflyer.parts.FreeFlyerBasicModel;
import gov.nasa.rapid.v2.framestore.ConvertUtils;
import gov.nasa.rapid.v2.framestore.EulerAngles;
import gov.nasa.rapid.v2.framestore.ReadOnlyEulerAngles;

import java.util.List;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.Cone;

public class StationModel extends Node {
		
	String num;
	private FreeFlyerBasicModel bigMarker;
	private Box littleMarker;
	private Cone littleMarkerX;
	
	private BMText bigLabel, littleLabel;
	private String labelString = "label";
	
	protected double bigMarkerRadius = 0.15;
	protected int samples = 12;
	protected double littleMarkerRadius = 0.03;
	protected float DEG2RAD = (float)(Math.PI / 180.0);
	
	protected MaterialState plainMtl, selectedMtl, alarmMtl, xMtl;
	protected ColorRGBA plainCol, selectedCol, alarmCol;
	
	public StationModel(String name, String num) {
		super(name);
		this.num = num;
		
		setupColorAndMaterials();
		createBigMarker();
		createLittleCubeMarker();
		createLabels();
		updateModelBounds();
		
		getSceneHints().setCullHint(CullHint.Inherit);
	}
	
	public String getNumberAsString() {
		return new String(num);
	}
	
	public void showColor(ColorRGBA color, MaterialState mtl) {
		littleMarker.setRenderState(mtl);
		littleMarkerX.setRenderState(mtl);
		littleLabel.setTextColor(color);
	}
	
	public void showDefaultColor() {
		showColor(plainCol, plainMtl);
	}
	
	public void showHighlightColor() {
		showColor(selectedCol, selectedMtl);
	}
	
	public void showAlarmColor() {
		showColor(alarmCol, alarmMtl);
		bigMarker.setRenderState(alarmMtl);
		bigLabel.setTextColor(alarmCol);
	}
	
	public void hideAlarmColor() {
		showDefaultColor();
		bigMarker.setRenderState(selectedMtl);
		bigLabel.setTextColor(selectedCol);
	}
	
	public Vector3 getCenter() {
		updateModelBounds();
		
		if(bigMarker.getWorldBound() == null) {
			return new Vector3(0, 0, 0);
		} else {
			return (Vector3) bigMarker.getWorldBound().getCenter();
		}
	}
	
	public Matrix3 getStationRotation() {
		return (Matrix3) bigMarker.getWorldRotation();
	}
	
	public void setStationRotation(Matrix3 rot) {
		setRotation(rot);
	}
	
	public void setStationPosition(Position newPos) {
		List<Float> newMovef = newPos.getCoordinates();
		Vector3 newMove = new Vector3(newMovef.get(0), newMovef.get(1), newMovef.get(2));
		setTranslation(newMove);
		
		List<Float> newRotf = newPos.getOrientation();
		EulerAngles ea = EulerAngles.fromDegrees(ReadOnlyEulerAngles.Type.ZYXr, newRotf.get(2), newRotf.get(1), newRotf.get(0));
		Matrix3 m33 = ConvertUtils.toRotationMatrix(ea, null);
		setStationRotation(m33);
	}
	
	public void showBigMarker() {
		bigMarker.getSceneHints().setCullHint(CullHint.Inherit);
	}
	
	public void hideBigMarker() {
		bigMarker.getSceneHints().setCullHint(CullHint.Always);
	}
	
	public void showBigLabel() {
		attachChild(bigLabel);
		detachChild(littleLabel);
	}
	
	public void hideBigLabel() {
		detachChild(bigLabel);
	}
	
	public void showLittleLabel() {
		attachChild(littleLabel);
		detachChild(bigLabel);
	}
	
	public void hideLittleLabel() {
		detachChild(littleLabel);
	}
	
	public void updateModelBounds() {
		bigMarker.updateModelAndWorldBounds();
	}
	
	private void createLabels() {
		bigLabel = createLabel(bigMarkerRadius, selectedCol);
		littleLabel = createLabel(littleMarkerRadius, plainCol);
	}
	
	private BMText createLabel(double radius, ColorRGBA color) {
		Vector3 offset = new Vector3(radius, radius, -radius);
		BMText label = new BMText(labelString + num, num, BMFontManager.sansLarge());
		label.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);
		label.setTextColor(color);
		label.setTranslation(offset);
		
		return label;
	}
	
	private void createBigMarker() {
		bigMarker = new FreeFlyerBasicModel();
		bigMarker.updateModelAndWorldBounds();
		bigMarker.setRenderState( selectedMtl );
		bigMarker.updateModelAndWorldBounds();
		attachChild( bigMarker );
	}

	private void createLittleCubeMarker() {
		littleMarker = new Box( getName()+"Little", new Vector3(),
				littleMarkerRadius,
				littleMarkerRadius,
				littleMarkerRadius);
		
		littleMarker.updateModelBound();
		littleMarker.setRenderState( plainMtl );
		attachChild( littleMarker );
		
		littleMarkerX = new Cone( getName()+"LittleX",
				samples,
				samples,
				(float)(littleMarkerRadius * 0.8),
				(float)littleMarkerRadius);

		Vector3 position = new Vector3(littleMarkerRadius*1.1, 0, 0);
		littleMarkerX.setTranslation(position);
		Matrix3 direction = new Matrix3();
		direction.fromStartEndLocal(Vector3.UNIT_Z, Vector3.NEG_UNIT_X);
		littleMarkerX.setRotation(direction);
		littleMarkerX.updateModelBound();
		littleMarkerX.setRenderState( plainMtl );
		attachChild( littleMarkerX );
	}
	
//	private void createLittleSphereMarker() {
//		littleMarker = new Sphere( getName()+"Little", new Vector3(0, 0, 0),
//				samples,
//				samples,
//				littleMarkerRadius);
//		
//		littleMarker.updateModelBound();
//		littleMarker.setRenderState( plainMtl );
//		attachChild( littleMarker );
//	}
	
	private void setupColorAndMaterials() {
		float[]	plain = new float[] { 0.52f, 0.81f, 0.98f, 1f};
		float[] alarm = new float[] { (255f/255f), (133f/255f), (36f/255f), 1f };
		float[]	black = new float[] { 0f, 0f, 0f, 1f};
		
		
		plainCol = new ColorRGBA(black[0], black[1], black[2], black[3]);
		selectedCol = new ColorRGBA(black[0], black[1], black[2], black[3]);
		alarmCol = new ColorRGBA(black[0], black[1], black[2], black[3]);
		
		plainMtl = makeMaterialState(plain[0], plain[1], plain[2], plain[3]);
		selectedMtl = makeMaterialState(plain[0], plain[1], plain[2], plain[3]);
		alarmMtl = makeMaterialState(alarm[0], alarm[1], alarm[2], alarm[3]);
		
		xMtl = makeMaterialState(plain[0], plain[1], plain[2], plain[3]);
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
