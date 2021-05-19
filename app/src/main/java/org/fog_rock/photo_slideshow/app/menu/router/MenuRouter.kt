package org.fog_rock.photo_slideshow.app.menu.router

import android.app.Activity
import android.content.Intent
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.app.splash.view.SplashActivity

class MenuRouter : MenuContract.Router {

    override fun startSplashActivityAndFinishAll(activity: Activity) {
        activity.startActivity(Intent(activity, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    override fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int) {
        activity.startActivity(Intent(activity, OssLicensesMenuActivity::class.java).apply {
            putExtra("title", activity.getString(titleResId))
        })
    }
}