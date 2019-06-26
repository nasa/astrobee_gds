package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nasa.arc.irg.freeflyer.rapid.CommandPublisher;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.ExecutingSequenceable;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.RunningPlanInfo;
import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.arc.simulator.freeflyer.FreeFlyerPreferences;
import gov.nasa.arc.simulator.freeflyer.compress.CompressPublisher;
import gov.nasa.arc.verve.freeflyer.workbench.parts.standard.LivePlanPart;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.dds.rti.preferences.DdsPreferences;
import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.system.RtiDds;
import gov.nasa.dds.rti.util.TypeSupportUtil;
import gov.nasa.dds.system.Dds;
import gov.nasa.freeflyer.test.helper.TestData;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;
import gov.nasa.rapid.v2.e4.agent.IDiscoveredAgentListener;
import gov.nasa.rapid.v2.e4.util.RapidTypeSupportUtil;
import gov.nasa.rapid.v2.ui.e4.DdsInitializerBase;
import gov.nasa.rapid.v2.ui.e4.RapidV2UiPreferences;
import gov.nasa.rapid.v2.util.RapidExtArcTypeSupportUtil;

import java.io.File;
import java.util.Collection;
import java.util.ListIterator;
import java.util.Random;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.BeforeClass;
import org.junit.Test;

import rapid.AckCompletedStatus;
import rapid.Command;
import rapid.ext.astrobee.PLAN;
import rapid.ext.astrobee.PLAN_METHOD_RUN_PLAN;
import rapid.ext.astrobee.util.RapidExtAstroBeeTypeSupportUtil;

import com.rti.dds.target.RtiDdsTarget;

// This test doesn't run
public class TestPlanStatusHandler {

	final private String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";
	private static IEclipseContext context;
	private static RunningPlanInfo runningPlanInfo;
	private static LivePlanPart livePlanPart;
	private static FreeFlyer ff;
	private static File f;
	private static Agent agent = Agent.FreeFlyerA;
	private static CompressPublisher publisher;
	private static CommandPublisher commandPublisher;
	private static Command runCommand;
	private static final int TIMEOUT_IN_SEC = 300;

	@BeforeClass
	public static void setup() {
		context = EclipseContextFactory.create();
		ContextInjectionFactory.make(RapidV2UiPreferences.class, context);
		ContextInjectionFactory.make(DdsInitializerBase.class, context);
		
		try {
			createParticipant();
			setupDds();
		} catch (Exception e) {
			System.out.println("Failed to create paticipant.");
			e.printStackTrace();
		}
		setupContext();
		
		publisher = CompressPublisher.getInstance(agent.name());

		commandPublisher = CommandPublisher.getInstance(agent);

		commandPublisher.sendGenericNoParamsCommand(PLAN_METHOD_RUN_PLAN.VALUE,
				PLAN.VALUE);
	}

	@Test
	public void testLotsOfCommands() {
		File planFile = TestData.getTestFile(BUNDLE_NAME,"LotsOfCommands.fplan");

		FreeFlyerPlan plan = createAndRunPlan(planFile);

		printPlan(runningPlanInfo.getExecutingSequenceables());
		verifyCorrectness(plan, runningPlanInfo.getExecutingSequenceables());
	}

	@Test
	public void testZigZags() {
		File planFile = TestData.getTestFile(BUNDLE_NAME,"Zigzags.fplan");

		FreeFlyerPlan plan = createAndRunPlan(planFile);

		printPlan(runningPlanInfo.getExecutingSequenceables());
		verifyCorrectness(plan, runningPlanInfo.getExecutingSequenceables());
	}
	
	@Test
	public void testSoundSurvey() {
		File planFile = TestData.getTestFile(BUNDLE_NAME,"SoundSurvey.fplan");

		FreeFlyerPlan plan = createAndRunPlan(planFile);

		printPlan(runningPlanInfo.getExecutingSequenceables());
		verifyCorrectness(plan, runningPlanInfo.getExecutingSequenceables());
	}
	
	@Test
	public void testAboutX() {
		File planFile = TestData.getTestFile(BUNDLE_NAME,"AboutX.fplan");

		FreeFlyerPlan plan = createAndRunPlan(planFile);

		printPlan(runningPlanInfo.getExecutingSequenceables());
		verifyCorrectness(plan, runningPlanInfo.getExecutingSequenceables());
	}
	
	@Test
	public void testMoveCommandsDelegatCommand() {
		File planFile = TestData.getTestFile(BUNDLE_NAME,"MoveCommandsDelegateCommand.fplan");

		FreeFlyerPlan plan = createAndRunPlan(planFile);

		printPlan(runningPlanInfo.getExecutingSequenceables());
		verifyCorrectness(plan, runningPlanInfo.getExecutingSequenceables());
	}
	
	@Test
	public void testExamplePlan() {
		File planFile = TestData.getTestFile(BUNDLE_NAME,"ExamplePlan.fplan");

		FreeFlyerPlan plan = createAndRunPlan(planFile);

		printPlan(runningPlanInfo.getExecutingSequenceables());
		verifyCorrectness(plan, runningPlanInfo.getExecutingSequenceables());
	}

	private ModuleBayPlan createAndRunPlan(File planFile) {
		PlanBuilder<ModuleBayPlan> planBuilder = PlanBuilder.getPlanBuilder(planFile, ModuleBayPlan.class, true);
		ModuleBayPlan plan = planBuilder.getPlan();

		waitForConnection();

		publisher.compressAndPublishSample("Testing", planFile);
		context.set(ModuleBayPlan.class, null); // only the first test runs
												// without this for some reason
		context.set(ModuleBayPlan.class, plan);

		// was causing tests to not run sometimes because runCommand was sent
		// too quickly after the plan itself
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		commandPublisher.sendCommand(runCommand);
		
		for (int i = 0; !planReceived(plan.getSequence().size()); i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println("wasn't tired");
			}
			if (i >= TIMEOUT_IN_SEC) {
				fail("timed out");
			}
		}

		return plan;
	}

	// Returns true when we have a completed status for every sequenceable in the plan
	private boolean planReceived(int size) {
		if (runningPlanInfo.getExecutingSequenceables() == null) {
			return false;
		} else if (runningPlanInfo.getExecutingSequenceables().length != size) {
			return false;
		} else {
			for (Object o : runningPlanInfo.getExecutingSequenceables()) {
				assertTrue("invalid type", o instanceof ExecutingSequenceable);
				ExecutingSequenceable es = (ExecutingSequenceable) o;

				if (es.getStatus() == null || es.getStatus() == AckCompletedStatus.ACK_COMPLETED_NOT) {
					System.out.println(es.getStatus() + " " + es.getPoint() + ", " + es.getCommand() + " Name: " + es.getName());
					return false;
				}

				if (es.hasChildren()) {
					for (ExecutingSequenceable child : es.getChildren()) {
						if (child.getStatus() == null || es.getStatus() == AckCompletedStatus.ACK_COMPLETED_NOT) {
							System.out.println(child.getStatus() + " " + child.getPoint() + ", " + es.getCommand() + " " + es.getName());
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	private void printPlan(Object[] sequence) {
		for (Object o : sequence) {
			ExecutingSequenceable es = (ExecutingSequenceable) o;

			System.out.print(es.getName());
			System.out.print("\t\t\tPoint: " + es.getPoint());
			System.out.print("\tCommand: " + es.getCommand());
			System.out.println("\tStatus: " + es.getStatus());

			if (es.hasChildren()) {
				for (ExecutingSequenceable child : es.getChildren()) {
					System.out.print(child.getName());
					System.out.print("\t\tPoint: " + child.getPoint());
					System.out.print("\tCommand: " + child.getCommand());
					System.out.println("\tStatus: " + child.getStatus());
				}
			}
		}
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

		int spices = numberOfSpices();
		if (spices == 0) {
			System.out.println("DISCOVERED THE NEW WORLD. (NO SPICES)");
		} else {
			System.out.println("DISCOVERED THE NEW WORLD. (" + spices + " SPICES)");
		}
	}

	private void verifyCorrectness(FreeFlyerPlan plan, Object[] sequence) {
		// plan.addStation(new Station());
		ListIterator<Sequenceable> it = plan.getSequence().listIterator();
		Sequenceable next;
		ExecutingSequenceable es;

		for (Object o : sequence) {
			assertTrue("invalid type found", o instanceof ExecutingSequenceable);
			es = (ExecutingSequenceable) o;

			assertTrue("command " + es.getName() + " should not exist", it.hasNext());
			next = it.next();

			assertEquals("station " + es.getName() + " is missing or incorrect", next, es.getSequenceable());
			assertEquals("incorrect status: " + es.getStatus(), es.getStatus(), AckCompletedStatus.ACK_COMPLETED_OK);

			if (next instanceof Station) {
				ListIterator<Sequenceable> children = ((Station) next).getFlattenedSequence().listIterator();
				Sequenceable nextChild;

				if (es.hasChildren()) {
					for (ExecutingSequenceable child : es.getChildren()) {
						assertTrue("child " + child.getName() + " is illegitimate", children.hasNext());
						nextChild = children.next();
						assertEquals("child " + child.getName() + " is incorrect", nextChild, child.getSequenceable());
						assertEquals("incorrect status: " + child.getStatus(), child.getStatus(),
								AckCompletedStatus.ACK_COMPLETED_OK);
					}
				}
			} else if (next instanceof Segment) {
				continue;
			} else {
				fail("invalid sequenceable found, check plan creation");
			}
		}

		assertFalse("whole plan wasn't found", it.hasNext());
	}

	private static void setupContext() {


		Display display = Display.getCurrent();
		Shell shell = new Shell(display, SWT.NONE);

		context.set(Composite.class, shell);
		context.set(Agent.class, agent);
		livePlanPart = ContextInjectionFactory.make(LivePlanPart.class, context);
		runningPlanInfo = ContextInjectionFactory.make(RunningPlanInfo.class, context);
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
				FreeFlyerPreferences.DOMAIN_ID,// DdsPreferences.getDomainId(FreeFlyer.PARTICIPANT_ID),
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

		// This already happened when making DDSInitializerBase
//		// -- Create the default factory configuration
//		final DomainParticipantFactoryConfig dpfConfig = new DomainParticipantFactoryConfig();
//		dpfConfig.qosUrlGroups = new String[] { "RAPID_QOS_PROFILES.xml" };
//		
//		  String os = System.getProperty("os.name");
//	        if(os.startsWith("Windows")) {
//	        	dpfConfig.qosUrlGroups = new String[]{"RAPID_QOS_PROFILES.xml"};
//	        	
//	        } else {
//	            String rapidQosPath = TestPlanStatusHandler.class.getResource("").getPath();
//	            final String locationMask = TestPlanStatusHandler.class.getPackage().getName().replace(".", File.separator);
//	            rapidQosPath = rapidQosPath.replace("bin"+File.separator+locationMask+File.separator,"");
//	            dpfConfig.qosUrlGroups = new String[]{rapidQosPath+"RAPID_QOS_PROFILES.xml"};
//	        }
//	        
//		// String[] qug = new String[1];
//		// qug[0] =
//		// "/hosts/strangelove/export/home/ddwheele/Workspaces/SmartphoneWB_Revival/gov.nasa.rapid.v2.ui.e4/RAPID_QOS_PROFILES.xml";
//		// dpfConfig.qosUrlGroups = aug;
//
//		DdsEntityFactory.initDomainParticipantFactory(dpfConfig);
	}

	private int numberOfSpices() {
		return (new Random()).nextInt(3);
	}
}
