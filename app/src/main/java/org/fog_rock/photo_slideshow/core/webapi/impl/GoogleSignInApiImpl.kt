package org.fog_rock.photo_slideshow.core.webapi.impl

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.holder.SingletonWebHolder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GoogleSignInApiImpl(private val context: Context): GoogleSignInApi {

    override suspend fun requestSilentSignIn(): ApiResult =
        suspendCoroutine { continuation ->
            var result = ApiResult.FAILED
            SingletonWebHolder.googleSignInClient.silentSignIn().apply {
                addOnSuccessListener {
                    logI("Succeeded to silent sign in.")
                    result = ApiResult.SUCCEEDED
                }
                addOnFailureListener {
                    logE("Failed to silent sign in.")
                    it.printStackTrace()
                    result = ApiResult.FAILED
                }
                addOnCanceledListener {
                    logE("Canceled to silent sign in.")
                    result = ApiResult.CANCELED
                }
                addOnCompleteListener {
                    logI("Completed to silent sign in.")
                    continuation.resume(result)
                }
            }
            return@suspendCoroutine
        }

    override suspend fun requestSignOut(): ApiResult =
        suspendCoroutine { continuation ->
            var result = ApiResult.FAILED
            SingletonWebHolder.googleSignInClient.signOut().apply {
                addOnSuccessListener {
                    logI("Succeeded to sign out.")
                    result = ApiResult.SUCCEEDED
                }
                addOnFailureListener {
                    logE("Failed to sign out.")
                    it.printStackTrace()
                    result = ApiResult.FAILED
                }
                addOnCanceledListener {
                    logE("Canceled to sign out.")
                    result = ApiResult.CANCELED
                }
                addOnCompleteListener {
                    logI("Completed to sign out.")
                    continuation.resume(result)
                }
            }
            return@suspendCoroutine
        }

    override suspend fun requestRevokeAccess(): ApiResult =
        suspendCoroutine { continuation ->
            var result = ApiResult.FAILED
            SingletonWebHolder.googleSignInClient.revokeAccess().apply {
                addOnSuccessListener {
                    logI("Succeeded to revoke access.")
                    result = ApiResult.SUCCEEDED
                }
                addOnFailureListener {
                    logE("Failed to revoke access.")
                    it.printStackTrace()
                    result = ApiResult.FAILED
                }
                addOnCanceledListener {
                    logE("Canceled to revoke access.")
                    result = ApiResult.CANCELED
                }
                addOnCompleteListener {
                    logI("Completed to revoke access.")
                    continuation.resume(result)
                }
            }
            return@suspendCoroutine
        }

    override fun getSignedInAccount(): GoogleSignInAccount =
        GoogleSignIn.getLastSignedInAccount(context) ?:
        throw NullPointerException("There are no sign in account.")

    override fun getSignedInEmailAddress(): String =
        GoogleSignIn.getLastSignedInAccount(context)?.email ?:
        throw NullPointerException("There are no sign in account.")

    override fun isSignedInAccount(): Boolean =
        GoogleSignIn.getLastSignedInAccount(context) != null

    override fun isSucceededUserSignIn(data: Intent?): Boolean =
        GoogleSignIn.getSignedInAccountFromIntent(data).isSuccessful
}