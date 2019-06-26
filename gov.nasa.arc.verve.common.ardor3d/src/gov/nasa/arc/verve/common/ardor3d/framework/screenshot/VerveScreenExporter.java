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
package gov.nasa.arc.verve.common.ardor3d.framework.screenshot;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.ardor3d.image.ImageDataFormat;
import com.ardor3d.image.PixelDataType;
import com.ardor3d.image.util.ImageUtils;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.util.geom.BufferUtils;

/**
 * Writes screen grabs in separate thread for better performance. 
 * Can easily blow out memory, though... 
 * @author mallan
 *
 */
public class VerveScreenExporter {
    private static Logger logger = Logger.getLogger(VerveScreenExporter.class);
    private static final VerveScreenExporter s_instance = new VerveScreenExporter();
    protected final ExecutorService m_threadPool = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
    protected ArrayDeque<ByteBuffer> m_bufferPool = new ArrayDeque<ByteBuffer>();

    public synchronized static void exportCurrentScreen(final Renderer renderer, final ScreenShotImageExporter exportable) {
        s_instance.queueScreenBuffer(renderer, exportable);
    }

    public void queueScreenBuffer(final Renderer renderer, final ScreenShotImageExporter exportable) {
        try {
            final ImageDataFormat format = exportable.getFormat();
            final Camera camera = Camera.getCurrentCamera();
            final int width = camera.getWidth(), height = camera.getHeight();
            final int size = width * height * ImageUtils.getPixelByteSize(format, PixelDataType.UnsignedByte);
            // FIXME: Create a pool of buffers that can be reused once they have been written to disk
            ByteBuffer _scratch;
            synchronized(m_bufferPool) {
                _scratch = m_bufferPool.pollFirst();
            }
            if(_scratch == null) {
                _scratch = BufferUtils.createByteBuffer(size);
            }
            renderer.grabScreenContents(_scratch, format, 0, 0, width, height);
            m_threadPool.submit(new ScreenWriterTask(_scratch, exportable, width, height));

        } catch (Throwable t) {
            logger.error("Screen capture error: "+t.getClass().getSimpleName()+":"+t.getMessage());// (buffer size is "+m_bufferQueue.size()+")", t);
            System.runFinalization();
            System.gc();
            System.runFinalization();
        }
    }

    public class ScreenWriterTask implements Runnable {
        public final ByteBuffer buffer;
        public final ScreenShotImageExporter exporter;
        public final int width;
        public final int height;

        public ScreenWriterTask(ByteBuffer buffer, ScreenShotImageExporter exporter, int width, int height) {
            this.buffer = buffer;
            this.exporter = exporter;
            this.width = width;
            this.height = height;
        }

        @Override
        public void run() {
            //long start = System.currentTimeMillis();
            exporter.export(buffer, width, height);
            synchronized(m_bufferPool) {
                buffer.rewind();
                m_bufferPool.add(buffer);
            }
            //long total = System.currentTimeMillis() - start;
            //logger.debug(total + " msecs to write "+sd.exporter.getLastFile().toString());
        }
    }
}
