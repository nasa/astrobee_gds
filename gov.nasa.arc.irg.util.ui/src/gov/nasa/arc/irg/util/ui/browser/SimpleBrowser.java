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
package gov.nasa.arc.irg.util.ui.browser;

import gov.nasa.arc.irg.util.ui.IrgUI;
import gov.nasa.arc.irg.util.ui.UtilUIActivator;
import gov.nasa.util.ui.LastPath;
import gov.nasa.util.ui.TextInputDialog;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

/**
 * Super simple web browser example
 */
public class SimpleBrowser extends ViewPart  {
    private Logger logger = Logger.getLogger(SimpleBrowser.class);

    public  static String ID = SimpleBrowser.class.getName();

    private static int    s_instanceCount = 0;

    protected Composite	topComposite = null;
    GridLayout	topLayout	 = null;

    Composite   locComposite = null;
    GridLayout	locLayout	 = null;

    ToolBar		m_toolbar1;
    ToolBar		m_toolbar2;
    ToolItem[]	m_buttons = new ToolItem[Buttons.values().length];
    Text		m_locationEdit;
    protected Browser		m_browser;

    GoMenuCreator m_goMenuCreator;

    String 		m_homePage = "";
    String		m_lastPage = "";
    List<String> m_recentSiteList = new ArrayList<String>();

    static int s_ButtonIndex = 0;
    enum Buttons {
        Back   ("&Back",   "browse_back"),
        Forward("&Forward","browse_forward"),
        Reload ("&Reload", "browse_refresh"),
        Stop   ("&Stop",   "browse_stop"),
        Home   ("&Home",   "browse_home"),
        Go     ("&Go",     "browse_go"),    
        Save   ("&Save",   "browse_home"),    
        ;
        final int    index;
        final String text;
        final ImageDescriptor imageDescriptor;
        final Image image;
        Action 		 action = null;
        Buttons(String text, String iconName) {
            this.index = s_ButtonIndex++;
            this.text  = text;
            this.imageDescriptor = UtilUIActivator.getImageDescriptorFromRegistry(iconName);
            this.image = UtilUIActivator.getImageFromRegistry(iconName);
        }
    }

    private Cursor m_waitCursor = null;
    static SimpleBrowser s_hackInstance = null;
    private boolean m_doLoadLastUrl = true;

    protected static Set<LocationListener> s_listenerExtensions;  // list to store extension point location listeners

    /**
     * Very simple browser widget
     */
    //===================================================
    public SimpleBrowser() {
        super();
        ++s_instanceCount;

        setTitleToolTip("Web Browser");

        /* get the home page */
        m_homePage = UtilUIActivator.getDefault().getPreferenceStore().getString(
                                                                                 SimpleBrowserPreferenceKeys.P_BROWSER_HOME_PAGE);
        loadRecentSites();
        s_hackInstance = this;

    }

    public static int getInstanceCount() {
        return s_instanceCount;
    }

    public static SimpleBrowser getHackInstance() {
        return s_hackInstance;
    }

    public Browser getBrowser() {
        return m_browser;
    }

    public boolean doBack() {
        boolean retVal = m_browser.back();
        return retVal;
    }

    public boolean doForward() {
        boolean retVal = m_browser.forward();
        return retVal;
    }

    public boolean doReload() {
        m_browser.refresh();
        return true;
    }

    public boolean doStop() {
        m_browser.stop();
        return true;
    }

    public boolean doHome() {
        m_browser.setUrl(m_homePage);
        return true;
    }

    public boolean doLoadUrl(String url) {
        m_browser.setUrl(url);
        return true;
    }

    public boolean doGo() {
        if(m_locationEdit != null) {
            return m_browser.setUrl(m_locationEdit.getText());
        }
        return false;
    }

    public boolean doGetUrl() {
        TextInputDialog dialog = new TextInputDialog("Enter URL", 
                                                     "Enter URL:", 
                                                     m_lastPage,
                                                     m_recentSiteList.toArray(new String[0]));
        int result = dialog.open();
        if (result == Window.OK){
            if(dialog.getValue() != null) {
                return m_browser.setUrl(dialog.getValue());
            }
        }
        return false;
    }

    public boolean doSave() {
        try {
            String text    = m_browser.getText();
            URL    url     = new URL(m_browser.getUrl());
            File   urlFile = new File(url.getFile());
            logger.info("File = "+url.getFile());
            logger.info("File = "+urlFile.getName());
            FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE | SWT.SINGLE);
            dialog.setText("Save Current Page as");
            dialog.setFilterPath(LastPath.get(this));
            dialog.setFileName(urlFile.getName());
            if (dialog.open() != null) {
                final String filePath = dialog.getFilterPath();
                LastPath.set(this, filePath);
                String filename = dialog.getFileName();
                File file = new File(filePath+"/"+filename);
                FileUtils.write(file, text);
            }
        }
        catch(Throwable t) {
            logger.error("Error saving file", t);
        }
        return false;
    }

    /**
     * 
     */
    Listener newButtonListener(Buttons button) {
        Listener listener = null;
        switch(button) {
        case Go:      listener = new Listener() { public void handleEvent(Event event) { doGo();      } }; break;
        case Back:    listener = new Listener() { public void handleEvent(Event event) { doBack();    } }; break;
        case Forward: listener = new Listener() { public void handleEvent(Event event) { doForward(); } }; break;
        case Reload:  listener = new Listener() { public void handleEvent(Event event) { doReload();  } }; break;
        case Stop:    listener = new Listener() { public void handleEvent(Event event) { doStop();    } }; break;
        case Home:    listener = new Listener() { public void handleEvent(Event event) { doHome();    } }; break;
        case Save:    listener = new Listener() { public void handleEvent(Event event) { doSave();    } }; break;
        }
        return listener;
    }

    ToolItem createButton(Buttons b, ToolBar toolbar) {
        ToolItem retVal  = new ToolItem(toolbar, SWT.FLAT);
        retVal.setToolTipText(b.text);
        retVal.setImage(b.image);
        retVal.addListener(SWT.Selection, newButtonListener(b));
        return retVal;
    }
    void createActions() {
        Buttons.Back.action    = new Action() { @Override
            public void run() { doBack();    } };
            Buttons.Forward.action = new Action() { @Override
                public void run() { doForward(); } };
                Buttons.Reload.action  = new Action() { @Override
                    public void run() { doReload();  } };
                    Buttons.Stop.action    = new Action() { @Override
                        public void run() { doStop();    } };
                        Buttons.Home.action    = new Action() { @Override
                            public void run() { doHome();    } };
                            Buttons.Save.action    = new Action() { @Override
                                public void run() { doSave();    } };
                                Buttons.Go.action      = new Action("", Action.AS_DROP_DOWN_MENU) { 
                                    @Override
                                    public void run() { doGetUrl();  } 
                                };
                                for(Buttons b : Buttons.values()) {
                                    if(b.action != null) {
                                        b.action.setImageDescriptor(b.imageDescriptor);
                                        b.action.setText(b.text);
                                    }
                                }
                                m_goMenuCreator = new GoMenuCreator(Buttons.Go.action, getSite().getShell());
                                Buttons.Go.action.setMenuCreator(m_goMenuCreator);

                                Buttons.Home.action.setToolTipText("Home\n"+m_homePage);
    }

    /**
     * Create the SimpleBrowser part
     */
    @Override
    //--------------------------------------------------------------------
    public void createPartControl(Composite parent) {	    
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

        topComposite = new Composite(parent, SWT.NONE);
        topComposite.setLayoutData(gridData);
        topComposite.setCursor(m_waitCursor);

        topLayout = new GridLayout();
        topLayout.horizontalSpacing = 1;
        topLayout.verticalSpacing   = 1;
        topLayout.marginHeight      = 1;
        topLayout.numColumns        = 1;
        topComposite.setLayout(topLayout);

        if(false) {
            gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
            locComposite = new Composite(topComposite, SWT.NONE);
            locLayout = new GridLayout();
            locLayout.horizontalSpacing = 1;
            locLayout.verticalSpacing   = 1;
            topLayout.marginHeight      = 1;
            locLayout.numColumns        = 3;
            locComposite.setLayout(locLayout);
            locComposite.setLayoutData(gridData);

            m_toolbar1 = new ToolBar(locComposite, SWT.NONE);
            for( Buttons b : new Buttons[] { Buttons.Back, Buttons.Forward, Buttons.Reload, Buttons.Stop, Buttons.Home} ) {
                m_buttons[b.index] = createButton(b, m_toolbar1);
            }

            gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
            m_locationEdit = new Text(locComposite, SWT.BORDER);
            m_locationEdit.setLayoutData(gridData);
            m_locationEdit.addListener(SWT.DefaultSelection, newButtonListener(Buttons.Go));

            m_toolbar2 = new ToolBar(locComposite, SWT.NONE);
            for( Buttons b : new Buttons[] { Buttons.Go } ) {
                m_buttons[b.index] = createButton(b, m_toolbar2);
            }
        }

        //== create action buttons
        createActions();
        IActionBars bars = getViewSite().getActionBars();
        for(Buttons b : Buttons.values()) {
            if(b.action != null) {
                bars.getMenuManager().add(b.action);
                bars.getToolBarManager().add(b.action);
            }
        }

        //-- create browser and initialize
        try {
            //System.err.println("about to create browser");
            gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            m_browser = new Browser(topComposite, 0);
            m_browser.setLayoutData(gridData);
            m_browser.setUrl(m_homePage);
            addLocationListener();
            addExtendedLocationListeners();
            addTitleListener();
            addOpenWindowListener();

            if(m_doLoadLastUrl) {
                IPreferenceStore store = UtilUIActivator.getDefault().getPreferenceStore();
                String lastUrl = store.getString(lastUrlPreferenceKey());
                if(lastUrl != null && lastUrl.length() > 12) {
                    m_browser.setUrl(lastUrl);
                }
            }
        }
        catch(Throwable th) {
            IrgUI.errorDialog("Web Browser Creation Error", "Failed To Create Web Browser", th);
            return;
        }		

        topComposite.pack();

        //-- context menu
        hookContextMenu();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.add(Buttons.Back.action);
        menuMgr.add(Buttons.Forward.action);
        menuMgr.add(Buttons.Reload.action);
        menuMgr.add(Buttons.Stop.action);
        menuMgr.add(new Separator());
        menuMgr.add(Buttons.Save.action);
        menuMgr.add(new Separator());
        menuMgr.add(Buttons.Go.action);
        Menu menu = menuMgr.createContextMenu(m_browser);
        m_browser.setMenu(menu);
    }


    /**
     * update location text widget on updates
     */
    protected void addLocationListener() {
        LocationListener ll = new LocationListener() {
            public void changed(LocationEvent event) {
                m_lastPage = event.location;
                if(m_locationEdit != null) {
                    m_locationEdit.setText(m_lastPage);
                }
                Buttons.Go.action.setToolTipText(m_lastPage);
                IActionBars bars = getViewSite().getActionBars();
                bars.getStatusLineManager().setMessage(m_lastPage);
                addRecentSite(m_lastPage);
            }
            public void changing(LocationEvent event) {
                // do nothing
            }
        };
        m_browser.addLocationListener(ll);
    }


    /**
     * Initialize extension point map 
     */
    @SuppressWarnings("unchecked")
    protected synchronized void initializeExtensions(){
        if (s_listenerExtensions == null){
            Set<LocationListener> listenerExtensions = new HashSet< LocationListener>();
            IExtensionRegistry reg = Platform.getExtensionRegistry();

            IConfigurationElement[] genericExtensions = reg.getConfigurationElementsFor("gov.nasa.arc.irg.util.ui.browser.LocationListener");
            for (IConfigurationElement element : genericExtensions){
                String listenerClassName = element.getAttribute("locationListener");
                Bundle bun = Platform.getBundle(element.getContributor().getName());
                try {
                    Class customizationClass = bun.loadClass(listenerClassName);
                    Constructor constructor = customizationClass.getConstructor((Class[])null);
                    LocationListener result = (LocationListener)constructor.newInstance((Object[])null);
                    listenerExtensions.add(result);
                } catch (Exception e) { 
                    // ignored
                }
            }
            s_listenerExtensions = listenerExtensions;
        }
    }

    protected void addExtendedLocationListeners(){
        initializeExtensions();
        for (LocationListener l : s_listenerExtensions){
            m_browser.addLocationListener(l);
        }
    }

    /**
     * update location text widget on updates
     */
    void addTitleListener() {
        TitleListener tl = new TitleListener() {
            public void changed(TitleEvent event) {
                String title = event.title;
                int max = 20;
                if(title.length() > max) {
                    title = title.substring(0,max);
                    title += "...";
                }
                setPartName(title);
                setTitleToolTip("Web Browser"+"\n"+event.title);
            }
        };
        m_browser.addTitleListener(tl);
    }

    /**
     * unused
     */
    void addOpenWindowListener() {
        OpenWindowListener tl = new OpenWindowListener() {
            public void open(WindowEvent event) {
                //IrgUI.warnDialog("OpenWindow", "SimpleBrowser OpenWindowListener activated.");
            }
        };
        m_browser.addOpenWindowListener(tl);
    }

    @Override
    public void setFocus() {
        if(m_browser != null)
            m_browser.setFocus();
    }

    String[] rejectSites = new String[] {
                                         ".yieldmanager.com",
                                         ".rockyou.com",
                                         ".sochr.com",
                                         ".atdmt.com",
                                         ".adshuffle.com",
                                         ".casalemedia.com",
                                         "information.com",
                                         "adstream",
                                         "ads.cnn.com",
                                         "doubleclick.net",
    };

    boolean acceptRecentSite(String url) {
        boolean accept = true;

        if(url.length() > 200) 
            accept = false;

        for(String rejectSite : rejectSites) {
            if(url.contains(rejectSite)) {
                accept = false;
                break;
            }
        }
        return accept;
    }

    public String lastUrlPreferenceKey() {
        String retVal = this.getClass().getSimpleName()+"..lastUrl";
        try {
            String thisId = this.getViewSite().getSecondaryId();
            retVal = this.getClass().getSimpleName()+"."+thisId+".lastUrl";
        }
        catch(Throwable t) {
            // do nothing
        }
        return retVal;
    }

    private void addRecentSite(String url) {
        // XXX some of the ad urls are crazy long, so only accept limited length urls for save list
        if(acceptRecentSite(url)) {
            // store the last site for this view instance
            IPreferenceStore store = UtilUIActivator.getDefault().getPreferenceStore();
            store.setValue(lastUrlPreferenceKey(), url);

            Iterator<String> it = m_recentSiteList.iterator();
            while(it.hasNext()) {
                if(it.next().equals(url)) {
                    it.remove();
                }
            }
            m_recentSiteList.add(0, url);

            final int max = 20;
            while(m_recentSiteList.size() > max) {
                m_recentSiteList.remove(max);
            }
            saveRecentSites();
        }
    }

    private void saveRecentSites() {
        IPreferenceStore store = UtilUIActivator.getDefault().getPreferenceStore();
        String recentSites = "";
        for(String site : m_recentSiteList ) {
            recentSites += site + "\n";
        }
        store.setValue(SimpleBrowserPreferenceKeys.P_BROWSER_RECENT_SITES, recentSites);
    }

    private void loadRecentSites() {
        IPreferenceStore store = UtilUIActivator.getDefault().getPreferenceStore();
        String recentSites = store.getString(SimpleBrowserPreferenceKeys.P_BROWSER_RECENT_SITES);
        String[] sites = recentSites.split("\n");
        for(String site : sites) {
            if(acceptRecentSite(site)) {
                m_recentSiteList.add(site);
            }
        }
        saveRecentSites();
    }

    //===============================================================
    public class GoMenuCreator implements IMenuCreator {
        final Action m_action;
        final Menu   m_menu;
        GoMenuCreator(Action action, Shell shell) {
            m_action = action;
            m_menu = new Menu(shell, SWT.POP_UP);
        }
        public void dispose() {
            MenuItem[] menuItems = m_menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
        }
        public Menu getMenu() { return m_menu; }

        public Menu getMenu(Control parent) {
            fillMenu();
            return m_menu;
        }
        public Menu getMenu(Menu parent) {
            fillMenu();
            return m_menu;
        }
        public void fillMenu() {
            MenuItem[] menuItems = m_menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
            MenuItem item = new MenuItem(m_menu, SWT.NONE);
            item.setText("Enter URL...");
            item.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    logger.debug("Enter URL...");
                    doGetUrl();
                }
            });
            item = new MenuItem(m_menu, SWT.SEPARATOR);

            for(final String site : m_recentSiteList) {
                item = new MenuItem(m_menu, SWT.NONE);
                item.setText(site);
                item.addListener(SWT.Selection, new Listener() {
                    public void handleEvent(Event event) {
                        m_browser.setUrl(site);
                    }
                } );
            }
        }
    }

    boolean m_isDisposed = false;
    @Override
    public void dispose() {
        m_isDisposed = true;
        super.dispose();
    }
    public boolean isDisposed() {
        return m_isDisposed;
    }


}
