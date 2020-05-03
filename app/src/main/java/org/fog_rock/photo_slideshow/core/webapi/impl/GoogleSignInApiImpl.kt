package org.fog_rock.photo_slideshow.core.webapi.impl

import android.util.Log
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInClientHolder
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GoogleSignInApiImpl(
    private val clientHolder: GoogleSignInClientHolder
): GoogleSignInApi {

    private val TAG = GoogleSignInApiImpl::class.java.simpleName

    override suspend fun requestSilentSignIn(): GoogleSignInApi.Result =
        suspendCoroutine { continuation ->
            var result = GoogleSignInApi.Result.FAILED
            clientHolder.client.silentSignIn().apply {
                addOnSuccessListener {
                    Log.i(TAG, "Succeeded to silent sign in.")
                    result = GoogleSignInApi.Result.SUCCEEDED
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to silent sign in.")
                    it.printStackTrace()
                    result = GoogleSignInApi.Result.FAILED
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to silent sign in.")
                    result = GoogleSignInApi.Result.CANCELED
                }
                addOnCompleteListener {
                    Log.i(TAG, "Completed to silent sign in.")
                    continuation.resume(result)
                }
            }
            return@suspendCoroutine
        }

    override suspend fun requestSignOut(): GoogleSignInApi.Result =
        suspendCoroutine { continuation ->
            var result = GoogleSignInApi.Result.FAILED
            clientHolder.client.signOut().apply {
                addOnSuccessListener {
                    Log.i(TAG, "Succeeded to sign out.")
                    result = GoogleSignInApi.Result.SUCCEEDED
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to sign out.")
                    it.printStackTrace()
                    result = GoogleSignInApi.Result.FAILED
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to sign out.")
                    result = GoogleSignInApi.Result.CANCELED
                }
                addOnCompleteListener {
                    Log.i(TAG, "Completed to sign out.")
                    continuation.resume(result)
                }
            }
            return@suspendCoroutine
        }

    override suspend fun requestRevokeAccess(): GoogleSignInApi.Result =
        suspendCoroutine { continuation ->
            var result = GoogleSignInApi.Result.FAILED
            clientHolder.client.revokeAccess().apply {
                addOnSuccessListener {
                    Log.i(TAG, "Succeeded to revoke access.")
                    result = GoogleSignInApi.Result.SUCCEEDED
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to revoke access.")
                    it.printStackTrace()
                    result = GoogleSignInApi.Result.FAILED
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to revoke access.")
                    result = GoogleSignInApi.Result.CANCELED
                }
                addOnCompleteListener {
                    Log.i(TAG, "Completed to revoke access.")
                    continuation.resume(result)
                }
            }
            return@suspendCoroutine
        }
}