package istat.android.base.security;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static byte[] createChecksum(byte[] bytes) throws NoSuchAlgorithmException, IOException {
        return createChecksum(new ByteArrayInputStream(bytes));
    }

    public static byte[] createChecksum(InputStream fis) throws NoSuchAlgorithmException, IOException {
        return createChecksum(fis, true);
    }

    public static byte[] createChecksum(InputStream fis, boolean closeStream) throws NoSuchAlgorithmException, IOException {
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        if (closeStream) {
            fis.close();
        }
        return complete.digest();
    }

    public static byte[] createChecksum(File file) throws NoSuchAlgorithmException, IOException {
        InputStream fis = new FileInputStream(file);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String computeMd5Checksum(File file) throws NoSuchAlgorithmException, IOException {
        byte[] b = createChecksum(file);
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static String computeMd5Checksum(byte[] bytes) throws NoSuchAlgorithmException, IOException {
        byte[] b = createChecksum(bytes);
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static String computeMd5Checksum(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        return computeMd5Checksum(inputStream, true);
    }

    public static String computeMd5Checksum(InputStream inputStream, boolean closeStream) throws NoSuchAlgorithmException, IOException {
        byte[] b = createChecksum(inputStream, closeStream);
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static void main(String args[]) {
        try {
            System.out.println(computeMd5Checksum(new File("apache-tomcat-5.5.17.exe")));
            // output :
            //  0bb2827c5eacf570b6064e24e0e6653b
            // ref :
            //  http://www.apache.org/dist/
            //          tomcat/tomcat-5/v5.5.17/bin
            //              /apache-tomcat-5.5.17.exe.MD5
            //  0bb2827c5eacf570b6064e24e0e6653b *apache-tomcat-5.5.17.exe
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}