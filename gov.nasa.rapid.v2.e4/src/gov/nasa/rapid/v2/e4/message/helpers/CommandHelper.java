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

import rapid.Command;
import rapid.CommandDef;
import rapid.ParameterUnion;

/**
 * 
 *
 */
public class CommandHelper {
    protected final CommandDef    m_cmdDef;
    protected final ParameterList m_params = new ParameterList();
    protected Command             m_cmd;

    public CommandHelper(CommandDef cmdDef) {
        m_cmdDef = cmdDef;
        m_params.init(m_cmdDef.parameters.userData);
    }

    public CommandHelper(CommandDef cmdDef, Command cmd) {
        this(cmdDef);
        setCommand(cmd);
    }

    public Command getCommand() {
        return m_cmd;
    }

    public CommandDef getCommandDef() {
        return m_cmdDef;
    }

    public void setCommand(Command cmd) {
        if(m_cmdDef.name.equals(cmd.cmdName)) {
            this.m_cmd = cmd;
            m_params.set(cmd.arguments.userData);
        } 
        else {
            throw new IllegalStateException("CommandDef and Command must match");
        }
    }

    public ParameterList getParameters() {
        return m_params;
    }

    public static String toString(Command cmd) {
        StringBuilder sb = new StringBuilder(cmd.subsysName).append("::").append( cmd.cmdName);
        sb.append("(");
        for(int i = 0; i < cmd.arguments.userData.size(); i++) {
            if(i > 0) 
                sb.append(",");
            ParameterUnion param = (ParameterUnion)cmd.arguments.userData.get(i);
            sb.append(ParamHelper.valueString(param));
        }
        sb.append(")");
        sb.append("::");
        sb.append(cmd.cmdId);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return toString(m_cmd);
    }

    public static class Key {
        protected String subsystem;
        protected String command;
        public Key(Command cmd) {
            this.subsystem = cmd.subsysName;
            this.command = cmd.cmdName;
        }
        public Key(String subsystem, String command) {
            this.subsystem = subsystem;
            this.command = command;
        }
        @Override
        public int hashCode() {
            return subsystem.hashCode()+command.hashCode();
        }
        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof CommandHelper.Key) {
                CommandHelper.Key other = (CommandHelper.Key)obj;
                if(this.subsystem.equals(other.subsystem) &&
                        (this.command.equals(other.command)) ) {
                    return true;
                }
            }
            return false;
        }
    }

}
