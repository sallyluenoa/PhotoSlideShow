package org.fog_rock.photo_slideshow.app.splash.presenter

import android.Manifest
import android.app.Activity
import android.content.Intent
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.app.splash.entity.SignInRequest
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.viper.ViperContract
import org.fog_rock.photo_slideshow.core.webapi.entity.ApiResult

class SplashPresenter(
    private var interactor: SplashContract.Interactor?,
    private var router: SplashContract.Router?
): SplashContract.Presenter, SplashContract.InteractorCallback {

    companion object {
        private val RUNTIME_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private var callback: SplashContract.PresenterCallback? = null

    override fun create(callback: ViperContract.PresenterCallback) {
        if (callback is SplashContract.PresenterCallback) {
            this.callback = callback
            interactor?.create(this)
        } else {
            IllegalArgumentException("SplashContract.PresenterCallback should be set.")
        }
    }

    override fun destroy() {
        interactor?.destroy()
        interactor = null
        router = null
        callback = null
    }

    override fun requestSignIn() {
        presentSequence(SignInRequest.RUNTIME_PERMISSIONS)
    }

    override fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        logI("evaluateActivityResult() " +
                "requestCode: $requestCode, resultCode: $resultCode")

        val request = SignInRequest.convertFromCode(requestCode)
        when (request) {
            SignInRequest.GOOGLE_SIGN_IN -> {
                if (interactor?.isSucceededGoogleUserSignIn(data) ?: return) {
                    logI("Succeeded google user sign in.")
                    presentSequence(request.next())
                } else {
                    logE( "Failed google user sign in.")
                    callback?.requestSignInResult(request)
                }
            }
            else -> {
                logE("Unknown requestCode: $requestCode")
                callback?.requestSignInResult(SignInRequest.UNKNOWN)
            }
        }
    }

    override fun evaluateRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        logI("evaluateRequestPermissionsResult() requestCode: $requestCode")

        val request = SignInRequest.convertFromCode(requestCode)
        when (request) {
            SignInRequest.RUNTIME_PERMISSIONS -> {
                if (interactor?.isGrantedRuntimePermissions((activity() ?: return), permissions) ?: return) {
                    logI("All runtime permissions are granted by user.")
                    presentSequence(request.next())
                } else {
                    logE("Runtime permissions are denied.")
                    callback?.requestSignInResult(request)
                }
            }
            else -> {
                logE("Unknown requestCode: $requestCode")
                callback?.requestSignInResult(SignInRequest.UNKNOWN)
            }
        }
    }

    override fun requestGoogleSilentSignInResult(result: ApiResult) {
        val request = SignInRequest.GOOGLE_SIGN_IN
        when (result) {
            ApiResult.SUCCEEDED -> {
                logI("Succeeded google silent sign in.")
                presentSequence(request.next())
            }
            ApiResult.INVALID -> {
                logI("Start GoogleSignInActivity.")
                router?.startGoogleSignInActivity((activity() ?: return), request.code)
            }
            else -> {
                logE( "Failed google silent sign in.")
                callback?.requestSignInResult(request)
            }
        }
    }

    override fun requestUpdateUserInfoResult(isSucceeded: Boolean) {
        val request = SignInRequest.UPDATE_USER_INFO
        if (isSucceeded) {
            logI("Succeeded to update user info.")
            presentSequence(request.next())
        } else {
            logE("Failed to update user info.")
            callback?.requestSignInResult(request)
        }
    }

    private fun activity(): Activity? = callback?.getActivity()

    /**
     * リクエストに応じたサインインシーケンスを行う.
     * @param request サインインリクエスト
     */
    private fun presentSequence(request: SignInRequest) {
        when (request) {
            SignInRequest.RUNTIME_PERMISSIONS -> {
                if (interactor?.isGrantedRuntimePermissions((activity() ?: return), RUNTIME_PERMISSIONS) ?: return) {
                    logI("All runtime permissions are granted.")
                    presentSequence(SignInRequest.GOOGLE_SIGN_IN)
                } else {
                    logI("Request runtime permissions.")
                    router?.startRuntimePermissions((activity() ?: return), RUNTIME_PERMISSIONS, request.code)
                }
            }
            SignInRequest.GOOGLE_SIGN_IN -> {
                logI("Request google silent sign in.")
                interactor?.requestGoogleSilentSignIn()
            }
            SignInRequest.UPDATE_USER_INFO -> {
                logI("Request update user info.")
                interactor?.requestUpdateUserInfo()
            }
            SignInRequest.COMPLETED -> {
                logI("All requests are completed. Start MainActivity.")
                router?.startMainActivity(activity() ?: return)
                callback?.requestSignInResult(request)
            }
            else -> {
                logE("Unknown request: $request")
                callback?.requestSignInResult(SignInRequest.UNKNOWN)
            }
        }
    }
}