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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nasa.arc.irg.plan.freeflyer.command.ArmPanAndTilt;
import gov.nasa.arc.irg.plan.freeflyer.command.ClearData;
import gov.nasa.arc.irg.plan.freeflyer.command.CustomGuestScience;
import gov.nasa.arc.irg.plan.freeflyer.command.Dock;
import gov.nasa.arc.irg.plan.freeflyer.command.DownloadData;
import gov.nasa.arc.irg.plan.freeflyer.command.GenericCommand;
import gov.nasa.arc.irg.plan.freeflyer.command.GripperControl;
import gov.nasa.arc.irg.plan.freeflyer.command.IdlePropulsion;
import gov.nasa.arc.irg.plan.freeflyer.command.PausePlan;
import gov.nasa.arc.irg.plan.freeflyer.command.Perch;
import gov.nasa.arc.irg.plan.freeflyer.command.PowerOffItem;
import gov.nasa.arc.irg.plan.freeflyer.command.PowerOnItem;
import gov.nasa.arc.irg.plan.freeflyer.command.SetCamera;
import gov.nasa.arc.irg.plan.freeflyer.command.SetCameraRecording;
import gov.nasa.arc.irg.plan.freeflyer.command.SetCameraStreaming;
import gov.nasa.arc.irg.plan.freeflyer.command.SetFlashlightBrightness;
import gov.nasa.arc.irg.plan.freeflyer.command.StartGuestScience;
import gov.nasa.arc.irg.plan.freeflyer.command.StopGuestScience;
import gov.nasa.arc.irg.plan.freeflyer.command.Undock;
import gov.nasa.arc.irg.plan.freeflyer.command.Unperch;
import gov.nasa.arc.irg.plan.freeflyer.command.Wait;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds.GuestScienceCommandGds;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceConfigList;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList.InertiaConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList.OperatingLimitsConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.OptionsForOneCamera;
import gov.nasa.arc.irg.plan.freeflyer.config.PlanPayloadConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.PlanPayloadConfigList;
import gov.nasa.arc.irg.plan.freeflyer.config.SetCameraPresetsList;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.model.modulebay.BayNumber;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.model.modulebay.Module.ModuleName;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.plan.ui.io.PlanPayloadConfigListLoader;
import gov.nasa.arc.irg.plan.ui.plancompiler.PlanCompiler;
import gov.nasa.arc.irg.plan.util.PlanConstants;
import gov.nasa.freeflyer.test.helper.TestData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestModuleBayPlan {
	final private String BUNDLE_NAME = "gov.nasa.arc.irg.plan.ui";
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
	public void testModuleBayPlan() {
		String planName = "ModuleBayPlan1";

		String planFilename = planName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,planFilename);
		} catch(Exception e) {
			fail("couldn't find "+planFilename);
			return;
		}
		ModuleBayPlan created = makeModuleBayPlan(planName + ".fake");

		PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		ModuleBayPlan loaded = pb.getPlan();

		assertNotNull("ModuleBayPlan didn't load", loaded);

		boolean createdEqualsLoaded = created.equals(loaded);

		for(int i=0; i<created.getSequence().size(); i++) {
			Sequenceable cr = created.getSequence().get(i);
			Sequenceable ld = loaded.getSequence().get(i);
			if(!cr.equals(ld)) {
				System.out.println(cr.toString() + " does not equal " + 
						ld.toString() );
			}
		}

		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("ModuleBayPlan had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testSimpleModuleBayPlan() {
		String planName = "SimpleModuleBayPlan";

		String planFilename = planName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,planFilename);
		} catch(Exception e) {
			fail("couldn't find "+planFilename);
			return;
		}
		ModuleBayPlan created = makeSimplePlan(planName + ".fake");

		PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		ModuleBayPlan loaded = pb.getPlan();

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("Simple ModuleBayPlan had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testPlanWithGuestScienceCommands() {
		String planName = "PlanWithGuestScienceCommands";

		String planFilename = planName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,planFilename);
		} catch(Exception e) {
			fail("couldn't find "+planFilename);
			return;
		}
		ModuleBayPlan created = makePlanWithGuestScienceCommands(planName + ".fake");

		PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		ModuleBayPlan loaded = pb.getPlan();

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("testPlanWithGuestScienceCommands had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testPlanWithPayloadCommands() {
		String planName = "PlanWithPayloadCommands";

		String planFilename = planName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,planFilename);
		} catch(Exception e) {
			fail("couldn't find "+planFilename);
			return;
		}
		ModuleBayPlan created = makePlanWithPayloadCommands(planName + ".fake");

		PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		ModuleBayPlan loaded = pb.getPlan();

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("testPlanWithPayloadCommands had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testPlanWithSetCameraCommands() {
		String planName = "PlanWithSetCameraCommands";

		String planFilename = planName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,planFilename);
		} catch(Exception e) {
			fail("couldn't find "+planFilename);
			return;
		}
		ModuleBayPlan created = makePlanWithSetCameraCommands(planName + ".fake");

		PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		ModuleBayPlan loaded = pb.getPlan();

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("testPlanWithSetCameraCommands had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testPlanWithAllSimpleCommands() {
		String planName = "AllCommandsPlan";

		String planFilename = planName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,planFilename);
		} catch(Exception e) {
			fail("couldn't find "+planFilename);
			return;
		}
		ModuleBayPlan created = makePlanWithAllSimpleCommands(planName + ".fake");

		PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		ModuleBayPlan loaded = pb.getPlan();

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("testPlanWithAllCommands had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testInsertNewStationInMiddle() {
		String planName = "InsertNewStationAfter";

		String planFilename = planName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,planFilename);
		} catch(Exception e) {
			fail("couldn't find "+planFilename);
			return;
		}
		ModuleBayPlan created = makePlanInsertNewStationInMiddle(planName + ".fake");

		PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		ModuleBayPlan loaded = pb.getPlan();

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("testInsertNewStationInMiddle had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testInsertNewStationAtEndOfPlan() {
		String planName = "SimpleModuleBayPlan";

		String planFilename = planName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,planFilename);
		} catch(Exception e) {
			fail("couldn't find "+planFilename);
			return;
		}
		ModuleBayPlan created = makePlanInsertNewStationAtEnd(planName + ".fake");

		PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		ModuleBayPlan loaded = pb.getPlan();

		boolean createdEqualsLoaded = created.equals(loaded);
		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("testInsertNewStationAtEndOfPlan had wrong result", createdEqualsLoaded);
	}

	@Test
	public void testInsertStationAndSetLocation() {
		String planName = "InsertStationAndSetLocationPlan1";

		String planFilename = planName+".fplan";
		File f;
		try {
			f = TestData.getTestFile(BUNDLE_NAME,planFilename);
		} catch(Exception e) {
			fail("couldn't find "+planFilename);
			return;
		}
		ModuleBayPlan created = makePlanInsertStationAndSetLocation(planName + ".fake");

		PlanBuilder<ModuleBayPlan> pb = PlanBuilder.getPlanBuilder(f, ModuleBayPlan.class, true);
		ModuleBayPlan loaded = pb.getPlan();
		PlanCompiler.compilePlan(loaded, loaded.getOperatingLimits());		

		boolean createdEqualsLoaded = created.equals(loaded);

		for(int i=0; i<created.getSequence().size(); i++) { 			
			if(!created.getSequence().get(i).equals(loaded.getSequence().get(i))) {
				Sequenceable cr = created.getSequence().get(i);
				Sequenceable ld = loaded.getSequence().get(i);
				System.out.println(cr.toString() + " does not equal " + 
						ld.toString());
			}
		}

		UtilsFortesting.moduleBayPlansShouldBeEqual(created, loaded);
		assertTrue("ModuleBay InsertStation had wrong result", createdEqualsLoaded);
	}

	// exercises Insert branch
	protected ModuleBayPlan makePlanInsertNewStationInMiddle(String planName) {
		// InsertNewStationAfter
		ModuleBayPlan mbp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ModuleBayStation first = mbp.appendNewStation();
		ModuleBayPoint pt1 = new ModuleBayPoint();
		pt1.setModule(ModuleName.US_LAB);
		pt1.setBayNumber(BayNumber.ONE);
		first.setCoordinate(pt1);

		ModuleBayStation second = mbp.appendNewStation();
		ModuleBayPoint pt2 = new ModuleBayPoint();
		pt2.setModule(ModuleName.US_LAB);
		pt2.setBayNumber(BayNumber.THREE);
		second.setCoordinate(pt2);

		mbp.insertNewStationAfter(first);

		mbp.setInertiaConfiguration(getInertiaOpt());
		mbp.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(mbp, mbp.getOperatingLimits());

		mbp.setCreator("ddwheele");
		mbp.setDateCreated( UtilsFortesting.makeDateFromStringPST( "2016-01-11T22:14:02Z" ));
		mbp.setDateModified( UtilsFortesting.makeDateFromStringPDT( "2019-06-14T13:46:12Z" ));
		return mbp;
	}

	protected ModuleBayPlan makePlanInsertStationAndSetLocation(String planName) {
		ModuleBayPlan mbp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ModuleBayStation s1 = mbp.appendNewStation();
		s1.setCoordinate(makeUSLabPointBayOne());

		ModuleBayStation s3 = mbp.appendNewStation();
		s3.setCoordinate(makeUSLabPointBayThree());

		ModuleBayStation s2 = (ModuleBayStation)mbp.insertNewStation(s3);
		ModuleBayPoint p2 = makeUSLabPointBtwOneTwo();
		p2.setIgnoreOrientation(true);
		s2.setCoordinate(p2);

		mbp.setInertiaConfiguration(getInertiaOpt());
		mbp.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(mbp, mbp.getOperatingLimits());

		mbp.setCreator("ddwheele");
		mbp.setDateCreated( UtilsFortesting.makeDateFromStringPDT( "2018-04-27T22:17:56Z" ));
		mbp.setDateModified( UtilsFortesting.makeDateFromStringPDT( "2018-04-27T22:41:11Z" ));
		return mbp;
	}

	protected ModuleBayPlan makeModuleBayPlan(String planName) {
		// ModuleBayPlan.fake
		ModuleBayPlan mbp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		ModuleBayStation s1 = mbp.appendNewStation();
		s1.setCoordinate(makeUSLabPointBayOne());

		ModuleBayStation s2 = mbp.appendNewStation();
		s2.setCoordinate(makeUSLabPointBtwOneTwo());

		ModuleBayStation s3 = mbp.appendNewStation();
		s3.setCoordinate(makeUSLabPointBayThree());

		mbp.setInertiaConfiguration(getInertiaOpt());
		mbp.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(mbp, mbp.getOperatingLimits());

		mbp.setDateCreated( UtilsFortesting.makeDateFromStringPDT( "2018-04-27T21:48:01Z" ));
		mbp.setDateModified( UtilsFortesting.makeDateFromStringPDT( "2019-06-14T13:45:00Z" ));
		
		return mbp;
	}

	protected ModuleBayPoint makeUSLabPointBtwOneTwo() {
		ModuleBayPoint s2 = new ModuleBayPoint();
		s2.setModule(ModuleName.US_LAB);
		s2.setBayNumber(BayNumber.BETWEEN_ONE_TWO);
		s2.setCenterOne(true);
		s2.setCenterTwo(true);
		return s2;
	}

	protected ModuleBayPoint makeUSLabPointBayThree() {
		ModuleBayPoint s3p = new ModuleBayPoint();
		s3p.setModule(ModuleName.US_LAB);
		s3p.setBayNumber(BayNumber.THREE);
		s3p.setCenterOne(false);
		s3p.setWallOne(LocationMap.Wall.PORT);
		s3p.setWallOneOffset(0.5);
		s3p.setCenterTwo(true);
		return s3p;
	}

	protected ModuleBayPoint makeUSLabPointBayOne() {
		ModuleBayPoint s1p = new ModuleBayPoint();
		s1p.setModule(ModuleName.US_LAB);
		s1p.setBayNumber(BayNumber.ONE);
		s1p.setCenterOne(false);
		s1p.setWallOne(LocationMap.Wall.STBD);
		s1p.setWallOneOffset(0.3);
		s1p.setCenterTwo(false);
		s1p.setWallTwo(LocationMap.Wall.DECK);
		s1p.setWallTwoOffset(0.4);
		s1p.setIgnoreOrientation(false);
		s1p.setOrientationWall(LocationMap.Wall.STBD);
		return s1p;
	}

	protected ModuleBayPlan makePlanWithPayloadCommands(String planName) {
		try {
			PlanPayloadConfigList loaded = PlanPayloadConfigListLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME, "TestPlanPayloadConfigurations.json").getAbsolutePath());

			ModuleBayPlan mbp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

			final ModuleBayStation station0 = mbp.appendNewStation();
			PowerOnItem c00 = new PowerOnItem();
			c00.setPlanPayloadConfig(loaded.getPlanPayloadConfigs().get(0));
			c00.setWhich(loaded.getPlanPayloadConfigs().get(0).getName());
			station0.addSequenceable(c00);

			PowerOffItem c01 = new PowerOffItem();
			c01.setPlanPayloadConfig(loaded.getPlanPayloadConfigs().get(1));
			c01.setWhich(loaded.getPlanPayloadConfigs().get(1).getName());
			station0.addSequenceable(c01);

			mbp.setInertiaConfiguration(getInertiaOpt());
			mbp.setOperatingLimits(getOpLimits());
			PlanCompiler.compilePlan(mbp, mbp.getOperatingLimits());

			mbp.setCreator("ddwheele");
			mbp.setDateCreated( UtilsFortesting.makeDateFromStringPST( "2017-02-10T23:33:54Z" ));
			mbp.setDateModified( UtilsFortesting.makeDateFromStringPST("2017-02-11T00:00:52Z" ));
			return mbp;

		} catch (Exception e) {
			fail(e.getMessage());
			return null;
		}
	}

	protected ModuleBayPlan makePlanWithGuestScienceCommands(String planName) {
		GuestScienceConfigList loaded = buildProgrammaticGuestScienceConfig();
		ModuleBayPlan mbp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		final ModuleBayStation station0 = mbp.appendNewStation();
		StartGuestScience c00 = new StartGuestScience();
		GuestScienceApkGds gsagA = loaded.getGuestScienceConfigs().get(0);
		c00.setGuestScienceApkGds(gsagA);
		c00.setApkName(gsagA.getApkName());
		station0.addSequenceable(c00);

		StopGuestScience c01 = new StopGuestScience();
		GuestScienceApkGds gsagC = loaded.getGuestScienceConfigs().get(2);
		c00.setGuestScienceApkGds(gsagC);
		c00.setApkName(gsagC.getApkName());
		station0.addSequenceable(c01);

		CustomGuestScience c02 = new CustomGuestScience();
		c02.setCommandIndex("0");
		c02.setGuestScienceApkGds(gsagA);
		c02.setApkName(gsagA.getApkName());
		c02.setCommand(gsagA.getGuestScienceCommands().get(0).getCommand());
		station0.addSequenceable(c02);

		mbp.setInertiaConfiguration(getInertiaOpt());
		mbp.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(mbp, mbp.getOperatingLimits());

		mbp.setCreator("ddwheele");
		mbp.setDateCreated( UtilsFortesting.makeDateFromStringPST( "2017-02-10T23:13:37Z" ));
		mbp.setDateModified( UtilsFortesting.makeDateFromStringPST( "2017-02-10T23:20:25Z" ));
		return mbp;
	}

	private OptionsForOneCamera getOptionsFor(String camName, SetCameraPresetsList loaded) {
		for(OptionsForOneCamera o : loaded.getOptionsForOneCamera()) {
			if(camName.equals(o.getCameraName())) {
				return o;
			}
		}
		fail("No options for camera named "+camName);
		return null;
	}

	protected ModuleBayPlan makePlanWithSetCameraCommands(String planName) {
		try {
			SetCameraPresetsList created = buildProgrammaticSetCameraPresetsList();

			ModuleBayPlan mbp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

			final ModuleBayStation station0 = mbp.appendNewStation();

			SetCamera c00 = new SetCamera();
			c00.setCameraName(PlanConstants.NAV_CAM_NAME);
			OptionsForOneCamera opts = getOptionsFor(PlanConstants.NAV_CAM_NAME, created);
			c00.setOptions(opts);
			c00.setPresetIndex("0");
			station0.addSequenceable(c00);


			SetCamera c01 = new SetCamera();
			c01.setCameraName(PlanConstants.DOCK_CAM_NAME);
			OptionsForOneCamera opts1 = getOptionsFor(PlanConstants.DOCK_CAM_NAME, created);
			c01.setOptions(opts1);
			c01.setPresetIndex("1");
			station0.addSequenceable(c01);

			mbp.setInertiaConfiguration(getInertiaOpt());
			mbp.setOperatingLimits(getOpLimits());
			PlanCompiler.compilePlan(mbp, mbp.getOperatingLimits());

			mbp.setCreator("ddwheele");
			mbp.setDateCreated( UtilsFortesting.makeDateFromStringPST( "2017-02-17T20:26:47Z" ));
			mbp.setDateModified( UtilsFortesting.makeDateFromStringPST( "2017-02-17T22:20:47Z" ));
			return mbp;

		} catch (Exception e) {
			fail("couldn't load " + BUNDLE_NAME + "TestSetCameraPresetsList.json");
		}
		return null;
	}

	protected ModuleBayPlan makePlanWithAllSimpleCommands(String planName) {
		// "AllCommandsPlan"
		ModuleBayPlan mbp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		final ModuleBayStation station0 = mbp.appendNewStation();
		ModuleBayPoint mbp0 = station0.getCoordinate();
		mbp0.setYaw(90);
		
		
		ArmPanAndTilt c00 = new ArmPanAndTilt();
		c00.setPan(21);
		c00.setTilt(37);
		station0.addSequenceable(c00);

		PausePlan c01 = new PausePlan();
		station0.addSequenceable(c01);

		Undock c02 = new Undock();
		station0.addSequenceable(c02);

		Perch c03 = new Perch();
		station0.addSequenceable(c03);

		Unperch c04 = new Unperch();
		station0.addSequenceable(c04);

		Wait c05 = new Wait();
		c05.setDuration(88);
		station0.addSequenceable(c05);

		IdlePropulsion c06 = new IdlePropulsion();
		station0.addSequenceable(c06);

		DownloadData c07 = new DownloadData();
		c07.setDataMethod(PlanConstants.IMMEDIATE_STRING);
		station0.addSequenceable(c07);

		ClearData c08 = new ClearData();
		c08.setDataMethod(PlanConstants.DELAYED_STRING);
		station0.addSequenceable(c08);

		Dock c09 = new Dock();
		c09.setBerth(1);
		station0.addSequenceable(c09);

		Dock c010 = new Dock();
		c010.setBerth(2);
		station0.addSequenceable(c010);

		GripperControl c011 = new GripperControl();
		c011.setOpen(false);
		station0.addSequenceable(c011);

		GripperControl c012 = new GripperControl();
		c012.setOpen(true);
		station0.addSequenceable(c012);

		SetFlashlightBrightness c013 = new SetFlashlightBrightness();
		c013.setBrightness(1.0f);
		c013.setWhich(PlanConstants.FLASHLIGHT_FRONT);
		station0.addSequenceable(c013);

		SetCameraRecording c014 = new SetCameraRecording();
		c014.setCameraName(PlanConstants.SCI_CAM_NAME);
		c014.setRecord(true);
		station0.addSequenceable(c014);

		SetCameraRecording c015 = new SetCameraRecording();
		c015.setCameraName(PlanConstants.NAV_CAM_NAME);
		c015.setRecord(false);
		station0.addSequenceable(c015);

		SetCameraStreaming c016 = new SetCameraStreaming();
		c016.setCameraName(PlanConstants.HAZ_CAM_NAME);
		c016.setStream(true);
		station0.addSequenceable(c016);

		SetCameraStreaming c017 = new SetCameraStreaming();
		c017.setCameraName(PlanConstants.DOCK_CAM_NAME);
		c017.setStream(false);
		station0.addSequenceable(c017);
		
		GenericCommand c018 = new GenericCommand();
		c018.setCommandName("derelict");
		c018.setParam("preconceptions");
		station0.addSequenceable(c018);

		mbp.setInertiaConfiguration(getInertiaOpt());
		mbp.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(mbp, mbp.getOperatingLimits());

		mbp.setCreator("ddwheele");
		mbp.setDateCreated( UtilsFortesting.makeDateFromStringPDT( "2017-07-06T17:24:04Z" ));
		mbp.setDateModified( UtilsFortesting.makeDateFromStringPDT( "2017-07-06T17:57:17Z" ));
		return mbp;
	}

	// exercises append branch
	protected ModuleBayPlan makePlanInsertNewStationAtEnd(String planName) {
		// SimpleModuleBayPlan.fplan
		ModuleBayPlan mbp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		Station s1 = mbp.appendNewStation();
		s1.setCoordinate(new ModuleBayPoint(0, 0, 5));
		mbp.insertNewStationAfter(s1);

		mbp.setInertiaConfiguration(getInertiaOpt());
		mbp.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(mbp, mbp.getOperatingLimits());

		mbp.setCreator("ddwheele");
		mbp.setDateCreated( UtilsFortesting.makeDateFromStringPST( "2018-02-21T21:52:06Z" ));
		mbp.setDateModified( UtilsFortesting.makeDateFromStringPST( "2018-02-21T21:58:37Z" ));
		return mbp;
	}

	protected ModuleBayPlan makeSimplePlan(String planName) {
		// SimpleModuleBayPlan.fplan
		ModuleBayPlan mbp = UtilsFortesting.createNewPlan(planName, ModuleBayPlan.class);

		Station s1 = mbp.appendNewStation();
		s1.setCoordinate(new ModuleBayPoint(0, 0, 5));
		Station s2 = mbp.appendNewStation();
		s2.setCoordinate(new ModuleBayPoint(0, 0, 5));

		mbp.setInertiaConfiguration(getInertiaOpt());
		mbp.setOperatingLimits(getOpLimits());
		PlanCompiler.compilePlan(mbp, mbp.getOperatingLimits());

		mbp.setCreator("ddwheele");
		mbp.setDateCreated( UtilsFortesting.makeDateFromStringPST( "2018-02-21T21:52:06Z" ));
		mbp.setDateModified( UtilsFortesting.makeDateFromStringPST( "2018-02-21T21:58:37Z" ));
		return mbp;
	}

	protected void spoofDatesAndCreatorPST(ModuleBayPlan plan, String dateCreated, String dateModified) {
		plan.setDateCreated( UtilsFortesting.makeDateFromStringPST( dateCreated ));
		plan.setDateModified( UtilsFortesting.makeDateFromStringPST( dateModified ));
		plan.setCreator("ddwheele");
	}

	protected void spoofDatesAndCreatorPDT(ModuleBayPlan plan, String dateCreated, String dateModified) {
		plan.setDateCreated( UtilsFortesting.makeDateFromStringPDT( dateCreated ));
		plan.setDateModified( UtilsFortesting.makeDateFromStringPDT( dateModified ));
		plan.setCreator("ddwheele");
	}

	/////////////////////////////////////
	// For some reason loading the GuestScienceConfig from this file doesn't work in Bamboo WHY???
	
	private SetCameraPresetsList buildProgrammaticSetCameraPresetsList() {
		SetCameraPresetsList list = new SetCameraPresetsList();
		list.setType("SetCameraPresetsList");

		List<OptionsForOneCamera> optionsForOneCamera = new ArrayList<OptionsForOneCamera>();

		OptionsForOneCamera dockCamOptions = makeDockCamOptions();
		OptionsForOneCamera navCamOptions = makeNavCamOptions();
		
		optionsForOneCamera.add(dockCamOptions);
		optionsForOneCamera.add(navCamOptions);
		list.setOptionsForOneCamera(optionsForOneCamera);

		return list;
	}
	
	private OptionsForOneCamera makeDockCamOptions() {
		OptionsForOneCamera camOptions = new OptionsForOneCamera();
		camOptions.setCameraName("Dock");
		
		OptionsForOneCamera.CameraPreset cp1 = makeAPreset("High Def","1024_768",5,640);
		OptionsForOneCamera.CameraPreset cp2 = makeAPreset("Low Def","640_480",4,92);
		
		List<OptionsForOneCamera.CameraPreset> presets = new ArrayList<OptionsForOneCamera.CameraPreset>();
		presets.add(cp1);
		presets.add(cp2);
		camOptions.setPreset(presets);
		return camOptions;
	}
	
	private OptionsForOneCamera makeNavCamOptions() {
		OptionsForOneCamera camOptions = new OptionsForOneCamera();
		camOptions.setCameraName("Navigation");
		
		OptionsForOneCamera.CameraPreset cp1 = makeAPreset("High Def","1920_1080",5,100);
		OptionsForOneCamera.CameraPreset cp2 = makeAPreset("Low Def","640_480",25,300);
		
		List<OptionsForOneCamera.CameraPreset> presets = new ArrayList<OptionsForOneCamera.CameraPreset>();
		presets.add(cp1);
		presets.add(cp2);
		camOptions.setPreset(presets);
		
		return camOptions;
	}
	
	private OptionsForOneCamera.CameraPreset makeAPreset(String name, String res, float fr, float bw) {
		OptionsForOneCamera.CameraPreset cp1 = new OptionsForOneCamera.CameraPreset();
		cp1.setPresetName(name);
		cp1.setResolution(res);
		cp1.setFrameRate(fr);
		cp1.setBandwidth(bw);
		return cp1;
	}

	/////////////////////////////////////
	// For some reason loading the GuestScienceConfig from this file doesn't work in Bamboo

	private GuestScienceConfigList buildProgrammaticGuestScienceConfig() {
		GuestScienceConfigList list = new GuestScienceConfigList();
		list.setType("GuestScienceConfigurationFile");

		GuestScienceApkGds gsA = makeGuestScienceAConfig();
		GuestScienceApkGds gsB = makeGuestScienceBConfig();
		GuestScienceApkGds gsC = makeGuestScienceCConfig();

		List<GuestScienceApkGds> guestScienceApkGds = new ArrayList<GuestScienceApkGds>();
		guestScienceApkGds.add(gsA);
		guestScienceApkGds.add(gsB);
		guestScienceApkGds.add(gsC);

		list.setGuestScienceConfigs(guestScienceApkGds);

		return list;
	}

	private GuestScienceApkGds makeGuestScienceAConfig() {
		GuestScienceCommandGds a1 = new GuestScienceCommandGds();
		a1.setName("Command A 1");
		a1.setCommand("command body for command A 1");
		a1.setPower(10);
		a1.setDuration(11);

		GuestScienceCommandGds a2 = new GuestScienceCommandGds();
		a2.setName("Command A 2");
		a2.setCommand("command body for command A 2");
		a2.setPower(20);

		GuestScienceCommandGds a3 = new GuestScienceCommandGds();
		a3.setName("Command A 3");
		a3.setCommand("command body for command A 3");
		a3.setDuration(31);

		GuestScienceApkGds gsA = new GuestScienceApkGds();
		gsA.setApkName("gov.nasa.arc.irg.astrobee.GuestScienceA");
		gsA.setShortName("Guest Science A");
		gsA.setPrimary(true);

		List<GuestScienceCommandGds> cmdListA = new ArrayList<GuestScienceCommandGds>();
		cmdListA.add(a1);
		cmdListA.add(a2);
		cmdListA.add(a3);
		gsA.setGuestScienceCommands(cmdListA);

		return gsA;
	}

	private GuestScienceApkGds makeGuestScienceBConfig() {
		GuestScienceApkGds gsB = new GuestScienceApkGds();
		gsB.setApkName("gov.nasa.arc.irg.astrobee.GuestScienceB");
		gsB.setShortName("Guest Science B");
		gsB.setPrimary(true);
		gsB.setPower(12);
		gsB.setDuration(19);
		return gsB;
	}

	private GuestScienceApkGds makeGuestScienceCConfig() {
		GuestScienceCommandGds c1 = new GuestScienceCommandGds();
		c1.setName("Command C 1");
		c1.setCommand("command body for command C 1");

		GuestScienceApkGds gsC = new GuestScienceApkGds();
		gsC.setApkName("gov.nasa.arc.irg.astrobee.GuestScienceC");
		gsC.setShortName("Guest Science C");
		gsC.setPrimary(false);

		gsC.setPower(11);

		List<GuestScienceCommandGds> cmdListC = new ArrayList<GuestScienceCommandGds>();
		cmdListC.add(c1);
		gsC.setGuestScienceCommands(cmdListC);

		return gsC;
	}

	/////////////////////////////////////
	// when this is in its own file, it fails in Bamboo (even though it runs in Eclipse)
	@Test
	public void testPlanPayloadConfigFromFile() {

		try {
			PlanPayloadConfigList loaded = PlanPayloadConfigListLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME, "TestPlanPayloadConfigurations.json").getAbsolutePath());

			// test for equals
			PlanPayloadConfigList created = buildProgrammaticConfig();
			UtilsFortesting.planPayloadConfigListsShouldBeEqual(created, loaded);
			assertTrue(created.equals(loaded));

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private PlanPayloadConfigList buildProgrammaticConfig() {
		PlanPayloadConfigList list = new PlanPayloadConfigList();
		list.setType("PlanPayloadConfigurationFile");

		PlanPayloadConfig gsA = makePayloadAConfig();
		PlanPayloadConfig gsB = makePayloadBConfig();

		List<PlanPayloadConfig> guestScienceApkGds = new ArrayList<PlanPayloadConfig>();
		guestScienceApkGds.add(gsA);
		guestScienceApkGds.add(gsB);

		list.setPlanPayloadConfigs(guestScienceApkGds);

		return list;
	}

	private PlanPayloadConfig makePayloadAConfig() {
		PlanPayloadConfig ret = new PlanPayloadConfig();
		ret.setName("Payload A");
		ret.setPower(25);
		return ret;
	}

	private PlanPayloadConfig makePayloadBConfig() {
		PlanPayloadConfig ret = new PlanPayloadConfig();
		ret.setName("Payload B");
		return ret;
	}

}
