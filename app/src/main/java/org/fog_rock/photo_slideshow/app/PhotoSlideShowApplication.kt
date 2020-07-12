package org.fog_rock.photo_slideshow.app

import android.app.Application
import org.fog_rock.photo_slideshow.core.database.room.SingletonRoomObject

class PhotoSlideShowApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        SingletonRoomObject.setup(this)
    }
}