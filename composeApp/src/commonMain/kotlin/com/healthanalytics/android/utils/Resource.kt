package com.healthanalytics.android.utils

sealed class Resource<T>(
    val data: T? = null,
) {

    class Loading<T> : Resource<T>()
    class Error<T>(errorMessage: String? = null, data: T? = null) : Resource<T>(data)
    class Success<T>(data: T) : Resource<T>(data)

}