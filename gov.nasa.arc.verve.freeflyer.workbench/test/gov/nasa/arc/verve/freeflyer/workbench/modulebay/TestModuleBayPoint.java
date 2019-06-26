package gov.nasa.arc.verve.freeflyer.workbench.modulebay;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.plan.bookmarks.StationBookmark;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.modulebay.BayNumber;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.model.modulebay.Module.ModuleName;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;

import org.junit.Test;

public class TestModuleBayPoint {
	private final float EPSILON = 0.001f;
	// position of US LAB Module 6, Offset Wall 1 = STBD 0.57, Offset Wall 2 = Deck 1.05
	private final double lab6x = -0.61;
	private final double lab6y =  0.52;
	private final double centerz =  4.85;
	
	private final double labYstbdPlus02 = 0.89;
	
	private final double deckPlus02 = 5.72;
	private final double deckPlus105 = 5.81;
	
	private final double jemCenterx = 10.94;
	private final double jemCentery = -6.66;

	@Test
	public void testModuleBayPointCopyConstructor() {
		ModuleBayPoint moduleBayPointFirst= new ModuleBayPoint();
		moduleBayPointFirst.setModule(ModuleName.US_LAB);
		moduleBayPointFirst.setBayNumber(BayNumber.SIX);
		moduleBayPointFirst.setCenterOne(false);
		moduleBayPointFirst.setWallOne(LocationMap.Wall.STBD);
		moduleBayPointFirst.setWallOneOffset(0.57);
		moduleBayPointFirst.setCenterTwo(false);
		moduleBayPointFirst.setWallTwo(LocationMap.Wall.DECK);
		moduleBayPointFirst.setWallTwoOffset(0.105);
		
		ModuleBayPoint copy = new ModuleBayPoint(moduleBayPointFirst);
		
		assertTrue(moduleBayPointFirst.equals(copy));
	}
	
	@Test
	public void testConstructFromPoint6DofBookmark() {
		Point6Dof p6d = new Point6Dof(1,2,3,4,5,6);
		StationBookmark p6dBookmark = new StationBookmark("Pt6Dof", p6d);
		ModuleBayPoint mbp = new ModuleBayPoint(p6dBookmark);
		
		assertEquals("x wrong", 1, mbp.getX(), EPSILON);
		assertEquals("y wrong", 2, mbp.getY(), EPSILON);
		assertEquals("z wrong", 3, mbp.getZ(), EPSILON);
		assertEquals("roll wrong", 4, mbp.getRoll(), EPSILON);
		assertEquals("pitch wrong", 5, mbp.getPitch(), EPSILON);
		assertEquals("yaw wrong", 6, mbp.getYaw(), EPSILON);
		assertFalse("moduleBayValid should be false", mbp.isModuleBayValid());
		assertEquals(p6dBookmark, mbp.getBookmark());
		assertTrue(mbp.isBookmarkValid());
	}
	
	@Test
	public void testSetModuleBayBookmark() {
		ModuleBayPoint moduleBayPointFirst= new ModuleBayPoint();
		moduleBayPointFirst.setModule(ModuleName.US_LAB);
		moduleBayPointFirst.setBayNumber(BayNumber.SIX);
		moduleBayPointFirst.setCenterOne(false);
		moduleBayPointFirst.setWallOne(LocationMap.Wall.STBD);
		moduleBayPointFirst.setWallOneOffset(0.57);
		moduleBayPointFirst.setCenterTwo(false);
		moduleBayPointFirst.setWallTwo(LocationMap.Wall.DECK);
		moduleBayPointFirst.setWallTwoOffset(0.105);
		
		StationBookmark mbpBookmark = new StationBookmark("mbp", moduleBayPointFirst);
		
		ModuleBayPoint mbp = new ModuleBayPoint();
		mbp.setBookmark(mbpBookmark);
		
		assertEquals("Module X coordinate wrong", lab6x, mbp.getX(), EPSILON);
		assertEquals("Module Y coordinate wrong", lab6y, mbp.getY(),  EPSILON);
		assertEquals("Module Z coordinate wrong", deckPlus105, mbp.getZ(), EPSILON);
		assertTrue("ModuleBayValid should be true", mbp.isModuleBayValid());
		assertTrue("Module should be US Lab", ModuleName.US_LAB.equals(mbp.getModule()));
		assertTrue("BayNumberValid should be true", mbp.isBayNumberValid());
		assertTrue("BayNumber should be six", BayNumber.SIX.equals(mbp.getBayNumber()));
		assertFalse("CenterOne should be false", mbp.isCenterOne());
		assertTrue("WallOne should be Stbd", LocationMap.Wall.STBD.equals(mbp.getWallOne()));
		assertFalse("CenterTwo should be false", mbp.isCenterTwo());
		assertEquals(mbpBookmark, mbp.getBookmark());
		assertTrue(mbp.isBookmarkValid());
	}

	@Test
	public void testSetWallOffsetTwo() {
		ModuleBayPoint mbp = new ModuleBayPoint();
		mbp.setModule(ModuleName.US_LAB);
		mbp.setBayNumber(BayNumber.SIX);
		mbp.setCenterOne(false);
		mbp.setWallOne(LocationMap.Wall.STBD);
		mbp.setWallOneOffset(0.57);
		mbp.setCenterTwo(false);
		mbp.setWallTwo(LocationMap.Wall.DECK);
		mbp.setWallTwoOffset(0.105);
		
		assertEquals("Module X coordinate wrong", lab6x, mbp.getX(), EPSILON);
		assertEquals("Module Y coordinate wrong", lab6y, mbp.getY(),  EPSILON);
		assertEquals("Module Z coordinate wrong", deckPlus105, mbp.getZ(), EPSILON);
		assertTrue("ModuleBayValid should be true", mbp.isModuleBayValid());
		assertTrue("Module should be US Lab", ModuleName.US_LAB.equals(mbp.getModule()));
		assertTrue("BayNumberValid should be true", mbp.isBayNumberValid());
		assertTrue("BayNumber should be six", BayNumber.SIX.equals(mbp.getBayNumber()));
		assertFalse("CenterOne should be false", mbp.isCenterOne());
		assertTrue("WallOne should be Stbd", LocationMap.Wall.STBD.equals(mbp.getWallOne()));
		assertFalse("CenterTwo should be false", mbp.isCenterTwo());
		assertFalse("Bookmark should be invalid", mbp.isBookmarkValid());
	}
	
	@Test
	public void testSetWallTwo() {
		ModuleBayPoint mbp = new ModuleBayPoint();
		mbp.setModule(ModuleName.US_LAB);
		mbp.setBayNumber(BayNumber.SIX);
		mbp.setCenterOne(false);
		mbp.setWallOne(LocationMap.Wall.STBD);
		mbp.setWallOneOffset(0.57);
		mbp.setCenterTwo(false);
		mbp.setWallTwo(LocationMap.Wall.DECK);
		
		assertEquals("Module X coordinate wrong", lab6x, mbp.getX(), EPSILON);
		assertEquals("Module Y coordinate wrong", lab6y, mbp.getY(),  EPSILON);
		assertEquals("Module Z coordinate wrong", deckPlus02, mbp.getZ(), EPSILON);
		assertTrue("ModuleBayValid should be true", mbp.isModuleBayValid());
		assertTrue("Module should be US Lab", ModuleName.US_LAB.equals(mbp.getModule()));
		assertTrue("BayNumberValid should be true", mbp.isBayNumberValid());
		assertTrue("BayNumber should be six", BayNumber.SIX.equals(mbp.getBayNumber()));
		assertFalse("CenterOne should be false", mbp.isCenterOne());
		assertTrue("WallOne should be Stbd", LocationMap.Wall.STBD.equals(mbp.getWallOne()));
		assertFalse("CenterTwo should be false", mbp.isCenterTwo());
		assertFalse("Bookmark should be invalid", mbp.isBookmarkValid());
	}
	
	@Test
	public void testSetCenterTwo() {
		ModuleBayPoint mbp = new ModuleBayPoint();
		mbp.setModule(ModuleName.US_LAB);
		mbp.setBayNumber(BayNumber.SIX);
		mbp.setCenterOne(false);
		mbp.setWallOne(LocationMap.Wall.STBD);
		mbp.setWallOneOffset(0.57);
		mbp.setCenterTwo(false);
		
		assertEquals("Module X coordinate wrong", lab6x, mbp.getX(), EPSILON);
		assertEquals("Module Y coordinate wrong", lab6y, mbp.getY(),  EPSILON);
		assertEquals("Module Z coordinate wrong", deckPlus02, mbp.getZ(), EPSILON);
		assertTrue("ModuleBayValid should be true", mbp.isModuleBayValid());
		assertTrue("Module should be US Lab", ModuleName.US_LAB.equals(mbp.getModule()));
		assertTrue("BayNumberValid should be true", mbp.isBayNumberValid());
		assertTrue("BayNumber should be six", BayNumber.SIX.equals(mbp.getBayNumber()));
		assertFalse("CenterOne should be false", mbp.isCenterOne());
		assertTrue("WallOne should be Stbd", LocationMap.Wall.STBD.equals(mbp.getWallOne()));
		assertFalse("CenterTwo should be false", mbp.isCenterTwo());
		assertFalse("Bookmark should be invalid", mbp.isBookmarkValid());
	}
	
	@Test
	public void testSetWallOffsetOne() {
		ModuleBayPoint mbp = new ModuleBayPoint();
		mbp.setModule(ModuleName.US_LAB);
		mbp.setBayNumber(BayNumber.SIX);
		mbp.setCenterOne(false);
		mbp.setWallOne(LocationMap.Wall.STBD);
		mbp.setWallOneOffset(0.57);
		
		// immediately go to the edge when you select a wall
		assertEquals("Module X coordinate wrong", lab6x, mbp.getX(), EPSILON);
		assertEquals("Module Y coordinate wrong", lab6y, mbp.getY(),  EPSILON);
		assertEquals("Module Z coordinate wrong", centerz, mbp.getZ(), EPSILON);
		assertTrue("ModuleBayValid should be true", mbp.isModuleBayValid());
		assertTrue("Module should be US Lab", ModuleName.US_LAB.equals(mbp.getModule()));
		assertTrue("BayNumberValid should be true", mbp.isBayNumberValid());
		assertTrue("BayNumber should be six", BayNumber.SIX.equals(mbp.getBayNumber()));
		assertFalse("CenterOne should be false", mbp.isCenterOne());
		assertTrue("WallOne should be Stbd", LocationMap.Wall.STBD.equals(mbp.getWallOne()));
		assertFalse("Bookmark should be invalid", mbp.isBookmarkValid());
	}
		
	@Test
	public void testSetWallOne() {
		ModuleBayPoint mbp = new ModuleBayPoint();
		mbp.setModule(ModuleName.US_LAB);
		mbp.setBayNumber(BayNumber.SIX);
		mbp.setCenterOne(false);
		mbp.setWallOne(LocationMap.Wall.STBD);
		
		// immediately go to the edge when you select a wall
		assertEquals("Module X coordinate wrong", lab6x, mbp.getX(), EPSILON);
		assertEquals("Module Y coordinate wrong", labYstbdPlus02, mbp.getY(),  EPSILON);
		assertEquals("Module Z coordinate wrong", centerz, mbp.getZ(), EPSILON);
		assertTrue("ModuleBayValid should be true", mbp.isModuleBayValid());
		assertTrue("Module should be US Lab", ModuleName.US_LAB.equals(mbp.getModule()));
		assertTrue("BayNumberValid should be true", mbp.isBayNumberValid());
		assertTrue("BayNumber should be six", BayNumber.SIX.equals(mbp.getBayNumber()));
		assertFalse("CenterOne should be false", mbp.isCenterOne());
		assertTrue("WallOne should be Stbd", LocationMap.Wall.STBD.equals(mbp.getWallOne()));
		assertFalse("Bookmark should be invalid", mbp.isBookmarkValid());
	}
	
	@Test
	public void testSetCenterOne() {
		ModuleBayPoint mbp = new ModuleBayPoint();
		mbp.setModule(ModuleName.US_LAB);
		mbp.setBayNumber(BayNumber.SIX);
		mbp.setCenterOne(false);
		
		// different from what happens if you enter in the Plan Editor
		assertEquals("Module X coordinate wrong", lab6x, mbp.getX(), EPSILON);
		assertEquals("Module Y coordinate wrong", 0.01, mbp.getY(),  EPSILON);
		assertEquals("Module Z coordinate wrong", deckPlus02, mbp.getZ(), EPSILON);
		assertTrue("ModuleBayValid should be true", mbp.isModuleBayValid());
		assertTrue("Module should be US Lab", ModuleName.US_LAB.equals(mbp.getModule()));
		assertTrue("BayNumberValid should be true", mbp.isBayNumberValid());
		assertTrue("BayNumber should be six", BayNumber.SIX.equals(mbp.getBayNumber()));
		assertFalse("CenterOne should be false", mbp.isCenterOne());
		assertFalse("Bookmark should be invalid", mbp.isBookmarkValid());
	}
	
	@Test
	public void testSetBay() {
		ModuleBayPoint mbp = new ModuleBayPoint();
		mbp.setModule(ModuleName.US_LAB);
		mbp.setBayNumber(BayNumber.SIX);
		assertEquals("Module X coordinate wrong", lab6x, mbp.getX(), EPSILON);
		assertEquals("Module Y coordinate wrong", 0.01, mbp.getY(), EPSILON);
		assertEquals("Module Z coordinate wrong", centerz, mbp.getZ(), EPSILON);
		assertTrue("ModuleBayValid should be true", mbp.isModuleBayValid());
		assertTrue("Module should be US Lab", ModuleName.US_LAB.equals(mbp.getModule()));
		assertTrue("BayNumberValid should be true", mbp.isBayNumberValid());
		assertTrue("BayNumber should be six", BayNumber.SIX.equals(mbp.getBayNumber()));
		assertFalse("Bookmark should be invalid", mbp.isBookmarkValid());
	}
	
	@Test
	public void testSetModule() {
		ModuleBayPoint mbp = new ModuleBayPoint();
		mbp.setModule(ModuleName.JEM);
		
		assertEquals("Module X coordinate wrong", jemCenterx, mbp.getX(), EPSILON);
		assertEquals("Module Y coordinate wrong", jemCentery, mbp.getY(), EPSILON);
		assertEquals("Module Z coordinate wrong", centerz, mbp.getZ(), EPSILON);
		assertTrue("ModuleBayValid should be true", mbp.isModuleBayValid());
		assertTrue("Module should be JEM", ModuleName.JEM.equals(mbp.getModule()));
		assertFalse("BayNumberValid should be false", mbp.isBayNumberValid());
		assertFalse("Bookmark should be invalid", mbp.isBookmarkValid());
	}
}
