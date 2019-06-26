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

import gov.nasa.util.PlatformInfo;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * the JFace MessageDialogWithToggle is useless because it doesn't honor 
 * platform button ordering. So, we create our own with analogous,
 * yet more convenient, behavior.
 * @author mallan
 *
 */
public class PoliteMessageDialog extends MessageDialog {
    protected Button dontAskBut;
    protected boolean dontAskValue = false;

    public PoliteMessageDialog(Shell parentShell, 
                               String dialogTitle, 
                               Image dialogTitleImage, 
                               String dialogMessage,
                               int dialogImageType, 
                               String[] dialogButtonLabels, 
                               int defaultIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
    }

    @Override
    protected Control createCustomArea(Composite parent) {
        dontAskBut = new Button(parent, SWT.CHECK);
        dontAskBut.setText("Please don't ask me again");
        // OMFG SWT is f*&%#%g horrendous! AGGHHH 
        dontAskBut.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dontAskValue = dontAskBut.getSelection();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        return dontAskBut;
    }
    
    @Override
    public int open() {
        return super.open();
    }

    public boolean getDontAskValue() {
        return dontAskValue;
    }

    public static boolean openConfirmDialog(final String title, final String message, String key) {
        final IPreferenceStore prefStore = UtilUIActivator.getDefault().getPreferenceStore();
        String prefKey = null;
        if(key != null) {
            prefKey = "DontAsk_"+key;
            if(prefStore.getBoolean(prefKey)) {
                return true;
            }
        }
        String[] buttons = new String[] { "&Ok", "&Cancel" };
        int okIdx = 0;
        if(PlatformInfo.invertButtonOrder()) {
            buttons = new String[] { "&Cancel", "&Ok" };
            okIdx = 1;
        }
        PoliteMessageDialog msgDialog = new PoliteMessageDialog(Display.getDefault().getActiveShell(),
                                                                title, null,
                                                                message,
                                                                MessageDialog.QUESTION,
                                                                buttons,
                                                                okIdx);
        int retVal = msgDialog.open();
        boolean ok = (retVal == okIdx);
        if(msgDialog.getDontAskValue() && prefKey != null) {
            if(ok) {
                prefStore.setValue(prefKey, true);
            }
        }
        return ok;
    }
}
