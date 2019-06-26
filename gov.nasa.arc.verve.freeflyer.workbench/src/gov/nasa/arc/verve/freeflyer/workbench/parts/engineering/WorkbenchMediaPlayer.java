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
package gov.nasa.arc.verve.freeflyer.workbench.parts.engineering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class WorkbenchMediaPlayer {


    private static int width = 600;
    private static int height = 400;

  
    private final JPanel videoSurface;
    private boolean stopVideo = false;

    private BufferedImage image;
    private float resizePercentWidth = Float.MIN_VALUE;
    private float resizePercentHeight = Float.MIN_VALUE;

    private final DirectMediaPlayerComponent mediaPlayerComponent;

    public WorkbenchMediaPlayer(final int width,final int height) {
       // frame = new JFrame("Direct Media Player");
    	WorkbenchMediaPlayer.width = width;
        WorkbenchMediaPlayer.height = height;
        videoSurface = new VideoSurfacePanel();
        image = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice()
            .getDefaultConfiguration()
            .createCompatibleImage(WorkbenchMediaPlayer.width , WorkbenchMediaPlayer.height);
       
        final BufferFormatCallback bufferFormatCallback = new BufferFormatCallback() {
            @Override
            public BufferFormat getBufferFormat(final int sourceWidth, final int sourceHeight) {
                return new RV32BufferFormat(WorkbenchMediaPlayer.width , WorkbenchMediaPlayer.height);
            }
        };
        mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
            @Override
            protected RenderCallback onGetRenderCallback() {
                return new MacRenderCallbackAdapter();
            }
        }; 
    }

    public DirectMediaPlayer getMediaPlayer(){
    	return mediaPlayerComponent.getMediaPlayer();
    }
    
    public void stopStream(){
    	stopVideo = true;
    	mediaPlayerComponent.getMediaPlayer().stop();
    	mediaPlayerComponent.getMediaPlayer().release();
    }
    
    public JPanel getVideoSurface(){
    	return videoSurface;
    }
    
    private class VideoSurfacePanel extends JPanel {

        private VideoSurfacePanel() {
            setBackground(Color.black);
            setOpaque(true);
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
        }

        @Override
        protected void paintComponent(final Graphics g) {
            final Graphics2D g2 = (Graphics2D)g; 
            
            //resize the video to fit within the componet
            if(resizePercentWidth != Float.MIN_VALUE && resizePercentHeight != Float.MIN_VALUE){
            	BufferedImage after = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	            final AffineTransform at = new AffineTransform();
	            at.scale(resizePercentWidth,resizePercentHeight);
	            final AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
	            after = scaleOp.filter(image, after);
            	g2.drawImage(after, null, 0, 0);
            }else
            	g2.drawImage(image, null, 0, 0);
        }
    }

    private class MacRenderCallbackAdapter extends RenderCallbackAdapter {

        private MacRenderCallbackAdapter() {
            super(new int[width * height]);
        }
        
        @Override
        protected void onDisplay(final DirectMediaPlayer mediaPlayer, final int[] rgbBuffer) {
        	// Simply copy buffer to the image and repaint
        	if(stopVideo){
        		final int[] blackScreen = new int[rgbBuffer.length];
        		image.setRGB(0, 0, width, height, blackScreen, 0, width);
        	}
        		
        	else
        		image.setRGB(0, 0, width, height, rgbBuffer, 0, width);
            videoSurface.repaint();
        }
    }

	public void resize(final float newWidth, final float newHeight) {
		resizePercentWidth = 1.0f-((float)width-newWidth)/(float)width;
		resizePercentHeight = 1.0f-((float)height-newHeight)/(float)height;
	}
}
