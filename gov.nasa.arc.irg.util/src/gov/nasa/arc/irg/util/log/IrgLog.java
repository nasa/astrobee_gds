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
package gov.nasa.arc.irg.util.log;

public interface IrgLog 
{
	public void log(Level l, String msg);

	public enum Level {
		Debug,
		Info, 
		Notice, 
		Warn, 
		Error,
		Fatal;
	}
	
	abstract public void replace(IrgLog oldLog);
	
	/** trace should be enabled by default */
	public void enableTrace(boolean status);

	public void debug(String msg);
	public void info (String msg);
	public void warn (String msg);
	public void error(String msg);
	public void fatal(String msg);
	public void debug(String msg, Throwable e);
	public void info (String msg, Throwable e);
	public void warn (String msg, Throwable e);
	public void error(String msg, Throwable e);
	public void fatal(String msg, Throwable e);
	
}
