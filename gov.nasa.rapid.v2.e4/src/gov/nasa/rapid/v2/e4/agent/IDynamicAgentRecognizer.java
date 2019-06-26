package gov.nasa.rapid.v2.e4.agent;

public interface IDynamicAgentRecognizer {
    
    boolean isAgentName(String partition);
    
    /** 
     * Gets Agent corresponding to partition name (creating if necessary)
     * @param partition
     * @return agent, if partition is valid agent name, else null
     */
    Agent getAgent(String partition);

}
