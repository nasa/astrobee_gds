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
package gov.nasa.arc.verve.freeflyer.workbench.scenario;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.AxisLines;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;

public class CoordinateFrame extends Node {
	double m_epsilon = 0.0000001;
	protected AxisLines m_cfaxes;
	protected float m_size = 0.75f;

	protected double _fontScale = 1.0;
	protected final Vector3 _look = new Vector3();
	protected final Vector3 _left = new Vector3();
	protected final Matrix3 _rot = new Matrix3();
	protected float labelsize = 15;

	protected CoordinateFrameImage m_cfBlue, m_cfRed, m_cfGreen;

	public CoordinateFrame() {
		super("Coordinate Frame");
		m_cfaxes = new AxisLines("Axes", m_size);

		attachChild(m_cfaxes);
		m_cfaxes.getSceneHints().setCullHint(CullHint.Never);

		m_cfBlue = new CoordinateFrameImage("uplabel",
				"resources/z.png",
				labelsize);
		attachChild(m_cfBlue);
		m_cfRed = new CoordinateFrameImage("fwdlabel",
				"resources/x.png",
				labelsize);
		attachChild(m_cfRed);
		m_cfGreen = new CoordinateFrameImage("portlabel",
				"resources/y.png",
				labelsize);
		attachChild(m_cfGreen);
	}

	@Override
	public void draw(final Renderer r) {
		final Camera cam = Camera.getCurrentCamera();
		double fifty = 50;
		double thirty = 30;
		double w = thirty;
		double h = thirty;
		Vector2 screenPos = new Vector2(w, h);
		Vector2 screenPosDiffHeight = new Vector2(w, 0);
		double zDepth = 0.1;

		Vector3 worldCoords = cam.getWorldCoordinates(screenPos, zDepth);
		Vector3 worldCoordsDiffHeight = cam.getWorldCoordinates(screenPosDiffHeight, zDepth);
		
		Vector2 locVec = new Vector2(fifty, fifty);
		Vector3 loc = cam.getWorldCoordinates(locVec, zDepth);
		m_size = (float)(worldCoords.distance(worldCoordsDiffHeight));
		m_cfaxes.setSize(m_size);
		m_cfaxes.setWorldTranslation(loc);

		double bigger_msize = m_size*1.3;

		// so if the axes are there, I need to know where the ends of the axes are in pixel coordinates
		// can add m_size to x, y, z in turn to get world coords, then will need to translate to image space
		// top coordinate
		Vector3 zCoord = new Vector3(loc.getX(), loc.getY(), loc.getZ()+bigger_msize);
		Vector3 zScreenCoord = cam.getScreenCoordinates(zCoord);
		m_cfBlue.setAxisOrigin(zScreenCoord);

		Vector3 xCoord = new Vector3(loc.getX()+bigger_msize, loc.getY(), loc.getZ());
		Vector3 xScreenCoord = cam.getScreenCoordinates(xCoord);
		m_cfRed.setAxisOrigin(xScreenCoord);

		Vector3 yCoord = new Vector3(loc.getX(), loc.getY()+bigger_msize, loc.getZ());
		Vector3 yScreenCoord = cam.getScreenCoordinates(yCoord);
		m_cfGreen.setAxisOrigin(yScreenCoord);
		
		super.draw(r);
	}
}
