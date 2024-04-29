package win.regin.base.ext

import androidx.annotation.MainThread
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import win.regin.base.BaseViewModel
import win.regin.base.R
import win.regin.base.exception.AppException
import win.regin.common.BaseEntity
import win.regin.common.ContextHolder
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * 功能描述:扩展类
 */
fun Throwable?.parseErrorString(): String {
    return when (this) {
        is ConnectException -> ContextHolder.getInstance().context.getString(R.string.base_ConnectException)
        is UnknownHostException -> ContextHolder.getInstance().context.getString(R.string.base_UnknownHostException)
        else -> ContextHolder.getInstance().context.getString(R.string.base_ElseNetException)
    }
}


@MainThread
inline fun <T> VmLiveData<T>.vmObserver(owner: LifecycleOwner, vmResult: VmResult<T>.() -> Unit) {
    val result = VmResult<T>();result.vmResult();observeVm(owner = owner) {
        when (it) {
            is VmState.Loading -> {
                result.onLoading()
            }

            is VmState.Success -> {
                result.onSuccess(it.data);result.onComplete()
            }

            is VmState.Error -> {
                result.onError(it.error);result.onComplete()
            }
        }
    }
}


@MainThread
@Suppress("unused")
inline fun <T> VmLiveData<T>.vmObserverForever(vmResult: VmResult<T>.() -> Unit) {
    val result = VmResult<T>();result.vmResult();observeForeverVm {
        when (it) {
            is VmState.Loading -> {
                result.onLoading()
            }

            is VmState.Success -> {
                result.onSuccess(it.data);result.onComplete()
            }

            is VmState.Error -> {
                result.onError(it.error);result.onComplete()
            }
        }
    }
}


/**
 * net request
 * @param request request method
 * @param viewState request result
 */
fun <T> BaseViewModel.launchVmRequest(
    request: suspend () -> BaseEntity<T>,
    viewState: VmLiveData<T>
) {
    viewModelScope.launch {
        runCatching {
            viewState.value = VmState.Loading
            request()
        }.onSuccess {
            viewState.paresVmResult(it)
        }.onFailure {
            viewState.paresVmException(it)
        }
    }
}

/**
 * net request
 * @param request request method
 * @param vmResult request result
 */
fun <T> BaseViewModel.launchVmRequest(
    request: suspend () -> BaseEntity<T>,
    vmResult: VmResult<T>.() -> Unit
) {
    val result = VmResult<T>().also(vmResult)
    viewModelScope.launch {
        runCatching {
            result.onLoading;request()
        }.onSuccess {
            if (it.dataRight()) result.onSuccess(it.getResData()) else result.onError(
                AppException(
                    it.getMsg(),
                    it.getResCode()
                )
            )
        }.onFailure {
            result.onError(AppException(it))
        }
    }
}


/**
 * net request
 * @param request request method
 */
@Suppress("unused")
fun <T> BaseViewModel.launchRequestNoState(request: suspend () -> BaseEntity<T>) {
    viewModelScope.launch {
        runCatching {
            request()
        }
    }
}


/**
 * 以协程形式执行
 */
@Suppress("unused")
fun BaseViewModel.launchBlock(block: () -> Unit) {
    viewModelScope.launch { block() }
}

fun <T> BaseViewModel.launchBlock(
    block: suspend () -> BaseEntity<T>,
    vmResult: VmResult<T>.() -> Unit
) {
    val result = VmResult<T>().also(vmResult)
    viewModelScope.launch {
        flow {
            val r = withContext(Dispatchers.IO){
                block.invoke()
            }
            emit(r)
        }.flowOn(Dispatchers.IO)
            .onStart {
                result.onLoading.invoke()
            }
            .onCompletion {
                result.onComplete.invoke()
            }
            .catch {
                result.onError.invoke(AppException(it))
            }
            .collect {
                if (it.dataRight()) result.onSuccess.invoke(it.getResData()) else result.onError.invoke(
                    AppException(it.getMsg(), it.getResCode())
                )
            }
    }
}


/**
 * 处理返回值
 *
 * @param result 请求结果
 */
fun <T> VmLiveData<T>.paresVmResult(result: BaseEntity<T>) {
    value = if (result.dataRight()) VmState.Success(result.getResData()) else
        VmState.Error(AppException(result.getMsg(), result.getResCode()))
}


/**
 * 异常转换异常处理
 */
fun <T> VmLiveData<T>.paresVmException(e: Throwable) {
    this.value = VmState.Error(AppException(e))
}

@MainThread
inline fun <T> LiveData<T>.observeVm(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
): Observer<T> {
    val wrappedObserver = Observer<T> { t -> onChanged.invoke(t) }
    observe(owner, wrappedObserver)
    return wrappedObserver
}

@MainThread
inline fun <T> LiveData<T>.observeForeverVm(
    crossinline onChanged: (T) -> Unit
): Observer<T> {
    val wrappedObserver = Observer<T> { t -> onChanged.invoke(t) }
    observeForever(wrappedObserver)
    return wrappedObserver
}
typealias VmLiveData<T> = MutableLiveData<VmState<T>>