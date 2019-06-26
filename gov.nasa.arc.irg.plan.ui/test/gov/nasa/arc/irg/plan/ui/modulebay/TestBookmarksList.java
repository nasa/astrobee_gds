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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.nasa.arc.irg.plan.bookmarks.StationBookmark;
import gov.nasa.arc.irg.plan.bookmarks.StationBookmarkList;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.modulebay.BayNumber;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.model.modulebay.Module.ModuleName;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.ui.io.BookmarkListBuilder;
import gov.nasa.freeflyer.test.helper.TestData;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

public class TestBookmarksList {
	
	private final String BUNDLE_NAME = "gov.nasa.arc.irg.plan.ui"; 
	
	@Test
	public void testWriteBookmarksList() {
		String filename = TestData.createFileName(BUNDLE_NAME, "testBookmarkList.json");
		String truthFilename = TestData.getTestFile(BUNDLE_NAME,"testBookmarkListTruth.json").getAbsolutePath();
		boolean compare1and2 = false;
		try {
			// write it
			StationBookmarkList toSave = generateBookmarkListProgrammatically();
			BookmarkListBuilder blb = new BookmarkListBuilder(toSave);
			blb.write(filename);
			// read it in and read in truth
			// (can't just compare files because no guarantee on write order of elements)
			StationBookmarkList readWritten = null;
			StationBookmarkList readTruth = null;
			try {
				readWritten = blb.readStationBookmark(filename);
				readTruth = blb.readStationBookmark(truthFilename);
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
	public void testReadBookmarksList() {
		String filename = TestData.getTestFile(BUNDLE_NAME,"testBookmarkListTruth.json").getAbsolutePath();
		BookmarkListBuilder blb = new BookmarkListBuilder();
		StationBookmarkList generated = generateBookmarkListProgrammatically();
		
		StationBookmarkList readIn = null;
		try {
			readIn = blb.readStationBookmark(filename);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
		if(readIn != null) {
			assertTrue(generated.equals(readIn));
		}
	}
	
	private StationBookmarkList generateBookmarkListProgrammatically() {
		StationBookmarkList list = new StationBookmarkList();
		
		StationBookmark sb = new StationBookmark("Origin", new Point6Dof());
		list.addBookmark(sb);
		
		ModuleBayPoint columbus = new ModuleBayPoint();
		columbus.setModule(ModuleName.COLUMBUS);
		columbus.setBayNumber(BayNumber.BETWEEN_TWO_THREE);
		StationBookmark sbCol = new StationBookmark("In center", columbus);
		list.addBookmark(sbCol);
		
		ModuleBayPoint mbp = new ModuleBayPoint();
		mbp.setModule(ModuleName.JEM);
		mbp.setBayNumber(BayNumber.ONE);
		mbp.setCenterOne(false);
		mbp.setWallOne(LocationMap.Wall.FWD);
		mbp.setWallOneOffset(0.57);
		mbp.setCenterTwo(false);
		mbp.setWallTwo(LocationMap.Wall.DECK);
		mbp.setWallTwoOffset(0.105);
		mbp.setOrientationWall(LocationMap.Wall.DECK);
		StationBookmark sbJem = new StationBookmark("Not in center", mbp);
		list.addBookmark(sbJem);
		
		return list;
	}
	
	@Test
	public void testStationBookmarkEquals() {
		StationBookmark sb1 = new StationBookmark("hello", new Point6Dof());
		StationBookmark sb2 = new StationBookmark("hello", new Point6Dof());
		StationBookmark sb3 = new StationBookmark("goodbye", new Point6Dof());
		
		assertTrue(sb1.equals(sb2));
		assertFalse(sb2.equals(sb3));
	}
	
	@Test
	public void testStationBookmarkListEquals() {
		StationBookmark hello1 = new StationBookmark("hello", new Point6Dof());
		StationBookmark hello2 = new StationBookmark("hello", new Point6Dof());
		StationBookmark goodbye = new StationBookmark("goodbye", new Point6Dof());
		
		StationBookmarkList list1 = new StationBookmarkList();
		list1.addBookmark(hello1);
		list1.addBookmark(goodbye);
		
		StationBookmarkList list2 = new StationBookmarkList();
		list2.addBookmark(hello2);
		list2.addBookmark(goodbye);
		
		StationBookmarkList list3 = new StationBookmarkList();
		list3.addBookmark(hello2);
		list3.addBookmark(hello1);
		
		assertFalse(list1.equals(list3));
	}	
}
