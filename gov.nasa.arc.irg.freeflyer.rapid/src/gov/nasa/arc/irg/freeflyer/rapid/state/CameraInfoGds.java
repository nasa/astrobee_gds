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
package gov.nasa.arc.irg.freeflyer.rapid.state;

import rapid.ext.astrobee.CameraInfo;
import rapid.ext.astrobee.CameraInfoConfig;
import rapid.ext.astrobee.CameraMode;
import rapid.ext.astrobee.CameraResolution;

/**
 * holds the info about one camera that is contained in 
 * CameraInfoConfig and CameraInfo (see TelemetryConfig.idl)
 * @author ddwheele
 *
 */
public class CameraInfoGds {

	public final String name;
	public final CameraMode mode; 
	private String[] availResolutions;
	private CameraResolution currentResolution;
	private boolean streaming;
	private boolean recording;
	private float frameRate;
	private float bandwidth;
	private final String blank = "-";
	
	public CameraInfoGds() {
		name = blank;
		mode = CameraMode.MODE_FRAMES;
		availResolutions = new String[] {"0x0" };
		currentResolution = CameraResolution.RESOLUTION_640_480;
		streaming = false;
		recording = false;
		frameRate = 0;
		bandwidth = 0;
	}
	
	public String getName() {
		return name;
	}
	
	public CameraInfoGds(CameraInfoGds original) {
		name = new String(original.name);
		mode = original.mode;
		availResolutions = new String[ original.availResolutions.length ];
		for(int i=0; i<original.availResolutions.length; i++) {
			availResolutions[i] = new String(original.availResolutions[i]);
		}
		currentResolution = original.currentResolution;
		streaming = original.streaming;
		recording = original.recording;
		frameRate = original.frameRate;
		bandwidth = original.bandwidth;
	}
	
	public CameraInfoGds(CameraInfoConfig conf) {
		this.name = conf.name;
		this.mode = conf.mode;
	
		int numRes = conf.availResolutions.userData.size();
		this.availResolutions = new String[numRes];
		
		for(int i=0; i<numRes; i++) {
			availResolutions[i] = cameraResolutionToString((CameraResolution)conf.availResolutions.userData.get(i));
		}
	}
	
	public void ingestCameraInfo(CameraInfo camInfo) {
		streaming = camInfo.streaming;
		recording = camInfo.recording;
		currentResolution = camInfo.resolution;
		frameRate = camInfo.frameRate;
		bandwidth = camInfo.bandwidth;
	}

	public String[] getAvailResolutions() {
		return availResolutions;
	}

	public String getCurrentResolutionString() {
		return cameraResolutionToString(currentResolution);
	}

	public boolean isStreaming() {
		return streaming;
	}

	public boolean isRecording() {
		return recording;
	}

	public float getFrameRate() {
		return frameRate;
	}

	public float getBandwidth() {
		return bandwidth;
	}
	
	public String cameraResolutionToString(CameraResolution resolution) {
		if(resolution == null) {
			return "";
		}
		String raw = resolution.toString();
		String[] tokens = raw.split("_");
		return tokens[1] + "_" + tokens[2];
	}
	
	@Override
	public String toString() {
		return name + ", " + mode + ", " + currentResolution + ", streaming=" + streaming + 
				", recording=" + recording + ", frameRate=" + frameRate + ", bandwidth=" + bandwidth;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + ((availResolutions == null) ? 0 : availResolutions.hashCode());
		result = prime * result + ((currentResolution == null) ? 0 : currentResolution.hashCode());
		result = prime * result + (streaming ? 0 : 1);
		result = prime * result + (recording ? 0 : 1);
		result = prime * result + Float.floatToIntBits(frameRate);
		result = prime * result + Float.floatToIntBits(bandwidth);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		float EPSILON = 0.0001f;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof CameraInfoGds) {
			CameraInfoGds other = (CameraInfoGds)obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			if (mode == null) {
				if (other.mode != null) {
					return false;
				}
			} else if (!mode.equals(other.mode)) {
				return false;
			}
			if (availResolutions == null) {
				if (other.availResolutions != null) {
					return false;
				}
			} else if (!availResolutions.equals(other.availResolutions)) {
				return false;
			}
			if (currentResolution == null) {
				if (other.currentResolution != null) {
					return false;
				}
			} else if (!currentResolution.equals(other.currentResolution)) {
				return false;
			}
			
			if (streaming != other.streaming) {
				return false;
			}
			
			if (recording != other.recording) {
				return false;
			}
			
			if(Math.abs(frameRate - other.frameRate) > EPSILON) {
				return false;
			}
			
			if(Math.abs(bandwidth - other.bandwidth) > EPSILON) {
				return false;
			}
			return true;
		}
		return false;
	}
}
