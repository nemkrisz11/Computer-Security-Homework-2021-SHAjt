package com.shajt.caffshop.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shajt.caffshop.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedUserActivity : AppCompatActivity() {

    companion object {
        const val ARG_USERNAME = "username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_user)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    DetailedUserFragment.newInstance(intent.getStringExtra(ARG_USERNAME))
                )
                .commitNow()
        }
    }
}