package org.fog_rock.photo_slideshow.core.webapi

import android.util.Log
import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.file.AssetsFileReader
import org.fog_rock.photo_slideshow.core.webapi.client.GoogleSignInClientHolder
import org.fog_rock.photo_slideshow.core.webapi.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.fog_rock.photo_slideshow.test.AndroidTestModuleGenerator
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * GoogleOAuth2Apiテスト
 * Googleアカウントでアプリ内にサインインした状態でテストすること.
 */
class GoogleOAuth2ApiTest {

    companion object {
        private val TAG = GoogleOAuth2ApiTest::class.java.simpleName
    }

    private val fileReader = object : AssetsFileReader {
        override fun read(fileName: String): String? = AndroidTestModuleGenerator.webClientSecret()
    }

    private val oauth2Api: GoogleOAuth2Api = GoogleOAuth2ApiImpl(fileReader)

    @Test
    fun requestTokenInfoWithAuthCode() {
        // 毎回新しい ServerAuthCode が必要なので、前処理として ServerAuthCode を取得する.
        val serverAuthCode = runBlocking {
            val appContext = AndroidTestModuleGenerator.appContext()
            val signInApi = GoogleSignInApiImpl(
                GoogleSignInClientHolder(appContext, listOf(PhotoScope.READ_ONLY), false, true)
            )
            signInApi.requestSilentSignIn()
            val account = GoogleSignInApi.getSignedInAccount(appContext)
            account?.serverAuthCode
        }
        assertNotNull(serverAuthCode)
        Log.i(TAG, "ServerAuthCode: $serverAuthCode")

        val tokenInfo = runBlocking {
            oauth2Api.requestTokenInfoWithAuthCode(serverAuthCode!!)
        }
        assertNotNull(tokenInfo)

        Log.i(TAG, "[TokenInfo Result]\n" +
                "AccessToken: ${tokenInfo?.accessToken}\nRefreshToken: ${tokenInfo?.refreshToken}\n" +
                "ExpiredAccessTokenTimeMillis: ${tokenInfo?.expiredAccessTokenTimeMillis}")
    }

    @Test
    fun requestTokenInfoWithRefreshToken() {
        val tokenInfo = runBlocking {
            val refreshToken = AndroidTestModuleGenerator.tokenInfo().refreshToken
            oauth2Api.requestTokenInfoWithRefreshToken(refreshToken)
        }
        assertNotNull(tokenInfo)

        Log.i(TAG, "[TokenInfo Result]\n" +
                "AccessToken: ${tokenInfo?.accessToken}\nRefreshToken: ${tokenInfo?.refreshToken}\n" +
                "ExpiredAccessTokenTimeMillis: ${tokenInfo?.expiredAccessTokenTimeMillis}")
    }
}