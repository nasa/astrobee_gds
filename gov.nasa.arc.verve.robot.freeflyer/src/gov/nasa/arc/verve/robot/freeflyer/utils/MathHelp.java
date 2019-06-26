package gov.nasa.arc.verve.robot.freeflyer.utils;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;

public class MathHelp {

	public static Vector3 findZYXEulerAngles(Quaternion q) 
	{
		// roll (x-axis rotation)
		double sinr_cosp = +2.0 * (q.getW() * q.getX() + q.getY() * q.getZ());
		double cosr_cosp = +1.0 - 2.0 * (q.getX() * q.getX() + q.getY() * q.getY());
		double roll = Math.atan2(sinr_cosp, cosr_cosp);

		// pitch (y-axis rotation)
		double sinp = +2.0 * (q.getW() * q.getY() - q.getZ() * q.getX());
		double pitch;
		if (Math.abs(sinp) >= 1)
			pitch = Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
		else
			pitch = Math.asin(sinp);

		// yaw (z-axis rotation)
		double siny_cosp = +2.0 * (q.getW() * q.getZ() + q.getX() * q.getY());
		double cosy_cosp = +1.0 - 2.0 * (q.getY() * q.getY() + q.getZ() * q.getZ());  
		double yaw = Math.atan2(siny_cosp, cosy_cosp);
		
		return new Vector3(roll, pitch, yaw);
	}
	

	public static Vector3 findZYXEulerAngles(ReadOnlyMatrix3 m33) {
		double sy = Math.sqrt(m33.getM00() * m33.getM00() +  m33.getM10() * m33.getM10());

		boolean singular = sy < 1e-6; // If

		double x, y, z;
		if (!singular)
		{
			x = Math.atan2(m33.getM21(), m33.getM22());
			y = Math.atan2(-m33.getM20(), sy);
			z = Math.atan2(m33.getM10(), m33.getM00());
		}
		else
		{
			x = Math.atan2(-m33.getM12(), -m33.getM11());
			y = Math.atan2(-m33.getM20(), sy);
			z = 0;
		}
		return new Vector3(x, y, z);
	}


	public static boolean anglesAreDifferent(int degrees, double radians) {
		double deg2 = radians * 180.0 / Math.PI;
		if(Math.abs(degrees - deg2) > 3) {
			return true;
		}
		return false;
	}
}
