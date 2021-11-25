package com.shajt.caffshop.ui.user

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shajt.caffshop.databinding.FragmentDetailedUserBinding
import com.shajt.caffshop.viewmodels.user.DetailedUserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedUserFragment : Fragment() {

    private var _binding: FragmentDetailedUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var detailedUserViewModel: DetailedUserViewModel

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
        /*detailedUserViewModel = ViewModelProvider(
            this,
            (requireActivity().application as CaffShopApplication).caffShopViewModelFactory
        )[DetailedUserViewModel::class.java]*/

        detailedUserViewModel = ViewModelProvider(this)[DetailedUserViewModel::class.java]
    }

}