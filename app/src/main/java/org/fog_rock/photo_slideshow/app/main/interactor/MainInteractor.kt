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
import org.fog_rock.photo_slideshow.app.module.GoogleWebApis
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.database.entity.UserInfoData
import org.fog_rock.photo_slideshow.core.file.PhotosDownloader
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class MainInteractor(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val photosDownloader: PhotosDownloader,
    private val googleWebApis: GoogleWebApis
): MainContract.Interactor {

    companion object {
        private const val INTERVAL_UPDATE_MILLISECS = 24 * 60 * 60 * 1000L
        private const val MEDIA_ITEMS_PICKUP_SIZE = 10
    }

    private var callback: MainContract.InteractorCallback? = null

    private var userInfoData = UserInfoData(
        UserInfo(googleWebApis.getSignedInEmailAddress(), TokenInfo()),
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
        GlobalScope.launch(Dispatchers.Default) {
            if (userInfoData.userInfo.id == 0L) {
                // 初回のみ(userInfo.id が 0 のとき)、DBからのデータ取得を行う.
                userInfoData = appDatabase.findUserInfoDataByEmailAddress(userInfoData.userInfo.emailAddress) ?: run {
                    requestLoadDisplayedPhotosResult(emptyList())
                    return@launch
                }
            }
            // 登録されているDisplayedPhotosを抽出し、シャッフルして返す.
            val displayedPhotos = mutableListOf<DisplayedPhoto>()
            userInfoData.dataList.forEach {
                displayedPhotos.addAll(it.displayedPhotos)
            }
            displayedPhotos.shuffle()
            requestLoadDisplayedPhotosResult(displayedPhotos.toList())
        }
    }

    override fun requestDownloadPhotos() {
        GlobalScope.launch(Dispatchers.Default) {
            // 登録されているSelectedAlbumsから、Albumを抽出する.
            val albums = mutableListOf<Album>()
            userInfoData.dataList.forEach {
                albums.add(it.selectedAlbum.album())
            }
            requestDownloadPhotosInner(albums.toList())
        }
    }

    override fun requestDownloadPhotos(albums: List<Album>) {
        GlobalScope.launch(Dispatchers.Default) {
            requestDownloadPhotosInner(albums)
        }
    }

    override fun requestUpdateDatabase(photosInfo: List<AppDatabase.PhotoInfo>) {
        GlobalScope.launch(Dispatchers.Default) {
            appDatabase.replaceUserInfoData(userInfoData, photosInfo)
            userInfoData = appDatabase.findUserInfoDataById(userInfoData.userInfo.id) ?: run {
                requestUpdateDatabaseResult(false)
                return@launch
            }
            requestUpdateDatabaseResult(true)
        }
    }

    override fun requestSignOut() {
        // TODO: Implement later.
    }

    override fun isNeededUpdatePhotos(): Boolean =
        userInfoData.userInfo.isNeededUpdatePhotos(INTERVAL_UPDATE_MILLISECS)

    override fun hasSelectedAlbums(): Boolean = userInfoData.dataList.isNotEmpty()

    private suspend fun requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>) {
        withContext(Dispatchers.Main) {
            callback?.requestLoadDisplayedPhotosResult(displayedPhotos)
        }
    }

    private suspend fun requestDownloadPhotosResult(photosInfo: List<AppDatabase.PhotoInfo>) {
        withContext(Dispatchers.Main) {
            callback?.requestDownloadPhotosResult(photosInfo)
        }
    }

    private suspend fun requestUpdateDatabaseResult(isSucceeded: Boolean) {
        withContext(Dispatchers.Main) {
            callback?.requestUpdateDatabaseResult(isSucceeded)
        }
    }

    private suspend fun requestDownloadPhotosInner(albums: List<Album>) {
        val photosInfo = mutableListOf<AppDatabase.PhotoInfo>()

        albums.forEach { album ->
            // MediaItemリストをサーバーから取得する.
            val result = googleWebApis.requestMediaItems(album)

            if (result.tokenInfo.afterUpdated(userInfoData.userInfo.tokenInfo())) {
                // トークン情報が更新されていたらDB側も更新する.
                appDatabase.updateUserInfo(userInfoData.userInfo.emailAddress, result.tokenInfo)
            }
            if (result.photosResults.isEmpty()) {
                // サーバーからのデータ取得に失敗した場合はスキップする.
                return@forEach
            }

            // ランダムピックアップしたMediaItemリストを元に、画像ファイルをダウンロードする.
            val mediaItems = convertMediaItems(result.photosResults, MEDIA_ITEMS_PICKUP_SIZE)
            val outputPaths = photosDownloader.requestDownloads(mediaItems, context.filesDir)

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

        requestDownloadPhotosResult(photosInfo.toList())
    }

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