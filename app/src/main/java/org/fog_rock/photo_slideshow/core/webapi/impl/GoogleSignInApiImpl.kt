package org.fog_rock.photo_slideshow.core.webapi.impl

import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.client.GoogleSignInClientHolder
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GoogleSignInApiImpl(
    private val clientHolder: GoogleSignInClientHolder
): GoogleSignInApi {

    override suspend fun requestSilentSignIn(): ApiResult =
        suspendCoroutine { continuation ->
            var result = ApiResult.FAILED
            clientHolder.client.silentSignIn().apply {
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
            clientHolder.client.signOut().apply {
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
            clientHolder.client.revokeAccess().apply {
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
}