package istat.android.base.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    final static String CIPHER_ALGORITHM = "AES";
    final static int DEFAULT_SALT_LENGTH = 8;
    public static final int DEFAULT_KEY_LENGTH_BITS = 128;    // see notes below where this is used.
    public static final int DEFAULT_ITERATIONS = 65536;
    public static final int DEFAULT_MAX_FILE_BUF = 1024;
    static final byte[] DEFAULT_SALT = new byte[DEFAULT_SALT_LENGTH];
    byte[] salt = null;
    private int keyLengthBits = DEFAULT_KEY_LENGTH_BITS;    // see notes below where this is used.
    private int iterations = DEFAULT_ITERATIONS;
    private int bufferSize = DEFAULT_MAX_FILE_BUF;

    public AES() {
        this(null);
    }

    public AES(String salt) {
        if (salt != null) {
            setSalt(salt);
        }
    }

    public byte[] getSalt() {
        if (salt == null || salt.length == 0) {
            // crate secureRandom salt and store  as member var for later use
            salt = new byte[DEFAULT_SALT_LENGTH];
//            SecureRandom rnd = new SecureRandom();
//            rnd.nextBytes(salt);
        }
        return salt;
    }

    public AES setSalt(String salt) {
        this.salt = salt != null ? salt.getBytes() : DEFAULT_SALT;
        return this;
    }

    public AES setSalt(byte[] salt) {
        this.salt = salt != null ? salt : DEFAULT_SALT;
        return this;
    }

    public AES setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    public AES setIterations(int iterations) {
        this.iterations = iterations;
        return this;
    }

    public AES setKeyLengthBits(int keyLengthBits) {
        this.keyLengthBits = keyLengthBits;
        return this;
    }

    public int getKeyLengthBits() {
        return keyLengthBits;
    }

    public int getIterations() {
        return iterations;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public static String byteToHex(byte[] raw) {
        if (raw == null) {
            return null;
        }

        final StringBuilder hex = new StringBuilder(2 * raw.length);

        for (final byte b : raw) {
            hex.append(HEX.charAt((b & 0xF0) >> 4)).append(HEX.charAt((b & 0x0F)));
        }

        return hex.toString();
    }

    public static byte[] hexToByte(String hexString) {
        int len = hexString.length();
        byte[] ba = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            ba[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }

        return ba;
    }

    /**
     * debug/print messages
     *
     * @param msg
     */
    private void Db(String msg) {
        System.out.println("** Crypt ** " + msg);
    }

    /**
     * This is where we write out the actual encrypted data to disk using the Cipher created in setupEncrypt().
     * Pass two file objects representing the actual input (cleartext) and output file to be encrypted.
     * <p>
     * there may be a way to write a cleartext header to the encrypted file containing the salt, but I ran
     * into uncertain problems with that.
     *
     * @param inputStream  - the cleartext file to be encrypted
     * @param outputStream - the encrypted data file
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public void encryptStream(String password, InputStream inputStream, OutputStream outputStream)
            throws IOException, IllegalBlockSizeException, BadPaddingException {
        try {
            Cipher mEcipher;
            byte[] mInitVec;
            long totalread = 0;
            int nread = 0;
            byte[] inbuf = new byte[bufferSize];
            SecretKeyFactory factory;
            SecretKey tmp;
            byte[] salt = getSalt();
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            /*
             *  Derive the key, given password and salt.
             *
             * in order to do 256 bit crypto, you have to muck with the files for Java's "unlimted security"
             * The end user must also install them (not compiled in) so beware.
             * see here:  http://www.javamex.com/tutorials/cryptography/unrestricted_policy_files.shtml
             */
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLengthBits);

            tmp = factory.generateSecret(spec);

            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            /*
             *  Create the Encryption cipher object and store as a member variable
             */
            mEcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            mEcipher.init(Cipher.ENCRYPT_MODE, secret);

            AlgorithmParameters params = mEcipher.getParameters();

            // get the initialization vectory and store as member var
            mInitVec = params.getParameterSpec(IvParameterSpec.class).getIV();
            Db("mInitVec is :" + byteToHex(mInitVec));
            outputStream.write(salt);
            outputStream.write(mInitVec);

            while ((nread = inputStream.read(inbuf)) > 0) {
                Db("read " + nread + " bytes");
                totalread += nread;

                // create a buffer to write with the exact number of bytes read. Otherwise a short read fills inbuf with 0x0
                // and results in full blocks of bufferSize being written.
                byte[] trimbuf = new byte[nread];

                for (int i = 0; i < nread; i++) {
                    trimbuf[i] = inbuf[i];
                }

                // encryptStream the buffer using the cipher obtained previosly
                byte[] tmpBuf = mEcipher.update(trimbuf);

                // I don't think this should happen, but just in case..
                if (tmpBuf != null) {
                    outputStream.write(tmpBuf);
                }
            }

            // finalize the encryption since we've done it in blocks of bufferSize
            byte[] finalbuf = mEcipher.doFinal();

            if (finalbuf != null) {
                outputStream.write(finalbuf);
            }

            outputStream.flush();
            inputStream.close();
            outputStream.close();
            outputStream.close();
            Db("wrote " + totalread + " encrypted bytes");
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidParameterSpecException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Read from the encrypted file (input) and turn the cipher back into cleartext. Write the cleartext buffer back out
     * to disk as (output) File.
     * <p>
     * I left CipherInputStream in here as a test to see if I could mix it with the update() and final() methods of encrypting
     * and still have a correctly decrypted file in the end. Seems to work so left it in.
     *
     * @param inputStream  - File object representing encrypted data on disk
     * @param outputStream - File object of cleartext data to write out after decrypting
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    public void decryptStream(String password, InputStream inputStream, OutputStream outputStream)
            throws IllegalBlockSizeException, BadPaddingException, IOException {
        try {
            Cipher mDecipher;
            byte[] mInitVec;
            CipherInputStream cin;
            long totalread = 0;
            int nread = 0;
            byte[] inbuf = new byte[bufferSize];
            byte[] salt = getSalt();
            // Read the Salt
            inputStream.read(salt);

            SecretKeyFactory factory;
            SecretKey tmp;
            SecretKey secret;

            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLengthBits);

            tmp = factory.generateSecret(spec);
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            /* Decrypt the message, given derived key and initialization vector. */
            mDecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            // Set the appropriate size for mInitVec by Generating a New One
            AlgorithmParameters params = mDecipher.getParameters();

            mInitVec = params.getParameterSpec(IvParameterSpec.class).getIV();

            // Read the old IV from the file to mInitVec now that size is set.
            inputStream.read(mInitVec);
            Db("mInitVec is :" + byteToHex(mInitVec));
            mDecipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(mInitVec));

            // creating a decoding stream from the FileInputStream above using the cipher created from setupDecrypt()
            cin = new CipherInputStream(inputStream, mDecipher);

            while ((nread = cin.read(inbuf)) > 0) {
                Db("read " + nread + " bytes");
                totalread += nread;

                // create a buffer to write with the exact number of bytes read. Otherwise a short read fills inbuf with 0x0
                byte[] trimbuf = new byte[nread];

                for (int i = 0; i < nread; i++) {
                    trimbuf[i] = inbuf[i];
                }

                // write out the size-adjusted buffer
                outputStream.write(trimbuf);
            }

            outputStream.flush();
            cin.close();
            inputStream.close();
            outputStream.close();
            Db("wrote " + totalread + " encrypted bytes");
        } catch (Exception ex) {
            Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String encryptByte(byte[] clearBytes, String password)
            throws Exception {
        byte[] rawKey = createBytePassword(password);
        byte[] result = encryptByte(rawKey, clearBytes);
        return toHex(result);
    }

    public String encryptString(String cleartext, String password)
            throws Exception {
        return encryptByte(cleartext.getBytes(), password);
    }

    private byte[] createBytePassword(String password) {
        return createBytePassword(password, 16);
    }

    private byte[] createBytePassword(String password, int length) {
        char[] chars = password.toCharArray();
        byte[] bytes = new byte[chars.length > length ? chars.length : length];
        for (int i = 0; i < length; i++) {
            bytes[i] = chars.length > i ? (byte) chars[i] : 0;
        }
        return bytes;
    }

    private byte[] createRandomBytes(int length) {
        byte[] bytes = new byte[length];
        char[] chars = HEX.toCharArray();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) chars[random.nextInt(length - 1)];
        }
        return bytes;
    }

    public String decryptByte(byte[] encryptedStringBytes, String password)
            throws Exception {

        byte[] enc = toByte(new String(encryptedStringBytes));
        byte[] result = decryptHexBytes(enc, password);
        return new String(result);
    }

    public String decryptString(String encrypted, String password)
            throws Exception {

        byte[] enc = toByte(encrypted);
        byte[] result = decryptHexBytes(enc, password);
        return new String(result);
    }

    private SecretKey getSecretKey(byte[] keyValue) throws Exception {
        SecretKeyFactory factory;
        SecretKey tmp;
        SecretKey secret;

        factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(new String(keyValue).toCharArray(), getSalt(), iterations, keyLengthBits);

        tmp = factory.generateSecret(spec);
        secret = new SecretKeySpec(tmp.getEncoded(), CIPHER_ALGORITHM);
        return secret;
    }


    private byte[] encryptByte(byte[] key, byte[] clear) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        SecretKey skeySpec = getSecretKey(key);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(clear);
    }

    private byte[] decryptHexBytes(byte[] encrypted, String password)
            throws Exception {
        SecretKey skeySpec = getSecretKey(createBytePassword(password));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(encrypted);
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
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