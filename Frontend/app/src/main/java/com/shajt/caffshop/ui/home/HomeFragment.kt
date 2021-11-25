package com.shajt.caffshop.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.shajt.caffshop.databinding.FragmentHomeBinding
import com.shajt.caffshop.viewmodels.home.HomeViewModel
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.shajt.caffshop.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_users)
        item.isVisible = homeViewModel.userIsAdmin
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId)
        {
            R.id.action_users -> {
                // TODO change to activity
                Toast.makeText(activity, "users clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_profile -> {
                // TODO change to activity
                true
            }
            R.id.action_search -> {
                // TODO make search
                Toast.makeText(activity, "search clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }
}