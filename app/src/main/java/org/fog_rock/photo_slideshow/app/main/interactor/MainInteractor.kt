package org.fog_rock.photo_slideshow.app.main.interactor

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import com.google.photos.types.proto.MediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.core.file.FileDownloader
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.fog_rock.photo_slideshow.core.webapi.PhotosLibraryApi
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import kotlin.math.abs
import kotlin.math.min

class MainInteractor(
    private val context: Context,
    private val callback: MainContract.InteractorCallback
): MainContract.Interactor, FileDownloader.Callback {

    private val TAG = MainInteractor::class.java.simpleName

    private val TIMEOUT_MILLISECS = 10000L

    private val oauth2Api = GoogleOAuth2Api(context)

    private var photosApi: PhotosLibraryApi? = null

    private val fileDownloader =
        FileDownloader(TIMEOUT_MILLISECS, TIMEOUT_MILLISECS, TIMEOUT_MILLISECS, this)

    private var index = 0
    private var downloadMediaItemList = listOf<MediaItem>()
    private var downloadSuccessFileList = mutableListOf<String>()

    override fun destroy() {
    }

    override fun requestSharedAlbums() {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        val serverAuthCode = account?.serverAuthCode ?: run {
            callback.requestSharedAlbumsResult(null)
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            val accessToken = withContext(Dispatchers.Default) {
                oauth2Api.requestAccessToken(serverAuthCode)
            }

            Log.d(TAG, "accessToken: $accessToken")
            photosApi = PhotosLibraryApi(context, accessToken)
            val albumList = withContext(Dispatchers.Default) {
                photosApi?.getSharedAlbumList()
            }

            callback.requestSharedAlbumsResult(albumList)
        }
    }

    override fun requestMediaItems(album: Album) {
        GlobalScope.launch(Dispatchers.Main) {
            val mediaItemList = withContext(Dispatchers.Default) {
                photosApi?.getMediaItemList(album, 10)
            }
            callback.requestMediaItemsResult(mediaItemList)
        }
    }

    override fun requestDownloadFiles(mediaItemList: List<MediaItem>) {
        index = 0
        downloadMediaItemList = mediaItemList
        downloadSuccessFileList = mutableListOf()
        doDownload()
    }

    override fun downloadResult(resultOutputFile: File?) {
        if (resultOutputFile != null) {
            Log.i(TAG, "Succeeded to download file: ${resultOutputFile.path}")
            downloadSuccessFileList.add(resultOutputFile.path)
        } else {
            Log.e(TAG, "Failed to download file.")
        }
        doNextDownload()
    }

    private fun doDownload() {
        if (index >= downloadMediaItemList.size) {
            Log.i(TAG, "Finished download files. FileSize: ${downloadSuccessFileList.size}")
            callback.completedDownloadFiles(downloadSuccessFileList.toList())
            return
        }
        val mediaItem = downloadMediaItemList[index]
        if (!mediaItem.hasMediaMetadata()) {
            Log.i(TAG, "MediaItem does not have meta data.")
            doNextDownload()
            return
        }
        val metadata = mediaItem.mediaMetadata
        if (metadata.metadataCase != MediaMetadata.MetadataCase.PHOTO) {
            Log.i(TAG, "MediaItem is not photo.")
            doNextDownload()
            return
        }

        val scale = estimateEffectiveScale(metadata.width, metadata.height, 500, 1000)
        val width = (metadata.width * scale).toLong()
        val height = (metadata.height * scale).toLong()
        val downloadUrl: URL
        try {
            downloadUrl = URL("${mediaItem.baseUrl}=w$width-h$height")
        } catch (e: MalformedURLException) {
            Log.e(TAG, "Invalid url format.")
            e.printStackTrace()
            doNextDownload()
            return
        }

        val outputFile = File(context.filesDir, mediaItem.filename)
        fileDownloader.doDownload(downloadUrl, outputFile)
    }

    private fun doNextDownload() {
        index++
        doDownload()
    }

    /**
     * 画像の元サイズから期待されるサイズに近づけるための、最も効率的な縮小率を求める.
     * Ex. org(1080,1920), exp(500,1000) の場合、最も期待サイズに近い値は (540,960) なので 0.5 を返す.
     */
    private fun estimateEffectiveScale(orgMin: Long, orgMax: Long, expMin: Long, expMax: Long): Float {
        // 最大値と最小値が逆になっていたら正しく修正する.
        var orgMin = orgMin
        var orgMax = orgMax
        var expMin = expMin
        var expMax = expMax
        if (orgMin > orgMax) {
            val tmp = orgMin
            orgMin = orgMax
            orgMax = tmp
        }
        if (expMin > expMax) {
            val tmp = expMin
            expMin = expMax
            expMax = tmp
        }

        // 期待サイズより元サイズが小さい場合は縮小しない.
        if (orgMin < expMin && orgMax < expMax) return 1.0f

        // 元サイズの半分のサイズで再帰処理をして、最小スケール値を求める.
        val minScale = estimateEffectiveScale(orgMin / 2, orgMax / 2, expMin, expMax) * 0.5f

        // 評価値を元に、期待サイズに最も近い効率の良いスケール値を判断する.
        // 縦横それぞれの期待サイズとの差の絶対値をとり、その和を評価値とする.
        // 比較対象: 元サイズ、元サイズに0.75かけたもの、元サイズに最小スケール値(0.5以下)をかけたもの
        val orgEvl = abs(orgMin - expMin) + abs(orgMax - expMax)
        val quaEvl = abs(orgMin * 3 / 4 - expMin) + abs(orgMax * 3 / 4 - expMax)
        val sclEvl = abs((orgMin * minScale).toLong() - expMin) + abs((orgMax * minScale).toLong() - expMax)

        return when( min(orgEvl, min(quaEvl, sclEvl)) ) {
            orgEvl -> 1.0f
            quaEvl -> 0.75f
            else -> minScale
        }
    }

}