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
import org.fog_rock.photo_slideshow.app.module.AppDatabase
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.GoogleOAuth2Api
import org.fog_rock.photo_slideshow.core.webapi.GoogleSignInApi
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult
import org.fog_rock.photo_slideshow.core.webapi.entity.TokenInfo

class SplashInteractor(
    private val context: Context,
    private val googleSignInApi: GoogleSignInApi,
    private val googleOAuth2Api: GoogleOAuth2Api,
    private val appDatabase: AppDatabase,
    private val callback: SplashContract.InteractorCallback
): SplashContract.Interactor {

    override fun destroy() {
    }

    override fun requestGoogleSilentSignIn() {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.Default) {
                googleSignInApi.requestSilentSignIn()
            }
            callback.requestGoogleSilentSignInResult(result == ApiResult.SUCCEEDED)
        }
    }

    override fun requestUpdateUserInfo() {
        GlobalScope.launch(Dispatchers.Main) {
            val email = googleSignInApi.getSignedInEmailAddress()
            val serverAuthCode = googleSignInApi.getSignedInAccount().serverAuthCode
            val tokenInfo = requestTokenInfo(email, serverAuthCode)

            if (tokenInfo != null) {
                withContext(Dispatchers.Default) {
                    appDatabase.updateUserInfo(email, tokenInfo)
                }
                callback.requestUpdateUserInfoResult(true)
            } else {
                // 失敗した場合はGoogleアカウントアクセス破棄をする.
                withContext(Dispatchers.Default) {
                    googleSignInApi.requestRevokeAccess()
                }
                callback.requestUpdateUserInfoResult(false)
            }
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

    override fun isGoogleSignedIn(): Boolean = googleSignInApi.isSignedInAccount()

    override fun isSucceededGoogleUserSignIn(data: Intent?): Boolean = googleSignInApi.isSucceededUserSignIn(data)

    private suspend fun requestTokenInfo(emailAddress: String, serverAuthCode: String?): TokenInfo? {
        val tokenInfo = withContext(Dispatchers.Default) {
            val userInfo = appDatabase.findUserInfoByEmailAddress(emailAddress)
            if (userInfo != null) {
                googleOAuth2Api.requestTokenInfoWithRefreshToken(userInfo.tokenInfo().refreshToken)
            } else {
                null
            }
        }
        if (tokenInfo != null) return tokenInfo

        return withContext(Dispatchers.Default) {
            if (serverAuthCode != null) {
                googleOAuth2Api.requestTokenInfoWithAuthCode(serverAuthCode)
            } else {
                null
            }
        }
    }
}