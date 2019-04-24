package istat.android.base.security;

import java.io.UnsupportedEncodingException;

public class RC4Utils {

    public static String encrypt(String clear, String password) throws CryptoException {
        return new String(encrypt(clear.getBytes(), password));
    }

    public static String decrypt(String encrypted, String password) throws CryptoException {
        return new String(decrypt(encrypted.getBytes(), password));
    }

    public static String encrypt(String encoding, String clear, String password) throws CryptoException, UnsupportedEncodingException {
        return new String(encrypt(clear.getBytes(encoding), password), encoding);
    }

    public static String decrypt(String encoding, String clear, String password) throws CryptoException, UnsupportedEncodingException {
        return new String(decrypt(clear.getBytes(encoding), password), encoding);
    }

    public static byte[] encrypt(byte[] bytes, String password) throws CryptoException {
        RC4 rc4 = new RC4();
        rc4.engineInitEncrypt(password.getBytes());
        byte[] cryptedBytes = rc4.crypt(bytes);
        return cryptedBytes;
    }

    public static byte[] decrypt(byte[] bytes, String password) throws CryptoException {
        RC4 rc4 = new RC4();
        rc4.engineInitDecrypt(password.getBytes());
        byte[] decryptBytes = rc4.crypt(bytes);
        return decryptBytes;
    }
}
