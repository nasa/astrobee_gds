package gov.nasa.arc.verve.rcp.e4.canvas;

import gov.nasa.arc.verve.common.IVerveScene;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.lwjgl.LWJGLException;

import com.ardor3d.framework.lwjgl.LwjglCanvasCallback;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.renderer.TextureRendererFactory;
import com.ardor3d.renderer.lwjgl.LwjglTextureRendererProvider;

@SuppressWarnings("unused")
public abstract class AbstractArdor3dViewLwjgl  extends AbstractArdor3dView {
	  @Override
	    public void createPartControl(Composite parent) {
	//	  super.createPartControl(parent);
	        IVerveScene scene = getScene(getSceneName());
	        if(s_textureRenderProvider == null) {
	            s_textureRenderProvider = new LwjglTextureRendererProvider();
	            TextureRendererFactory.INSTANCE.setProvider(s_textureRenderProvider);
	        }
	        LwjglCanvasRenderer canvasRenderer = new LwjglCanvasRenderer(scene);

	        // uuhhh...?
	        canvasRenderer.setCanvasCallback(new LwjglCanvasCallback() {
	            @Override
	            public void makeCurrent() throws LWJGLException {
	                //Display.makeCurrent();
	            }
	            
				@Override
	            public void releaseContext() throws LWJGLException {
	                //Display.releaseContext();
	            }
	        });
	        parent.setData("gov.nasa.arc.irg.iss.ui.widget.key", "verveViewer");
	        createPartControl(parent, scene, canvasRenderer);
	    }
}
