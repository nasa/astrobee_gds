package gov.nasa.arc.verve.robot.freeflyer.camera;

import gov.nasa.arc.verve.robot.freeflyer.RectangularPyramid;
import gov.nasa.arc.verve.robot.freeflyer.camera.CameraConfigList.CameraConfig;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

public class FOVCameraModel extends Node {
	
	public enum CameraType {
		TWO_D("2D"), DEPTH("depth");
		
		private final String name;
		
		CameraType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			return getName();
		}
	}
	
	private Spatial camera;
	private MaterialState defMaterial;
	
	private String name;
	private double[] position;
	private double[] rotation;
	private double horizFOV;
	private double vertFOV;
	private CameraType type;
	
	private double height = .4;
	private double DEG_TO_RAD = Math.PI / 180;
	
	private float[] blue = new float[] { 0f, 0f, 1f };
	private float[] green = new float[] { 0f, 1f, 0f };
	private float[] white = new float[] { 1f, 1f, 1f };
	
	public FOVCameraModel(CameraConfig config) {
		super(config.getName());
		name = config.getName();
		position = config.getPosition();
		rotation = config.getRotation();
		horizFOV = config.getHoriz_fov();
		vertFOV = config.getVert_fov();
		
		if(config.getType() != null && config.getType().equals(CameraType.DEPTH.getName())) {
			this.type = CameraType.DEPTH;
		} else if(config.getType() != null && config.getType().equals(CameraType.TWO_D.getName())) {
			this.type = CameraType.TWO_D;
		} else {
			this.type = null;
		}
		
		init();
	}
	
	public FOVCameraModel(String name, double[] pos, double[] rot, double hFOV, double vFOV, String type) {
		super(name);
		this.name = name;
		position = pos;
		rotation = rot;
		horizFOV = hFOV;
		vertFOV = vFOV;
		
		if(type != null && type.equals(CameraType.DEPTH.getName())) {
			this.type = CameraType.DEPTH;
		} else if(type != null && type.equals(CameraType.TWO_D.getName())) {
			this.type = CameraType.TWO_D;
		} else {
			this.type = null;
		}
		
		init();
	}
	
	private void init() {		
		Quaternion rot = new Quaternion(rotation[0], rotation[1], rotation[2], rotation[3]);
		Quaternion correction = new Quaternion();
		correction.fromEulerAngles(new double[] {90*DEG_TO_RAD, 0*DEG_TO_RAD, -90*DEG_TO_RAD});
		
		setRotation(rot.multiply(correction, null));
		setTranslation(new Vector3(position[0], position[1], -position[2]));
		
		double length = 2 * Math.tan((horizFOV/2) * DEG_TO_RAD) * height;
		double width = 2 * Math.tan((vertFOV/2) * DEG_TO_RAD) * height;
		camera = new RectangularPyramid(name, width, length, height);
		camera.setTranslation(new Vector3(0, -height/2, 0));
		camera.updateWorldBound(true);
		attachChild(camera);
		
		setupMaterial();
		camera.setRenderState(defMaterial);	
	}
	
	private void setupMaterial() {
		float alpha = 1f;
		float diff = 0.5f; // was 0.2
		float spec = 0.5f; // was 1.0
		float emis = 0.5f; // was 0.3
		float ambt = (spec+diff)/4;
		float shininess = 5;
		
		float[] color;
		if(type == CameraType.TWO_D) {
			color = green;
		} else if (type == CameraType.DEPTH) {
			color = blue;
		} else {
			color = white;
		}
		
		defMaterial = new MaterialState();
		defMaterial.setShininess(shininess); 
		defMaterial.setDiffuse (new ColorRGBA(diff*color[0], diff*color[1], diff*color[2], alpha ));
		defMaterial.setSpecular(new ColorRGBA(spec*color[0], spec*color[1], spec*color[2], alpha ));
		defMaterial.setEmissive(new ColorRGBA(emis*color[0], emis*color[1], emis*color[2], alpha ));
		defMaterial.setAmbient (new ColorRGBA(ambt*color[0], ambt*color[1], ambt*color[2], alpha ));
	}
	
	public String getName() {
		return name;
	}
	
}
