package org.fog_rock.photo_slideshow.app.module.lib.impl

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.app.module.lib.GoogleWebApis
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.extension.logW
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.PhotosLibraryApi
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class GoogleWebApisImpl(
    private val context: Context,
    private val googleSignInApi: GoogleSignInApi,
    private val googleOAuth2Api: GoogleOAuth2Api,
    private val photosLibraryApi: PhotosLibraryApi
): GoogleWebApis {

    override suspend fun requestSilentSignIn(): ApiResult =
        if (googleSignInApi.getSignedInAccount(context) != null) {
            googleSignInApi.requestSilentSignIn()
        } else {
            logW("Signed out already.")
            ApiResult.INVALID
        }

    override suspend fun requestSignOut(withRevokeAccess: Boolean): ApiResult =
        if (googleSignInApi.getSignedInAccount(context) != null) {
            if (withRevokeAccess) {
                googleSignInApi.requestRevokeAccess()
            } else {
                googleSignInApi.requestSignOut()
            }
        } else {
            logW("Signed out already.")
            ApiResult.INVALID
        }

    override suspend fun requestUpdateTokenInfo(oldTokenInfo: TokenInfo?): TokenInfo? {
        if (oldTokenInfo != null) {
            if (oldTokenInfo.isAvailableAccessToken()) {
                // アクセストークンの有効期限が切れていないので更新不要.
                logI("TokenInfo is still available.")
                photosLibraryApi.updatePhotosLibraryClient(oldTokenInfo)
                return oldTokenInfo
            }

            // リフレッシュトークンからトークン情報を更新.
            logI("Try to update TokenInfo by refresh token.")
            val tokenInfo = googleOAuth2Api.requestTokenInfoWithRefreshToken(oldTokenInfo.refreshToken)
            if (tokenInfo != null) {
                logI("TokenInfo is successfully updated by refresh token.")
                photosLibraryApi.updatePhotosLibraryClient(tokenInfo)
                return tokenInfo
            }
        }
        // サーバー認証コードからトークン情報を更新.
        logI("Try to update TokenInfo by server auth code.")
        val serverAuthCode = getSignedInAccount().serverAuthCode
        val tokenInfo = if (serverAuthCode != null) {
            googleOAuth2Api.requestTokenInfoWithAuthCode(serverAuthCode)
        } else null

        // フォトライブラリのクライアントを更新.
        logI("Update TokenInfo.")
        photosLibraryApi.updatePhotosLibraryClient(tokenInfo)
        return tokenInfo
    }

    override suspend fun requestSharedAlbums(): GoogleWebApis.PhotosApiResult<Album> {
        val tokenInfo = updateTokenInfo()
        return if (tokenInfo != null) {
            GoogleWebApis.PhotosApiResult(tokenInfo, photosLibraryApi.requestSharedAlbums())
        } else {
            GoogleWebApis.PhotosApiResult()
        }
    }

    override suspend fun requestMediaItems(album: Album): GoogleWebApis.PhotosApiResult<MediaItem> {
        val tokenInfo = updateTokenInfo()
        return if (tokenInfo != null) {
            GoogleWebApis.PhotosApiResult(tokenInfo, photosLibraryApi.requestMediaItems(album))
        } else {
            GoogleWebApis.PhotosApiResult()
        }
    }

    override fun getSignedInAccount(): GoogleSignInAccount =
        googleSignInApi.getSignedInAccount(context) ?:
        throw NullPointerException("There are no sign in account.")

    override fun getSignedInEmailAddress(): String =
        googleSignInApi.getSignedInAccount(context)?.email ?:
        throw NullPointerException("There are no sign in account.")

    override fun isSucceededUserSignIn(data: Intent?): Boolean =
        googleSignInApi.isSucceededUserSignIn(data)

    /**
     * トークン情報を更新する.
     * 主に PhotosLibraryApi のリクエストメソッドを使用する前に呼び出し、
     * アクセストークンの有効期限が切れていればリフレッシュトークンで更新する.
     */
    private suspend fun updateTokenInfo(): TokenInfo? {
        val currentTokenInfo = photosLibraryApi.currentTokenInfo()
        if (currentTokenInfo.isAvailableAccessToken()) {
            // アクセストークンが有効なのでそのまま返す.
            logI("TokenInfo is still available.")
            return currentTokenInfo
        }
        // リフレッシュトークンでトークン情報を更新.
        val tokenInfo = googleOAuth2Api.requestTokenInfoWithRefreshToken(currentTokenInfo.refreshToken) ?: run {
            // トークン更新に失敗.
            logE("Failed to update TokenInfo by refresh token.")
            return null
        }
        // フォトライブラリのクライアントを更新.
        logI("Succeeded to update TokenInfo by refresh token.")
        photosLibraryApi.updatePhotosLibraryClient(tokenInfo)
        return tokenInfo
    }
}