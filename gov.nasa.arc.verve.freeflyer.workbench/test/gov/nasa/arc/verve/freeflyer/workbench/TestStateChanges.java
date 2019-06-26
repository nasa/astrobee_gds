package gov.nasa.arc.verve.freeflyer.workbench;

//import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.allOf;
//import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;
//import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withId;
//import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withLabel;
//import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.withText;
import static org.junit.Assert.*;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.RunningPlanInfo;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;
import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.arc.simulator.freeflyer.FreeFlyerPreferences;
import gov.nasa.arc.simulator.freeflyer.compress.CompressPublisher;
import gov.nasa.arc.verve.freeflyer.workbench.parts.standard.BeeCommandingOnRunPlanTab;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.dds.rti.preferences.DdsPreferences;
import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.system.DomainParticipantFactoryConfig;
import gov.nasa.dds.rti.system.RtiDds;
import gov.nasa.dds.rti.util.TypeSupportUtil;
import gov.nasa.dds.system.Dds;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;
import gov.nasa.rapid.v2.e4.agent.IDiscoveredAgentListener;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import gov.nasa.rapid.v2.e4.util.RapidTypeSupportUtil;
import gov.nasa.rapid.v2.e4.util.RapidUtil;
import gov.nasa.rapid.v2.util.RapidExtArcTypeSupportUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import rapid.AckStatus;
import rapid.Command;
import rapid.Message;
import rapid.ext.astrobee.AgentState;
import rapid.ext.astrobee.CompressedFile;
import rapid.ext.astrobee.ExecutionState;
import rapid.ext.astrobee.FileCompressionType;
import rapid.ext.astrobee.MobilityState;
import rapid.ext.astrobee.OperatingState;
import rapid.ext.astrobee.PlanStatus;
import rapid.ext.astrobee.util.RapidExtAstroBeeTypeSupportUtil;

import com.rti.dds.target.RtiDdsTarget;
//import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
//import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
//import org.eclipse.swtbot.swt.finder.SWTBotAssert;
//import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
//import org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot;
//import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;

public class TestStateChanges {
	
	private static IEclipseContext context;
	private static MApplication app;
	private static Agent agent = Agent.FreeFlyerA;
	private static BeeCommandingOnRunPlanTab control;
	private static RunningPlanInfo rpi;
//	private static SWTWorkbenchBot bot;
	private static final String testFilepath = new File("testdata" + File.separator + "Zigzags.fplan").getAbsolutePath();
	private boolean flag = false;

//	@BeforeClass
//	public static void setup() throws Exception {
////		SWTBotPreferences.TIMEOUT = 1000;
////		SWTBotPreferences.PLAYBACK_DELAY = 2000;
//		System.out.println(testFilepath);
//		IWorkbench workbench = getService(IWorkbench.class, IWorkbench.class);
//	    app = workbench.getApplication();
//	    context = app.getContext();	
//		
//		ContextInjectionFactory.make(RapidV2UiPreferences.class, context);
//		ContextInjectionFactory.make(DdsInitializerBase.class, context);
//		rpi = ContextInjectionFactory.make(RunningPlanInfo.class, context);
//		context.set(RunningPlanInfo.class, rpi);
//		
//		//Trick the workbench into thinking it's connected.
//		ConnectionListenerRegistry listener = ConnectionListenerRegistry.INSTANCE;
//		ArrayList<Agent> agents = new ArrayList<>();
//		agents.add(agent);
//		listener.newAgentsDiscovered(FreeFlyer.PARTICIPANT_ID, agents);
//		
//		try {
//			createParticipant();
//			setupDds();
//		} catch (Exception e) {
//			System.out.println("Failed to create paticipant.");
//			e.printStackTrace();
//		}
//		
////		bot = new SWTWorkbenchBot(context);
//	}
	
	private static <T> T getService(Class<T> pClass, Class pContextClass) {
	    BundleContext context = FrameworkUtil.getBundle(pContextClass).getBundleContext();
	    ServiceReference<T> reference = context.getServiceReference(pClass);
	    if(reference == null){
	        return null;
	    }
	    T service = context.getService(reference);
	    return service;
	}
	
	
	
	@Test
	public void testAgentState() {
		AgentState agentState = new AgentState();
		agentState.proximity = 1;
//		agentState.profileName = fakeProfileName;
//		agentState.flightMode = fakeFlightMode;
		agentState.targetLinearVelocity = 2;
		agentState.targetLinearAccel = 3;
		agentState.targetAngularVelocity = 4;
		agentState.targetAngularAccel = 5;
		agentState.collisionDistance = 6;
		agentState.enableHolonomic = true;
		agentState.checkObstacles = true;
		agentState.checkKeepouts = true;
		agentState.enableAutoReturn = true;
		agentState.bootTime = 8;		
		agentState.operatingState = rapid.ext.astrobee.OperatingState.OPERATING_STATE_PLAN_EXECUTION;
		agentState.executionState = rapid.ext.astrobee.ExecutionState.EXECUTION_STATE_IDLE;
		agentState.mobilityState = rapid.ext.astrobee.MobilityState.MOBILITY_STATE_FLYING;
		
		agentState.guestScienceState = ExecutionState.EXECUTION_STATE_IDLE;
		agentState.mobilityState = MobilityState.MOBILITY_STATE_DOCKING;
		
		AstrobeeStateGds astroState = AggregateAstrobeeState.getInstance().getAstrobeeState();
		astroState.ingestAgentState(agentState);
		
		assertTrue("proximity wrong", astroState.getProximity() == 1);
		assertTrue("max velocity wrong", astroState.getTargetLinearVelocity() == 2);
		assertTrue("max acceleration wrong", astroState.getTargetLinearAccel() == 3);
		assertTrue("collisionDistance wrong", astroState.getCollisionDistance() == 4);
		assertTrue("targetSpeed wrong", astroState.getTargetAngularVelocity() == 5);
		assertTrue("targetVelocity wrong", astroState.getTargetAngularAccel() == 6);
		assertTrue("boot time wrong", astroState.getBootTime() == 7);
		
		assertTrue("operating state wrong", astroState.getOperatingState().name().equals("PLAN_EXECUTION"));
		assertTrue("executing state wrong", astroState.getPlanExecutionState().name().equals("EXECUTING"));
		assertTrue("guest science state wrong", astroState.getGuestState().name().equals("IDLE"));
		assertTrue("mobility state wrong", astroState.getMobilityStateName().equals("DOCKING"));
		
		agentState.targetAngularAccel = 3;
		agentState.executionState = ExecutionState.EXECUTION_STATE_PAUSED;
		agentState.guestScienceState = ExecutionState.EXECUTION_STATE_IDLE;
		astroState.ingestAgentState(agentState);
		
		assertTrue("proximity wrong", astroState.getProximity() == 1);
		assertTrue("max velocity wrong", astroState.getTargetLinearVelocity() == 2);
		assertTrue("max acceleration wrong", astroState.getTargetLinearAccel() == 3);
		assertTrue("collisionDistance wrong", astroState.getCollisionDistance() == 4);
		assertTrue("targetSpeed wrong", astroState.getTargetAngularVelocity() == 5);
		assertTrue("targetvelocity wrong", astroState.getTargetAngularAccel() == 3);
		assertTrue("boot time wrong", astroState.getBootTime() == 7);
		
		assertTrue("operating state wrong", astroState.getOperatingState().name().equals("PLAN_EXECUTION"));
		assertTrue("executing state wrong", astroState.getPlanExecutionState().name().equals("PAUSED"));
		assertTrue("guest science state wrong", astroState.getGuestState().name().equals("IDLE"));
		assertTrue("mobility state wrong", astroState.getMobilityStateName().equals("DOCKING"));
	}
	
	@Test
	public void testPlanStatus() {
		PlanStatus planStatus = new PlanStatus();
		planStatus.currentCommand = 1;
		planStatus.currentPoint = 2;
		planStatus.currentStatus = AckStatus.ACK_COMPLETED;
		planStatus.planName = "test_plan";
		
		
		AggregateAstrobeeState astroState = AggregateAstrobeeState.getInstance();
		astroState.ingestPlanStatus(planStatus);		
		assertTrue("plan name wrong", astroState.getCurrentPlanName().equals("test_plan"));
		
		planStatus.planName = "changed";
		astroState.ingestPlanStatus(planStatus);		
		assertTrue("plan name wrong", astroState.getCurrentPlanName().equals("changed"));
		
		astroState.ingestPlanStatus(planStatus);		
		assertTrue("plan name wrong", astroState.getCurrentPlanName().equals("changed"));
		
		planStatus.planName = "";
		astroState.ingestPlanStatus(planStatus);
		assertTrue("plan name wrong", astroState.getCurrentPlanName().equals(""));
	}
	
//	@Test
//	public void testAstrobeeStateChange() throws InterruptedException {
//		AgentState agentState = new AgentState();		
//		AggregateAstrobeeState astroState = new AggregateAstrobeeState();
//		
//		bot.cTabItem("Run Tab").activate();
//		bot.comboBoxWithId("gov.nasa.arc.irg.iss.ui.widget.key", "Partitions").setSelection(0);
//		
//		control = context.get(ControlPanelRun.class);
//		Display.getDefault().syncExec(new Runnable() {
//			public void run() {
//				control.validateChosenPlanAndPutInContext(testFilepath);
//			}
//		});
//		
//		agentState.executingState = ExecutionState.EXECUTION_STATE_IDLE;
//		astroState.ingestAgentState(agentState);
//		control.onAstrobeeStateChange(astroState);
//		assertFalse(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Run"))).isEnabled());
//		assertFalse(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Pause"))).isEnabled());
//		assertFalse(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Skip"))).isEnabled());
//		assertTrue(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Upload"))).isEnabled());
//		
//		agentState.executingState = ExecutionState.EXECUTION_STATE_EXECUTING;
//		astroState.ingestAgentState(agentState);
//		control.onAstrobeeStateChange(astroState);
//		assertFalse(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Run"))).isEnabled());
//		assertTrue(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Pause"))).isEnabled());
//		assertFalse(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Skip"))).isEnabled());
//		assertFalse(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Upload"))).isEnabled());
//		
//		agentState.executingState = ExecutionState.EXECUTION_STATE_PAUSED;
//		astroState.ingestAgentState(agentState);
//		control.onAstrobeeStateChange(astroState);
//		assertTrue(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Run"))).isEnabled());
//		assertFalse(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Pause"))).isEnabled());
//		assertTrue(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Skip"))).isEnabled());
//		assertTrue(bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", "Upload"))).isEnabled());
//	}
	
	TestCommandListener listener;
	
//	@Test
//	public void testCorrectCommandsSent() throws InterruptedException {
//		AgentState agentState = new AgentState();
//		AggregateAstrobeeState astroState = new AggregateAstrobeeState();
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//				listener = new TestCommandListener();
//			}
//		});
//		
//		bot.cTabItem("Run Tab").activate();
//		SWTBotCombo combo = bot.comboBoxWithId("gov.nasa.arc.irg.iss.ui.widget.key", "Partitions");
//		combo.setSelection(0);
//		CommandPublisher publisher = CommandPublisher.getInstance(Agent.valueOf(combo.getText()));
//		
//		control = context.get(ControlPanelRun.class);
//		Display.getDefault().syncExec(new Runnable() {
//			public void run() {
//				control.validateChosenPlanAndPutInContext(testFilepath);
//			}
//		});
//		
//		agentState.executingState = ExecutionState.EXECUTION_STATE_IDLE;
//		astroState.ingestAgentState(agentState);
//		control.onAstrobeeStateChange(astroState);
//		listener.setExpectedMessage(createCompressedFile()); 
//		//waitForConnection();
//		clickCommandButton("Upload");
//		
//		while(!flag) {
//			Thread.sleep(1000);
//		}
//		flipFlag();
//		
//		agentState.executingState = ExecutionState.EXECUTION_STATE_EXECUTING;
//		astroState.ingestAgentState(agentState);
//		control.onAstrobeeStateChange(astroState);
//		listener.setExpectedMessage(publisher.buildCommand(
//				ASTROBEE_METHOD_PAUSE_PLAN.VALUE,
//				FreeFlyerCommands.SUBSYSTEM_NAME)); 
//		clickCommandButton("Pause");
//		
//		while(!flag) {
//			Thread.sleep(1000);
//		}
//		flipFlag();
//		
//		agentState.executingState = ExecutionState.EXECUTION_STATE_PAUSED;
//		astroState.ingestAgentState(agentState);
//		control.onAstrobeeStateChange(astroState);
//		listener.setExpectedMessage(publisher.buildCommand(
//				ASTROBEE_METHOD_RUN_PLAN.VALUE,
//				FreeFlyerCommands.SUBSYSTEM_NAME)); 
//		clickCommandButton("Run");
//		
//		while(!flag) {
//			Thread.sleep(1000);
//		}
//		flipFlag();
//		
//		agentState.executingState = ExecutionState.EXECUTION_STATE_PAUSED;
//		astroState.ingestAgentState(agentState);
//		control.onAstrobeeStateChange(astroState);
//		listener.setExpectedMessage(publisher.buildGoToStepCommand(
//				FreeFlyerCommands.SUBSYSTEM_NAME,
//				rpi.getNextThing().point, rpi.getNextThing().command));
//		clickCommandButton("Skip");
//		
//		while(!flag) {
//			Thread.sleep(1000);
//		}
//		flipFlag();
//	}
	
//	private void clickCommandButton(String label) {
//		Display.getDefault().syncExec(new Runnable () {
//			public void run() {				
//				CommandButton button = bot.widget(allOf(widgetOfType(CommandButton.class), withId("gov.nasa.arc.irg.iss.ui.widget.key", label)));
//				Event event = new Event();
//				event.widget = button;
//				event.display = button.getDisplay();
//				
//				button.notifyListeners(SWT.Selection, event);
//			}
//		});
//	}
	
	private CompressedFile createCompressedFile() {
		CompressedFile sample = new CompressedFile();
		sample.compressionType = FileCompressionType.COMPRESSION_TYPE_DEFLATE;
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), CompressPublisher.class.getSimpleName(), 0);
		
		File compressedFile = CompressPublisher.getInstance(agent.name()).compress(new File(testFilepath));
		try {
    		sample.compressedFile.userData.clear();
			sample.compressedFile.userData.addAllByte(IOUtils.toByteArray(new FileInputStream(compressedFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sample;
	}
	
	private void flipFlag() {
		flag = !flag;
	}
	
	private class TestCommandListener implements IRapidMessageListener {
		private Command expectedCommand;
		private CompressedFile expectedFile;
		
		public TestCommandListener() {
			RapidMessageCollector.instance().addRapidMessageListener(FreeFlyer.PARTICIPANT_ID, FreeFlyer.getAgent(), MessageTypeExtAstro.COMPRESSED_FILE_TYPE, this);
			RapidMessageCollector.instance().addRapidMessageListener(FreeFlyer.PARTICIPANT_ID, FreeFlyer.getAgent(), MessageType.COMMAND_TYPE, this);
		}

		@Override
		public void onRapidMessageReceived(Agent agent, MessageType msgType,
				Object msgObj, Object cfgObj) {
			//System.out.println("here");
			if(msgType.equals(MessageType.COMMAND_TYPE)) {
				assertTrue("Incorrect command found. Expected: " + expectedCommand + " Found: " + msgObj, ((Command) msgObj).equals(expectedCommand));
				flipFlag();
			} else if (msgType.equals(MessageTypeExtAstro.COMPRESSED_FILE_TYPE)){
				assertTrue("Incorrect file found. Expected: " + expectedFile + " Found: " + msgObj, ((CompressedFile) msgObj).equals(expectedFile));
				flipFlag();
			}
		}
		
		public void setExpectedMessage(Message msg) {
			if(msg instanceof Command) {
				expectedCommand = (Command) msg;
			} else if(msg instanceof CompressedFile) {
				expectedFile = (CompressedFile) msg;
			} else {
				fail("Incorrect expected message type.");
			}
		}
		
	}
	
	private static void setupContext() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				Display display = Display.getDefault();
				Shell shell = new Shell(display, SWT.NONE);		
				context.set(Composite.class, shell);
				context.set(Display.class, display);
				context.set(Agent.class, agent);
				context.set(RunningPlanInfo.class, rpi);
				
				rpi = ContextInjectionFactory.make(RunningPlanInfo.class, context);
				control = ContextInjectionFactory.make(BeeCommandingOnRunPlanTab.class, context);
			}
		});
	}
	
	private void waitForConnection() {
		try {
			if (DiscoveredAgentRepository.INSTANCE.getDiscoveredAgents().length <= 0) {
				ListenerPlus lp = new ListenerPlus();
				DiscoveredAgentRepository.INSTANCE.addListener(lp);
				while (!lp.isAgentsDiscovered()) {
					Thread.sleep(1000);
				}
			}
		} catch (InterruptedException e1) { // TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("DISCOVERED THE NEW WORLD. (NO SPICES)");
	}
	
	private class ListenerPlus implements IDiscoveredAgentListener {
		boolean agentsDiscovered = false;

		public boolean isAgentsDiscovered() {
			return agentsDiscovered;
		}

		@Override
		public void newAgentsDiscovered(String participantId, Collection<Agent> agents) {
			agentsDiscovered = true;
		}

		@Override
		public void newPartitionsDiscovered(String participantId, Collection<String> partitions) {
			// TODO Auto-generated method stub

		}

		@Override
		public void agentsDisappeared(String participantId,
				Collection<Agent> agents) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void partitionsDisappeared(String participantId,
				Collection<String> partitions) {
			// TODO Auto-generated method stub
			
		}

	};
	
	public static void createParticipant() throws DdsEntityCreationException {
		// -- create the participant
		DdsEntityFactory.createParticipant(FreeFlyer.PARTICIPANT_ID,
				FreeFlyer.PARTICIPANT_ID,
				FreeFlyerPreferences.DOMAIN_ID,
				DdsPreferences.getQosLibrary(FreeFlyer.PARTICIPANT_ID),
				DdsPreferences.getQosProfile(FreeFlyer.PARTICIPANT_ID), null, null);
	}

	public static void setupDds() {
		// -- Load the native RTI DDS libraries
		RtiDdsTarget.loadNativeLibraries();

		// -- Set the implementation
		Dds.setDdsImpl(new RtiDds());

		// -- Set the preferences implementation
		DdsPreferences.setImpl(new FreeFlyerPreferences());

		// -- Make the RAPID types visible
		TypeSupportUtil.addImpl(new RapidTypeSupportUtil());
		TypeSupportUtil.addImpl(new RapidExtAstroBeeTypeSupportUtil());
		TypeSupportUtil.addImpl(new RapidExtArcTypeSupportUtil());
		
		 //-- Create the default factory configuration
        final DomainParticipantFactoryConfig dpfConfig = new DomainParticipantFactoryConfig();
        
        String os = System.getProperty("os.name");
        if(os.startsWith("Windows")) {
        	dpfConfig.qosUrlGroups = new String[]{"RAPID_QOS_PROFILES.xml"};
        	
        } else {
        	String rapidQosPath = new File("").getAbsolutePath().replace("verve.freeflyer.workbench", "simulator.freeflyer" + File.separator + "RAPID_QOS_PROFILES.xml");
        	dpfConfig.qosUrlGroups = new String[]{rapidQosPath};
        }
        
        //String[] qug = new String[1];
        //qug[0] = "/hosts/strangelove/export/home/ddwheele/Workspaces/SmartphoneWB_Revival/gov.nasa.rapid.v2.ui.e4/RAPID_QOS_PROFILES.xml";
        //dpfConfig.qosUrlGroups = aug;
        
        DdsEntityFactory.initDomainParticipantFactory(dpfConfig);
	}
	
}
