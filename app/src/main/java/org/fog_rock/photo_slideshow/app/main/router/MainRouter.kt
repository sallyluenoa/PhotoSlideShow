package org.fog_rock.photo_slideshow.app.main.router

import android.app.Activity
import android.content.Intent
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.select.view.SelectActivity

class MainRouter : MainContract.Router {

    override fun startSelectActivity(activity: Activity, albums: List<Album>, requestCode: Int) {
        val intent = Intent(activity, SelectActivity::class.java).apply {
            putExtra(SelectActivity.REQUEST_ALBUMS, albums.toTypedArray())
        }
        activity.startActivityForResult(intent, requestCode)
    }

    override fun startOssLicensesMenuActivity(activity: Activity, titleResId: Int) {
        val intent = Intent(activity, OssLicensesMenuActivity::class.java).apply {
            putExtra("title", activity.getString(titleResId))
        }
        activity.startActivity(intent)
    }
}