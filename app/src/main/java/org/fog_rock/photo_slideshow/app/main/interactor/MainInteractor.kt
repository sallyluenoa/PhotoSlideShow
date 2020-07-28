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
import org.fog_rock.photo_slideshow.core.database.DisplayedPhotoDatabase
import org.fog_rock.photo_slideshow.core.database.SelectedAlbumDatabase
import org.fog_rock.photo_slideshow.core.database.UserInfoDatabase
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.database.entity.SelectedAlbum
import org.fog_rock.photo_slideshow.core.database.entity.UserInfo
import org.fog_rock.photo_slideshow.core.database.impl.DisplayedPhotoDatabaseImpl
import org.fog_rock.photo_slideshow.core.database.impl.SelectedAlbumDatabaseImpl
import org.fog_rock.photo_slideshow.core.database.impl.UserInfoDatabaseImpl
import org.fog_rock.photo_slideshow.core.file.PhotosDownloader
import org.fog_rock.photo_slideshow.core.file.impl.AssetsFileReaderImpl
import org.fog_rock.photo_slideshow.core.file.impl.FileDownloaderImpl
import org.fog_rock.photo_slideshow.core.file.impl.PhotosDownloaderImpl
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.PhotosLibraryApi
import org.fog_rock.photo_slideshow.core.webapi.client.PhotosLibraryClientHolder
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl

class MainInteractor(
    private val context: Context,
    private val callback: MainContract.InteractorCallback
): MainContract.Interactor {

    companion object {
        private const val DOWNLOADER_TIMEOUT_MILLISECS = 10 * 1000L

        private const val INTERVAL_EXPIRED_MILLISECS = 60 * 1000L

        private const val INTERVAL_UPDATE_MILLISECS = 24 * 60 * 60 * 1000L
    }

    private val userInfoDatabase: UserInfoDatabase = UserInfoDatabaseImpl()
    private val selectedAlbumDatabase: SelectedAlbumDatabase = SelectedAlbumDatabaseImpl()
    private val displayedPhotoDatabase: DisplayedPhotoDatabase = DisplayedPhotoDatabaseImpl()

    private var userInfo: UserInfo = UserInfo(
        GoogleSignInApi.getSignedInEmailAddress(context), TokenInfo()
    )

    private var selectedAlbums: List<SelectedAlbum> = emptyList()
    private var displayedPhotosMap: MutableMap<String, List<DisplayedPhoto>> = HashMap()

    private val oauth2Api: GoogleOAuth2Api = GoogleOAuth2ApiImpl(AssetsFileReaderImpl(context))

    private var photosApi: PhotosLibraryApi? = null

    private var clientHolder: PhotosLibraryClientHolder? = null

    private val photosDownloader: PhotosDownloader = PhotosDownloaderImpl(
        FileDownloaderImpl(DOWNLOADER_TIMEOUT_MILLISECS), 500, 1000
    )

    override fun destroy() {
    }

    override fun requestLoadFromDatabase() {
        GlobalScope.launch(Dispatchers.Main) {
            val userInfoWithSelectedAlbums = withContext(Dispatchers.Default) {
                userInfoDatabase.findWithSelectedAlbums(userInfo.emailAddress)
            } ?: run {
                callback.requestDownloadPhotosResult(false)
                return@launch
            }
            userInfo = userInfoWithSelectedAlbums.userInfo
            selectedAlbums = userInfoWithSelectedAlbums.selectedAlbums
            selectedAlbums.forEach {
                val selectedAlbumWithDisplayedPhotos = withContext(Dispatchers.Default) {
                    selectedAlbumDatabase.findWithDisplayedPhotos(it.albumId)
                } ?: return@forEach
                displayedPhotosMap[it.albumId] = selectedAlbumWithDisplayedPhotos.displayedPhotos
            }
            callback.requestDownloadPhotosResult(true)
        }
    }

    override fun requestAlbums() {
        GlobalScope.launch(Dispatchers.Main) {
            if (updateAccessToken()) {
                val albums = photosApi?.requestSharedAlbums()
                callback.requestAlbumsResult(albums)
            } else {
                callback.requestAlbumsResult(null)
            }
        }
    }

    override fun requestUpdateSelectedAlbums(albums: List<Album>) {
        GlobalScope.launch(Dispatchers.Main) {
            val userInfoWithSelectedAlbums = withContext(Dispatchers.Default) {
                selectedAlbumDatabase.update(userInfo.id, albums)
                userInfoDatabase.findWithSelectedAlbums(userInfo.emailAddress)
            } ?: run {
                callback.requestUpdateSelectedAlbumsResult(false)
                return@launch
            }
            userInfo = userInfoWithSelectedAlbums.userInfo
            selectedAlbums = userInfoWithSelectedAlbums.selectedAlbums
            callback.requestUpdateSelectedAlbumsResult(true)
        }
    }

    override fun requestDownloadPhotos() {
        GlobalScope.launch(Dispatchers.Main) {
            selectedAlbums.forEach {
                val mediaItems = withContext(Dispatchers.Default) {
                    val album = photosApi?.requestAlbum(it.albumId)
                    photosApi?.requestMediaItems(album!!)
                }
                if (mediaItems.isNullOrEmpty()) return@forEach
                val displayedPhotos = convertMediaItems(mediaItems, 100, 10)
                withContext(Dispatchers.Default) {
                    displayedPhotoDatabase.update(it.id, displayedPhotos)
                }
            }
        }
    }

    override fun requestSignOut() {
        // TODO: Implement later.
    }

    override fun isNeededUpdatePhotos(): Boolean = userInfo.isNeededUpdatePhotos(INTERVAL_UPDATE_MILLISECS)

    override fun hasSelectedAlbums(): Boolean = selectedAlbums.isNotEmpty()

    private suspend fun updateAccessToken(): Boolean {
        if (!userInfo.isAvailableAccessToken(INTERVAL_EXPIRED_MILLISECS)) {
            photosApi = null

            val tokenInfo = withContext(Dispatchers.Default) {
                oauth2Api.requestTokenInfoWithRefreshToken(userInfo.refreshToken)
            } ?: run {
                return false
            }
            userInfo = userInfo.copy(tokenInfo)
            withContext(Dispatchers.Default) {
                userInfoDatabase.update(userInfo.emailAddress, tokenInfo)
            }
        }
        if (photosApi == null) {
            clientHolder = PhotosLibraryClientHolder(userInfo.accessToken)
            photosApi = PhotosLibraryApiImpl(clientHolder!!)
        }
        return true
    }

    private fun convertMediaItems(mediaItems: List<MediaItem>, a: Int, b: Int): List<MediaItem> {
        val size = mediaItems.size
        var tmpMediaItems = mediaItems.filter {
            it.hasMediaMetadata() && it.mediaMetadata.metadataCase == MediaMetadata.MetadataCase.PHOTO
        }
        if (size - a > 0) {
            tmpMediaItems = tmpMediaItems.subList(size - a, size)
        }
        return tmpMediaItems.shuffled().subList(0, b)
    }
}