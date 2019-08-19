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
package gov.nasa.arc.irg.plan.freeflyer.config;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public  class OptionsForOneCamera {
	protected String cameraName;
	protected List<CameraPreset> preset;

	public OptionsForOneCamera() {
		// for JSON
	}

	public String getCameraName() {
		return cameraName;
	}

	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}

	public List<CameraPreset> getPreset() {
		return preset;
	}

	public void setPreset(List<CameraPreset> preset) {
		this.preset = preset;
	}
	
	@JsonIgnore
	public CameraPreset getPresetNumber(int index) {
		if(preset.size() > index) {
			return preset.get(index);
		} else {
			return new CameraPreset();
		}
	}

	@JsonIgnore
	public String[] getPresetNames() {
		String[] ret = new String[preset.size()];
		for(int i=0; i<ret.length; i++) {
			ret[i] = preset.get(i).getPresetName();
		}
		return ret;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cameraName == null) ? 0 : cameraName.hashCode());
		for(CameraPreset p : preset) {
			result = prime * result + ((p == null) ? 0 : p.hashCode());
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof OptionsForOneCamera)) {
			return false;
		}
		
		OptionsForOneCamera other = (OptionsForOneCamera)o;
		if(cameraName == null) {
			if(other.cameraName != null) {
				return false;
			}
		} else {
			if(!cameraName.equals(other.cameraName)) {
				return false;
			}
		}

		List<OptionsForOneCamera.CameraPreset> otherPreset = other.getPreset();

		if(preset.size() != otherPreset.size()) {
			return false;
		}

		for(int i=0; i<preset.size(); i++) {
			if(!preset.get(i).equals(otherPreset.get(i))){
				return false;
			}
		}
		return true;
	}

	public static class CameraPreset {
		protected String presetName;
		protected String resolution;
		protected String cameraMode;
		protected float frameRate;
		protected float bandwidth;

		public CameraPreset() {
			// for JSON
		}

		public String getPresetName() {
			return presetName;
		}
		public void setPresetName(String presetName) {
			this.presetName = presetName;
		}
		public String getResolution() {
			return resolution;
		}
		public void setResolution(String resolution) {
			this.resolution = resolution;
		}
		public String getCameraMode() {
			return cameraMode;
		}
		public void setCameraMode(String cameraMode) {
			this.cameraMode = cameraMode;
		}
		public float getFrameRate() {
			return frameRate;
		}
		public void setFrameRate(float frameRate) {
			this.frameRate = frameRate;
		}
		public float getBandwidth() {
			return bandwidth;
		}
		public void setBandwidth(float bandwidth) {
			this.bandwidth = bandwidth;	
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((presetName == null) ? 0 : presetName.hashCode());
			result = prime * result + ((resolution == null) ? 0 : resolution.hashCode());
			result = prime * result + ((cameraMode == null) ? 0 : cameraMode.hashCode());
			result = prime * result + Float.floatToIntBits(frameRate);
			result = prime * result + Float.floatToIntBits(bandwidth);
			return result;
		}

		@Override
		public boolean equals(Object o) {
			float EPSILON = 0.001f;

			if(this == o) {
				return true;
			}
			// instanceof returns false for null
			if(!(o instanceof CameraPreset)) {
				return false;
			}
		
			CameraPreset other = (CameraPreset)o;
			if(presetName == null) {
				if(other.presetName != null) {
					return false;
				}
			} else {
				if(!presetName.equals(other.presetName)) {
					return false;
				}
			}
			if(resolution == null) {
				if(other.resolution != null) {
					return false;
				}
			} else {
				if(!resolution.equals(other.resolution)) {
					return false;
				}
			}
			if(cameraMode == null) {
				if(other.cameraMode != null) {
					return false;
				}
			} else {
				if(!cameraMode.equals(other.cameraMode)) {
					return false;
				}
			}
			if(Math.abs(frameRate - other.frameRate) > EPSILON) {
				return false;
			}

			if(Math.abs(bandwidth - other.bandwidth) > EPSILON) {
				return false;
			}
			return true;
		}
	}
}