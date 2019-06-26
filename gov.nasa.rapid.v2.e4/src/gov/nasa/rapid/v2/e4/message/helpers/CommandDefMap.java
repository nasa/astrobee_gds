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
package gov.nasa.rapid.v2.e4.message.helpers;

import java.util.LinkedHashMap;

import rapid.CommandDef;
import rapid.CommandDefSequence;

/**
 * LinkedHashMap of CommandDef objects. Insertion order is maintained. 
 * @author mallan
 *
 */
public class CommandDefMap extends LinkedHashMap<String,CommandDef> {
    
    public CommandDefMap(CommandDefSequence cmdDefSeq) {
        init(cmdDefSeq);
    }
    
    public void init(CommandDefSequence cmdDefSeq) {
        clear();
        add(cmdDefSeq);
    }
    
    /**
     * add all CommandDefs in cmdDefSeq to this collection
     * @param cmdDefSeq
     */
    public void add(CommandDefSequence cmdDefSeq) {
        for(int i = 0; i < cmdDefSeq.userData.size(); i++) {
            CommandDef cmdDef = (CommandDef)cmdDefSeq.userData.get(i);
            put(cmdDef.name, cmdDef);
        }
    }
    
    /**
     * assign the values of this object to cmdDefSeq
     * @param cmdDefSeq if null, a new CommandDefSequence will be created
     * @return cmdDefSeq
     */
    public CommandDefSequence assign(CommandDefSequence cmdDefSeq) {
        if(cmdDefSeq == null) {
            cmdDefSeq = new CommandDefSequence();
        }
        cmdDefSeq.userData.clear();
        for(CommandDef value : values()) {
            cmdDefSeq.userData.add(value);
        }
        return cmdDefSeq;
    }
}
