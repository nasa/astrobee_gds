/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.irg.plan.json;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class JsonDateDeserializer extends JsonDeserializer<Date> {

	private static final Logger logger = Logger.getLogger(JsonDateDeserializer.class);
	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		try {
			return parse(jp.getText());
		} catch (ParseException e) {
			logger.error(e);
		}
		return null;
		
	}
	
	   public static Date parse( String input ) throws java.text.ParseException {

	        //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
	        //things a bit.  Before we go on we have to repair this.
	        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ" );
	        SimpleDateFormat df2 = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" );
	        
	        //this is zero time so we need to add that TZ indicator for 
	        if ( input.endsWith( "Z" ) ) {
	            input = input.substring( 0, input.length() - 1) + "GMT-00:00";
	        } else {
	            int inset = 6;
	        
	            String s0 = input.substring( 0, input.length() - inset );
	            String s1 = input.substring( input.length() - inset, input.length() );

	            input = s0 + "GMT" + s1;
	        }
	        
	        try {
	        	return df.parse( input );
	        } catch (ParseException ex){
	        	return df2.parse(input);
	        }
	        
	    }

}
