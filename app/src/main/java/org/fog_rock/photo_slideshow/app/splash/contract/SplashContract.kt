package org.fog_rock.photo_slideshow.app.splash.contract

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.GoogleApiClient
import org.fog_rock.photo_slideshow.core.viper.BaseInteractor
import org.fog_rock.photo_slideshow.core.viper.BasePresenter
import org.fog_rock.photo_slideshow.core.viper.BaseRouter
import org.fog_rock.photo_slideshow.core.viper.BaseView

class SplashContract {

    interface View : BaseView

    interface Presenter : BasePresenter {
        fun requestSignIn()
        fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
        fun evaluateRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    }

    interface PresenterCallback {
        fun succeededSignIn()
        fun failedSignIn()
    }

    interface Interactor : BaseInteractor {
        fun getGoogleApiClient(activity: FragmentActivity, scopes: Array<String>): GoogleApiClient
        fun isGrantedRuntimePermissions(permissions: Array<String>): Boolean
        fun isSignedInGoogle(): Boolean
        fun isSignedInGoogle(data: Intent?): Boolean
    }

    interface Router : BaseRouter {
        fun startRuntimePermissions(activity: Activity, permissions: Array<String>, requestCode: Int)
        fun startGoogleSignInActivity(activity: Activity, client: GoogleApiClient, requestCode: Int)
        fun startMainActivity(activity: Activity)
    }
}