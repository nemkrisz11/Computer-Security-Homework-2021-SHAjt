package com.shajt.caffshop.ui.commons

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import android.app.Activity
import android.view.inputmethod.InputMethodManager

/**
 * Displays messages in Snackbar or in Toast.
 */
object DisplayMessage {

    /**
     * Displays Snackbar.
     */
    fun displaySnackbar(view: View, @StringRes messageResId: Int, anchorView: View? = null) {
        hideKeyboard(view)
        Snackbar
            .make(view, messageResId, Snackbar.LENGTH_SHORT)
            .setAnchorView(anchorView)
            .show()
    }

    /**
     * Displays snackbar.
     */
    fun displaySnackbar(view: View, message: String, anchorView: View? = null) {
        hideKeyboard(view)
        Snackbar
            .make(view, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(anchorView)
            .show()
    }

    /**
     * Displays Toast.
     */
    fun displayToast(context: Context, @StringRes messageResId: Int) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
    }

    /**
     * Displays Toast.
     */
    fun displayToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Hides soft keyboard.
     */
    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}