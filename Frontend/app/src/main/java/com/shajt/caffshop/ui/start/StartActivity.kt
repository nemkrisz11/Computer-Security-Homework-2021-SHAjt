package com.shajt.caffshop.ui.start

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shajt.caffshop.app.CaffShopApplication
import com.shajt.caffshop.ui.auth.AuthActivity
import com.shajt.caffshop.ui.home.HomeActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as CaffShopApplication

        // If user logged in start home activity else auth activity
        if (app.caffShopRepository.isLoggedIn) {
            startActivity(
                Intent(baseContext, HomeActivity::class.java)
            )
        } else {
            startActivity(
                Intent(baseContext, AuthActivity::class.java)
            )
        }

        // Complete and destroy login activity once successful
        setResult(Activity.RESULT_OK)
        finish()
    }
}