package com.shajt.caffshop.ui.caffdetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.shajt.caffshop.databinding.FragmentCaffDetailsBinding
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.shajt.caffshop.R
import com.shajt.caffshop.data.models.CaffAnimationImage
import com.shajt.caffshop.ui.commons.CreateCiff
import com.shajt.caffshop.ui.commons.DisplayMessage
import com.shajt.caffshop.ui.home.HomeActivity
import com.shajt.caffshop.viewmodels.caffdetails.CaffDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CaffDetailsFragment : Fragment() {

    companion object {

        private const val ARG_CAFF_ID = "caffId"
        private const val CALLBACK_ID_LOAD_DETAILS = 1
        private const val CALLBACK_ID_LOAD_COMMENTS = 2
        private const val CALLBACK_ID_POST_COMMENT = 3
        private const val CALLBACK_ID_DELETE_COMMENT = 4
        private const val CALLBACK_ID_DOWNLOAD_CAFF = 5
        private const val CALLBACK_ID_DELETE_CAFF = 6

        @JvmStatic
        fun newInstance(caffId: String) =
            CaffDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CAFF_ID, caffId)
                }
            }
    }

    private var _binding: FragmentCaffDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var caffDetailsViewModel: CaffDetailsViewModel

    private var caffId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            caffId = it.getString(ARG_CAFF_ID, "")
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

        caffDetailsViewModel.getCaffDetails(caffId, CALLBACK_ID_LOAD_DETAILS)
        caffDetailsViewModel.getMoreComments(caffId, CALLBACK_ID_LOAD_COMMENTS)

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
            createCiffImage(it.caffAnimationImage)
        })

        var isFullScreen = false
        var oldParams: ViewGroup.LayoutParams? = null
        ciff.setOnClickListener {
            isFullScreen = if (isFullScreen) {
                ciff.layoutParams = oldParams
                ciff.adjustViewBounds = true

                commentInput.visibility = View.VISIBLE
                send.visibility = View.VISIBLE

                !isFullScreen
            } else {
                oldParams = ciff.layoutParams
                ciff.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                ciff.scaleType = ImageView.ScaleType.FIT_CENTER

                commentInput.visibility = View.INVISIBLE
                send.visibility = View.INVISIBLE

                !isFullScreen
            }
        }

        download.setOnClickListener {
            caffDetailsViewModel.downloadCaff(
                caffId, name.text.toString().replace("\"", ""),
                CALLBACK_ID_DOWNLOAD_CAFF
            )
        }

        caffDetailsViewModel.downloadCaffResult.observe(viewLifecycleOwner, Observer {
            DisplayMessage.displaySnackbar(
                binding.root,
                getString(R.string.caff_details_content_download_success, it),
                binding.commentInput
            )
        })

        with(delete) {
            if (caffDetailsViewModel.userIsAdmin) {
                visibility = View.VISIBLE
                setOnClickListener {
                    caffDetailsViewModel.deleteCaff(caffId, CALLBACK_ID_DELETE_CAFF)
                }
            }
        }

        caffDetailsViewModel.deleteCaffResult.observe(viewLifecycleOwner, Observer {
            DisplayMessage.displayToast(requireContext(), R.string.caff_details_content_caff_delete_success)
            startActivity(
                Intent(requireContext(), HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            with(requireActivity()) {
                setResult(Activity.RESULT_OK)
                finish()
            }
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
                if (listAdapter.itemCount == 0) {
                    return@setOnScrollChangeListener
                }
                if (linearLayoutManager.findLastVisibleItemPosition() == listAdapter.itemCount - 1) {
                    caffDetailsViewModel.getMoreComments(caffId, CALLBACK_ID_LOAD_COMMENTS)
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
            val trimmed = validateCommentText(commentInput.editText!!.text.toString())
            if (trimmed != null) {
                caffDetailsViewModel.postComment(caffId, trimmed, CALLBACK_ID_POST_COMMENT)
                commentInput.editText!!.text.clear()
            }
        }

        caffDetailsViewModel.error.observe(viewLifecycleOwner, Observer {
            when (it.second) {
                CALLBACK_ID_LOAD_DETAILS -> {
                    loading.visibility = View.GONE
                    commentsLoading.visibility = View.GONE
                }
                CALLBACK_ID_LOAD_COMMENTS -> commentsLoading.visibility = View.GONE
            }
            DisplayMessage.displaySnackbar(
                binding.root,
                it.first.errorStringResourceId,
                binding.commentInput
            )
        })
    }

    private fun createCiffImage(caffAnimationImage: CaffAnimationImage) {
        lifecycleScope.launch(Dispatchers.IO) {
            val bitmap = CreateCiff.createCiff(caffAnimationImage.pixelValues)
            binding.ciff.post {
                binding.ciff.setImageBitmap(bitmap)
            }
        }
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