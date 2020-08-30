package org.fog_rock.photo_slideshow.app.splash.entity

import androidx.annotation.StringRes
import org.fog_rock.photo_slideshow.R

/**
 * サインインシーケンスにおけるリクエスト.
 */
enum class SignInRequest(
    val code: Int,
    @StringRes val failedTitle: Int,
    @StringRes val failedMessage: Int
) {

    /**
     * ランタイムパーミッション
     */
    RUNTIME_PERMISSIONS(
        1000,
        R.string.failed_runtime_permissions_title,
        R.string.failed_runtime_permissions_message
    ),

    /**
     * Googleサインイン
     */
    GOOGLE_SIGN_IN(
        1001,
        R.string.failed_google_sign_in_title,
        R.string.failed_google_sign_in_message
    ),

    /**
     * ユーザー情報更新
     */
    UPDATE_USER_INFO(
        1010,
        R.string.failed_update_user_info_in_title,
        R.string.failed_update_user_info_in_message
    ),

    /**
     * 完了
     */
    COMPLETED(
        1111,
        R.string.empty,
        R.string.empty
    ),

    /**
     * 不明
     */
    UNKNOWN(
        9999,
        R.string.failed_unknown_request_title,
        R.string.failed_unknown_request_message
    ),
    ;

    companion object {
        /**
         * コードナンバーからリクエストへコンバートする.
         */
        fun convertFromCode(code: Int): SignInRequest =
            values().find { it.code == code } ?: UNKNOWN
    }

    /**
     * 次のシーケンスリクエストを取得する.
     */
    fun next(): SignInRequest = when (this) {
        RUNTIME_PERMISSIONS -> GOOGLE_SIGN_IN
        GOOGLE_SIGN_IN -> UPDATE_USER_INFO
        UPDATE_USER_INFO -> COMPLETED
        else -> UNKNOWN
    }
}