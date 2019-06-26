package gov.nasa.freeflyer.test.helper;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

public class TestData {

	public static File getTestFile(final String bundleName, final String fileName) {
		try {
			final File testFile = new File(FileLocator.getBundleFile(
					Platform.getBundle(bundleName)).getAbsolutePath()
					+ File.separator
					+ "testdata"
					+ File.separator + fileName);
			if (testFile.exists()) {
				return testFile;
			}
		} catch (final Exception e) {}

		// default location
		return new File("testdata" + File.separator + fileName);
	}
	
	public static String createFileName(final String bundleName, final String fileName) {
		try {
			return FileLocator.getBundleFile(
					Platform.getBundle(bundleName)).getAbsolutePath()
					+ File.separator
					+ "testdata"
					+ File.separator + fileName;
		} catch (final Exception e) {}

		// default location
		return "testdata" + File.separator + fileName;
	}

}