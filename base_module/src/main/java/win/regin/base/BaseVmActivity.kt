package win.regin.base

import android.os.Bundle
/**
 * 功能描述:ViewModelActivity基类，ViewModelActivity继承
 */

abstract class BaseVmActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createObserver()
    }

    /**
     * 创建观察者
     */
    abstract fun createObserver()
}