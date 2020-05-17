package org.fog_rock.photo_slideshow.app.main.interactor

import android.content.Context
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl

class MainInteractor(
    private val context: Context,
    private val callback: MainContract.InteractorCallback
): MainContract.Interactor {

    private val TAG = MainInteractor::class.java.simpleName

    private val TIMEOUT_MILLISECS = 10000L

//    private val signOutApi = GoogleSignOutApi(context, arrayOf(PhotoScope.READ_ONLY), this)
//
//    private val oauth2Api =
//        GoogleOAuth2ApiImpl(context)

    private var photosApi: PhotosLibraryApiImpl? = null

//    private val fileDownloaders =
//        PhotosDownloaderImpl(
//            context,
//            500,
//            1000,
//            TIMEOUT_MILLISECS
//        )

    override fun destroy() {
    }

    override fun requestSharedAlbums() {
    /*
        val account = GoogleSignIn.getLastSignedInAccount(context)
        val serverAuthCode = account?.serverAuthCode ?: run {
            callback.requestSharedAlbumsResult(null)
            return
        }

        GlobalScope.launch(Dispatchers.Main) {
            val accessToken = withContext(Dispatchers.Default) {
//                oauth2Api.requestAccessToken(serverAuthCode)
                ""
            }

            Log.d(TAG, "accessToken: $accessToken")
//            photosApi =
//                PhotosLibraryApiImpl(
//                    context,
//                    accessToken
//                )
            val albums = withContext(Dispatchers.Default) {
                photosApi?.requestSharedAlbums()
            }

            callback.requestSharedAlbumsResult(albums)
        }

     */
    }

    override fun requestMediaItems(album: Album) {
        /*
        GlobalScope.launch(Dispatchers.Main) {
            val mediaItems = withContext(Dispatchers.Default) {
                photosApi?.requestMediaItems(album, 10)
            }
            callback.requestMediaItemsResult(mediaItems)
        }
         */
    }

    override fun requestDownloadFiles(mediaItems: List<MediaItem>) {
        /*
        GlobalScope.launch(Dispatchers.Main) {
            val outputFiles = withContext(Dispatchers.Default) {
//                fileDownloaders.doDownloads(mediaItems)
                listOf<String>()
            }
            callback.completedDownloadFiles(outputFiles)
        }

         */
    }

    override fun requestSignOut() {
//        signOutApi.requestSignOut()
    }
}