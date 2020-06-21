package org.fog_rock.photo_slideshow.app.splash.interactor

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.core.database.UserInfoDatabase
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class SplashInteractor(
    private val context: Context,
    private val signInApi: GoogleSignInApi,
    private val oAuth2Api: GoogleOAuth2Api,
    private val database: UserInfoDatabase,
    private val callback: SplashContract.InteractorCallback
): SplashContract.Interactor {

    override fun destroy() {
    }

    override fun requestGoogleSilentSignIn() {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.Default) {
                signInApi.requestSilentSignIn()
            }
            callback.requestGoogleSilentSignInResult(result == ApiResult.SUCCEEDED)
        }
    }

    override fun requestUpdateUserInfo() {
        GlobalScope.launch(Dispatchers.Main) {
            val account = GoogleSignInApi.getSignedInAccount(context)
            val email = account?.email ?: run {
                requestUpdateUserInfoResult(false)
                return@launch
            }
            val tokenInfo = requestTokenInfo(email, account.serverAuthCode) ?: run {
                requestUpdateUserInfoResult(false)
                return@launch
            }
            val isSucceeded = withContext(Dispatchers.Default) {
                database.update(email, tokenInfo)
            }
            requestUpdateUserInfoResult(isSucceeded)
        }
    }

    override fun isGrantedRuntimePermissions(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            logI("OS version is less than M.")
            return true
        }
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                logI("All runtime permissions are not granted.")
                return false
            }
        }
        logI("All runtime permissions are granted.")
        return true
    }

    override fun isGoogleSignedIn(): Boolean =
        GoogleSignInApi.isSignedInAccount(context)

    override fun isSucceededGoogleUserSignIn(data: Intent?): Boolean =
        GoogleSignInApi.isSucceededUserSignIn(data)

    private suspend fun requestTokenInfo(email: String, serverAuthCode: String?): TokenInfo? {
        val tokenInfo = withContext(Dispatchers.Default) {
            val userInfo = database.find(email)
            if (userInfo != null) {
                oAuth2Api.requestTokenInfoWithRefreshToken(userInfo.refreshToken)
            } else {
                null
            }
        }
        if (tokenInfo != null) return tokenInfo

        return withContext(Dispatchers.Default) {
            if (serverAuthCode != null) {
                oAuth2Api.requestTokenInfoWithAuthCode(serverAuthCode)
            } else {
                null
            }
        }
    }

    private suspend fun requestUpdateUserInfoResult(isSucceeded: Boolean) {
        if (!isSucceeded) {
            // 失敗した場合はGoogleアカウントアクセス破棄をする.
            withContext(Dispatchers.Default) {
                signInApi.requestRevokeAccess()
            }
        }
        callback.requestUpdateUserInfoResult(false)
    }
}