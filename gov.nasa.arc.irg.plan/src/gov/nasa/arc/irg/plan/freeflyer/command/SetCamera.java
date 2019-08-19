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
package gov.nasa.arc.irg.plan.freeflyer.command;

import gov.nasa.arc.irg.plan.freeflyer.config.OptionsForOneCamera;
import gov.nasa.arc.irg.plan.util.PlanConstants;

import org.codehaus.jackson.annotate.JsonIgnore;

public class SetCamera extends FreeFlyerCommand {
	protected String cameraName;
	protected String presetIndex; // databinding just gives us the selectedIndex in the Combo
	// can't do the fancy convert-index-to-object that we do with the others, because this Combo
	// changes depending on which command is selected
	protected OptionsForOneCamera options;

	// copy these to top level for FSW
	protected String cameraMode;
	protected String presetName;
	protected String resolution;
	protected float frameRate;
	protected float bandwidth;

	public String getCameraMode() {
		return cameraMode;
	}

	public void setCameraMode(String cameraMode) {
		String old = this.cameraMode;
		this.cameraMode = cameraMode;
		firePropertyChange("cameraMode", old, cameraMode);
	}

	public String getCameraName() {
		return cameraName;
	}

	public void setCameraName(String cameraName) {
		String old = this.cameraName;
		this.cameraName = cameraName;
		firePropertyChange("cameraName", old, cameraName);
	}
	
	@Override
	public int getCalculatedDuration() {
		return 0;
	}

	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Set Camera";
	}

	@Override
	@JsonIgnore
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder(getName());

		if(cameraName != null) {
			sb.append( " " + cameraName);
		} else {
			sb.append(" " + PlanConstants.UNKNOWN_CHARACTER);
		}

		if(presetName != null) {
			sb.append(" " + presetName);
		}
		else {
			sb.append(" " + PlanConstants.UNKNOWN_CHARACTER);
		}

		return sb.toString();
	}

	public String getPresetIndex() {
		return presetIndex;
	}

	public void setPresetIndex(String presetIndex) {
		String old = this.presetIndex;
		this.presetIndex = presetIndex;

		copyInfoFromOptions();

		firePropertyChange("presetIndex", old, presetIndex);
	}

	protected void copyInfoFromOptions() {
		if(options == null || presetIndex == null) {
			return;
		}
		int presetInd = Integer.valueOf(presetIndex);
		OptionsForOneCamera.CameraPreset preset = options.getPresetNumber(presetInd);
		// XXX do we have to set these through the setters for JSON to rewrite them???
		cameraMode = preset.getCameraMode();
		presetName = preset.getPresetName();
		resolution = preset.getResolution();
		bandwidth = preset.getBandwidth();
		frameRate = preset.getFrameRate();
	}

	public OptionsForOneCamera getOptions() {
		return options;
	}

	public void setOptions(OptionsForOneCamera options) {
		OptionsForOneCamera old = this.options;
		this.options = options;
		copyInfoFromOptions();
		firePropertyChange("options", old, options);
	}

	public String getPresetName() {
		return presetName;
	}

	public void setPresetName(String presetName) {
		String old = this.presetName;
		this.presetName = presetName;
		firePropertyChange("presetName", old, presetName);
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		String old = this.resolution;
		this.resolution = resolution;
		firePropertyChange("resolution", old, resolution);
	}

	public float getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(float frameRate) {
		float old = this.frameRate;
		this.frameRate = frameRate;
		firePropertyChange("frameRate", old, frameRate);
	}

	public float getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(float bandwidth) {
		float old = this.bandwidth;
		this.bandwidth = bandwidth;
		firePropertyChange("bandwidth", old, bandwidth);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cameraMode == null) ? 0 : cameraMode.hashCode());
		result = prime * result + ((cameraName == null) ? 0 : cameraName.hashCode());
		result = prime * result + ((presetIndex == null) ? 0 : presetIndex.hashCode());
		result = prime * result + ((options == null) ? 0 : options.hashCode());
		result = prime * result + ((presetName == null) ? 0 : presetName.hashCode());
		result = prime * result + ((resolution == null) ? 0 : resolution.hashCode());

		result = prime * result + Float.floatToIntBits(frameRate);
		result = prime * result + Float.floatToIntBits(bandwidth);

		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		float EPSILON = 0.001f;

		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof SetCamera)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}

		SetCamera other = (SetCamera)o;

		if (cameraMode == null) {
			if (other.cameraMode != null) {
				return false;
			}
		} else {
			if (!cameraMode.equals(other.cameraMode)) {
				return false;
			}
		}

		if(cameraName == null) {
			if(other.cameraName != null) {
				return false;
			}
		} else {
			if(!cameraName.equals(other.cameraName)) {
				return false;
			}
		}

		if(presetIndex == null) {
			if(other.presetIndex != null) {
				return false;
			}
		} else {
			if(!presetIndex.equals(other.presetIndex)) {
				return false;
			}
		}

		if(options == null) {
			if(other.options != null) {
				return false;
			}
		} else {
			if(!options.equals(other.options)) {
				return false;
			}
		}

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

		if(Math.abs(frameRate - other.frameRate) > EPSILON) {
			return false;
		}

		if(Math.abs(bandwidth - other.bandwidth) > EPSILON) {
			return false;
		}
		return true;
	}
}
