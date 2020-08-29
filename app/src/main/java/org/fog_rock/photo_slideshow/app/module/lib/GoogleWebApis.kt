package org.fog_rock.photo_slideshow.app.module.lib

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import java.io.Serializable

interface GoogleWebApis {

    data class PhotosApiResult<ResultT : Serializable> (
        val tokenInfo: TokenInfo,
        val photosResults: List<ResultT>
    ) {
        constructor(): this(TokenInfo(), emptyList())
    }

    suspend fun requestSilentSignIn(): ApiResult

    suspend fun requestSignOut(withRevokeAccess: Boolean): ApiResult

    suspend fun requestUpdateTokenInfo(oldTokenInfo: TokenInfo?): TokenInfo?

    suspend fun requestSharedAlbums(): PhotosApiResult<Album>

    suspend fun requestMediaItems(album: Album): PhotosApiResult<MediaItem>

    fun getSignedInAccount(): GoogleSignInAccount

    fun getSignedInEmailAddress(): String

    fun isSucceededUserSignIn(data: Intent?): Boolean
}