package org.fog_rock.photo_slideshow.test

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.google.photos.types.proto.Album
import com.google.photos.types.proto.MediaItem
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import org.fog_rock.photo_slideshow.debug.test.R

class AndroidTestModuleGenerator {

    companion object {

        fun appContext(): Context = InstrumentationRegistry.getInstrumentation().targetContext

        fun testContext(): Context = InstrumentationRegistry.getInstrumentation().context

        fun webClientSecret(): String {
            val context = testContext()
            return "{\"web\":{" +
                    "\"client_id\":\"${context.getString(R.string.client_id)}\"," +
                    "\"client_secret\":\"${context.getString(R.string.client_secret)}\"" +
                    "}}"
        }

        fun tokenInfo(): TokenInfo {
            val context = testContext()
            return TokenInfo(
                context.getString(R.string.access_token),
                context.getString(R.string.refresh_token),
                context.getString(R.string.expired_access_token_time_millis).toLong()
            )
        }

        fun albumId(number: Int = 0): String {
            val context = testContext()
            return when (number) {
                1 -> context.getString(R.string.album_id_1)
                2 -> context.getString(R.string.album_id_2)
                3 -> context.getString(R.string.album_id_3)
                else -> context.getString(R.string.album_id_1)
            }
        }

        fun mediaItemId(number: Int = 0): String {
            val context = testContext()
            return when (number) {
                1 -> context.getString(R.string.media_item_id_1)
                2 -> context.getString(R.string.media_item_id_2)
                3 -> context.getString(R.string.media_item_id_3)
                else -> context.getString(R.string.media_item_id_1)
            }
        }

        fun album(number: Int = 0): Album =
            Album.newBuilder().apply { id = albumId(number) }.build()

        fun mediaItem(number: Int = 0): MediaItem =
            MediaItem.newBuilder().apply { id = mediaItemId(number) }.build()
    }
}