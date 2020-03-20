package org.fog_rock.photo_slideshow.app.main.presenter

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.interactor.MainInteractor
import org.fog_rock.photo_slideshow.app.main.router.MainRouter
import org.fog_rock.photo_slideshow.app.select.view.SelectActivity

class MainPresenter(
    private val callback: MainContract.PresenterCallback
) : MainContract.Presenter, MainContract.InteractorCallback {

    private val TAG = MainPresenter::class.java.simpleName

    private val CODE_SELECT_ACTIVITY = 1000

    private val interactor: MainContract.Interactor =
        MainInteractor(activity().applicationContext, this)

    private val router: MainContract.Router = MainRouter()

    override fun destroy() {
        interactor.destroy()
    }

    override fun requestAlbums() {
        interactor.requestSharedAlbums()
    }

    override fun requestLicense() {
        router.startOssLicensesMenuActivity(activity(), R.string.license)
    }

    override fun requestSignOut() {
        interactor.requestSignOut()
    }

    override fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CODE_SELECT_ACTIVITY -> {
                if (resultCode == RESULT_OK && data != null) {
                    Log.i(TAG, "Succeeded to select album.")
                    val album = data.getSerializableExtra(SelectActivity.RESULT_DECIDE_ALBUM) as Album
                    interactor.requestMediaItems(album)
                } else {
                    Log.i(TAG, "Canceled to select album.")
                }
            }
            else -> {
                Log.e(TAG, "Unknown requestCode: $requestCode")
            }
        }
    }

    override fun requestSharedAlbumsResult(albums: List<Album>?) {
        if (!albums.isNullOrEmpty()) {
            Log.i(TAG, "Succeeded to get albums. ${albums.count()}")
            router.startSelectActivity(activity(), albums, CODE_SELECT_ACTIVITY)
        } else {
            Log.i(TAG, "Failed to get albums.")
        }
    }

    override fun requestMediaItemsResult(mediaItems: List<MediaItem>?) {
        if (!mediaItems.isNullOrEmpty()) {
            Log.i(TAG, "Succeeded to get mediaItems. ${mediaItems.count()}")
            interactor.requestDownloadFiles(mediaItems)
        } else {
            Log.i(TAG, "Failed to get mediaItems.")
        }
    }

    override fun completedDownloadFiles(files: List<String>) {
        Log.i(TAG, "Completed to download files. Slide show will be started.")
        callback.requestSlideShow(files)
    }

    override fun requestSignOutResult(isSucceeded: Boolean) {
        if (isSucceeded) {
            router.startSplashActivity(activity())
            callback.requestFinish()
        } else {
        }
    }

    private fun activity() = callback.getActivity()
}