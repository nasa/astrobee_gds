package gov.nasa.arc.verve.freeflyer.workbench.modulebay;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.plan.model.modulebay.BayNumber;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.model.modulebay.Module.ModuleName;

import org.junit.Test;

public class TestLocationMap {
	final double EPSILON = 0.0001;
	
	@Test
	public void testValidateBayNumber() {
		boolean shouldBeFalse = LocationMap.getInstance().validateBayNumber(ModuleName.NODE1, 
				BayNumber.BETWEEN_SEVEN_EIGHT);

		assertFalse("Bay should be invalid", shouldBeFalse);
		
		shouldBeFalse = LocationMap.getInstance().validateBayNumber(ModuleName.NODE3, 
				BayNumber.SEVEN);

		assertFalse("Bay should be invalid", shouldBeFalse);

		boolean shouldBeTrue = LocationMap.getInstance().validateBayNumber(ModuleName.JEM, 
				BayNumber.BETWEEN_SIX_SEVEN);
		assertTrue("Bay should be valid", shouldBeTrue);

		shouldBeTrue = LocationMap.getInstance().validateBayNumber(ModuleName.COLUMBUS, 
				BayNumber.ZERO);
		assertTrue("Bay should be valid", shouldBeTrue);
	}

	@Test
	public void testFindGreatestDistanceToWall() {
		double notSplit = LocationMap.getInstance().findGreatestDistanceToWall(ModuleName.US_LAB,
				BayNumber.FIVE, LocationMap.Wall.STBD);
		assertEquals("Wrong distance to US Lab wall", 1.07870, notSplit, EPSILON);

		double split = LocationMap.getInstance().findGreatestDistanceToWall(ModuleName.NODE1,
				BayNumber.BETWEEN_ZERO_ONE, LocationMap.Wall.OVHD);

		assertEquals("Wrong distance to Node 1 wall",  1.07628, split, EPSILON);
	}
}
