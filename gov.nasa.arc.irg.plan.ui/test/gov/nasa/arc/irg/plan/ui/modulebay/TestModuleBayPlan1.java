/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.irg.plan.ui.modulebay;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.plan.freeflyer.command.ArmPanAndTilt;
import gov.nasa.arc.irg.plan.freeflyer.command.Dock;
import gov.nasa.arc.irg.plan.freeflyer.command.PausePlan;
import gov.nasa.arc.irg.plan.freeflyer.command.PowerOnItem;
import gov.nasa.arc.irg.plan.freeflyer.command.Perch;
import gov.nasa.arc.irg.plan.freeflyer.command.Undock;
import gov.nasa.arc.irg.plan.freeflyer.command.Unperch;
import gov.nasa.arc.irg.plan.freeflyer.command.Wait;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList.InertiaConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList.OperatingLimitsConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.PlanPayloadConfig;
import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.irg.plan.freeflyer.plan.PointCommand;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.plan.ui.plancompiler.PlanCompiler;
import gov.nasa.freeflyer.test.helper.TestData;

import java.io.File;

import org.junit.Test;

// Used to be FreeFlyerPlan, but we can't actually make FreeFlyerPlans with our GUI anymore
public class TestModuleBayPlan1 {

	private final String BUNDLE_NAME = "gov.nasa.arc.irg.plan.ui";

	final String defaultSiteName = "ISS Analysis Frame";
	final String defaultSiteId = "iss";
	
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
	public void testGetFinalPointCommand() {

		final FreeFlyerPlan created = makeTwoStationsPlan(TestData.createFileName(BUNDLE_NAME,"simple.fake"));
		final PointCommand last = created.getFinalPointCommand();
		final PointCommand shouldBe = new PointCommand(1, -1);
		assertTrue("Reported wrong final PointCommand for plan with empty final Station", last.equals(shouldBe));

		final FreeFlyerPlan complexCreated = makeComplexPlan(TestData.createFileName(BUNDLE_NAME,"Complex.fake"));
		final PointCommand lastComplex = complexCreated.getFinalPointCommand();
		final PointCommand complexShouldBe = new PointCommand(4, 0);
		assertTrue("Reported wrong final PointCommand for plan with final Station with children", lastComplex.equals(complexShouldBe));
	}

	@Test
	public void testGetNextExecutableElement() {
		final FreeFlyerPlan complexCreated = makeComplexPlan(TestData.createFileName(BUNDLE_NAME, "Complex.fake"));

		final PointCommand offTheEnd = complexCreated.getNextExecutableElement( new PointCommand(4,0) );
		assertNull("Didn't get null for command off the end of a plan", offTheEnd);

		// Station with another kid
		final PointCommand shouldBe01 = complexCreated.getNextExecutableElement( new PointCommand(0,0) );
		assertTrue("Didn't go to next command", shouldBe01.equals( new PointCommand(0,1) ));

		// Station with no more kids
		final PointCommand shouldBe3minus1 = complexCreated.getNextExecutableElement( new PointCommand(2,3) );
		assertTrue("Didn't go to next segment", shouldBe3minus1.equals( new PointCommand(3,-1) ));

		// segment before Station with kids
		final PointCommand shouldBe20 = complexCreated.getNextExecutableElement( new PointCommand(1,-1) );
		assertTrue("Didn't go to child of next station", shouldBe20.equals( new PointCommand(2,0) ));

		// segment before Station with no kids
		final FreeFlyerPlan createdMoveDown = makeMoveStationDownPlan(TestData.createFileName(BUNDLE_NAME, "movedown.fake"));
		final PointCommand shouldBe7minus1 = createdMoveDown.getNextExecutableElement( new PointCommand(5,-1) );
		assertTrue("Didn't go to child of next station", shouldBe7minus1.equals( new PointCommand(7,-1) ));
	}

	@Test
	public void testRearrangeCommands() {
		final String baseName = "RearrangeCommands";
		final ModuleBayPlan created = makeRearrangeCommandsPlan(TestData.createFileName(BUNDLE_NAME,baseName+".fake"));
		final String simplePlanFilename = baseName+".fplan";

		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,simplePlanFilename);
		} catch(final Exception e) {
			fail("couldn't find "+simplePlanFilename);
			return;
		}
		final PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		final ModuleBayPlan loaded = pb.getPlan();

		final boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);

		assertTrue("Rearrange Commands had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testStationMoveUp() {
		final String baseName = "MoveStationUp";
		final ModuleBayPlan created = makeMoveStationUpPlan(TestData.createFileName(BUNDLE_NAME, baseName+".fake"));
		
		final String simplePlanFilename = baseName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME, simplePlanFilename);
		} catch(final Exception e) {
			fail("couldn't find "+simplePlanFilename);
			return;
		}
		final PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		final ModuleBayPlan loaded = pb.getPlan();

		final boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);

		assertTrue("Move Station Up had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testStationMoveDown() {
		final String BaseName = "MoveStationDown"; 
		final ModuleBayPlan created = makeMoveStationDownPlan(TestData.createFileName(BUNDLE_NAME, BaseName+".fake"));
		
		final String simplePlanFilename = BaseName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,simplePlanFilename);
		} catch(final Exception e) {
			fail("couldn't find "+simplePlanFilename);
			return;
		}
		final PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		final ModuleBayPlan loaded = pb.getPlan();

		final boolean createdEqualsLoaded = created.equals(loaded);

		assertTrue("Move Station Down had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testSimplePlanCreation() {
		// Make a plan with a Station, Segment, Station, and make sure it looks right
		final String baseName = "Simple";
		final ModuleBayPlan created = makeTwoStationsPlan(TestData.createFileName(BUNDLE_NAME,baseName+".fake"));
		
		final String simplePlanFilename = baseName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME, simplePlanFilename);
		} catch(final Exception e) {
			fail("couldn't find "+simplePlanFilename);
			return;
		}
		final PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		final ModuleBayPlan loaded = pb.getPlan();

		final boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);

		assertTrue("Created Plan not equal to Loaded Plan", createdEqualsLoaded);
	}

	@Test
	public void testComplexPlanCreation() {
		final String baseName = "Complex";
		final ModuleBayPlan complexCreated = makeComplexPlan(TestData.createFileName(BUNDLE_NAME, baseName+".fake"));
		
		final String complexPlanFilename = baseName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME, complexPlanFilename);
		} catch(final Exception e) {
			fail("Couldn't find "+ complexPlanFilename);
			return;
		}
		final PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		final ModuleBayPlan complexLoaded = pb.getPlan();

		final boolean createdEqualsLoaded = complexCreated.equals(complexLoaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(complexCreated, complexLoaded);

		assertTrue("Created complex plan not equal to loaded complex plan", createdEqualsLoaded);
	}

	@Test
	public void testStationInsertion() {
		// Make a plan, insert a station, make sure it looks right
		final String baseName = "StationInsertion";
		final ModuleBayPlan created = makeStationInsertionTestPlan(TestData.createFileName(BUNDLE_NAME,baseName+".fake"));

		final String simplePlanFilename = baseName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,simplePlanFilename);
		} catch(final Exception e) {
			fail("couldn't find "+simplePlanFilename);
			return;
		}
		final PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		final ModuleBayPlan loaded = pb.getPlan();

		final boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("Station insertion had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testStationDeletion() {
		// Make a plan, delete a station, make sure it looks right
		final String baseName = "StationDeletion";
		final ModuleBayPlan created = makeStationDeletionTestPlan(TestData.createFileName(BUNDLE_NAME, baseName + ".fake"));

		final String simplePlanFilename = baseName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME, simplePlanFilename);
		} catch(final Exception e) {
			fail("couldn't find "+simplePlanFilename);
			return;
		}
		final PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		final ModuleBayPlan loaded = pb.getPlan();

		final boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);

		assertTrue("Station deletion had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testCommandInsertion() {
		// Make a plan, add subcommands, delete them, make sure it looks right
		final String baseName = "CommandInsertion";
		final ModuleBayPlan created = makeCommandInsertionPlan(TestData.createFileName(BUNDLE_NAME, baseName + ".fake"));

		// Then check that the plan is the same
		final String simplePlanFilename = baseName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,simplePlanFilename);
		} catch(final Exception e) {
			fail("couldn't find "+simplePlanFilename);
			return;
		}
		final PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		final ModuleBayPlan loaded = pb.getPlan();

		final boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("Command insertion had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testCommandDeletion() {
		// Make a plan, add subcommands, delete them, make sure it looks right
		final String baseName = "CommandDeletion";
		final ModuleBayPlan created = makeCommandDeletionPlan(TestData.createFileName(BUNDLE_NAME, baseName + ".fake"));

		// Then check that the plan is the same
		final String simplePlanFilename = baseName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,simplePlanFilename);
		} catch(final Exception e) {
			fail("couldn't find "+simplePlanFilename);
			return;
		}
		final PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		final ModuleBayPlan loaded = pb.getPlan();

		final boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("Command deletion had wrong result", createdEqualsLoaded);
	}

	protected ModuleBayPlan makeComplexPlan(final String planName) {
		// Complex.fplan
		final ModuleBayPlan ffp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ffp.setDateCreated(UtilsFortesting.makeDateFromStringPDT("2016-08-01T16:37:23Z"));
		ffp.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:27:46Z"));

		final ModuleBayStation station0 = ffp.appendNewStation();
		station0.setCoordinate(new ModuleBayPoint(0, 0, 5));
		final ModuleBayStation station1 = ffp.appendNewStation();
		moveStationUpHalfMeter(station1);
		final ModuleBayStation station2 = ffp.appendNewStation();
		moveStationOverHalfMeter(station2);

		final Undock undock = new Undock();
		undock.setName("0.0 Undock");
		station0.addSequenceable(undock);

		final PowerOnItem po = new PowerOnItem();
		po.setName("0.1 PowerOnItem");
		po.setPlanPayloadConfig(new PlanPayloadConfig("LaserPointer", 1));
		station0.addSequenceable(po);

		final Wait recStart = new Wait();
		recStart.setName("0.2 Wait");
		recStart.setDuration(2007);
		station0.addSequenceable(recStart);

		// change the speed of Segment 0-1
		final Segment seg01 = ffp.getNextSegment(0);
		seg01.setUseCustomSpeed(true);
		seg01.setSpeed(0.2f);

		// move station1 somewhere else
		final ModuleBayPoint st1start = station1.getCoordinate();
		st1start.setX(1.5f);
		st1start.setY(-0.65f);
		st1start.setZ(5.5f);
		station1.setCoordinate(st1start);

		final Perch perch = new Perch();
		perch.setName("1.0 Perch");
		station1.addSequenceable(perch);
		final ArmPanAndTilt apat = new ArmPanAndTilt();
		apat.setName("1.1 ArmPanAndTilt");
		apat.setPan(55);
		apat.setTilt(75);
		station1.addSequenceable(apat);
		final PausePlan pause = new PausePlan();
		pause.setName("1.2 PausePlan");
		station1.addSequenceable(pause);
		final Unperch unperch = new Unperch();
		unperch.setName("1.3 Unperch");
		station1.addSequenceable(unperch);

		final ModuleBayPoint s2start = station2.getCoordinate();
		s2start.setZ(4.6f);
		s2start.setRoll(2);
		s2start.setPitch(20);
		s2start.setYaw(178);
		station2.setCoordinate(s2start);

		final Dock dock = new Dock();
		dock.setName("2.0 Dock");
		station2.addSequenceable(dock);

		ffp.setOperatingLimits(getOpLimits());
		ffp.setInertiaConfiguration(getInertiaOpt());
		System.out.println("Compiling ComplexPlan");
		PlanCompiler.compilePlan(ffp, ffp.getOperatingLimits());
		return ffp;
	}

	protected ModuleBayPlan makeCommandInsertionPlan(final String planName) {
		//CommandInsertion
		final ModuleBayPlan ffp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ffp.setDateCreated(UtilsFortesting.makeDateFromStringPST("2016-01-20T18:18:36Z"));
		ffp.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:30:53Z"));

		final Station station0 = ffp.appendNewStation();
		station0.setCoordinate(new ModuleBayPoint(0,0,5));
		final Wait sk = new Wait();
		sk.setName("0.0 Wait");
		sk.setDuration(5);
		station0.addSequenceable(sk);

		final ModuleBayStation station1 = ffp.appendNewStation();
		moveStationUpHalfMeter(station1);
		final ArmPanAndTilt apat1 = new ArmPanAndTilt();
		apat1.setName("1.0 ArmPanAndTilt");
		apat1.setPan(14);
		apat1.setTilt(17);
		station1.addSequenceable(apat1);

		final ArmPanAndTilt apat2 = new ArmPanAndTilt();
		apat2.setName("1.1 ArmPanAndTilt");
		apat2.setPan(6);
		apat2.setTilt(12);
		station1.addSequenceable(apat2);

		final ModuleBayStation station2 = ffp.appendNewStation();
		moveStationOverHalfMeter(station2);
		final Wait sk3 = new Wait();
		sk3.setName("2.0 Wait");
		sk3.setDuration(10);
		station2.addSequenceable(sk3);

		// Insert at beginning
		final ArmPanAndTilt o1 = new ArmPanAndTilt();
		o1.setPan(120);
		o1.setTilt(98);
		o1.setName("0.0 ArmPanAndTilt");
		station0.insertSequenceable(o1, sk);

		// Insert in middle		
		final Wait o2 = new Wait();
		o2.setDuration(30);
		o2.setName("1.1 Wait");
		station1.insertSequenceable(o2, apat2);

		ffp.setOperatingLimits(getOpLimits());
		ffp.setInertiaConfiguration(getInertiaOpt());
		PlanCompiler.compilePlan(ffp, ffp.getOperatingLimits());
		return ffp;
	}

	protected ModuleBayPlan makeCommandDeletionPlan(final String planName) {
		// CommandDeletion
		final ModuleBayPlan ffp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ffp.setDateCreated(UtilsFortesting.makeDateFromStringPST("2016-11-14T22:39:09Z"));
		ffp.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:29:25Z"));

		final ModuleBayStation firstStation = ffp.appendNewStation();
		firstStation.setCoordinate(new ModuleBayPoint(0,0,5));
		final ModuleBayPoint s0start = firstStation.getCoordinate();
		s0start.setRoll(1);
		s0start.setPitch(2);
		s0start.setYaw(3);
		firstStation.setCoordinate(s0start);
		final ArmPanAndTilt sk = new ArmPanAndTilt();
		sk.setName("0.0 ArmPanAndTilt");
		sk.setPan(44);
		sk.setTilt(88);
		firstStation.addSequenceable(sk);

		final Wait o1 = new Wait();
		o1.setDuration(30);
		o1.setName("0.1 Wait");
		firstStation.addSequenceable(o1);

		final ArmPanAndTilt apat = new ArmPanAndTilt();
		apat.setName("0.2 ArmPanAndTilt");
		apat.setPan(37);
		apat.setTilt(57);
		firstStation.addSequenceable(apat);

		final ModuleBayStation secondStation = ffp.appendNewStation();
		moveStationUpHalfMeter(secondStation);
		final Wait sk1 = new Wait();
		sk1.setName("1.0 Wait");
		sk1.setDuration(10);
		secondStation.addSequenceable(sk1);

		final Sequenceable seq = firstStation.getSequenceable(1);

		// then remove Wait
		firstStation.removeSequenceable(seq);

		secondStation.removeSequenceable(0);

		ffp.setOperatingLimits(getOpLimits());
		ffp.setInertiaConfiguration(getInertiaOpt());
		
		PlanCompiler.compilePlan(ffp, ffp.getOperatingLimits());
		return ffp;
	}

	protected ModuleBayPlan makeStationInsertionTestPlan(final String planName) {
		//StationInsertion
		final ModuleBayPlan ffp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ffp.setDateCreated(UtilsFortesting.makeDateFromStringPST("2016-01-20T18:03:00Z"));
		ffp.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:13:45Z"));

		ModuleBayStation station0 = ffp.appendNewStation();
		final ModuleBayPoint s0coord = station0.getCoordinate();
		s0coord.setX(-0.5f);
		s0coord.setY(-0.5f);
		s0coord.setZ( 4.5f);
		
		final ModuleBayStation station4 = ffp.appendNewStation();
		final ModuleBayPoint s4coord = station4.getCoordinate();
		s4coord.setX(0.5f);
		s4coord.setY(0.5f);
		s4coord.setZ(5.5f);

		ModuleBayStation station1 = (ModuleBayStation) ffp.insertNewStation(station4);
		station1.setCoordinate(new ModuleBayPoint(0,0,5));

		final ModuleBayStation station2 = new ModuleBayStation();
		final ModuleBayPoint s2coord = station2.getCoordinate();
		s2coord.setX(0.25f);
		s2coord.setY(0.25f);
		s2coord.setZ(5.25f);
		s2coord.setRoll(90);
		s2coord.setPitch(90);
		s2coord.setYaw(90);
		ffp.insertThisStation(station2, station4);
		
		// this one should have 90 degrees rotations
		ModuleBayStation station3 = (ModuleBayStation) ffp.insertNewStation(station4);
		
		ffp.setOperatingLimits(getOpLimits());
		ffp.setInertiaConfiguration(getInertiaOpt());
		
		PlanCompiler.compilePlan(ffp, ffp.getOperatingLimits());
		return ffp;
	}

	protected ModuleBayPlan makeStationDeletionTestPlan(final String planName) {
		// StationDeletion
		final ModuleBayPlan ffp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ffp.setDateCreated(UtilsFortesting.makeDateFromStringPST("2016-01-20T18:12:59Z"));
		ffp.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:23:22Z"));

		ModuleBayStation s0 = ffp.appendNewStation();
		s0.setCoordinate(new ModuleBayPoint(0,0,5));
		final ModuleBayStation station1 = ffp.appendNewStation();
		moveStationUpHalfMeter(station1);
		final Wait o00 = new Wait();
		o00.setDuration(2);
		o00.setName("1.0 Wait");
		station1.addSequenceable(o00);
		final Wait o01 = new Wait();
		o01.setDuration(4);
		o01.setName("1.1 Wait");
		station1.addSequenceable(o01);

		final ModuleBayStation s2 = ffp.appendNewStation();
		moveStationOverHalfMeter(s2);

		ffp.removeSequenceable(station1);
		
		ffp.setInertiaConfiguration(getInertiaOpt());
		ffp.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(ffp, ffp.getOperatingLimits());
		return ffp;
	}

	private ModuleBayPlan makeRearrangeCommandsPlan(final String planName) {
		//RearrangeCommands
		final ModuleBayPlan ffp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ffp.setDateCreated(UtilsFortesting.makeDateFromStringPST("2016-01-20T17:34:19Z"));
		ffp.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:32:00Z"));

		final ModuleBayStation station0 = ffp.appendNewStation();
		station0.setCoordinate(new ModuleBayPoint(0,0,5));
		final Wait o00 = new Wait();
		o00.setDuration(2);
		o00.setName("0.0 Wait");
		station0.addSequenceable(o00);
		final Wait o01 = new Wait();
		o01.setDuration(4);
		o01.setName("0.1 Wait");
		station0.addSequenceable(o01);

		final ModuleBayStation station1 = ffp.appendNewStation();
		moveStationUpHalfMeter(station1);
		final Wait o10 = new Wait();
		o10.setDuration(5);
		o10.setName("1.0 Wait");
		station1.addSequenceable(o10);
		final Wait o11 = new Wait();
		o11.setDuration(10);
		o11.setName("1.1 Wait");
		station1.addSequenceable(o11);

		final ModuleBayStation s2 = ffp.appendNewStation();
		moveStationOverHalfMeter(s2);

		// move o00 down
		station0.moveThisCommandDown(o00);

		// move o11 up
		station1.moveThisCommandUp(o11);

		ffp.setInertiaConfiguration(getInertiaOpt());
		ffp.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(ffp, ffp.getOperatingLimits());
		return ffp;
	}

	protected ModuleBayPlan makeTwoStationsPlan(final String planName) {
		// Simple.fplan
		final ModuleBayPlan ffp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ModuleBayStation s0 = ffp.appendNewStation();
		s0.setCoordinate(new ModuleBayPoint(0, 0, 5));

		final ModuleBayStation s1 = ffp.appendNewStation();
		moveStationUpHalfMeter(s1);
		
		Segment seg01 = (Segment)s1.getPrevious();
		seg01.setFaceForward(false);

		ffp.setInertiaConfiguration(getInertiaOpt());
		ffp.setOperatingLimits(getOpLimits());
		
		PlanCompiler.compilePlan(ffp, ffp.getOperatingLimits());

		ffp.setDateCreated(UtilsFortesting.makeDateFromStringPST("2016-01-20T00:20:20Z"));
		ffp.setDateModified(UtilsFortesting.makeDateFromStringPDT("2018-07-06T23:49:30Z"));

		return ffp;
	}

	protected ModuleBayPlan makeMoveStationUpPlan(final String planName) {
		//MoveStationUp
		final ModuleBayPlan ffp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ffp.setDateCreated(UtilsFortesting.makeDateFromStringPST("2016-01-20T17:47:04Z"));
		ffp.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:37:52Z"));

		ModuleBayStation station0 = ffp.appendNewStation();
		station0.setCoordinate(new ModuleBayPoint(0,0,5));

		final ModuleBayStation s1 = ffp.appendNewStation();
		moveStationOverHalfMeter(s1);

		final ModuleBayStation s2 = ffp.appendNewStation();
		moveStationOverHalfMeter(s2);

		final ModuleBayStation station3 = ffp.appendNewStation();
		moveStationOverHalfMeter(station3);
		final Wait sk3 = new Wait();
		sk3.setDuration(10);
		station3.addSequenceable(sk3);

		final ModuleBayStation s4 = ffp.appendNewStation();
		moveStationOverHalfMeter(s4);

		// move station 3 up
		ffp.moveThisStationUp(station3);
		
		ffp.setOperatingLimits(getOpLimits());
		ffp.setInertiaConfiguration(getInertiaOpt());
		boolean valid = PlanCompiler.compilePlan(ffp, ffp.getOperatingLimits());
		if(!valid) {
			String msg = "MoveStationUp: " + PlanCompiler.getLastErrorMessage();
			fail(msg);
		}
		return ffp;
	}

	protected ModuleBayPlan makeMoveStationDownPlan(final String planName) {
		// MoveStationDown
		final ModuleBayPlan ffp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ffp.setDateCreated(UtilsFortesting.makeDateFromStringPST("2016-01-20T17:55:25Z"));
		ffp.setDateModified(UtilsFortesting.makeDateFromStringPDT("2019-06-14T13:36:59Z"));

		ModuleBayStation station0 = ffp.appendNewStation();
		station0.setCoordinate(new ModuleBayPoint(0,0,5));
		final ModuleBayStation station1 = ffp.appendNewStation();
		moveStationOverHalfMeter(station1);
		final Wait o3 = new Wait();
		o3.setDuration(3);
		o3.setName("1.0 Wait");
		station1.addSequenceable(o3);

		final ModuleBayStation s2 = ffp.appendNewStation();
		moveStationOverHalfMeter(s2);
		final ModuleBayStation s3 = ffp.appendNewStation();
		moveStationOverHalfMeter(s3);
		final ModuleBayStation s4 = ffp.appendNewStation();
		moveStationOverHalfMeter(s4);

		// move station 1 down
		ffp.moveThisStationDown(station1);
		
		ffp.setOperatingLimits(getOpLimits());
		ffp.setInertiaConfiguration(getInertiaOpt());
		boolean valid = PlanCompiler.compilePlan(ffp, ffp.getOperatingLimits());
		if(!valid) {
			String msg = "MoveStationDown: " + PlanCompiler.getLastErrorMessage();
			fail(msg);
		}
		return ffp;
	}

	// So I don't have to remake all the plans now that new stations
	// show up on top of old stations
	protected void moveStationUpHalfMeter(final ModuleBayStation station) {
		final ModuleBayPoint s1start = station.getCoordinate();
		final float oldZ = s1start.getZ();
		s1start.setZ( oldZ - 0.5f );
	}
	
	protected void moveStationOverHalfMeter(final ModuleBayStation station) {
		final ModuleBayPoint s1start = station.getCoordinate();
		final float oldX = s1start.getX();
		s1start.setX( oldX - 0.5f );
	}

}
