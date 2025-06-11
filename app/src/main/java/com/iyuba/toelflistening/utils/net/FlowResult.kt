package com.iyuba.toelflistening.utils.net

/**
苏州爱语吧科技有限公司
@Date:  2022/10/9
@Author:  han rong cheng
 */
sealed class FlowResult<T> {
    data class Success<T>(val data: T) : FlowResult<T>()
    data class Loading<T>(val loading:Boolean=true) : FlowResult<T>()
    data class Error<T>(val error: Throwable) : FlowResult<T>()

    inline fun onSuccess(method: (para: T) -> Unit): FlowResult<T> {
        if (this is Success) {
            method(data)
        }
        return this
    }

    inline fun onError(method: (error: Throwable) -> Unit): FlowResult<T> {
        if (this is Error) {
            method.invoke(error)
        }
        return this
    }

    inline fun onLoading(method: () -> Unit): FlowResult<T> {
        if (this is Loading) {
            method.invoke()
        }
        return this
    }
}
