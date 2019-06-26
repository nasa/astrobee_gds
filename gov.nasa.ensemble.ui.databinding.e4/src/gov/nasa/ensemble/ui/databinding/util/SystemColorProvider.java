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
package gov.nasa.ensemble.ui.databinding.util;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Color;

public class SystemColorProvider {
	
	final private static Logger logger = Logger.getLogger(SystemColorProvider.class);

	protected static boolean s_initialized = false;
	
	protected static Map<String, Color> s_colorMap = new HashMap<String, Color>();

	protected static void initialize() {
		if (s_initialized){
			return;
		}
		ArrayList<String> colorList = new ArrayList<String>();
		// get both our own and the draw2d color constants
		for (Field f : ColorConstants.class.getFields()) {
			if (Color.class.isAssignableFrom(f.getType()) &&
					((f.getModifiers() & Modifier.STATIC) > 0)){
				String name = f.getName();
				//remove the colors that are non-intuitive as to what they mean
				if (!name.equals("getDisplay") && !name.startsWith("title") && !name.startsWith("menu") &&
						!name.startsWith("button") && !name.endsWith("ground"))
					colorList.add(name);
			}
		}
		Collections.sort(colorList);
		
		for (String c : colorList){
			try {
				Field colorField  = ColorConstants.class.getField(c);
				Color color = (Color) colorField.get(null);
				s_colorMap.put(c, color);
			} catch (SecurityException e) {
				logger.warn(e);
			} catch (IllegalArgumentException e) {
				logger.warn(e);
			} catch (NoSuchFieldException e) {
				logger.warn(e);
			} catch (IllegalAccessException e) {
				logger.warn(e);
			}
		}
		s_initialized = true;
	}
	
	public static Color getColor(String name){
		initialize();
		return s_colorMap.get(name);
	}
	
	public static Set<String> getColorNames() {
		initialize();
		return s_colorMap.keySet();
	}
}
