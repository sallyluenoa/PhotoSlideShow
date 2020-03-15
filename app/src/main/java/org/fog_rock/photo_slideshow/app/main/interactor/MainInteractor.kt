package org.fog_rock.photo_slideshow.app.main.interactor

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.core.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignOutApi
import org.fog_rock.photo_slideshow.core.webapi.PhotosDownloader
import org.fog_rock.photo_slideshow.core.webapi.PhotosLibraryApi

class MainInteractor(
    private val context: Context,
    private val callback: MainContract.InteractorCallback
): MainContract.Interactor, GoogleSignOutApi.Callback {

    private val TAG = MainInteractor::class.java.simpleName

    private val TIMEOUT_MILLISECS = 10000L

    private val signOutApi = GoogleSignOutApi(context, arrayOf(PhotoScope.READ_ONLY), this)

    private val oauth2Api = GoogleOAuth2Api(context)

    private var photosApi: PhotosLibraryApi? = null

    private val fileDownloaders =
        PhotosDownloader(context, 500, 1000, TIMEOUT_MILLISECS)

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
            val albums = withContext(Dispatchers.Default) {
                photosApi?.getSharedAlbums()
            }

            callback.requestSharedAlbumsResult(albums)
        }
    }

    override fun requestMediaItems(album: Album) {
        GlobalScope.launch(Dispatchers.Main) {
            val mediaItems = withContext(Dispatchers.Default) {
                photosApi?.getMediaItems(album, 10)
            }
            callback.requestMediaItemsResult(mediaItems)
        }
    }

    override fun requestDownloadFiles(mediaItems: List<MediaItem>) {
        GlobalScope.launch(Dispatchers.Main) {
            val outputFiles = withContext(Dispatchers.Default) {
                fileDownloaders.doDownloads(mediaItems)
            }
            callback.completedDownloadFiles(outputFiles)
        }
    }

    override fun requestSignOut() {
        signOutApi.requestSignOut()
    }

    override fun requestSignOutResult(isSucceeded: Boolean) {
        callback.requestSignOutResult(isSucceeded)
    }
}