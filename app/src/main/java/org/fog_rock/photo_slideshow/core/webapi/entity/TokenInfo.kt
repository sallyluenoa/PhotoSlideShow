package org.fog_rock.photo_slideshow.core.webapi.entity

import com.google.api.client.auth.oauth2.TokenResponse

/**
 * トークン情報
 * @param accessToken アクセストークン
 * @param refreshToken リフレッシュトークン
 * @param expiredAccessTokenTimeMillis アクセストークンの有効期限
 */
data class TokenInfo(
    val accessToken: String,
    val refreshToken: String,
    val expiredAccessTokenTimeMillis: Long
) {

    constructor(
        response: TokenResponse
    ): this(
        response.accessToken,
        (if (response.refreshToken.isNullOrEmpty()) response.refreshToken
        else throw IllegalArgumentException("RefreshToken must not be null or empty.")),
         convertExpiredAccessTokenTimeMillis(response.expiresInSeconds)
    )

    constructor(
        response: TokenResponse, refreshToken: String
    ): this (
        response.accessToken,
        refreshToken,
        convertExpiredAccessTokenTimeMillis(response.expiresInSeconds)
    )

    companion object {
        private fun convertExpiredAccessTokenTimeMillis(expiresInSeconds: Long?): Long =
            if (expiresInSeconds != null) System.currentTimeMillis() + expiresInSeconds * 1000L else 0
    }
}