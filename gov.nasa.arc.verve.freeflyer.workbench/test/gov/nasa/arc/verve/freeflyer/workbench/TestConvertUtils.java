package gov.nasa.arc.verve.freeflyer.workbench;

import static org.junit.Assert.*;
import gov.nasa.rapid.v2.framestore.ConvertUtils;
import gov.nasa.rapid.v2.framestore.EulerAngles;
import gov.nasa.rapid.v2.framestore.ReadOnlyEulerAngles.Type;

import org.junit.Test;

import com.ardor3d.math.Matrix3;

public class TestConvertUtils {
	double under90 = 1.4;
	double under180 = 2.8;
	double under180again = 3.1;

	@Test
	public void testZYXrSingleRotations() {
		EulerAngles ea1 = new EulerAngles(Type.ZYXr, under180, 0, 0);
		Matrix3 mat = ConvertUtils.toRotationMatrix(ea1, null);
		EulerAngles ea2 = ConvertUtils.toEulerAngles(mat, null);
		assertTrue("Euler Angle corrupted roll", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.ZYXr, 0, under90, 0);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAngles(mat, null);
		assertTrue("Euler Angle corrupted pitch", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.ZYXr, 0, 0, under180again);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAngles(mat, null);
		assertTrue("Euler Angle corrupted yaw", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.ZYXr, -under180, 0, 0);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAngles(mat, null);
		assertTrue("Euler Angle corrupted negative roll", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.ZYXr, 0, -under90, 0);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAngles(mat, null);
		assertTrue("Euler Angle corrupted negative pitch", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.ZYXr, 0, 0, -under180again);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAngles(mat, null);
		assertTrue("Euler Angle corrupted negative yaw", ea1.equals(ea2));
	}

	@Test
	public void testXYZrSingleRotations() {
		EulerAngles ea1 = new EulerAngles(Type.XYZr, under180again, 0, 0);
		Matrix3 mat = ConvertUtils.toRotationMatrix(ea1, null);
		EulerAngles ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted roll", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, 0, under90, 0);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted pitch", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, 0, 0, under180);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted yaw", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, -under180again, 0, 0);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted roll", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, 0, -under90, 0);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted pitch", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, 0, 0, -under180);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted yaw", ea1.equals(ea2));
	}

	@Test
	public void testXYZrDoubleRotations() {
		EulerAngles ea1 = new EulerAngles(Type.XYZr, under180, under90, 0);
		Matrix3 mat = ConvertUtils.toRotationMatrix(ea1, null);
		EulerAngles ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted roll and pitch", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, 0, under90, under180again);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted pitch and yaw", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, under180, 0, under180);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted roll and yaw", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, -under180, under90, 0);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted negative roll and pitch", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, 0, -under90, under180again);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted negative pitch and yaw", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, under180, 0, -under180);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted roll and negative yaw", ea1.equals(ea2));
	}
	
	@Test
	public void testXYZrTripleRotations() {
		EulerAngles ea1 = new EulerAngles(Type.XYZr, under180, under90, under180again);
		Matrix3 mat = ConvertUtils.toRotationMatrix(ea1, null);
		EulerAngles ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted roll, pitch, and yaw", ea1.equals(ea2));

		ea1 = new EulerAngles(Type.XYZr, -under90, under90, under180again);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted roll, pitch, and yaw", ea1.equals(ea2));
		
		ea1 = new EulerAngles(Type.XYZr, under180, under90, -under180again);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted roll, pitch, and yaw", ea1.equals(ea2));
		
		ea1 = new EulerAngles(Type.XYZr, -under180, -under90, under90);
		mat = ConvertUtils.toRotationMatrix(ea1, null);
		ea2 = ConvertUtils.toEulerAnglesXYZr(mat, null);
		assertTrue("Euler Angle corrupted roll, pitch, and yaw", ea1.equals(ea2));
	}
}
