package gov.nasa.freeflyer.test.pde.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;

public class PDETestPortLocator {

    public static void main(String[] args) {
        new PDETestPortLocator().savePortToFile();
    }

    public void savePortToFile() {
        int port = locatePDETestPortNumber();
        File propsFile = new File("pde_test_port.properties");
        System.out.println("PDE Test port: " + port);
        OutputStream os = null;
        try {
            os = new FileOutputStream(propsFile);
            os.write(new String("pde.test.port=" + port).getBytes());
            os.flush();
            System.out.println("PDE Test port saved to file " + propsFile.getAbsolutePath());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ioe) {
                    // ignore
                }
            }
            os = null;
        }
    }

    private int locatePDETestPortNumber() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } catch (IOException e) {
            // ignore
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return -1;
    }
}
