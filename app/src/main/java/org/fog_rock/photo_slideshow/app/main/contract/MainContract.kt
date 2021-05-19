package org.fog_rock.photo_slideshow.app.main.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.app.main.entity.UpdatePhotosRequest
import org.fog_rock.photo_slideshow.app.module.lib.AppDatabase
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.viper.ViperContract

interface MainContract {

    interface Presenter : ViperContract.Presenter {
        /**
         * 写真表示のためのデータ取得を要求.
         * @see PresenterCallback.requestLoadDisplayedPhotosResult
         */
        fun requestLoadDisplayedPhotos()

        /**
         * 写真更新に必要な一連処理を要求.
         * @see PresenterCallback.requestUpdateDisplayedPhotosResult
         */
        fun requestUpdateDisplayedPhotos()

        /**
         * メニュー画面表示を要求.
         */
        fun requestShowMenu()

        /**
         * Activity#onActivityResult()の結果を評価する.
         * @see Activity.onActivityResult
         */
        fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        /**
         * 写真表示のためのデータ取得結果.
         * @see Presenter.requestLoadDisplayedPhotos
         */
        fun requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>, timeIntervalSecs: Int)

        /**
         * 写真更新に必要な一連処理の結果.
         * @see Presenter.requestUpdateDisplayedPhotos
         */
        fun requestUpdateDisplayedPhotosResult(request: UpdatePhotosRequest)
    }

    interface Interactor : ViperContract.Interactor {
        /**
         * 写真表示のためのデータ取得を要求.
         * @see InteractorCallback.requestLoadDisplayedPhotosResult
         */
        fun requestLoadDisplayedPhotos()

        /**
         * 写真リストのダウンロード要求.
         * @see InteractorCallback.requestDownloadPhotosResult
         */
        fun requestDownloadPhotos(context: Context, albums: List<Album>?)

        /**
         * データベース更新要求.
         * @see InteractorCallback.requestUpdateDatabaseResult
         */
        fun requestUpdateDatabase(photosInfo: List<AppDatabase.PhotoInfo>)

        /**
         * 写真更新する必要があるか.
         */
        fun isNeededUpdatePhotos(): Boolean

        /**
         * 選択されたアルバムが存在するか.
         */
        fun hasSelectedAlbums(): Boolean
    }

    interface InteractorCallback : ViperContract.InteractorCallback {
        /**
         * 写真表示のためのデータ取得結果.
         * @see Interactor.requestLoadDisplayedPhotos
         */
        fun requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>, timeIntervalSecs: Int)

        /**
         * 写真リストのダウンロード要求の結果.
         * @see Interactor.requestDownloadPhotos
         */
        fun requestDownloadPhotosResult(photosInfo: List<AppDatabase.PhotoInfo>)

        /**
         * データベース更新要求の結果.
         * @see Interactor.requestUpdateDatabase
         */
        fun requestUpdateDatabaseResult(isSucceeded: Boolean)
    }

    interface Router : ViperContract.Router {
        /**
         * SelectActivityの表示.
         */
        fun startSelectActivity(activity: Activity, requestCode: Int)

        /**
         * MenuActivityの表示.
         */
        fun startMenuActivity(activity: Activity, requestCode: Int)
    }
}