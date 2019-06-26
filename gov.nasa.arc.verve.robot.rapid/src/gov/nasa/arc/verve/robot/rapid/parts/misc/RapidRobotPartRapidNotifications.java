//package gov.nasa.arc.verve.robot.rapid.parts.misc;
//
//import gov.nasa.arc.verve.robot.rapid.RapidRobot;
//import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
//import gov.nasa.rapid.v2.e4.agent.Agent;
//import gov.nasa.rapid.v2.e4.message.MessageType;
//import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
//import gov.nasa.rapid.v2.ext.traclabs.Saliency;
//import gov.nasa.rapid.v2.ext.traclabs.message.helpers.NoticeStateHelper;
//import gov.nasa.rapid.v2.message.MessageTypeExtTraclabs;
//import rapid.AccessControlState;
//import rapid.Ack;
//import rapid.AckCompletedStatus;
//import rapid.AckStatus;
//import rapid.MessageLevel;
//import rapid.TextMessage;
//import rapid.ext.traclabs.NoticeState;
//
//import com.ardor3d.scenegraph.Node;
//
//public class RapidRobotPartRapidNotifications extends RapidRobotPart {
//    //private static final Logger logger = Logger.getLogger(RapidRobotPartRapidNotifications.class);
//
//    private final String itemIdAck           = "Ack";
//    private final String itemIdAccessControl = "AccessControl";
//    private final String itemIdTextMessage   = "TextMessage";
//    private NoticeState  m_noticeState       = new NoticeState();
//    private String       m_lastController    = "";
//
//    public RapidRobotPartRapidNotifications(String partName, RapidRobot parent, String participantId) {
//        super(partName, parent, participantId);
//        // TODO Auto-generated constructor stub
//    }
//
//    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object msgObj, Object cfgObj) {
//        if(!isVisible())
//            return;
//
//        //-- bad Acks ----
//        if(msgType.equals(MessageType.ACK_TYPE)) {
//            Ack ack = (Ack)msgObj;
//            //logger.info(ack.toString("Ack received", 0));
//            if(ack.status.equals(AckStatus.ACK_COMPLETED) || ack.status.equals(AckStatus.ACK_REQUEUED)) {
//                switch(ack.completedStatus.ordinal()) {
//                case AckCompletedStatus._ACK_COMPLETED_OK:
//                    break;
//                case AckCompletedStatus._ACK_COMPLETED_NOT:
//                case AckCompletedStatus._ACK_COMPLETED_BAD_SYNTAX:
//                case AckCompletedStatus._ACK_COMPLETED_EXEC_FAILED:
//                case AckCompletedStatus._ACK_COMPLETED_CANCELED:
//                    String subject = ack.status.toString()+":\nCommand "+ack.completedStatus.toString().substring(14);
//                    NoticeStateHelper.assignNoticeState(agent, itemIdAck, Saliency.Alert, 
//                                                        subject, 
//                                                        ack.message, 
//                                                        10, m_noticeState);
//                    RapidMessageCollector.instance().injectMessage(getParticipantId(), agent, 
//                                                                   MessageTypeExtTraclabs.NOTICE_STATE_TYPE, 
//                                                                   m_noticeState);
//                }
//            }
//        }
//
//        //-- Access Control -----
//        if(msgType.equals(MessageType.ACCESSCONTROL_STATE_TYPE)) {
//            AccessControlState acs = (AccessControlState)msgObj;
//            if(acs.controller.length() > 0 && !m_lastController.equals(acs.controller)) {
//                String msg =  acs.controller +" has control of "+agent.name();
//                NoticeStateHelper.assignNoticeState(agent, itemIdAccessControl, Saliency.Notice, 
//                                                    "Access Control", msg, 
//                                                    5, m_noticeState);
//                RapidMessageCollector.instance().injectMessage(getParticipantId(), agent, 
//                                                               MessageTypeExtTraclabs.NOTICE_STATE_TYPE, 
//                                                               m_noticeState);
//            }
//            m_lastController = acs.controller;
//        }
//
//        //-- TextMessage -----
//        if(msgType.equals(MessageType.TEXTMESSAGE_TYPE)) {
//            TextMessage tm = (TextMessage)msgObj;
//            Saliency saliency = Saliency.LogNotice;
//            switch(tm.level.ordinal()) {
//            case MessageLevel._MSG_DEBUG:    saliency = Saliency.LogNotice; break;
//            case MessageLevel._MSG_INFO:     saliency = Saliency.Notice;    break;
//            case MessageLevel._MSG_ATTENTION:saliency = Saliency.Important; break;
//            case MessageLevel._MSG_WARN:     saliency = Saliency.Urgent;    break;
//            case MessageLevel._MSG_ERROR:    saliency = Saliency.Alarm;     break;
//            case MessageLevel._MSG_CRITICAL: saliency = Saliency.Alert;     break;
//            }
//            NoticeStateHelper.assignNoticeState(agent, itemIdTextMessage, saliency, 
//                                                tm.category+" message", tm.message, 
//                                                10, m_noticeState);
//            RapidMessageCollector.instance().injectMessage(getParticipantId(), agent, 
//                                                           MessageTypeExtTraclabs.NOTICE_STATE_TYPE, 
//                                                           m_noticeState);
//        }
//
//    }
//
//    @Override
//    public MessageType[] rapidMessageTypes() {
//        return new MessageType[] { MessageType.ACK_TYPE, 
//                                   MessageType.ACCESSCONTROL_STATE_TYPE,
//                                   MessageType.TEXTMESSAGE_TYPE
//        };
//    }
//
//    @Override
//    public void attachToNodesIn(Node model) throws IllegalStateException {
//        // nothing to attach
//    }
//
//    @Override
//    public void handleFrameUpdate(long currentTime) {
//        // nothing to udpate
//
//    }
//
//    @Override
//    public void reset() {
//        // nothing to reset
//    }
//
//}
