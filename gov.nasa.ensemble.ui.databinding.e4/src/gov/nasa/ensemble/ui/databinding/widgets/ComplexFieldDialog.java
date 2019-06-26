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
package gov.nasa.ensemble.ui.databinding.widgets;

import gov.nasa.ensemble.ui.databinding.databinding.BoundWidgetFactory;
import gov.nasa.ensemble.ui.databinding.status.IStatusListener;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

public class ComplexFieldDialog extends Dialog {

    // the actual model we are editing.  Changes to the widget immediately change this model (if they are valid).  
    // If you don't want live editing of the model be sure to pass in a clone.
    // note you do not have to keep a reference to the model here; you can simply pass it directly into the widget.
    protected Object m_model;	

    protected AbstractDatabindingWidget m_widget; // the widget which will be auto generated

    protected Label m_errorMessage; // the label for the error message

    protected ScrolledComposite m_scrolledComposite; // the scrolled composite

    protected Composite m_widgetComposite; // the composite which holds the widget

    public static int defaultLabelWidth = 160;
    public static int defaultFieldWidth = 120;

    // optional listener if you want to do anything with your error messages such as display them
    protected IStatusListener m_errorStatusListener = new IStatusListener() {

        public void statusChanged(IStatus status) {
            setErrorMessage(null);
            StringBuffer text = new StringBuffer(getDefaultText());
            if (status != null){
                text = new StringBuffer(status.getMessage());
                if (status instanceof MultiStatus){
                    MultiStatus ms = (MultiStatus)status;
                    for (IStatus s : ms.getChildren()){
                        text.append(s.getMessage());
                        text.append("\n");
                    }
                }
                if (status.getSeverity() == IStatus.WARNING){
                    setErrorMessage(text.toString());
                } else {
                    setErrorMessage(text.toString());
                }
            }
        }
    };

    protected ComplexFieldDialog(Shell parentShell, Object model) {
        super(parentShell);
        setBlockOnOpen(false);
        m_model = model;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(m_model.getClass().getSimpleName());
    }

    @Override
    protected void setShellStyle(int newShellStyle) 
    {	
        int newstyle = newShellStyle & ~SWT.APPLICATION_MODAL; /* turn off APPLICATION_MODAL */
        newstyle |= SWT.MODELESS; /* turn on MODELESS */
        newstyle |= SWT.RESIZE;   /* allow resizing */
        super.setShellStyle(newstyle); 
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        Composite c = new Composite(parent, SWT.NONE);
        FillLayout fl = new FillLayout();
        c.setLayout(fl);

        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.widthHint = 350;
        gd.heightHint = 150;
        gd.minimumHeight = 150;
        gd.minimumWidth = 300;
        c.setLayoutData(gd);

        m_scrolledComposite = new ScrolledComposite(c, SWT.H_SCROLL|SWT.V_SCROLL );
        m_scrolledComposite.setLayout(new GridLayout());
        m_scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL));

        if (m_widgetComposite == null){
            m_widgetComposite = new Composite(m_scrolledComposite, SWT.NONE);
        }
        m_widgetComposite.setLayout(new GridLayout());
        GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        m_widgetComposite.setLayoutData(gd2);

        m_errorMessage = new Label(m_widgetComposite, SWT.NONE);
        m_errorMessage.setText(getDefaultText());
        m_errorMessage.setLayoutData(gd2);

        createWidget(m_widgetComposite, false);

        // support auto scrolling 
        m_widgetComposite.setSize(m_widgetComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        m_scrolledComposite.setContent(m_widgetComposite);

        setModel(m_model);
        return m_scrolledComposite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button close = createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL,true);
        close.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                close();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                //
            }

        });
    }

    /**
     * Create the actual widget
     * @param container
     */
    protected void createWidget(Composite container, boolean forceAllFields){
        // add the widget on the left
        try {
            m_widget = BoundWidgetFactory.getWidget(m_model.getClass(), container, SWT.NONE, false, forceAllFields);

            // add the listener to update the multi-error display at the top of the view
            m_widget.addListener(m_errorStatusListener);

            m_widget.setLabelWidth(defaultLabelWidth);
            m_widget.setFieldWidth(defaultFieldWidth);

        } catch (NullPointerException e1) {
            // do nothing
        } catch (ClassNotFoundException e1) {
            // do nothing
        } catch (InstantiationException e) {
            // do nothing
        }
    }

    /**
     * @return the default text used in the title
     */
    public String getDefaultText(){
        return "";
    }

    /**
     * Set the model; this will fill the widget if it exists
     * @param model
     */
    public void setModel(final Object model){
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (m_widget != null){
                    setErrorMessage("");
                    m_widget.setModel(model);
                }
            }

        });
    }

    /**
     * Set the error message for display
     * @param message
     */
    protected void setErrorMessage(final String message){
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                if (message == null){
                    m_errorMessage.setText("");
                } else {
                    m_errorMessage.setText(message);
                }
                m_errorMessage.redraw();
                m_errorMessage.getParent().layout();
            }
        });
    }


    @Override
    protected Point getInitialLocation(Point initialSize) {
        Point cursorLocation = getShell().getDisplay().getCursorLocation();
        Composite parent = getShell().getParent();

        Monitor monitor = getShell().getDisplay().getPrimaryMonitor();
        if (parent != null) {
            monitor = parent.getMonitor();
        }

        Rectangle monitorBounds = monitor.getClientArea();

        int x = cursorLocation.x - (initialSize.x / 2);
        int ctrY = cursorLocation.y - (initialSize.y * 2/3);
        int minY = Math.min(ctrY, monitorBounds.y+monitorBounds.height - initialSize.y);
        int y = Math.max(monitorBounds.y, minY);
        return new Point(x,y);
    }


}
