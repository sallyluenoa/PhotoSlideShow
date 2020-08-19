package org.fog_rock.photo_slideshow.app.main.interactor

import android.content.Context
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import com.google.photos.types.proto.MediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.module.AppDatabase
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.database.entity.UserInfoData
import org.fog_rock.photo_slideshow.core.file.PhotosDownloader
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.PhotosLibraryApi
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class MainInteractor(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val googleSignInApi: GoogleSignInApi,
    private val photosLibraryApi: PhotosLibraryApi,
    private val photosDownloader: PhotosDownloader
): MainContract.Interactor {

    companion object {
        private const val INTERVAL_UPDATE_MILLISECS = 24 * 60 * 60 * 1000L
        private const val MEDIA_ITEMS_PICKUP_SIZE = 10
    }

    private var callback: MainContract.InteractorCallback? = null

    private var userInfoData = UserInfoData(
        UserInfo(googleSignInApi.getSignedInEmailAddress(), TokenInfo()),
        emptyList()
    )

    override fun create(callback: ViperContract.InteractorCallback) {
        if (callback is MainContract.InteractorCallback) {
            this.callback = callback
        } else {
            IllegalArgumentException("MainContract.InteractorCallback should be set.")
        }
    }

    override fun destroy() {
        callback = null
    }

    override fun requestLoadDisplayedPhotos() {
        GlobalScope.launch(Dispatchers.Main) {
            if (userInfoData.userInfo.id == 0L) {
                // 初回のみ(userInfo.id が 0 のとき)、DBからのデータ取得を行う.
                userInfoData = withContext(Dispatchers.Default) {
                    appDatabase.findUserInfoDataByEmailAddress(userInfoData.userInfo.emailAddress)
                } ?: run {
                    callback?.requestLoadDisplayedPhotosResult(emptyList())
                    return@launch
                }
            }
            // 登録されているDisplayedPhotosを抽出し、シャッフルして返す.
            val displayedPhotos = mutableListOf<DisplayedPhoto>()
            userInfoData.dataList.forEach {
                displayedPhotos.addAll(it.displayedPhotos)
            }
            displayedPhotos.shuffle()
            callback?.requestLoadDisplayedPhotosResult(displayedPhotos.toList())
        }
    }

    override fun requestDownloadPhotos() {
        GlobalScope.launch(Dispatchers.Main) {
            // 登録されているSelectedAlbumsから、Albumを抽出する.
            val albums = mutableListOf<Album>()
            userInfoData.dataList.forEach {
                // Album はサーバー更新しておく.
                val album = withContext(Dispatchers.Default) {
                    photosLibraryApi.requestAlbum(it.selectedAlbum.albumId)
                }
                albums.add(album)
            }
            requestDownloadPhotos(albums.toList())
        }
    }

    override fun requestDownloadPhotos(albums: List<Album>) {
        GlobalScope.launch(Dispatchers.Main) {
            val photosInfo = mutableListOf<AppDatabase.PhotoInfo>()
            albums.forEach { album ->
                // MediaItemリストをサーバーから取得する.
                var mediaItems = withContext(Dispatchers.Default) {
                    photosLibraryApi.requestMediaItems(album)
                }
                if (mediaItems.isEmpty()) {
                    // サーバーからのデータ取得に失敗した場合はスキップする.
                    return@forEach
                }
                // ランダムピックアップしたMediaItemリストを元に、画像ファイルをダウンロードする.
                mediaItems = convertMediaItems(mediaItems, MEDIA_ITEMS_PICKUP_SIZE)
                val outputPaths = withContext(Dispatchers.Default) {
                    photosDownloader.requestDownloads(mediaItems, context.filesDir)
                }
                // MediaDetailリストを作成.
                val mediaDetails = mutableListOf<AppDatabase.PhotoInfo.MediaDetail>()
                outputPaths.forEach { outputPath ->
                    val mediaItem = mediaItems.find { outputPath.endsWith(it.filename) }
                    if (mediaItem != null) {
                        mediaDetails.add(AppDatabase.PhotoInfo.MediaDetail(mediaItem, outputPath))
                    }
                }
                // PhotoInfoを生成して追加.
                photosInfo.add(AppDatabase.PhotoInfo(album, mediaDetails.toList()))
            }
            callback?.requestDownloadPhotosResult(photosInfo.toList())
        }
    }

    override fun requestUpdateDatabase(photosInfo: List<AppDatabase.PhotoInfo>) {
        GlobalScope.launch(Dispatchers.Main) {
            userInfoData = withContext(Dispatchers.Default) {
                appDatabase.replaceUserInfoData(userInfoData, photosInfo)
                appDatabase.findUserInfoDataById(userInfoData.userInfo.id)
            } ?: run {
                callback?.requestUpdateDatabaseResult(false)
                return@launch
            }
            callback?.requestUpdateDatabaseResult(true)
        }
    }

    override fun requestSignOut() {
        // TODO: Implement later.
    }

    override fun isNeededUpdatePhotos(): Boolean =
        userInfoData.userInfo.isNeededUpdatePhotos(INTERVAL_UPDATE_MILLISECS)

    override fun hasSelectedAlbums(): Boolean = userInfoData.dataList.isNotEmpty()

    /**
     * MediaItemリストをランダムピックアップする.
     */
    private fun convertMediaItems(mediaItems: List<MediaItem>, size: Int): List<MediaItem> {
        val tmpMediaItems = mediaItems.filter {
            it.hasMediaMetadata() && it.mediaMetadata.metadataCase == MediaMetadata.MetadataCase.PHOTO
        }
        tmpMediaItems.shuffled()
        return if (size < tmpMediaItems.size) tmpMediaItems.subList(0, size) else tmpMediaItems
    }
}