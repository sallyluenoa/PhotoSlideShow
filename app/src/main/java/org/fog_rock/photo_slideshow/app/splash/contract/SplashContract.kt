package org.fog_rock.photo_slideshow.app.splash.contract

import android.app.Activity
import android.content.Intent
import com.google.android.gms.common.api.GoogleApiClient
import org.fog_rock.photo_slideshow.core.entity.SignInRequest
import org.fog_rock.photo_slideshow.core.viper.ViperContract

class SplashContract {

    interface Presenter : ViperContract.Presenter {
        /**
         * サインインに必要な一連処理をリクエストする.
         */
        fun requestSignIn()

        /**
         * Activity#onActivityResult()の結果を評価する.
         */
        fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

        /**
         * Activity#onRequestPermissionsResult()の結果を評価する.
         */
        fun evaluateRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        /**
         * サインインの一連処理に成功.
         */
        fun succeededSignIn()

        /**
         * サインインの一連処理に失敗.
         * @param request 失敗したリクエスト
         */
        fun failedSignIn(request: SignInRequest)
    }

    interface Interactor : ViperContract.Interactor {
        /**
         * GoogleApiClientを取得.
         */
        fun getGoogleApiClient(activity: Activity, scopes: Array<String>): GoogleApiClient

        /**
         * ランタイムパーミッションが許可されているか.
         */
        fun isGrantedRuntimePermissions(permissions: Array<String>): Boolean

        /**
         * Googleアカウントでサインインしているか.
         */
        fun isSignedInGoogle(): Boolean

        /**
         * Googleアカウントでのサインインに成功したか.
         */
        fun isSignedInGoogle(data: Intent?): Boolean
    }

    interface Router : ViperContract.Router {
        /**
         * ランタイムパーミッション許可の表示.
         */
        fun startRuntimePermissions(activity: Activity, permissions: Array<String>, requestCode: Int)

        /**
         * Googleサインインの表示.
         */
        fun startGoogleSignInActivity(activity: Activity, client: GoogleApiClient, requestCode: Int)

        /**
         * MainActivityの表示.
         */
        fun startMainActivity(activity: Activity)
    }
}