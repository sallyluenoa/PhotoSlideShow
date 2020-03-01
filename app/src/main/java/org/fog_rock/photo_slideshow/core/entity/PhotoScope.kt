package org.fog_rock.photo_slideshow.core.entity

import com.google.android.gms.common.api.Scope

/**
 * Scope of Google Photo API.
 * See
 * + https://developers.google.com/identity/protocols/googlescopes#photoslibraryv1
 * + https://developers.google.com/photos/library/guides/authentication-authorization
 */
enum class PhotoScope(val value: String) {
    /**
     * Read and write access. (Doesn't include sharing.)
     */
    READ_WRITE("https://www.googleapis.com/auth/photoslibrary"),

    /**
     * Read access only.
     */
    READ_ONLY("https://www.googleapis.com/auth/photoslibrary.readonly"),

    /**
     * Write access only.
     */
    WRITE_ONLY("https://www.googleapis.com/auth/photoslibrary.appendonly"),

    /**
     * Read access by the developer.
     */
    READ_ONLY_APP("https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata"),

    /**
     * Sharing access.
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