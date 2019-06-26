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
package gov.nasa.arc.irg.util.ui.workspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Configure a workspace with a default project
 * right now set up to hold plans
 * 
 * @author tecohen
 *
 */
public class WorkspaceConfigurer {

	public static void setupWorkspace() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		createProject("Plans", root);
	}
	
	public static void createProject(String projectName, IWorkspaceRoot root) throws CoreException {
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IProject project = root.getProject(projectName);
		if (!project.exists()){
			project.create(progressMonitor);
		}
		if (!project.isOpen()){
			project.open(progressMonitor);
		}

	}
}
