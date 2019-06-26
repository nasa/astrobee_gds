package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds.GuestScienceCommandGds;
import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceConfigList;
import gov.nasa.arc.irg.plan.ui.io.GuestScienceConfigListLoader;
import gov.nasa.freeflyer.test.helper.TestData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestGuestScienceConfig {

	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";
	@Test
	public void testGuestScienceConfigFromFile() {
		
		try {
			GuestScienceConfigList loaded = GuestScienceConfigListLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME, "TestGuestScienceConfigurations.json").getAbsolutePath());
			
			// test for equals
			GuestScienceConfigList created = buildProgrammaticConfig();
			
			assertTrue(created.equals(loaded));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private GuestScienceConfigList buildProgrammaticConfig() {
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
	
}
