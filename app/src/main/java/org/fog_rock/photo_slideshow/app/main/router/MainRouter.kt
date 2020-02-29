package org.fog_rock.photo_slideshow.app.main.router

import android.app.Activity
import android.content.Intent
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.select.view.SelectActivity

class MainRouter : MainContract.Router {

    override fun startSelectActivity(activity: Activity, requestCode: Int) {
        activity.startActivityForResult(Intent(activity, SelectActivity::class.java), requestCode)
    }
}