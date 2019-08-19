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
package gov.nasa.arc.ff.ocu.handlers;


import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class AboutHandler {
	@Execute
	public void execute(final Shell shell) {
		final URL entry = Platform.getBundle("gov.nasa.arc.ff.ocu").getEntry("about.mappings");
		System.out.println("MAP PATH: "+entry.getPath());
		final StringBuilder aboutMessage = new StringBuilder();
		aboutMessage.append("Astrobee Workbench\n\n");
		try {
			final List<String> readLines = IOUtils.readLines(entry.openStream());
			aboutMessage.append("Built On: "+readLines.get(2).trim()+"\n");
			aboutMessage.append("Built ID:   "+readLines.get(3).trim()+"\n");
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		aboutMessage.append("\nThe Astrobee Control Station is an extension of the Visual Environment for Remote Virtual Exploration (VERVE) that has been customized to operate the Astrobee robot on the International Space Station (ISS).\n");

		aboutMessage.append("\nCopyright © 2019, United States Government as represented by the Administrator of the National Aeronautics and Space Administration. All rights reserved.\n");
		aboutMessage.append("\nVerve Copyright © 2011, United States Government, as represented by the Administrator of the National Aeronautics and Space Administration. All rights reserved.\n");
		
		aboutMessage.append("\nThe Astrobee Control Station framework is licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this application except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.\n");
		aboutMessage.append("\nUnless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.");

        aboutMessage.append("\n\nFor legal notices and disclaimers, see Help->Legal Notices.");


		MessageDialog.openInformation(shell, "About", aboutMessage.toString());
	}
}
