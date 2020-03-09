package org.fog_rock.photo_slideshow.core.webapi

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.core.entity.PhotoScope
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Googleサインアウトに関連するAPI.
 */
class GoogleSignOutApi(
    private val clientHolder: GoogleSignInClientHolder,
    private val callback: Callback
) {

    private val TAG = GoogleSignOutApi::class.java.simpleName

    interface Callback {

        /**
         * サインアウト要求に成功したか.
         * @see requestSignOut
         */
        fun requestSignOutResult(isSucceeded: Boolean)
    }

    constructor(context: Context, scopes: Array<PhotoScope>, callback: Callback):
            this(GoogleSignInClientHolder(context, scopes), callback)

    /**
     * サインアウト要求. アカウントのアクセス破棄まで行う.
     * https://developers.google.com/identity/sign-in/android/disconnect
     * @see Callback.requestSignOutResult
     */
    fun requestSignOut() {
        GlobalScope.launch(Dispatchers.Main) {
            if (!withContext(Dispatchers.Default) { signOut() }) {
                Log.e(TAG, "Failed to sign out.")
                callback.requestSignOutResult(false)
                return@launch
            }
            if (!withContext(Dispatchers.Default) { revokeAccess() }) {
                Log.e(TAG, "Failed to revoke access.")
                callback.requestSignOutResult(false)
                return@launch
            }
            Log.i(TAG, "Succeeded to sign out and revoke access.")
            callback.requestSignOutResult(true)
        }
    }

    /**
     * サインアウト.
     * Suspendメソッドなので、Coroutine内で呼び出すこと.
     */
    private suspend fun signOut(): Boolean {
        return suspendCoroutine { continuation ->
            clientHolder.client.signOut().apply {
                addOnCompleteListener {
                    Log.i(TAG, "Completed to sign out.")
                    continuation.resume(true)
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to sign out.")
                    it.printStackTrace()
                    continuation.resume(false)
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to sign out.")
                    continuation.resume(false)
                }
            }
            return@suspendCoroutine
        }
    }

    /**
     * アカウントのアクセス破棄.
     * Suspendメソッドなので、Coroutine内で呼び出すこと.
     */
    private suspend fun revokeAccess(): Boolean {
        return suspendCoroutine { continuation ->
            clientHolder.client.revokeAccess().apply {
                addOnCompleteListener {
                    Log.i(TAG, "Completed to revoke access.")
                    continuation.resume(true)
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to revoke access.")
                    it.printStackTrace()
                    continuation.resume(false)
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to revoke access.")
                    continuation.resume(false)
                }
            }
            return@suspendCoroutine
        }
    }
}