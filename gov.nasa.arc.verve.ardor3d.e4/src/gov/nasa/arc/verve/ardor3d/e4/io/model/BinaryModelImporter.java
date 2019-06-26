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
package gov.nasa.arc.verve.ardor3d.e4.io.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.osgi.framework.Bundle;

import gov.nasa.arc.viz.io.importer.IModelImporter;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.export.Savable;
import com.ardor3d.util.export.binary.BinaryImporter;

public class BinaryModelImporter extends BinaryImporter implements
		IModelImporter {

	@Override
	public Node importModel(URL url, Map<String, Object> map)
			throws IOException {
		Savable result =  load(url);
		if (result != null && result instanceof Node){
			return (Node)result;
		}
		return null;
	}

	@Override
	public Node importModel(String filepath, Map<String, Object> map)
			throws IOException {
		Savable result =  load(new File(filepath));
		if (result != null && result instanceof Node){
			return (Node)result;
		}
		return null;
	}

	@Override
	//TODO implement
	public Node importModel(Bundle bundle, String filepath, Map<String, Object> map) throws IOException {
		throw new IOException("METHOD IS UNIMPLEMENTED");
	}

	@Override
	//TODO implement
	public Node importModel(InputStream inputStream, Map<String, Object> map) throws IOException {
        throw new IOException("METHOD IS UNIMPLEMENTED");
	}

}
