package com.kitabeli.ae.ui.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kitabeli.ae.model.AppError
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.utils.SingleLiveDataEvent
import com.kitabeli.ae.utils.ext.toAppError

abstract class BaseViewModel : ViewModel() {
    var errorLiveData: MutableLiveData<SingleLiveDataEvent<AppError>> = MutableLiveData()
    fun onError(error: AppError) {
        errorLiveData.postValue(SingleLiveDataEvent(error))
    }

    var messageLiveData: MutableLiveData<SingleLiveDataEvent<String>> = MutableLiveData()
    fun showMessage(message: String) {
        messageLiveData.postValue(SingleLiveDataEvent(message))
    }

    var loadingLiveData: MutableLiveData<SingleLiveDataEvent<Boolean>> = MutableLiveData()
    fun showLoading(visible: Boolean) {
        loadingLiveData.postValue(SingleLiveDataEvent(visible))
    }

    fun LoadState<Any?>.handleErrorAndLoadingState() {
        showLoading(isLoading)
        getErrorIfExists().toAppError()?.let { appError -> onError(appError) }
    }
}
