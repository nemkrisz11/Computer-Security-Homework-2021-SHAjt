package com.shajt.caffshop.viewmodels.caffdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.Caff
import com.shajt.caffshop.data.models.Comment
import com.shajt.caffshop.data.models.CommentToCreate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CaffDetailsViewModel @Inject constructor(
    private val caffShopRepository: CaffShopRepository
) : ViewModel() {

    private var _error = MutableLiveData<ErrorMessage>()
    val error: LiveData<ErrorMessage> = _error

    private var _caff = MutableLiveData<Caff>()
    val caff: LiveData<Caff> = _caff

    private var actualPage = 0
    private var totalPages = 1
    private var _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    val userIsAdmin: Boolean
        get() = caffShopRepository.localUser?.isAdmin!!


    fun getCaffDetails(caffId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val caffResult = caffShopRepository.getCaff(caffId)
            if (caffResult.success != null) {
                _caff.postValue(caffResult.success!!)
            } else {
                _error.postValue(caffResult.error!!)
            }
        }
        getMoreComments(caffId)
    }

    fun getMoreComments(caffId: Int) {
        if (actualPage >= totalPages) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val commentList = caffShopRepository.getComments(caffId, actualPage + 1)
            if (commentList.success != null) {
                actualPage++
                totalPages = commentList.success.totalPages
                if (comments.value != null) {
                    val mergedList = _comments.value!!.toMutableList().apply {
                        addAll(commentList.success.comments)
                    }
                    _comments.postValue(mergedList)
                } else {
                    _comments.postValue(commentList.success.comments)
                }
            } else {
                _error.postValue(commentList.error!!)
            }
        }
    }

    fun postComment(caffId: Int, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val postCommentResult = caffShopRepository.postComment(CommentToCreate(caffId, text))
            if (postCommentResult.success) {
                // TODO post should return comment id
            } else {
                _error.postValue(postCommentResult.error!!)
            }
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteResult = caffShopRepository.deleteComment(comment.id)
            if (deleteResult.success) {
                if (comments.value != null) {
                    val comments = comments.value!!.toMutableList().apply {
                        remove(comment)
                    }
                    _comments.postValue(comments)
                }
            } else {
                _error.postValue(deleteResult.error!!)
            }
        }
    }

}