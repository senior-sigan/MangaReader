package org.seniorsigan.mangareader.data

data class Response<T>(
        val data: T,
        val pending: Boolean,
        val error: String? = null,
        val exception: Throwable? = null
)