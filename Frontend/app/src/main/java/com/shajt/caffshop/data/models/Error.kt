package com.shajt.caffshop.data.models

import com.shajt.caffshop.R

/**
 * Enum class which contains the most frequent errors and the connecting resource strings.
 */
enum class Error(val errorStringResourceId: Int) {

    REQUIRED_FIELD_IS_EMPTY(R.string.error_required_field_is_empty),
    INVALID_USERNAME_OR_PASSWORD(R.string.error_invalid_username_or_password),
    INVALID_USERNAME(R.string.error_invalid_username),
    INVALID_PASSWORD(R.string.error_invalid_password),
    INVALID_PASSWORD_AGAIN(R.string.error_invalid_password_again),
    AUTH_FAILED(R.string.error_auth_failed)

}