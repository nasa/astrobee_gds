package gov.nasa.rapid.v2.message.cache;

import rapid.ext.arc.StateMachineConfig;
import rapid.ext.arc.StateMachineState;

import com.google.common.base.Objects;

public class StateMachineKey {
    protected final String subsystem;
    protected final String stateMachine;
    
    public StateMachineKey(String subsystem, String stateMachine) {
        this.subsystem = subsystem;
        this.stateMachine = stateMachine;
    }
    
    public StateMachineKey(StateMachineConfig config) {
        this.subsystem = config.subsystem;
        this.stateMachine = config.stateMachine;
    }
    
    public StateMachineKey(StateMachineState state) {
        this.subsystem = state.subsystem;
        this.stateMachine = state.stateMachine;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(subsystem, stateMachine);
    }
    
    @Override
    public String toString() {
        return subsystem+"::"+stateMachine;
    }
    
    @Override 
    public boolean equals(Object obj) {
        if(obj instanceof StateMachineKey) {
            StateMachineKey other = (StateMachineKey)obj;
            if(other.subsystem.equals(subsystem) && other.stateMachine.equals(stateMachine)) {
                return true;
            }
        }
        return false;
    }
}
