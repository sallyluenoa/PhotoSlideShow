package org.fog_rock.photo_slideshow.app.menu.contract

import android.app.Activity
import org.fog_rock.photo_slideshow.core.viper.ViperContract

interface MenuContract {

    interface Presenter : ViperContract.Presenter {
        /**
         * ライセンス画面表示を要求.
         */
        fun requestShowLicenses()

        /**
         * ユーザーの切り替えを要求.
         */
        fun requestChangeUser()

        /**
         * サインアウトを要求.
         */
        fun requestSignOut()
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        /**
         * 初期化処理のデータロード結果.
         * @see ViperContract.Presenter.create
         */
        fun createLoadResult(accountName: String, emailAddress: String)
    }

    interface Interactor : ViperContract.Interactor

    interface InteractorCallback : ViperContract.InteractorCallback {
        /**
         * 初期化処理のデータロード結果.
         * @see ViperContract.Interactor.create
         */
        fun createLoadResult(accountName: String, emailAddress: String)
    }

    interface Router : ViperContract.Router {
        /**
         * OssLicensesMenuActivityの表示.
         */
        fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int)
    }
}