package org.fog_rock.photo_slideshow.core.webapi

import com.google.api.client.auth.oauth2.TokenResponse

/**
 * Google OAuth2 認証に関連するAPI
 * https://developers.google.com/identity/sign-in/android/offline-access
 * https://developers.google.com/identity/protocols/oauth2
 */
interface GoogleOAuth2Api {

    /**
     * トークン情報
     * @param accessToken アクセストークン
     * @param refreshToken リフレッシュトークン
     * @param expiredAccessTokenTimeMillis アクセストークンの有効期限
     */
    data class TokenInfo(
        val accessToken: String?,
        val refreshToken: String?,
        val expiredAccessTokenTimeMillis: Long
    ) {
        constructor(response: TokenResponse): this(response, response.refreshToken)
        constructor(response: TokenResponse, refreshToken: String?): this(
            response.accessToken, refreshToken,
            System.currentTimeMillis() + response.expiresInSeconds * 1000L
        )
    }

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