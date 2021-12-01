package com.shajt.caffshop.data.models

/**
 * User list representation with total page number.
 */
data class UserList(
    val users: List<UserData>,
    val totalPages: Int
)
