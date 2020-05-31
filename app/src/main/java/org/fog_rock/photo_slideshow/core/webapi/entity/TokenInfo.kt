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

    companion object {

        /**
         * レスポンスからトークン情報を生成する.
         * @param response トークンレスポンス
         * @exception IllegalArgumentException リフレッシュトークンがNULLの場合に発生
         */
        fun newTokenInfo(response: TokenResponse) =
            if (!response.refreshToken.isNullOrEmpty()) {
                TokenInfo(
                    response.accessToken,
                    response.refreshToken,
                    convertExpiredAccessTokenTimeMillis(response.expiresInSeconds)
                )
            } else {
                throw IllegalArgumentException("RefreshToken must not be null or empty.")
            }

        /**
         * レスポンスからトークン情報を生成する.
         * @param response トークンレスポンス
         * @param refreshToken リフレッシュトークン
         */
        fun newTokenInfo(response: TokenResponse, refreshToken: String) = TokenInfo(
            response.accessToken,
            refreshToken,
            convertExpiredAccessTokenTimeMillis(response.expiresInSeconds)
        )

        private fun convertExpiredAccessTokenTimeMillis(expiresInSeconds: Long?): Long =
            if (expiresInSeconds != null) System.currentTimeMillis() + expiresInSeconds * 1000L else 0
    }
}