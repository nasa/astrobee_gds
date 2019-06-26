package gov.nasa.arc.verve.robot.freeflyer.camera;

import java.util.ArrayList;
import java.util.List;

public class CameraConfigList {
	private String type;
	private List<CameraConfig> cameraConfigs = new ArrayList<CameraConfig>();
	
	
	public CameraConfigList() {
		// for json deserializing
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<CameraConfig> getCameraConfigs() {
		return cameraConfigs;
	}

	public void setCameraConfigs(List<CameraConfig> cameraConfigs) {
		this.cameraConfigs = cameraConfigs;
	}

	public static class CameraConfig {
		private String name;
		private double[] position;
		private double[] rotation;
		private double horiz_fov;
		private double vert_fov;
		private String type;
		
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public double[] getPosition() {
			return position;
		}
		
		public void setPosition(double[] position) {
			this.position = position;
		}
		
		public double[] getRotation() {
			return rotation;
		}
		
		public void setRotation(double[] rotation) {
			this.rotation = rotation;
		}
		
		public double getHoriz_fov() {
			return horiz_fov;
		}
		
		public void setHoriz_fov(double horiz_fov) {
			this.horiz_fov = horiz_fov;
		}
		
		public double getVert_fov() {
			return vert_fov;
		}
		
		public void setVert_fov(double vert_fov) {
			this.vert_fov = vert_fov;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
	}
}
