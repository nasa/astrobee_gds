package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nasa.arc.irg.plan.freeflyer.config.KeepoutConfig;
import gov.nasa.arc.irg.plan.freeflyer.config.ZonesConfig;
import gov.nasa.arc.irg.plan.ui.io.ZonesConfigLoaderAndWriter;
import gov.nasa.freeflyer.test.helper.TestData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestZonesConfig {
	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";

	@Test
	public void testWriteZonesConfig() {
		String filename = TestData.createFileName(BUNDLE_NAME, "testTestZonesFile.json");
		String truthFilename = TestData.getTestFile(BUNDLE_NAME,"TestZonesFile.json").getAbsolutePath();
		boolean compare1and2 = false;
		try {
			// write it
			ZonesConfig toSave = buildProgrammaticConfig();
			ZonesConfigLoaderAndWriter.write(filename, toSave);
			
			// read it in and read in truth
			// (can't just compare files because no guarantee on write order of elements)
			ZonesConfig readWritten = null;
			ZonesConfig readTruth = null;
			try {
				readWritten = ZonesConfigLoaderAndWriter.loadFromFile(filename);
				readTruth = ZonesConfigLoaderAndWriter.loadFromFile(truthFilename);
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
	public void testLoadZonesConfigFromFile() {
		try {
			ZonesConfig loaded = ZonesConfigLoaderAndWriter.loadFromFile(TestData.getTestFile(BUNDLE_NAME,"TestZonesFile.json").toString());

			// test for equals
			ZonesConfig created = buildProgrammaticConfig();

			assertTrue(created.equals(loaded));

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	private ZonesConfig buildProgrammaticConfig()  {
		ZonesConfig ret = new ZonesConfig();
		ret.setTimestamp("1459296193930");
		ret.addZone(buildDockKeepout());
		ret.addZone(buildFakeKeepout());
		ret.addZone(buildIssUsFreedom());
		return ret;
	}

	private KeepoutConfig buildDockKeepout() {
		KeepoutConfig ret = new KeepoutConfig();

		ret.setDateCreated("1457559937478");
		ret.setNotes("Don't go here.");
		ret.setAuthor("DW");
		ret.setName("DockKeepout");
		ret.setSafe(false);

		//  [ 5.878, -1.099, 0.356, 6.878, -0.899, 1.056 ]
		List<Float> box1 = new ArrayList<Float>();
		box1.add( 5.878f);
		box1.add(-1.099f);
		box1.add( 0.356f);
		box1.add( 6.878f);
		box1.add(-0.899f);
		box1.add( 1.056f);

		List<List<Float>> seq = new ArrayList<List<Float>>();
		seq.add(box1);
		ret.setSequence(seq);
		
		return ret;
	}

	private KeepoutConfig buildFakeKeepout() {
		KeepoutConfig ret = new KeepoutConfig();
		
		ret.setDateCreated("1459296193930");
		ret.setNotes("Don't go here.");
		ret.setAuthor(null);
		ret.setName("FakeKeepout");
		ret.setSafe(false);

		//  [ -7.8, 0.8, -0.3, -6.8, 1.1, 0.5 ],
		List<Float> box1 = new ArrayList<Float>();
		box1.add(-7.8f);
		box1.add( 0.8f);
		box1.add(-0.3f);
		box1.add(-6.8f);
		box1.add( 1.1f);
		box1.add( 0.5f);

		//  [ -7.5, -7.1, -1.6, -6.6, -5.8, 1.6 ] 
		List<Float> box2 = new ArrayList<Float>();
		box2.add(-7.5f);
		box2.add(-7.1f);
		box2.add(-1.6f);
		box2.add(-6.6f);
		box2.add(-5.8f);
		box2.add( 1.6f);

		List<List<Float>> seq = new ArrayList<List<Float>>();
		seq.add(box1);
		seq.add(box2);
		ret.setSequence(seq);
		
		return ret;
	}

	private KeepoutConfig buildIssUsFreedom() {
		KeepoutConfig ret = new KeepoutConfig();

		ret.setDateCreated("1463703443674");
		ret.setNotes("Go here.");
		ret.setAuthor("chitt");
		ret.setSafe(true);
		ret.setName("ISS_US_FREEDOM");

		// [-3.85,-0.64,-0.66,-3.43,0.67,0.57]
		List<Float> box1 = new ArrayList<Float>();
		box1.add(-3.85f);
		box1.add(-0.64f);
		box1.add(-0.66f);
		box1.add(-3.43f);
		box1.add( 0.67f);
		box1.add( 0.57f);

		// [-3.43,-1.05,-1.05,3.43,1.07,1.06],
		List<Float> box2 = new ArrayList<Float>();
		box2.add(-3.43f);
		box2.add(-1.05f);
		box2.add(-1.05f);
		box2.add( 3.43f);
		box2.add( 1.07f);
		box2.add( 1.06f);

		//  [7.17,-1.23,-1.24,8.44,1.20,1.20],
		List<Float> box3 = new ArrayList<Float>();
		box3.add( 7.17f);
		box3.add(-1.23f);
		box3.add(-1.24f);
		box3.add( 8.44f);
		box3.add( 1.20f);
		box3.add( 1.20f);

		List<List<Float>> seq = new ArrayList<List<Float>>();
		seq.add(box1);
		seq.add(box2);
		seq.add(box3);
		ret.setSequence(seq);

		return ret;
	}
}
