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
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

import com.ardor3d.util.export.InputCapsule;
import com.ardor3d.util.export.OutputCapsule;
import com.ardor3d.util.resource.ResourceSource;

public class BundleResourceSource implements ResourceSource {
    private static final Logger logger = Logger.getLogger(BundleResourceSource.class.getName());

    Bundle m_bundle;
    String m_fullPath;
    String m_path;
    String m_extension = "";

    public BundleResourceSource(Bundle bun, String path){
        assert(bun != null): "bundle cannot be null";
        assert (path != null): "path must contain a path / filename within the bundle";
        m_fullPath = path;
        m_bundle = bun;
        final int dot = path.lastIndexOf('.');
        if (dot >= 0){
            m_extension = path.substring(dot);
        }

        m_path = m_fullPath;
        int separator = path.lastIndexOf(File.separator);
        if (separator < 0){
            // try just a slash
            separator = path.lastIndexOf("/");
        }
        if (separator >= 0){
            m_path = m_fullPath.substring(0, separator + 1);
        } 

    }

    public String getName() {
        if (m_fullPath != null){
            return m_fullPath.toString();
        }
        return null;
    }

    public ResourceSource getRelativeSource(String name) {

        String newPath = m_path + name;
        if (m_bundle.getEntry(newPath) != null){
            return new BundleResourceSource(m_bundle, newPath);
        }

        if (logger.isLoggable(Level.WARNING)) {
            logger.logp(Level.WARNING, getClass().getName(), "getRelativeSource(String)",
                    "Unable to find relative file {0} from {1}.", new Object[] { name, newPath });
        }
        return null;
    }

    public String getType() {
        return m_extension;
    }

    public InputStream openStream() throws IOException {
        return FileLocator.openStream(m_bundle, new Path(m_fullPath), false);
    }

    public Class<?> getClassTag() {
        return BundleResourceSource.class;
    }

    @SuppressWarnings("unused")
    public void read(InputCapsule im) throws IOException {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unused")
    public void write(OutputCapsule ex) throws IOException {
        // writing to a bundle not supported!!! noop.
    }

    public Bundle getBundle() {
        return m_bundle;
    }

    public void setBundle(Bundle bundle) {
        m_bundle = bundle;
    }

    public String getPath() {
        return m_fullPath;
    }

    public void setPath(String path) {
        m_fullPath = path;
    }


}
