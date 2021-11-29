package com.shajt.caffshop.ui.start

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.ui.auth.AuthActivity
import com.shajt.caffshop.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {

    @Inject
    lateinit var caffShopRepository: CaffShopRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        // If user logged in start home activity else auth activity
        if (caffShopRepository.isLoggedIn) {
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