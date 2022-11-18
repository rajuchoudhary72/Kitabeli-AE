package com.kitabeli.ae.utils.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// A predicate is a function that evaluates to true when its param matches the condition of the predicate
typealias Predicate = (value: String?) -> Boolean

class LiveDataValidator(private val liveData: LiveData<String>) {
    private val validationRules = mutableListOf<Predicate>()
    private val errorMessages = mutableListOf<Int>()

    var error = MutableLiveData<Int?>()

    // For checking if the liveData value matches the error condition set in the validation rule predicate
    // The livedata value is said to be valid when its value doesn't match an error condition set in the predicate
    fun isValid(): Boolean {
        for (i in 0 until validationRules.size) {
            if (validationRules[i](liveData.value)) {
                emitErrorMessage(errorMessages[i])
                return false
            }
        }

        emitErrorMessage(null)
        return true
    }

    // For emitting error messages
    private fun emitErrorMessage(messageRes: Int?) {
        error.value = messageRes
    }

    // For adding validation rules
    fun addRule(errorMsg: Int, predicate: Predicate) {
        validationRules.add(predicate)
        errorMessages.add(errorMsg)
    }
}

class LiveDataValidatorResolver(private val validators: List<LiveDataValidator>) {
    fun isValid(): Boolean {
        for (validator in validators) {
            if (!validator.isValid()) return false
        }
        return true
    }
}
