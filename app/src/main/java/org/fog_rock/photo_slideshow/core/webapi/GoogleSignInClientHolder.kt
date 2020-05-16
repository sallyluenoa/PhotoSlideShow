package org.fog_rock.photo_slideshow.core.webapi

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.core.entity.PhotoScope

/**
 * GoogleSignInClientをシングルトンで保持するHolderクラス.
 */
class GoogleSignInClientHolder(
    context: Context,
    scopes: List<PhotoScope>,
    requestIdToken: Boolean = false,
    requestServerAuthCode: Boolean = false
) {

    val client: GoogleSignInClient = try {
        val clientId = context.getString(R.string.default_web_client_id)
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).apply {
            requestEmail()
            scopes.forEach { requestScopes(it.scope()) }
            if (requestIdToken) requestIdToken(clientId)
            if (requestServerAuthCode) requestServerAuthCode(clientId)
        }.build()
        GoogleSignIn.getClient(context, options)
    } catch (e: NullPointerException) {
        throw NullPointerException("Failed to get GoogleSignInClient.")
    }
}