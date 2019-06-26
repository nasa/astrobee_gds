package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.Box;

import com.ardor3d.math.Vector3;

/** Simple model of a box that can stand in for the FreeFlyer */
public class FreeFlyerBoxModel extends FreeFlyerBasicModel {
	
	@Override
	protected void init(String color) {
		double shrinkBy = 0.01; // avoid Z-fighting when boxes overlap
		
		normalMaterial = makeMaterialState(hColor[0], hColor[1], hColor[2]);
		myName = color + "FreeFlyer-m_base";
		// make big cube
		base = new Box(myName, new Vector3(),
				halfSideLength - shrinkBy, 
				halfSideLength - shrinkBy, 
				halfSideLength - shrinkBy);
		base.setRenderState(normalMaterial);
		base.updateModelBound();

		attachChild(base);

		alarmMaterial = makeMaterialState(1, (165.0f/255.0f), 0);
	}

}
