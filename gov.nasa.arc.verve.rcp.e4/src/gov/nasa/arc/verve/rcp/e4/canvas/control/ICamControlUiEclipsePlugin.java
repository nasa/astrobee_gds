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
package gov.nasa.arc.verve.rcp.e4.canvas.control;

import org.eclipse.jface.action.ActionContributionItem;

import gov.nasa.arc.verve.ardor3d.e4.input.control.ICamControlUi;
import gov.nasa.arc.verve.common.interest.InterestPointProvider;

public interface ICamControlUiEclipsePlugin extends ICamControlUi {

    @Override
    public void setFollow(InterestPointProvider ipp);
    
    public ActionContributionItem[] getActionItems();
}
