package istat.android.base.utils;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;

/**
 * Helpers around Android 16 (API 36) intent hardening.
 */
public final class IntentSecurity {

    private IntentSecurity() {
    }

    /**
     * Opts a nested intent (unparceled from another intent's extras) out of the Android 16
     * intent redirection protection, so it can be relaunched as before.
     * Only use it on intents built by this app.
     */
    @Nullable
    public static Intent allowRelaunch(@Nullable Intent intent) {
        if (intent != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            intent.removeLaunchSecurityProtection();
        }
        return intent;
    }

    /**
     * Neutralizes an intent parsed from untrusted content (e.g. an {@code intent://} URL of a web
     * page): it can only target browsable activities, never a specific component.
     */
    @Nullable
    public static Intent sanitizeParsedUri(@Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setComponent(null);
        intent.setSelector(null);
        return intent;
    }
}
