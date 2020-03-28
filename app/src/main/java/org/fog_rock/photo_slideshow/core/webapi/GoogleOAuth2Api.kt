package org.fog_rock.photo_slideshow.core.webapi

/**
 * Google OAuth2 認証に関連するAPI
 */
interface GoogleOAuth2Api {

    /**
     * アクセストークン取得要求.
     * コルーチン内で呼び出すこと.
     * https://developers.google.com/identity/sign-in/android/offline-access
     */
    suspend fun requestAccessToken(serverAuthCode: String): String
}