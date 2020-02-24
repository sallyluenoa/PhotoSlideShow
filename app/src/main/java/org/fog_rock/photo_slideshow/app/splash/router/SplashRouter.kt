package org.fog_rock.photo_slideshow.app.splash.router

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityCompat
import org.fog_rock.photo_slideshow.app.main.view.MainActivity
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInClientHolder

class SplashRouter : SplashContract.Router {
    override fun startRuntimePermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    override fun startGoogleSignInActivity(activity: Activity, clientHolder: GoogleSignInClientHolder, requestCode: Int) {
        activity.startActivityForResult(clientHolder.client.signInIntent, requestCode)
    }

    override fun startMainActivity(activity: Activity) {
        activity.startActivity(Intent(activity, MainActivity::class.java))
    }
}