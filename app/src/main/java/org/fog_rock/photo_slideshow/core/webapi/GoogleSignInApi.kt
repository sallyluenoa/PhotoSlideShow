package org.fog_rock.photo_slideshow.core.webapi

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

/**
 * Googleサインインに関連するAPI
 */
class GoogleSignInApi(private val context: Context) {

    /**
     * Googleアカウントの取得.
     */
    fun getAccount(): GoogleSignInAccount? =
        GoogleSignIn.getLastSignedInAccount(context)

    /**
     * Googleアカウントでサインインしているか.
     */
    fun isSignedIn(): Boolean = (getAccount() != null)

    /**
     * Googleアカウントでのサインインに成功したか.
     */
    fun isSignedIn(data: Intent?): Boolean =
        GoogleSignIn.getSignedInAccountFromIntent(data).isSuccessful
}