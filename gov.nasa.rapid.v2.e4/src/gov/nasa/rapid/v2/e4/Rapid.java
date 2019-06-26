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
package gov.nasa.rapid.v2.e4;

import gov.nasa.util.ProcessInfo;

import java.util.Random;

public class Rapid {

    public static final String PrimaryParticipant   = "PrimaryParticipant";
    public static final String SecondaryParticipant = "SecondaryParticipant";
    public static final String TertiaryParticipant  = "TertiaryParticipant";
    public static final String QuaternaryParticipant= "QuaternaryParticipant";
    public static final String StaticParticipant    = "StaticParticipant";
    public static final String TestingParticipant   = "TestingParticipant";

    public static String DefaultParticipant = PrimaryParticipant;

    public static final Random random = new Random(System.currentTimeMillis());

    public static final long pidFallback = random.nextInt()%99999;
    
    /**
     * @return name for primary participant in the form of "username@hostname:pid" The call to 
     * obtain the process id may fail, in which case the pid will be a 5 digit random number. 
     */
    public static String defaultParticipantName() {
        return ProcessInfo.username()+"@"+ProcessInfo.hostname()+":"+ProcessInfo.processId(pidFallback);
    }
    

}
