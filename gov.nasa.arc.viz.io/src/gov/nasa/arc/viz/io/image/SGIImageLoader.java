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
package gov.nasa.arc.viz.io.image;

import gov.nasa.arc.viz.io.exception.ParsingErrorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.ardor3d.image.Image;
import com.ardor3d.image.ImageDataFormat;
import com.ardor3d.image.util.ImageLoader;
import com.ardor3d.util.geom.BufferUtils;

/**
 * 
 * SGIImageFile is a class for loading and caching SGI format files.
 *
 * @author Leslie Keely, NASA Ames
 */
public class SGIImageLoader implements ImageLoader {

	protected int magic;
	protected int storage;
	protected int bpc;
	protected int dimension;
	protected int width;
	protected int height;
	protected int bands;
	protected int pixMin;
	protected int pixMax;
	protected String imageName;
	protected int colorMapID;
	protected static int RLE = 1;
	protected int dataCount;
	protected String filepath;
	protected InputStream iStream;

	/**
	 * Construct a new SGIImageFile.
	 *
	 * @param filepath the file path
	 */
	public SGIImageLoader() {
		// empty
	}

	/**
	 * Construct a new SGIImageFile.
	 *
	 * @param filepath the file path
	 */
	public SGIImageLoader(String filepath) {
		this.filepath = filepath;
		}

	/**
	 * Construct a new SGIImageFile.
	 *
	 * @param iStream an InputStream
	 */
	public Image load(InputStream iStream, boolean flipped)
		throws IOException {
		this.iStream = iStream;
		return(getImage());
		}

	/**
	 * Get the SGI image as a BufferedImage.
	 * 
	 * @return the image
	 * @throws VizExplorerException
	 */
	public Image getImage()
		throws IOException {
		byte[] buffer = new byte[50000];
		byte[] readBuf = new byte[4000];
		int numBytes = 0;
		try {
			if (iStream == null) {
				File file = new File(filepath);
				iStream = new FileInputStream(file);
				}
			}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
			}
		int n = iStream.read(readBuf, 0, 2);
		magic = bytes2Short(readBuf[0], readBuf[1]);
		//System.out.println("MAGIC "+magic);
		if (magic != 474)
			return(null);
		System.arraycopy(readBuf, 0, buffer, numBytes, n);
		numBytes = 2;
		while ((n = iStream.read(readBuf)) >= 0) {
			int newNumBytes = numBytes+n;
			if (newNumBytes > buffer.length) {
				byte[] newBuffer = new byte[buffer.length+50000];
				System.arraycopy(buffer, 0, newBuffer, 0, numBytes);
				buffer = newBuffer;
				}					
			System.arraycopy(readBuf, 0, buffer, numBytes, n);
			numBytes = newNumBytes;
			}
		
		// get the header
		Map<String,Object> headerMap = getHeader(buffer);
		if (headerMap == null)
			throw new ParsingErrorException("Error reading header for "+filepath);
		dataCount = width*height;
		if (Runtime.getRuntime().freeMemory() < (4*dataCount)) {
			System.gc();
			long freeMem = Runtime.getRuntime().freeMemory();
			if (freeMem < (4*dataCount))
				throw new RuntimeException("Not enough memory available for "+filepath+" "+freeMem);
			}
		byte[] imageData = new byte[dataCount*4];
		if (storage == RLE)
			loadRLE(imageData, buffer, 512);
		else
			loadVerbatim(imageData, buffer, 512);
		if (bands != 4) {
			if (bands == 1) {
				for (int i=0; i<dataCount; ++i) {
					for (int j=1; j<3; ++j)
						imageData[i*4+j] = imageData[i*4];
					imageData[i*4+3] = (byte)0xff;
					}
				}
			else if (bands == 3) {
				for (int i=0; i<dataCount; ++i) {
					imageData[i*4+3] = (byte)0xff;
					}
				}
			}

		// on SGIs Y=0 is at the bottom of the screen so flip the image
		/*
		int[] scanLine = new int[width];
		for (int h=0; h<height/2; ++h) {
			System.arraycopy(imageIntData, h*width, scanLine, 0, width);
			System.arraycopy(imageIntData, (height-h-1)*width, imageIntData, h*width, width);
			System.arraycopy(scanLine, 0, imageIntData, (height-h-1)*width, width);
			}
		*/
		// create the image
		/*
		BufferedImage bufferedImage = null;
		if (bands == 1) {
			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster raster = bufferedImage.getRaster();
			raster.setPixels(0, 0, width, height, imageIntData);
			}
		else {
			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
			WritableRaster raster = bufferedImage.getRaster();
			DataBufferByte dataBuffer = (DataBufferByte)raster.getDataBuffer();
			byte[] dataBufData = dataBuffer.getData();
			int jj = 0;
			if (bands < 4)
				for (int j=0; j<dataCount; ++j) {
					dataBufData[jj++] = (byte)((imageIntData[j] | 0xff000000) >> 24);
					dataBufData[jj++] = (byte)((imageIntData[j] & 0x00ff0000) >> 16);
					dataBufData[jj++] = (byte)((imageIntData[j] & 0x0000ff00) >> 8);
					dataBufData[jj++] = (byte)((imageIntData[j] & 0x000000ff));
					}
			else
				for (int j=0; j<dataCount; ++j) {
					dataBufData[jj++] = (byte)((imageIntData[j] | 0xff000000) >> 24);
					dataBufData[jj++] = (byte)((imageIntData[j] & 0x00ff0000) >> 16);
					dataBufData[jj++] = (byte)((imageIntData[j] & 0x0000ff00) >> 8);
					dataBufData[jj++] = (byte)((imageIntData[j] & 0x000000ff));
					}
			}
			*/
		buffer = null;
		/*
		if ((width > 1024) || (height > 1024)) {
			// if this image is bigger than 1024, reduce its size to make it usable
			double xScale = 1024.0/width;
			double yScale = 1024.0/height;
			width = 1024;
			height = 1024;
			AffineTransform transform = AffineTransform.getScaleInstance(xScale, yScale);
			System.out.println("Image "+filepath+" downscaled by "+xScale+","+ yScale+".");
			BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = newImage.createGraphics();
			g2d.drawRenderedImage(bufferedImage, transform);
			bufferedImage = newImage;
			}
		*/
        // Get a pointer to the image memory
        ByteBuffer scratch = BufferUtils.createByteBuffer(4 * width * height);
        scratch.clear();
        scratch.put(imageData);
        scratch.flip();
        Image textureImage = new Image();
        textureImage.setDataFormat(ImageDataFormat.RGBA);
        textureImage.setWidth(width);
        textureImage.setHeight(height);
        textureImage.setData(scratch);
        return textureImage;
		}

	/**
	 * Get the header from the file
	 *
	 * @param buffer the buffer containing the file data
	 * @return a HashMap of the header key/value pairs
	 */
	private Map<String,Object> getHeader(byte[] buffer) {
		Map<String, Object> headerMap = new HashMap<String, Object>();
		try {
			magic = bytes2Short(buffer[0], buffer[1]);
			storage = buffer[2];
			headerMap.put("storage", new Integer(storage));
			bpc = buffer[3];
			headerMap.put("precision", new Integer(bpc));
			dimension = bytes2Int(buffer[4], buffer[5]);
			headerMap.put("dimension", new Integer(dimension));
			width = bytes2Int(buffer[6], buffer[7]);
			headerMap.put("width", new Integer(width));
			height = bytes2Int(buffer[8], buffer[9]);
			headerMap.put("height", new Integer(height));
			bands = bytes2Int(buffer[10], buffer[11]);
			headerMap.put("bands", new Integer(bands));
			pixMin = bytes2Int(buffer[12], buffer[13], buffer[14],
				buffer[15]);
			headerMap.put("minimum Pixel Value", new Integer(pixMin));
			pixMax = bytes2Int(buffer[16], buffer[17], buffer[18], buffer[19]);
			headerMap.put("maximum Pixel Value", new Integer(pixMax));
			if (buffer[24] == 0)
				imageName = new String("");
			else
				imageName = new String(buffer, 24, 79);
			headerMap.put("image name", imageName);
			colorMapID = bytes2Int(buffer[104], buffer[105], buffer[106],
				buffer[107]);
			headerMap.put("color map id", new Integer(colorMapID));
			return(headerMap);
			}
		catch (Exception e) {
			e.printStackTrace();
			return(null);
			}
		}

	/**
	 * Load from a non-RLE encoded file
	 *
	 * @param imageIntData the array to contain the image data
	 * @param buffer the file data
	 * @param bufferIndex the current location in the buffer
	 */
	private void loadVerbatim(byte[] imageData, byte[] buffer, int bufferIndex) {
		for (int i=0; i<bands; ++i) {
			if (bpc == 1) {
				for (int j=0; j<dataCount; ++j)
					imageData[j*4+i] = buffer[bufferIndex+j];
				}
			else if (bpc == 2) {
				int k = 0;
				int gray = pixMax-pixMin;
				for (int j=0; j<dataCount; ++j) {
					int val = bytes2Int(buffer[bufferIndex+k], buffer[bufferIndex+k+1]);
					k += 2; 
					imageData[j*4+i] = (byte)((int)(((val-pixMin)/gray)*255.0+0.5));
					}
				}
			bufferIndex += dataCount*bpc;
			}
		}

	/**
	 * Load from an RLE encoded file
	 *
	 * @param imageIntData the array to contain the image data
	 * @param buffer the file data
	 * @param bufferIndex the current location in the buffer
	 */
	private void loadRLE(byte[] imageData, byte[] buffer, int bufferIndex) {
		int[][] startTable = new int[bands][height];
		int[][] lengthTable = new int[bands][height];
		for (int i=0; i<bands; ++i) {
			int k = 0;
			for (int h=0; h<height; ++h) {
				startTable[i][h] = bytes2Int(buffer[bufferIndex+k], buffer[bufferIndex+k+1],
					buffer[bufferIndex+k+2], buffer[bufferIndex+k+3]);
				k += 4;
				}
			bufferIndex += height*4;
			}
		for (int i=0; i<bands; ++i) {
			int k = 0;
			for (int h=0; h<height; ++h) {
				lengthTable[i][h] = bytes2Int(buffer[bufferIndex+k], buffer[bufferIndex+k+1],
					buffer[bufferIndex+k+2], buffer[bufferIndex+k+3]);
				k += 4;
				}
			bufferIndex += height*4;
			}
		int[] scanLine = new int[width];
		for (int i=0; i<bands; ++i) {
			for (int h=0; h<height; ++h) {
				int rleStart = startTable[i][h];
				int rleLength = lengthTable[i][h];
				expandRow(rleStart, rleLength, buffer, scanLine);
				int k = h*width*4;
				for (int j=0; j<width; ++j) {
// TODO: this only works for bpc = 1
					imageData[k+j*4+i] = (byte)scanLine[j];
					}
				}
			}
		}

	/**
	 * Expand an RLE encoded row
	 *
	 * @param start the start position in the buffer
	 * @param length the length of the data for the row
	 * @param buffer the file data
	 * @param scanLine the array for the expanded data
	 */
	private void expandRow(int start, int length, byte[] buffer, int[] scanLine) {
		int j = start;
		if (bpc == 2)
			j ++;
		int k = 0;
		while (true) {
			if (j >= start+length)
				return;
			int count = (buffer[j] & 0x7f);
			if (count == 0)
				return;
			if ((buffer[j] & 0x80) == 0x80) {
				j ++;
				for (int i=0; i<count; ++i) {
					if (bpc == 1) {
						scanLine[k] = (buffer[j] & 0xff);
						j ++;
						}
					else if (bpc == 2) {
						scanLine[k] = bytes2Int(buffer[j], buffer[j+1]);
						j += 2;
						}
					k ++;
					}
				}
			else {
				j ++;
				for (int i=0; i<count; ++i) {
					if (bpc == 1) {
						scanLine[k] = (buffer[j] & 0xff);
						}
					else if (bpc == 2) {
						scanLine[k] = bytes2Int(buffer[j], buffer[j+1]);
						}
					k ++;
					}
				if (bpc == 1)
					j ++;
				else if (bpc == 2)
					j += 2;
				}
			}
		}

	/**
	 * Convert two bytes to a short.
	 *
	 * @param b0 the first byte
	 * @param b1 the second byte
	 * @return the short value
	 */
	private short bytes2Short(byte b0, byte b1) {
		return((short)(((b0 & 0xff) << 8) | (b1 & 0xff)));
		}

	/**
	 * Convert two bytes to an int.
	 *
	 * @param b0 the first byte
	 * @param b1 the second byte
	 * @return the int value
	 */
	private int bytes2Int(byte b0, byte b1) {
		return(((b0 & 0xff) << 8) | (b1 & 0xff));
		}

	/**
	 * Convert two bytes to an int.
	 *
	 * @param b0 the first byte
	 * @param b1 the second byte
	 * @param b2 the third byte
	 * @param b3 the fourth byte
	 * @return the int value
	 */
	private int bytes2Int(byte b0, byte b1, byte b2, byte b3) {
		return(((b0 & 0xff) << 24) | ((b1 & 0xff) << 16) | ((b2 & 0xff) << 8) | (b3 & 0xff));
		}
	}
