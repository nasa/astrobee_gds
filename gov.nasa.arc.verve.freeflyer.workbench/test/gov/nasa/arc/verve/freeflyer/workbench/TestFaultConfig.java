package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.plan.freeflyer.config.FaultConfigList;
import gov.nasa.arc.irg.plan.freeflyer.config.FaultInfoGds;
import gov.nasa.arc.irg.plan.ui.io.FaultConfigListLoader;
import gov.nasa.freeflyer.test.helper.TestData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestFaultConfig {
	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";
	@Test
	public void testFaultConfigFromFile() {
		
		try {
			FaultConfigList loaded = FaultConfigListLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME, "TestFaultConfigurations.json").getAbsolutePath());
			
			// test for equals
			FaultConfigList created = buildProgrammaticConfig();
			
			assertTrue(created.equals(loaded));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private FaultConfigList buildProgrammaticConfig() {
		FaultConfigList faultConfigList = new FaultConfigList();
		faultConfigList.setType("FaultConfigurationFile");

		List<String> subsystems = new ArrayList<String>();
		subsystems.add("Subsystem A");
		subsystems.add("Subsystem B");
		faultConfigList.setSubsystems(subsystems);
		
		List<String> nodes = new ArrayList<String>();
		nodes.add("Node 1");
		nodes.add("Node 2");
		nodes.add("Node 3");
		faultConfigList.setNodes(nodes);
		
		List<FaultInfoGds> faults = new ArrayList<FaultInfoGds>();
		faults.add(makeFault1());
		faults.add(makeFault2());
		faults.add(makeFault3());
		faultConfigList.setFaultInfos(faults);
		
		return faultConfigList;
	}
	
	private FaultInfoGds makeFault1() {
		FaultInfoGds fig = new FaultInfoGds();
		fig.setSubsystem((short) 0);
		fig.setNode((short) 0);
		fig.setFaultId(100);
		fig.setWarning(false);
		fig.setFaultDescription("Subsystem A, Node 1, is indisposed");
		return fig;
	}
	
	private FaultInfoGds makeFault2() {
		FaultInfoGds fig = new FaultInfoGds();
		fig.setSubsystem((short) 1);
		fig.setNode((short) 2);
		fig.setFaultId(201);
		fig.setWarning(true);
		fig.setFaultDescription("Subsystem B, Node 3, has the hiccups");
		return fig;
	}
	
	private FaultInfoGds makeFault3() {
		FaultInfoGds fig = new FaultInfoGds();
		fig.setSubsystem((short) 0);
		fig.setNode((short) 1);
		fig.setFaultId(120);
		fig.setWarning(false);
		fig.setFaultDescription("Subsystem A, Node 2, is inoperable");
		return fig;
	}
	

}
