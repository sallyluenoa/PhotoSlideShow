package org.fog_rock.photo_slideshow.app.main.router

import android.app.Activity
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.menu.view.MenuActivity
import org.fog_rock.photo_slideshow.app.module.ui.extension.startActivityForResult
import org.fog_rock.photo_slideshow.app.select.view.SelectActivity

class MainRouter : MainContract.Router {

    override fun startSelectActivity(activity: Activity, requestCode: Int) {
        activity.startActivityForResult<SelectActivity>(requestCode)
    }

    override fun startMenuActivity(activity: Activity, requestCode: Int) {
        activity.startActivityForResult<MenuActivity>(requestCode)
    }
}