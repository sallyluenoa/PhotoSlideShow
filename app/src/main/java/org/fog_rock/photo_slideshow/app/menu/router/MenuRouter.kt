package org.fog_rock.photo_slideshow.app.menu.router

import android.app.Activity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract
import org.fog_rock.photo_slideshow.app.module.ui.extension.newIntent
import org.fog_rock.photo_slideshow.app.module.ui.extension.startActivityAndFinishAll
import org.fog_rock.photo_slideshow.app.splash.view.SplashActivity

class MenuRouter : MenuContract.Router {

    override fun startSplashActivityAndFinishAll(activity: Activity) {
        activity.startActivityAndFinishAll<SplashActivity>()
    }

    override fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int) {
        activity.startActivity(activity.newIntent<OssLicensesMenuActivity>().apply {
            putExtra("title", activity.getString(titleResId))
        })
    }
}