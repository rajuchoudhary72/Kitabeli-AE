package com.kitabeli.ae.ui.training_video

import androidx.lifecycle.viewModelScope
import com.kitabeli.ae.data.remote.dto.TrainingVideoDto
import com.kitabeli.ae.data.remote.service.KiosService
import com.kitabeli.ae.model.LoadingState
import com.kitabeli.ae.ui.common.BaseViewModel
import com.kitabeli.ae.utils.ext.toLoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingVideoViewModel @Inject constructor(
    private val kiosService: KiosService,
) : BaseViewModel() {

    private var _trainingVideoData = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val trainingVideoData get() = _trainingVideoData


    fun getTrainingVideos(func: (TrainingVideoDto.Payload?) -> Unit) {
        viewModelScope.launch {
            kiosService.getTrainingVideos()
                .flowOn(Dispatchers.IO)
                .toLoadingState()
                .collectLatest { response ->
                    response.handleErrorAndLoadingState()
                    response.getValueOrNull()?.let { baseResponseDto ->
                        func(baseResponseDto.payload)
                    }

                    //Log.e("sasdsadas", response.payload.toString())
                }
        }

    }

}