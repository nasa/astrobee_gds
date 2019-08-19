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

import java.util.Vector;

import org.apache.log4j.Logger;

import rapid.ext.astrobee.CameraInfo;
import rapid.ext.astrobee.CameraInfoConfig;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_COMM_STATUS;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_CPU_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_DISK_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_EKF_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_GNC_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_PMC_CMD_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_POSITION;
import rapid.ext.astrobee.TelemetryConfig;
import rapid.ext.astrobee.TelemetryState;

public class TelemetryStateGds {
	private static final Logger logger = Logger.getLogger(TelemetryStateGds.class);
	private TelemetryFrequency positionRate;
	private TelemetryFrequency ekfStateRate;
	private TelemetryFrequency commStatusRate;
	private TelemetryFrequency diskStateRate;
	private TelemetryFrequency cpuStateRate;
	private TelemetryFrequency gncStateRate;
	private TelemetryFrequency pmcCmdStateRate;

	private Vector<CameraInfoGds> cameras;

	private TelemetryState savedTelemetryState; // for when the state arrives before the config

	private boolean doneIngestingConfig = false;

	public TelemetryStateGds() {
		clear();
	}

	public TelemetryStateGds copyFrom(TelemetryStateGds original) {
		positionRate = new TelemetryFrequency(original.positionRate);
		ekfStateRate = new TelemetryFrequency(original.ekfStateRate);
		commStatusRate = new TelemetryFrequency(original.commStatusRate);
		diskStateRate = new TelemetryFrequency(original.diskStateRate);
		cpuStateRate = new TelemetryFrequency(original.cpuStateRate);
		gncStateRate = new TelemetryFrequency(original.gncStateRate);
		pmcCmdStateRate = new TelemetryFrequency(original.pmcCmdStateRate);

		synchronized(cameras) {
			cameras.clear();

			for(CameraInfoGds info : original.cameras) {
				cameras.add(new CameraInfoGds(info));
			}
		}
		if(original.savedTelemetryState != null) {
			savedTelemetryState = new TelemetryState(original.savedTelemetryState);
		} else {
			savedTelemetryState = null;
		}
		return this;
	}

	public void clear() {
		positionRate = new TelemetryFrequency(SETTINGS_TELEMETRY_TYPE_POSITION.VALUE);
		ekfStateRate = new TelemetryFrequency(SETTINGS_TELEMETRY_TYPE_EKF_STATE.VALUE);
		commStatusRate = new TelemetryFrequency(SETTINGS_TELEMETRY_TYPE_COMM_STATUS.VALUE);
		diskStateRate = new TelemetryFrequency(SETTINGS_TELEMETRY_TYPE_DISK_STATE.VALUE);
		cpuStateRate = new TelemetryFrequency(SETTINGS_TELEMETRY_TYPE_CPU_STATE.VALUE);
		gncStateRate = new TelemetryFrequency(SETTINGS_TELEMETRY_TYPE_GNC_STATE.VALUE);
		pmcCmdStateRate = new TelemetryFrequency(SETTINGS_TELEMETRY_TYPE_PMC_CMD_STATE.VALUE);

		cameras = new Vector<CameraInfoGds>();
		for(int i=0; i<3; i++) {
			cameras.add(new CameraInfoGds());
		}
		savedTelemetryState = null;
		doneIngestingConfig = false;
	}

	public synchronized void ingestTelemetryConfig(TelemetryConfig telConfig){
		cameras.clear();

		for(int i=0; i<telConfig.cameras.userData.size(); i++) {
			cameras.add(new CameraInfoGds((CameraInfoConfig)telConfig.cameras.userData.get(i)));
		}

		if(savedTelemetryState != null) {
			ingestTelemetryState(savedTelemetryState);
		}
		doneIngestingConfig = true;
	}

	public synchronized void ingestTelemetryState(TelemetryState telState) {
		positionRate.setFrequency(telState.positionRate);
		ekfStateRate.setFrequency(telState.ekfStateRate);
		commStatusRate.setFrequency(telState.commStatusRate);
		diskStateRate.setFrequency(telState.diskStateRate);
		cpuStateRate.setFrequency(telState.cpuStateRate);
		gncStateRate.setFrequency(telState.gncStateRate);
		pmcCmdStateRate.setFrequency(telState.pmcCmdStateRate);

		if(!doneIngestingConfig) {
			savedTelemetryState = telState;
			return;
		}

		int numCameras = telState.cameras.userData.size();

		if(numCameras != cameras.size()) {
			logger.error("Number of cameras in TelemetryState does not match number of cameras in TelmetryConfig");
			return;
		}

		for(int i=0; i<numCameras; i++) {
			cameras.get(i).ingestCameraInfo((CameraInfo)telState.cameras.userData.get(i));
		}
	}

	public TelemetryFrequency[] getTelemetryFrequencies() {
		return new TelemetryFrequency[] {
				positionRate, ekfStateRate, commStatusRate, diskStateRate, cpuStateRate, gncStateRate, pmcCmdStateRate };
	}

	public Vector<CameraInfoGds> getCameras() {
		return cameras;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(positionRate.toString() + ", ");
		sb.append(ekfStateRate.toString() + ", ");
		sb.append(commStatusRate.toString() + ", ");
		sb.append(diskStateRate.toString() + ", ");
		sb.append(cpuStateRate.toString() + ", ");
		sb.append(gncStateRate.toString() + ", ");
		sb.append(pmcCmdStateRate.toString() + "\n");

		for(CameraInfoGds cam : cameras) {
			sb.append(cam.toString() + "\n");
		}

		return sb.toString();		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((positionRate == null) ? 0 : positionRate.hashCode());
		result = prime * result + ((ekfStateRate == null) ? 0 : ekfStateRate.hashCode());
		result = prime * result + ((commStatusRate == null) ? 0 : commStatusRate.hashCode());
		result = prime * result + ((diskStateRate == null) ? 0 : diskStateRate.hashCode());
		result = prime * result + ((cpuStateRate == null) ? 0 : cpuStateRate.hashCode());
		result = prime * result + ((gncStateRate == null) ? 0 : gncStateRate.hashCode());
		result = prime * result + ((pmcCmdStateRate == null) ? 0 : pmcCmdStateRate.hashCode());
		result = prime * result + ((cameras == null) ? 0 : cameras.hashCode());
		result = prime * result + ((savedTelemetryState == null) ? 0 : savedTelemetryState.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof TelemetryStateGds) {
			TelemetryStateGds other = (TelemetryStateGds)obj;
			if (positionRate == null) {
				if (other.positionRate != null) {
					return false;
				}
			} else if (!positionRate.equals(other.positionRate)) {
				return false;
			}
			if (ekfStateRate == null) {
				if (other.ekfStateRate != null) {
					return false;
				}
			} else if (!ekfStateRate.equals(other.ekfStateRate)) {
				return false;
			}
			if (commStatusRate == null) {
				if (other.commStatusRate != null) {
					return false;
				}
			} else if (!commStatusRate.equals(other.commStatusRate)) {
				return false;
			}
			if (diskStateRate == null) {
				if (other.diskStateRate != null) {
					return false;
				}
			} else if (!diskStateRate.equals(other.diskStateRate)) {
				return false;
			}
			if (cpuStateRate == null) {
				if (other.cpuStateRate != null) {
					return false;
				}
			} else if (!cpuStateRate.equals(other.cpuStateRate)) {
				return false;
			}
			if (gncStateRate == null) {
				if (other.gncStateRate != null) {
					return false;
				}
			} else if (!gncStateRate.equals(other.gncStateRate)) {
				return false;
			}
			if (pmcCmdStateRate == null) {
				if (other.pmcCmdStateRate != null) {
					return false;
				}
			} else if (!pmcCmdStateRate.equals(other.pmcCmdStateRate)) {
				return false;
			}
			if (cameras == null) {
				if (other.cameras != null) {
					return false;
				}
			} else if (!cameras.equals(other.cameras)) {
				return false;
			}
			if (savedTelemetryState == null) {
				if (other.savedTelemetryState != null) {
					return false;
				}
			} else if (!savedTelemetryState.equals(other.savedTelemetryState)) {
				return false;
			}
			return true;
		}
		return false;
	}

	public static class TelemetryFrequency {
		public final String rapidTelemetryType;
		private float frequency;

		public TelemetryFrequency(String typename) {
			this.rapidTelemetryType = typename;
			setFrequency(0);
		}

		public TelemetryFrequency(TelemetryFrequency original) {
			this.rapidTelemetryType = new String(original.rapidTelemetryType);
			this.frequency = original.frequency;
		}

		public float getFrequency() {
			return frequency;
		}

		public void setFrequency(float frequency) {
			this.frequency = frequency;
		}

		public String getRapidTelemetryType() {
			return rapidTelemetryType;
		}

		@Override
		public String toString() {
			return rapidTelemetryType + " = " + frequency + " Hz";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((rapidTelemetryType == null) ? 0 : rapidTelemetryType.hashCode());
			result = prime * result + Float.floatToIntBits(frequency);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			if (obj instanceof TelemetryFrequency) {
				TelemetryFrequency other = (TelemetryFrequency)obj;
				if (rapidTelemetryType == null) {
					if (other.rapidTelemetryType != null) {
						return false;
					}
				} else if (!rapidTelemetryType.equals(other.rapidTelemetryType)) {
					return false;
				}
				if(frequency != other.frequency) {
					return false;
				}
				return true;
			}
			return false;
		}
	}
}
