package com.shajt.caffshop.ui.home

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.shajt.caffshop.databinding.FragmentHomeBinding
import com.shajt.caffshop.viewmodels.home.HomeViewModel
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shajt.caffshop.R
import com.shajt.caffshop.ui.caffdetails.CaffDetailsActivity
import com.shajt.caffshop.ui.commons.CaffsRecyclerViewAdapter
import com.shajt.caffshop.ui.commons.DisplayMessage
import com.shajt.caffshop.ui.home.upload.UploadBottomSheet
import com.shajt.caffshop.ui.search.SearchActivity
import com.shajt.caffshop.ui.start.StartActivity
import com.shajt.caffshop.ui.user.DetailedUserActivity
import com.shajt.caffshop.ui.users.UsersActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    private var permission = false

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
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        checkPermission()
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                permission = isGranted
                UploadBottomSheet().show(childFragmentManager, UploadBottomSheet.TAG)
            }
        }

        val caffs = binding.caffs
        val upload = binding.upload
        val loading = binding.loading

        with(caffs) {
            val gridLayoutManager = GridLayoutManager(context, 2)
            layoutManager = gridLayoutManager

            val listAdapter = CaffsRecyclerViewAdapter(this@HomeFragment::onCaffSelect)
            adapter = listAdapter

            homeViewModel.caffs.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it)
                loading.visibility = View.GONE
            })

            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (listAdapter.itemCount == 0) {
                    return@setOnScrollChangeListener
                }
                if (gridLayoutManager.findLastVisibleItemPosition() == listAdapter.itemCount - 1) {
                    homeViewModel.getMoreCaffs()
                }
            }
        }

        upload.setOnClickListener {
            checkPermission()
            if (permission) {
                UploadBottomSheet().show(childFragmentManager, UploadBottomSheet.TAG)
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        homeViewModel.uploadSuccess.observe(viewLifecycleOwner, Observer {
            DisplayMessage.displaySnackbar(binding.root, R.string.upload_content_successful, upload)
        })

        homeViewModel.error.observe(viewLifecycleOwner, Observer {
            DisplayMessage.displaySnackbar(binding.root, it.errorStringResourceId, upload)
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_users)
        item.isVisible = homeViewModel.userIsAdmin
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_users -> {
                startActivity(
                    Intent(activity, UsersActivity::class.java)
                )
                true
            }
            R.id.action_profile -> {
                startActivity(
                    Intent(activity, DetailedUserActivity::class.java)
                )
                true
            }
            R.id.action_search -> {
                startActivity(
                    Intent(activity, SearchActivity::class.java)
                )
                true
            }
            R.id.action_logout -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.logout_title_logout)
                    .setMessage(R.string.logout_content_logout)
                    .setPositiveButton(R.string.logout_action_positive, ::dialogOnClick)
                    .setNegativeButton(R.string.logout_action_negative, ::dialogOnClick)
                    .setCancelable(true)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkPermission() {
        permission = when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {
                true
            }
            else -> {
                false
            }
        }
    }

    private fun onCaffSelect(caffId: String) {
        startActivity(
            Intent(context, CaffDetailsActivity::class.java).apply {
                putExtra(CaffDetailsActivity.ARG_CAFF_ID, caffId)
            }
        )
    }

    private fun dialogOnClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                homeViewModel.logout()
                dialog?.dismiss()
                startActivity(
                    Intent(requireContext(), StartActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
                with(requireActivity()) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            DialogInterface.BUTTON_NEGATIVE -> dialog?.dismiss()
        }
    }
}