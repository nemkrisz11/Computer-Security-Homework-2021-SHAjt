package com.shajt.caffshop.ui.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.shajt.caffshop.app.CaffShopApplication
import com.shajt.caffshop.databinding.FragmentHomeBinding
import com.shajt.caffshop.viewmodels.home.HomeViewModel
import android.content.Intent
import android.view.*
import android.widget.Toast
import com.shajt.caffshop.R
import com.shajt.caffshop.ui.user.DetailedUserActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

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
        viewModel = ViewModelProvider(
            this,
            (requireActivity().application as CaffShopApplication).caffShopViewModelFactory
        )[HomeViewModel::class.java]
    }

    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_users)
        item.isVisible = viewModel.userIsAdmin
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