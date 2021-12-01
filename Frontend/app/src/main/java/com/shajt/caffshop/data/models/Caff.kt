package com.shajt.caffshop.data.models

/**
 * Caff file representation.
 */
data class Caff(
    val id: String,
    val caffName: String,
    val creator: String,
    val creationDate: Long,
    val uploaderName: String,
    val uploadDate: Long,
    val numOfCiffs: Int,
    val caffAnimationImage: CaffAnimationImage
)
