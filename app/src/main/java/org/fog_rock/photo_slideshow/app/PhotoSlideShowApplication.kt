package org.fog_rock.photo_slideshow.app

import android.app.Application
import org.fog_rock.photo_slideshow.core.database.room.SingletonRoomObject
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.file.impl.AssetsFileReaderImpl
import org.fog_rock.photo_slideshow.core.webapi.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.webapi.holder.SingletonWebHolder

class PhotoSlideShowApplication: Application() {

    companion object {
        private const val CLIENT_SECRET_JSON = "client_secret.json"

        private const val CONNECTION_TIMEOUT_MILLISECS = 10 * 1000L
        private const val READ_TIMEOUT_MILLISECS = 10 * 1000L
        private const val WRITE_TIMEOUT_MILLISECS = 60 * 1000L
    }


    override fun onCreate() {
        super.onCreate()

        logI("Setup singleton objects.")

        SingletonRoomObject.setup(this)

        SingletonWebHolder.loadClientSecret(
            AssetsFileReaderImpl(this), CLIENT_SECRET_JSON
        )
        SingletonWebHolder.setupOkHttpClient(
            CONNECTION_TIMEOUT_MILLISECS, READ_TIMEOUT_MILLISECS, WRITE_TIMEOUT_MILLISECS
        )
        SingletonWebHolder.setupGoogleSignInClient(
            this, listOf(PhotoScope.READ_ONLY),
            requestIdToken = false, requestServerAuthCode = true
        )
    }
}