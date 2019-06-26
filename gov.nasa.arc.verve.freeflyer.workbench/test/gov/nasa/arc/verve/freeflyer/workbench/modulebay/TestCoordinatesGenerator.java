package gov.nasa.arc.verve.freeflyer.workbench.modulebay;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.plan.model.modulebay.BayNumber;
import gov.nasa.arc.irg.plan.model.modulebay.CoordinatesGenerator;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.model.modulebay.Module.ModuleName;
import gov.nasa.arc.irg.plan.model.modulebay.Point3D;

import org.junit.Test;

public class TestCoordinatesGenerator {
	
	@Test
	public void testGetCoordinatesFromTwoWalls() {
		Point3D expected = new Point3D(11.19, 1.28, 5.86);
		Point3D computed = CoordinatesGenerator.getCoordinates(
				ModuleName.NODE2, 
				BayNumber.TWO, 
				LocationMap.Wall.STBD,
				0.5,
				LocationMap.Wall.DECK,
				0.7);
		
		assertTrue("Two wall offset coordinate wrong", expected.equals(computed));
	}
	
	@Test
	public void testGetCoordinatesFromWall() {
		Point3D expectedSplit = new Point3D(10.94, -7.2, 4.48);
		Point3D computedSplit = CoordinatesGenerator.getCoordinates(
				ModuleName.JEM, 
				BayNumber.BETWEEN_FOUR_FIVE, 
				LocationMap.Wall.OVHD,
				0.7);
		assertTrue("One wall offset split coordinate wrong", expectedSplit.equals(computedSplit));

		Point3D expected = new Point3D(10.16, -10.51, 4.85);
		Point3D computed = CoordinatesGenerator.getCoordinates(
				ModuleName.JEM, 
				BayNumber.SEVEN, 
				LocationMap.Wall.AFT,
				0.3);

		assertTrue("One wall offset coordinate wrong", expected.equals(computed));
		
			}
	
	@Test
	public void testGetCoordinates() {
		Point3D expected = new Point3D(10.93, 5.58, 4.85);
		
		Point3D received = CoordinatesGenerator.getCoordinates(ModuleName.COLUMBUS,
															BayNumber.THREE);
		assertTrue("Bay center coordinate wrong", expected.equals(received));
		
		Point3D expectedSplit = Point3D.getMidpoint(new Point3D(-4.46, -6.66, 4.84),
													new Point3D(-4.46, -5.56, 4.84));
		
		Point3D receivedSplit = CoordinatesGenerator.getCoordinates(ModuleName.NODE3,
															BayNumber.BETWEEN_TWO_THREE);
		assertTrue("Bay split center coordinate wrong", expectedSplit.equals(receivedSplit));
	}
	
	@Test
	public void testMovePointFromWall() {
		Point3D startPoint = new Point3D(0,0,0);
		
		Point3D expectedAft  = new Point3D(-1,  0,  0);
		Point3D expectedFwd  = new Point3D( 1,  0,  0);
		Point3D expectedPort = new Point3D( 0, -1,  0);
		Point3D expectedStbd = new Point3D( 0,  1,  0);
		Point3D expectedDeck = new Point3D( 0,  0,  1);
		Point3D expectedOvhd = new Point3D( 0,  0, -1);	
		
		Point3D computedAft = CoordinatesGenerator.movePointFromWall(startPoint,
																  LocationMap.Wall.AFT,
																  1.0, 2.0);
		
		assertTrue("Move point from aft wall wrong", expectedAft.equals(computedAft));
		
		Point3D computedFwd = CoordinatesGenerator.movePointFromWall(startPoint,
				  LocationMap.Wall.FWD,
				  1.0, 2.0);
		assertTrue("Move point from aft wall wrong", expectedFwd.equals(computedFwd));
		
		Point3D computedPort = CoordinatesGenerator.movePointFromWall(startPoint,
				  LocationMap.Wall.PORT,
				  1.0, 2.0);
		assertTrue("Move point from aft wall wrong", expectedPort.equals(computedPort));
		
		Point3D computedStbd = CoordinatesGenerator.movePointFromWall(startPoint,
				  LocationMap.Wall.STBD,
				  1.0, 2.0);
		assertTrue("Move point from aft wall wrong", expectedStbd.equals(computedStbd));
		
		Point3D computedDeck = CoordinatesGenerator.movePointFromWall(startPoint,
				  LocationMap.Wall.DECK,
				  1.0, 2.0);
		assertTrue("Move point from aft wall wrong", expectedDeck.equals(computedDeck));
		
		Point3D computedOvhd = CoordinatesGenerator.movePointFromWall(startPoint,
				  LocationMap.Wall.OVHD,
				  1.0, 2.0);
		assertTrue("Move point from aft wall wrong", expectedOvhd.equals(computedOvhd));
	}
}
