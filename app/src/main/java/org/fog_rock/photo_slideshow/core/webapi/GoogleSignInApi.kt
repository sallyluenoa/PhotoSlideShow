package org.fog_rock.photo_slideshow.core.webapi

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.core.entity.PhotoScope
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Googleサインインに関連するAPI.
 */
class GoogleSignInApi(
    private val context: Context,
    private val clientHolder: GoogleSignInClientHolder,
    private val callback: Callback
) {

    private val TAG = GoogleSignInApi::class.java.simpleName

    interface Callback {

        /**
         * サイレントサインインを要求に成功したか.
         * @see requestSilentSignIn
         */
        fun requestSilentSignInResult(isSucceeded: Boolean)
    }

    constructor(
        context: Context, scopes: Array<PhotoScope>,
        requestIdToken: Boolean, requestServerAuthCode: Boolean, callback: Callback
    ): this(
        context, GoogleSignInClientHolder(context, scopes, requestIdToken, requestServerAuthCode), callback
    )

    /**
     * Googleアカウントでのユーザーサインインに成功したか.
     */
    fun isSucceededUserSignIn(data: Intent?): Boolean =
        GoogleSignIn.getSignedInAccountFromIntent(data).isSuccessful

    /**
     * サイレントサインイン要求.
     * https://developers.google.com/identity/sign-in/android/backend-auth
     * @see Callback.requestSilentSignInResult
     */
    fun requestSilentSignIn() {
        GlobalScope.launch(Dispatchers.Main) {
            if (GoogleSignIn.getLastSignedInAccount(context) == null) {
                Log.i(TAG, "Sign out now.")
                callback.requestSilentSignInResult(false)
                return@launch
            }
            if (!withContext(Dispatchers.Default) { silentSignIn() }) {
                Log.e(TAG, "Failed to silent sign in.")
                callback.requestSilentSignInResult(false)
                return@launch
            }
            Log.i(TAG, "Succeeded to silent sign in.")
            callback.requestSilentSignInResult(true)
        }
    }

    /**
     * サイレントサインイン.
     * Suspendメソッドなので、Coroutine内で呼び出すこと.
     */
    private suspend fun silentSignIn(): Boolean =
        suspendCoroutine { continuation ->
            clientHolder.client.silentSignIn().apply {
                addOnCompleteListener {
                    Log.i(TAG, "Completed to silent sign in.")
                    try {
                        val account = it.getResult(ApiException::class.java) ?: run {
                            Log.e(TAG, "Failed to get account from result task.")
                            continuation.resume(false)
                            return@addOnCompleteListener
                        }
                        Log.i(TAG, "Succeeded to get account from result task.")
                        continuation.resume(true)
                    } catch (e: ApiException) {
                        e.printStackTrace()
                        continuation.resume(false)
                    }
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to silent sign in.")
                    it.printStackTrace()
                    continuation.resume(false)
                }
                addOnCanceledListener {
                    Log.e(TAG, "Canceled to silent sign in.")
                    continuation.resume(false)
                }
            }
            return@suspendCoroutine
        }
}