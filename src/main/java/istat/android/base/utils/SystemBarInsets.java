package istat.android.base.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.graphics.Insets;

/**
 * Utility that centralises edge-to-edge / system-bar inset handling for Activities.
 *
 * <p><b>Why edge-to-edge matters (API 35+):</b> targeting API 35 forces edge-to-edge mode — the
 * app window draws behind the status bar and the navigation bar. Without explicit inset handling,
 * UI elements end up hidden under those bars (toolbar obscured by the status bar, bottom buttons
 * hidden behind the nav bar). All methods in this class register a live
 * {@link ViewCompat#setOnApplyWindowInsetsListener} so insets are re-applied automatically on
 * every configuration change (rotation, multi-window resize, foldable posture).</p>
 *
 * <p><b>How to use — call exactly once per Activity, in {@code onCreate} after
 * {@code setContentView}, then never again:</b></p>
 * <ul>
 *   <li>{@link #apply} — standard screen with an AppBar at the top and optional bottom
 *       content / FAB</li>
 *   <li>{@link #applyWithIme} — same as {@code apply} but also adapts to the software keyboard
 *       opening and closing (screens with text input)</li>
 *   <li>{@link #applyCollapsingToolbarInsets} — screen with a collapsing toolbar whose height
 *       changes dynamically as the user scrolls</li>
 *   <li>{@link #applyStatusBarOverlay} — screen that paints its own solid background across the
 *       full height (reader, media viewer) and wants a color band drawn <em>over</em> the status
 *       bar area rather than pushing content below it</li>
 *   <li>{@link #applyImmersive} — hides both bars entirely; they reappear temporarily on swipe
 *       (video player, game, full-screen reader)</li>
 *   <li>{@link #colorSystemBars} — rare case where only the bar tint is needed, with no inset
 *       adjustments to any view</li>
 * </ul>
 *
 * <p><b>Dependencies:</b> requires {@code androidx.core:core} ≥ 1.7.0 (transitively provided
 * by {@code androidx.appcompat:appcompat} ≥ 1.4.0).</p>
 */
public final class SystemBarInsets {
    private SystemBarInsets() {
    }

    // -------------------------------------------------------------------------
    // apply — standard AppBar screen
    // -------------------------------------------------------------------------

    /**
     * Standard edge-to-edge setup for a screen with a top AppBar and optional bottom content / FAB.
     * Convenience overload that defaults {@code lightStatusBars} to {@code false}.
     *
     * @param activity          the hosting Activity
     * @param root              the root view that receives the inset listener (typically the layout
     *                          root or {@code android.R.id.content})
     * @param topInsetView      view that gains top padding equal to the status-bar height (typically
     *                          the AppBar); {@code null} to skip
     * @param expandTopHeight   {@code true} to also grow {@code topInsetView}'s layout height by the
     *                          status-bar inset — needed when the AppBar has a fixed dp height in XML
     *                          that must visually extend to the screen edge
     * @param bottomPaddingView view that gains bottom padding equal to the nav-bar height (typically
     *                          the main content container); {@code null} to skip
     * @param bottomMarginView  view that gains bottom margin equal to the nav-bar height (typically
     *                          a FAB so it floats above the nav bar); {@code null} to skip
     *
     * @see #apply(Activity, View, View, boolean, View, View, boolean)
     */
    public static void apply(Activity activity, View root, @Nullable View topInsetView, boolean expandTopHeight, @Nullable View bottomPaddingView, @Nullable View bottomMarginView) {
        apply(activity, root, topInsetView, expandTopHeight, bottomPaddingView, bottomMarginView, false);
    }

    /**
     * Full variant of the standard edge-to-edge setup.
     *
     * <p>Activates edge-to-edge ({@code WindowCompat.setDecorFitsSystemWindows(false)}), makes
     * both bars transparent, then keeps insets live via a
     * {@link ViewCompat#setOnApplyWindowInsetsListener} on {@code root}.</p>
     *
     * <p>When {@code topInsetView} and {@code bottomPaddingView} refer to the same view — useful
     * when a single root layout handles the entire screen — both top and bottom insets are merged
     * into a single {@code setPadding} call to avoid conflicting updates.</p>
     *
     * <p><b>Example — screen with a separate AppBar, content area, and FAB:</b></p>
     * <pre>
     * SystemBarInsets.apply(this,
     *     findViewById(R.id.root_layout),       // root: receives the inset listener
     *     findViewById(R.id.app_bar),           // top inset → AppBar gains paddingTop
     *     false,                                // AppBar height stays as declared in XML
     *     findViewById(R.id.content_container), // bottom inset → content gains paddingBottom
     *     findViewById(R.id.fab));              // bottom inset → FAB gains marginBottom
     * </pre>
     *
     * <p><b>Example — single root view (top + bottom target combined, light background):</b></p>
     * <pre>
     * SystemBarInsets.apply(this,
     *     findViewById(R.id.root), findViewById(R.id.root),
     *     false, null, null,
     *     true);  // lightStatusBars: dark icons for a light-colored background
     * </pre>
     *
     * @param lightStatusBars {@code true} to show dark icons in the status bar (use on light
     *                        backgrounds); {@code false} for white icons (dark themes)
     */
    public static void apply(Activity activity, View root, @Nullable View topInsetView, boolean expandTopHeight, @Nullable View bottomPaddingView, @Nullable View bottomMarginView, boolean lightStatusBars) {
        if (activity == null || root == null) {
            return;
        }
        prepareSystemBars(activity, lightStatusBars);

        // Snapshot original paddings/margins before the listener is attached so each
        // inset update adds to the design-time value, not to a previously accumulated one.
        final Padding topPadding = topInsetView != null ? new Padding(topInsetView) : null;
        final Padding bottomPadding = bottomPaddingView != null ? new Padding(bottomPaddingView) : null;
        final int topBaseHeight = topInsetView != null && topInsetView.getLayoutParams() != null ? topInsetView.getLayoutParams().height : ViewGroup.LayoutParams.WRAP_CONTENT;
        final ViewGroup.MarginLayoutParams bottomMarginParams = bottomMarginView != null && bottomMarginView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams ? (ViewGroup.MarginLayoutParams) bottomMarginView.getLayoutParams() : null;
        final int bottomBaseMargin = bottomMarginParams != null ? bottomMarginParams.bottomMargin : 0;

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (topInsetView != null && topInsetView == bottomPaddingView && topPadding != null) {
                topInsetView.setPadding(topPadding.left, topPadding.top + systemBars.top, topPadding.right, topPadding.bottom + systemBars.bottom);
            } else if (topInsetView != null && topPadding != null) {
                topInsetView.setPadding(topPadding.left, topPadding.top + systemBars.top, topPadding.right, topPadding.bottom);
            }
            if (topInsetView != null && expandTopHeight && topBaseHeight > 0) {
                ViewGroup.LayoutParams params = topInsetView.getLayoutParams();
                params.height = topBaseHeight + systemBars.top;
                topInsetView.setLayoutParams(params);
            }
            if (bottomPaddingView != null && bottomPaddingView != topInsetView && bottomPadding != null) {
                bottomPaddingView.setPadding(bottomPadding.left, bottomPadding.top, bottomPadding.right, bottomPadding.bottom + systemBars.bottom);
            }
            if (bottomMarginView != null && bottomMarginParams != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) bottomMarginView.getLayoutParams();
                params.bottomMargin = bottomBaseMargin + systemBars.bottom;
                bottomMarginView.setLayoutParams(params);
            }
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    // -------------------------------------------------------------------------
    // applyWithIme — screen with text input (keyboard-aware)
    // -------------------------------------------------------------------------

    /**
     * Edge-to-edge setup that also reacts to the software keyboard opening and closing.
     *
     * <p>{@link #apply} only handles {@code systemBars} insets (status bar + nav bar). It is blind
     * to the IME: when the keyboard appears, it covers the bottom of the screen and any
     * {@code EditText} or send button positioned there stays hidden behind it. This method listens
     * to both inset types and uses {@code Math.max(navBarHeight, imeHeight)} as the effective
     * bottom inset, so the content always clears whichever is taller at any given moment.</p>
     *
     * <p><b>Prerequisite:</b> the Activity must declare
     * {@code android:windowSoftInputMode="adjustResize"} in the manifest for IME insets to be
     * dispatched through the inset system.</p>
     *
     * <p><b>Example — form screen with a scrollable field area and a submit button:</b></p>
     * <pre>
     * SystemBarInsets.applyWithIme(this,
     *     findViewById(R.id.root_layout),
     *     findViewById(R.id.app_bar),          // gains paddingTop (status bar)
     *     findViewById(R.id.scroll_view),      // gains paddingBottom = max(navBar, keyboard)
     *     findViewById(R.id.btn_submit));      // gains marginBottom = max(navBar, keyboard)
     * </pre>
     *
     * @param root              root view that receives the inset listener
     * @param topInsetView      view that gains top padding for the status bar; {@code null} to skip
     * @param scrollableContent view that gains bottom padding equal to the larger of the nav-bar
     *                          height and the keyboard height (typically a {@code ScrollView} or
     *                          {@code RecyclerView}); {@code null} to skip
     * @param bottomMarginView  view that gains bottom margin equal to the same effective bottom
     *                          inset (typically a submit button or FAB); {@code null} to skip
     */
    public static void applyWithIme(Activity activity, View root, @Nullable View topInsetView, @Nullable View scrollableContent, @Nullable View bottomMarginView) {
        if (activity == null || root == null) {
            return;
        }
        prepareSystemBars(activity, false);

        final Padding topPadding = topInsetView != null ? new Padding(topInsetView) : null;
        final Padding contentPadding = scrollableContent != null ? new Padding(scrollableContent) : null;
        final ViewGroup.MarginLayoutParams marginParams = bottomMarginView != null && bottomMarginView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams ? (ViewGroup.MarginLayoutParams) bottomMarginView.getLayoutParams() : null;
        final int baseMargin = marginParams != null ? marginParams.bottomMargin : 0;

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            // When the keyboard is visible its inset already covers the nav-bar area,
            // so taking the max gives the right padding in both states without a jump.
            int bottomInset = Math.max(systemBars.bottom, ime.bottom);

            if (topInsetView != null && topPadding != null) {
                topInsetView.setPadding(topPadding.left, topPadding.top + systemBars.top, topPadding.right, topPadding.bottom);
            }
            if (scrollableContent != null && contentPadding != null) {
                scrollableContent.setPadding(contentPadding.left, contentPadding.top, contentPadding.right, contentPadding.bottom + bottomInset);
            }
            if (bottomMarginView != null && marginParams != null) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) bottomMarginView.getLayoutParams();
                params.bottomMargin = baseMargin + bottomInset;
                bottomMarginView.setLayoutParams(params);
            }
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    // -------------------------------------------------------------------------
    // applyStatusBarOverlay — reader / full-height background screens
    // -------------------------------------------------------------------------

    /**
     * Reader-screen variant: injects a dynamically-sized color band at position 0 of {@code root}
     * to visually fill the transparent status-bar area, without shifting content below it.
     *
     * <p>The band color is resolved from the current theme's {@code colorPrimaryDark} attribute.
     * If the attribute is not present, the band is transparent (space reserved, no visual band).</p>
     *
     * @see #applyStatusBarOverlay(Activity, View, View, int) overload with an explicit color
     */
    public static void applyStatusBarOverlay(Activity activity, View root, @Nullable View bottomPaddingView) {
        if (activity == null) return;
        TypedValue typedValue = new TypedValue();
        int color = Color.TRANSPARENT;
        if (activity.getTheme().resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true)) {
            color = typedValue.data;
        }
        applyStatusBarOverlay(activity, root, bottomPaddingView, color);
    }

    /**
     * Reader-screen variant with an explicit overlay color.
     *
     * <p>Use this instead of {@link #apply} when the screen draws its own background across the
     * full height (paged reader, Markdown/HTML viewer, media gallery…) and the toolbar sits
     * <em>below</em> the status bar rather than <em>behind</em> it. A {@link View} of height
     * {@code systemBars.top} is injected as the first child of {@code root} and resized live on
     * every inset change. The nav-bar bottom inset is handled via {@code bottomPaddingView}.</p>
     *
     * <p><b>Example — custom semi-transparent overlay:</b></p>
     * <pre>
     * SystemBarInsets.applyStatusBarOverlay(this,
     *     findViewById(R.id.root_layout),
     *     findViewById(R.id.root_layout),
     *     Color.argb(180, 0, 0, 0));  // translucent black band
     * </pre>
     *
     * @param root              a {@link ViewGroup} at the top of the hierarchy; the colored band is
     *                          inserted as child 0
     * @param bottomPaddingView view that gains bottom padding for the nav bar; may be the same
     *                          as {@code root}; {@code null} to skip
     * @param overlayColor      ARGB color for the injected band (use {@code Color.TRANSPARENT} to
     *                          reserve the space without painting anything)
     */
    public static void applyStatusBarOverlay(Activity activity, View root, @Nullable View bottomPaddingView, @ColorInt int overlayColor) {
        if (activity == null || !(root instanceof ViewGroup rootView)) {
            return;
        }
        prepareSystemBars(activity, false);
        final View statusBarView = new View(activity);
        statusBarView.setBackgroundColor(overlayColor);
        statusBarView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        rootView.addView(statusBarView, 0);

        final Padding bottomPadding = bottomPaddingView != null ? new Padding(bottomPaddingView) : null;
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.LayoutParams params = statusBarView.getLayoutParams();
            params.height = systemBars.top;
            statusBarView.setLayoutParams(params);
            if (bottomPaddingView != null && bottomPadding != null) {
                bottomPaddingView.setPadding(bottomPadding.left, bottomPadding.top, bottomPadding.right, bottomPadding.bottom + systemBars.bottom);
            }
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    // -------------------------------------------------------------------------
    // applyImmersive / exitImmersive — full-screen mode
    // -------------------------------------------------------------------------

    /**
     * Hides both the status bar and the navigation bar entirely.
     *
     * <p>The bars reappear temporarily when the user swipes from the screen edge, then
     * auto-hide again — this is Android's "sticky immersive" mode. No inset listener is needed
     * here because there are no visible bars whose height must be compensated.</p>
     *
     * <p>Call {@link #exitImmersive} to restore the bars permanently (e.g. when the user taps
     * a UI control that requires the bars to stay visible).</p>
     *
     * <p><b>Typical use cases:</b> video playback, full-screen image viewer, game, distraction-free
     * reading mode.</p>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * // Enter immersive on screen tap:
     * rootView.setOnClickListener(v -> SystemBarInsets.applyImmersive(this));
     *
     * // Exit immersive when showing playback controls:
     * controlsView.setVisibility(View.VISIBLE);
     * SystemBarInsets.exitImmersive(this);
     * </pre>
     */
    public static void applyImmersive(Activity activity) {
        if (activity == null) return;
        prepareSystemBars(activity, false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(
                activity.getWindow(), activity.getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        // Bars reappear on swipe and then auto-hide again — no permanent restore on interaction.
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    /**
     * Restores the status bar and navigation bar after a call to {@link #applyImmersive}.
     *
     * <p>The bars reappear and stay visible until the user or the app hides them again.
     * Call this when transitioning back to a UI that requires visible system bars.</p>
     */
    public static void exitImmersive(Activity activity) {
        if (activity == null) return;
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(
                activity.getWindow(), activity.getWindow().getDecorView());
        controller.show(WindowInsetsCompat.Type.systemBars());
    }

    // -------------------------------------------------------------------------
    // colorSystemBars — tint only, no inset adjustments
    // -------------------------------------------------------------------------

    /**
     * Tints the system bars without adjusting any view's insets or padding.
     *
     * <p>Use only when the Activity manages its own layout positioning and just needs the bars to
     * have the correct color and icon style — for example, a fully custom full-screen layout that
     * already accounts for insets via XML attributes or its own logic.</p>
     *
     * @param lightStatusBars {@code true} for dark status-bar icons (light backgrounds),
     *                        {@code false} for white icons (dark backgrounds)
     */
    public static void colorSystemBars(Activity activity, boolean lightStatusBars) {
        prepareSystemBars(activity, lightStatusBars);
    }

    // -------------------------------------------------------------------------
    // applyCollapsingToolbarInsets — collapsing toolbar screens
    // -------------------------------------------------------------------------

    /**
     * Edge-to-edge setup for screens that use a collapsing toolbar.
     *
     * <p>A plain {@link #apply} call is not enough here because the toolbar has a dynamic height:
     * it is tall while the AppBar is expanded and shrinks to {@code actionBarSize} once fully
     * collapsed. This method fixes the toolbar height to {@code actionBarSize + systemBars.top}
     * so the title remains fully visible in collapsed state, and sets the same value as
     * {@code minHeight} on both the {@code appBarLayout} and the {@code collapsingToolbar} so
     * the collapse animation stops at exactly the right position.</p>
     *
     * <p><b>Example:</b></p>
     * <pre>
     * SystemBarInsets.applyCollapsingToolbarInsets(this,
     *     findViewById(android.R.id.content),  // root
     *     findViewById(R.id.app_bar),           // AppBarLayout (passed as View)
     *     findViewById(R.id.toolbar_layout),    // CollapsingToolbarLayout (passed as View)
     *     findViewById(R.id.toolbar));          // inner Toolbar
     * </pre>
     *
     * @param appBarLayout      the {@code AppBarLayout} wrapping the collapsing toolbar, passed
     *                          as a {@link View} to avoid a dependency on the Material library
     * @param collapsingToolbar the {@code CollapsingToolbarLayout} (or any view acting as one),
     *                          passed as {@link View} for the same reason
     * @param toolbar           the inner {@code Toolbar} whose height and top padding are adjusted
     */
    public static void applyCollapsingToolbarInsets(Activity activity, View root, View appBarLayout, View collapsingToolbar, View toolbar) {
        if (activity == null || root == null || appBarLayout == null || collapsingToolbar == null || toolbar == null) {
            return;
        }
        prepareSystemBars(activity, false);

        final int toolbarBaseHeight = getActionBarHeight(activity, toolbar);
        final Padding toolbarPadding = new Padding(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int collapsedHeight = toolbarBaseHeight + systemBars.top;
            ViewGroup.LayoutParams toolbarParams = toolbar.getLayoutParams();
            toolbarParams.height = collapsedHeight;
            toolbar.setLayoutParams(toolbarParams);
            toolbar.setPadding(toolbarPadding.left, toolbarPadding.top + systemBars.top, toolbarPadding.right, toolbarPadding.bottom);
            appBarLayout.setMinimumHeight(collapsedHeight);
            collapsingToolbar.setMinimumHeight(collapsedHeight);
            appBarLayout.requestLayout();
            collapsingToolbar.requestLayout();
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /** Activates edge-to-edge and applies bar colors / icon tint for the given Activity. */
    private static void prepareSystemBars(Activity activity, boolean lightStatusBars) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(activity.getWindow(), activity.getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(lightStatusBars);
        controller.setAppearanceLightNavigationBars(isLightTheme(activity));
    }

    /** Snapshots a view's current padding so inset listeners can add to the original value, not accumulate. */
    private static class Padding {
        final int left;
        final int top;
        final int right;
        final int bottom;

        Padding(View view) {
            left = view.getPaddingLeft();
            top = view.getPaddingTop();
            right = view.getPaddingRight();
            bottom = view.getPaddingBottom();
        }
    }

    private static boolean isLightTheme(Activity activity) {
        return (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES;
    }

    /** Resolves the action bar height from the theme, falling back to the toolbar's own declared dimensions. */
    private static int getActionBarHeight(Activity activity, View toolbar) {
        TypedValue value = new TypedValue();
        if (activity.getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, value, true)) {
            return TypedValue.complexToDimensionPixelSize(value.data, activity.getResources().getDisplayMetrics());
        }
        if (toolbar.getMinimumHeight() > 0) {
            return toolbar.getMinimumHeight();
        }
        return toolbar.getLayoutParams() != null && toolbar.getLayoutParams().height > 0
                ? toolbar.getLayoutParams().height
                : 0;
    }
}
