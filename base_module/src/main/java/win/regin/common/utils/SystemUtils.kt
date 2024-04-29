package win.regin.common.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Looper
import android.os.Process
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

object SystemUtils {

    private var statusBarHeight = 0

    fun getKeyboardHeight(paramActivity: Activity): Int {
        var height = (getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity)
                - getAppHeight(paramActivity))
        if (height == 0) {
            height = MMKVUtil.getInt("KeyboardHeight")//787为默认软键盘高度 基本差不离
        } else {
            MMKVUtil.put( "KeyboardHeight", height)
        }
        return height
    }

    /**
     * 屏幕分辨率高
     */
    fun getScreenHeight(paramActivity: Activity): Int {
        val display = paramActivity.windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.heightPixels
    }

    fun getScreenWidth(paramActivity: Activity): Int {
        val display = paramActivity.windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics.widthPixels
    }


    /**
     * statusBar高度
     */
    fun getStatusBarHeight(paramActivity: Activity?): Int {
        if(statusBarHeight == 0){
            paramActivity?.let{
                statusBarHeight = SystemBarUtils.getStatusBarHeight(paramActivity)
            }
        }
        return statusBarHeight
    }

    /**
     * 可见屏幕高度
     */
    fun getAppHeight(paramActivity: Activity): Int {
        val localRect = Rect()
        paramActivity.window.decorView.getWindowVisibleDisplayFrame(localRect)
        return localRect.height()
    }

    /**
     * 关闭键盘
     */
    fun hideSoftInput(paramEditText: View) {
        val application = Utils.getApp()
        if (application != null) {
            val inputMethodManager = application.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(paramEditText.windowToken, 0)
        }
    }

    /**
     * dp转px
     */
    fun dip2px(dipValue: Int): Int {
        val reSize = Utils.getApp()!!.resources.displayMetrics.density
        return (dipValue * reSize + 0.5).toInt()
    }

    fun px2dip(pxValue: Int): Int {
        val reSize = Utils.getApp()!!.resources.displayMetrics.density
        return (pxValue / reSize + 0.5).toInt()
    }

    /**
     * 键盘是否在显示
     */
    fun isKeyBoardShow(paramActivity: Activity): Boolean {
        val height = (getScreenHeight(paramActivity) - getStatusBarHeight(paramActivity)
                - getAppHeight(paramActivity))
        return height != 0
    }

    /**
     * 显示键盘
     */
    fun showKeyBoard(paramEditText: View?) {
        paramEditText?.requestFocus()
        paramEditText?.post {
            val inputMethodManager = Utils.getApp()!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager?.showSoftInput(paramEditText, 0)
        }
    }

    /**
     * 判断当前是否是主线程
     */
    fun isCurrentMainThread() : Boolean{
        return Thread.currentThread() == Looper.getMainLooper().thread
    }

    /**
     * 效果　isCurrentMainThread(),七鱼 sdk 用
     *
     * @param context
     * @return
     */
    fun inMainProcess(context: Context): Boolean {
        val mainProcessName = context.applicationInfo.processName
        val processName = processName
        return TextUtils.equals(mainProcessName, processName)
    }

    /**
     * 获取当前进程名
     */
    private val processName: String?
        private get() {
            var reader: BufferedReader? = null
            return try {
                val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
                reader = BufferedReader(FileReader(file))
                reader.readLine().trim { it <= ' ' }
            } catch (e: IOException) {
                null
            } finally {
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    /**
     * 获取AppName
     *
     * @param context
     * @return
     */
    fun getApplicationName(context: Context): String {
        var packageManager: PackageManager? = null
        var applicationInfo: ApplicationInfo? = null
        try {
            packageManager = context.applicationContext.packageManager
            applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            applicationInfo = null
        }
        return packageManager?.getApplicationLabel(applicationInfo!!) as String
    }

    /**
     * 获取本地软件版本号
     */
    fun getLocalVersion(ctx: Context): Int {
        var localVersion = 0
        try {
            val packageInfo = ctx.applicationContext
                    .packageManager
                    .getPackageInfo(ctx.packageName, PackageManager.GET_SIGNATURES)
            localVersion = packageInfo.versionCode
            Log.d("AppInfo", "本软件的版本号：$localVersion")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return localVersion
    }

    /**
     * 获取本地软件版本号名称
     */
    fun getLocalVersionName(ctx: Context?): String {
        var localVersion = ""
        try {
            val packageInfo = ctx?.applicationContext?.packageManager?.getPackageInfo(ctx.packageName.toString(), PackageManager.GET_SIGNATURES)
            localVersion = packageInfo?.versionName?:""
            Log.d("AppInfo", "本软件的版本名：$localVersion")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
//            localVersion = BuildConfig.VERSION_NAME
        }
        return localVersion
    }

    /**
     * 获取当前本应用占用的内存
     * 返回值为kb
     */
    val curAppMemory: Float
        get() = (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024)).toFloat()

    //  获取app可分配的最大内存MB
    val maxAppMemory: Float
        get() = (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024)).toFloat()
}