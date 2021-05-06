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
         * @see PresenterCallback.requestChangeUserResult
         */
        fun requestChangeUser()

        /**
         * サインアウトを要求.
         * @see PresenterCallback.requestSignOutResult
         */
        fun requestSignOut()
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        /**
         * 初期化処理のデータロード結果.
         * @see ViperContract.Presenter.create
         */
        fun createLoadResult(accountName: String, emailAddress: String)

        /**
         * ユーザーの切り替え要求の結果.
         * @see Presenter.requestChangeUser
         */
        fun requestChangeUserResult(result: ApiResult)

        /**
         * サインアウト要求の結果.
         * @see Presenter.requestSignOut
         */
        fun requestSignOutResult(result: ApiResult)
    }

    interface Interactor : ViperContract.Interactor {
        /**
         * ユーザーの切り替えを要求.
         * @see InteractorCallback.requestChangeUserResult
         */
        fun requestChangeUser()

        /**
         * サインアウトを要求.
         * @see InteractorCallback.requestSignOutResult
         */
        fun requestSignOut()
    }

    interface InteractorCallback : ViperContract.InteractorCallback {
        /**
         * 初期化処理のデータロード結果.
         * @see ViperContract.Interactor.create
         */
        fun createLoadResult(accountName: String, emailAddress: String)

        /**
         * ユーザーの切り替え要求の結果.
         * @see Interactor.requestChangeUser
         */
        fun requestChangeUserResult(result: ApiResult)

        /**
         * サインアウト要求の結果.
         * @see Interactor.requestSignOut
         */
        fun requestSignOutResult(result: ApiResult)
    }

    interface Router : ViperContract.Router {
        /**
         * OssLicensesMenuActivityの表示.
         */
        fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int)
    }
}