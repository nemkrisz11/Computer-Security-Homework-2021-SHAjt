package com.shajt.caffshop.ui.commons

import android.graphics.Bitmap
import android.graphics.Color

object CreateCiff {

    fun createCiff(pixelValues: List<List<List<Int>>>): Bitmap {

        val height = pixelValues.size
        val width = pixelValues[0].size

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for ((i, row) in pixelValues.withIndex()) {
            for ((j, column) in row.withIndex()) {
                bitmap.setPixel(j, i, Color.rgb(column[0], column[1], column[2]))
            }
        }
        return bitmap
    }

}