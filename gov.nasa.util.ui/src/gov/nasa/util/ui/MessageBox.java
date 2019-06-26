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
package gov.nasa.util.ui;


import gov.nasa.util.PlatformInfo;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MessageBox {
    
	/**
	 * pop up an error dialog
	 * @param title
	 * @param msg
	 */
	public static void error(final String title, final String msg) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				//System.err.println(title + " : " + msg);
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						title,
						msg);
			}
		});
	}

	/**
	 * pop up an error dialog with exception information
	 * @param title
	 * @param msg
	 * @param e
	 */
	public static void error(final String title, final String msg, Throwable e)
	{
		String exmsg = "";
		if(e != null) {
			exmsg  = "\n";
			exmsg += "\nThe following exception(s) caused this error: ";
			exmsg += "\n[" + e.getClass().getSimpleName() + "]"+(e.getMessage() == null ? "" : " : \n  "+e.getMessage());
			Throwable cause = e.getCause();
			while(cause != null) {
				exmsg += "\n    Caused by: ["+cause.getClass().getSimpleName()+"]"+(cause.getMessage() == null ? "" : " : \n      "+cause.getMessage());
				cause = cause.getCause();
			}
		}
		
		final int maxSize = 300;
		final String eMsg;
		if(exmsg.length() <= maxSize)
			eMsg = exmsg;
		else
			eMsg = exmsg.substring(0, maxSize) + "...<TRUNCATED>";
		
		if (e != null) 
			e.printStackTrace();
		
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						title,
						msg + eMsg);
			}
		});
	}
	
	/**
	 * pop up a warning dialog
	 * @param title
	 * @param message
	 */
    public static void warn(final String title, final String message) {
        warn(title, message, Display.getDefault().getActiveShell());
    }

    public static void warn(final String title, final String message, final Shell shell) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                MessageDialog.openWarning(shell,
                        title,
                        message);
            }
        });
    }

	/**
	 * Opens a confirm dialog with a "Please don't ask again" check. To persist value,
	 * the title is appended to the key class name.
	 */
    public static boolean confirm(final String title, final String message, final Object key) {
        String saferTitle = title.replace(' ', '_');
        String msgKey = null;
        if(key != null) {
            msgKey = key.getClass().getName()+"-"+saferTitle;
        }
        return PoliteMessageDialog.openConfirmDialog(title, message, msgKey);
    }
    
    public static boolean confirm(final String title, final String message) {
        String[] buttons = new String[] { "&OK", "&Cancel" };
        int okIdx = 0;
        if(PlatformInfo.invertButtonOrder()) {
            buttons = new String[] { "&Cancel", "&OK" };
            okIdx = 1;
        }
        MessageDialog msgDialog = new MessageDialog(
                Display.getDefault().getActiveShell(),
                title, null,
                message,
                MessageDialog.QUESTION,
                buttons,
                okIdx);
        return (msgDialog.open() == okIdx);
    }
    
	public static boolean confirmDialogNoImage(final String title, final String message) {
        String[] buttons = new String[] { "&OK", "&Cancel" };
        int okIdx = 0;
        if(PlatformInfo.invertButtonOrder()) {
            buttons = new String[] { "&Cancel", "&OK" };
            okIdx = 1;
        }
        MessageDialog msgDialog = new MessageDialog(
                Display.getDefault().getActiveShell(),
                title, null,
                message,
                MessageDialog.NONE,
                buttons,
                okIdx);
        return (msgDialog.open() == okIdx);
	}
}
