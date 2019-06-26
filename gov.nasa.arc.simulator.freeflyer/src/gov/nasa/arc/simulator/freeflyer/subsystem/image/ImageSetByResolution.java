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
package gov.nasa.arc.simulator.freeflyer.subsystem.image;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.ImageData;

import rapid.ext.astrobee.CameraResolution;

public class ImageSetByResolution {
	private Map<CameraResolution, Vector<ImageData>> imagesByResolution =
			new HashMap<CameraResolution, Vector<ImageData>>();

	public ImageSetByResolution(String imgDirectory) {
		loadImages(imgDirectory);
	}
	
	public Vector<ImageData> getImages(CameraResolution res) {
		return imagesByResolution.get(res);
	}

	public Set<CameraResolution> getResolutions() {
		return imagesByResolution.keySet();
	}
	
	private void loadImages(String dir) {
		// go through the subdirectories of dir
		for(CameraResolutionPlus res : CameraResolutionPlus.values()) {
			String fullDir = dir + File.separator + res.getFolderName();
			File imgDir = new File(fullDir);
			
			if(imgDir == null) {
				continue;
			}
			Vector<ImageData> pics = new Vector<ImageData>();
			
			// if the subdirectory exists
			// load all the files from it into images map
			File[] look = imgDir.listFiles();
			if(look == null) {
				continue;
			}
			
			for(File f : look) {
				if (f == null || f.isDirectory()) {
					continue;
				}

				String fpath = f.getPath();

				try {
					pics.add(new ImageData(fpath));
				} catch(SWTException e) {
					continue;
				}
			}

			if(pics.size() > 0) {
				imagesByResolution.put(res.getCameraResolution(), pics);
			}
		}
	}
}
