package istat.android.base.tools;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.text.Html;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.Provider;
import java.security.Security;

import istat.android.base.security.SimpleCrypto;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("istat.android.base.tools.test", appContext.getPackageName());
        String source= "bonjour les ami: \"Helooow\"";
        String escaped= Html.escapeHtml(source);
        System.out.println(escaped);
    }

    @Test
    public void encryptTest() throws Exception {
        String password = "hello";
        String wordToEncrypt = "bonjour mama comment vas tu? bisous";
        String cryptat = SimpleCrypto.encrypt(password, wordToEncrypt);
        String decode = SimpleCrypto.decrypt(password, cryptat);
        assertEquals(decode, wordToEncrypt);

    }

    @Test
    public void listProviders() throws Exception {
        Provider[] providers = Security.getProviders();
        for (Provider p : providers) {
            System.out.println("provider: " + p.getName()+", "+p);
        }
    }
    /*
    provider: AndroidKeyStoreBCWorkaround, AndroidKeyStoreBCWorkaround version 1.0
              provider: AndroidOpenSSL, AndroidOpenSSL version 1.0
              provider: BC, BC version 1.52
              provider: Crypto, Crypto version 1.0
              provider: HarmonyJSSE, HarmonyJSSE version 1.0
              provider: AndroidKeyStore, AndroidKeyStore version 1.0



    provider: SUN, SUN version 1.8
provider: SunRsaSign, SunRsaSign version 1.8
provider: SunEC, SunEC version 1.8
provider: SunJSSE, SunJSSE version 1.8
provider: SunJCE, SunJCE version 1.8
provider: SunJGSS, SunJGSS version 1.8
provider: SunSASL, SunSASL version 1.8
provider: XMLDSig, XMLDSig version 1.8
provider: SunPCSC, SunPCSC version 1.8

     */
}
