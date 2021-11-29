package com.shajt.caffshop.data.enums

import com.shajt.caffshop.R

/**
 * Enum class which contains the most frequent errors and the connecting resource strings.
 */
enum class ErrorMessage(val errorStringResourceId: Int) {

    NETWORK_ERROR(R.string.error_network_error),
    USER_LIST_REQUEST_FAILED(R.string.error_user_list_request_failed),
    USER_REQUEST_FAILED(R.string.error_user_request_failed),
    USER_DELETE_FAILED(R.string.error_user_delete_failed),
    REGISTRATION_FAILED(R.string.error_registration_failed),
    LOGIN_FAILED(R.string.error_login_failed),
    LOGOUT_FAILED(R.string.error_logout_failed),
    MODIFY_PASSWORD_FAILED(R.string.error_modify_password_failed),
    AUTH_FAILED(R.string.error_auth_failed),
    CAFF_REQUEST_FAILED(R.string.error_caff_request_failed),
    CAFF_DELETE_FAILED(R.string.error_caff_delete_failed),
    CAFF_SEARCH_FAILED(R.string.error_caff_search_failed),
    CAFF_UPLOAD_FAILED(R.string.error_caff_upload_failed),
    CAFF_DOWNLOAD_FAILED(R.string.error_caff_download_failed),
    COMMENT_LIST_REQUEST_FAILED(R.string.error_comment_list_request_failed),
    COMMENT_POST_FAILED(R.string.error_comment_post_failed),
    COMMENT_DELETE_FAILED(R.string.error_comment_delete_failed),
    REQUIRED_FIELD_IS_EMPTY(R.string.error_required_field_is_empty),
    INVALID_USERNAME_OR_PASSWORD(R.string.error_invalid_username_or_password),
    INVALID_USERNAME(R.string.error_invalid_username),
    INVALID_PASSWORD(R.string.error_invalid_password),
    INVALID_PASSWORD_AGAIN(R.string.error_invalid_password_again),
    INVALID_USER_DATA(R.string.error_invalid_user_data),
    INVALID_FILE_EXTENSION(R.string.error_invalid_file_extension)
}