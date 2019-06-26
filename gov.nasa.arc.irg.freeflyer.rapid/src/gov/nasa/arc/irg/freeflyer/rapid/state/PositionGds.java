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

import rapid.Mat33f;
import rapid.PositionConfig;
import rapid.PositionSample;
import rapid.RotationEncoding;
import rapid.Vec3d;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;

public class PositionGds {
	private PositionConfig positionConfig;
	private PositionSample positionSample;

	public PositionGds copyFrom(PositionGds original) {
		if(original.positionConfig != null) {
			if(positionConfig == null) {
				positionConfig = new PositionConfig();
			}
			positionConfig.copy_from(original.positionConfig);
		} else {
			positionConfig = null;
		}

		if(original.positionSample != null) {
			if(positionSample == null) {
				positionSample = new PositionSample();
			}
			positionSample.copy_from(original.positionSample);
		} else {
			positionSample = null;
		}

		return this;
	}

	public void ingestPositionSample(PositionSample sample) {
		positionSample = sample;
	}

	public void ingestPositionConfig(PositionConfig pc) {
		positionConfig = pc;
	}

	/** returns current position of Astrobee */
	public Vector3 getXYZ() {
		if(positionSample != null && positionConfig != null) { 
			int sampleID = positionSample.hdr.serial;
			int configID = positionConfig.hdr.serial;
			if (configID == sampleID){
				double[] xyz = positionSample.pose.xyz.userData;
				return new Vector3(xyz[0], xyz[1], xyz[2]);
			}
		}
		return new Vector3();
	}

	/** returns raw position of Astrobee, or origin if no positionSample received yet */
	public Vec3d getVec3d() {
		if(positionSample != null) 
			return positionSample.pose.xyz;
		else
			return new Vec3d();
	}

	/** returns raw rotation of Astrobee, or identity if no positionSample received yet */
	public Mat33f getMat33f() {
		if(positionSample != null) {
			return positionSample.pose.rot;
		} else {
			Mat33f ret = new Mat33f();
			ret.userData[3] = 1;
			return ret;
		}
	}

	/** returns rotation of Astrobee as Quaternion */
	public Quaternion getQuaternion() {
		if(positionSample != null) {
			int sampleID = positionSample.hdr.serial;
			int configID = positionConfig.hdr.serial;
			if (configID == sampleID){
				if(positionConfig.poseEncoding.equals(RotationEncoding.RAPID_ROT_QUAT)) {
					return new Quaternion(positionSample.pose.rot.userData[0],
							positionSample.pose.rot.userData[1],
							positionSample.pose.rot.userData[2],
							positionSample.pose.rot.userData[3]);
				}
			}
		}
		return new Quaternion();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((positionConfig == null) ? 0 : positionConfig.hashCode());
		result = prime * result + ((positionSample == null) ? 0 : positionSample.hashCode());
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
		if (obj instanceof PositionGds) {
			PositionGds other = (PositionGds) obj;

			if (positionConfig == null) {
				if (other.positionConfig != null) {
					return false;
				}
			} else if (!positionConfig.equals(other.positionConfig)) {
				return false;
			}
			if (positionSample == null) {
				if (other.positionSample != null) {
					return false;
				}
			} else if (!positionSample.equals(other.positionSample)) {
				return false;
			}
		}
		return true;
	}

}
