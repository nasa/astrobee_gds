package gov.nasa.arc.verve.robot.freeflyer.plan;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Cone;

public class CollisionMarker extends Node {
	
	private ReadOnlyVector3 center;
	
	private MaterialState redMaterial;
	
	private int samples = 10;
	private float radius = .04f;
	private float height = .2f;
	
	public CollisionMarker (String name, ReadOnlyVector3 center) {
		super(name);
		this.center = center;
		setupColor();
		setupCones();
		setTranslation(this.center);
	}
	
	private void setupCones() {
		int i = 0;
		createConesAboutAxis(i, new double[] {Math.PI/4, 0, 0});
		createConesAboutAxis(i, new double[] {Math.PI/4, Math.PI/2, 0});
		createConesAboutAxis(i, new double[] {Math.PI/4, 0, Math.PI/2});
	}
	
	private void createConesAboutAxis(int i, double[] flags) {
		for(double x = 0; x < 2*Math.PI; x += Math.PI/2) {
			Cone cone = new Cone("CollisionCone" + (i++), samples, samples, radius, height);
			
			Matrix3 rot = new Matrix3();
			rot.fromAngles(x + flags[0], flags[1], flags[2]);
			Quaternion q = new Quaternion();
			q.fromRotationMatrix(rot);
			Vector3 center = q.apply(new Vector3(0, 0, -height/2), null);
			
			cone.setTranslation(center);
			cone.setRotation(rot);
			cone.setRenderState(redMaterial);
			attachChild(cone);
		}
	}
	
	private void setupColor() {
		float diff = 0.5f;
		float spec = 0.0f;
		float emis = 0.2f;
		float ambt = (spec+diff)/4;
		float[]	orange = new float[] { (255f/255f), (133f/255f), (36f/255f), 1f };
		
		redMaterial = new MaterialState();
		redMaterial.setShininess(5);
		redMaterial.setDiffuse (new ColorRGBA(diff*orange[0], diff*orange[1], diff*orange[2],orange[3]));
		redMaterial.setSpecular(new ColorRGBA(spec*orange[0], spec*orange[1], spec*orange[2],orange[3]));
		redMaterial.setEmissive(new ColorRGBA(emis*orange[0], emis*orange[1], emis*orange[2],orange[3]));
		redMaterial.setAmbient (new ColorRGBA(ambt*orange[0], ambt*orange[1], ambt*orange[2],orange[3]));
	}
	
}
