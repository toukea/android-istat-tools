package istat.android.base.tools;

import android.app.Service;

/**
 * Created by istat on 24/10/17.
 */

public class NoDoze {

    public <T extends Service> NoDoze(T service) {
        byte[] b = {-128};
        byte[] b2 = {127};
    }

    public void begin() {

    }

}
