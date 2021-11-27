package com.shajt.caffshop.ui.search

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.shajt.caffshop.data.models.caff.SearchCaffQuery
import com.shajt.caffshop.databinding.FragmentSearchBinding
import com.shajt.caffshop.ui.caffdetails.CaffDetailsActivity
import com.shajt.caffshop.ui.commons.CaffsRecyclerViewAdapter
import com.shajt.caffshop.ui.commons.DisplayMessage
import com.shajt.caffshop.ui.search.date.DatePickerFragment
import com.shajt.caffshop.viewmodels.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchViewModel: SearchViewModel

    private var searchCaffQuery = SearchCaffQuery()
    private var selectedCreationDate: Long? = null
    private var selectedUploadDate: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        val caffs = binding.caffs
        val searchTerm = binding.searchTerm
        val creator = binding.creator
        val uploader = binding.uploader
        val creationDate = binding.creationDate
        val uploadDate = binding.uploadDate
        val search = binding.search
        val clear = binding.clear
        val loading = binding.loading

        with(caffs) {
            val gridLayoutManager = GridLayoutManager(context, 2)
            layoutManager = gridLayoutManager

            val listAdapter = CaffsRecyclerViewAdapter(this@SearchFragment::onCaffSelect)
            adapter = listAdapter

            searchViewModel.caffs.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it)
                loading.visibility = View.GONE
            })

            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (gridLayoutManager.findLastVisibleItemPosition() == listAdapter.itemCount - 1) {
                    searchViewModel.searchCaffs(searchCaffQuery)
                }
            }
        }

        creationDate.setOnClickListener {
            DatePickerFragment(creationDate, selectedCreationDate).show(childFragmentManager, null)
        }

        uploadDate.setOnClickListener {
            DatePickerFragment(uploadDate, selectedUploadDate).show(childFragmentManager, null)
        }

        clear.setOnClickListener {
            searchTerm.editText!!.text.clear()
            creator.editText!!.text.clear()
            uploader.editText!!.text.clear()
            creationDate.text = null
            uploadDate.text = null
        }

        search.setOnClickListener {
            searchCaffQuery = SearchCaffQuery(
                validateText(searchTerm.editText!!.text.toString()),
                validateText(creator.editText!!.text.toString()),
                validateText(uploader.editText!!.text.toString()),
                selectedCreationDate,
                selectedUploadDate
            )
            searchViewModel.searchCaffs(searchCaffQuery)
        }

        searchViewModel.error.observe(viewLifecycleOwner, Observer {
            DisplayMessage.displaySnackbar(binding.root, it.errorStringResourceId)
        })
    }

    private fun validateText(text: String): String? {
        val trimmed = text.trim()
        return if (trimmed.isBlank()) {
            null
        } else {
            trimmed
        }
    }

    private fun onCaffSelect(caffId: Int) {
        startActivity(
            Intent(context, CaffDetailsActivity::class.java).apply {
                putExtra(CaffDetailsActivity.ARG_CAFF_ID, caffId)
            }
        )
    }
}