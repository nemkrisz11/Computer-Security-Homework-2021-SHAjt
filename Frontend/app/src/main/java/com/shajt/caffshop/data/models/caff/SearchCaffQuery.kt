package com.shajt.caffshop.data.models.caff

data class SearchCaffQuery(
    var searchTerm: String? = null,
    var username: String? = null,
    var uploaderName: String? = null,
    var creationDate: Long? = null,
    var uploadDate: Long? = null
)