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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ardor3d.framework.FrameHandler;
import com.ardor3d.util.Timer;

/**
 * Very simple render update 
 */
public class RenderUpdateThread extends Thread {
    static Logger logger = Logger.getLogger(RenderUpdateThread.class);	

    private final FrameHandler frameHandler;
    private final Timer timer;
    private final RenderRunnable runnable = new RenderRunnable();
    public AtomicBoolean keepGoing = new AtomicBoolean(true);
    private double fps = 30;
    //private double fps = 15;
    private double throttle = 1;
    private int    sleepTime = (int)(1000/fps);
    private Display m_display;
    
    public RenderUpdateThread(Display display, FrameHandler frameWork, Timer timer) {
        super("RenderUpdateThread");
        this.frameHandler = frameWork;
        this.timer = timer;
        m_display = display;
    }

    @Override
    public void run() {
    	Display display = m_display;// = m_shell.getDisplay();
        while(m_display == null) {
            try { Thread.sleep(5000); } 
            catch (InterruptedException e) { /**/ }
            display = m_display;
            
            logger.info("Waiting for workbench to materialize...");
        }

        waitUntilReady(3, display);
        
        double currentFps = fps;
        double    lastFps = fps;
        long lastFpsChange = System.currentTimeMillis();
        long currentTime = lastFpsChange;
        
        while(keepGoing.get() && !display.isDisposed()) {
        	//System.out.println("Doing good things ...");
            currentTime = System.currentTimeMillis();
            // add some basic frame throttling to prevent ui event starvation
            // change fps once a second at most
            if(lastFpsChange < currentTime-1000) {
                int frameTime = runnable.getFrameTime();
                if(frameTime > sleepTime) {
                    throttle += 0.5;
                }
                else if(frameTime < (sleepTime-10)/1.5) {
                    throttle = 1;
                }
                currentFps = fps / throttle;
                sleepTime = (int)(1000/currentFps);
                lastFpsChange = currentTime;
                if(lastFps != currentFps) {
                    //logger.debug("framerate changed, now targeting "+currentFps);
                }
                lastFps = currentFps;
            }
            display.asyncExec(runnable.push());
            try { Thread.sleep(sleepTime); } 
            catch (final InterruptedException e) {
                logger.warn(this.getClass().getSimpleName()+" interrupted.");
            }
        }
    }

    // render some '0 time' frames before starting
    void waitUntilReady(int delayFrames, Display display) {
        timer.reset();
        for(int i = 0; i < delayFrames; i++) {
            display.asyncExec(runnable.push());
            while(runnable.getCount() != 0) {
                try { Thread.sleep(100); } 
                catch (InterruptedException e) { /**/ }
            }
            timer.reset();
        }
    }

    //------------------------------------------------
    public class RenderRunnable implements Runnable {
        int  count = 0;
        long startTime;
        long frameTime;
        double running = 50;
        AtomicInteger runningFameTime = new AtomicInteger();

        public RenderRunnable() {
        }
        
        @Override
        public synchronized void run() {
            if(count == 1) {
                try{
                    startTime = System.currentTimeMillis();
                    frameHandler.updateFrame();
                    frameTime = System.currentTimeMillis()-startTime;
                    running = 0.2*frameTime + 0.8*running;
                    runningFameTime.set((int)running);
                }
                catch(Throwable t) {
                    keepGoing.set(false);
                    t.printStackTrace();
                }
            }
            if(count > 0) {
                --count;
            }
        }
        public synchronized Runnable push() {
            count++;
            return this;
        }
        public synchronized int getCount() {
            return count;
        }
        public int getFrameTime() {
            return runningFameTime.get();
        }
    }

}
