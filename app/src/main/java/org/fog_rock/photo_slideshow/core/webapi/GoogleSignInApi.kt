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

/**
 * Googleサインインに関連するAPI
 */
class GoogleSignInApi(
    private val context: Context,
    scopes: Array<PhotoScope>,
    requestIdToken: Boolean,
    requestServerAuthCode: Boolean,
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

    val clientHolder = GoogleSignInClientHolder(context, scopes, requestIdToken, requestServerAuthCode)

    /**
     * サイレントサインイン要求.
     * @see Callback.requestSilentSignInResult
     */
    fun requestSilentSignIn() {
        GlobalScope.launch(Dispatchers.Main) {
            if (GoogleSignIn.getLastSignedInAccount(context) != null) {
                withContext(Dispatchers.Default) {
                    val task = clientHolder.client.silentSignIn()
                    if (task.isSuccessful) {
                        Log.i(TAG, "Sign in account is immediately available.")
                        callback.requestSilentSignInResult(true)
                    } else {
                        task.addOnCompleteListener{
                            Log.i(TAG, "Completed silent sign in.")
                            try {
                                val account = it.getResult(ApiException::class.java)
                                callback.requestSilentSignInResult(account != null)
                            } catch (e: ApiException) {
                                e.printStackTrace()
                                callback.requestSilentSignInResult(false)
                            }
                        }
                    }
                }
            } else {
                Log.i(TAG, "Sign out now.")
                callback.requestSilentSignInResult(false)
            }
        }
    }

    /**
     * Googleアカウントでのユーザーサインインに成功したか.
     */
    fun isSucceededUserSignIn(data: Intent?): Boolean =
        GoogleSignIn.getSignedInAccountFromIntent(data).isSuccessful
}