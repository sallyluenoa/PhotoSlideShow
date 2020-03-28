package org.fog_rock.photo_slideshow.core.webapi.impl

import android.util.Log
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.auth.oauth2.TokenResponseException
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.fog_rock.photo_slideshow.core.file.FileReader
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * Google OAuth2 認証に関連するAPI
 */
class GoogleOAuth2ApiImpl(
    private val fileReader: FileReader
): GoogleOAuth2Api {

    private val TAG = GoogleOAuth2ApiImpl::class.java.simpleName

    companion object {
        private const val CLIENT_SECRET_FILE = "client_secret.json"
        private const val JSON_KEY_WEB = "web"
        private const val JSON_KEY_CLIENT_ID = "client_id"
        private const val JSON_KEY_CLIENT_SECRET = "client_secret"
    }

    private var clientId: String = ""
    private var clientSecret: String = ""

    private var tokenResponse: TokenResponse? = null

    override suspend fun requestAccessToken(serverAuthCode: String): String {
        if (!loadClientSecrets()) {
            Log.i(TAG, "Failed to load client secrets.")
            return ""
        }

        val refreshToken = tokenResponse?.refreshToken
        return if (refreshToken != null) {
            getAccessTokenWithRefreshToken(refreshToken)
        } else {
            getAccessTokenWithAuthCode(serverAuthCode)
        }
    }

    /**
     * アクセストークン取得.
     * @param serverAuthCode サーバーの認証コード
     */
    private fun getAccessTokenWithAuthCode(serverAuthCode: String): String =
        try {
            tokenResponse = GoogleAuthorizationCodeTokenRequest(
                NetHttpTransport(), JacksonFactory(), clientId, clientSecret, serverAuthCode, ""
            ).execute()
            Log.d(TAG, "accessToken: ${tokenResponse?.accessToken}")
            Log.d(TAG, "refreshToken: ${tokenResponse?.refreshToken}")
            tokenResponse!!.accessToken
        } catch (e: TokenResponseException) {
            Log.e(TAG, "Failed to get token response. " +
                    "Error: ${e.details.error}, Description: ${e.details.errorDescription}")
            e.printStackTrace()
            ""
        } catch (e: IOException) {
            Log.e(TAG, "Failed to execute request.")
            e.printStackTrace()
            ""
        }

    /**
     * アクセストークン取得.
     * @param refreshToken リフレッシュトークン
     */
    private fun getAccessTokenWithRefreshToken(refreshToken: String): String =
        try {
            tokenResponse = GoogleRefreshTokenRequest(
                NetHttpTransport(), JacksonFactory(), refreshToken, clientId, clientSecret
            ).execute()
            tokenResponse!!.accessToken
        } catch (e: TokenResponseException) {
            Log.e(TAG, "Failed to get token response. " +
                    "Error: ${e.details.error}, Description: ${e.details.errorDescription}")
            e.printStackTrace()
            ""
        } catch (e: IOException) {
            Log.e(TAG, "Failed to execute request.")
            e.printStackTrace()
            ""
        }

    /**
     * クライアントの秘密情報をロードする.
     */
    private fun loadClientSecrets(): Boolean {
        if (clientId.isNotEmpty() && clientSecret.isNotEmpty()) {
            Log.i(TAG, "Client secrets are already loaded.")
            return true
        }
        val jsonString = fileReader.readAssetsFile(CLIENT_SECRET_FILE) ?: run {
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