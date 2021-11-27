package com.shajt.caffshop.ui.caffdetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.shajt.caffshop.databinding.FragmentCaffDetailsBinding
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shajt.caffshop.data.models.CaffAnimationImage
import com.shajt.caffshop.viewmodels.caffdetails.CaffDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CaffDetailsFragment : Fragment() {

    companion object {

        private const val ARG_CAFF_ID = "caffId"

        @JvmStatic
        fun newInstance(caffId: Int) =
            CaffDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_CAFF_ID, caffId)
                }
            }
    }

    private var _binding: FragmentCaffDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var caffDetailsViewModel: CaffDetailsViewModel

    private var caffId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            caffId = it.getInt(ARG_CAFF_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaffDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        caffDetailsViewModel = ViewModelProvider(this)[CaffDetailsViewModel::class.java]

        caffDetailsViewModel.getCaffDetails(caffId)

        val ciff = binding.ciff
        val name = binding.name
        val creator = binding.creator
        val creation = binding.creation
        val uploader = binding.uploader
        val upload = binding.upload
        val numOfCiffs = binding.numOfCiffs
        val download = binding.download
        val delete = binding.delete
        val comments = binding.comments
        val commentsLoading = binding.commentLoading
        val commentInput = binding.commentInput
        val send = binding.send
        val loading = binding.loading

        caffDetailsViewModel.caff.observe(viewLifecycleOwner, Observer {
            name.text = it.caffName
            creator.text = it.creator
            creation.text = SimpleDateFormat.getDateInstance().format(Date(it.creationDate))
            uploader.text = it.uploaderName
            upload.text = SimpleDateFormat.getDateInstance().format(Date(it.uploadDate))
            numOfCiffs.text = it.numOfCiffs.toString()
            loading.visibility = View.GONE
        })

        download.setOnClickListener {
            caffDetailsViewModel.downloadCaff(caffId, requireContext())
        }

        with(delete) {
            if (caffDetailsViewModel.userIsAdmin) {
                visibility = View.VISIBLE
                setOnClickListener {
                    caffDetailsViewModel.deleteCaff(caffId)
                }
            }
        }

        caffDetailsViewModel.deleteCaffResult.observe(viewLifecycleOwner, Observer {
            requireActivity().onBackPressed()
        })

        with(comments) {
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager

            val listAdapter = CaffDetailsCommentsRecyclerViewAdapter(
                caffDetailsViewModel.userIsAdmin,
                caffDetailsViewModel::deleteComment
            )
            adapter = listAdapter

            caffDetailsViewModel.comments.observe(viewLifecycleOwner, Observer {
                listAdapter.submitList(it)
                commentsLoading.visibility = View.GONE
            })

            setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                if (linearLayoutManager.findLastVisibleItemPosition() == listAdapter.itemCount - 1) {
                    caffDetailsViewModel.getMoreComments(caffId)
                }
            }

            /*ItemTouchHelper(
                GenericSwipeCallback({ pos ->
                    caffDetailsViewModel.deleteComment(listAdapter.currentList[pos])
                })
            ).apply {
                attachToRecyclerView(this@with)
            }*/
        }

        send.setOnClickListener {
            //val trimmed = validateCommentText(commentInput.text.toString())
            val trimmed = validateCommentText(commentInput.editText!!.text.toString())
            if (trimmed != null) {
                caffDetailsViewModel.postComment(caffId, trimmed)
                commentInput.editText!!.text.clear()
            }
        }
    }

    private fun createCiffImage(caffAnimationImage: CaffAnimationImage) {
        // TODO
    }

    private fun validateCommentText(text: String): String? {
        val trimmed = text.trim()
        return if (trimmed.isBlank()) {
            null
        } else {
            trimmed
        }
    }
}