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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * Utility class to create a SHA1 hex string
 */
public class Sha1Hash {
	private static final Logger logger = Logger.getLogger(Sha1Hash.class);
	protected MessageDigest m_digester;

	public Sha1Hash() {
		try {
			m_digester = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			logger.error("SHA-1 algorithm is not available", e);
			m_digester = null;
		}
	}

	/**
	 * @param input
	 * @throws IllegalStateException
	 *             if MessageDigest was unable to be initialized
	 */
	public Sha1Hash update(byte[] input) throws IllegalStateException {
		if (m_digester == null) {
			throw new IllegalStateException("cannot compute SHA-1");
		}
		m_digester.update(input);
		return this;
	}

	/**
	 * digests the data, resets, and returns a hex string hash
	 */
	public String hexString() {
		return convertToHexString(digest());
	}

	/**
	 * digests the data, resets, and returns 40 byte hash
	 * 
	 * @return
	 * @throws IllegalStateException
	 *             if MessageDigest was unable to be initialized
	 */
	public byte[] digest() throws IllegalStateException {
		if (m_digester == null) {
			throw new IllegalStateException("cannot compute SHA-1");
		}
		return m_digester.digest();
	}

	/**
	 * convert a byte array to a hex string
	 * 
	 * @param data
	 * @return
	 */
	public static String convertToHexString(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int hbyte = (data[i] >>> 4) & 0x0F;
			int both = 0;
			do {
				if ((0 <= hbyte) && (hbyte <= 9))
					buf.append((char) ('0' + hbyte));
				else
					buf.append((char) ('a' + (hbyte - 10)));
				hbyte = data[i] & 0x0F;
			} while (both++ < 1);
		}
		return buf.toString();
	}

	public static void main(final String[] args) {
		Sha1Hash sha1 = new Sha1Hash();
		String inputString = "Hello, this is a test of the emergency broadcast system. This is only a test.";
		System.out.println("input string=\"" + inputString + "\"");
		try {
			String hashString = sha1.update(inputString.getBytes("iso-8859-1")).hexString();
			System.out.println("hex string=" + hashString);
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
