package com.kitabeli.ae.ui.kios

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kitabeli.ae.data.remote.dto.KiosDetail
import com.kitabeli.ae.model.AppError
import com.kitabeli.ae.model.LoadState
import com.kitabeli.ae.model.repository.KiosRepository
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class KiosViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, kiosRepository: KiosRepository
) : ViewModel() {

    private val _stockOpNameId = savedStateHandle.get<Int>("stockOpNameId")
    val stockOpNameId = _stockOpNameId

    private val _kiosDetail = MutableStateFlow<KiosDetail?>(null)

    val kiosStocks: Flow<KiosUiState> =
        kiosRepository.getKiosStocks(stockOpNameId!!).flowOn(Dispatchers.IO).toLoadingState()
            .map { state ->
                when (state) {
                    LoadState.Loading -> {
                        KiosUiState.Loading
                    }

                    is LoadState.Error -> {
                        KiosUiState.Error(state.getAppErrorIfExists())
                    }

                    is LoadState.Loaded -> {
                        _kiosDetail.update { state.value }
                        KiosUiState.Success(state.value)
                    }
                }
            }


    fun getKiosDetails(): KiosDetail? = _kiosDetail.value

}

sealed interface KiosUiState {
    object Loading : KiosUiState

    data class Error(
        val error: AppError?,
    ) : KiosUiState

    data class Success(
        val report: KiosDetail?
    ) : KiosUiState

    fun isLoading() = this == Loading
}