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

import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class SwitchWorkspaceHandler extends AbstractHandler {

    @SuppressWarnings("unused")
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        PickWorkspaceDialog pwd = new PickWorkspaceDialog(true, null); 
        int pick = pwd.open(); 
        if (pick == Dialog.CANCEL){ 
            return null; 
        }

        MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Switch Workspace", "The client will now restart with the new workspace"); 

        // restart client 
        PlatformUI.getWorkbench().restart();

        return null;
    }

    public static Location setupWorkspace(Display display) {
        Location instanceLoc = Platform.getInstanceLocation();

        try {

            // get what the user last said about remembering the workspace location 
            boolean remember = PickWorkspaceDialog.isRememberWorkspace(); 

            // get the last used workspace location 
            String lastUsedWs = PickWorkspaceDialog.getLastSetWorkspaceDirectory(); 

            // if we have a "remember" but no last used workspace, it's not much to remember 
            if (remember && (lastUsedWs == null || lastUsedWs.length() == 0)) { 
                remember = false; 
            } 

            // check to ensure the workspace location is still OK 
            if (remember) { 
                // if there's any problem whatsoever with the workspace, force a dialog which in its turn will tell them what's bad 
                String ret = PickWorkspaceDialog.checkWorkspaceDirectory(Display.getDefault().getActiveShell(), lastUsedWs, false, false); 
                if (ret != null) { 
                    remember = false; 
                } 

            } 

            // if we don't remember the workspace, show the dialog 
            if (!remember) { 
                PickWorkspaceDialog pwd = new PickWorkspaceDialog(false, null); 
                int pick = pwd.open(); 

                // if the user cancelled, we can't do anything as we need a workspace, so in this case, we tell them and exit 
                if (pick == Window.CANCEL) { 
                    if (pwd.getSelectedWorkspaceLocation()  == null) { 
                        MessageDialog.openError(display.getActiveShell(), "Error", 
                                "The application can not start without a workspace root and will now exit."); 
                        try { 
                            PlatformUI.getWorkbench().close(); 
                        } 
                        catch (Exception err) { 
                            // ignore
                        } 
                        System.exit(0); 
                        return null;
                    } 
                } 
                else { 
                    // tell Eclipse what the selected location was and continue 
                    instanceLoc.set(new URL("file", null, pwd.getSelectedWorkspaceLocation()), false); 
                } 
            } 
            else { 
                // set the last used location and continue 
                instanceLoc.set(new URL("file", null, lastUsedWs), false); 
            }    

            WorkspaceConfigurer.setupWorkspace();
            return instanceLoc;
        } catch (Exception ex){
            return instanceLoc;
        }

    }

}
