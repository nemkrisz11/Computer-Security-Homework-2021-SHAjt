package com.shajt.caffshop.viewmodels.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shajt.caffshop.data.CaffShopRepository
import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.models.Caff
import com.shajt.caffshop.data.models.caff.SearchCaffQuery
import com.shajt.caffshop.data.models.caff.SearchCaffsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val caffShopRepository: CaffShopRepository
) : ViewModel() {

    private var _error = MutableLiveData<ErrorMessage>()
    val error: LiveData<ErrorMessage> = _error

    private var _caffs = MutableLiveData<List<Caff>>()
    val caffs: LiveData<List<Caff>> = _caffs

    private var actualPage = 0
    private var totalPages = 1
    private lateinit var prevSearchCaffQuery: SearchCaffQuery


    /**
     * Searches caffs.
     *
     * @param searchCaffQuery caff search query
     * @return search started or not
     */
    fun searchCaffs(searchCaffQuery: SearchCaffQuery): Boolean {
        if (::prevSearchCaffQuery.isInitialized) {
            if (prevSearchCaffQuery == searchCaffQuery && actualPage >= totalPages) {
                // Not searching if same query or all loaded
                return false
            } else if (prevSearchCaffQuery != searchCaffQuery) {
                // New search then reset values
                actualPage = 0
                totalPages = 1
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            val searchCaffResult: SearchCaffsResult
            with(searchCaffQuery) {
                searchCaffResult = caffShopRepository.searchCaffs(
                    searchTerm, username, uploaderName, creationDate, uploadDate, actualPage + 1
                )
            }
            if (searchCaffResult.success != null) {
                actualPage++
                totalPages = searchCaffResult.success.totalPages
                if (caffs.value != null && actualPage >= 2) {
                    val mergedList = _caffs.value!!.toMutableList().apply {
                        addAll(searchCaffResult.success.caffs)
                    }
                    _caffs.postValue(mergedList)
                } else {
                    prevSearchCaffQuery = searchCaffQuery
                    _caffs.postValue(searchCaffResult.success.caffs)
                }
            } else {
                _error.postValue(searchCaffResult.error!!)
            }
        }
        return true
    }
}