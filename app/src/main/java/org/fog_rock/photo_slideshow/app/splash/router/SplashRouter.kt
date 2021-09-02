package org.fog_rock.photo_slideshow.app.splash.router

import android.app.Activity
import androidx.core.app.ActivityCompat
import org.fog_rock.photo_slideshow.app.main.view.MainActivity
import org.fog_rock.photo_slideshow.app.module.ui.extension.startActivity
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.core.webapi.holder.SingletonWebHolder

class SplashRouter : SplashContract.Router {

    override fun startRuntimePermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    override fun startGoogleSignInActivity(activity: Activity, requestCode: Int) {
        activity.startActivityForResult(SingletonWebHolder.googleSignInClient.signInIntent, requestCode)
    }

    override fun startMainActivity(activity: Activity) {
        activity.startActivity<MainActivity>()
    }
}