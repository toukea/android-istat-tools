package istat.android.base.tools;


import android.util.Base64;

import com.google.gson.Gson;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import istat.android.base.security.RSA;
import istat.android.base.security.SimpleCrypto;
import istat.android.base.utils.HtmlStringUtils;
import istat.android.base.utils.ListWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void htmlEcapeTest() {
        String source = "bonjour les ami: \"Helooow\"";
        String htmlStringUtilEscape = HtmlStringUtils.encode(source);
        String htmlStringUtilDecode = HtmlStringUtils.decode(htmlStringUtilEscape);
        assertEquals(source, htmlStringUtilDecode);
    }

    @Test
    public void decodeTopGradeJsonString() throws FileNotFoundException {
        String source = ToolKits.Stream.streamToString(new FileInputStream("/home/istat/Temp/TopGradePseudoJson.json"));
        String htmlStringUtilEscape = HtmlStringUtils.encode(source);
        String htmlStringUtilDecode = HtmlStringUtils.decode(htmlStringUtilEscape);
        assertEquals(source, htmlStringUtilDecode);
    }

    @Test
    public void encryptTest() throws Exception {
        String password = "hellompkolkiopml";
        String wordToEncrypt = "bonjour mama comment vas tu? bisous";
        String cryptat = SimpleCrypto.encrypt(password, wordToEncrypt);
        String decode = SimpleCrypto.decrypt(password, cryptat);
        assertEquals(decode, wordToEncrypt);

    }

    @Test
    public void listProviders() throws Exception {
        Provider[] providers = Security.getProviders();
        for (Provider p : providers) {
            System.out.println("provider: " + p.getName() + ", " + p);
        }
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        MyList list = new MyList();
        String toJson = list.toJson();
        System.out.println(toJson);
    }

    @Test
    public void matches() throws Exception {
        String pattern = ".*";
        String text = "{\"Pharmacies\":{";//"{\n\t\"Pharmacies\":{\n\t";
        boolean matches = text.matches(pattern);
        assertTrue(matches);
    }

    @Test
    public void stringValue() throws Exception {
        CharSequence charSequence = null;
        String stringValue = String.valueOf(charSequence);
        assertTrue(stringValue == null);
    }

    /*
    {
				"id_pharmacie":"15",
				"nom_pharmacie":"PHARMACIE SAINT HERMANN",
				"adresse":"Dr AGBASSI N'SOUA HIPPOL \nBASE CIE - ENTREE FACE STATION PETRO IVOIRE",
				"idville":"1",
				"idcommune":"1",
				"ville":"ABIDJAN",
				"commune":"YOPOUGON",
				"bons":"",
				"latitude":"5.335111",
				"longitude":"-4.090890",
				"contacts":"23 50 72 77 - 03 38 74 91",
				"status":"0",
				"distance":"-1"
			}
     */
    @Test
    public void TestFinder() throws Exception {
        TextFinder finder = new TextFinder("\\{.*\\}");
    }

    class MyList extends ListWrapper<String> implements List<String> {
        public MyList() {
            super();
            add("Hello");
            add("world");
            add("dady");
            add("polo");
        }

        public String toJson() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }


    @Test
    public void encryptDecryptRSA() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, InvalidKeySpecException, BadPaddingException, SignatureException {
        KeyPair keyPair = RSA.generateKeyPair(512);
        String original = "papa est parti au travail !";
        System.out.println("original=" + original);
        byte[] encryptedBytes = RSA.encrypt(original, keyPair.getPublic());
        String encryptedString = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
        System.out.println("encrypted64=" + encryptedString);
        String decryptString = RSA.decrypt(Base64.decode(encryptedString, Base64.NO_WRAP), keyPair.getPrivate());
        System.out.println("decrypted=" + decryptString);
        byte[] signature = RSA.sign(original.getBytes(), keyPair.getPrivate());
        System.out.println(Base64.encodeToString(signature, Base64.NO_WRAP));
        boolean verified = RSA.verify(original.getBytes(), signature, keyPair.getPublic());
        System.out.println("verified=" + verified);


    }

    @Test
    public void copyDirectoryTest() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, InvalidKeySpecException, BadPaddingException, SignatureException, IOException {
        File source = new File("/home/istat/Temp/qproject/");
        File destination = new File("/home/istat/Temp/qproject-copy/");
        int copiedCount = ToolKits.FileKits.copyDirectory(source, destination);
        assertTrue(copiedCount > 0);
    }
    @Test
    public void moveDirectoryTest() throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, InvalidKeySpecException, BadPaddingException, SignatureException, IOException {
        File source = new File("/home/istat/Temp/qproject-copy/");
        File destination = new File("/home/istat/Temp/qproject-copy-2/");
        int copiedCount = ToolKits.FileKits.moveDirectory(source, destination);
        assertTrue(copiedCount > 0);
    }
}