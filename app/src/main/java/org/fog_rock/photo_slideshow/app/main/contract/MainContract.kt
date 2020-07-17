package org.fog_rock.photo_slideshow.app.main.contract

import android.app.Activity
import android.content.Intent
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.app.main.entity.UpdatePhotosRequest
import org.fog_rock.photo_slideshow.core.viper.ViperContract

class MainContract {

    interface Presenter : ViperContract.Presenter {
        /**
         * 写真更新に必要な一連処理をリクエストする.
         * @see PresenterCallback.requestUpdatePhotosResult
         */
        fun requestUpdatePhotos()

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
         * 写真更新に必要な一連処理の結果.
         * @see Presenter.requestUpdatePhotos
         */
        fun requestUpdatePhotosResult(request: UpdatePhotosRequest)

        /**
         * ビューの終了処理を要求する.
         */
        fun requestFinish()
    }

    interface Interactor : ViperContract.Interactor {
        /**
         * ユーザー情報の取得要求.
         * @see InteractorCallback.requestLoadUserInfoResult
         */
        fun requestLoadUserInfo()

        /**
         * アクセストーンの更新要求.
         * @see InteractorCallback.requestUpdateAccessTokenResult
         */
        fun requestUpdateAccessToken()

        /**
         * 選択したアルバムの更新要求.
         * @see InteractorCallback.requestUpdateSelectedAlbumsResult
         */
        fun requestUpdateSelectedAlbums()

        /**
         * 写真リストのダウンロード要求.
         * @see InteractorCallback.requestDownloadPhotosResult
         */
        fun requestDownloadPhotos()

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
         * アクセストークンを更新する必要があるか.
         */
        fun isNeededUpdateAccessToken(): Boolean

        /**
         * 選択されたアルバムが存在するか.
         */
        fun hasSelectedAlbums(): Boolean
    }

    interface InteractorCallback : ViperContract.InteractorCallback {
        /**
         * ユーザー情報の取得要求の結果.
         * @see Interactor.requestLoadUserInfo
         */
        fun requestLoadUserInfoResult(isSucceeded: Boolean)

        /**
         * アクセストーンの更新要求の結果.
         * @see Interactor.requestUpdateAccessToken
         */
        fun requestUpdateAccessTokenResult(isSucceeded: Boolean)

        /**
         * 選択したアルバムの更新要求の結果.
         * @see Interactor.requestUpdateSelectedAlbums
         */
        fun requestUpdateSelectedAlbumsResult(isSucceeded: Boolean)

        /**
         * 写真リストのダウンロード要求の結果.
         * @see Interactor.requestDownloadPhotos
         */
        fun requestDownloadPhotosResult(isSucceeded: Boolean)

        /**
         * サインアウト結果.
         * @see Interactor.requestSignOut
         */
        fun requestSignOutResult(isSucceeded: Boolean)
    }

    interface Router : ViperContract.Router {
        /**
         * SplashActivityの表示.
         */
        fun startSplashActivity(activity: Activity)

        /**
         * SelectActivityの表示.
         */
        fun startSelectActivity(activity: Activity, albums: List<Album>, requestCode: Int)

        /**
         * OssLicensesMenuActivityの表示.
         */
        fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int)
    }
}