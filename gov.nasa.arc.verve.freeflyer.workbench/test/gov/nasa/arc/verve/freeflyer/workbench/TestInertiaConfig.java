package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList;
import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList.InertiaConfig;
import gov.nasa.arc.irg.plan.ui.io.InertiaConfigListLoader;
import gov.nasa.freeflyer.test.helper.TestData;

import org.junit.Test;

public class TestInertiaConfig {

	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";
	@Test
	public void testLoadInertiaConfigFromFile() {
		try {
			InertiaConfigList loaded = InertiaConfigListLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME,"TestInertiaConfig.json").toString());

			// test for equals
			InertiaConfigList created = buildProgrammaticConfig();
			
			assertTrue(created.equals(loaded));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private InertiaConfigList buildProgrammaticConfig() {
		InertiaConfigList ret = new InertiaConfigList();
		
		InertiaConfig one = new InertiaConfig("UnloadedAstrobee",
				5.f, 
				new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f} );
		InertiaConfig two = new InertiaConfig("OffbalanceAstrobee", 
				7.2f, 
				new float[]{ 1.0f, 0.0f, 0.0f, 0.0f, 0.5f, 0.5f, 0.0f, 0.5f, 0.5f  } );
		InertiaConfig three = new InertiaConfig("Heavy", 
				15.0f, 
				new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f} );
		
		ret.addInertiaConfig(one);
		ret.addInertiaConfig(two);
		ret.addInertiaConfig(three);
		
		return ret;
	}
	
}
