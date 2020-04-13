package org.fog_rock.photo_slideshow.core.webapi

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.junit.Test

import org.junit.Assert.*

/**
 * GoogleSignInApiテスト
 * Googleアカウントでアプリ内にサインインした状態でテストすること.
 */
class GoogleSignInApiTest {

    private val TAG = GoogleSignInApiTest::class.java.simpleName

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val clientHolder =
        GoogleSignInClientHolder(context, arrayOf(PhotoScope.READ_ONLY), true, true)

    private val signInApi = GoogleSignInApiImpl(clientHolder)

    @Test
    fun requestSilentSignIn() {
        if (GoogleSignInApi.getSignedInAccount(context) != null) {
            // サインイン情報があるので、サイレントサインインの正常系が通る.
            Log.i(TAG, "Current status is signed in. Normal case will be checked.")

            val account = runBlocking {
                signInApi.requestSilentSignIn()
            }
            assertNotEquals(null, account)
            assertNotEquals(null, account?.displayName)
            assertNotEquals(null, account?.email)
            assertNotEquals(null, account?.idToken)
            assertNotEquals(null, account?.serverAuthCode)

            Log.i(TAG, "[Account Result]\n" +
                    "Name: ${account?.displayName}\nEmail: ${account?.email}\n" +
                    "IdToken: ${account?.idToken}\nServerAuthCode:${account?.serverAuthCode}")
        } else {
            // サインアウト状態なので、サイレントサインインの異常系になる.
            Log.i(TAG, "Current status is signed out. Abnormal case will be checked.")

            val account = runBlocking {
                signInApi.requestSilentSignIn()
            }
            assertEquals(null, account)
        }
    }

    @Test
    fun requestSignOut() {
        if (GoogleSignInApi.getSignedInAccount(context) == null) {
            Log.w(TAG, "Must be signed in before running test.")
            return
        }

        val result = runBlocking {
            signInApi.requestSignOut()
        }
        assertEquals(true, result)
    }
}