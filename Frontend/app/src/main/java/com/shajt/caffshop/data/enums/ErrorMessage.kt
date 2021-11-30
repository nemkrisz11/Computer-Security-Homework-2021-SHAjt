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
    INVALID_FILE_EXTENSION(R.string.error_invalid_file_extension),
    INVALID_TOKEN(R.string.error_invalid_token),
    AUTHORIZATION_FAILED(R.string.error_authorization_failed),
    INVALID_PAGE_NUMBER(R.string.error_invalid_page_number),
    INVALID_PERPAGE_NUMBER(R.string.error_invalid_perpage_number),
    NOT_VALID_USERNAME(R.string.error_not_valid_username),
    USERNAME_CANNOT_BE_EMPTY(R.string.error_username_cannot_be_empty),
    USERNAME_TOO_SHORT(R.string.error_username_too_short),
    USERNAME_TOO_LONG(R.string.error_username_too_long),
    USERNAME_IS_ALREADY_TAKEN(R.string.error_username_is_already_taken),
    NOT_VALID_PASSWORD(R.string.error_not_valid_password),
    PASSWORD_CANNOT_BE_EMPTY(R.string.error_password_cannot_be_empty),
    PASSWORD_TOO_SHORT(R.string.error_password_too_short),
    PASSWORD_TOO_LONG(R.string.error_password_too_long),
    PASSWORD_TOO_WEAK(R.string.error_password_too_weak),
    USER_NOT_FOUND(R.string.error_user_not_found),
    CAFF_PARSE_FAILED(R.string.error_caff_parse_failed),
    CAFF_NOT_FOUND(R.string.error_caff_not_found),
    COMMENT_CANNOT_BE_EMPTY(R.string.error_comment_cannot_be_empty),
    COMMENT_TOO_LONG(R.string.error_comment_too_long),
    COMMENT_NOT_FOUND(R.string.error_comment_not_found)
}