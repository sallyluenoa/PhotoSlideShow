package org.fog_rock.photo_slideshow.app.main.router

import android.app.Activity
import android.content.Intent
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.menu.view.MenuActivity
import org.fog_rock.photo_slideshow.app.select.view.SelectActivity
import org.fog_rock.photo_slideshow.app.splash.view.SplashActivity

class MainRouter : MainContract.Router {

    override fun startSelectActivity(activity: Activity, requestCode: Int) {
        activity.startActivityForResult(Intent(activity, SelectActivity::class.java), requestCode)
    }

    override fun startMenuActivity(activity: Activity, requestCode: Int) {
        activity.startActivityForResult(Intent(activity, MenuActivity::class.java), requestCode)
    }
}