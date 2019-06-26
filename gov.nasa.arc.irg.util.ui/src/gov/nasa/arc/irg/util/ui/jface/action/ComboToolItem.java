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
package gov.nasa.arc.irg.util.ui.jface.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Put a combo dropdown in a toolbar or coolbar
 * @author tecohen
 *
 */
public class ComboToolItem extends ContributionItem {
    private Composite m_composite;
    private Combo m_combo;
    private CoolItem m_coolItem;
    private ToolItem m_toolItem;

    protected List<? extends Object> m_options;
    protected List<SelectionListener> m_selectionListeners = new ArrayList<SelectionListener>();

    public ComboToolItem() {

    }

    public ComboToolItem(List<? extends Object> options) {
        m_options = options;
    }

    @Override
    public void fill (ToolBar parent, int index) {
        m_toolItem = new ToolItem(parent, SWT.SEPARATOR);
        Control box = createCombo(parent);
        m_toolItem.setControl(box);
        m_toolItem.setWidth(getWidth());
    }

    @Override
    public void fill (CoolBar coolBar, int index) {
        Control box = createCombo(coolBar);
        int flags = SWT.DROP_DOWN;
        if (index >= 0) {
            m_coolItem = new CoolItem(coolBar, flags, index);
        }
        else {
            m_coolItem = new CoolItem(coolBar, flags);
        }
        m_coolItem.setData(this);
        m_coolItem.setControl(box);
        Point toolBarSize = box.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        m_coolItem.setMinimumSize(toolBarSize);
        m_coolItem.setPreferredSize(toolBarSize);
        m_coolItem.setSize(toolBarSize);
    }

    /**
     * Override this to change the width of your combo in a toolbar
     * @return the width of the combo
     */
    protected int getWidth() {
        return 85;
    }

    public void addSelectionListener(SelectionListener listener){
        m_selectionListeners.add(listener);
        if (m_combo != null){
            m_combo.addSelectionListener(listener);
        }
    }

    public Object getSelection() {
        if (m_combo != null && m_combo.getText() != null){
            return m_combo.getData(m_combo.getText());
        }
        return null;
    }

    /**
     * @param parent
     * @return
     */
    private Control createCombo (Composite parent) {
        m_composite = new Composite(parent, SWT.NONE);
        GridData gd = new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
        m_composite.setLayoutData(gd);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        layout.numColumns = 1;
        m_composite.setLayout(layout);
        m_combo = new Combo(m_composite, SWT.READ_ONLY);
        if (m_options != null && !m_options.isEmpty()){
            for (Object entry : m_options) {
                m_combo.add(entry.toString());
                m_combo.setData(entry.toString(), entry);
            }
        }
        GridData gd2 = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        m_combo.setLayoutData(gd2);
        for (SelectionListener listener : m_selectionListeners){
            m_combo.addSelectionListener(listener);
        }
        return m_composite;
    }

    @Override
    public void fill (Composite parent) {
        createCombo(parent);
    }

    public void select(int index){
        m_combo.select(index);
    }

    public boolean select(Object entry) {
        if (m_combo != null) {
            int index = m_options.indexOf(entry);
            if (index >= 0){
                select(index);
                return true;
            }
        }
        return false;
    }

    public List<? extends Object> getOptions() {
        return m_options;
    }

    public void setOptions(List<? extends Object> options) {
        m_options = options;
        m_combo.removeAll();
        for (Object entry : options){
            m_combo.add(entry.toString());
            m_combo.setData(entry.toString(), entry);
        }

    }

    public void setEnabled(boolean enabled){
        m_combo.setEnabled(enabled);
    }

    public void setBackground(Color background){
        if (m_toolItem != null){
            m_toolItem.getControl().setBackground(background);
        }
        if (m_coolItem != null){
            m_coolItem.getControl().setBackground(background);
        }
        if (m_composite != null){
            m_composite.setBackground(background);
        }

    }

    public int getSelectionIndex(){
        return m_combo.getSelectionIndex();
    }
}
