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
package gov.nasa.arc.verve.common.ardor3d.framework.screenshot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.log4j.Logger;

import com.ardor3d.image.ImageDataFormat;
import com.ardor3d.util.screen.ScreenExportable;

public class ScreenShotImageExporter implements ScreenExportable {
    private static final Logger logger = Logger.getLogger(ScreenShotImageExporter.class);

    protected String  _directoryName;
    protected String  _prepend;
    protected String  _fileFormat;
    protected boolean _useAlpha;

    //protected File    _lastFile;
    protected String  _lastFileName = null;

    protected static ImageWriteParam jpegParams;

    static {
        Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter jpegWriter = (ImageWriter)iter.next();
        jpegParams = jpegWriter.getDefaultWriteParam();
        jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpegParams.setCompressionQuality(0.94f);
    }

    /**
     * Make a new exporter with the default settings:
     * 
     * <pre>
     * directory: local working directory
     * prepend: &quot;capture_&quot;
     * format: &quot;png&quot;
     * useAlpha: false
     * </pre>
     */
    public ScreenShotImageExporter() {
        this(new File(System.getProperty("user.home")), "capture_", "png", false);
    }

    /**
     * Construct a new exporter.
     * 
     * @param directory
     *            the directory to save the screen shots in.
     * @param prepend
     *            a value to prepend onto the generated file name. This must be at least 3 characters long.
     * @param format
     *            the format to use for saving the image. ImageIO is used for this, so safe values are likely: "png",
     *            "jpg", "gif" and "bmp"
     * @param useAlpha
     *            true for alpha values to be stored in image (as applicable, depending on the given format)
     */
    public ScreenShotImageExporter(final File directory, final String prepend, final String format,
                                   final boolean useAlpha) {
        _directoryName = directory.getAbsolutePath();
        _prepend    = prepend;
        _fileFormat = format;
        _useAlpha   = useAlpha;
    }

    ImageWriter jpegWriter = null;
    @Override
    public void export(final ByteBuffer data, final int width, final int height) {
        final BufferedImage img = new BufferedImage(width, height, _useAlpha ? BufferedImage.TYPE_INT_ARGB
                                                                             : BufferedImage.TYPE_INT_RGB);

        int index, r, g, b, a;
        int argb;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                //index = (_useAlpha ? 4 : 3) * ((height - y - 1) * width + x);
                index = 4 * ((height - y - 1) * width + x);
                r = ((data.get(index + 0)));
                g = ((data.get(index + 1)));
                b = ((data.get(index + 2)));

                argb = ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);

                if (_useAlpha) {
                    a = ((data.get(index + 3)));
                    argb |= (a & 0xFF) << 24;
                }

                img.setRGB(x, y, argb);
            }
        }

        try {
            //final String filename = _prepend + System.currentTimeMillis() + "." + _fileFormat;
            final String filename = _prepend + "." + _fileFormat;
            final File dir = new File(_directoryName);
            final File out = new File(dir, filename);
            //logger.debug("Taking screenshot: " + out.getAbsolutePath());

            // write out the screen shot image to a file.
            if(_fileFormat.equals("jpg")) {
                if(jpegWriter == null) {
                    Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
                    jpegWriter = (ImageWriter)iter.next();
                }
                ImageOutputStream outStream = ImageIO.createImageOutputStream(out); 
                jpegWriter.setOutput(outStream);
                IIOImage image = new IIOImage(img, null, null);
                jpegWriter.write(null, image, jpegParams);
                jpegWriter.reset();
                outStream.close();
                //jpegWriter.dispose();
            }
            else {
                ImageIO.write(img, _fileFormat, out);
            }

            // save our successful file to be accessed as desired.
            _lastFileName = out.getAbsolutePath();
        } catch (final IOException e) {
            logger.warn("export(ByteBuffer, int, int)", e);
        }
    }

    @Override
    public ImageDataFormat getFormat() {
        return ImageDataFormat.RGBA;
        //
        //		if (_useAlpha) {
        //			return Format.RGBA8;
        //		} else {
        //			return Format.RGB8;
        //		}
    }

    /**
     * @return the last filename written by this exporter, or null if none were written.
     */
    public String getLastFileName() {
        return _lastFileName;
    }

    public String getDirectoryName() {
        return _directoryName;
    }

    public void setDirectoryName(String directoryName) {
        _directoryName = directoryName;
    }

    public String getPrepend() {
        return _prepend;
    }

    public void setPrepend(final String prepend) {
        _prepend = prepend;
    }

    public boolean isUseAlpha() {
        return _useAlpha;
    }

    public void setUseAlpha(final boolean useAlpha) {
        _useAlpha = useAlpha;
    }

    public String getFileFormat() {
        return _fileFormat;
    }

    public void setFileFormat(final String format) {
        _fileFormat = format;
    }
}
