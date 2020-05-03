package org.fog_rock.photo_slideshow.core.webapi

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Googleサインインに関連するAPI.
 */
interface GoogleSignInApi {

    companion object {
        /**
         * 現在Googleアカウントでサインインしているか確認.
         */
        fun isSignedInAccount(context: Context): Boolean =
            getSignedInAccount(context) != null

        /**
         * 現在サインインしているGoogleアカウント取得.
         */
        fun getSignedInAccount(context: Context): GoogleSignInAccount? =
            GoogleSignIn.getLastSignedInAccount(context)

        /**
         * Googleアカウントでのユーザーサインインに成功したか.
         */
        fun isSucceededUserSignIn(data: Intent?): Boolean =
            GoogleSignIn.getSignedInAccountFromIntent(data).isSuccessful
    }

    /**
     * 結果取得.
     */
    enum class Result {
        /**
         * 成功した.
         */
        SUCCEEDED,

        /**
         * 失敗した.
         */
        FAILED,

        /**
         * キャンセルした.
         */
        CANCELED;
    }

    /**
     * サイレントサインイン要求.
     * コルーチン内で呼び出すこと.
     * https://developers.google.com/identity/sign-in/android/backend-auth
     */
    suspend fun requestSilentSignIn(): Result

    /**
     * サインアウト要求.
     * コルーチン内で呼び出すこと.
     * https://developers.google.com/identity/sign-in/android/disconnect
     */
    suspend fun requestSignOut(): Result

    /**
     * アカウントアクセス破棄要求.
     * コルーチン内で呼び出すこと.
     * https://developers.google.com/identity/sign-in/android/disconnect
     */
    suspend fun requestRevokeAccess(): Result
}