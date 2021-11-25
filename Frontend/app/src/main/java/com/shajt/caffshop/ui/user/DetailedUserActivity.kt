package com.shajt.caffshop.ui.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shajt.caffshop.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_user)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailedUserFragment())
                .commitNow()
        }
    }
}