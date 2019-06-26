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
package gov.nasa.rapid.v2.e4.util;

import java.io.Serializable;

import com.rti.dds.infrastructure.Time_t;

/**
 * The RTI Time_t class does not provide a default ctor, which is 
 * super awesome. So we have to create a compatibility class for 
 * cases when we want to do things like serialize time. I mean,like 
 * who would ever want to deserialize a time class?
 * @author mallan
 */
public class TimeType implements Serializable, Comparable {
    public int nanosec;
    public int sec;

    public TimeType() {
    }

    public TimeType(int sec, int nanosec) {
        this.sec = sec;
        this.nanosec = nanosec;
    }

    public TimeType(TimeType time) {
        nanosec = time.nanosec;
        sec     = time.sec;
    }

    public TimeType(Time_t time) {
        nanosec = time.nanosec;
        sec     = time.sec;
    }

    public TimeType(long msec) {
        set(msec);
    }

    public TimeType set(Time_t time) {
        sec = time.sec;
        nanosec = time.nanosec;
        return this;
    }

    public TimeType set(TimeType time) {
        sec = time.sec;
        nanosec = time.nanosec;
        return this;
    }

    public TimeType set(long msec) {
        sec     = (int)(msec/1000);
        nanosec = (int)(msec%1000 * 1000000);
        return this;
    }

    public Time_t get() {
        return new Time_t(sec, nanosec);
    }

    public long msec() {
        return ((long)sec*1000) + (nanosec /1000000);
    }

    @Override
    public String toString() {
        return String.format("%d.%09d", sec, nanosec);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TimeType) {
            TimeType that = (TimeType)obj;
            return (this.sec==that.sec && this.nanosec==that.nanosec);   
        }
        if(obj instanceof Time_t) {
            Time_t that = (Time_t)obj;
            return (this.sec==that.sec && this.nanosec==that.nanosec);   
        }
        return false;
    }

    @Override
    public int hashCode() {
        assert false : "no hash";
    return 42;
    }

    @Override
    public int compareTo(Object obj) {
        if(obj instanceof TimeType) {
            TimeType that = (TimeType)obj;
            if(this.sec == that.sec) {
                return this.nanosec-that.nanosec;
            }
            else {
                return this.sec-that.sec;
            }
        }
        if(obj instanceof Time_t) {
            Time_t that = (Time_t)obj;
            if(this.sec == that.sec) {
                return this.nanosec-that.nanosec;
            }
            else {
                return this.sec-that.sec;
            }
        }
        return 0;
    }
}
