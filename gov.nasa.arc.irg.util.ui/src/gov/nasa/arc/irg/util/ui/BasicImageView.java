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
package gov.nasa.arc.irg.util.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

public class BasicImageView extends ViewPart {
	public static String ID = BasicImageView.class.getName();
		
	protected Image	m_image = null;
	protected Label	m_label	= null;
	protected String m_initialImage = "mars_b_sm.jpg";
	
	@Override
	public void createPartControl(Composite parent) {
		m_label = new Label(parent, SWT.NONE);
		//loadInitialImage();
	}

	@Override
	public void setFocus() {
		// do nothing
	}

	protected void loadInitialImage() {
		Bundle bundle = UtilUIActivator.getDefault().getBundle();
		
		if(bundle != null ) {
			Path path = new Path("images/" + m_initialImage);
			URL bndlURL = FileLocator.find(bundle, path, null);	
			if(bndlURL != null)
			{
				try {
					URL fileURL = FileLocator.toFileURL(bndlURL);
					String fileString = fileURL.getPath();
					
					m_image = new Image(null, fileString);
					
					m_label.setImage(m_image);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
