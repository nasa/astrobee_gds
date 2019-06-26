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
package gov.nasa.arc.irg.iss.ui.application;

import gov.nasa.arc.irg.iss.ui.IssButtonEventLoggingConfigurator;
import gov.nasa.arc.irg.iss.ui.IssUiActivator;
import gov.nasa.arc.irg.iss.ui.control.SimpleStatusLineManager;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.irg.util.ui.IrgUI;
import gov.nasa.arc.irg.util.ui.PlatformParameterUtil;
import gov.nasa.arc.irg.util.ui.status.StatusLineUtil;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.handlers.IHandlerService;

public class IssApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	private static final Logger buttonLogger = Logger.getLogger(IssButtonEventLoggingConfigurator.BUTTON_LOGGER_NAME);
	private static final Logger logger = Logger.getLogger(IssApplicationWorkbenchWindowAdvisor.class);

	public static final String HIDDEN = "Hidden";
	protected IActionBarConfigurer m_configurer;
	protected IssApplicationActionBarAdvisor m_applicationActionBarAdvisor;

	protected static String s_exitMessage = "Are you sure you want to exit the ";

	protected Control m_topToolbar = null;
	protected Control m_perspectiveBar = null;
	protected CTabFolder m_cTabFolder = null;
	protected Control m_page = null;
	protected Control m_statusline = null;
	protected SimpleStatusLineManager m_statusLineManager = null;
	protected Menu m_menubar = null;
	protected Control m_rightToolbar = null;

	protected IWorkbenchWindow m_workbenchWindow = null;
	protected IPerspectiveRegistry m_perspectiveRegistry = null;
	
	public IssApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		s_exitMessage += getTitle() + " ?";
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {

		m_configurer = configurer;
		m_applicationActionBarAdvisor = new IssApplicationActionBarAdvisor(configurer);
		return m_applicationActionBarAdvisor;
	}

	@Override
	public void createWindowContents(Shell shell) {
		IWorkbenchWindowConfigurer configurer =  getWindowConfigurer();
		m_workbenchWindow = configurer.getWindow();
		Menu menu = configurer.createMenuBar();
		shell.setMenuBar(menu);
		shell.setLayout(new FormLayout());
		m_topToolbar = configurer.createCoolBarControl(shell);
		m_perspectiveBar = createPerspectiveBarControl(shell);

		m_page = configurer.createPageComposite(m_cTabFolder);

		m_perspectiveRegistry = m_workbenchWindow.getWorkbench().getPerspectiveRegistry();
		createPerspectiveBarTabs();
		
		m_rightToolbar = createRightToolbar(shell);

		m_statusline = new SimpleStatusLineManager().createControl(shell);

		// The layout method does the work of connecting the
		// controls together.
		layoutNormal();
		configurer.getWorkbenchConfigurer().getWorkbench().getThemeManager().setCurrentTheme("gov.nasa.arc.irg.iss.ui.theme");
	}
	

	protected Control createRightToolbar(Composite parent){

		Composite toolbar = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 8;
		layout.marginWidth = 5;
		layout.marginHeight = 0;
		toolbar.setLayout(layout);

		createTopButtons(toolbar);
		
		Composite bottomToolbar = new Composite(toolbar, SWT.NONE);
		bottomToolbar.setLayout(layout);
		
		GridData bottom1 = new GridData(SWT.FILL, SWT.BOTTOM, true, true, 1, 1);
		bottom1.verticalAlignment = SWT.BOTTOM;
		bottomToolbar.setLayoutData(bottom1);

		createButton(bottomToolbar, "&Log", "log_24", "gov.nasa.arc.irg.iss.ui.commandLog");
		createButton(bottomToolbar, "Help", "help_24", "org.eclipse.ui.help.helpContents");
		createButton(bottomToolbar, "E&xit", "exit_24", "gov.nasa.arc.irg.iss.ui.exit");

		return toolbar;
	}
	
	/**
	 * Implement this to create buttons near the top
	 * @param parent
	 */
	protected void createTopButtons(Composite parent){
	}

	protected Control createButtonWithoutIcon(Composite parent, final String text, final String commandID){
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setOrientation(SWT.HORIZONTAL);
		button.setSize(90, 30);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String loggedEventText;
				if (text.equals("E&xit"))
					loggedEventText = text + " entire application button";
				else 
					loggedEventText = text;
					
				buttonLogger.log(Level.INFO, IssApplicationWorkbenchWindowAdvisor.class.getName() + "| " + loggedEventText + " pressed.");
				callCommand(commandID);
			}
		});
		
		GridData buttonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
		buttonGD.widthHint = 90;
		buttonGD.heightHint = 40;

		button.setLayoutData(buttonGD);
		return button;
	}
	
	protected Control createButtonWithoutIconOrListener(Composite parent, final String text, final String commandID){
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setOrientation(SWT.HORIZONTAL);
		button.setSize(90, 30);
		
		GridData buttonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
		buttonGD.widthHint = 90;
		buttonGD.heightHint = 40;

		button.setLayoutData(buttonGD);
		return button;
		
	}
	
	protected Control createButton(Composite parent, final String text, String icon, final String commandID){
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setOrientation(SWT.HORIZONTAL);
		button.setSize(90, 30);
		button.setImage(IssUiActivator.getImageFromRegistry(icon));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String loggedEventText;
				if (text.equals("E&xit"))
					loggedEventText = text + " entire application button";
				else 
					loggedEventText = text;
					
				buttonLogger.log(Level.INFO, IssApplicationWorkbenchWindowAdvisor.class.getName() + "| " + loggedEventText + " pressed.");
				callCommand(commandID);
			}
		});
		
		GridData buttonGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
		buttonGD.widthHint = 90;
		buttonGD.heightHint = 40;

		button.setLayoutData(buttonGD);
		return button;
	}

	protected void callCommand(String commandID){
		IHandlerService handlerService = (IHandlerService) m_workbenchWindow.getService(IHandlerService.class);
		try {
			handlerService.executeCommand(commandID, null);
		} catch (Exception ex) {
			throw new RuntimeException(commandID + " not found");
			// Give message
		}
	}

	protected Control createPerspectiveBarControl(Composite parent){
		m_cTabFolder = new CTabFolder(parent, SWT.TOP) {
			@Override
			public int getBorderWidth() {
				return 10;
			}
		};
		setTabFolderFont(m_cTabFolder);
		m_cTabFolder.setMinimumCharacters(20);
		m_cTabFolder.setTabHeight(40);
		m_cTabFolder.setSimple(false);
		m_cTabFolder.setBorderVisible(true);
		m_cTabFolder.setBackground(ColorProvider.INSTANCE.WIDGET_BACKGROUND);
		
		return m_cTabFolder;
	}
	
	protected void createPerspectiveBarTabs(){

		String foundHiddenPerspective = "";
		for (String peID : getPerspectiveExtensionIds()){
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.ui", "perspectives", peID);
			for (IConfigurationElement e : config) {
				String id = e.getAttribute("id");
				String name = e.getAttribute("name");
				if (!name.contains(HIDDEN)){
					CTabItem item = createTabItem(m_cTabFolder, name, m_page, id);
				} else {
					foundHiddenPerspective = id;
				}
			}
		}

		final CTabFolder tabFolder = m_cTabFolder;

		m_cTabFolder.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				CTabItem tabItem = tabFolder.getSelection();
				String perspectiveID = (String)tabItem.getData();
				selectPerspective(perspectiveID, e);
				tabItem.getControl().setFocus();
				buttonLogger.log(Level.INFO, IssApplicationWorkbenchWindowAdvisor.class.getName() + "| " + tabItem.getText() + "tab pressed.");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});
		
		m_workbenchWindow.addPerspectiveListener(new PerspectiveAdapter() {
			/* (non-Javadoc)
			 * @see org.eclipse.ui.PerspectiveAdapter#perspectiveActivated(org.eclipse.ui.IWorkbenchPage, org.eclipse.ui.IPerspectiveDescriptor)
			 */
			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspectiveDescriptor) {
				CTabItem foundTab = getTabForPerspective(perspectiveDescriptor.getId());
				if (foundTab != null){
					m_cTabFolder.setSelection(foundTab);
				}
			}
		});
		
		m_cTabFolder.setSelection(0);
		
		populateTopRightButtons(m_cTabFolder);
		
		m_cTabFolder.pack();
		
        final String hiddenId  = foundHiddenPerspective;
//        "gov.nasa.arc.irg.surface.telerobotics.workbench.perspective.HiddenPerspective";
//        final String runTaskId = "gov.nasa.arc.irg.surface.telerobotics.workbench.perspective.RunPlanPerspective";
        if (!foundHiddenPerspective.isEmpty()) {
	        Runnable showHiddenPerspective = new Runnable() {
	            @Override
	            public void run() {
	                try {
	                	IPerspectiveDescriptor pd = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective();
	                	final String lastPerspectiveID = pd.getId();
	                    PlatformUI.getWorkbench().showPerspective(hiddenId, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
	                    Runnable showRunTaskSequencePerspective = new Runnable() {
	                        @Override
	                        public void run() {
	                            try {
	                                PlatformUI.getWorkbench().showPerspective(lastPerspectiveID, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
	                            }
	                            catch(WorkbenchException e) {
	                                e.printStackTrace();
	                            }
	                        }
	                    };
	                    Display.getCurrent().asyncExec(showRunTaskSequencePerspective);
	                } 
	                catch (WorkbenchException e) {
	                    e.printStackTrace();
	                }
	            }
	        };
	        Display.getCurrent().asyncExec(showHiddenPerspective);
        }
	}
	
	protected String[] getPerspectiveExtensionIds() {
		// since we always know that we want the perspectives from the comms workbench before the other ones, 
		// I have given an identifier to the parent 
		return new String[] {"gov.nasa.arc.irg.iss.ui"};
	}
	
	protected void populateTopRightButtons(CTabFolder folder){
//		m_stopSphereButton = new StopSphereButton(m_cTabFolder, SWT.CENTER);
//		m_cTabFolder.setTopRight(m_stopSphereButton);
	}
	
	
	protected CTabItem getTabForPerspective(String perspectiveID){
		for (CTabItem ti : m_cTabFolder.getItems()){
			if (ti.getData().equals(perspectiveID)){
				return ti;
			}
		}
		return null;
	}

	protected void selectPerspective(String perspectiveID, SelectionEvent e){
		IWorkbenchPage page = m_workbenchWindow.getActivePage();
		if(page != null) {
			IPerspectiveDescriptor descriptor = m_perspectiveRegistry.findPerspectiveWithId(perspectiveID);
			page.setPerspective(descriptor);
			page.getActivePart().setFocus();
		}
	}

	private void setTabFolderFont(CTabFolder tabFolder) {
		FontData[] fontData = tabFolder.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++) {
			FontData fontDatum = fontData[i];
			fontDatum.setHeight(12); //ISS requirement
			fontDatum.setStyle(SWT.BOLD);
		}
		FontRegistry fontRegistry = JFaceResources.getFontRegistry();
		fontRegistry.put("tab.font", fontData);
		tabFolder.setFont(fontRegistry.get("tab.font"));
	}

	protected CTabItem createTabItem(CTabFolder tabFolder, String title, Control control, final String id) {
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NONE);
		tabItem.setText("  " + title + "  ");
		tabItem.setData(id);
		tabItem.setControl(control);
		return tabItem;
	}

	protected void layoutNormal() {
		// Toolbar
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		m_topToolbar.setLayoutData(data);
		
		// Status line
		data = new FormData();
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		m_statusline.setLayoutData(data);

		// right toolbar
		data = new FormData();
		data.top = new FormAttachment(m_topToolbar);
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(m_statusline);
		data.width = 100;
		m_rightToolbar.setLayoutData(data);

		// Perspectivebar
		data = new FormData();
		data.top = new FormAttachment(m_topToolbar);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(m_rightToolbar);
		data.bottom = new FormAttachment(m_statusline);
		this.m_cTabFolder.setLayoutData(data);

		getWindowConfigurer().getWindow().getShell().layout(true);
	}

	@Override
	public void preWindowOpen() {

		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setTitle(getTitle());
		configurer.setShowMenuBar(true);
		if (!PlatformParameterUtil.isDebug()){
			configurer.setShellStyle(SWT.DIALOG_TRIM);
			// SIZE is finely tuned.
			configurer.setInitialSize(new Point(1440, 900)); // 1920 x 1200 for t61p, ground is 16 x 9
		}
		configurer.setShowPerspectiveBar(true);

	}


	@Override
	public void postWindowOpen() {
		super.postWindowOpen();
		if (m_configurer != null){
			ICoolBarManager manager = m_configurer.getCoolBarManager();
			if (manager != null){
				manager.setLockLayout(!PlatformParameterUtil.isDebug());
			}
		}

		StatusLineUtil.setStatus("Workbench started; waiting for connection.");
	}

	public static String getExitMessage() {
		return s_exitMessage;
	}

	@Override
	public boolean preWindowShellClose() {

		boolean result = IrgUI.confirmDialogNoImage("Confirm Exit", getExitMessage());
		return result;
	}


	protected String getTitle() {
		return "Surface Telerobotics Workbench";
	}
	

}
