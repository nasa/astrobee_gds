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

/**
 * Eclipse 3.7.x disposes and reconstructs hidden views unless they are referenced by another perspective.
 * Extend this perspective to include your view to keep that from happening
 * @author tecohen
 *
 */
public class HiddenPerspective implements IPerspectiveFactory {

	protected static final String LEFT_AREA = "LeftArea";
	protected static final String TOP_LEFT_AREA = "TopLeftArea";
	protected static final String RIGHT_AREA  = "ImageFolder";
	protected IFolderLayout m_folderLayout;

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		 m_folderLayout = layout.createFolder(TOP_LEFT_AREA, IPageLayout.LEFT, 0.3f, editorArea);

	}

}
