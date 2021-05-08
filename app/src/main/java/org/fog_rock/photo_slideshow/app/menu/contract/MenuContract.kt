package org.fog_rock.photo_slideshow.app.menu.contract

import android.app.Activity
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

interface MenuContract {

    interface Presenter : ViperContract.Presenter {
        /**
         * ライセンス画面表示を要求.
         */
        fun requestShowLicenses()

        /**
         * ユーザーの切り替えを要求.
         * @see PresenterCallback.onFailedChangeUser
         */
        fun requestChangeUser()

        /**
         * サインアウトを要求.
         * @see PresenterCallback.onFailedSignOut
         */
        fun requestSignOut()
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        /**
         * 初期化処理のデータロード結果.
         * @see ViperContract.Presenter.create
         */
        fun onCreateResult(accountName: String, emailAddress: String)

        /**
         * ユーザーの切り替えに失敗.
         * @see Presenter.requestChangeUser
         */
        fun onFailedChangeUser()

        /**
         * サインアウトに失敗.
         * @see Presenter.requestSignOut
         */
        fun onFailedSignOut()
    }

    interface Interactor : ViperContract.Interactor {
        /**
         * ユーザーの切り替えを要求.
         * @see InteractorCallback.onChangeUserResult
         */
        fun requestChangeUser()

        /**
         * サインアウトを要求.
         * @see InteractorCallback.onSignOutResult
         */
        fun requestSignOut()
    }

    interface InteractorCallback : ViperContract.InteractorCallback {
        /**
         * 初期化処理のデータロード結果.
         * @see ViperContract.Interactor.create
         */
        fun onCreateResult(accountName: String, emailAddress: String)

        /**
         * ユーザーの切り替え要求の結果.
         * @see Interactor.requestChangeUser
         */
        fun onChangeUserResult(result: ApiResult)

        /**
         * サインアウト要求の結果.
         * @see Interactor.requestSignOut
         */
        fun onSignOutResult(result: ApiResult)
    }

    interface Router : ViperContract.Router {
        /**
         * SplashActivityの表示.
         * これまでの画面はすべて終了する.
         */
        fun startSplashActivityAndFinishAll(activity: Activity)

        /**
         * OssLicensesMenuActivityの表示.
         */
        fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int)
    }
}