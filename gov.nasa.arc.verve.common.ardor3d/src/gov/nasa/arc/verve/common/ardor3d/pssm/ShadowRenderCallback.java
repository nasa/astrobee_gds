/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package gov.nasa.arc.verve.common.ardor3d.pssm;

import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;

public interface ShadowRenderCallback {

    void onRender(int splitIndex, Renderer renderer, ParallelSplitShadowMapPass pass, Camera renderCamera);

}
