package org.fog_rock.photo_slideshow.app.module.lib

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import java.io.Serializable

/**
 * Google Web API をハンドリングするためのインターフェース.
 */
interface GoogleWebApis {

    data class PhotosApiResult<ResultT : Serializable> (
        val tokenInfo: TokenInfo,
        val photosResults: List<ResultT>
    ) {
        constructor(): this(TokenInfo(), emptyList())
    }

    /**
     * Googleアカウントへのサイレントサインインを要求.
     */
    suspend fun requestSilentSignIn(): ApiResult

    /**
     * Googleアカウントからのサインアウトを要求.
     * @param withRevokeAccess アカウントアクセス破棄を行う場合はtrue
     */
    suspend fun requestSignOut(withRevokeAccess: Boolean): ApiResult

    /**
     * トークン情報更新を要求.
     */
    suspend fun requestUpdateTokenInfo(oldTokenInfo: TokenInfo?): TokenInfo?

    /**
     * 共有アルバム取得を要求.
     */
    suspend fun requestSharedAlbums(): PhotosApiResult<Album>

    /**
     * アルバム内のメディアアイテムリスト取得を要求.
     */
    suspend fun requestMediaItems(album: Album): PhotosApiResult<MediaItem>

    /**
     * サインインしているGoogleアカウントを取得.
     */
    fun getSignedInAccount(): GoogleSignInAccount

    /**
     * サインインしているメールアドレスを取得.
     */
    fun getSignedInEmailAddress(): String

    /**
     * Googleユーザーのサインインに成功したか.
     */
    fun isSucceededUserSignIn(data: Intent?): Boolean
}