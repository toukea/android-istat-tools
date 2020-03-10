package istat.android.base.tools;

/*
 * Copyright (c)
 * The scaffold of this class was originaly taken from this project :https://github.com/delight-im/Android-Languages and has been modified after
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.app.Application;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Toukea tatsi J (istat)
 * Helper methods for setting a custom language for the process your application is running in
 **/
public class Language {
    //TODO lorsuque l'instance est release, eviter les SET et lancer exception
    //TODO method special si Language:isInitialized()
    //TODO method special Language.wasReleased()
    //TODO ajouter un système de persistance.
    protected static Locale mOriginalLocale;

    static {
        // save the original default locale so that we can reference it later
        mOriginalLocale = Locale.getDefault();
    }

    static Language instance;
    Application application;
    boolean autoCancellableOnAllActivitiesDestroyed = false;
    Activity currentForegroundActivity;
    String languageToApply = mOriginalLocale.getLanguage();
    final List<Activity> openedActivities = new ArrayList<>();

    private Language(Application application) {
        instance = this;
        this.application = application;
    }

    public static Language retrieveOrInitializeInstance(Application application) {
        if (instance != null) {
            return getInstance();
        } else {
            return initialize(application);
        }
    }

    public static Language getInstance() {
        if (instance == null) {
            throw new IllegalStateException("You should call initialize(ContextWrapper) first");
        }
        return instance;
    }

    public static Language initialize(Application application) {
        if (instance != null) {
            throw new IllegalStateException("The language instance is already initialized");
        }
        if (application == null) {
            throw new IllegalArgumentException("The given Application instance can't be NULL");
        }
        instance = new Language(application);
        instance.initialize();
        return instance;
    }

    private void initialize() {
        application.registerActivityLifecycleCallbacks(mActivityLifeCyLifecycleCallbacks);
    }

    public void setAutoCancellableOnAllActivitiesDestroyed(boolean autoCancellableOnAllActivitiesDestroyed) {
        this.autoCancellableOnAllActivitiesDestroyed = autoCancellableOnAllActivitiesDestroyed;
    }

    public boolean isAutoCancellableOnAllActivitiesDestroyed() {
        return autoCancellableOnAllActivitiesDestroyed;
    }

    public void setLocale(String languageCode) {
        setLocale(languageCode, false);
    }

    public void setLocale(String languageCode, boolean forceUpdate) {
        setLocale(currentForegroundActivity, languageCode, forceUpdate);
    }

    public void setLocale(Activity activity, String languageCode) {
        setLocale(activity, languageCode, false);
    }

    public void setLocale(Activity activity, String languageCode, boolean forceUpdate) {
        this.languageToApply = languageCode;
        Language.set(application, languageCode, forceUpdate);
        if (activity != null) {
            Language.set(activity, languageCode, forceUpdate);
            activity.recreate();
        }
    }

    /**
     * Release the Language instance an free all resources attached.
     *
     * @return
     */
    public boolean release() {
        try {
            application.unregisterActivityLifecycleCallbacks(mActivityLifeCyLifecycleCallbacks);
            currentForegroundActivity = null;
            openedActivities.clear();
            application = null;
            instance = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isReady() {
        return application != null;
    }


    private Application.ActivityLifecycleCallbacks mActivityLifeCyLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (!openedActivities.contains(activity)) {
                openedActivities.add(activity);
            } else {
                Collections.swap(openedActivities, openedActivities.indexOf(activity), openedActivities.size() - 1);
            }
            set(activity, languageToApply, false);

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            currentForegroundActivity = activity;

        }

        @Override
        public void onActivityPaused(Activity activity) {
            currentForegroundActivity = null;
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            openedActivities.remove(activity);
            if (openedActivities.isEmpty() && autoCancellableOnAllActivitiesDestroyed) {
                release();
            }
        }
    };
//
//    /**
//     * Sets the language/locale for the current application and its process to the given language code
//     *
//     * @param context      the `ContextWrapper` instance to get a `Resources` instance from
//     * @param languageCode the language code in the form `[a-z]{2}` (e.g. `es`) or `[a-z]{2}-r?[A-Z]{2}` (e.g. `pt-rBR`)
//     */
//    static void setLocale(final ContextWrapper context, final String languageCode) {
//        setLocale(context, languageCode, false);
//    }

    /**
     * Sets the language/locale for the current application and its process to the given language code
     *
     * @param context      the `ContextWrapper` instance to get a `Resources` instance from
     * @param languageCode the language code in the form `[a-z]{2}` (e.g. `es`) or `[a-z]{2}-r?[A-Z]{2}` (e.g. `pt-rBR`)
     * @param forceUpdate  whether to force an update when the default language (empty language code) is requested
     */
    static void set(final ContextWrapper context, final String languageCode, final boolean forceUpdate) {
        // if a custom language is requested (non-empty language code) or a forced update is requested
        if (!languageCode.equals("") || forceUpdate) {
            try {
                // create a new Locale instance
                final Locale newLocale;

                // if the default language is requested (empty language code)
                if (languageCode.equals("")) {
                    // setLocale the new Locale instance to the default language
                    newLocale = mOriginalLocale;
                }
                // if a custom language is requested (non-empty language code)
                else {
                    // if the language code does also contain a region
                    if (languageCode.contains("-r") || languageCode.contains("-")) {
                        // split the language code into language and region
                        final String[] language_region = languageCode.split("\\-(r)?");
                        // construct a new Locale object with the specified language and region
                        newLocale = new Locale(language_region[0], language_region[1]);
                    }
                    // if the language code does not contain a region
                    else {
                        // simply construct a new Locale object from the given language code
                        newLocale = new Locale(languageCode);
                    }
                }

                if (newLocale != null) {
                    // update the app's configuration to use the new Locale
                    final Resources resources = context.getBaseContext().getResources();
                    final android.content.res.Configuration conf = resources.getConfiguration();
                    conf.locale = newLocale;
                    resources.updateConfiguration(conf, resources.getDisplayMetrics());

                    // overwrite the default Locale
                    Locale.setDefault(newLocale);
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Returns the original Locale instance that was in use before any custom selection may have been applied
     *
     * @return the original Locale instance
     */
    public static Locale getOriginalLocale() {
        return mOriginalLocale;
    }

}