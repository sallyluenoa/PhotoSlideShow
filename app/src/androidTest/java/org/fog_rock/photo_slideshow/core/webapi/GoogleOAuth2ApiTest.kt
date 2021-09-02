package org.fog_rock.photo_slideshow.core.webapi

import kotlinx.coroutines.runBlocking
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.file.AssetsFileReader
import org.fog_rock.photo_slideshow.core.webapi.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.webapi.holder.SingletonWebHolder
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.fog_rock.photo_slideshow.test.AndroidTestModuleGenerator
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/**
 * GoogleOAuth2Apiテスト
 * Googleアカウントでアプリ内にサインインした状態でテストすること.
 */
class GoogleOAuth2ApiTest {

    private val appContext = AndroidTestModuleGenerator.appContext()

    private val googleOAuth2Api: GoogleOAuth2Api = GoogleOAuth2ApiImpl()

    @Before
    fun loadClientSecret() {
        SingletonWebHolder.loadClientSecret(
            object : AssetsFileReader {
                override fun read(fileName: String): String? = AndroidTestModuleGenerator.clientSecret()
            },
            "client_secret.json"
        )
        SingletonWebHolder.setupGoogleSignInClient(
            appContext, listOf(PhotoScope.READ_ONLY),
            requestIdToken = false, requestServerAuthCode = true
        )
    }

    @Test
    fun requestTokenInfoWithAuthCode() {
        // 毎回新しい ServerAuthCode が必要なので、前処理として ServerAuthCode を取得する.
        val serverAuthCode = runBlocking {
            val googleSignInApi = GoogleSignInApiImpl()
            googleSignInApi.requestSilentSignIn()
            val account = googleSignInApi.getSignedInAccount(appContext)
            account?.serverAuthCode
        }
        assertNotNull(serverAuthCode)
        logI("ServerAuthCode: $serverAuthCode")

        val tokenInfo = runBlocking {
            googleOAuth2Api.requestTokenInfoWithAuthCode(serverAuthCode!!)
        }
        assertNotNull(tokenInfo)

        logI("[TokenInfo Result]\n" +
                "AccessToken: ${tokenInfo?.accessToken}\nRefreshToken: ${tokenInfo?.refreshToken}")
    }

    @Test
    fun requestTokenInfoWithRefreshToken() {
        val tokenInfo = runBlocking {
            val refreshToken = AndroidTestModuleGenerator.tokenInfo().refreshToken
            googleOAuth2Api.requestTokenInfoWithRefreshToken(refreshToken)
        }
        assertNotNull(tokenInfo)

        logI("[TokenInfo Result]\n" +
                "AccessToken: ${tokenInfo?.accessToken}\nRefreshToken: ${tokenInfo?.refreshToken}")
    }
}