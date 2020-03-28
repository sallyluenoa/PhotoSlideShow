package org.fog_rock.photo_slideshow.core.webapi

import android.content.Intent

/**
 * Googleサインインに関連するAPI.
 */
interface GoogleSignInApi {

    /**
     * Googleアカウントでのユーザーサインインに成功したか.
     */
    fun isSucceededUserSignIn(data: Intent?): Boolean

    /**
     * サイレントサインイン要求.
     * コルーチン内で呼び出すこと.
     * https://developers.google.com/identity/sign-in/android/backend-auth
     */
    suspend fun requestSilentSignIn(): Boolean

    /**
     * サインアウト要求.
     * コルーチン内で呼び出すこと.
     * https://developers.google.com/identity/sign-in/android/disconnect
     */
    suspend fun requestSignOut(): Boolean
}