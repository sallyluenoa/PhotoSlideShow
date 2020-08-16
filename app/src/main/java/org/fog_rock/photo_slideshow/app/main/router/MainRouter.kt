package org.fog_rock.photo_slideshow.app.main.router

import android.app.Activity
import android.content.Intent
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.select.view.SelectActivity
import org.fog_rock.photo_slideshow.app.splash.view.SplashActivity

class MainRouter : MainContract.Router {

    override fun startSplashActivity(activity: Activity) {
        activity.startActivity(Intent(activity, SplashActivity::class.java))
    }

    override fun startSelectActivity(activity: Activity, requestCode: Int) {
        activity.startActivityForResult(Intent(activity, SelectActivity::class.java), requestCode)
    }

    override fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int) {
        val intent = Intent(activity, OssLicensesMenuActivity::class.java).apply {
            putExtra("title", activity.getString(titleResId))
        }
        activity.startActivity(intent)
    }
}