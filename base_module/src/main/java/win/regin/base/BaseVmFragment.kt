package win.regin.base

import android.os.Bundle

/**
 * 功能描述:ViewModelFragment基类，ViewModelFragment继承
 */
abstract class BaseVmFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createObserver()
    }


    /**
     * 创建观察者
     */
    abstract fun createObserver()
}