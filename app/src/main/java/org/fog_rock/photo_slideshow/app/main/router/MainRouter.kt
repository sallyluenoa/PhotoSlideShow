package org.fog_rock.photo_slideshow.app.main.router

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.select.view.SelectActivity

class MainRouter : MainContract.Router {

    override fun startSelectActivity(activity: Activity, albumList: List<Album>, requestCode: Int) {
        val intent = Intent(activity, SelectActivity::class.java).apply {
            putExtra("album_list", albumList.toTypedArray())
        }
        activity.startActivityForResult(intent, requestCode)
    }
}