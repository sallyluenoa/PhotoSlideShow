package org.fog_rock.photo_slideshow.app.module

import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.PhotosLibraryApi
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import java.io.Serializable

class GoogleWebApis(
    private val googleSignInApi: GoogleSignInApi,
    private val googleOAuth2Api: GoogleOAuth2Api,
    private val photosLibraryApi: PhotosLibraryApi
) {

    data class PhotosApiResult<ResultT : Serializable> (
        val tokenInfo: TokenInfo?,
        val photosResults: List<ResultT>
    )

    suspend fun requestSilentSignIn(): ApiResult =
        if (googleSignInApi.isSignedInAccount()) {
            googleSignInApi.requestSilentSignIn()
        } else {
            ApiResult.INVALID
        }

    suspend fun requestSignOut(withRevokeAccess: Boolean): ApiResult =
        if (googleSignInApi.isSignedInAccount()) {
            if (withRevokeAccess) {
                googleSignInApi.requestRevokeAccess()
            } else {
                googleSignInApi.requestSignOut()
            }
        } else {
            ApiResult.INVALID
        }

    suspend fun requestUpdateTokenInfo(oldTokenInfo: TokenInfo?): TokenInfo? {
        if (oldTokenInfo != null) {
            if (oldTokenInfo.isAvailableAccessToken()) {
                // アクセストークンの有効期限が切れていないので更新不要.
                photosLibraryApi.updatePhotosLibraryClient(oldTokenInfo)
                return oldTokenInfo
            }

            // リフレッシュトークンからトークン情報を更新.
            val tokenInfo = googleOAuth2Api.requestTokenInfoWithRefreshToken(oldTokenInfo.refreshToken)
            if (tokenInfo != null) {
                photosLibraryApi.updatePhotosLibraryClient(tokenInfo)
                return tokenInfo
            }
        }
        // サーバー認証コードからトークン情報を更新.
        val serverAuthCode = googleSignInApi.getSignedInAccount().serverAuthCode
        val tokenInfo = if (serverAuthCode != null) {
            googleOAuth2Api.requestTokenInfoWithAuthCode(serverAuthCode)
        } else null

        photosLibraryApi.updatePhotosLibraryClient(tokenInfo)
        return tokenInfo
    }

    suspend fun requestSharedAlbums(): PhotosApiResult<Album> {
        val tokenInfo = updateTokenInfo()
        return if (tokenInfo != null) {
            PhotosApiResult(tokenInfo, photosLibraryApi.requestSharedAlbums())
        } else {
            PhotosApiResult(null, emptyList())
        }
    }

    suspend fun requestMediaItems(album: Album): PhotosApiResult<MediaItem> {
        val tokenInfo = updateTokenInfo()
        return if (tokenInfo != null) {
            PhotosApiResult(tokenInfo, photosLibraryApi.requestMediaItems(album))
        } else {
            PhotosApiResult(null, emptyList())
        }
    }

    private suspend fun updateTokenInfo(): TokenInfo? {
        val currentTokenInfo = photosLibraryApi.currentTokenInfo()
        if (currentTokenInfo.isAvailableAccessToken()) {
            // アクセストークンが有効なのでそのまま返す.
            return currentTokenInfo
        }
        // リフレッシュトークンでトークン情報を更新.
        val tokenInfo = googleOAuth2Api.requestTokenInfoWithRefreshToken(currentTokenInfo.refreshToken) ?: run {
            // トークン更新に失敗.
            return null
        }
        // クライアントを更新する.
        photosLibraryApi.updatePhotosLibraryClient(tokenInfo)
        return tokenInfo
    }
}