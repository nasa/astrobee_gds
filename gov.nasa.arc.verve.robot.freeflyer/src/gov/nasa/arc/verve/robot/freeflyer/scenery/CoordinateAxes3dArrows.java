package gov.nasa.arc.verve.robot.freeflyer.scenery;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.shape.Arrow;

public class CoordinateAxes3dArrows extends Node {
	private double m_arrowWidth = 0.07;
	private double m_arrowLength = 0.4;
	
	public CoordinateAxes3dArrows() {
		super("CoordinateAxes3dArrows");
		init();
	}
	
	private void init() {
		Matrix3 m33 = new Matrix3();
		
		float[]	barColor = new float[] { 1f, 1f, 0f };
		m33.fromStartEndLocal(Vector3.UNIT_X, Vector3.UNIT_X);
		makeArrow("arrowXpos", m33, barColor);
		m33.fromAngleNormalAxis(Math.PI, Vector3.UNIT_Z);
		makeArrow("arrowXneg", m33, barColor);
		
		barColor = new float[] { 0f, 1f, 0f };
		m33.fromStartEndLocal(Vector3.UNIT_X, Vector3.UNIT_Y);
		makeArrow("arrowYpos", m33, barColor);
		m33.fromStartEndLocal(Vector3.UNIT_X, Vector3.NEG_UNIT_Y);
		makeArrow("arrowYneg", m33, barColor);
		
		barColor = new float[] { 1f, 0f, 0f };
		m33.fromAngleNormalAxis(Math.PI/2.0, Vector3.UNIT_X);
		makeArrow("arrowZpos", m33, barColor);
		m33.fromAngleNormalAxis(Math.PI/2.0, Vector3.NEG_UNIT_X);
		makeArrow("arrowZneg", m33, barColor);
	}
	
	private void makeArrow(String name, Matrix3 m33, float[] barColor) {
		float diff = 0.2f;
		float spec = 1.0f;
		float emis = 0.0f;
		float ambt = (spec+diff)/4;
		MaterialState mtl = new MaterialState();
		mtl.setShininess(5);
		mtl.setDiffuse (new ColorRGBA(diff*barColor[0], diff*barColor[1], diff*barColor[2],1));
		mtl.setSpecular(new ColorRGBA(spec*barColor[0], spec*barColor[1], spec*barColor[2],1));
		mtl.setEmissive(new ColorRGBA(emis*barColor[0], emis*barColor[1], emis*barColor[2],1));
		mtl.setAmbient (new ColorRGBA(ambt*barColor[0], ambt*barColor[1], ambt*barColor[2],1));
		
		Transform xfm = new Transform();
		
//		Matrix3 m33 = new Matrix3();
//		m33.fromStartEndLocal(Vector3.UNIT_X, direction);
		xfm.setRotation(m33);
		// using Verve coordinates (which are the same for the x axis anyway)
		Vector3 centerV = new Vector3(0, m_arrowLength/2.0, 0);
		xfm.setTranslation(m33.applyPost(centerV, null));
		
		Arrow bar = new Arrow(name, m_arrowLength, m_arrowWidth);	
		bar.setRenderState(mtl);
		bar.getSceneHints().setCullHint(CullHint.Never);
		bar.setTransform(xfm);
		this.attachChild(bar);
	}
}
