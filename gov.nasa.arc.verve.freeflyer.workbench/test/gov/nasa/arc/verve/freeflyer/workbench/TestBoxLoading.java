package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.plan.json.JsonBox;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.ColoredBoxList;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.ColoredBoxList.ColoredBox;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.ColoredBoxListLoader;
import gov.nasa.freeflyer.test.helper.TestData;

import java.util.List;

import org.junit.Test;

public class TestBoxLoading {
	
	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";

	@Test
	public void testLoadingColoredBoxes() {
		try {
			ColoredBoxList loaded = ColoredBoxListLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME,"TestColoredBoxes.json").toString());

			// test for equals
			ColoredBoxList created = buildProgrammaticColoredBoxList();
			
			assertTrue(created.equals(loaded));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private ColoredBoxList buildProgrammaticColoredBoxList() {
		ColoredBoxList ret = new ColoredBoxList();
		ret.setType("ColoredBoxesConfigurationFile");
		
		ColoredBox one = new ColoredBox("MockDock",
				new float[] {0.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f},
				new float[] {20, 30, 40 });
		
		ret.addColoredBox(one);
		
		return ret;
	}
	
	@Test
	public void testLoadingJsonBoxFromFile() {
		try {
			JsonBox loaded = new JsonBox(TestData.getTestFile(BUNDLE_NAME,"TestKeepoutFile.json"));

			List<double[]> boxes = loaded.getBoxes();
			
			assertEquals(2, boxes.size());
			assertTrue(arraysAreEqual(new double[]{-8, 4, 7, 2, 5, 9}, boxes.get(0)));
			assertTrue(arraysAreEqual(new double[]{ 3, 6, -1, 4, 12, 18 }, boxes.get(1)));
			assertEquals("DW", loaded.getAuthor());
			assertEquals("TestKeepouts", loaded.getName());
			assertEquals(false, loaded.isSafe());
			assertEquals("Don't go here.", loaded.getNotes());
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	public boolean arraysAreEqual(double[] one, double[] two) {
		if(one.length != two.length){
			return false;
		}
		
		for(int i=0; i<one.length; i++) {
			if(one[i] != two[i]) {
				return false;
			}
		}
		
		return true;
	}
}
