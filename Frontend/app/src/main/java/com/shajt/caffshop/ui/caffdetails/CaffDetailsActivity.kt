package com.shajt.caffshop.ui.caffdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shajt.caffshop.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CaffDetailsActivity : AppCompatActivity() {

    companion object {
        const val ARG_CAFF_ID = "caffId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caff_details)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    CaffDetailsFragment.newInstance(
                        intent.getIntExtra(ARG_CAFF_ID, -1)
                    )
                )
                .commitNow()
        }
    }
}