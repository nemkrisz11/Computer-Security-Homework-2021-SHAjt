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
import android.widget.EditText
import com.shajt.caffshop.databinding.ActivityAuthBinding

import com.shajt.caffshop.R
import com.shajt.caffshop.data.models.User
import com.shajt.caffshop.ui.commons.DisplayMessage
import com.shajt.caffshop.ui.home.HomeActivity
import com.shajt.caffshop.viewmodels.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var binding: ActivityAuthBinding

    private var loginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val modeToggle = binding.modeToggle
        val login = binding.login
        val register = binding.register
        val username = binding.username
        val password = binding.password
        val passwordAgain = binding.passwordAgain
        val auth = binding.auth
        val loading = binding.loading

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        authViewModel.authFormState.observe(this, Observer {
            val authState = it ?: return@Observer

            // Disable login button unless both username / password is valid
            auth.isEnabled = authState.isDataValid

            // Show errors
            if (authState.usernameError != null) {
                username.error = getString(authState.usernameError.errorStringResourceId)
            }
            if (authState.passwordError != null) {
                password.error = getString(authState.passwordError.errorStringResourceId)
            }
            if (authState.passwordAgainError != null) {
                passwordAgain.error = getString(authState.passwordAgainError.errorStringResourceId)
            }
        })

        authViewModel.authResult.observe(this, Observer {
            val authResult = it ?: return@Observer

            // Hide progress indicator
            loading.visibility = View.GONE

            // Show register/login error message
            if (authResult.error != null) {
                changeAllControlsIsEnabled()
                showAuthFailed(authResult.error.errorStringResourceId)
                return@Observer
            }

            if (authResult.success != null) {
                // Welcome user
                updateUiWithUser(authResult.success)

                // Start home activity
                startActivity(
                    Intent(baseContext, HomeActivity::class.java)
                )
                // Complete and destroy login activity once successful
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        // Set the login checked as default
        modeToggle.check(login.id)

        modeToggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            // If login mode is selected show login related controls else register related
            if (checkedId == login.id && isChecked) {
                loginMode = true
                passwordAgain.visibility = View.GONE
                passwordAgain.editText!!.text.clear()
            } else if (checkedId == register.id && isChecked) {
                loginMode = false
                passwordAgain.editText!!.text.clear()
                passwordAgain.visibility = View.VISIBLE
            }
        }

        // Validate data on change
        username.editText!!.afterTextChanged {
            authViewModel.authDataChanged(
                username.editText!!.text.toString(),
                password.editText!!.text.toString(),
                if (loginMode) {
                    null
                } else {
                    passwordAgain.editText!!.text.toString()
                }
            )
        }

        // Validate data on change
        password.apply {
            editText!!.afterTextChanged {
                authViewModel.authDataChanged(
                    username.editText!!.text.toString(),
                    password.editText!!.text.toString(),
                    if (loginMode) {
                        null
                    } else {
                        passwordAgain.editText!!.text.toString()
                    }
                )
            }
        }

        // Validate data on change
        passwordAgain.apply {
            editText!!.afterTextChanged {
                authViewModel.authDataChanged(
                    username.editText!!.text.toString(),
                    password.editText!!.text.toString(),
                    passwordAgain.editText!!.text.toString()
                )
            }
        }

        auth.setOnClickListener {
            loading.visibility = View.VISIBLE
            changeAllControlsIsEnabled(false)
            if (loginMode) {
                authViewModel.login(
                    username.editText!!.text.toString(),
                    password.editText!!.text.toString()
                )
            } else {
                authViewModel.register(
                    username.editText!!.text.toString(),
                    password.editText!!.text.toString()
                )
            }
        }
    }

    /**
     * Changes the enabled sate of the controls.
     */
    private fun changeAllControlsIsEnabled(isEnabled: Boolean = true) {
        binding.modeToggle.isEnabled = isEnabled
        binding.login.isEnabled = isEnabled
        binding.register.isEnabled = isEnabled
        binding.username.isEnabled = isEnabled
        binding.password.isEnabled = isEnabled
        binding.passwordAgain.isEnabled = isEnabled
        binding.auth.isEnabled = isEnabled
    }

    /**
     * Shows welcome message.
     */
    private fun updateUiWithUser(user: User) {
        val welcome = getString(R.string.auth_content_welcome, user.username)
        DisplayMessage.displayToast(baseContext, welcome)
    }

    /**
     * Shows error message.
     */
    private fun showAuthFailed(@StringRes errorString: Int) {
        DisplayMessage.displaySnackbar(binding.root, errorString)
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
