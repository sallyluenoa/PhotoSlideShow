package org.fog_rock.photo_slideshow.core.webapi.entity

import com.google.api.client.auth.oauth2.TokenResponse

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