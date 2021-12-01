package com.shajt.caffshop.ui.user

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.shajt.caffshop.databinding.FragmentDetailedUserBinding
import com.shajt.caffshop.viewmodels.user.DetailedUserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import com.shajt.caffshop.R
import com.shajt.caffshop.ui.commons.DisplayMessage
import com.shajt.caffshop.ui.home.HomeActivity
import com.shajt.caffshop.utils.UserCredentialValidator

@AndroidEntryPoint
class DetailedUserFragment : Fragment() {

    companion object {

        private const val ARG_USERNAME = "username"

        @JvmStatic
        fun newInstance(username: String?) =
            DetailedUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }

    private var _binding: FragmentDetailedUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var detailedUserViewModel: DetailedUserViewModel

    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailedUserBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailedUserViewModel = ViewModelProvider(this)[DetailedUserViewModel::class.java]

        detailedUserViewModel.getUserDetails(username)

        val username = binding.username
        val isAdmin = binding.isAdmin
        val regDate = binding.regDate
        val loading = binding.loading
        val delete = binding.delete
        val newPasswordInput = binding.passwordInput
        val newPasswordAgainInput = binding.passwordAgainInput
        val changePassword = binding.changePassword
        val modifyPasswordContainer = binding.modifyPasswordContainer

        detailedUserViewModel.user.observe(viewLifecycleOwner, Observer {
            username.text = it.username
            isAdmin.text = it.isAdmin.toString()
            regDate.text = SimpleDateFormat.getDateInstance().format(Date(it.regDate))
            loading.visibility = View.GONE
            delete.isVisible = detailedUserViewModel.userIsAdmin && !it.isAdmin
            modifyPasswordContainer.isVisible =
                (detailedUserViewModel.userIsAdmin && !it.isAdmin) ||
                        it.username == detailedUserViewModel.currentUsername
        })

        delete.setOnClickListener {
            detailedUserViewModel.deleteUser(this.username)
        }

        detailedUserViewModel.deleteUserResult.observe(viewLifecycleOwner, Observer {
            DisplayMessage.displayToast(requireContext(), R.string.user_details_content_user_delete_success)
            startActivity(
                Intent(requireContext(), HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            with(requireActivity()) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        })

        changePassword.setOnClickListener {
            val passwordTrimmed =
                validatePasswordText(newPasswordInput.editText!!.text.toString())
            val passwordAgainTrimmed =
                validatePasswordText(newPasswordAgainInput.editText!!.text.toString())

            if (passwordTrimmed != null && passwordTrimmed == passwordAgainTrimmed) {
                detailedUserViewModel.changePassword(passwordTrimmed)
            } else if (passwordTrimmed == null) {
                newPasswordInput.error = getString(R.string.error_invalid_password)
            } else {
                newPasswordAgainInput.error = getString(R.string.error_invalid_password_again)
            }
        }

        detailedUserViewModel.modifyPasswordResult.observe(viewLifecycleOwner, Observer {
            DisplayMessage.displaySnackbar(binding.root, R.string.user_details_content_password_change_success)
        })

        newPasswordInput.editText!!.doAfterTextChanged {
            newPasswordInput.error = null
        }

        newPasswordAgainInput.editText!!.doAfterTextChanged {
            newPasswordAgainInput.error = null
        }

        detailedUserViewModel.error.observe(viewLifecycleOwner, Observer {
            DisplayMessage.displaySnackbar(binding.root, it.errorStringResourceId)
        })
    }

    /**
     * Validates password.
     */
    private fun validatePasswordText(text: String): String? {
        val trimmed = text.trim()
        return if (trimmed.isBlank() || !UserCredentialValidator.isPasswordValid(trimmed)) {
            null
        } else {
            trimmed
        }
    }
}