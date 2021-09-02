package org.fog_rock.photo_slideshow.test

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.webapi.entity.ClientSecret
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class AndroidTestModuleGenerator {

    companion object {

        fun appContext(): Context = InstrumentationRegistry.getInstrumentation().targetContext

        fun testContext(): Context = InstrumentationRegistry.getInstrumentation().context

        fun clientSecret(): String = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
            .toJson(ClientSecret(ClientSecret.WebInfo(
                clientId = AndroidTestConstants.CLIENT_ID,
                projectId = AndroidTestConstants.PROJECT_ID
            )))

        fun tokenInfo(): TokenInfo = TokenInfo(
            AndroidTestConstants.ACCESS_TOKEN,
            AndroidTestConstants.REFRESH_TOKEN,
            AndroidTestConstants.EXPIRED_ACCESS_TOKEN_TIME_MILLIS
        )

        fun albumId(number: Int = 0): String = when (number) {
            0 -> AndroidTestConstants.ALBUM_ID_0
            1 -> AndroidTestConstants.ALBUM_ID_1
            2 -> AndroidTestConstants.ALBUM_ID_2
            else -> AndroidTestConstants.ALBUM_ID_0
        }

        fun mediaItemId(number: Int = 0): String = when (number) {
            0 -> AndroidTestConstants.MEDIA_ITEM_ID_0
            1 -> AndroidTestConstants.MEDIA_ITEM_ID_1
            2 -> AndroidTestConstants.MEDIA_ITEM_ID_2
            else -> AndroidTestConstants.MEDIA_ITEM_ID_0
        }

        fun album(number: Int = 0): Album =
            Album.newBuilder().apply { id = albumId(number) }.build()

        fun mediaItem(number: Int = 0): MediaItem =
            MediaItem.newBuilder().apply { id = mediaItemId(number) }.build()
    }
}