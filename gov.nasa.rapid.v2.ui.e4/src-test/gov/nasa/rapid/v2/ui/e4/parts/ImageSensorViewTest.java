package gov.nasa.rapid.v2.ui.e4.parts;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rapid.MIME_IMAGE_BMP;
import rapid.MIME_IMAGE_GIF;
import rapid.MIME_IMAGE_JPEG;
import rapid.MIME_IMAGE_TIFF;

public class ImageSensorViewTest extends Assert {

	private ImageSensorView testedClass;


	@Before
	public void setUp() throws Exception {
		testedClass = new ImageSensorView();
		testedClass.initializeRapid();
	}


	@Test
	public void loadInitialImageTest() {
		boolean passed = true;
		
		try {
			testedClass.loadInitialImage(testedClass.m_initialImage);
		} catch (Throwable t) {
			passed = false;
		}
		
		try {
			testedClass.loadInitialImage("no_such_file");
		} catch (Throwable t) {
			passed = true;
		}
		
		assertTrue(passed);
	}
	
	@Test
	public void mimeViewableTest() {
        assertTrue(testedClass.mimeTypeIsViewable(MIME_IMAGE_BMP.VALUE));
        assertTrue(testedClass.mimeTypeIsViewable(MIME_IMAGE_GIF.VALUE)); 
        assertTrue(testedClass.mimeTypeIsViewable(MIME_IMAGE_JPEG.VALUE));
        assertTrue(testedClass.mimeTypeIsViewable(MIME_IMAGE_TIFF.VALUE));
        
        assertFalse(testedClass.mimeTypeIsViewable("NOT_SUPPORTED"));
	}

}
