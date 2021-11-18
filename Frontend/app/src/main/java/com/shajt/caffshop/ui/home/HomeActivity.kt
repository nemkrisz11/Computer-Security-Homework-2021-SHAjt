package com.shajt.caffshop.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shajt.caffshop.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .commitNow()
        }
    }
}