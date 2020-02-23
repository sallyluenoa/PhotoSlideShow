package org.fog_rock.photo_slideshow.app.splash.contract

import android.app.Activity
import android.content.Intent
import com.google.android.gms.common.api.GoogleApiClient
import org.fog_rock.photo_slideshow.core.entity.SignInRequest
import org.fog_rock.photo_slideshow.core.viper.ViperContract

class SplashContract {

    interface Presenter : ViperContract.Presenter {
        fun requestSignIn()
        fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun evaluateRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    }

    interface PresenterCallback : ViperContract.PresenterCallback {
        fun succeededSignIn()
        fun failedSignIn(request: SignInRequest)
    }

    interface Interactor : ViperContract.Interactor {
        fun getGoogleApiClient(activity: Activity, scopes: Array<String>): GoogleApiClient
        fun isGrantedRuntimePermissions(permissions: Array<String>): Boolean
        fun isSignedInGoogle(): Boolean
        fun isSignedInGoogle(data: Intent?): Boolean
    }

    interface Router : ViperContract.Router {
        fun startRuntimePermissions(activity: Activity, permissions: Array<String>, requestCode: Int)
        fun startGoogleSignInActivity(activity: Activity, client: GoogleApiClient, requestCode: Int)
        fun startMainActivity(activity: Activity)
    }
}