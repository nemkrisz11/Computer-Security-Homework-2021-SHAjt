package com.shajt.caffshop.data.models

/**
 * Generic server result, contains a result or an error.
 */
data class ServerResult<R, E>(
    val result: R? = null,
    val error: E? = null
)