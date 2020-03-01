package org.fog_rock.photo_slideshow.app.splash.contract

import android.app.Activity
import android.content.Intent
import org.fog_rock.photo_slideshow.core.entity.SignInRequest
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInClientHolder

class SplashContract {

    interface Presenter : ViperContract.Presenter {
        /**
         * サインインに必要な一連処理をリクエストする.
         * @see PresenterCallback.succeededSignIn
         * @see PresenterCallback.failedSignIn
         */
        fun requestSignIn()

        /**
         * Activity#onActivityResult()の結果を評価する.
         * @see Activity.onActivityResult
         */
        fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

        /**
         * Activity#onRequestPermissionsResult()の結果を評価する.
         * @see Activity.onRequestPermissionsResult
         */
        fun evaluateRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        /**
         * サインインの一連処理に成功.
         * @see Presenter.requestSignIn
         */
        fun succeededSignIn()

        /**
         * サインインの一連処理に失敗.
         * @param request 失敗したリクエスト
         * @see Presenter.requestSignIn
         */
        fun failedSignIn(request: SignInRequest)
    }

    interface Interactor : ViperContract.Interactor {
        /**
         * ClientHolderを取得.
         */
        fun getClientHolder(): GoogleSignInClientHolder

        /**
         * ランタイムパーミッションが許可されているか.
         */
        fun isGrantedRuntimePermissions(permissions: Array<String>): Boolean

        /**
         * Googleアカウントでのサイレントサインイン要求.
         * @see InteractorCallback.requestGoogleSilentSignInResult
         */
        fun requestGoogleSilentSignIn()

        /**
         * Googleアカウントでのユーザーサインインに成功したか.
         */
        fun isSucceededGoogleUserSignIn(data: Intent?): Boolean
    }

    interface InteractorCallback: ViperContract.InteractorCallback {
        /**
         * Googleアカウントでのサイレントサインインを要求に成功したか.
         * @see Interactor.requestGoogleSilentSignIn
         */
        fun requestGoogleSilentSignInResult(isSucceeded: Boolean)
    }

    interface Router : ViperContract.Router {
        /**
         * ランタイムパーミッション許可の表示.
         */
        fun startRuntimePermissions(activity: Activity, permissions: Array<String>, requestCode: Int)

        /**
         * Googleサインインの表示.
         */
        fun startGoogleSignInActivity(activity: Activity, clientHolder: GoogleSignInClientHolder, requestCode: Int)

        /**
         * MainActivityの表示.
         */
        fun startMainActivity(activity: Activity)
    }
}