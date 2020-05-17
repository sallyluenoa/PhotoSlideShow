package org.fog_rock.photo_slideshow.core.webapi.impl

import android.util.Log
import com.google.api.client.auth.oauth2.TokenResponseException
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.fog_rock.photo_slideshow.core.file.AssetsFileReader
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * Google OAuth2 認証に関連するAPI
 */
class GoogleOAuth2ApiImpl(
    private val fileReader: AssetsFileReader
): GoogleOAuth2Api {

    companion object {
        private val TAG = GoogleOAuth2ApiImpl::class.java.simpleName

        private const val CLIENT_SECRET_FILE = "client_secret.json"
        private const val JSON_KEY_WEB = "web"
        private const val JSON_KEY_CLIENT_ID = "client_id"
        private const val JSON_KEY_CLIENT_SECRET = "client_secret"
    }

    private var clientId: String = ""
    private var clientSecret: String = ""

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun requestTokenInfoWithAuthCode(serverAuthCode: String): TokenInfo? {
        if (!loadClientSecrets()) {
            Log.i(TAG, "Failed to load client secrets.")
            return null
        }

        try {
            val response = GoogleAuthorizationCodeTokenRequest(
                NetHttpTransport(), JacksonFactory(), clientId, clientSecret, serverAuthCode, ""
            ).execute()
            return TokenInfo(response)
        } catch (e: TokenResponseException) {
            Log.e(TAG, "Failed to get token response. " +
                    "Error: ${e.details.error}, Description: ${e.details.errorDescription}")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to execute request.")
            e.printStackTrace()
        }
        return null
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun requestTokenInfoWithRefreshToken(refreshToken: String): TokenInfo? {
        if (!loadClientSecrets()) {
            Log.i(TAG, "Failed to load client secrets.")
            return null
        }

        try {
            val response = GoogleRefreshTokenRequest(
                NetHttpTransport(), JacksonFactory(), refreshToken, clientId, clientSecret
            ).execute()
            return TokenInfo(response, refreshToken)
        } catch (e: TokenResponseException) {
            Log.e(TAG, "Failed to get token response. " +
                    "Error: ${e.details.error}, Description: ${e.details.errorDescription}")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to execute request.")
            e.printStackTrace()
        }
        return null
    }

    /**
     * クライアントの秘密情報をロードする.
     */
    private fun loadClientSecrets(): Boolean {
        if (clientId.isNotEmpty() && clientSecret.isNotEmpty()) {
            Log.i(TAG, "Client secrets are already loaded.")
            return true
        }
        val jsonString = fileReader.read(CLIENT_SECRET_FILE) ?: run {
            Log.e(TAG, "Failed to read assets file.")
            return false
        }

        return try {
            val webSecret = JSONObject(jsonString).getJSONObject(JSON_KEY_WEB)
            clientId = webSecret.getString(JSON_KEY_CLIENT_ID)
            clientSecret = webSecret.getString(JSON_KEY_CLIENT_SECRET)
            true
        } catch (e : JSONException) {
            Log.e(TAG, "Failed to get client secrets from json object.")
            e.printStackTrace()
            false
        }
    }
}