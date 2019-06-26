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
package gov.nasa.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;


/**
 * String conversion utilities for base types and arrays of base types.
 * 
 * @author mallan
 * 
 */
public class StrUtil {

    /**
     * our default delimiter set includes comma in addition to the standard Java whitespace delimiter set
     */
    public static final String DELIMITERS = ", \t\n\r\f";

    public static boolean toBoolean(String str) {
        return Boolean.valueOf(str).booleanValue();
    }

    public static int toInt(String str) {
        return Integer.valueOf(str).intValue();
    }

    public static short toShort(String str) {
        return Short.valueOf(str).shortValue();
    }

    public static float toFloat(String str) {
        return Float.valueOf(str).floatValue();
    }

    public static double toDouble(String str) {
        return Double.valueOf(str).doubleValue();
    }

    public static int[] toIntArray(String str, String delim) {
        StringTokenizer strtok = new StringTokenizer(str, delim);
        int sz = strtok.countTokens();
        int[] retVal = new int[sz];
        for (int i = 0; i < sz; i++) {
            retVal[i] = toInt(strtok.nextToken());
        }
        return retVal;
    }

    /** @see DELIMITERS */
    public static short[] toShortArray(String str) {
        return toShortArray(str, DELIMITERS);
    }

    public static short[] toShortArray(String str, String delim) {
        final StringTokenizer strtok = new StringTokenizer(str, delim);
        final int sz = strtok.countTokens();
        short[] retVal = new short[sz];
        for (int i = 0; i < sz; i++) {
            retVal[i] = toShort(strtok.nextToken());
        }
        return retVal;
    }

    /** @see DELIMITERS */
    public static int[] toIntArray(String str) {
        return toIntArray(str, DELIMITERS);
    }

    public static float[] toFloatArray(String str, String delim) {
        StringTokenizer strtok = new StringTokenizer(str, delim);
        int sz = strtok.countTokens();
        float[] retVal = new float[sz];
        for (int i = 0; i < sz; i++) {
            retVal[i] = toFloat(strtok.nextToken());
        }
        return retVal;
    }

    /** @see DELIMITERS */
    public static float[] toFloatArray(String str) {
        return toFloatArray(str, DELIMITERS);
    }

    public static double[] toDoubleArray(String str, String delim) {
        StringTokenizer strtok = new StringTokenizer(str, delim);
        int sz = strtok.countTokens();
        double[] retVal = new double[sz];
        for (int i = 0; i < sz; i++) {
            retVal[i] = toDouble(strtok.nextToken());
        }
        return retVal;
    }

    /** @see DELIMITERS */
    public static double[] toDoubleArray(String str) {
        return toDoubleArray(str, DELIMITERS);
    }

    public static double[] toDoubleArray(List<String> input) {
        List<double[]> resultList = new ArrayList<double[]>();
        int totalLength = 0;
        for (String str : input) {
            double[] converted = toDoubleArray(str, DELIMITERS);
            totalLength += converted.length;
            resultList.add(converted);
        }

        double[] result = new double[totalLength];
        int i = 0;
        for (double[] d : resultList) {
            for (int j = 0; j < d.length; j++) {
                result[i + j] = d[j];
            }
            i += d.length;
        }

        return result;
    }

    /**
     * Set a field from a string without knowing what type it is beforehand
     * 
     * @param obj
     *            object owning field
     * @param field
     *            field to be set (must be base type or array of base type)
     * @param str
     *            string value of field to be set
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean setField(Object obj, java.lang.reflect.Field field, String str) {
        boolean retVal = false;
        try {
            if (field.getType().isArray()) // take care of simple arrays of basic types
            {
                Class comp = field.getType().getComponentType();
                if (comp == double.class) {
                    field.set(obj, toDoubleArray(str));
                } 
                else if (comp == float.class) {
                    field.set(obj, toFloatArray(str));
                } 
                else if (comp == int.class) {
                    field.set(obj, toIntArray(str));
                }
                else if (comp == short.class) {
                    field.set(obj, toShortArray(str));
                }
            } 
            else  {// set basic types
                if (field.getType() == double.class) {
                    field.set(obj, toDouble(str));
                } 
                else if (field.getType() == float.class) {
                    field.set(obj, toFloat(str));
                } 
                else if (field.getType() == int.class) {
                    field.set(obj, toInt(str));
                } 
                else if (field.getType() == short.class) {
                    field.set(obj, toShort(str));
                } 
                else if (field.getType() == boolean.class) {
                    field.set(obj, toBoolean(str));
                } 
                else if (field.getType() == String.class) {
                    field.set(obj, str);
                }
            }
            retVal = true;
        } catch (Exception e) {
            // do nothing
        }
        return retVal;
    }

    public static String arrayToString(Object array) {
        return arrayToString(array, " ", "", "");
    }

    public static String arrayToString(Object array, String sep) {
        return arrayToString(array, sep, "", "");
    }

    /**
     * convert an array/collection/hashtable to a printable string
     * 
     * @param array
     * @param beg
     *            string to begin array (e.g. "[")
     * @param end
     *            string to end array (e.g. "]")
     * @param sep
     *            separator (e.g. " " or "\t", ", ", etc)
     * @return stringified array
     */
    @SuppressWarnings("unchecked")
    public static String arrayToString(Object array, String sep, String beg, String end) {
        final String nullStr = "(null)";
        if (beg == null)
            beg = "";
        if (end == null)
            end = "";
        if (sep == null)
            sep = " ";

        if (array == null) {
            return nullStr;
        } else {
            Object obj = null;
            if (array instanceof Collection) {
                array = ((Collection) array).toArray();
            } else if (array instanceof Hashtable) {
                array = ((Hashtable) array).entrySet().toArray();
            } else if (array instanceof HashSet) {
                array = ((HashSet) array).toArray();
            }
            int length = Array.getLength(array);
            int lastItem = length - 1;
            StringBuffer sb = new StringBuffer(beg);
            for (int i = 0; i < length; i++) {
                obj = Array.get(array, i);
                if (obj != null) {
                    sb.append(obj);
                } else {
                    sb.append(nullStr);
                }
                if (i < lastItem) {
                    sb.append(sep);
                }
            }
            sb.append(end);
            return sb.toString();
        }
    }

    /**
     * Take a string. Return a string that has the first character uppercase. eg first becomes First eg FIRST becomes First
     * 
     * @param string
     * @param lowerTheRest
     *            to lowercase the rest of the word, ie Camelcase vs CamelCase
     * @return
     */
    public static String upperFirstChar(String string, boolean lowerTheRest) {
        if (string == null) {
            return null;
        }
        if (string.length() == 0) {
            return "";
        }

        StringBuffer result = new StringBuffer();
        result.setLength(1);
        String upperFirst = string.substring(0, 1);
        result.setCharAt(0, upperFirst.toUpperCase().charAt(0));

        if (string.length() == 1) {
            return result.toString();
        }

        String rest;
        if (lowerTheRest) {
            rest = string.substring(1).toLowerCase();
        } else {
            rest = string.substring(1);
        }
        result.append(rest);
        return result.toString();
    }

    /**
     * Take a string. Return a string that has the first character lowercase. eg First becomes first eg FIRST becomes fIRST
     * 
     * @param string
     * @param lowerTheRest
     *            ie camelCase vs camelcase
     * @return
     */
    public static String lowerFirstChar(String string, boolean lowerTheRest) {
        if (string == null) {
            return null;
        }
        if (string.length() == 0) {
            return "";
        }

        StringBuffer result = new StringBuffer();
        result.setLength(1);
        String upperFirst = string.substring(0, 1);
        result.setCharAt(0, upperFirst.toLowerCase().charAt(0));

        if (string.length() == 1) {
            return result.toString();
        }

        String rest;
        if (lowerTheRest) {
            rest = string.substring(1).toLowerCase();
        } else {
            rest = string.substring(1);
        }
        result.append(rest);
        return result.toString();
    }

    /**
     * Convert a list to a single string separated by the given separator.
     * 
     * @param list
     * @param separator
     * @return
     */
    public static String toSingleString(List<String> list, String separator) {
        StringBuffer result = new StringBuffer();
        for (String s : list) {
            result.append(s);
            result.append(separator);
        }
        return result.toString();
    }

    /**
     * Return a List full of strings extracted from the given string separated by the given separator.
     * 
     * @param string
     * @param separator
     * @return
     */
    public static List<String> fromSingleString(String string, String separator) {
        List<String> result = new ArrayList<String>();
        if (string == null || string.length() == 0 || separator == null || separator.length() == 0) {
            return result;
        }
        String[] splitsville = string.split(separator);
        for (String s : splitsville) {
            result.add(s);
        }
        return result;
    }

    /**
     * Removes "" from around a string. given "henry" returns henry
     * 
     * @param string
     * @return
     */
    public static String trimEndQuotes(String string) {
        String result = string;
        if (result.charAt(0) == '"') {
            result = result.substring(1);
        }
        if (result.charAt(result.length() - 1) == '"') {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * Removes "" from around a string. given "henry" returns henry
     * 
     * @param string
     * @return
     */
    public static String trimEndCurlies(String string) {
        String result = string;
        if (result.charAt(0) == '{') {
            result = result.substring(1);
        }
        if (result.charAt(result.length() - 1) == '}') {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String SEPARATOR = "/";

    /*
     * Cleans a path that has \\ and replaces them with Path.SEPARATOR
     * 
     * @param the path to clean
     * 
     * @return clean string
     */
    public static String cleanPath(String path) {
        String cleanPath = path.trim();
        cleanPath = cleanPath.replaceAll("\\\\", SEPARATOR);
        return cleanPath;
    }

    /**
     * removes path elements and everything following the last "." from filepath horribly inefficient, but not used very often
     */
    public static String basename(String filepath) {
        int idx;
        filepath = cleanPath(filepath);
        while (filepath.endsWith(SEPARATOR)) {
            filepath = filepath.substring(filepath.length() - 2);
        }
        idx = filepath.lastIndexOf(SEPARATOR);
        if (idx >= 0) {
            filepath = filepath.substring(idx + 1);
        }
        idx = filepath.lastIndexOf('.');
        if (idx > 0) { // dotfiles ok
            filepath = filepath.substring(0, idx);
        }
        return filepath;
    }

    public static String stackTrace(Throwable t) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        t.printStackTrace(printWriter);
        return result.toString();
    }

    /**
     * Given a class, get it's package as a directory, eg:
     * 
     * gov.nasa.util.StrUtil -> gov/nasa/util
     * @param myclass
     * @return
     */
    public static String getClasspathAsDirectoryName(Class myclass) {
        if (myclass == null){
            return "";
        }
        String name = myclass.getPackage().getName();
        return (name.replace(".", File.separator));
    }
    

    /**
     * Take a string that may be very long and insert newlines at the space closest to the width given.
     * 
     * ie turn the above into something like:
     * Take a string that may be very long 
     * and insert newlines at the space 
     * closest to the width given.
     * 
     * @param s
     * @param width
     * @return
     */
    public static String insertNewlines(String s, int width){
    	if (s == null){
    		return null;
    	}
    	if (width <= 1){
    		return s;
    	}
    	
    	if (s.length() <= width){
    		return s;
    	}
    	StringBuilder sb = new StringBuilder(s);

    	int i = 0;
    	while ((i = sb.indexOf(" ", i + width)) != -1) {
    	    sb.replace(i, i + 1, "\n");
    	}
    	
    	String[] splits = sb.toString().split("\n");
    	int begin = 0;
    	for (int index = 0; index < splits.length; index++){
    		if (splits[index].length() > width){
    			int lastSpace = splits[index].lastIndexOf(" ");
    			if (lastSpace > 0){
    				sb.replace(begin + lastSpace, begin + lastSpace + 1, "\n");
    			}
    		}
    		begin += splits[index].length();
    	}

    	return sb.toString();
    }
    
    public static int computeNumberOfLines(String s, int width) {
    	if (s == null){
    		return 0;
    	}
    	
    	if (width <= 1){
    		return 1;
    	}
    	
    	if (s.length() <= width){
    		return 1;
    	}
    	
    	StringBuilder sb = new StringBuilder(s);

    	int i = 0;
    	while ((i = sb.indexOf(" ", i + width)) != -1) {
    	    sb.replace(i, i + 1, "\n");
    	}
    	
    	String[] splits = sb.toString().split("\n");
    	
    	return splits.length;
    }
    
    /**
     * Return the extension if any of a file not including the dot.
     * If there is no extension return the filename
     * if file is null return empty string
     * @param file
     * @return
     */
    public static String getExtension(File file){
    	if (file == null){
    		return "";
    	}
    	String name = file.getName();
    	int index = name.lastIndexOf(".");
    	if (index >= 0 && index < name.length()-1){
    		return name.substring(index + 1);
    	}
    	return name;
    }
    
    /**
     * Get the ascii number for a character
     * @param c
     * @return
     */
    public static int getAscii(char c){
		return Character.codePointAt(new char[]{c}, 0);
    }
    
    /**
     * Get a char array by converting the number
     * @param ascii
     * @return
     */
    public static char[] getCharFromAscii(int ascii){
    	return Character.toChars(ascii);
    }
    
    /**
     * convert byte array to a hex string
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        final char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
