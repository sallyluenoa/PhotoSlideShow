package org.fog_rock.photo_slideshow.core.webapi

import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

/**
 * Google OAuth2 認証に関連するAPI
 * https://developers.google.com/identity/sign-in/android/offline-access
 * https://developers.google.com/identity/protocols/oauth2
 */
interface GoogleOAuth2Api {

    /**
     * 認証コードを用いてトークン情報取得要求.
     * コルーチン内で呼び出すこと.
     * @param serverAuthCode サーバーの認証コード
     */
    suspend fun requestTokenInfoWithAuthCode(serverAuthCode: String): TokenInfo?

    /**
     * リフレッシュトークンを用いてトークン情報取得要求.
     * コルーチン内で呼び出すこと.
     * @param refreshToken リフレッシュトークン
     */
    suspend fun requestTokenInfoWithRefreshToken(refreshToken: String): TokenInfo?
}