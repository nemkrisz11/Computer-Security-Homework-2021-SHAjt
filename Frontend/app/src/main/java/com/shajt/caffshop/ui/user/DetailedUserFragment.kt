package com.shajt.caffshop.ui.user

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shajt.caffshop.app.CaffShopApplication
import com.shajt.caffshop.databinding.FragmentHomeBinding
import com.shajt.caffshop.viewmodels.user.DetailedUserViewModel

class DetailedUserFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DetailedUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as CaffShopApplication).caffShopViewModelFactory
        )[DetailedUserViewModel::class.java]
    }

}