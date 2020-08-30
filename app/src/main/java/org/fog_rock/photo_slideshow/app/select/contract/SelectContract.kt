package org.fog_rock.photo_slideshow.app.select.contract

import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.core.viper.ViperContract

class SelectContract {

    interface Presenter : ViperContract.Presenter {
        /**
         * 共有アルバムの取得をリクエストする.
         * @see PresenterCallback.requestLoadSharedAlbumsResult
         */
        fun requestLoadSharedAlbums()
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        /**
         * 共有アルバムの取得結果.
         * @see Presenter.requestLoadSharedAlbums
         */
        fun requestLoadSharedAlbumsResult(albums: List<Album>)
    }

    interface Interactor : ViperContract.Interactor {
        /**
         * 共有アルバムの取得をリクエストする.
         * @see InteractorCallback.requestLoadSharedAlbumsResult
         */
        fun requestLoadSharedAlbums()
    }

    interface InteractorCallback : ViperContract.InteractorCallback {
        /**
         * 共有アルバムの取得結果.
         * @see Interactor.requestLoadSharedAlbums
         */
        fun requestLoadSharedAlbumsResult(albums: List<Album>)
    }
}