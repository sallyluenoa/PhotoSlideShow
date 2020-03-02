package org.fog_rock.photo_slideshow.app.main.contract

import android.app.Activity
import android.content.Intent
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.viper.ViperContract

class MainContract {

    interface Presenter : ViperContract.Presenter {
        fun requestAlbums()

        /**
         * Activity#onActivityResult()の結果を評価する.
         * @see Activity.onActivityResult
         */
        fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        fun requestSlideShow(fileList: List<String>)
    }

    interface Interactor : ViperContract.Interactor {
        fun requestSharedAlbums()
        fun requestMediaItems(album: Album)
        fun requestDownloadFiles(mediaItemList: List<MediaItem>)
    }

    interface InteractorCallback : ViperContract.InteractorCallback {
        fun requestSharedAlbumsResult(albumList: List<Album>?)
        fun requestMediaItemsResult(mediaItemList: List<MediaItem>?)
        fun completedDownloadFiles(fileList: List<String>)
    }

    interface Router : ViperContract.Router {
        fun startSelectActivity(activity: Activity, albumList: List<Album>, requestCode: Int)
    }
}