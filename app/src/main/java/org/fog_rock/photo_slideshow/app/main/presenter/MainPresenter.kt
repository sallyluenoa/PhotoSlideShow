package org.fog_rock.photo_slideshow.app.main.presenter

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.util.Log
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.interactor.MainInteractor
import org.fog_rock.photo_slideshow.app.main.router.MainRouter

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

    override fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CODE_SELECT_ACTIVITY -> {
                if (resultCode == RESULT_OK && data != null) {
                    Log.i(TAG, "Succeeded to select album.")
                    val album = data.getSerializableExtra("decided_album") as Album
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

    override fun requestSharedAlbumsResult(albumList: List<Album>?) {
        if (!albumList.isNullOrEmpty()) {
            Log.i(TAG, "Succeeded to get album list. ${albumList.count()}")
            router.startSelectActivity(activity(), albumList, CODE_SELECT_ACTIVITY)
        } else {
            Log.i(TAG, "Failed to get album list.")
        }
    }

    override fun requestMediaItemsResult(mediaItemList: List<MediaItem>?) {
        if (!mediaItemList.isNullOrEmpty()) {
            Log.i(TAG, "Succeeded to get mediaItem list. ${mediaItemList.count()}")
        } else {
            Log.i(TAG, "Failed to get mediaItem list.")
        }
    }

    private fun activity() = callback.getActivity()
}