package org.fog_rock.photo_slideshow.app.select.contract

import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.core.viper.ViperContract

interface SelectContract {

    interface Presenter : ViperContract.Presenter

    interface PresenterCallback : ViperContract.PresenterCallback {
        /**
         * 初期化処理のデータロード結果.
         * @see ViperContract.Presenter.create
         */
        fun createLoadResult(albums: List<Album>)
    }

    interface Interactor : ViperContract.Interactor

    interface InteractorCallback : ViperContract.InteractorCallback {
        /**
         * 初期化処理のデータロード結果.
         * @see ViperContract.Interactor.create
         */
        fun createLoadResult(albums: List<Album>)
    }
}