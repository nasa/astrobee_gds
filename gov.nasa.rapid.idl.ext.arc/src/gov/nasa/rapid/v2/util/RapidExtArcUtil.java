/*******************************************************************************
 * Copyright (c) 2011 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *******************************************************************************/
package gov.nasa.rapid.v2.util;

import gov.nasa.rapid.v2.e4.util.RapidUtil;
import rapid.ext.arc.Float32Config;
import rapid.ext.arc.Float32Sample;
import rapid.ext.arc.StateMachineConfig;
import rapid.ext.arc.StateMachineState;


public class RapidExtArcUtil extends RapidUtil {
    
    public static String toString(Float32Sample sample, Float32Config config) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < config.dataKeys.userData.size(); i++) {
            final String key = (String)config.dataKeys.userData.get(i);
            final float value = sample.data.userData.getFloat(i);
            sb.append(String.format("%20s=%.4f\n", key, value));
        }
        return sb.toString();
    }
    
    public static String toString(StateMachineState state, StateMachineConfig config) {
        int cs = state.currentState;
        int ps = state.previousState;
        int ev = state.lastEvent;
        StringBuilder sb = new StringBuilder();
        sb.append("Subsystem=").append(config.subsystem).append("  StateMachine=").append(config.stateMachine).append("\n");
        sb.append("     lastEvent=").append(config.events.userData.get(ev)).append("\n");
        sb.append("  currentState=").append(config.states.userData.get(cs)).append("\n");
        return sb.toString();
    }
    
    public static String toString(StateMachineConfig config) {
        StringBuilder sb = new StringBuilder();
        sb.append("Subsystem="+config.subsystem+"  StateMachine="+config.stateMachine+"\n");
        sb.append("  States:\n");
        for(int i = 0; i < config.states.userData.size(); i++) {
            String str = (String)config.states.userData.get(i);
            sb.append("    ").append(str).append("\n");
        }
        sb.append("  Events:\n");
        for(int i = 0; i < config.events.userData.size(); i++) {
            String str = (String)config.events.userData.get(i);
            sb.append("    ").append(str).append("\n");
        }
        return sb.toString();
    }
}
