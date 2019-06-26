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

import rapid.ext.astrobee.DiskInfo;
import rapid.ext.astrobee.DiskInfoConfig;


public class DiskInfoGds { 
	private String diskName;
	private long dataSize;
	private long diskSize;
	
	public DiskInfoGds(DiskInfoConfig ds) {
		diskName = ds.name;
		dataSize = 0;
		diskSize = ds.capacity;
	}
	
	public DiskInfoGds(DiskInfoGds original) {
		diskName = original.diskName;
		dataSize = original.dataSize;
		diskSize = original.diskSize;
	}
	
	public void update(DiskInfo di) {
		dataSize = di.used;
	}
	
	public String getName() {
		return diskName;
	}
	
	public long getDataSize() {
		return dataSize;
	}
	
	public long getDiskSize() {
		return diskSize;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((diskName == null) ? 0 : diskName.hashCode());
		result = prime * result + (int)dataSize;
		result = prime * result + (int)diskSize;
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
		if (obj instanceof DiskInfoGds) {
			DiskInfoGds other = (DiskInfoGds)obj;
			if (diskName == null) {
				if (other.diskName != null) {
					return false;
				}
			} else if (!diskName.equals(other.diskName)) {
				return false;
			}
			if(dataSize != other.dataSize) {
				return false;
			}
			if(diskSize != other.diskSize) {
				return false;
			}
			return true;
		}
		return false;
	}
}
