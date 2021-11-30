package com.shajt.caffshop.data.models

/**
 * List of caff files with total page number.
 */
data class CaffList(
    val caffs: List<Caff>,
    val totalPages: Int
)
