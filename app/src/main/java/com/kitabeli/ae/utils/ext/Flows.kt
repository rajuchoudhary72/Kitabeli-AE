package com.kitabeli.ae.utils.ext

import com.kitabeli.ae.model.LoadState
import kotlinx.coroutines.flow.*

/**
 * wrap Flow with LoadState
 */
fun <T> Flow<T>.toLoadingState(): Flow<LoadState<T>> {
    return map<T, LoadState<T>> { LoadState.Loaded(it) }
        .onStart {
            @Suppress("UNCHECKED_CAST")
            emit(LoadState.Loading as LoadState<T>)
        }.onEach { LoadState.Loading }
        .catch { e ->
            emit(LoadState.Error<T>(e))
        }
}

suspend inline fun <T> Flow<T>.dropWhileIndexed(
    crossinline predicate: suspend (index: Int, value: T) -> Boolean,
): Flow<T> = flow {
    var index = 0
    var matched = false
    collect { value ->
        if (matched) {
            emit(value)
        } else if (!predicate(index, value)) {
            matched = true
            emit(value)
        }
        index++
    }
}
