package com.shajt.caffshop.network

import com.shajt.caffshop.data.enums.ErrorMessage
import com.shajt.caffshop.data.enums.ErrorMessage.*

object ServerErrorMapper {

    fun mapServerError(errorId: Int, default: ErrorMessage = NETWORK_ERROR): ErrorMessage {
        return when (errorId) {
            1 -> INVALID_TOKEN
            2 -> AUTHORIZATION_FAILED
            3 -> INVALID_PAGE_NUMBER
            4 -> INVALID_PERPAGE_NUMBER
            100 -> NOT_VALID_USERNAME
            101 -> USERNAME_CANNOT_BE_EMPTY
            102 -> USERNAME_TOO_SHORT
            103 -> USERNAME_TOO_LONG
            104 -> USERNAME_IS_ALREADY_TAKEN
            110 -> NOT_VALID_PASSWORD
            111 -> PASSWORD_CANNOT_BE_EMPTY
            112 -> PASSWORD_TOO_SHORT
            113 -> PASSWORD_TOO_LONG
            114 -> PASSWORD_TOO_WEAK
            120 -> INVALID_USERNAME_OR_PASSWORD
            199 -> USER_NOT_FOUND
            200 -> CAFF_PARSE_FAILED
            299 -> CAFF_NOT_FOUND
            300 -> COMMENT_CANNOT_BE_EMPTY
            301 -> COMMENT_TOO_LONG
            399 -> COMMENT_NOT_FOUND
            else -> default
        }
    }
}