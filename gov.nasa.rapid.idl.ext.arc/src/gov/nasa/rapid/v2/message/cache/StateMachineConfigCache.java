package gov.nasa.rapid.v2.message.cache;


import gov.nasa.dds.system.DdsTask;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.message.MessageTypeExtArc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import rapid.ext.arc.StateMachineConfig;
import rapid.ext.arc.StateMachineState;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The StateMachine message has two keyed fields (subsystem and stateMachine) to allow
 * multiple instances of StateMachine messages to share the same topic. In order to 
 * properly match a config to state message, the keyed fields must match (as opposed to
 * most RAPID messages that are paired with the most recent Config). 
 * Configs are queried by passing in a State message. If a matching Config is not 
 * found, the State message <i>will be saved</i>. If/when a matching Config is added 
 * to the cache, the saved messages will be re-delivered to the parent through 
 * the IRapidMessageListener.onRapidMessageReceived() method. 
 * @author mallan
 * 
 * <pre>
 * public class Example implements IRapidMessageListener {
 *   final MessageType stateType  = MessageTypeExtArc.STATEMACHINE_STATE_TYPE;
 *   final MessageType configType = MessageTypeExtArc.STATEMACHINE_CONFIG_TYPE;
 *   final StateMachineConfigCache configCache;
 *     
 *   public Example(Agent agent) {
 *     configCache = new StateMachineConfigCache(this, agent, stateType);
 *   }
 *     
 *   public void onRapidMessageReceived(Agent agent, MessageType type, Object eventObj, Object configObj) {
 *     if(type.equals(configType)) {
 *       StateMachineConfig config = (StateMachineConfig)eventObj;
 *       configCache.add(config);
 *     }
 *     if(type.equals(stateType)) {
 *       StateMachineState  state  = (StateMachineState)eventObj;
 *       StateMachineConfig config = configCache.get(state);
 *       if(config != null) {
 *         // here we have a matching pair
 *         System.out.println(RapidExtArcUtil.toString(state, config));
 *       }
 *     }
 *   }
 * }
 * </pre>
 */
public class StateMachineConfigCache {
    private static final Logger logger = Logger.getLogger(StateMachineConfigCache.class);

    protected final IRapidMessageListener m_parent;
    protected final Agent                 m_agent;
    protected final MessageType           m_stateMachineStateType;
    protected final Map<StateMachineKey,StateMachineConfig> m_cache = Maps.newHashMap();
    protected final List<StateMachineState> m_saved = Lists.newLinkedList();

    public StateMachineConfigCache(IRapidMessageListener parent, Agent agent) {
        this(parent, agent, MessageTypeExtArc.STATEMACHINE_STATE_TYPE);
    }
    
    public StateMachineConfigCache(IRapidMessageListener parent, Agent agent, MessageType stateMachineStateType) {
        m_parent = parent;
        m_agent = agent;
        m_stateMachineStateType = stateMachineStateType;
    }

    public void add(StateMachineConfig config) {
        StateMachineKey key = new StateMachineKey(config);
        m_cache.put(key, config);
        checkSavedStatesFor(config);
    }

    /** 
     * get the Config that matches the keyed fields and serial id of state. <br>
     * If no matching Config is found, the StateMachineState will be saved
     * in a list so it can be redelivered if/when a matching config is 
     * added to the cache. 
     */
    public StateMachineConfig get(StateMachineState state) {
        StateMachineKey key = new StateMachineKey(state);
        StateMachineConfig retVal = m_cache.get(key);
        if(retVal == null) {
            synchronized(m_saved) {
                m_saved.add(state);
            }
        }
        return retVal;
    }

    /**
     * in a separate thread, check saved state messages for those matching
     * config and invoke onRapidMessageReceived() for all matching
     * @param config
     */
    protected void checkSavedStatesFor(final StateMachineConfig config) {
        if(m_saved.size() > 0) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    List<StateMachineState> states = getMatchingSavedStates(config);
                    for(StateMachineState state : states) {
                        m_parent.onRapidMessageReceived(m_agent, m_stateMachineStateType, state, config);
                    }
                }
            };
            DdsTask.dispatchExec(runnable);
        }
    }
    
    protected List<StateMachineState> getMatchingSavedStates(StateMachineConfig config) {
        List<StateMachineState> retVal = Lists.newLinkedList();
        synchronized(m_saved) {
            Iterator<StateMachineState> it = m_saved.iterator();
            while(it.hasNext()) {
                StateMachineState state = it.next();
                StateMachineKey key = new StateMachineKey(state);
                if(m_cache.get(key) != null) {
                    // check serial
                    if(config.hdr.serial == state.hdr.serial) {
                        retVal.add(state);
                        it.remove();
                    }
                }
            }
        }
        return retVal;
    }

}
