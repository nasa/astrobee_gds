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
package gov.nasa.arc.irg.iss.ui;

import gov.nasa.arc.irg.util.ui.table.FontHelper;

import org.eclipse.swt.graphics.Font;

public class IssFontHelper extends FontHelper {
	protected static Font s_issFont;
    
	 public static Font getISSFont() {
	        if (s_issFont == null || s_issFont.isDisposed()){
	            s_issFont = getFontRegistry().get("gov.nasa.arc.irg.iss.ui.font");
	           
//	           FontData[] fdarray = s_issFont.getFontData();
//	           if (fdarray != null && fdarray.length > 0){
//	               FontData fd  = fdarray[0];
//	               s_tableFontHeight = fd.getHeight() + 6;
//	           } else {
//	               s_tableFontHeight = 14;
//	           }
//	           
//	           GC gc = new GC(Display.getDefault());
//	           gc.setFont(s_issFont);
//	           Point size = gc.textExtent(TEST);
//	           gc.dispose();
//	           s_tableFontHeight = Math.max(s_tableFontHeight, size.y + 2);
	        }
	        return s_issFont;
	    }
}
