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
package gov.nasa.arc.verve.common.notify;

import java.util.List;

import com.google.common.collect.Lists;

public class NoticeActionConnector {
    //private static final Logger logger = Logger.getLogger(NoticeActionConnector.class);
    
    protected List<INoticeActionListener> m_listeners = Lists.newLinkedList();
    
    public static final NoticeActionConnector INSTANCE = new NoticeActionConnector();
    
    public void addListener(INoticeActionListener listener) {
        if(!m_listeners.contains(listener)) {
            m_listeners.add(listener);
        }
    }
    
    public void removeListener(INoticeActionListener listener) {
        m_listeners.remove(listener);
    }
    
    public void noticeDismissed(String context, String itemId, String noticeId) {
        //logger.debug(String.format("noticeDismissed(%s, %s, %s)", context, itemId, noticeId));
        for(INoticeActionListener listener : m_listeners) {
            listener.noticeDismissed(context, itemId, noticeId);
        }
    }
    
}
