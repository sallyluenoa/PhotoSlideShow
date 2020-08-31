package org.fog_rock.photo_slideshow.core.webapi

import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.webapi.holder.SingletonWebHolder
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.fog_rock.photo_slideshow.test.AndroidTestModuleGenerator
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * GoogleSignInApiテスト
 * Googleアカウントでアプリ内にサインインした状態でテストすること.
 */
class GoogleSignInApiTest {

    private val appContext = AndroidTestModuleGenerator.appContext()

    private val googleSignInApi: GoogleSignInApi = GoogleSignInApiImpl()

    @Before
    fun setupGoogleSignInClient() {
        SingletonWebHolder.setupGoogleSignInClient(
            appContext, listOf(PhotoScope.READ_ONLY),
            requestIdToken = false, requestServerAuthCode = true
        )
    }

    @Test
    fun requestSilentSignIn() {
        if (googleSignInApi.getSignedInAccount(appContext) != null) {
            // サインイン情報があるので、サイレントサインインの正常系が通る.
            logI("Current status is signed in. Normal case will be checked.")

            val result = runBlocking {
                googleSignInApi.requestSilentSignIn()
            }
            assertEquals(ApiResult.SUCCEEDED, result)

            val account = googleSignInApi.getSignedInAccount(appContext)
            logI("[Account Result]\n" +
                    "Name: ${account?.displayName}\nEmail: ${account?.email}\n" +
                    "IdToken: ${account?.idToken}\nServerAuthCode:${account?.serverAuthCode}")
        } else {
            // サインアウト状態なので、サイレントサインインの異常系になる.
            logI("Current status is signed out. Abnormal case will be checked.")

            val result = runBlocking {
                googleSignInApi.requestSilentSignIn()
            }
            assertEquals(ApiResult.FAILED, result)
        }
    }

    @Test
    fun requestSignOut() {
        if (googleSignInApi.getSignedInAccount(appContext) != null) {
            // サインイン情報があるので、サインアウトの正常系が通る.
            logI("Current status is signed in. Normal case will be checked.")

            val result = runBlocking {
                googleSignInApi.requestSignOut()
            }
            assertEquals(ApiResult.SUCCEEDED, result)
        } else {
            // サインアウト状態なので、サインアウトの異常系になる.
            logI("Current status is signed out. Abnormal case will be checked.")

            val result = runBlocking {
                googleSignInApi.requestSignOut()
            }
            assertEquals(ApiResult.FAILED, result)
        }
    }

    @Test
    fun requestRevokeAccess() {
        if (googleSignInApi.getSignedInAccount(appContext) != null) {
            // サインイン情報があるので、アカウントアクセス破棄の正常系が通る.
            logI("Current status is signed in. Normal case will be checked.")

            val result = runBlocking {
                googleSignInApi.requestRevokeAccess()
            }
            assertEquals(ApiResult.SUCCEEDED, result)
        } else {
            // サインアウト状態なので、アカウントアクセス破棄の異常系になる.
            logI("Current status is signed out. Abnormal case will be checked.")

            val result = runBlocking {
                googleSignInApi.requestRevokeAccess()
            }
            assertEquals(ApiResult.FAILED, result)
        }
    }
}