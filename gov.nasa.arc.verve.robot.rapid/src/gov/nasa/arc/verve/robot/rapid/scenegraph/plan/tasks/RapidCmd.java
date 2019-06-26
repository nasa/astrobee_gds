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
package gov.nasa.arc.verve.robot.rapid.scenegraph.plan.tasks;

import gov.nasa.arc.verve.robot.parts.concepts.plan.Task.TaskStatus;
import rapid.StatusType;

public class RapidCmd {

    public static TaskStatus getTaskStatus(StatusType sType) {
        switch(sType.ordinal()) {
        case StatusType._Q_STATUS_INIT:
        case StatusType._Q_STATUS_OUTBOUND:
        case StatusType._Q_STATUS_INLINE:
        case StatusType._Q_STATUS_PREEMPTED:
        case StatusType._Q_STATUS_ALIEN_PENDING:
            return TaskStatus.Pending;
        case StatusType._Q_STATUS_INPROCESS:
        case StatusType._Q_STATUS_ALIEN_ACTIVE:
            return TaskStatus.Executing;
        case StatusType._Q_STATUS_PAUSED:
            return TaskStatus.Paused;
        case StatusType._Q_STATUS_CANCELED:
            return TaskStatus.Aborted;
        case StatusType._Q_STATUS_SUCCEEDED:
        case StatusType._Q_STATUS_ALIEN_COMPLETED:
            return TaskStatus.Completed;
        case StatusType._Q_STATUS_FAILED:
        case StatusType._Q_STATUS_LOST:
        case StatusType._Q_STATUS_FORGOTTEN_BY_ROBOT:
            return TaskStatus.Failed;
        }
        return TaskStatus.Unknown;
    }


}
