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
class PhotosLibraryClientHolder(accessToken: String) {

    val client: PhotosLibraryClient =
        try {
            val credentials = OAuth2Credentials.create(AccessToken(accessToken, null))
            val settings = PhotosLibrarySettings.newBuilder().apply {
                credentialsProvider = FixedCredentialsProvider.create(credentials)
            }.build()
            PhotosLibraryClient.initialize(settings)
        } catch (e : IOException) {
            throw IOException("Failed to initialize PhotosLibraryClient.")
        }
}