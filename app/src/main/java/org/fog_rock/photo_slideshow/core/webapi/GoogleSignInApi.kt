package org.fog_rock.photo_slideshow.core.webapi

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

/**
 * Googleサインインに関連するAPI.
 */
interface GoogleSignInApi {

    /**
     * サイレントサインイン要求.
     * コルーチン内で呼び出すこと.
     * https://developers.google.com/identity/sign-in/android/backend-auth
     */
    suspend fun requestSilentSignIn(): ApiResult

    /**
     * サインアウト要求.
     * コルーチン内で呼び出すこと.
     * https://developers.google.com/identity/sign-in/android/disconnect
     */
    suspend fun requestSignOut(): ApiResult

    /**
     * アカウントアクセス破棄要求.
     * コルーチン内で呼び出すこと.
     * https://developers.google.com/identity/sign-in/android/disconnect
     */
    suspend fun requestRevokeAccess(): ApiResult

    /**
     * 現在サインインしているGoogleアカウント取得.
     */
    fun getSignedInAccount(context: Context): GoogleSignInAccount?

    /**
     * Googleアカウントでのユーザーサインインに成功したか.
     */
    fun isSucceededUserSignIn(data: Intent?): Boolean
}