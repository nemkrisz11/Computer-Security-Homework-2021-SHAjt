package com.shajt.caffshop.ui.auth

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import com.shajt.caffshop.databinding.ActivityAuthBinding

import com.shajt.caffshop.R
import com.shajt.caffshop.app.CaffShopApplication
import com.shajt.caffshop.data.models.User
import com.shajt.caffshop.ui.home.HomeActivity
import com.shajt.caffshop.viewmodels.auth.AuthViewModel

class AuthActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val password = binding.password
        val auth = binding.auth
        val loading = binding.loading

        val viewModelFactory = (application as CaffShopApplication).caffShopViewModelFactory
        authViewModel = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]

        authViewModel.authFormState.observe(this, Observer {
            val loginState = it ?: return@Observer

            // Disable login button unless both username / password is valid
            auth.isEnabled = loginState.isDataValid

            // Show errors
            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError.errorStringResourceId)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError.errorStringResourceId)
            }
        })

        authViewModel.authResult.observe(this, Observer {
            val loginResult = it ?: return@Observer

            // Hide progress indicator
            loading.visibility = View.GONE

            // Show register/login error message
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error.errorStringResourceId)
                return@Observer
            }

            if (loginResult.success != null) {
                // Welcome user
                updateUiWithUser(loginResult.success)

                // Start home activity
                startActivity(
                    Intent(baseContext, HomeActivity::class.java)
                )
                //Complete and destroy login activity once successful
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        username.afterTextChanged {
            authViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                authViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        authViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            auth.setOnClickListener {
                loading.visibility = View.VISIBLE
                authViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    /**
     * Shows welcome message.
     */
    private fun updateUiWithUser(user: User) {
        val welcome = getString(R.string.auth_content_welcome, user.username)
        Toast.makeText(baseContext, welcome, Toast.LENGTH_SHORT).show()
    }

    /**
     * Shows error message.
     */
    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(baseContext, errorString, Toast.LENGTH_SHORT).show()
    }

    /**
     * Extension function to simplify setting an afterTextChanged action to EditText components.
     */
    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }
}
