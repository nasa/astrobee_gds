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
package gov.nasa.rapid.v2.e4.message.collector;

import gov.nasa.dds.rti.util.TypeSupportUtil;

import org.apache.log4j.Logger;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.topic.TypeSupportImpl;

public class SizeAccumulator {
    private static final Logger logger = Logger.getLogger(SizeAccumulator.class);
    long min, max, count;
    long sampleMax;
    double total;
    //double ave;
    TypeSupportImpl typeSupport = null;
    
    final double mix = 0.3;
    final double old = 1.0-mix;

    public SizeAccumulator(Object object) {
        try {
            Copyable copyable = (Copyable)object;
            typeSupport = TypeSupportUtil.getTypeSupportImpl(copyable.getClass());
            sampleMax = typeSupport.get_serialized_sample_max_size(null, false, (short)0, 0);
            long size = typeSupport.get_serialized_sample_size(null, false, (short)0, 0, copyable);
            min = max = size;
            //ave = size;
            total = size;
            count = 1;
        }
        catch(Throwable t) {
            logger.warn(t);
        }
    }

    public void add(Object object) {
        long size = typeSupport.get_serialized_sample_size(null, false, (short)0, 0, object);
        if(size < min) min = size;
        if(size > max) max = size;
        total += size;
        //ave = old*ave + mix*size;
        count++;
    }

    /** maximum possible size of sample */
    public long getSampleMax() {
        return sampleMax;
    }
    
    public long getMin() {
        return min;
    }
    public long getMax() {
        return max;
    }
    public long getAverage() {
        return Math.round(total/count);
        //return Math.round(ave);
    }
    public long getCount() {
        return count;
    }
    public double getTotal() {
    	return total;
    }
}
