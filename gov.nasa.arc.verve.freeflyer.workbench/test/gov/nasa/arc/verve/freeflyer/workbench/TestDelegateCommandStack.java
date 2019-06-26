package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nasa.arc.irg.plan.freeflyer.command.FreeFlyerCommand;
import gov.nasa.arc.irg.plan.freeflyer.command.Unperch;
import gov.nasa.arc.irg.plan.freeflyer.command.Wait;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList.InertiaConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList.OperatingLimitsConfig;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.model.TypedObject;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.plan.ui.modulebay.UtilsFortesting;
import gov.nasa.arc.irg.plan.ui.plancompiler.PlanCompiler;
import gov.nasa.arc.verve.freeflyer.workbench.parts.planeditor.PlanFileManager;
import gov.nasa.arc.verve.freeflyer.workbench.undo.DelegateCommandStack;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;
import gov.nasa.freeflyer.test.helper.TestData;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.junit.Test;

// You must run this as a Plugin test
public class TestDelegateCommandStack {
	
	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";
	
	private IEclipseContext context;
	private PlanFileManager planFileManager;
	private DelegateCommandStack delegateCommandStack;
	private ModuleBayPlan created, loaded;
	private String simpleName = "SimpleDelegateCommand";
	private String fplanSuffix = ".fplan";
	private String fakeSuffix = ".fake";
	
	private OperatingLimitsConfig conservativeOpLimits;
	private InertiaConfig unloadedInertiaOption;
	
	protected OperatingLimitsConfig getOpLimits() {
		if(conservativeOpLimits == null) {
			conservativeOpLimits = new OperatingLimitsConfig();
			conservativeOpLimits.setProfileName("Conservative");
			conservativeOpLimits.setFlightMode("Flight Mode One");
			conservativeOpLimits.setTargetLinearVelocity(0.1f);
			conservativeOpLimits.setTargetLinearAccel(0.03f);
			conservativeOpLimits.setTargetAngularVelocity(0.02f);
			conservativeOpLimits.setTargetAngularAccel(0.01f);
			conservativeOpLimits.setCollisionDistance(0.1f);
		}
		return conservativeOpLimits;
	}
	
	protected InertiaConfig getInertiaOpt() {
		if(unloadedInertiaOption == null) {
			unloadedInertiaOption = new InertiaConfig();
			unloadedInertiaOption.setName("UnloadedAstrobee");
			unloadedInertiaOption.setMass(5.0f);
			unloadedInertiaOption.setMatrix(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f});
		}
		return unloadedInertiaOption;
	}
	
	@Test
	public void testCommandStackSimplePlanCreation() {
		
		makeSimpleTestPlan(simpleName);
		loadThisPlan(simpleName);

		boolean createdEqualsLoaded = created.equals(loaded);
		
		planFileManager.deletePlan();
		
		assertTrue("CommandStack Simple Plan Creation had wrong result", createdEqualsLoaded);
	}
	
	@Test
	public void testCommandStackStationInsertion() {
		String insertStationName = "InsertStationDelegateCommand";
		makeStationInsertionTestPlan(insertStationName);
		loadThisPlan(insertStationName);

		boolean createdEqualsLoaded = created.equals(loaded);
		planFileManager.deletePlan();
		assertTrue("CommandStack Station Insertion had wrong result", createdEqualsLoaded);

		undoStationInsertionAndChangeNameAndDate();
		loadThisPlan(simpleName);
		createdEqualsLoaded = created.equals(loaded);
		planFileManager.deletePlan();
		assertTrue("Undo Station Insertion had wrong result", createdEqualsLoaded);
	}
	
	@Test
	public void testCommandStackMoveUp() {
		String moveStationUpName = "MoveUpDelegateCommand";
		makeMoveUpTestPlan(moveStationUpName);
		loadThisPlan(moveStationUpName);

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		planFileManager.deletePlan();
		assertTrue("CommandStack Move Up had wrong result", createdEqualsLoaded);
	}
	
	@Test
	public void testCommandStackMoveDown() {
		String moveStationDownName = "MoveDownDelegateCommand";
		makeMoveDownTestPlan(moveStationDownName);
		loadThisPlan(moveStationDownName);

		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		
		boolean createdEqualsLoaded = created.equals(loaded);
		planFileManager.deletePlan();
		assertTrue("CommandStack Move Down had wrong result", createdEqualsLoaded);
	}
	
	@Test
	public void testCommandStackMoveStationCoordinate() {
		String moveStationCoordinateName = "MoveStationCoordinateDelegateCommand";
		makeMoveStationCoordinateTestPlan(moveStationCoordinateName);
		loadThisPlan(moveStationCoordinateName);

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		planFileManager.deletePlan();
		assertTrue("CommandStack Move station coordinate had wrong result", createdEqualsLoaded);
	}
	
	@Test
	public void testCommandStackMoveIgnoreOrientationStationCoordinate() {
		String moveStationIgnoreOrientationName = "MoveStationIgnoreOrientationDelegateCommand";
		makeMoveStationIgnoreOrientationCoordinateTestPlan(moveStationIgnoreOrientationName);
		loadThisPlan(moveStationIgnoreOrientationName);

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		planFileManager.deletePlan();
		assertTrue("CommandStack Move station ignoreOrientation coordinate had wrong result", createdEqualsLoaded);
	}
	
	@Test
	public void testCommandStackDeleteStation() {
		String deleteStationName = "DeleteStationDelegateCommand";
		makeDeleteStationTestPlan(deleteStationName);
		loadThisPlan(deleteStationName);

		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		boolean createdEqualsLoaded = created.equals(loaded);
		planFileManager.deletePlan();
		assertTrue("Command Stack Delete Station had wrong result", createdEqualsLoaded);
	}
	
	@Test
	public void testCommandStackDeleteCommand() {
		makeDeleteCommandTestPlan(simpleName);
		loadThisPlan(simpleName);

		boolean createdEqualsLoaded = created.equals(loaded);

		assertTrue("Command Stack Delete Command had wrong result", createdEqualsLoaded);
		
		planFileManager.deletePlan();
	}
	
	@Test
	public void testCommandStackMoveCommands() {
		String moveCommandsName = "MoveCommandsDelegateCommand";
		makeMoveCommandUpTestPlan(moveCommandsName);
		loadThisPlan(moveCommandsName);

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		planFileManager.deletePlan();
		assertTrue("Command Stack Move Command Up had wrong result", createdEqualsLoaded);
		
		makeMoveCommandDownTestPlan(moveCommandsName);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		createdEqualsLoaded = created.equals(loaded);
		planFileManager.deletePlan();
		assertTrue("Command Stack Move Command Down had wrong result", createdEqualsLoaded);
	}

	private void makeSimpleTestPlan(String planName) {
		//  SimpleDelegateCommand
		makeInjectionRelatedObjects();

		created = planFileManager.setupNewPlan(TestData.createFileName(BUNDLE_NAME, planName + fakeSuffix));
		created.setInertiaConfiguration(getInertiaOpt());
		created.setOperatingLimits(getOpLimits());
		
		ModuleBayStation s0 = (ModuleBayStation) delegateCommandStack.onAppend();
		s0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		ModuleBayStation s1 = (ModuleBayStation)delegateCommandStack.onAppend();
		s1.setCoordinate(new ModuleBayPoint(1f, 0, 5));
		
		PlanCompiler.compilePlan(created, created.getOperatingLimits());
		
		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-01-11T18:31:49Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPST("2018-02-22T20:23:22Z"));
		created.setCreator("ddwheele");
	}
	
	private void makeStationInsertionTestPlan(String planName) {
		// InsertStationDelegateCommand
		makeInjectionRelatedObjects();

		created = planFileManager.setupNewPlan(TestData.createFileName(BUNDLE_NAME, planName + fakeSuffix));

		ModuleBayStation s0 = (ModuleBayStation) delegateCommandStack.onAppend();
		s0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		ModuleBayStation lastStation = (ModuleBayStation)delegateCommandStack.onAppend();
		lastStation.setCoordinate(new ModuleBayPoint(1f, 0, 5));
		
		context.set(TypedObject.class, lastStation);
		delegateCommandStack.onInsert();
		
		Segment seg = created.getNextSegment(0);
		context.set(TypedObject.class, seg);
		delegateCommandStack.onInsert();
		
		created.setInertiaConfiguration(getInertiaOpt());
		created.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(created, created.getOperatingLimits());
		
		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-01-11T18:41:57Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPST("2018-02-22T20:53:02Z"));
		created.setCreator("ddwheele");
	}
	
	private void undoStationInsertionAndChangeNameAndDate() {
		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-01-11T18:31:49Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPST("2018-02-22T20:23:22Z"));
		changePlanNameTo(simpleName);
		
		// two undoes should turn it back into SimplePlan
		delegateCommandStack.onUndo();
		delegateCommandStack.onUndo();
		
		PlanCompiler.compilePlan(created, created.getOperatingLimits());
	}

	private void makeMoveUpTestPlan(String planName) {
		// MoveUpDelegateCommand
		makeInjectionRelatedObjects();

		created = planFileManager.setupNewPlan(TestData.createFileName(BUNDLE_NAME, planName + fakeSuffix));

		ModuleBayStation s0 = (ModuleBayStation) delegateCommandStack.onAppend();
		s0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		ModuleBayStation station1 = (ModuleBayStation)delegateCommandStack.onAppend();
		station1.setCoordinate(new ModuleBayPoint(1f, 0.5f, 5));
		ModuleBayStation station2 = (ModuleBayStation)delegateCommandStack.onAppend();
		station2.setCoordinate(new ModuleBayPoint(1f, -0.5f, 5));
		ModuleBayStation station3 = (ModuleBayStation)delegateCommandStack.onAppend();
		station3.setCoordinate(new ModuleBayPoint(0, 0, 4.5f));
		
		FreeFlyerCommand command = new Wait();
		String commandName = station3.getName()+"."+station3.getSequence().size()+" "+command.getClass().getSimpleName();
		command.setName(commandName);
		station3.addSequenceable(command);
		
		context.set(TypedObject.class, station1);
		delegateCommandStack.onMoveUp();
		context.set(TypedObject.class, station3);
		delegateCommandStack.onMoveUp();
		
		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-01-11T19:06:41Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:51:03Z"));
		created.setCreator("ddwheele");

		created.setInertiaConfiguration(getInertiaOpt());
		created.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(created, created.getOperatingLimits());
	}
	
	protected void makeMoveDownTestPlan(String planName) {
		// MoveDownDelegateCommand
		makeInjectionRelatedObjects();

		created = planFileManager.setupNewPlan(TestData.createFileName(BUNDLE_NAME, planName + fakeSuffix));

		ModuleBayStation s0 = (ModuleBayStation) delegateCommandStack.onAppend();
		s0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		ModuleBayStation station1 = (ModuleBayStation)delegateCommandStack.onAppend();
		station1.setCoordinate(new ModuleBayPoint(1f, 0.5f, 5));
		ModuleBayStation station2 = (ModuleBayStation)delegateCommandStack.onAppend();
		station2.setCoordinate(new ModuleBayPoint(1f, -0.5f, 5));
		ModuleBayStation station3 = (ModuleBayStation)delegateCommandStack.onAppend();
		station3.setCoordinate(new ModuleBayPoint(0, 0, 4.5f));
		
		context.set(TypedObject.class, station2);
		FreeFlyerCommand command = new Wait();
		String commandName = station2.getName()+"."+station2.getSequence().size()+" "+command.getClass().getSimpleName();
		command.setName(commandName);
		station2.addSequenceable(command);
		delegateCommandStack.onMoveDown();
		
		context.set(TypedObject.class, s0);
		delegateCommandStack.onMoveDown();
		
		created.setInertiaConfiguration(getInertiaOpt());
		created.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(created, created.getOperatingLimits());

		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-01-11T19:16:34Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:56:24Z"));
		created.setCreator("ddwheele");
	}
	
	protected void makeMoveStationIgnoreOrientationCoordinateTestPlan(String planName) {
		// MoveStationIgnoreOrientationDelegateCommand
		makeInjectionRelatedObjects();

		created = planFileManager.setupNewPlan(TestData.createFileName(BUNDLE_NAME, planName + fakeSuffix));

		ModuleBayStation s0 = (ModuleBayStation) delegateCommandStack.onAppend();
		s0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		ModuleBayStation station1 = (ModuleBayStation)delegateCommandStack.onAppend();
		station1.setCoordinate(new ModuleBayPoint(1f, 0.5f, 5));
		ModuleBayStation station2 = (ModuleBayStation)delegateCommandStack.onAppend();
		station2.setCoordinate(new ModuleBayPoint(1f, -0.5f, 5));
		
		context.set(TypedObject.class, station1);
		FreeFlyerCommand command = new Wait();
		String commandName = station1.getName()+"."+station1.getSequence().size()+" "+command.getClass().getSimpleName();
		command.setName(commandName);
		station1.addSequenceable(command);
		
		Point6Dof p2 = new Point6Dof(2f, 0.5f, 5f, 0f, 0f, 0f);
		ModuleBayPoint mbp2 = new ModuleBayPoint(p2);
		mbp2.setIgnoreOrientation(true);
		
		context.set(ContextNames.NEW_STATION_LOCATION, mbp2);

		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-04-15T00:42:50Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:53:52Z"));
		created.setCreator("ddwheele");
		
		created.setInertiaConfiguration(getInertiaOpt());
		created.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(created, created.getOperatingLimits());
	}
	
	protected void makeMoveStationCoordinateTestPlan(String planName) {
		// MoveStationCoordinateDelegateCommand
		makeInjectionRelatedObjects();

		created = planFileManager.setupNewPlan(TestData.createFileName(BUNDLE_NAME, planName + fakeSuffix));

		ModuleBayStation s0 = (ModuleBayStation) delegateCommandStack.onAppend();
		s0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		ModuleBayStation station1 = (ModuleBayStation)delegateCommandStack.onAppend();
		station1.setCoordinate(new ModuleBayPoint(1f, 0.5f, 5));
		ModuleBayStation station2 = (ModuleBayStation)delegateCommandStack.onAppend();
		station2.setCoordinate(new ModuleBayPoint(1f, -0.5f, 5));
		
		context.set(TypedObject.class, station1);
		FreeFlyerCommand command = new Wait();
		String commandName = station1.getName()+"."+station1.getSequence().size()+" "+command.getClass().getSimpleName();
		command.setName(commandName);
		station1.addSequenceable(command);
		
		Point6Dof p2 = new Point6Dof(2f, 0.5f, 5f, 0f, 0f, 0f);
		context.set(ContextNames.NEW_STATION_LOCATION, p2);
		
		created.setInertiaConfiguration(getInertiaOpt());
		created.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(created, created.getOperatingLimits());
		
		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-01-11T19:24:23Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:55:15Z"));
		created.setCreator("ddwheele");
	}
	
	protected void makeDeleteStationTestPlan(String planName) {
		// DeleteStationDelegateCommand
		makeInjectionRelatedObjects();

		created = planFileManager.setupNewPlan(TestData.createFileName(BUNDLE_NAME, planName + fakeSuffix));

		ModuleBayStation s0 = (ModuleBayStation) delegateCommandStack.onAppend();
		s0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		ModuleBayStation station1 = (ModuleBayStation) delegateCommandStack.onAppend();
		station1.setCoordinate(new ModuleBayPoint(0.5f,0,5));
		ModuleBayStation station2 = (ModuleBayStation) delegateCommandStack.onAppend();
		station2.setCoordinate(new ModuleBayPoint(1f,0,5));
		ModuleBayStation station3 = (ModuleBayStation) delegateCommandStack.onAppend();
		station3.setCoordinate(new ModuleBayPoint(1.5f,0,5));
		
		context.set(TypedObject.class, station1);
		FreeFlyerCommand command = new Wait();
		String commandName = station1.getName()+"."+station1.getSequence().size()+" "+command.getClass().getSimpleName();
		command.setName(commandName);
		station1.addSequenceable(command);
		
		context.set(TypedObject.class, station2);
		delegateCommandStack.onDelete();
		
		context.set(TypedObject.class, station1);
		delegateCommandStack.onDelete();
		
		created.setInertiaConfiguration(getInertiaOpt());
		created.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(created, created.getOperatingLimits());
		
		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-01-11T19:37:12Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPST("2018-02-22T20:57:29Z"));
		created.setCreator("ddwheele");
	}
	
	protected void makeDeleteCommandTestPlan(String planName) {
		// Simple
		makeInjectionRelatedObjects();

		created = planFileManager.setupNewPlan(TestData.createFileName(BUNDLE_NAME, planName + fakeSuffix));

		ModuleBayStation s0 = (ModuleBayStation) delegateCommandStack.onAppend();
		s0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		Station station1 = (Station)delegateCommandStack.onAppend();
		station1.setCoordinate(new ModuleBayPoint(1f, 0, 5));
		
		FreeFlyerCommand command = new Wait();
		String commandName = station1.getName()+"."+station1.getSequence().size()+" "+command.getClass().getSimpleName();
		command.setName(commandName);
		station1.addSequenceable(command);
		
		context.set(TypedObject.class, command);
		delegateCommandStack.onDelete();
		
		created.setInertiaConfiguration(getInertiaOpt());
		created.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(created, created.getOperatingLimits());
		
		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-01-11T18:31:49Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPST("2018-02-22T20:23:22Z"));
		created.setCreator("ddwheele");
	}

	protected void makeMoveCommandUpTestPlan(String planName) {
		makeInjectionRelatedObjects();

		created = planFileManager.setupNewPlan(TestData.createFileName(BUNDLE_NAME, planName + fakeSuffix));

		ModuleBayStation station0 = (ModuleBayStation) delegateCommandStack.onAppend();
		station0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		
		FreeFlyerCommand command = new Wait();
		String commandName = station0.getName()+"."+station0.getSequence().size()+" "+command.getClass().getSimpleName();
		command.setName(commandName);
		station0.addSequenceable(command);
		
		FreeFlyerCommand unperchCommand = new Unperch();
		String unperchName = station0.getName()+"."+station0.getSequence().size()+" "+unperchCommand.getClass().getSimpleName();
		unperchCommand.setName(unperchName);
		station0.addSequenceable(unperchCommand);
		
		context.set(TypedObject.class, unperchCommand);
		delegateCommandStack.onMoveUp();
		
		created.setInertiaConfiguration(getInertiaOpt());
		created.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(created, created.getOperatingLimits());
		
		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-01-11T19:43:46Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPDT("2016-08-08T16:22:20Z"));
		created.setCreator("ddwheele");
	}
	
	protected void makeMoveCommandDownTestPlan(String planName) {
		makeInjectionRelatedObjects();

		created = planFileManager.setupNewPlan(TestData.createFileName(BUNDLE_NAME, planName + fakeSuffix));

		ModuleBayStation station0 = (ModuleBayStation) delegateCommandStack.onAppend();
		station0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		
		FreeFlyerCommand command = new Wait();
		String commandName = station0.getName()+"."+station0.getSequence().size()+" "+command.getClass().getSimpleName();
		command.setName(commandName);
		station0.addSequenceable(command);
		
		FreeFlyerCommand unperchCommand = new Unperch();
		String unperchName = station0.getName()+"."+station0.getSequence().size()+" "+unperchCommand.getClass().getSimpleName();
		unperchCommand.setName(unperchName);
		station0.addSequenceable(unperchCommand);
		
		context.set(TypedObject.class, command);
		delegateCommandStack.onMoveDown();
		
		created.setInertiaConfiguration(getInertiaOpt());
		created.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(created, created.getOperatingLimits());
		
		created.setDateCreated( UtilsFortesting.makeDateFromStringPST("2016-01-11T19:43:46Z"));
		created.setDateModified(UtilsFortesting.makeDateFromStringPDT("2016-08-08T16:22:20Z"));
		created.setCreator("ddwheele");
	}

	protected void makeInjectionRelatedObjects() {
		if(context == null) {
			context = EclipseContextFactory.create();
			planFileManager = ContextInjectionFactory.make(PlanFileManager.class, context);
			delegateCommandStack = ContextInjectionFactory.make(DelegateCommandStack.class, context);
		}
	}
	
	private void loadThisPlan(String planName) {
		String planFilename = planName+fplanSuffix;
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME, planFilename);
		} catch(Exception e) {
			fail("couldn't find "+planFilename);
			return;
		}
		PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		loaded = pb.getPlan();
	}
	
	protected void spoofDatesAndCreator(String dateCreated, String dateModified) {
		created.setDateCreated( makeDateFromString( dateCreated ));
		created.setDateModified( makeDateFromString( dateModified ));
		created.setCreator("ddwheele");
	}
	
	private void changePlanNameTo(String name) {
		created.setName(name);
		created.setId(name);
	}

	protected Date makeDateFromString(String str) {
		Calendar cal = Calendar.getInstance();
		String delims = "[-T:Z]";
		String[] tokens = str.split(delims);
		int year = Integer.parseInt(tokens[0]);
		// month is 0-based(?!)
		int month = Integer.parseInt(tokens[1]) - 1;
		int day = Integer.parseInt(tokens[2]);
		// Date is written in GMT (PST + 7)
		int hour = Integer.parseInt(tokens[3]) - 8;
		if(hour < 0) {
			hour += 24;
			day -= 1;
		}
		int minute = Integer.parseInt(tokens[4]);
		int second = Integer.parseInt(tokens[5]);
		cal.set(year, month, day, hour, minute, second);
		return cal.getTime();
	}
}
