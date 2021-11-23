package com.shajt.caffshop.data.models

data class ServerResult<R, E>(
    val result: R? = null,
    val error: E? = null
)