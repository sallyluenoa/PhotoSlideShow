package org.fog_rock.photo_slideshow.app.splash.contract

import android.app.Activity
import android.content.Intent
import org.fog_rock.photo_slideshow.app.splash.entity.SignInRequest
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.client.GoogleSignInClientHolder

class SplashContract {

    interface Presenter : ViperContract.Presenter {
        /**
         * サインインに必要な一連処理をリクエストする.
         * @see PresenterCallback.requestSignInResult
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
         * サインインに必要な一連処理の結果.
         * @see Presenter.requestSignIn
         */
        fun requestSignInResult(request: SignInRequest)
    }

    interface Interactor : ViperContract.Interactor {
        /**
         * Googleアカウントでのサイレントサインイン要求.
         * @see InteractorCallback.requestGoogleSilentSignInResult
         */
        fun requestGoogleSilentSignIn()

        /**
         * ユーザー情報の更新要求.
         * @see InteractorCallback.requestUpdateUserInfoResult
         */
        fun requestUpdateUserInfo()

        /**
         * ランタイムパーミッションが許可されているか.
         */
        fun isGrantedRuntimePermissions(permissions: Array<String>): Boolean

        /**
         * Googleアカウントでサインインしているか.
         */
        fun isGoogleSignedIn(): Boolean

        /**
         * Googleアカウントでのユーザーサインインに成功したか.
         */
        fun isSucceededGoogleUserSignIn(data: Intent?): Boolean
    }

    interface InteractorCallback: ViperContract.InteractorCallback {
        /**
         * Googleアカウントでのサイレントサインイン要求の結果.
         * @see Interactor.requestGoogleSilentSignIn
         */
        fun requestGoogleSilentSignInResult(isSucceeded: Boolean)

        /**
         * ユーザー情報の更新要求の結果.
         * @see Interactor.requestUpdateUserInfo
         */
        fun requestUpdateUserInfoResult(isSucceeded: Boolean)
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