package org.fog_rock.photo_slideshow.core.webapi

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

/**
 * Googleサインインに関連するAPI.
 */
interface GoogleSignInApi {

    /**
     * 現在Googleアカウントでサインインしているか確認.
     */
    fun isSignedInAccount(): Boolean

    /**
     * 現在サインインしているGoogleアカウント取得.
     * @throws NullPointerException
     */
    fun getSignedInAccount(): GoogleSignInAccount

    /**
     * 現在サインインしているE-mailアドレス取得.
     * @throws NullPointerException
     */
    fun getSignedInEmailAddress(): String

    /**
     * Googleアカウントでのユーザーサインインに成功したか.
     */
    fun isSucceededUserSignIn(data: Intent?): Boolean

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
}