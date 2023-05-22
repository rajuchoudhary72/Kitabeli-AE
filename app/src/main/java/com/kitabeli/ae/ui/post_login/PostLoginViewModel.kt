package com.kitabeli.ae.ui.post_login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.data.remote.dto.KioskDetailDto
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
class PostLoginViewModel @Inject constructor(
    private val kioskRepository: KiosRepository,
) : BaseViewModel() {

    private val _kioskDetail: MutableLiveData<KioskDetailDto?> = MutableLiveData()
    val kioskDetail: LiveData<KioskDetailDto?> = _kioskDetail

    fun getKioskDetail(kioskCode: String?) = viewModelScope.launch {
        kioskRepository.getKioskDetail(kioskCode)
            .flowOn(Dispatchers.IO)
            .toLoadingState()
            .collectLatest { response ->
                response.handleErrorAndLoadingState()
                if (response is LoadState.Loaded) {
                    val result = response.value
                    _kioskDetail.postValue(result)
                }
            }
    }

    fun resetKioskDetail() {
        _kioskDetail.postValue(null)
    }
}