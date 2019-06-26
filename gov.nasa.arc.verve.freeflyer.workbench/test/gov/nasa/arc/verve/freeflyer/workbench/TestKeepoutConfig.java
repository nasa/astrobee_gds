package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nasa.arc.irg.plan.freeflyer.config.KeepoutConfig;
import gov.nasa.arc.irg.plan.ui.io.KeepoutConfigLoaderAndWriter;
import gov.nasa.freeflyer.test.helper.TestData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestKeepoutConfig {

	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";
	
	@Test
	public void testWriteKeepoutConfig() {
		String filename = TestData.createFileName(BUNDLE_NAME, "testTestKeepoutFile.json");
		String truthFilename = TestData.getTestFile(BUNDLE_NAME,"TestKeepoutFile.json").getAbsolutePath();
		boolean compare1and2 = false;
		try {
			// write it
			KeepoutConfig toSave = buildProgrammaticConfig();
			KeepoutConfigLoaderAndWriter.write(filename, toSave);
			
			// read it in and read in truth
			// (can't just compare files because no guarantee on write order of elements)
			KeepoutConfig readWritten = null;
			KeepoutConfig readTruth = null;
			try {
				readWritten = KeepoutConfigLoaderAndWriter.loadFromFile(filename);
				readTruth =KeepoutConfigLoaderAndWriter.loadFromFile(truthFilename);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				fail();
				return;
			}
			
			// compare them
			compare1and2 = readTruth.equals(readWritten);
			new File(filename).delete();
		} catch(Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
		assertTrue(compare1and2);
	}
	
	@Test
	public void testLoadKeepoutConfigFromFile() {
		try {
			KeepoutConfig loaded = KeepoutConfigLoaderAndWriter.loadFromFile(TestData.getTestFile(BUNDLE_NAME,"TestKeepoutFile.json").toString());

			// test for equals
			KeepoutConfig created = buildProgrammaticConfig();
			
			assertTrue(created.equals(loaded));
			
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private KeepoutConfig buildProgrammaticConfig()  {
		KeepoutConfig ret = new KeepoutConfig();
		
		ret.setDateCreated("1456251730865");
		ret.setNotes("Don't go here.");
		ret.setAuthor("DW");
		ret.setName("TestKeepouts");
		ret.setSafe(false);
		
		// [ -8, 4, 7, 2, 5, 9 ], 
		List<Float> box1 = new ArrayList<Float>();
		box1.add(-8f);
		box1.add( 4f);
		box1.add( 7f);
		box1.add( 2f);
		box1.add( 5f);
		box1.add( 9f);
		
		// [ 3, 6, -1, 4, 12, 18 ]
		List<Float> box2 = new ArrayList<Float>();
		box2.add( 3f);
		box2.add( 6f);
		box2.add(-1f);
		box2.add( 4f);
		box2.add(12f);
		box2.add(18f);
		
		List<List<Float>> seq = new ArrayList<List<Float>>();
		seq.add(box1);
		seq.add(box2);
		ret.setSequence(seq);
		
		return ret;
	}
	
}






















