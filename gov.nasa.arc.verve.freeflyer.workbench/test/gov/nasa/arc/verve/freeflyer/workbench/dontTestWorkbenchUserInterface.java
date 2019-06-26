package gov.nasa.arc.verve.freeflyer.workbench;
//package gov.nasa.arc.verve.freeflyer.workbench;
//
//import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
//import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
//import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withId;
//import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withText;
//import static org.junit.Assert.*;
//import gov.nasa.arc.ff.ocu.commands.CommandStack;
//import gov.nasa.arc.irg.iss.ui.widget.RoundedButton;
//import gov.nasa.arc.irg.plan.model.PlanBuilder;
//import gov.nasa.arc.irg.plan.model.Sequenceable;
//import gov.nasa.arc.irg.plan.model.Station;
//import gov.nasa.arc.verve.freeflyer.workbench.handlers.NewHandler;
//import gov.nasa.arc.verve.freeflyer.workbench.parts.planeditor.PlanEditorPart;
//import gov.nasa.arc.verve.freeflyer.workbench.parts.planeditor.PlanFileManager;
//import gov.nasa.arc.irg.plan.freeflyer.plancompiler.PlanCompiler;
//import gov.nasa.arc.verve.freeflyer.workbench.undo.DelegateCommandStack;
//import gov.nasa.util.ui.MessageBox;
//import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
//import gov.nasa.arc.irg.plan.model.PlanBuilder;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map.Entry;
//
//import javax.inject.Inject;
//
//import org.eclipse.core.runtime.IProgressMonitor;
//import org.eclipse.core.runtime.IStatus;
//import org.eclipse.core.runtime.Status;
//import org.eclipse.core.runtime.jobs.Job;
//import org.eclipse.core.runtime.preferences.IEclipsePreferences;
//import org.eclipse.e4.core.contexts.ContextInjectionFactory;
//import org.eclipse.e4.core.contexts.EclipseContextFactory;
//import org.eclipse.e4.core.contexts.IEclipseContext;
//import org.eclipse.e4.core.di.annotations.Execute;
//import org.eclipse.e4.core.services.events.IEventBroker;
//import org.eclipse.e4.ui.di.UIEventTopic;
//import org.eclipse.e4.ui.internal.workbench.E4XMIResourceFactory;
//import org.eclipse.e4.ui.model.application.MApplication;
//import org.eclipse.e4.ui.model.application.impl.ApplicationFactoryImpl;
//import org.eclipse.e4.ui.model.application.impl.ApplicationPackageImpl;
//import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
//import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
//import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
//import org.eclipse.e4.ui.model.application.ui.basic.MPart;
//import org.eclipse.e4.ui.services.EMenuService;
//import org.eclipse.e4.ui.services.IServiceConstants;
//import org.eclipse.e4.ui.workbench.IWorkbench;
//import org.eclipse.e4.ui.workbench.UIEvents;
//import org.eclipse.e4.ui.workbench.modeling.EPartService;
//import org.eclipse.e4.ui.workbench.modeling.IPartListener;
//import org.eclipse.emf.common.notify.Notifier;
//import org.eclipse.emf.common.util.TreeIterator;
//import org.eclipse.emf.common.util.URI;
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.emf.ecore.resource.Resource;
//import org.eclipse.emf.ecore.resource.ResourceSet;
//import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.MenuItem;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.Event;
//import org.eclipse.swtbot.e4.finder.finders.ContextMenuFinder;
//import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
//import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
//import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
//import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
//import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
//import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
//import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
//import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
//import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
//import org.eclipse.ui.PlatformUI;
//import org.eclipse.ui.internal.Workbench;
//import org.hamcrest.Matcher;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.osgi.framework.BundleContext;
//import org.osgi.framework.FrameworkUtil;
//import org.osgi.framework.ServiceReference;
//import org.osgi.service.event.EventHandler;
//import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
//import org.eclipse.e4.ui.internal.workbench.swt.MenuServiceCreationFunction;
//
//@RunWith(SWTBotJunit4ClassRunner.class)
//public class TestWorkbenchUserInterface {
//
//	private static SWTWorkbenchBot bot;
//	private static IEclipseContext context;
//	private static PlanFileManager manager;
//	//Relative URL caused an error with PlanBuilder.
//	private static final String testFilepath = new File("testdata" + File.separator + "test_plan.fplan").getAbsolutePath();
//	private static MApplication app;
//	
//	@BeforeClass
//	public static void setup() throws InterruptedException {		
//		SWTBotPreferences.TIMEOUT = 1000;
//		SWTBotPreferences.PLAYBACK_DELAY = 100;
//		
//		IWorkbench workbench = getService(IWorkbench.class, IWorkbench.class);
//	    app = workbench.getApplication();
//	    context = app.getContext();		
//	    
//		manager = context.get(PlanFileManager.class);
//		bot = new SWTWorkbenchBot(context);	
//	}
//	
//	private static <T> T getService(Class<T> pClass, Class pContextClass) {
//	    BundleContext context = FrameworkUtil.getBundle(pContextClass).getBundleContext();
//	    ServiceReference<T> reference = context.getServiceReference(pClass);
//	    if(reference == null){
//	        return null;
//	    }
//	    T service = context.getService(reference);
//	    return service;
//	}
//	
//	//appends station in all three different ways and checks that context menu options are correct
//	@Test
//	public void testAddStation() {
//		openNewPlan();
//		
//		SWTBotTreeItem root = bot.tree().getTreeItem("test_plan");
//		SWTBotMenu rootMenu = root.contextMenu("Append Station");
//		rootMenu.click();
//		
//		appendFromViewer();
//	
//		SWTBotTreeItem node0 = root.getNode("0 Station");		
//		root.getNode("1 Station").contextMenu("Insert Station").click();
//		
//		SWTBotTreeItem node1 = root.getNode("1 Station");
//		SWTBotTreeItem node2 = root.getNode("2 Station");
//
//		int[] flags = {0b00001, 0b11011, 0b11111, 0b10111};
//		SWTBotTreeItem[] menus = {root, node0, node1, node2};
//		validateMenus(flags, menus);
//		
//		HashMap<String, String[]> key = new HashMap<>();
//		key.put("0 Station", new String[] {});
//		key.put("0-1 Segment", new String[] {});
//		key.put("1 Station", new String[] {});
//		key.put("1-2 Segment", new String[] {});
//		key.put("2 Station", new String[] {});
//		validateTreeStructure(key, root);
//		
//		closePlan();
//	}
//	
//	@Test
//	public void testReorderStations() {
//		openNewPlan();
//		
//		SWTBotTreeItem root = bot.tree().getTreeItem("test_plan");
//		SWTBotMenu menu = root.contextMenu("Append Station");
//		menu.click();
//		menu.click();
//		menu.click();
//		
//		root.getNode("0 Station").contextMenu("Reorder Down").click();
//		root.getNode("2 Station").contextMenu("Reorder Up").click();
//		root.getNode("1 Station").contextMenu("Reorder Down").click();
//		root.getNode("1 Station").contextMenu("Reorder Up").click();
//		root.getNode("0 Station").contextMenu("Reorder Down").click();
//		
//		int[] flags = {0b11011, 0b11111, 0b10111};
//		SWTBotTreeItem[] menus = {root.getNode("0 Station"), root.getNode("1 Station"), root.getNode("2 Station")};
//		validateMenus(flags, menus);
//		
//		HashMap<String, String[]> key = new HashMap<>();
//		key.put("0 Station", new String[] {});
//		key.put("0-1 Segment", new String[] {});
//		key.put("1 Station", new String[] {});
//		key.put("1-2 Segment", new String[] {});
//		key.put("2 Station", new String[] {});
//		validateTreeStructure(key, root);
//		
//		closePlan();
//	}
//	
//	@Test
//	public void testDeleteStations() {
//		openNewPlan();
//		
//		SWTBotTreeItem root = bot.tree().getTreeItem("test_plan");
//		SWTBotMenu menu = root.contextMenu("Append Station");
//		menu.click();
//		menu.click();
//		menu.click();
//		
//		root.getNode("1 Station").contextMenu("Delete").click();
//		root.getNode("1 Station").contextMenu("Delete").click();
//		root.getNode("0 Station").contextMenu("Append Station").click();
//		root.getNode("0 Station").contextMenu("Delete").click();
//		root.getNode("0 Station").contextMenu("Append Station").click();
//		root.getNode("1 Station").contextMenu("Insert Station").click();
//		root.getNode("2 Station").contextMenu("Insert Station").click();
//		root.getNode("3 Station").contextMenu("Delete").click();
//		root.getNode("0 Station").contextMenu("Delete").click();
//		root.getNode("0 Station").contextMenu("Delete").click();
//		
//		int[] flags = {0b10011};
//		SWTBotTreeItem[] menus = {root.getNode("0 Station")};
//		validateMenus(flags, menus);
//		
//		HashMap<String, String[]> key = new HashMap<>();
//		key.put("0 Station", new String[] {});
//		validateTreeStructure(key, root);
//		
//		closePlan();		
//	}
//	
//	@Test
//	public void testAddSubcommands() {
//		openNewPlan();
//		
//		SWTBotTreeItem root = bot.tree().getTreeItem("test_plan");
//		SWTBotMenu menu = root.contextMenu("Append Station");
//		menu.click();
//		menu.click();
//		menu.click();
//		
//		SWTBotTreeItem node0 = root.getNode("0 Station");
//		SWTBotTreeItem node1 = root.getNode("1 Station");
//		SWTBotTreeItem node2 = root.getNode("2 Station");
//		
//		addSubcommand(node0, "Station Keep");
//		addSubcommand(node1, "Arm Pan And Tilt");
//		addSubcommand(node2, "Configure Safeguards");
//		addSubcommand(node1, "Pause Plan");
//		addSubcommand(node0, "Dock");
//		addSubcommand(node0, "Undock");
//		addSubcommand(node1, "Perch");
//		addSubcommand(node0, "Unperch");
//		
//		int[] flags = {0b11001, 0b11101, 0b10101, 0b11101, 0b10001};
//		SWTBotTreeItem[] menus = {node0.getNode("0.0 Wait"), node0.getNode("0.1 Dock"), node0.getNode("0.3 Unperch"), 
//				node1.getNode("1.1 PausePlan"), node2.getNode("2.0 ConfigureOperatingLimits")};
//		validateMenus(flags, menus);
//		
//		HashMap<String, String[]> key = new HashMap<>();
//		key.put("0 Station", new String[] {"0.0 Wait", "0.1 Dock", "0.2 Undock", "0.3 Unperch"});
//		key.put("0-1 Segment", new String[] {});
//		key.put("1 Station", new String[] {"1.0 ArmPanAndTilt", "1.1 PausePlan", "1.2 Perch"});
//		key.put("1-2 Segment", new String[] {});
//		key.put("2 Station", new String[] {"2.0 ConfigureOperatingLimits"});
//		validateTreeStructure(key, root);
//		
//		closePlan();
//	}
//	
//	@Test
//	public void testReorderSubcommands() {
//		openNewPlan();
//		
//		SWTBotTreeItem root = bot.tree().getTreeItem("test_plan");
//		SWTBotMenu menu = root.contextMenu("Append Station");
//		menu.click();
//		menu.click();
//		
//		addSubcommand(root.getNode("0 Station"), "Station Keep");
//		addSubcommand(root.getNode("1 Station"), "Arm Pan And Tilt");
//		addSubcommand(root.getNode("1 Station"), "Configure Safeguards");
//		addSubcommand(root.getNode("0 Station"), "Pause Plan");
//		addSubcommand(root.getNode("1 Station"), "Dock");
//		
//		root.getNode("0 Station").getNode("0.0 Wait").contextMenu("Reorder Down").click();
//		root.getNode("0 Station").getNode("0.0 PausePlan").contextMenu("Reorder Down").click();
//		root.getNode("1 Station").getNode("1.1 ConfigureOperatingLimits").contextMenu("Reorder Down").click();
//		root.getNode("0 Station").contextMenu("Reorder Down").click();
//		root.getNode("1 Station").expand().getNode("1.0 Wait").contextMenu("Reorder Down").click();
//		
//		SWTBotTreeItem node0 = root.getNode("0 Station");
//		SWTBotTreeItem node1 = root.getNode("1 Station");
//		
//		int[] flags = {0b11001, 0b11011, 0b10111, 0b10101};
//		SWTBotTreeItem[] menus = {node0.getNode("0.0 ArmPanAndTilt"), node0, node1, node1.getNode("1.1 Wait")};
//		validateMenus(flags, menus);
//		
//		HashMap<String, String[]> key = new HashMap<>();
//		key.put("0 Station", new String[] {"0.0 ArmPanAndTilt", "0.1 Dock", "0.2 ConfigureOperatingLimits"});
//		key.put("0-1 Segment", new String[] {});
//		key.put("1 Station", new String[] {"1.0 PausePlan", "1.1 Wait"});
//		validateTreeStructure(key, root);
//		
//		closePlan();
//	}
//	
//	@Test
//	public void testDeleteSubcommands() {
//		openNewPlan();
//		
//		SWTBotTreeItem root = bot.tree().getTreeItem("test_plan");
//		SWTBotMenu menu = root.contextMenu("Append Station");
//		menu.click();
//		menu.click();
//		
//		addSubcommand(root.getNode("1 Station"), "Arm Pan And Tilt");
//		addSubcommand(root.getNode("1 Station"), "Dock");
//		root.getNode("1 Station").getNode("1.0 ArmPanAndTilt").contextMenu("Delete").click();
//		addSubcommand(root.getNode("0 Station"), "Unperch");
//		addSubcommand(root.getNode("1 Station"), "Undock");
//		root.getNode("0 Station").contextMenu("Delete").click();
//		root.getNode("0 Station").getNode("0.0 Dock").contextMenu("Delete").click();
//		menu.click();
//		menu.click();
//		addSubcommand(root.getNode("2 Station"), "Pause Plan");
//		addSubcommand(root.getNode("2 Station"), "Configure Safeguards");
//		root.getNode("1 Station").contextMenu("Delete").click();
//		root.getNode("1 Station").getNode("1.0 PausePlan").contextMenu("Delete").click();
//		
//		int[] flags = {0b11011, 0b10001, 0b10111, 0b10001};
//		SWTBotTreeItem[] menus = {root.getNode("0 Station"), root.getNode("0 Station").getNode("0.0 Undock"),
//				root.getNode("1 Station"), root.getNode("1 Station").getNode("1.0 ConfigureOperatingLimits")};
//		validateMenus(flags, menus);
//		
//		HashMap<String, String[]> key = new HashMap<>();
//		key.put("0 Station", new String[] {"0.0 Undock"});
//		key.put("0-1 Segment", new String[] {});
//		key.put("1 Station", new String[] {"1.0 ConfigureOperatingLimits"});
//		validateTreeStructure(key, root);
//		
//		closePlan();
//	}
//
//	@Test
//	public void testUndoAdd() {
//		openNewPlan();
//		
//		SWTBotTreeItem root = bot.tree().getTreeItem("test_plan");
//		SWTBotMenu menu = root.contextMenu("Append Station");
//		menu.click();
//		menu.click();
//		bot.menu("Edit").menu("Undo").click();
//		menu.click();
//		bot.menu("Edit").menu("Undo").click();
//		bot.menu("Edit").menu("Undo").click();
//		menu.click();
//		menu.click();
//		root.getNode("1 Station").contextMenu("Insert Station").click();
//		bot.menu("Edit").menu("Undo").click();
//		addSubcommand(root.getNode("0 Station"), "Arm Pan And Tilt");
//		addSubcommand(root.getNode("1 Station"), "Unperch");
//		bot.menu("Edit").menu("Undo").click();
//		appendFromViewer();
//		bot.menu("Edit").menu("Undo").click();
//		bot.menu("Edit").menu("Undo").click();
//		
//		int[] flags = {0b11011, 0b10111};
//		SWTBotTreeItem[] menus = {root.getNode("0 Station"), root.getNode("1 Station")};
//		validateMenus(flags, menus);
//		
//		HashMap<String, String[]> key = new HashMap<>();
//		key.put("0 Station", new String[] {});
//		key.put("0-1 Segment", new String[] {});
//		key.put("1 Station", new String[] {});
//		validateTreeStructure(key, root);
//		
//		closePlan();
//	}
//	
//	@Test
//	public void testUndoReorder() {
//		openNewPlan();
//		
//		SWTBotTreeItem root = bot.tree().getTreeItem("test_plan");
//		SWTBotMenu menu = root.contextMenu("Append Station");
//		menu.click();
//		menu.click();
//		menu.click();
//		
//		root.getNode("0 Station").contextMenu("Reorder Down").click();
//		addSubcommand(root.getNode("0 Station"), "Perch");
//		addSubcommand(root.getNode("0 Station"), "Unperch");
//		addSubcommand(root.getNode("0 Station"), "Dock");
//		root.getNode("0 Station").getNode("0.0 Perch").contextMenu("Reorder Down").click();
//		bot.menu("Edit").menu("Undo").click();
//		addSubcommand(root.getNode("1 Station"), "Pause Plan");
//		root.getNode("0 Station").contextMenu("Reorder Down").click();
//		root.getNode("2 Station").contextMenu("Reorder Up").click();
//		bot.menu("Edit").menu("Undo").click();
//		root.getNode("1 Station").contextMenu("Reorder Down").click();
//		bot.menu("Edit").menu("Undo").click();
//		bot.menu("Edit").menu("Undo").click();
//		root.getNode("0 Station").expand().getNode("0.2 Dock").contextMenu("Reorder Up").click();
//		root.getNode("0 Station").getNode("0.1 Dock").contextMenu("Reorder Up").click();
//		root.getNode("0 Station").contextMenu("Reorder Down").click();
//		bot.menu("Edit").menu("Undo").click();
//		bot.menu("Edit").menu("Undo").click();
//			
//		int[] flags = {0b11111, 0b11011, 0b11101, 0b10111};
//		SWTBotTreeItem[] menus = {root.getNode("1 Station"), root.getNode("0 Station"),
//				root.getNode("0 Station").getNode("0.1 Dock"), root.getNode("2 Station")};
//		validateMenus(flags, menus);
//		
//		HashMap<String, String[]> key = new HashMap<>();
//		key.put("0 Station", new String[] {"0.0 Perch", "0.1 Dock", "0.2 Unperch"});
//		key.put("0-1 Segment", new String[] {});
//		key.put("1 Station", new String[] {"1.0 PausePlan"});
//		key.put("1-2 Segment", new String[] {});
//		key.put("2 Station", new String[] {});
//		validateTreeStructure(key, root);
//		
//		closePlan();
//	}
//	
//	@Test
//	public void testUndoDelete() {
//		openNewPlan();
//		
//		SWTBotTreeItem root = bot.tree().getTreeItem("test_plan");
//		SWTBotMenu menu = root.contextMenu("Append Station");
//		menu.click();
//		menu.click();
//		
//		addSubcommand(root.getNode("1 Station"), "Arm Pan And Tilt");
//		addSubcommand(root.getNode("1 Station"), "Dock");
//		root.getNode("1 Station").getNode("1.0 ArmPanAndTilt").contextMenu("Delete").click();
//		bot.menu("Edit").menu("Undo").click();
//		addSubcommand(root.getNode("0 Station"), "Unperch");
//		addSubcommand(root.getNode("1 Station"), "Undock");
//		root.getNode("0 Station").contextMenu("Delete").click();
//		root.getNode("0 Station").getNode("0.1 Dock").contextMenu("Delete").click();
//		bot.menu("Edit").menu("Undo").click();
//		bot.menu("Edit").menu("Undo").click();
//		menu.click();
//		addSubcommand(root.getNode("2 Station"), "Pause Plan");
//		addSubcommand(root.getNode("2 Station"), "Configure Safeguards");
//		root.getNode("1 Station").contextMenu("Delete").click();
//		root.getNode("1 Station").getNode("1.0 PausePlan").contextMenu("Delete").click();
//		bot.menu("Edit").menu("Undo").click();
//		
//		int[] flags = {0b11011, 0b10001, 0b10111, 0b11001};
//		SWTBotTreeItem[] menus = {root.getNode("0 Station"), root.getNode("0 Station").expand().getNode("0.0 Unperch"),
//				root.getNode("1 Station"), root.getNode("1 Station").getNode("1.0 PausePlan")};
//		validateMenus(flags, menus);
//		
//		HashMap<String, String[]> key = new HashMap<>();
//		key.put("0 Station", new String[] {"0.0 Unperch"});
//		key.put("0-1 Segment", new String[] {});
//		key.put("1 Station", new String[] {"1.0 PausePlan", "1.1 ConfigureOperatingLimits"});
//		validateTreeStructure(key, root);
//		
//		closePlan();
//	}
//	
//	@Test
//	public void testPlanValidation() throws InterruptedException{
//		openNewPlan();
//						
//	    assertValid(true);
//		
//		SWTBotTreeItem root = bot.tree().getTreeItem("test_plan");
//		SWTBotMenu menu = root.contextMenu("Append Station");
//		menu.click();
//		assertValid(true);
//		
//		menu.click();
//		assertValid(false);
//		
//		root.getNode("0 Station").select();
//		bot.textWithLabel("X").selectAll().typeText("1.0");
//		bot.textWithLabel("Y").selectAll().typeText(".25");
//		bot.textWithLabel("Z").selectAll().typeText("-.5");
//		Thread.sleep(1000);
//		assertValid(true);
//		
//		//move into keep out zone
//		root.getNode("1 Station").select();
//		bot.textWithLabel("X").selectAll().typeText("1.3");
//		bot.textWithLabel("Y").selectAll().typeText(".82");
//		bot.textWithLabel("Z").selectAll().typeText("-.1");
//		Thread.sleep(1000);
//		assertValid(false);
//		
//		root.getNode("1 Station").select();
//		bot.textWithLabel("Z").selectAll().typeText("-.7");
//		Thread.sleep(1000);
//		assertValid(true);
//		
//		root.getNode("1 Station").select();
//		bot.textWithLabel("Z").selectAll().typeText("1.5");
//		Thread.sleep(1000);
//		assertValid(false);
//		
//		closePlan();
//	}
//	
//	private void assertValid(boolean assertion) {
//		PlanBuilder<FreeFlyerPlan> planBuilder = (PlanBuilder<FreeFlyerPlan>) context.get(ContextNames.PLAN_BUILDER_FOR_PLAN_EDITOR);
//		
//		Display.getDefault().syncExec(new Runnable() {
//			public void run() {
//				if(assertion) {
//					assertTrue(PlanCompiler.compilePlan(planBuilder.getPlan()));
//				} else {
//					assertFalse(PlanCompiler.compilePlan(planBuilder.getPlan()));
//				}
//			}
//		});
//	}
//	
//	private void openNewPlan() {
//		Display.getDefault().syncExec(new Runnable() {
//			public void run() {
//				manager.setupNewPlan(testFilepath);
//			}
//		});
//	}
//	
//	private void closePlan() {
//		context.set(Sequenceable.class, null);
//		context.set("repositionEnabled", false);
//		CommandStack.getInstance().flush();
//		manager.setPlanNotDirty();
//		manager.setPlanClosed();
//	}
//	
//	private void appendFromViewer() {
//		Composite viewer = bot.widget(allOf(widgetOfType(Composite.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "verveViewer")));
//		Display.getDefault().syncExec(new Runnable() {
//			public void run() {
//				ContextMenuFinder finder = new ContextMenuFinder(viewer.getChildren()[0]);		
//				Matcher<MenuItem> matcher = withText("Append Station");
//				new SWTBotMenu(finder.findMenus(matcher).get(0)).click();
//			}
//		});
//	}
//	
//	private void validateTreeStructure(HashMap<String, String[]> key, SWTBotTreeItem root) {
//		ArrayList<String> stations = (ArrayList<String>) root.getNodes();
//		
//		for(String station : key.keySet()) {
//			assertTrue("Missing station: " + station, stations.contains(station));
//			for(String subcommand : key.get(station)) {
//				ArrayList<String> subcommands = (ArrayList<String>) root.getNode(station).getNodes();
//				assertTrue("Missing subcommand: " + subcommand, subcommands.contains(subcommand));
//			}
//		}
//		
//		System.out.println("Plan structure validated.");
//	}
//	
//	private void clickValidate() {
//		Display.getDefault().syncExec(new Runnable () {
//			public void run() {				
//				RoundedButton button = bot.widget(allOf(widgetOfType(RoundedButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Validate")));
//				Event event = new Event();
//				event.widget = button;
//				event.display = button.getDisplay();
//				
//				button.notifyListeners(SWT.Selection, event);
//			}
//		});
//	}
//	
//	private void addSubcommand(SWTBotTreeItem node, String subcommand) {
//		//needs to be synchronous so test can be performed after adding
//		Display.getDefault().syncExec(new Runnable () {
//			public void run() {
//				node.select();
//				
//				RoundedButton button = bot.widget(allOf(widgetOfType(RoundedButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Add Command")));
//				Event event = new Event();
//				event.widget = button;
//				event.display = button.getDisplay();
//				
//				bot.comboBoxWithLabel("Add a subcommand").setSelection(subcommand);
//				button.notifyListeners(SWT.Selection, event);
//			}
//		});
//	}
//	
//	//Needed to use i in new thread without thread sync issues.
//	private volatile int i;
//	
//	//Uses a bit mask for conciseness, see array below for fields 2^n is [n].
//	private void validateMenus(int[] flags, SWTBotTreeItem[] menus) {
//		String[] options = {"appendStationEnabled", "insertStationEnabled", "moveStationUpEnabled",
//							"moveStationDownEnabled", "deleteStationEnabled"};
//		
//		for(i = 0; i < flags.length; i++) {
//			menus[i].select();
//			//Needs to be synchronous for tests to work, not much time difference with async anyway.
//			Display.getDefault().syncExec(new Runnable() {
//				public void run() {
//					for(int j = 0; j < options.length; j++) {
//						//Check to make sure only widgets that should be there are found.
//						if((flags[i] & (int) Math.pow(2, j)) != 0) {
//							assertTrue(options[j], (boolean) context.get(options[j]));
//						} else {
//							assertFalse(options[j], (boolean) context.get(options[j]));
//						}
//					}
//				}
//			});
//			System.out.println("Station/Subcommand " + menus[i].getText() + " passed.");
//		}
//		
//	}
//
//	@AfterClass
//	public static void tearDown() {
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//}