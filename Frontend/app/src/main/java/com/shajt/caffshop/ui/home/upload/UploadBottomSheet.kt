package com.shajt.caffshop.ui.home.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomsheetUploadCaffBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        val fileName = binding.fileName
        val select = binding.select
        val name = binding.name
        val upload = binding.upload

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