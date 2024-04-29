package win.regin.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import win.regin.common.utils.ActivityUtils
import win.regin.common.utils.Utils

/**
 * 功能描述:Activity基类，普通Activity继承
 */
abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var mToolBar: Toolbar

    private var mDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_activity_base_layout)
        ActivityUtils.addActivityLifecycleCallbacks(this,object :
            Utils.ActivityLifecycleCallbacks() {
        })
        val viewContent = findViewById<FrameLayout>(R.id.viewContent)
        LayoutInflater.from(this).inflate(layoutId, viewContent)
        initView(savedInstanceState)
        initHeaderView()
        initToolBar()
    }


    @get:LayoutRes
    protected abstract val layoutId: Int


    private fun initHeaderView() {
        mToolBar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolBar)
        mToolBar.setNavigationOnClickListener { finish() }
    }

    protected open fun initToolBar() {

    }

    protected open fun initView(savedInstanceState: Bundle?) {

    }

    fun showProgress() {
        mDialog ?: let {
            mDialog = Dialog(it)
            mDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            val progressBar = ProgressBar(it)
            progressBar.indeterminateDrawable =
                ContextCompat.getDrawable(it, R.drawable.progressbar)
            mDialog?.setContentView(progressBar)
        }
        mDialog?.show()
    }

    fun dismissProgress() {
        mDialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityUtils.removeActivityLifecycleCallbacks(this)
    }
}

