/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.ardor3d.effect.texture;

import gov.nasa.arc.verve.common.DataBundleHelper;

import org.apache.log4j.Logger;

import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture2D;

public class GradientTexture {
	private static Logger logger = Logger.getLogger(GradientTexture.class);
	
	public enum Type {
		GreyScale           ("images/gradient_greyscale.png"),
		Hue                 ("images/gradient_hue_full_saturation.png"),
		HueSaturation       ("images/gradient_hue_saturation_ramp.png"),
		MirrorGreyScale     ("images/mirror_gradient_greyscale.png"),
		MirrorHue           ("images/mirror_gradient_hue_full_saturation.png"),
		MirrorHueSaturation ("images/mirror_gradient_hue_saturation_ramp.png"),
		;

		public final String filename;

		Type(String filename) {
			this.filename = filename;
		}
	}

	/**
	 * note: we rely on the Ardor3d texture cache to avoid reloading multiple copies of the texture
	 * @param type
	 * @return texture for gradient type. May return null if there was an error generating the texture. 
	 */
	public static Texture2D get(Type type) {
		final String b = "ardor3d.effect";
		try {
			return DataBundleHelper.loadTexture(b, type.filename, Texture.WrapMode.EdgeClamp);
		}
		catch(Throwable t) {
			logger.error("Failed to load "+type.toString()+" texture.", t);
		}
		return null;
	}
	
	public static Type getType(String name) {
		Type retVal = Type.Hue;
		try {
			retVal = Type.valueOf(name);
		}
		catch(Throwable t) {
			// ignore and return default
		}
		return retVal;
	}
}
