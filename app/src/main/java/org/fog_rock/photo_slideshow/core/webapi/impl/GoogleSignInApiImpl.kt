package org.fog_rock.photo_slideshow.core.webapi.impl

import android.util.Log
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.client.GoogleSignInClientHolder
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GoogleSignInApiImpl(
    private val clientHolder: GoogleSignInClientHolder
): GoogleSignInApi {

    companion object {
        private val TAG = GoogleSignInApiImpl::class.java.simpleName
    }

    override suspend fun requestSilentSignIn(): ApiResult =
        suspendCoroutine { continuation ->
            var result = ApiResult.FAILED
            clientHolder.client.silentSignIn().apply {
                addOnSuccessListener {
                    Log.i(TAG, "Succeeded to silent sign in.")
                    result = ApiResult.SUCCEEDED
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to silent sign in.")
                    it.printStackTrace()
                    result = ApiResult.FAILED
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to silent sign in.")
                    result = ApiResult.CANCELED
                }
                addOnCompleteListener {
                    Log.i(TAG, "Completed to silent sign in.")
                    continuation.resume(result)
                }
            }
            return@suspendCoroutine
        }

    override suspend fun requestSignOut(): ApiResult =
        suspendCoroutine { continuation ->
            var result = ApiResult.FAILED
            clientHolder.client.signOut().apply {
                addOnSuccessListener {
                    Log.i(TAG, "Succeeded to sign out.")
                    result = ApiResult.SUCCEEDED
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to sign out.")
                    it.printStackTrace()
                    result = ApiResult.FAILED
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to sign out.")
                    result = ApiResult.CANCELED
                }
                addOnCompleteListener {
                    Log.i(TAG, "Completed to sign out.")
                    continuation.resume(result)
                }
            }
            return@suspendCoroutine
        }

    override suspend fun requestRevokeAccess(): ApiResult =
        suspendCoroutine { continuation ->
            var result = ApiResult.FAILED
            clientHolder.client.revokeAccess().apply {
                addOnSuccessListener {
                    Log.i(TAG, "Succeeded to revoke access.")
                    result = ApiResult.SUCCEEDED
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to revoke access.")
                    it.printStackTrace()
                    result = ApiResult.FAILED
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to revoke access.")
                    result = ApiResult.CANCELED
                }
                addOnCompleteListener {
                    Log.i(TAG, "Completed to revoke access.")
                    continuation.resume(result)
                }
            }
            return@suspendCoroutine
        }
}