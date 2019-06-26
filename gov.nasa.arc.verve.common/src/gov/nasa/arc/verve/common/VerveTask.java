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
package gov.nasa.arc.verve.common;

import gov.nasa.arc.verve.common.util.BlockingLifoQueue;
import gov.nasa.util.NamedThreadFactory;
import gov.nasa.util.ThreadUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ardor3d.util.GameTaskQueue;

/**
 * Task manager for events that must be performed in the render thread or in 
 * a thread pool. 
 */
public class VerveTask {
    private static final GameTaskQueue   s_queue  = new GameTaskQueue();
    private static final GameTaskQueue   s_rQueue = new GameTaskQueue();
    private static final ExecutorService s_single = Executors.newSingleThreadExecutor(new NamedThreadFactory("VerveTask:Single"));

    private static final ExecutorService s_pool   = new ThreadPoolExecutor( 3,    // core pool size
                                                                           30,    // max pool size
                                                                           60L,   // idle thread timeout
                                                                           TimeUnit.SECONDS,
                                                                           new ArrayBlockingQueue<Runnable>(1000),
                                                                           new NamedThreadFactory("VerveTask:Pool"));

    private static final ExecutorService s_lifo   = new ThreadPoolExecutor(1, 
                                                                           1,
                                                                           0L, TimeUnit.MILLISECONDS,
                                                                           new BlockingLifoQueue<Runnable>(),
                                                                           new NamedThreadFactory("VerveTask:LIFO"));
    static {
        ((ThreadPoolExecutor)s_pool).allowCoreThreadTimeOut(true);
    }

    /**
     * execute task in update() (for scenegraph operations)
     */
    public static <V> Future<V> asyncExec(final Callable<V> callable) {
        return s_queue.enqueue(callable);
    }
    public static GameTaskQueue getQueue() {
        return s_queue;
    }

    /**
     * execute task in update() (for scenegraph operations) after waiting
     * for next frame, then a delay of n milliseconds
     * @param <V>
     * @param <V>
     */
    public static <V> void asyncExecDelayed(final long milliseconds, final Callable<V> callable) {
        final Callable<Void> nextFrame = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                final Callable<Void> background = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ThreadUtils.sleep(milliseconds);
                        VerveTask.asyncExec(callable);
                        return null;
                    }
                };
                VerveTask.backgroundExec(background);
                return null;
            }
        };
        s_queue.enqueue(nextFrame);
    }

    /**
     * Execute a task in single threaded queue. Do not use this if you need 
     * the task to execute immediately; the purpose of this queue is to throttle
     * background events, so it is perfectly legit for the tasks to call 
     * Thread.sleep(). 
     */
    public static <V> Future<V> serialExec(final Callable<V> callable) {
        return s_single.submit(callable);
    }

    /**
     * Execute a task in single threaded lifo queue. Do not use this if you need 
     * the task to execute immediately; the purpose of this queue is to throttle
     * background events, so it is perfectly legit for the tasks to call 
     * Thread.sleep(). 
     */
    public static <V> Future<V> serialLifoExec(final Callable<V> callable) {
        return s_lifo.submit(callable);
    }

    /**
     * Execute a task in a cached thread pool. 
     */
    public static <V> Future<V> backgroundExec(final Callable<V> callable) {
        return s_pool.submit(callable);
    }

    /**
     * Execute a task in a cached thread pool, <i>after</i> waiting for the
     * next frame
     */
    public static <V> Future<V> asyncBackgroundExec(final Callable<V> callable) {
        final Callable<V> wrap = new Callable<V>() {
            @Override
            public V call() throws Exception {
                VerveTask.backgroundExec(callable);
                return null;
            }
        };
        return s_queue.enqueue(wrap);
    }

    /**
     * Execute a task in a single thread, <i>after</i> waiting for the
     * next frame
     */
    public static <V> Future<V> asyncSerialExec(final Callable<V> callable) {
        final Callable<V> wrap = new Callable<V>() {
            @Override
            public V call() throws Exception {
                VerveTask.serialExec(callable);
                return null;
            }
        };
        return s_queue.enqueue(wrap);
    }

    /**
     * execute task during a draw() call so that a Renderer is active and available. 
     * NOTE: This queue should only be used if absolutely necessary. 
     * @param <V>
     * @param callable
     * @return
     */
    public static <V> Future<V> asyncRenderExec(final Callable<V> callable) {
        return s_rQueue.enqueue(callable);
    }
    public static GameTaskQueue getRenderQueue() {
        return s_rQueue;
    }


}
