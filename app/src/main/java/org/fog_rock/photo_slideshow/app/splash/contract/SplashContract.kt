package org.fog_rock.photo_slideshow.app.splash.contract

import android.app.Activity
import android.content.Intent
import org.fog_rock.photo_slideshow.core.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.entity.SignInRequest
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInClientHolder

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
         * ClientHolderを取得.
         */
        fun getClientHolder(scopes: Array<PhotoScope>): GoogleSignInClientHolder

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
        fun startGoogleSignInActivity(activity: Activity, clientHolder: GoogleSignInClientHolder, requestCode: Int)

        /**
         * MainActivityの表示.
         */
        fun startMainActivity(activity: Activity)
    }
}