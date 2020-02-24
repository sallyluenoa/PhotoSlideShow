package org.fog_rock.photo_slideshow.core.entity

import com.google.android.gms.common.api.Scope

/**
 * Scope of Google Photo API.
 * See https://developers.google.com/photos/library/guides/authentication-authorization
 */
enum class PhotoScope(val value: String) {
    /**
     * Read access only.
     */
    READ_ONLY("https://www.googleapis.com/auth/photoslibrary.readonly"),

    /**
     * Write access only.
     */
    WRITE_ONLY("https://www.googleapis.com/auth/photoslibrary.appendonly"),

    /**
     * Read access to media items and albums created by the developer.
     */
    READ_ONLY_APP("https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata"),

    /**
     * Access to both the READ_ONLY and WRITE_ONLY scopes. (Doesn't include SHARING.)
     */
    READ_WRITE("https://www.googleapis.com/auth/photoslibrary"),

    /**
     * Access to create an album, share it, upload media items to it, and join a shared album.
     */
    SHARING("https://www.googleapis.com/auth/photoslibrary.sharing");

    companion object {

        fun generateScope(scopes: Array<PhotoScope>): Scope {
            var tmp = ""
            scopes.forEach {
                if (tmp.isNotEmpty()) tmp += " "
                tmp += it.value
            }
            return Scope(tmp)
        }
    }
}