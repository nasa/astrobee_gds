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
package gov.nasa.arc.viz.io.importer;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.runtime.Path;

public class FileDescriptor {

    protected File file;
    protected String ext, ext2;
    protected URL url;
    protected String location;
    protected String fileName;

    public static final String SEPARATOR = Character.toString(Path.SEPARATOR);
    
    public FileDescriptor() {
        // empty
    }

    public FileDescriptor(String fileName, String ext, String location, File file, URL url) {
        this.file = file;
        this.ext = ext;
        ext2 = ext;
        this.url = url;
        this.location = location;
        this.fileName = fileName;
    }

    /**
     * Given a file location return the appropriate I/O object.
     * @param location the file location
     * @return the I/O object (File or URL)
     */
    public FileDescriptor(String loc) {
        /*
		location = location.replace("\\", Path.SEPARATOR);
		file = new File(location);
		fileName = location.substring(location.lastIndexOf(Path.SEPARATOR)+1);
		ext = location.toLowerCase().substring(location.lastIndexOf('.')+1);
		ext2 = ext;
         */
        location = loc.replace("\\", SEPARATOR);
        String lloc = location.toLowerCase();
        try {
            if (lloc.startsWith("bundle")) {
                url = new URL(location);
                ext = lloc.substring(lloc.lastIndexOf('.')+1);
                ext2 = ext;
                fileName = location.substring(location.lastIndexOf(Path.SEPARATOR)+1);
            }
            else if (lloc.startsWith("file:/")) {
                URL tmpurl = new URL(location);
                file = new File(tmpurl.getPath());
                location = file.getAbsolutePath();
                ext = lloc.substring(lloc.lastIndexOf('.')+1);
                ext2 = ext;
                fileName = location.substring(location.lastIndexOf(Path.SEPARATOR)+1);
            }
            else if ((lloc.startsWith("http:/")) || (lloc.startsWith("https:/"))) {
                url = new URL(location);
                URLConnection urlConnection = url.openConnection();
                String urlContentType = urlConnection.getContentType();
                String cdisp = urlConnection.getHeaderField("Content-disposition");
                fileName = url.getPath();
                ext = fileName.toLowerCase().substring(fileName.lastIndexOf('.')+1);
                //System.err.println("FileDescriptor "+urlContentType+" "+cdisp+" "+fileName);
                //System.out.println("HEADERFIELDS: "+urlConnection.getHeaderFields());
                //System.out.println("CONTENTTYPE "+type+" CONTENTDISP "+cdisp);
                int p = 0;
                if (cdisp != null) {
                    p = cdisp.indexOf("filename=");
                    if (p >= 0)
                        fileName = cdisp.substring(p+9);
                }
                if (urlContentType != null) {
                    ext2 = urlContentType.toLowerCase();
                    ext2 = ext.substring(ext.lastIndexOf(Path.SEPARATOR)+1);
                    // fix for our website problem
                    if (ext2.equals("x-rgb"))
                        ext2 = "rgb";
                }
                else {
                    if (fileName != null) {
                        p = fileName.lastIndexOf('.');
                        ext2 = fileName.toLowerCase().substring(p+1);
                    }
                    else
                        ext2 = "";
                }
            }
            else {
                file = new File(location);
                location = file.getAbsolutePath();
                fileName = location.substring(location.lastIndexOf(Path.SEPARATOR)+1);
                ext = lloc.substring(lloc.lastIndexOf('.')+1);
                ext2 = ext;
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Given a file location return the appropriate I/O object.
     * @param location the file location
     * @return the I/O object (File or URL)
     */
    public FileDescriptor(File loc) {
        file = loc;
        location = loc.getAbsolutePath();
        location = location.replace("\\", SEPARATOR);
        fileName = location.substring(location.lastIndexOf(Path.SEPARATOR)+1);
        ext = location.toLowerCase().substring(location.lastIndexOf('.')+1);
    }

    public boolean isURL() {
        return(url != null);
    }

    /**
     * Create a node label from a location string.
     * @param location the location string
     * @return the label
     */
    public String getLabel() {
        int p = fileName.lastIndexOf('.');
        if (p < 0)
            p = fileName.length();
        String label = fileName.substring(0, p);
        int q = label.lastIndexOf(Path.SEPARATOR)+1;
        if (q == 0)
            return(label);
        p = label.substring(0, q).lastIndexOf(Path.SEPARATOR)+1;
        if (p == 0)
            return(label.substring(q));
        return(label.substring(p));
    }

    public File getFile() {
        return(file);
    }

    public URL getURL() {
        if (url != null)
            return(url);
        else {
            try {
                //return(new File(location).toURL());
                return(new File(location).toURI().toURL());
            }
            catch (Exception e) {
                return(null);
            }
        }

    }

    public String getLocation() {
        return(location);
    }

    public String getExtension() {
        return(ext);
    }

    public String getSecondaryExtension() {
        return(ext2);
    }

    public String getFileName() {
        return(fileName);
    }

    @Override
    public String toString() {
        return(location);
    }

    
}
