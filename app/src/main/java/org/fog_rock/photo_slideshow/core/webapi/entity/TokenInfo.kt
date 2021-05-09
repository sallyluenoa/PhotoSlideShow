package org.fog_rock.photo_slideshow.core.webapi.entity

import com.google.api.client.auth.oauth2.TokenResponse
import org.fog_rock.photo_slideshow.core.extension.ONE_MINUTE_MILLIS
import org.fog_rock.photo_slideshow.core.extension.ONE_SECOND_MILLIS
import org.fog_rock.photo_slideshow.core.extension.logD
import org.fog_rock.photo_slideshow.core.extension.toDateString

/**
 * トークン情報
 * @param accessToken アクセストークン
 * @param refreshToken リフレッシュトークン
 * @param expiredAccessTokenTimeMillis アクセストークンの有効期限
 */
data class TokenInfo(
    val accessToken: String,
    val refreshToken: String,
    private val expiredAccessTokenTimeMillis: Long
) {

    companion object {
        // 有効期限までのバッファー時間
        private const val INTERVAL_EXPIRED_MILLIS = ONE_MINUTE_MILLIS

        private fun convertExpiredAccessTokenTimeMillis(expiresInSeconds: Long?): Long =
            if (expiresInSeconds != null) System.currentTimeMillis() + expiresInSeconds * ONE_SECOND_MILLIS else 0
    }

    constructor(): this("", "", 0)

    constructor(
        response: TokenResponse
    ): this(
        response.accessToken,
        (if (!response.refreshToken.isNullOrEmpty()) response.refreshToken
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

    /**
     * アクセストークンが有効か.
     * @return 現在の時間が「アクセストークン有効期限 - バッファー時間」を過ぎていなければ true
     */
    fun isAvailableAccessToken(): Boolean {
        val availableAccessTokenTimeMillis = expiredAccessTokenTimeMillis - INTERVAL_EXPIRED_MILLIS
        logD("Access token available date: ${availableAccessTokenTimeMillis.toDateString()}")
        return System.currentTimeMillis() < availableAccessTokenTimeMillis
    }

    /**
     * 指定されたトークン情報よりも後で更新されたか.
     * @return アクセストークンの有効期限が、指定のトークン情報より後ならば true
     */
    fun afterUpdated(tokenInfo: TokenInfo): Boolean {
        logD("Compare access token available date. " +
                "Current: $expiredAccessTokenTimeMillis, NewOne: ${tokenInfo.expiredAccessTokenTimeMillis}")
        return expiredAccessTokenTimeMillis > tokenInfo.expiredAccessTokenTimeMillis
    }
}