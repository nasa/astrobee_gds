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
package gov.nasa.arc.irg.iss.ui.view.log.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class TailInputStream extends InputStream {

	private RandomAccessFile fRaf;

	private long fTail;

	public TailInputStream(File file, long maxLength) throws IOException {
		super();
		fTail = maxLength;
		fRaf = new RandomAccessFile(file, "r"); //$NON-NLS-1$
		skipHead(file);
	}

	private void skipHead(File file) throws IOException {
		if (file.length() > fTail) {
			fRaf.seek(file.length() - fTail);
			// skip bytes until a new line to be sure we start from a beginnng of valid UTF-8 character
			int c = read();
			while (c != '\n' && c != 'r' && c != -1) {
				c = read();
			}

		}
	}

	@Override
	public int read() throws IOException {
		byte[] b = new byte[1];
		int len = fRaf.read(b, 0, 1);
		if (len < 0) {
			return len;
		}
		return b[0];
	}

	@Override
	public int read(byte[] b) throws IOException {
		return fRaf.read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return fRaf.read(b, off, len);
	}

	@Override
	public void close() throws IOException {
		fRaf.close();
	}

}
