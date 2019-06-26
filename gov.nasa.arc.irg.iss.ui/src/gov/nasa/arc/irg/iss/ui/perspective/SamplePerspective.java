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
package gov.nasa.arc.irg.iss.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SamplePerspective implements IPerspectiveFactory {

	public static final String ID = SamplePerspective.class.getName();
	private final String LEFT_AREA = "LeftArea";
	private final String TOP_LEFT_AREA = "TopLeftArea";
	private final String RIGHT_AREA  = "ImageFolder";
	private final String LOG_FOLDER    = "LogFolder";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		IFolderLayout statusArea = layout.createFolder(TOP_LEFT_AREA, IPageLayout.LEFT, 0.3f, editorArea);
		//statusArea.addView(ViewID.getUniqueID("gov.nasa.arc.irg.spheres.workbench.comms.view.StatusIndicatorView"));
		layout.getViewLayout(TOP_LEFT_AREA).setCloseable(false);
		layout.getViewLayout(TOP_LEFT_AREA).setMoveable(false);

		IFolderLayout leftArea = layout.createFolder(LEFT_AREA, IPageLayout.BOTTOM, 0.3f, TOP_LEFT_AREA);
		//leftArea.addView(ViewID.getUniqueID("gov.nasa.arc.irg.spheres.workbench.comms.view.EchoView"));
		layout.getViewLayout(LEFT_AREA).setCloseable(false);
		layout.getViewLayout(LEFT_AREA).setMoveable(false);
		
		
		IFolderLayout imageArea = layout.createFolder(RIGHT_AREA, IPageLayout.RIGHT, 0.7f, editorArea);
		//imageArea.addView(ViewID.getUniqueID("gov.nasa.arc.irg.spheres.workbench.comms.view.SimpleImageSensorView"));
		layout.getViewLayout(RIGHT_AREA).setCloseable(false);
		layout.getViewLayout(RIGHT_AREA).setMoveable(false);
		
	}

}
