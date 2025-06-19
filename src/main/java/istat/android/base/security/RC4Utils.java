package istat.android.base.security;

import java.io.UnsupportedEncodingException;

public class RC4Utils {

    public static String encryptString(String clear, String password) throws CryptoException {
        return new String(encryptByte(clear.getBytes(), password));
    }

    public static String decryptString(String encrypted, String password) throws CryptoException {
        return new String(decryptByte(encrypted.getBytes(), password));
    }

    public static String encryptString(String encoding, String clear, String password) throws CryptoException, UnsupportedEncodingException {
        return new String(encryptByte(clear.getBytes(encoding), password), encoding);
    }

    public static String decryptString(String encoding, String clear, String password) throws CryptoException, UnsupportedEncodingException {
        return new String(decryptByte(clear.getBytes(encoding), password), encoding);
    }

    public static byte[] encryptByte(byte[] bytes, String password) throws CryptoException {
        RC4 rc4 = new RC4();
        rc4.engineInitEncrypt(password.getBytes());
        return rc4.crypt(bytes);
    }

    public static byte[] decryptByte(byte[] bytes, String password) throws CryptoException {
        RC4 rc4 = new RC4();
        rc4.engineInitDecrypt(password.getBytes());
        return rc4.crypt(bytes);
    }
}
