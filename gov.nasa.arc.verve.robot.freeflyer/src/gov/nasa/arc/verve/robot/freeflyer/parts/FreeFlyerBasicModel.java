package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.Box;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;

/**
 * The built-in-Verve model of Astrobee (as opposed to the .dae model file)
 * @author ddwheele
 *
 */
public class FreeFlyerBasicModel extends Node {
	protected double halfSideLength;
	protected Float[] hColor;
	protected float alpha = 1.0f;
	protected Box base, leftProp, rightProp, frontFace, frontRamp;
	protected float diff = 0.5f; // was 0.2
	protected float spec = 0.5f; // was 1.0
	protected float emis = 0.2f; // was 0.3
	protected float ambt = (spec+diff)/4;
	protected float shininess = 5;
	protected MaterialState normalMaterial, alarmMaterial, xMaterial;
	protected double fudgeFactor = 0.0005;
	protected double DEG_TO_RAD = Math.PI/180;
	String myName;
	protected Box bound;
	protected float beeRadius = 0.1524f;
	protected float[] alarmMatValues = {(255f/255f), (133f/255f), (36f/255f)};

	/**
	 * @param color red, blue, orange, yellow, green, purple, pink, mint, rose, or gray
	 */
	public FreeFlyerBasicModel(String name, double size, String color) {
		super(name);
		halfSideLength = size+fudgeFactor/2.0;
		prepareColor(color);
		init(color);
	}

	/** 
	 * @param color red, blue, orange, yellow, green, purple, pink, mint, rose, or gray
	 */
	public FreeFlyerBasicModel(String color) {
		super(color+"SingleModel");
		halfSideLength = beeRadius+fudgeFactor/2.0;
		prepareColor(color);
		init(color);
	}

	// color defaults to gray
	public FreeFlyerBasicModel() {
		super("graySingleModel");
		bound = new Box("BoundingBox", new Vector3(0, 0, 0), beeRadius, beeRadius, beeRadius);
		halfSideLength = beeRadius + fudgeFactor/2;
		prepareColor("gray");
		init("gray");
	}

	public void showAlarmColor() {
		setAllPartsToMaterial(alarmMaterial);
	}

	public void hideAlarmColor() {
		setAllPartsToMaterial(normalMaterial);
	}

	protected void setAllPartsToMaterial(MaterialState mat) {
		base.setRenderState(mat);
		leftProp.setRenderState(mat);
		rightProp.setRenderState(mat);
		//frontFace.setRenderState(mat);
		//frontRamp.setRenderState(mat);
	}

	protected void init(String color) {
		normalMaterial = makeMaterialState(hColor[0], hColor[1], hColor[2]);
		xMaterial = makeMaterialState(0, 0, 0.6f);
		myName = color + "FreeFlyer-m_base";
		alarmMaterial = makeMaterialState(alarmMatValues[0],alarmMatValues[1],alarmMatValues[2]);

		makeCenterBox();
		makeLeftProp();
		makeRightProp();
		makeFrontFace();
		makeFrontRamp();

		bound = new Box("BoundingBox", new Vector3(0, 0, 0), beeRadius, beeRadius, beeRadius);
		attachChild(bound);
		updateModelAndWorldBounds();
		bound.getSceneHints().setCullHint(CullHint.Always);
	}

	protected void makeCenterBox() {
		base = new Box(myName + "-center", new Vector3(),
				halfSideLength - fudgeFactor, 
				halfSideLength/2, 
				halfSideLength/2);
		base.setRenderState(normalMaterial);
		base.updateModelBound();
		attachChild(base);
	}

	protected void makeLeftProp() {
		leftProp = new Box(myName + "-left", new Vector3(0, halfSideLength*0.75, 0),
				halfSideLength, 
				halfSideLength/4, 
				halfSideLength);
		leftProp.setRenderState(normalMaterial);
		leftProp.updateModelBound();
		attachChild(leftProp);
	}

	protected void makeRightProp() {
		rightProp = new Box(myName + "-right", new Vector3(0, -halfSideLength*0.75, 0),
				halfSideLength, 
				halfSideLength/4, 
				halfSideLength);
		rightProp.setRenderState(normalMaterial);
		rightProp.updateModelBound();
		attachChild(rightProp);
	}

	protected void makeFrontFace() {
		frontFace = new Box(myName + "-front", new Vector3(halfSideLength, 0, -halfSideLength/2),
				0, 
				halfSideLength/2, 
				halfSideLength/4);
		frontFace.setRenderState(xMaterial);
		frontFace.updateModelBound();
		attachChild(frontFace);
	}

	protected void makeFrontRamp() {
		Vector3 cornerOne = new Vector3(halfSideLength, halfSideLength, -3*halfSideLength/4);
		Vector3 cornerTwo = new Vector3(3*halfSideLength/4, -halfSideLength, -halfSideLength);
		Vector3 center = new Vector3((cornerOne.getX() + cornerTwo.getX()) / 2,
				(cornerOne.getY() + cornerTwo.getY()) / 2,
				(cornerOne.getZ() + cornerTwo.getZ()) / 2);

		frontRamp = new Box(myName + "-frontRamp",
				new Vector3(0, 0, 0), 
				halfSideLength/(4*Math.sqrt(2)), 
				halfSideLength, 
				0);

		Matrix3 rot = new Matrix3();
		rot.fromAngles(0, -Math.PI/4, 0);
		frontRamp.setRotation(rot);
		frontRamp.setTranslation(center);		
		frontRamp.setRenderState(xMaterial);
		frontFace.updateModelBound();
		attachChild(frontRamp);
	}

	protected MaterialState makeMaterialState(float red, float green, float blue) {
		MaterialState mtl;
		mtl = new MaterialState();
		mtl.setShininess(shininess); 
		mtl.setDiffuse (new ColorRGBA(diff*red, diff*green, diff*blue, alpha ));
		mtl.setSpecular(new ColorRGBA(spec*red, spec*green, spec*blue, alpha ));
		mtl.setEmissive(new ColorRGBA(emis*red, emis*green, emis*blue, alpha ));
		mtl.setAmbient (new ColorRGBA(ambt*red, ambt*green, ambt*blue, alpha ));
		return mtl;
	}

	protected void prepareColor(String color) {
		// make light and dark variants for all colors
		if(color.equals("red")) {
			hColor = new Float[]{ 0.4f, 0.0f, 0.0f };
		} 
		else if(color.equals("orange")) {
			hColor = new Float[]{ 0.82f, 0.41f, 0.12f };
		}
		else if(color.equals("yellow")) {
			hColor = new Float[]{ 0.74f, 0.52f, 0.04f };
		}
		else if(color.equals("green")) {
			hColor = new Float[]{ 0.0f, 0.39f, 0.0f };
		}
		else if(color.equals("mint")) {
			hColor = new Float[]{ 0.60f, 1.00f, 0.60f };
		}
		else if(color.equals("blue")) {
			hColor = new Float[]{ 0.0f, 0.0f, 0.5f };
		}
		else if(color.equals("purple")) {
			hColor = new Float[]{ 0.29f, 0.0f, 0.51f };
		}
		else if(color.equals("pink")) {
			hColor = new Float[]{ 1.0f, 0.08f, 0.52f };
		}
		else if(color.equals("rose")) {
			hColor = new Float[]{ 1.00f, 0.7f, 0.9f };
		}
		else if(color.equals("white")) {
			hColor = new Float[]{ 1.0f, 1.0f, 1.0f };
		}
		else if(color.equals("plain")) {
			hColor = new Float[]{ 0.52f, 0.81f, 0.98f, 1f};
		}
		else { // gray
			hColor = new Float[]{ 0.6f, 0.6f, 0.6f };
		}
	}

	public void updateModelAndWorldBounds() {
		for(Spatial child : getChildren()) {
			if(child instanceof Mesh) {
				Mesh mesh = (Mesh) child;
				mesh.updateModelBound();
			}
		}
		this.updateWorldBound(true);
	}

	@Override
	public RenderState setRenderState(RenderState rs) {
		if(rs instanceof MaterialState) {
			RenderState old = frontFace.getLocalRenderState(StateType.Material);
			setAllPartsToMaterial((MaterialState) rs);
			return old;
		} else {
			return setRenderState(rs);
		}
	}

	@Override
	public void setTranslation(ReadOnlyVector3 t) {
		super.setTranslation(t);
	}

	@Override
	public BoundingVolume getWorldBound() {
		bound.updateModelBound();
		return bound.getWorldBound();
	}
}
