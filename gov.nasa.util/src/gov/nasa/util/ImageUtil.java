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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtil {

	public static ByteArrayOutputStream getByteArrayOutputStream(URL url) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 1k at a time
		byte[] buf = new byte[1024];
		try {
			for (int readNum; (readNum = bis.read(buf)) != -1;) {
				bos.write(buf, 0, readNum); 
			}
		} catch (IOException ex) {
			Logger.getLogger(ImageUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
		return bos;
	}
	
	
	/**
	 * given a byte array and a file location, create a JPG 
	 *
	 * @param bytes
	 * @param outputJpegFile
	 * @throws IOException
	 */
	public static void writeToJpegFile(byte[] bytes, String outputJpegFile) throws IOException {
		
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpg");
        //ImageIO is a class containing static convenience methods for locating ImageReaders
        //and ImageWriters, and performing simple encoding and decoding. 
 
        ImageReader reader = (ImageReader) readers.next();
        Object source = bis; // File or InputStream, it seems file is OK
 
        ImageInputStream iis = ImageIO.createImageInputStream(source);
        //Returns an ImageInputStream that will take its input from the given Object
 
        reader.setInput(iis, true);
        ImageReadParam param = reader.getDefaultReadParam();
 
        Image image = reader.read(0, param);
        //got an image file
 
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        //bufferedImage is the RenderedImage to be written
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, null, null);
        File imageFile = new File(outputJpegFile);
        ImageIO.write(bufferedImage, "jpg", imageFile);
 
        // System.out.println(imageFile.getPath());
	}
}
