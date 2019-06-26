package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nasa.arc.irg.plan.freeflyer.config.OptionsForOneCamera;
import gov.nasa.arc.irg.plan.freeflyer.config.SetCameraPresetsList;
import gov.nasa.arc.irg.plan.ui.io.SetCameraPresetsListLoader;
import gov.nasa.freeflyer.test.helper.TestData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestSetCameraPresets {

	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";
	
	@Test
	public void testLoadSetCameraPresetsFromFile() {
		
		try {
			SetCameraPresetsList loaded = SetCameraPresetsListLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME, "TestSetCameraPresetsList.json").getAbsolutePath());
			
			// test for equals
			SetCameraPresetsList created = buildProgrammaticConfig();
			
			assertTrue(created.equals(loaded));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private SetCameraPresetsList buildProgrammaticConfig() {
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
	
	
	
	
	
	
	
	
}
