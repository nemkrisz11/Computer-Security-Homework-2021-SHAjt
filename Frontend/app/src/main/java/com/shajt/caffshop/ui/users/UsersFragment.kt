package com.shajt.caffshop.ui.users

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.shajt.caffshop.R
import com.shajt.caffshop.databinding.FragmentDetailedUserBinding
import com.shajt.caffshop.databinding.FragmentUsersBinding
import com.shajt.caffshop.ui.caffdetails.CaffDetailsCommentsRecyclerViewAdapter
import com.shajt.caffshop.ui.user.DetailedUserFragment
import com.shajt.caffshop.viewmodels.user.DetailedUserViewModel
import com.shajt.caffshop.viewmodels.users.UsersViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsersFragment : Fragment() {

    companion object {

        @JvmStatic
        fun newInstance() = UsersFragment()
    }

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var usersViewModel: UsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usersViewModel = ViewModelProvider(this)[UsersViewModel::class.java]

        usersViewModel.getMoreUsers()

        val users = binding.usersList
        val loading = binding.loading

        with(users) {
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager

            val listAdapter = UsersRecyclerViewAdapter()
            adapter = listAdapter

            usersViewModel.users.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it)
                loading.visibility = View.GONE
            })

            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (linearLayoutManager.findLastVisibleItemPosition() == listAdapter.itemCount - 1) {
                    usersViewModel.getMoreUsers()
                }
            }
        }
    }

}