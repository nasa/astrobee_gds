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
package gov.nasa.arc.irg.util.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 *
 */
public class ViewID {
    private static Logger logger = Logger.getLogger(ViewID.class);
    private static Random random = new Random();

    /**
     * Get a unique secondary id for the given view.
     * If there is no active workbench window / page, this will 
     * return a random number between 0 and 9999
     * 
     * @param viewID
     * @return
     */
    public static String getUniqueSecondaryID(String viewID){
    	return getUniqueSecondaryID(viewID, null);
    }
    
    /**
     * Get a unique secondary id for the given view.
     * If there is no active workbench window / page, this will 
     * return a random number between 0 and 9999
     * 
     * @param viewID
     * @param options e.g. "vip=4321;foo=bar;option=sweet"
     * @return
     */
    public static String getUniqueSecondaryID(String viewID, String optionsString) {
        int result = random.nextInt()%10000;
        String secondaryID = Integer.toString(result);
        
        if (optionsString != null){
        	secondaryID += optionsString;
        }

        IWorkbench wb = PlatformUI.getWorkbench();
        if (wb != null){
            IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();
            if (ww != null){
                IWorkbenchPage page = ww.getActivePage();
                if (page != null){
                	IViewReference found = page.findViewReference(viewID, secondaryID);
                    if (found == null) {
                        return secondaryID;
                    } else {
                        result = 0;
                        while (found != null){
                            result++;
                            secondaryID = Integer.toString(result);
                            if (optionsString != null){
                            	secondaryID += optionsString;
                            }
                            found = page.findViewReference(viewID, secondaryID);
                        }
                        return secondaryID;
                    }
                }
                else {
                    //logger.debug("failed to get active workbench page");
                }
            }
            else {
                logger.debug("failed to get workbench window");
            }
        }
        else {
            logger.debug("failed to get workbench");
        }
        return secondaryID;
    }

    /**
     * Create a full ID string including the View ID and secondary ID
     * @param viewID ID string of the view 
     * @return viewID + ":" + uniqueSecondaryID
     */
    public static String getUniqueID(String viewID) {
        String retVal = viewID + ":" + getUniqueSecondaryID(viewID, null);
        return retVal;
    }

    /**
     * 
     * @param viewID
     * @param optionString @see getOptionMap(String secondaryId)
     * @return
     */
    public static String getUniqueID(String viewID, String optionString) {
        if(!optionString.startsWith(";")) {
            optionString = ";"+optionString;
        }
        return viewID + ":" + getUniqueSecondaryID(viewID, optionString);
    }

    /**
     * 
     * @param viewID
     * @param optionMap
     * @return
     */
    public static String getUniqueID(String viewID, Map<String,String> optionMap) {
        String optionString = getOptionString(optionMap);
        return getUniqueID(viewID, optionString);
    }

    /**
     * Get a map of key,value options from a secondary id. Options
     * are separated by semicolon (';') character and are of the 
     * form option=value. e.g. "vip=4321;foo=bar;option=sweet"
     * @param secondaryId ViewPart secondary id, obtained from ViewPart.getViewSite().getSecondaryId();
     * @return map of options
     */
    public static Map<String,String> getOptionMap(String secondaryId) {
        HashMap<String,String> options = new HashMap<String,String>();
        return getOptionMap(secondaryId, options);
    }
    public static Map<String,String> getOptionMap(String secondaryId, Map<String,String> options) {
        if(secondaryId != null) {
            String[] opts = secondaryId.split(";");
            for(String opt : opts) {
                String[] option = opt.split("=");
                if(option.length == 2) {
                    options.put(option[0], option[1]);
                }
            }
        }
        return options;
    }

    /**
     * Convenience method
     * @param viewPart ViewPart reference
     * @return ViewID.getOptionMap(viewPart.getViewSite().getSecondaryId())
     */
    public static Map<String,String> getOptionMap(ViewPart viewPart) {
        return getOptionMap(viewPart.getViewSite().getSecondaryId());
    }
    /**
     * Convenience method
     * @param viewPart ViewPart reference
     * @param options map to fill and return 
     * @return options
     */
    public static Map<String,String> getOptionMap(ViewPart viewPart, Map<String,String> options) {
        return getOptionMap(viewPart.getViewSite().getSecondaryId(), options);
    }

    /**
     * 
     * @param optionMap
     * @return string in the form of ";option1=value1;option2=value2"
     */
    public static String getOptionString(Map<String,String> optionMap) {
        StringBuilder builder = new StringBuilder();
        for(String key : optionMap.keySet()) {
            String value = optionMap.get(key);
            if( key!=null && key.length() > 0) {
                builder.append(";");
                builder.append(key);
                builder.append("=");
                builder.append(value);
            }
        }
        return builder.toString();
    }
}
