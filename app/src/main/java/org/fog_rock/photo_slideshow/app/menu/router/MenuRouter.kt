package org.fog_rock.photo_slideshow.app.menu.router

import android.app.Activity
import android.content.Intent
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.fog_rock.photo_slideshow.app.menu.contract.MenuContract

class MenuRouter : MenuContract.Router {

    override fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int) {
        val intent = Intent(activity, OssLicensesMenuActivity::class.java).apply {
            putExtra("title", activity.getString(titleResId))
        }
        activity.startActivity(intent)
    }
}