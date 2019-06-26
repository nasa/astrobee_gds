/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.ardor3d.e4.framework;

import gov.nasa.arc.verve.common.SceneHack;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.Scene;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Node;

/**
 * 
 * @author mallan
 *
 */
public class OffscreenVerveScene implements Scene {
	protected final DisplaySettings      m_settings;
	protected final VerveLwjglHeadlessCanvas m_canvas;
	protected final Node      			m_root;
	protected final BufferedImage 		m_image;
	protected final int[] 				tmpData;


	public OffscreenVerveScene(Node root, int width, int height) {
		this(root, width, height, ColorRGBA.BLACK_NO_ALPHA);
	}
	
	public OffscreenVerveScene(Node root, int width, int height, ReadOnlyColorRGBA backgroundColor) {
		//m_settings = new DisplaySettings(width, height, 0, 0, false);
		CanvasRenderer shareCanvasRenderer = SceneHack.getShareCanvasRenderer();
		m_settings = new DisplaySettings(width, height, 0, 0, 0, 8, 0, 0, false, false, shareCanvasRenderer);

		m_canvas = new VerveLwjglHeadlessCanvas(m_settings, this);
		m_canvas.getRenderer().setBackgroundColor(backgroundColor);

		m_image  = new BufferedImage(m_settings.getWidth(), m_settings.getHeight(), BufferedImage.TYPE_INT_ARGB);
		tmpData = ((DataBufferInt)m_image.getRaster().getDataBuffer()).getData();

		m_root = root;

		//setCameraTransform(null);
	}

	public void setCameraTransform(Transform xfm) {
		Camera camera = m_canvas.getCamera();
		if(xfm == null) {
			camera.setLocation(new Vector3(-5,0,5));
			camera.lookAt(0,0,0, Vector3.UNIT_Z);
		}
		else {
			Vector3 up   = new Vector3();
			Vector3 left = new Vector3();
			Vector3 look = new Vector3();
			Matrix3 rot  = new Matrix3();
			rot.set(xfm.getMatrix());
			rot.getColumn(0, look);
			rot.getColumn(1, left);
			rot.getColumn(2, up);
			camera.setAxes(left, up, look);
			camera.setLocation(xfm.getTranslation());
		}
		camera.update();
	}

	public void renderScene(String filename) {		
		m_root.updateGeometricState(0, true);
		m_root.updateWorldRenderStates(true);
		m_canvas.draw();

		final IntBuffer data = m_canvas.getDataBuffer();

		final int width  = m_settings.getWidth();
		final int height = m_settings.getHeight();

		for (int x = height; --x >= 0;) {
			data.get(tmpData, x * width, width);
		}
		final File out = new File(System.getProperty("user.home"), filename);

		try {
			ImageIO.write(m_image, "png", out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    @Override
	public boolean renderUnto(Renderer renderer) {
		renderer.draw(m_root);
		return true;
	}

    @Override
	public PickResults doPick(Ray3 pickRay) {
		// nothing to do here
		return null;
	}

	public void setBackgroundColor(ReadOnlyColorRGBA backgroundColor) {
		m_canvas.getRenderer().setBackgroundColor(backgroundColor);
	}
	
	public ReadOnlyColorRGBA getBackgroundColor() {
		return m_canvas.getRenderer().getBackgroundColor();
	}
}
