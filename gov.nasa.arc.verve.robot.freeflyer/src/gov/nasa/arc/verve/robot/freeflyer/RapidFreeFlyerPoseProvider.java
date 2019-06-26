package gov.nasa.arc.verve.robot.freeflyer;

import gov.nasa.arc.verve.robot.rapid.RapidPoseProvider;
import gov.nasa.rapid.v2.e4.agent.Agent;

public class RapidFreeFlyerPoseProvider extends RapidPoseProvider {
    static int cnt = 0;
    protected RapidFreeFlyerPoseProvider(String participantId, Agent agent) {
        super(participantId, agent);
        double x = cnt;
        setInitialPosition(x/7, 0, 0);
        cnt++;
    }
}
