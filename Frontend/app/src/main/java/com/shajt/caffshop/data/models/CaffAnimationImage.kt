package com.shajt.caffshop.data.models

data class CaffAnimationImage(
    val duration: Long,
    val width: Int,
    val height: Int,
    val caption: String,
    val tags: List<String>,
    val pixelValues: List<Int>
)
