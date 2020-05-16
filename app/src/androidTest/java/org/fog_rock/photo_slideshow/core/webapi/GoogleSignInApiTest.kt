package org.fog_rock.photo_slideshow.core.webapi

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * GoogleSignInApiテスト
 * Googleアカウントでアプリ内にサインインした状態でテストすること.
 */
class GoogleSignInApiTest {

    private val TAG = GoogleSignInApiTest::class.java.simpleName

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val clientHolder =
        GoogleSignInClientHolder(context, arrayOf(PhotoScope.READ_ONLY), true, true)

    private val signInApi: GoogleSignInApi = GoogleSignInApiImpl(clientHolder)

    @Test
    fun requestSilentSignIn() {
        if (GoogleSignInApi.isSignedInAccount(context)) {
            // サインイン情報があるので、サイレントサインインの正常系が通る.
            Log.i(TAG, "Current status is signed in. Normal case will be checked.")

            val result = runBlocking {
                signInApi.requestSilentSignIn()
            }
            assertEquals(GoogleSignInApi.Result.SUCCEEDED, result)

            val account = GoogleSignInApi.getSignedInAccount(context)
            Log.i(TAG, "[Account Result]\n" +
                    "Name: ${account?.displayName}\nEmail: ${account?.email}\n" +
                    "IdToken: ${account?.idToken}\nServerAuthCode:${account?.serverAuthCode}")
        } else {
            // サインアウト状態なので、サイレントサインインの異常系になる.
            Log.i(TAG, "Current status is signed out. Abnormal case will be checked.")

            val result = runBlocking {
                signInApi.requestSilentSignIn()
            }
            assertEquals(GoogleSignInApi.Result.FAILED, result)
        }
    }

    @Test
    fun requestSignOut() {
        if (GoogleSignInApi.isSignedInAccount(context)) {
            // サインイン情報があるので、サインアウトの正常系が通る.
            Log.i(TAG, "Current status is signed in. Normal case will be checked.")

            val result = runBlocking {
                signInApi.requestSignOut()
            }
            assertEquals(GoogleSignInApi.Result.SUCCEEDED, result)
        } else {
            // サインアウト状態なので、サインアウトの異常系になる.
            Log.i(TAG, "Current status is signed out. Abnormal case will be checked.")

            val result = runBlocking {
                signInApi.requestSignOut()
            }
            assertEquals(GoogleSignInApi.Result.FAILED, result)
        }
    }

    @Test
    fun requestRevokeAccess() {
        if (GoogleSignInApi.isSignedInAccount(context)) {
            // サインイン情報があるので、アカウントアクセス破棄の正常系が通る.
            Log.i(TAG, "Current status is signed in. Normal case will be checked.")

            val result = runBlocking {
                signInApi.requestRevokeAccess()
            }
            assertEquals(GoogleSignInApi.Result.SUCCEEDED, result)
        } else {
            // サインアウト状態なので、アカウントアクセス破棄の異常系になる.
            Log.i(TAG, "Current status is signed out. Abnormal case will be checked.")

            val result = runBlocking {
                signInApi.requestRevokeAccess()
            }
            assertEquals(GoogleSignInApi.Result.FAILED, result)
        }
    }
}