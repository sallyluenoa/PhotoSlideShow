package org.fog_rock.photo_slideshow.core.webapi.impl

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInClientHolder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GoogleSignInApiImpl(
    private val clientHolder: GoogleSignInClientHolder
): GoogleSignInApi {

    private val TAG = GoogleSignInApiImpl::class.java.simpleName

    override suspend fun requestSilentSignIn(): GoogleSignInAccount? = silentSignIn()

    override suspend fun requestSignOut(): Boolean {
        val isSignedOut = signOut()
        val isRevokedAccess = revokeAccess()
        return isSignedOut
    }

    /**
     * サイレントサインイン.
     */
    private suspend fun silentSignIn(): GoogleSignInAccount? =
        suspendCoroutine { continuation ->
            var account: GoogleSignInAccount? = null
            clientHolder.client.silentSignIn().apply {
                addOnSuccessListener {
                    Log.i(TAG, "Succeeded to silent sign in.")
                    account = it
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to silent sign in.")
                    it.printStackTrace()
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to silent sign in.")
                }
                addOnCompleteListener {
                    Log.i(TAG, "Completed to silent sign in.")
                    continuation.resume(account)
                }
            }
            return@suspendCoroutine
        }

    /**
     * サインアウト.
     */
    private suspend fun signOut(): Boolean =
        suspendCoroutine { continuation ->
            var isSignedOut = false
            clientHolder.client.signOut().apply {
                addOnSuccessListener {
                    Log.i(TAG, "Succeeded to sign out.")
                    isSignedOut = true
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to sign out.")
                    it.printStackTrace()
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to sign out.")
                }
                addOnCompleteListener {
                    Log.i(TAG, "Completed to sign out.")
                    continuation.resume(isSignedOut)
                }
            }
            return@suspendCoroutine
        }

    /**
     * アカウントのアクセス破棄.
     */
    private suspend fun revokeAccess(): Boolean =
        suspendCoroutine { continuation ->
            var isRevokedAccess = false
            clientHolder.client.revokeAccess().apply {
                addOnSuccessListener {
                    Log.i(TAG, "Succeeded to revoke access.")
                    isRevokedAccess = true
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to revoke access.")
                    it.printStackTrace()
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to revoke access.")
                }
                addOnCompleteListener {
                    Log.i(TAG, "Completed to revoke access.")
                    continuation.resume(isRevokedAccess)
                }
            }
            return@suspendCoroutine
        }
}