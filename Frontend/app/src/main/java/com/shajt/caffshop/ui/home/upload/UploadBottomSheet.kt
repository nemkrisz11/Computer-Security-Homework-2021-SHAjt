package com.shajt.caffshop.ui.home.upload

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.database.getStringOrNull
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shajt.caffshop.R
import com.shajt.caffshop.databinding.BottomsheetUploadCaffBinding
import com.shajt.caffshop.viewmodels.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UploadBottomSheet : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "UploadBottomSheet"
    }

    private var _binding: BottomsheetUploadCaffBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    private var fileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetUploadCaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        val fileName = binding.fileName
        val select = binding.select
        val name = binding.name
        val upload = binding.upload

        val fileSelectionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                fileUri = result.data?.data
                fileName.text = getFileName()
            }
        }

        fileName.isSelected = true

        select.setOnClickListener {
            fileSelectionLauncher.launch(
                Intent.createChooser(
                    Intent().apply {
                        type = "application/octet-stream"
                        action = Intent.ACTION_GET_CONTENT
                    },
                    getString(R.string.upload_title_select)
                )
            )
        }

        upload.setOnClickListener {
            fileUri?.let {
                val text = validateNameText(name.editText!!.text.toString())
                if (text == null) {
                    name.error = getString(R.string.error_invalid_caff_name)
                } else {
                    homeViewModel.uploadCaff(it, text, requireContext())
                    dismiss()
                }
            }
        }

        name.editText!!.doAfterTextChanged {
            name.error = null
        }

    }

    private fun getFileName(): String {
        var name = getString(R.string.upload_content_file_name)
        fileUri?.let {
            requireContext().contentResolver.query(it, null, null, null, null)
        }?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getStringOrNull(nameIndex)?.let { n ->
                name = n
            }
        }
        return name
    }

    private fun validateNameText(text: String): String? {
        val trimmed = text.trim()
        return if (trimmed.isBlank()) {
            null
        } else {
            trimmed
        }
    }
}