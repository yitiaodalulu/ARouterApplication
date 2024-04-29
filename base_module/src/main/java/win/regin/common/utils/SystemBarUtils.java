package win.regin.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.gyf.immersionbar.ImmersionBar;

import java.lang.reflect.Method;

/**
 * Created by xy on 15/12/23.
 */
public class SystemBarUtils {

    private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
    private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
    private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
    private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";
    private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";

    private static String sNavBarOverride;

    static {
        // Android allows a system property to override the presence of the
        // topics bar.
        // Used by the emulator.
        // See
        // https://github.com/android/platform_frameworks_base/blob/master/policy/src/com/android/internal/policy/impl/PhoneWindowManager.java#L1076
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                @SuppressWarnings("rawtypes")
                Class c = Class.forName("android.os.SystemProperties");
                @SuppressWarnings("unchecked")
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
                sNavBarOverride = null;
            }
        }
    }

    //如果是 Miui 和 魅族 4.4 以上 或 其他 Android 6.0 以上支持改变状态栏字体颜色，其他修改透明度
    public static boolean setStatusBarColor(ImmersionBar immersionBar, boolean statusBarDarkFontColor) {
        if (immersionBar != null) {
            if (ImmersionBar.isSupportStatusBarDarkFont()) {
                immersionBar.statusBarDarkFont(statusBarDarkFontColor)
                        .init();
                return true;
            } else {
                immersionBar.statusBarAlpha((float) 0.5).init();
                return false;
            }
        }
        return false;
    }

    public static void initStatusBar(ImmersionBar immersionBar) {
        if (immersionBar != null) {
            if (ImmersionBar.isSupportStatusBarDarkFont()) {
                immersionBar.init();
            } else {
                immersionBar.statusBarAlpha((float) 0.5).init();
            }
        }
    }

    @TargetApi(14)
    public static int getActionBarHeight(Context context) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
            result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return result;
    }

    public static boolean inPortarit(Resources res) {
        return (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    public static int getNavigationBarHeight(Context context) {
        Resources res = context.getResources();
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavigationBar(context)) {
                String key;
                if (inPortarit(res)) {
                    key = NAV_BAR_HEIGHT_RES_NAME;
                } else {
                    key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
                }
                return getInternalDimensionSize(res, key);
            }
        }
        return result;
    }

    @TargetApi(14)
    public static int getNavigationBarWidth(Context context) {
        Resources res = context.getResources();
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (hasNavigationBar(context)) {
                return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME);
            }
        }
        return result;
    }

    @TargetApi(14)
    public static boolean hasNavigationBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag (see static block)
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    public static int getStatusBarHeight(Context context) {
        return getInternalDimensionSize(context.getResources(), STATUS_BAR_HEIGHT_RES_NAME);
    }

    private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean hasBottomNav(Activity activity) {
        Rect r = new Rect();
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        View mChildOfContent = content.getChildAt(0);
        mChildOfContent.getGlobalVisibleRect(r);
        DisplayMetrics outMetrics = new DisplayMetrics();
        ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(outMetrics);
        Log.d("usableHeightNow", "heightPixels = ${outMetrics.heightPixels},r.bottom =${r.bottom} ,diff = ${outMetrics.heightPixels - r.bottom}");
        return r.bottom < outMetrics.heightPixels && outMetrics.heightPixels - r.bottom < r.bottom / 4;
    }

    public static void setStateTextColor(Boolean isGray, Activity activity) {
        if (isGray) {
            // 灰色
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        } else {
            // 白色
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }


    public static void setNavigationBarColor(int color, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(Color.parseColor("#FF0000"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.setNavigationBarDividerColor(color);
        }
    }
}
