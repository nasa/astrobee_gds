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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

@SuppressWarnings("unused")
public class URIUtil {
    private static final Logger logger = Logger.getLogger(URIUtil.class);

    private static final char ESCAPE = '%';
    // Some character classes, as defined in RFC 2396's BNF for URI.
    // These are 128-bit bitmasks, stored as two longs, where the Nth bit is set
    // iff the ASCII character with value N is included in the set.  These are
    // created with the highBitmask() and lowBitmask() methods defined below,
    // and a character is tested against them using matches().
    //
    private static final long ALPHA_HI = highBitmask('a', 'z') | highBitmask('A', 'Z');
    private static final long ALPHA_LO = lowBitmask('a', 'z')  | lowBitmask('A', 'Z');
    private static final long DIGIT_HI = highBitmask('0', '9');
    private static final long DIGIT_LO = lowBitmask('0', '9');
    private static final long ALPHANUM_HI = ALPHA_HI | DIGIT_HI;
    private static final long ALPHANUM_LO = ALPHA_LO | DIGIT_LO;
    private static final long HEX_HI = DIGIT_HI | highBitmask('A', 'F') | highBitmask('a', 'f');
    private static final long HEX_LO = DIGIT_LO | lowBitmask('A', 'F')  | lowBitmask('a', 'f');
    private static final long UNRESERVED_HI = ALPHANUM_HI | highBitmask("-_.!~*'()"); 
    private static final long UNRESERVED_LO = ALPHANUM_LO | lowBitmask("-_.!~*'()");
    private static final long RESERVED_HI = highBitmask(";/?:@&=+$,");
    private static final long RESERVED_LO = lowBitmask(";/?:@&=+$,");


    /**
     * Decodes the given string by interpreting three-digit escape sequences as the bytes of a UTF-8 encoded character
     * and replacing them with the characters they represent.
     * Incomplete escape sequences are ignored and invalid UTF-8 encoded bytes are treated as extended ASCII characters.
     */
    public static String decode(String value)
    {
        if (value == null) return null;

        int i = value.indexOf('%');
        if (i < 0)
        {
            return value;
        }
        else
        {
            StringBuilder result = new StringBuilder(value.substring(0, i));
            byte [] bytes = new byte[4];
            int receivedBytes = 0;
            int expectedBytes = 0;
            for (int len = value.length(); i < len; i++)
            {
                if (isEscaped(value, i)) 
                {
                    char character = unescape(value.charAt(i + 1), value.charAt(i + 2));
                    i += 2;

                    if (expectedBytes > 0)
                    {
                        if ((character & 0xC0) == 0x80)
                        {
                            bytes[receivedBytes++] = (byte)character;
                        }
                        else
                        {
                            expectedBytes = 0;
                        }
                    }
                    else if (character >= 0x80)
                    {
                        if ((character & 0xE0) == 0xC0)
                        {
                            bytes[receivedBytes++] = (byte)character;
                            expectedBytes = 2;
                        }
                        else if ((character & 0xF0) == 0xE0)
                        {
                            bytes[receivedBytes++] = (byte)character;
                            expectedBytes = 3;
                        }
                        else if ((character & 0xF8) == 0xF0)
                        {
                            bytes[receivedBytes++] = (byte)character;
                            expectedBytes = 4;
                        }
                    }

                    if (expectedBytes > 0)
                    {
                        if (receivedBytes == expectedBytes)
                        {
                            switch (receivedBytes)
                            {
                            case 2:
                            {
                                result.append((char)((bytes[0] & 0x1F) << 6 | bytes[1] & 0x3F));
                                break;
                            }
                            case 3:
                            {
                                result.append((char)((bytes[0] & 0xF) << 12 | (bytes[1] & 0X3F) << 6 | bytes[2] & 0x3F));
                                break;
                            }
                            case 4:
                            {
                                result.appendCodePoint(((bytes[0] & 0x7) << 18 | (bytes[1] & 0X3F) << 12 | (bytes[2] & 0X3F) << 6 | bytes[3] & 0x3F));
                                break;
                            }
                            }
                            receivedBytes = 0;
                            expectedBytes = 0;
                        }
                    }
                    else
                    {
                        for (int j = 0; j < receivedBytes; ++j)
                        {
                            result.append((char)bytes[j]);
                        }
                        receivedBytes = 0;
                        result.append(character);
                    }
                }
                else
                {
                    for (int j = 0; j < receivedBytes; ++j)
                    {
                        result.append((char)bytes[j]);
                    }
                    receivedBytes = 0;
                    result.append(value.charAt(i));
                }
            }
            return result.toString();
        }
    }

    // Tests whether an escape occurs in the given string, starting at index i.
    // An escape sequence is a % followed by two hex digits.
    private static boolean isEscaped(String s, int i)
    {
        return s.charAt(i) == ESCAPE && s.length() > i + 2 &&
                matches(s.charAt(i + 1), HEX_HI, HEX_LO) &&
                matches(s.charAt(i + 2), HEX_HI, HEX_LO);
    }

    // Returns the lower half bitmask for all ASCII characters between the two
    // given characters, inclusive.
    private static long lowBitmask(char from, char to)
    {
        long result = 0L;
        if (from < 64 && from <= to)
        {
            to = to < 64 ? to : 63;
            for (char c = from; c <= to; c++)
            {
                result |= (1L << c);
            }
        }
        return result;
    }

    // Returns the upper half bitmask for all AsCII characters between the two
    // given characters, inclusive.
    private static long highBitmask(char from, char to)
    {
        return to < 64 ? 0 : lowBitmask((char)(from < 64 ? 0 : from - 64), (char)(to - 64));
    }

    // Returns the lower half bitmask for all the ASCII characters in the given
    // string.
    private static long lowBitmask(String chars)
    {
        long result = 0L;
        for (int i = 0, len = chars.length(); i < len; i++)
        {
            char c = chars.charAt(i);
            if (c < 64) result |= (1L << c);
        }
        return result;
    }

    // Returns the upper half bitmask for all the ASCII characters in the given
    // string.
    private static long highBitmask(String chars)
    {
        long result = 0L;
        for (int i = 0, len = chars.length(); i < len; i++)
        {
            char c = chars.charAt(i);
            if (c >= 64 && c < 128) result |= (1L << (c - 64));
        }
        return result;
    }


    // Returns whether the given character is in the set specified by the given
    // bitmask.
    private static boolean matches(char c, long highBitmask, long lowBitmask)
    {
        if (c >= 128) return false;
        return c < 64 ?
                       ((1L << c) & lowBitmask) != 0 :
                           ((1L << (c - 64)) & highBitmask) != 0;
    }

    // Returns the character encoded by % followed by the two given hex digits,
    // which is always 0xFF or less, so can safely be casted to a byte.  If
    // either character is not a hex digit, a bogus result will be returned.
    private static char unescape(char highHexDigit, char lowHexDigit)
    {
        return (char)((valueOf(highHexDigit) << 4) | valueOf(lowHexDigit));
    }

    // Returns the int value of the given hex digit.
    private static int valueOf(char hexDigit)
    {
        if (hexDigit >= 'A' && hexDigit <= 'F')
        {
            return hexDigit - 'A' + 10;
        }
        if (hexDigit >= 'a' && hexDigit <= 'f')
        {
            return hexDigit - 'a' + 10;
        }
        if (hexDigit >= '0' && hexDigit <= '9')
        {
            return hexDigit - '0';
        }
        return 0;
    }


    /**
     * Copy a file specified with a URI to a new location.
     * This does not do checking of timestamp.
     * @param remote the URI of the remote file to copy
     * @param directoryName the string describing the full path for the newly copied file
     * @param filename the newly copied filename
     * @return the newly copied file.
     * @throws IOException
     */
    public static File copyFile(URI remote, String directoryName, String filename) throws IOException{
        URL url = remote.toURL();

        FileUtils.forceMkdir(new File(directoryName));

        if (!directoryName.endsWith(File.pathSeparator)){
            directoryName = directoryName.concat(File.pathSeparator);
        }
        File destination = new File(directoryName.concat(filename));
        FileUtils.copyURLToFile(url, destination);
        return destination;
    }

    /**
     * Get the last modified time of the contents of the uri
     * @param uri
     * @return 0 if there was a problem getting to the file.
     */
    public static long getLastModified(URI uri){
        URL url;
        try {
            url = new URL(uri.toString());
            return getLastModified(url);
        } catch (MalformedURLException e) {
            return 0;
        }
    }

    /**
     * Get the last modified time of the contents of the uri
     * @param uri
     * @return 0 if there was a problem getting to the file.
     */
    public static long getLastModified(URL url){
        try {
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            long timestamp = uc.getLastModified();
            return timestamp;
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * pass in a string that we want to make sure can be a uri
     * return the uri-happy string (spaces encoded, file prefix)
     * or the same string if this fails.
     * 
     * @param path
     * @return
     */
    public static String getURIAsString(String path) throws URISyntaxException {
        String cleanPath = StrUtil.cleanPath(path);
        java.net.URI uri = null;

        int index = cleanPath.indexOf("://");
        if (index > 0){
            if (cleanPath.startsWith("file")){
                File file = new File(cleanPath.substring(index + 3));
                uri = file.toURI();
            } else {
                uri = new java.net.URI(cleanPath);
            }
            return uri.toString();
        } else {
            // cleanPath is a relative path; just return path
            return path;
        }

    }

    /**
     * properly encode a URI from a URL, unlike the 
     * braindead URL.toURI() method
     * @throws URISyntaxException
     */
    public static URI toURI(URL url) throws URISyntaxException {
        URI uri = null;
        uri = new URI(url.getProtocol(), url.getAuthority(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        return uri;
    }
}
