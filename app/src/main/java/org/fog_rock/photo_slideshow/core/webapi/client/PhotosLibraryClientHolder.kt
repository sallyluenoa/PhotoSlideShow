package org.fog_rock.photo_slideshow.core.webapi.client

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.OAuth2Credentials
import com.google.photos.library.v1.PhotosLibraryClient
import com.google.photos.library.v1.PhotosLibrarySettings
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import java.io.IOException

/**
 * PhotosLibraryClientをシングルトンで保持するHolderクラス.
 */
class PhotosLibraryClientHolder(tokenInfo: TokenInfo) {

    companion object {
        // 有効期限を確認してから実際にAPI実行するまでの時間を考慮.
        private const val INTERVAL_TIME_MILLIS = 60 * 1000L
    }

    val client: PhotosLibraryClient =
        try {
            val credentials = OAuth2Credentials.create(AccessToken(tokenInfo.accessToken, null))
            val settings = PhotosLibrarySettings.newBuilder().apply {
                credentialsProvider = FixedCredentialsProvider.create(credentials)
            }.build()
            PhotosLibraryClient.initialize(settings)
        } catch (e : IOException) {
            throw IOException("Failed to initialize PhotosLibraryClient.")
        }

    private val expiredTimeMillis = tokenInfo.expiredAccessTokenTimeMillis

    /**
     * Clientの有効期限が切れていないか確認する.
     */
    fun isAvailable(): Boolean =
        System.currentTimeMillis() < expiredTimeMillis - INTERVAL_TIME_MILLIS
}