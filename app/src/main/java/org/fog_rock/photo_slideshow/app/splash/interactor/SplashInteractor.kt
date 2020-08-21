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
import org.fog_rock.photo_slideshow.app.module.GoogleWebApis
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

class SplashInteractor(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val googleWebApis: GoogleWebApis
): SplashContract.Interactor {

    private var callback: SplashContract.InteractorCallback? = null

    override fun create(callback: ViperContract.InteractorCallback) {
        if (callback is SplashContract.InteractorCallback) {
            this.callback = callback
        } else {
            IllegalArgumentException("SplashContract.InteractorCallback should be set.")
        }
    }

    override fun destroy() {
        callback = null
    }

    override fun requestGoogleSilentSignIn() {
        GlobalScope.launch(Dispatchers.Default) {
            val result = googleWebApis.requestSilentSignIn()
            requestGoogleSilentSignInResult(result)
        }
    }

    override fun requestUpdateUserInfo() {
        GlobalScope.launch(Dispatchers.Default) {
            val emailAddress = googleWebApis.getSignedInEmailAddress()
            val userInfo = appDatabase.findUserInfoByEmailAddress(emailAddress)
            val tokenInfo = googleWebApis.requestUpdateTokenInfo(userInfo?.tokenInfo())

            if (tokenInfo != null) {
                appDatabase.updateUserInfo(emailAddress, tokenInfo)
                requestUpdateUserInfoResult(true)
            } else {
                // 失敗した場合はGoogleアカウントアクセス破棄をする.
                googleWebApis.requestSignOut(true)
                requestUpdateUserInfoResult(false)
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

    override fun isSucceededGoogleUserSignIn(data: Intent?): Boolean =
        googleWebApis.isSucceededUserSignIn(data)

    private suspend fun requestGoogleSilentSignInResult(result: ApiResult) {
        withContext(Dispatchers.Main) {
            callback?.requestGoogleSilentSignInResult(result)
        }
    }

    private suspend fun requestUpdateUserInfoResult(isSucceeded: Boolean) {
        withContext(Dispatchers.Main) {
            callback?.requestUpdateUserInfoResult(isSucceeded)
        }
    }
}