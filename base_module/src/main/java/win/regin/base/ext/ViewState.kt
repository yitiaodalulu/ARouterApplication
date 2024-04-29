package win.regin.base.ext

import win.regin.base.exception.AppException

/**
 *         功能描述:请求回调类
 */

class VmResult<T> {
    var onSuccess: (data: T) -> Unit = {}
    var onError: (AppException) -> Unit = {}
    var onLoading: () -> Unit = {}
    var onComplete: () -> Unit = {}

    fun onSuccess(success: (T) -> Unit) {
        onSuccess = success
    }

    fun onError(error: (AppException) -> Unit) {
        onError = error
    }

    fun onLoading(loading: () -> Unit) {
        onLoading = loading
    }

    fun onComplete(complete: () -> Unit) {
        onComplete = complete
    }
}

sealed class VmState<out T> {
    object Loading : VmState<Nothing>()
    data class Success<out T>(val data: T) : VmState<T>()
    data class Error(val error: AppException) : VmState<Nothing>()
}
