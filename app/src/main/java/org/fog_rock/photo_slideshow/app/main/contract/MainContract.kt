package org.fog_rock.photo_slideshow.app.main.contract

import android.app.Activity
import android.content.Intent
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.app.main.entity.UpdatePhotosRequest
import org.fog_rock.photo_slideshow.app.module.AppDatabase
import org.fog_rock.photo_slideshow.core.database.entity.DisplayedPhoto
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

class MainContract {

    interface Presenter : ViperContract.Presenter {
        /**
         * 写真のロードをリクエストする.
         * @see PresenterCallback.requestLoadDisplayedPhotosResult
         */
        fun requestLoadDisplayedPhotos()

        /**
         * 写真更新に必要な一連処理をリクエストする.
         * @see PresenterCallback.requestUpdateDisplayedPhotosResult
         */
        fun requestUpdateDisplayedPhotos()

        /**
         * ライセンス表示を要求.
         */
        fun requestShowLicenses()

        /**
         * サインアウトを要求.
         */
        fun requestSignOut()

        /**
         * Activity#onActivityResult()の結果を評価する.
         * @see Activity.onActivityResult
         */
        fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        /**
         * 写真をロードした結果.
         * @see Presenter.requestLoadDisplayedPhotos
         */
        fun requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>)

        /**
         * 写真更新に必要な一連処理の結果.
         * @see Presenter.requestUpdateDisplayedPhotos
         */
        fun requestUpdateDisplayedPhotosResult(request: UpdatePhotosRequest)

        /**
         * サインアウトの要求結果.
         */
        fun requestSignOutResult(result: ApiResult)
    }

    interface Interactor : ViperContract.Interactor {
        /**
         * ユーザー情報の取得要求.
         * @see InteractorCallback.requestLoadDisplayedPhotosResult
         */
        fun requestLoadDisplayedPhotos()

        /**
         * 写真リストのダウンロード要求.
         * @see InteractorCallback.requestDownloadPhotosResult
         */
        fun requestDownloadPhotos(albums: List<Album>?)

        /**
         * データベース更新要求.
         * @see InteractorCallback.requestUpdateDatabaseResult
         */
        fun requestUpdateDatabase(photosInfo: List<AppDatabase.PhotoInfo>)

        /**
         * サインアウト要求.
         * @see InteractorCallback.requestSignOutResult
         */
        fun requestSignOut()

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
         * ユーザー情報の取得要求の結果.
         * @see Interactor.requestLoadDisplayedPhotos
         */
        fun requestLoadDisplayedPhotosResult(displayedPhotos: List<DisplayedPhoto>)

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

        /**
         * サインアウト結果.
         * @see Interactor.requestSignOut
         */
        fun requestSignOutResult(result: ApiResult)
    }

    interface Router : ViperContract.Router {
        /**
         * SplashActivityの表示.
         */
        fun startSplashActivity(activity: Activity)

        /**
         * SelectActivityの表示.
         */
        fun startSelectActivity(activity: Activity, requestCode: Int)

        /**
         * OssLicensesMenuActivityの表示.
         */
        fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int)
    }
}