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
package gov.nasa.dds.system;

import gov.nasa.util.NamedThreadFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author mallan
 *
 */
public class DdsTask {
    private static ExecutorService s_admin = Executors.newSingleThreadExecutor(new NamedThreadFactory("DdsAdmin"));
    private static ExecutorService s_dispatch = new ThreadPoolExecutor(5,     // core pool size
                                                                       50,    // max pool size
                                                                       60,    // idle thread timeout
                                                                       TimeUnit.SECONDS,
                                                                       new ArrayBlockingQueue<Runnable>(2500),
                                                                       new NamedThreadFactory("DdsDispatch"));

    /**
     * execute a task in the admin thread. 
     */
    public static <V> Future<V> adminExec(Callable<V> callable) {
        return s_admin.submit(callable);
    }
    
    /**
     * execute a task in the admin thread. 
     */
    public static void adminExec(Runnable runnable) {
        s_admin.submit(runnable);
    }
    
    /**
     * fixed size thread pool for dispatching dds message events
     */
    public static <V> Future<V> dispatchExec(Callable<V> callable) {
        return s_dispatch.submit(callable);
    }
    
    /**
     * fixed size thread pool for dispatching dds message events
     */
    public static void dispatchExec(Runnable runnable) {
        s_dispatch.submit(runnable);
    }
    
}
