package istat.android.base.security;

import android.util.Base64;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {

    public static PublicKey readPublicKey(byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey readPrivateKey(byte[] privateKeyBytes) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    @Deprecated
    public static byte[] encryptString(String data, byte[] publicKeyBytes) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        return encryptString(data, readPublicKey(publicKeyBytes));
    }

    @Deprecated
    public static byte[] encryptString(String data, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    @Deprecated
    public static String decryptToString(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return new String(decrypt(data, privateKey));
    }

    @Deprecated
    public static String decryptToString(String data, byte[] privateKeyBytes) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decryptToString(Base64.decode(data.getBytes(), Base64.NO_WRAP), readPrivateKey(privateKeyBytes));
    }

    public static byte[] encrypt(byte[] data, byte[] publicKeyBytes) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        return encrypt(data, readPublicKey(publicKeyBytes));
    }

    public static byte[] encrypt(byte[] data, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] privateKeyBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        return decrypt(data, readPrivateKey(privateKeyBytes));
    }

    public static byte[] sign(byte[] plainTextBytes, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainTextBytes);
        byte[] signature = privateSignature.sign();
        return signature;
    }

    public static boolean verify(byte[] plainTextBytes, byte[] signatureBytes, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainTextBytes);
        return publicSignature.verify(signatureBytes);
    }

    public static KeyPair generateKeyPair(int length) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(length);
        return keyGen.generateKeyPair();
    }

    public static KeyPair generateKeyPair(InputStream JavaKeyStoreInputStream, String alias, String password) throws Exception {
        //Generated with:
        //  keytool -genkeypair -alias mykey -storepass s3cr3t -keypass s3cr3t -keyalg RSA -keystore keystore.jks
        char[] passwordChars = password != null ? password.toCharArray() : new char[]{};

        KeyStore keyStore = KeyStore.getInstance("JCEKS"); // ou "PKCS12"
        keyStore.load(JavaKeyStoreInputStream, passwordChars);   //Keystore password
        KeyStore.PasswordProtection keyPassword =       //Key password
                new KeyStore.PasswordProtection(passwordChars);

        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, keyPassword);

        java.security.cert.Certificate cert = keyStore.getCertificate(alias);
        PublicKey publicKey = cert.getPublicKey();
        PrivateKey privateKey = privateKeyEntry.getPrivateKey();

        return new KeyPair(publicKey, privateKey);
    }
//
//        public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
//            RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
//            keyPairGenerator.writeToFile("RSA/publicKey", keyPairGenerator.getPublicKey().getEncoded());
//            keyPairGenerator.writeToFile("RSA/privateKey", keyPairGenerator.getPrivateKey().getEncoded());
//            System.out.println(Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded()));
//            System.out.println(Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded()));
//        }

}
