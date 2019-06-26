package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList.OperatingLimitsConfig;
import gov.nasa.arc.irg.plan.ui.io.OperatingLimitsConfigListLoader;
import gov.nasa.freeflyer.test.helper.TestData;

import org.junit.Test;

public class TestOperatingLimitsConfig {
	
	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";
	@Test
	public void testLoadOperatingLimitsConfigFromFile() {
		
		try {
//			OperatingLimitsConfigList loaded = null;//OperatingLimitsConfigListLoader.loadFromFile(TestData.getTestFile("TestOpLimitsConfig.json").toString());
			OperatingLimitsConfigList loaded = OperatingLimitsConfigListLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME, "TestOperatingLimitsConfigurations.json").getAbsolutePath());
			
			// test for equals
			OperatingLimitsConfigList created = buildProgrammaticConfig();
			
			assertTrue(created.equals(loaded));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private OperatingLimitsConfigList buildProgrammaticConfig() {
		OperatingLimitsConfigList ret = new OperatingLimitsConfigList();
		
		OperatingLimitsConfig one = new OperatingLimitsConfig("Conservative",
				"Flight Mode One", 0.1f, 0.03f, 0.02f, 0.01f, 0.1f );
		
		
		OperatingLimitsConfig two = new OperatingLimitsConfig("Speedy",
		    	 "Flight Mode Two", 5.0f, 0.05f, 0.1f, 0.1f, 0.01f);
		
		
		OperatingLimitsConfig three = new OperatingLimitsConfig("IgnoreObstacles",
		    	"Flight Mode Three", 0.1f, 0.03f, 0.02f, 0.01f, 0.0f);
		
		ret.addOperatingLimitsConfig(one);
		ret.addOperatingLimitsConfig(two);
		ret.addOperatingLimitsConfig(three);
		
		return ret;
	}
	
}
