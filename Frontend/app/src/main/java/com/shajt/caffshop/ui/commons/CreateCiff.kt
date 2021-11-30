package com.shajt.caffshop.ui.commons

import android.graphics.Bitmap
import android.graphics.Color

/**
 * Creates ciff image from pixel values.
 */
object CreateCiff {

    fun createCiff(pixelValues: List<List<List<Int>>>): Bitmap {

        // Getting sizes
        val height = pixelValues.size
        val width = pixelValues[0].size

        // Creating base bitmap.
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Creating ciff image from pixel values.
        for ((i, row) in pixelValues.withIndex()) {
            for ((j, column) in row.withIndex()) {
                bitmap.setPixel(j, i, Color.rgb(column[0], column[1], column[2]))
            }
        }
        return bitmap
    }

}