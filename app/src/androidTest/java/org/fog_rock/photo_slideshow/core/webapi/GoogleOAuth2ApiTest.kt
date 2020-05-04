package org.fog_rock.photo_slideshow.core.webapi

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.file.AssetsFileReader
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * GoogleOAuth2Apiテスト
 * Googleアカウントでアプリ内にサインインした状態でテストすること.
 */
class GoogleOAuth2ApiTest {

    companion object {
        /**
         * それぞれ必要な情報を適宜更新すること.
         */
        private const val CLIENT_ID = ""
        private const val CLIENT_SECRET = ""
        private const val REFRESH_TOKEN = ""
    }

    private val TAG = GoogleOAuth2ApiTest::class.java.simpleName

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private val fileReader = object : AssetsFileReader {
        override fun read(fileName: String): String? =
            "{\"web\":{\"client_id\":\"$CLIENT_ID\",\"client_secret\":\"$CLIENT_SECRET\"}}"
    }

    private val oauth2Api = GoogleOAuth2ApiImpl(fileReader)

    @Test
    fun requestTokenInfoWithAuthCode() {
        // 毎回新しい ServerAuthCode が必要なので、前処理として ServerAuthCode を取得する.
        val serverAuthCode = runBlocking {
            val signInApi = GoogleSignInApiImpl(
                GoogleSignInClientHolder(context, arrayOf(PhotoScope.READ_ONLY), false, true)
            )
            signInApi.requestSilentSignIn()
            val account = GoogleSignInApi.getSignedInAccount(context)
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
            oauth2Api.requestTokenInfoWithRefreshToken(REFRESH_TOKEN)
        }
        assertNotNull(tokenInfo)

        Log.i(TAG, "[TokenInfo Result]\n" +
                "AccessToken: ${tokenInfo?.accessToken}\nRefreshToken: ${tokenInfo?.refreshToken}\n" +
                "ExpiredAccessTokenTimeMillis: ${tokenInfo?.expiredAccessTokenTimeMillis}")
    }
}