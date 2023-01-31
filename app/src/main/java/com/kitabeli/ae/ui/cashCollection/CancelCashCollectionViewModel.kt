package com.kitabeli.ae.ui.cashCollection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CancelCashCollectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val kiosRepository: KiosRepository
) : BaseViewModel() {

    private val _cancelReason: MutableLiveData<UiState> = MutableLiveData()
    val cancelReason: LiveData<UiState> = _cancelReason

    fun getCancelReasonList() = viewModelScope.launch {
        kiosRepository.getCancelReasons()
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                //response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    response.value?.forEach {
                        Log.e("RESPONSEEEEE", it.name ?: "testt")
                    }
                    _cancelReason.postValue(UiState.Success(cancelReason = response.value))
                } else {
                    _cancelReason.postValue(UiState.Error("Failed"))
                }
            }
        /*_cancelReason.postValue(UiState.Loading)
        Log.e("ERORRRR", "ASDASDASDASD")
        val data = kiosRepository.getCancelReasons().asLiveData().value
        if (data != null) {
            _cancelReason.postValue(UiState.Success(cancelReason = data))
        } else {
            _cancelReason.postValue(UiState.Error("Failed"))
        }*/
    }
}