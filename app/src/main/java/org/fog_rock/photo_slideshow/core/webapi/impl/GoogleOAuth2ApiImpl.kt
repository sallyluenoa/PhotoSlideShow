package org.fog_rock.photo_slideshow.core.webapi.impl

import com.google.api.client.auth.oauth2.TokenResponseException
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import org.fog_rock.photo_slideshow.core.webapi.holder.SingletonWebHolder
import java.io.IOException

class GoogleOAuth2ApiImpl(): GoogleOAuth2Api {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun requestTokenInfoWithAuthCode(serverAuthCode: String): TokenInfo? {
        try {
            val clientSecret = SingletonWebHolder.clientSecret
            val response = GoogleAuthorizationCodeTokenRequest(
                NetHttpTransport(), JacksonFactory(),
                clientSecret.web.clientId, clientSecret.web.clientSecret,
                serverAuthCode, ""
            ).execute()
            return TokenInfo(response)
        } catch (e: TokenResponseException) {
            logE("Failed to get token response. " +
                    "Error: ${e.details.error}, Description: ${e.details.errorDescription}")
            e.printStackTrace()
        } catch (e: IOException) {
            logE("Failed to execute request.")
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            logE("Failed to get TokenInfo.")
            e.printStackTrace()
        }
        return null
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun requestTokenInfoWithRefreshToken(refreshToken: String): TokenInfo? {
        try {
            val clientSecret = SingletonWebHolder.clientSecret
            val response = GoogleRefreshTokenRequest(
                NetHttpTransport(), JacksonFactory(),
                refreshToken, clientSecret.web.clientId, clientSecret.web.clientSecret
            ).execute()
            return TokenInfo(response, refreshToken)
        } catch (e: TokenResponseException) {
            logE("Failed to get token response. " +
                    "Error: ${e.details.error}, Description: ${e.details.errorDescription}")
            e.printStackTrace()
        } catch (e: IOException) {
            logE("Failed to execute request.")
            e.printStackTrace()
        }
        return null
    }
}