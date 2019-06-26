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
package gov.nasa.rapid.v2.ui.e4.widget;

import gov.nasa.util.PlatformInfo;
import gov.nasa.util.PlatformInfo.OS;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * Combo box that can go into the view toolbar 
 */
public class ToolCombo<T> extends ContributionItem {
    private Combo      m_combo;
    private CoolItem   m_coolItem;
    protected List<SelectionListener> m_selectionListeners = new ArrayList<SelectionListener>();

    protected int          m_index  = -1;
    protected ArrayList<T> m_values = new ArrayList<T>();

    /**
     * ctor
     */
    public ToolCombo() {
    }

    @Override
    public String getId() {
        return ToolCombo.class.getSimpleName();
    }

    @Override
    public void fill (ToolBar toolBar, int index) {
        ToolItem item = new ToolItem(toolBar, SWT.SEPARATOR);
        Control box = createAgentsCombo(toolBar);
        item.setControl(box);
        item.setWidth(getWidth());
        toolBar.pack(true);
    }

    @Override
    public void fill (CoolBar coolBar, int index) {
        Control box = createAgentsCombo(coolBar);
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
        coolBar.pack(true);
    }

    @Override
    public void fill (Composite parent) {
        createAgentsCombo(parent);
    }

    /**
     * Override this to change the width of your combo in a toolbar
     * @return the width of the combo
     */
    protected int getWidth() {
        return 90;
    }

    public void addSelectionListener(SelectionListener listener){
        if (m_combo == null){
            m_selectionListeners.add(listener);
        }
        else {
            m_combo.addSelectionListener(listener);
        }
    }

    public void setIndex(int index) {
        if(index >= 0 && index < m_values.size()) {
            m_index = index;
            if (m_combo != null){
                m_combo.setText(m_values.get(index).toString());
            } 
        }
        else {
            throw new IndexOutOfBoundsException("index "+index+" is outside range of 0,"+(m_values.size()-1));
        }
    }

    public Listener[] getListeners(int eventType) {
        if(m_combo == null) {
            if(eventType == SWT.Selection) {
                return m_selectionListeners.toArray(new Listener[0]);
            }
            else 
                return new Listener[0];
        }
        else {
            return m_combo.getListeners(eventType);
        }
    }

    public String[] getItems() {
        if(m_combo == null) {
            String[] retVal = new String[m_values.size()];
            for(int i = 0; i < m_values.size(); i++) {
                retVal[i] = m_values.toString();
            }
            return retVal;
        }
        else {
            return m_combo.getItems();
        }
    }

    /**
     * Find item that matches itemName
     * @param itemName
     * @return index, or -1 if not found
     */
    public int findIndex(String itemName) {
        String[] items = getItems();
        for(int i = 0; i < items.length; i++) {
            if(itemName.equals(items[i])) {
                return i;
            }
        }
        return -1;
    }

    public int getIndex() {
        return m_index;
    }

    public String getText() {
        return m_values.get(m_index).toString();
    }

    protected static int s_comboFontReduce = 1;
    /**
     * @param parent
     * @return
     */
    private synchronized Control createAgentsCombo(Composite parent) {
        m_combo = new Combo(parent, SWT.READ_ONLY);

        for (SelectionListener listener : m_selectionListeners){
            m_combo.addSelectionListener(listener);
        }
        m_selectionListeners.clear();

        // only do this on windows because the other platforms seem OK
        if(PlatformInfo.getOS() == OS.Windows) {
            // in order for the combo to fit properly in the toolbar, it must be 20
            // pixels tall or less (otherwise it gets chopped off and looks awful)
            FontData[] fontData = m_combo.getFont().getFontData();
            final int origHeight = m_combo.getFont().getFontData()[0].getHeight();
            for(int f = s_comboFontReduce; f < 4; f++) {
                // this is so f^c&ing g@dd*am ridiculous... all I want to do is change
                // the f^c&ing font size and it requires 10 g@dd*am lines of code. Arggghhh
                final int height = origHeight - f;
                for(int i = 0; i < fontData.length; ++i) {
                    fontData[i].setHeight(height);
                    fontData[i].setStyle(SWT.BOLD);
                }
                final Font newFont = new Font(parent.getDisplay(), fontData);
                m_combo.setFont(newFont);
                m_combo.pack();
                Point sz = m_combo.getSize();
                //logger.debug("Size = "+sz.y);
                if(sz.y < 23) {
                    m_combo.addDisposeListener(new DisposeListener() {
                        public void widgetDisposed(DisposeEvent e) {
                            newFont.dispose();
                        }
                    });  
                    s_comboFontReduce = f;
                    break;
                }
                else {
                    newFont.dispose();
                }
            }
        }
        m_combo.pack();

        return m_combo;
    }

    /** 
     * populate combo
     */
    public void setItems(final T[] values) {
        m_values.clear();
        for(T value : values) {
            m_values.add(value);
        }
        if (m_combo != null){
            for(T value : m_values) {
                m_combo.add(value.toString());
                m_combo.setData(value.toString(), value);
            }
        }
    }

    /** 
     * populate combo
     */
    public void setItems(final List<T> values) {
        m_values.clear();
        for(T value : values) {
            m_values.add(value);
        }
        if (m_combo != null){
            for(T value : m_values) {
                m_combo.add(value.toString());
                m_combo.setData(value.toString(), value);
            }
        }
    }

}
