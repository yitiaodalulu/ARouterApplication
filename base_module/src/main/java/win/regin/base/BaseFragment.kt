package win.regin.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 * 功能描述:Fragment基类，普通Fragment继承
 */
abstract class BaseFragment : Fragment() {

    @get:LayoutRes
    protected abstract val layoutId: Int


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view, savedInstanceState)
    }

    protected open fun initView(view: View, savedInstanceState: Bundle?) {

    }

}