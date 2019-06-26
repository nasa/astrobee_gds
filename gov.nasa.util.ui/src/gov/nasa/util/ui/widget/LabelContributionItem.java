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
package gov.nasa.util.ui.widget;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A Label contribution item, currently used for timestamps.
 * This allows you to put plain labels into toolbars.
 * 
 * @author tecohen
 *
 */
public class LabelContributionItem extends ContributionItem {
    protected Label m_label;

    public LabelContributionItem(String id) {
        super(id);
    }

    @Override
    public void fill(ToolBar parent, int index) {
        ToolItem item = new ToolItem(parent, SWT.SEPARATOR);
        Control box = createLabel(parent);
        item.setControl(box);
        item.setWidth(60);
    }

    @Override
    public void fill(Composite parent) {
        createLabel(parent);
    }

    private Control createLabel(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        // layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        layout.numColumns = 1;
        top.setLayout(layout);
        m_label = new Label(top, SWT.NONE);
        GridData gd = new GridData(SWT.END, SWT.CENTER, false, false);
        gd.horizontalAlignment = SWT.END;
        gd.verticalAlignment = SWT.CENTER;
        m_label.setLayoutData(gd);
        return top;
    }

    public void updateText(String text) {
        if (m_label != null) {
            m_label.setText(text);
            m_label.redraw();
            m_label.getParent().layout();
        }
    }
}
