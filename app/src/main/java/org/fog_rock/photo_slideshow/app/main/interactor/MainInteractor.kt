package org.fog_rock.photo_slideshow.app.main.interactor

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.photos.types.proto.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.PhotosLibraryApi

class MainInteractor(
    private val context: Context,
    private val callback: MainContract.InteractorCallback
): MainContract.Interactor {

    private val TAG = MainInteractor::class.java.simpleName

    private val oauth2Api = GoogleOAuth2Api(context)

    private var photosApi: PhotosLibraryApi? = null

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
}