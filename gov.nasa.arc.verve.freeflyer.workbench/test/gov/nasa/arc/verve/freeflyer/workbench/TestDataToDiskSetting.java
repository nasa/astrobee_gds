package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nasa.arc.irg.plan.freeflyer.config.DataToDiskSetting;
import gov.nasa.arc.irg.plan.freeflyer.config.DataToDiskSetting.RosTopicSetting;
import gov.nasa.arc.irg.plan.ui.io.DataToDiskSettingsLoader;
import gov.nasa.freeflyer.test.helper.TestData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestDataToDiskSetting {
	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";
	@Test
	public void testLoadDataToDiskConfigFromFile() {
		
		try {
			DataToDiskSetting loaded = DataToDiskSettingsLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME, "TestDataToDiskSettings.json").getAbsolutePath());
			
			// test for equals
			DataToDiskSetting created = buildProgrammaticConfig();
			
			assertTrue(created.equals(loaded));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private DataToDiskSetting buildProgrammaticConfig() {
		List<RosTopicSetting> rtset = new ArrayList<RosTopicSetting>();
		
		rtset.add(new RosTopicSetting("RosTopic0","delayed", 10.3f));
		rtset.add(new RosTopicSetting("RosTopic1","immediate", 1.5f));
		rtset.add(new RosTopicSetting("RosTopic2","delayed", 0.1f));
		rtset.add(new RosTopicSetting("RosTopic3","delayed", 3.2f));
		rtset.add(new RosTopicSetting("RosTopic4","delayed", 6.67f));
		rtset.add(new RosTopicSetting("RosTopic5","delayed", 12f));
		rtset.add(new RosTopicSetting("RosTopic6","immediate", 79f));
		
		DataToDiskSetting ret = new DataToDiskSetting();
		ret.setName("TestDataToDiskSettings");
		ret.setType("DataConfigurationFile");
		ret.setTopicSettings(rtset);
		
		return ret;
	}
	
}
