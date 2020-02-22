package org.fog_rock.photo_slideshow.app.splash.router

import android.app.Activity
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract

class SplashRouter : SplashContract.Router {
    override fun startRuntimePermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    override fun startGoogleSignInActivity(activity: Activity, client: GoogleApiClient, requestCode: Int) {
        val intent = Auth.GoogleSignInApi.getSignInIntent(client)
        activity.startActivityForResult(intent, requestCode)
    }

    override fun startMainActivity(activity: Activity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}