package istat.android.base.security;

import java.security.spec.KeySpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

@Deprecated
public class AESUtils {
    public final static int SALT_LEN = 8;
    static byte[] mSalt = "qmaker-salt".getBytes();//new byte[SALT_LEN];//createRandomBytes(SALT_LEN);
    private static final int KEY_LENGTH_BITS = 128;    // see notes below where this is used.
    private static final int ITERATIONS = 50;
    final static String CIPHER_ALGORITHM = "AES", SECRET_ALGORITHM = "AES";

//    private static final byte[] keyValue =
//            new byte[]{'c', 'o', 'd', 'i', 'n', 'g', 'a', 'f', 'f', 'a', 'i', 'r', 's', 'c', 'o', 'm'};

    public static String encrypt(byte[] clearBytes, String password)
            throws Exception {
        byte[] rawKey = createBytePassword(password);
        byte[] result = encrypt(rawKey, clearBytes);
        return toHex(result);
    }

    public static String encrypt(String cleartext, String password)
            throws Exception {
        return encrypt(cleartext.getBytes(), password);
    }

    private static byte[] createBytePassword(String password) {
        return createBytePassword(password, 16);
    }

    private static byte[] createBytePassword(String password, int length) {
        char[] chars = password.toCharArray();
        byte[] bytes = new byte[chars.length > length ? chars.length : length];
        for (int i = 0; i < length; i++) {
            bytes[i] = chars.length > i ? (byte) chars[i] : 0;
        }
        return bytes;
    }

    private static byte[] createRandomBytes(int length) {
        byte[] bytes = new byte[length];
        char[] chars = HEX.toCharArray();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) chars[random.nextInt(length - 1)];
        }
        return bytes;
    }

    public static String decrypt(byte[] encryptedStringBytes, String password)
            throws Exception {

        byte[] enc = toByte(new String(encryptedStringBytes));
        byte[] result = decryptHexBytes(enc, password);
        return new String(result);
    }

    public static String decrypt(String encrypted, String password)
            throws Exception {

        byte[] enc = toByte(encrypted);
        byte[] result = decryptHexBytes(enc, password);
        return new String(result);
    }

    private static SecretKey getSecretKey(byte[] keyValue) throws Exception {
        SecretKeyFactory factory;
        SecretKey tmp;
        SecretKey secret;

        factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(new String(keyValue).toCharArray(), mSalt, ITERATIONS, KEY_LENGTH_BITS);

        tmp = factory.generateSecret(spec);
        secret = new SecretKeySpec(tmp.getEncoded(), "AES");
        return secret;
    }


    private static byte[] encrypt(byte[] key, byte[] clear) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKey skeySpec = getSecretKey(key);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decryptHexBytes(byte[] encrypted, String password)
            throws Exception {
        SecretKey skeySpec = getSecretKey(createBytePassword(password));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}