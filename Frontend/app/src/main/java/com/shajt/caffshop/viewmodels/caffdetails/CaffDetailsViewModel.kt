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
import kotlinx.coroutines.delay
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

    private var _downloadCaffResult = MutableLiveData<String>()
    val downloadCaffResult: LiveData<String> = _downloadCaffResult

    private var _deleteCaffResult = MutableLiveData<Boolean>()
    val deleteCaffResult: LiveData<Boolean> = _deleteCaffResult

    private var actualPage = 0
    private var totalPages = 1
    private var _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> = _comments

    val userIsAdmin: Boolean
        get() = caffShopRepository.localUser?.isAdmin!!


    fun getCaffDetails(caffId: String) {
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

    fun getMoreComments(caffId: String) {
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

    fun downloadCaff(caffId: String, fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val downloadResult = caffShopRepository.downloadCaff(caffId, fileName)
            if (downloadResult.success != null) {
                _downloadCaffResult.postValue(downloadResult.success!!)
            } else {
                _error.postValue(downloadResult.error!!)
            }
        }
    }

    fun deleteCaff(caffId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val deleteCaffResult = caffShopRepository.deleteCaff(caffId)
            if (deleteCaffResult.success) {
                _deleteCaffResult.postValue(true)
            } else {
                _error.postValue(deleteCaffResult.error!!)
            }
        }
    }

    fun postComment(caffId: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val postCommentResult = caffShopRepository.postComment(CommentToCreate(caffId, text))
            if (postCommentResult.success) {
                _comments.postValue(emptyList())
                actualPage = 0
                getMoreComments(caffId)
            } else {
                _error.postValue(postCommentResult.error!!)
            }
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(5000)
            val deleteResult = caffShopRepository.deleteComment(comment.id, comment.caffId)
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