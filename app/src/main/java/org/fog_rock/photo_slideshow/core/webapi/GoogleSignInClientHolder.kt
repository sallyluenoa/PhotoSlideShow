package org.fog_rock.photo_slideshow.core.webapi

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.fog_rock.photo_slideshow.core.entity.PhotoScope

/**
 * GoogleSignInClientをシングルトンで保持するHolderクラス.
 */
class GoogleSignInClientHolder(context: Context, scopes: Array<PhotoScope>) {

    val client: GoogleSignInClient

    init {
        val scope = PhotoScope.generateScope(scopes)
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).apply {
            requestScopes(scope)
            requestEmail()
        }.build()
        client = GoogleSignIn.getClient(context, options)
    }
}