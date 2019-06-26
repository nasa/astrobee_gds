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
package gov.nasa.arc.irg.iss.ui;

import gov.nasa.arc.irg.util.ui.ColorProvider;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class IssColorProvider extends ColorProvider {
	
	public static final IssColorProvider SPHERES_INSTANCE = new IssColorProvider();
	
	public Color PURPLE;
	public Color CYAN;
	public Color YELLOW;
	
	public IssColorProvider() {
		super();
		
	}
	
	@Override
	protected void initializeColors() {
		super.initializeColors();
		
		Display display = Display.getDefault();
		
		PURPLE = new Color(display, 160, 32, 240);
		addDisposable(PURPLE);
		
		CYAN = new Color(display, 0, 255, 255);
		addDisposable(CYAN);
		
		YELLOW = new Color(display, 255, 255, 0);
		addDisposable(YELLOW);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		PURPLE = null;
		CYAN = null;
		YELLOW = null;
	}
}
