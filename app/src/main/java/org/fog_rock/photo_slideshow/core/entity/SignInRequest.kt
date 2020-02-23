package org.fog_rock.photo_slideshow.core.entity

import org.fog_rock.photo_slideshow.R

enum class SignInRequest(val code: Int, val failedTitle: Int, val failedMessage: Int) {

    RUNTIME_PERMISSIONS(
        1000,
        R.string.failed_runtime_permissions_title,
        R.string.failed_runtime_permissions_message
    ),

    GOOGLE_SIGN_IN(
        1001,
        R.string.failed_google_sign_in_title,
        R.string.failed_google_sign_in_message
    ),

    COMPLETED(9999, 0, 0)
}