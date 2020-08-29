package org.fog_rock.photo_slideshow.app.main.interactor

import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import com.google.photos.types.proto.MediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.module.lib.AppDatabase
import org.fog_rock.photo_slideshow.app.module.lib.GoogleWebApis
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.database.entity.UserInfoData
import org.fog_rock.photo_slideshow.core.extension.logD
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.extension.logW
import org.fog_rock.photo_slideshow.core.file.PhotosDownloader
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import java.io.File
import java.util.concurrent.CancellationException

class MainInteractor(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val photosDownloader: PhotosDownloader,
    private val googleWebApis: GoogleWebApis
): ViewModel(), MainContract.Interactor {

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
        viewModelScope.cancel(CancellationException("Destroy method is called."))
        callback = null
    }

    override fun requestLoadDisplayedPhotos() {
        viewModelScope.launch(Dispatchers.Default) {
            logI("requestLoadDisplayedPhotos: Start coroutine.")
            val displayedPhotos = loadDisplayedPhotos()
            withContext(Dispatchers.Main) {
                callback?.requestLoadDisplayedPhotosResult(displayedPhotos)
            }
            logI("requestLoadDisplayedPhotos: End coroutine.")
        }
    }

    override fun requestDownloadPhotos(albums: List<Album>?) {
        viewModelScope.launch(Dispatchers.Default) {
            logI("requestDownloadPhotos: Start coroutine.")
            val photosInfo = downloadPhotos(albums)
            withContext(Dispatchers.Main) {
                callback?.requestDownloadPhotosResult(photosInfo)
            }
            logI("requestDownloadPhotos: End coroutine.")
        }
    }

    override fun requestUpdateDatabase(photosInfo: List<AppDatabase.PhotoInfo>) {
        viewModelScope.launch(Dispatchers.Default) {
            logI("requestUpdateDatabase: Start coroutine.")
            val result = updateDatabase(photosInfo)
            withContext(Dispatchers.Main) {
                callback?.requestUpdateDatabaseResult(result)
            }
            logI("requestUpdateDatabase: End coroutine.")
        }
    }

    override fun requestSignOut() {
        // TODO: Implement later.
    }

    override fun isNeededUpdatePhotos(): Boolean =
        userInfoData.userInfo.isNeededUpdatePhotos(INTERVAL_UPDATE_MILLISECS)

    override fun hasSelectedAlbums(): Boolean = userInfoData.dataList.isNotEmpty()

    private suspend fun loadDisplayedPhotos(): List<DisplayedPhoto> {
        if (userInfoData.userInfo.id == 0L) {
            // 初回のみ(userInfo.id が 0 のとき)、DBからのデータ取得を行う.
            logI("This is the first request. Load UserInfoData from database.")
            userInfoData = appDatabase.findUserInfoDataByEmailAddress(userInfoData.userInfo.emailAddress) ?: run {
                logE("Failed to load UserInfoData from database.")
                return emptyList()
            }
        }
        // 登録されているDisplayedPhotosを抽出し、シャッフルして返す.
        val displayedPhotos = mutableListOf<DisplayedPhoto>()
        userInfoData.dataList.forEach {
            displayedPhotos.addAll(it.displayedPhotos)
        }
        return displayedPhotos.shuffled()
    }

    private suspend fun downloadPhotos(albums: List<Album>?): List<AppDatabase.PhotoInfo> {
        val searchAlbums = if (albums.isNullOrEmpty()) {
            if (userInfoData.dataList.isEmpty()) {
                logE("No albums found.")
                return emptyList()
            }
            logI("Load albums from UserInfoData.")
            val tmp = mutableListOf<Album>()
            userInfoData.dataList.forEach { tmp.add(it.selectedAlbum.album()) }
            tmp.toList()
        } else {
            logI("Use request parameter of album.")
            albums
        }

        val outputDir = getOutputDir() ?: run {
            logE("Failed to get dir.")
            return emptyList()
        }

        val photosInfo = mutableListOf<AppDatabase.PhotoInfo>()

        searchAlbums.forEach { album ->
            logI("Try to get mediaItem from album.")
            logD("album#id: ${album.id}")

            // MediaItemリストをサーバーから取得する.
            val result = googleWebApis.requestMediaItems(album)

            if (result.tokenInfo.afterUpdated(userInfoData.userInfo.tokenInfo())) {
                // トークン情報が更新されていたらDB側も更新する.
                logI("Update tokenInfo to database.")
                appDatabase.updateUserInfo(userInfoData.userInfo.emailAddress, result.tokenInfo)
            }
            if (result.photosResults.isEmpty()) {
                // サーバーからのデータ取得に失敗した場合はスキップする.
                logW("Failed to get MediaItems.")
                return@forEach
            }

            // ランダムピックアップしたMediaItemリストを元に、画像ファイルをダウンロードする.
            val mediaItems = convertMediaItems(result.photosResults, MEDIA_ITEMS_PICKUP_SIZE)
            val outputPaths = photosDownloader.requestDownloads(mediaItems, outputDir)

            // MediaDetailリストを作成.
            val mediaDetails = mutableListOf<AppDatabase.PhotoInfo.MediaDetail>()
            outputPaths.forEach { outputPath ->
                val mediaItem = mediaItems.find { outputPath.endsWith(it.filename) }
                if (mediaItem != null) {
                    mediaDetails.add(AppDatabase.PhotoInfo.MediaDetail(mediaItem, outputPath))
                }
            }
            // PhotoInfoを生成して追加.
            logI("Succeeded to get MediaDetails. size: ${mediaDetails.size}")
            photosInfo.add(AppDatabase.PhotoInfo(album, mediaDetails.toList()))
        }

        return photosInfo.toList()
    }

    private suspend fun updateDatabase(photosInfo: List<AppDatabase.PhotoInfo>): Boolean {
        appDatabase.replaceUserInfoData(userInfoData, photosInfo)
        userInfoData = appDatabase.findUserInfoDataById(userInfoData.userInfo.id) ?: run {
            logE("Failed to load new UserInfoData from database.")
            return false
        }
        return true
    }

    private fun getOutputDir(): File? {
        val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: run {
            logE("Failed to get context#getExternalFilesDir().")
            return null
        }
        logD("Output dir: $outputDir")
        return when {
            outputDir.exists() -> {
                logI("Output dir exists.")
                outputDir
            }
            outputDir.mkdirs() -> {
                logI("Succeeded to make dir.")
                outputDir
            }
            else -> {
                logE("Failed to make dir.")
                null
            }
        }
    }

    /**
     * MediaItemリストをランダムピックアップする.
     */
    private fun convertMediaItems(mediaItems: List<MediaItem>, size: Int): List<MediaItem> {
        val tmpMediaItems = mediaItems.filter {
            it.hasMediaMetadata() && it.mediaMetadata.metadataCase == MediaMetadata.MetadataCase.PHOTO
        }.shuffled()
        return if (size < tmpMediaItems.size) tmpMediaItems.subList(0, size) else tmpMediaItems
    }
}