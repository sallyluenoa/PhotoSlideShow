package org.fog_rock.photo_slideshow.app.splash.presenter

import android.Manifest
import android.app.Activity
import android.content.Intent
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.app.splash.entity.SignInRequest
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI

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

    override fun create(callback: SplashContract.PresenterCallback) {
        this.callback = callback
        interactor?.create(this)
    }

    override fun destroy() {
        interactor?.destroy()
        callback = null
    }

    override fun requestSignIn() = presentSequence(SignInRequest.RUNTIME_PERMISSIONS)

    override fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        logI("evaluateActivityResult() " +
                "requestCode: $requestCode, resultCode: $resultCode")

        when (requestCode) {
            SignInRequest.GOOGLE_SIGN_IN.code -> {
                val isSucceeded = interactor?.isSucceededGoogleUserSignIn(data) ?: return
                presentSequenceResult(SignInRequest.GOOGLE_SIGN_IN, isSucceeded)
            }
            else -> {
                logE("Unknown requestCode: $requestCode")
                callback?.requestSignInResult(SignInRequest.UNKNOWN)
            }
        }
    }

    override fun evaluateRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        logI("evaluateRequestPermissionsResult() requestCode: $requestCode")

        when (requestCode) {
            SignInRequest.RUNTIME_PERMISSIONS.code -> {
                val isSucceeded = interactor?.isGrantedRuntimePermissions(permissions) ?: return
                presentSequenceResult(SignInRequest.RUNTIME_PERMISSIONS, isSucceeded)
            }
            else -> {
                logE("Unknown requestCode: $requestCode")
                callback?.requestSignInResult(SignInRequest.UNKNOWN)
            }
        }
    }

    override fun requestGoogleSilentSignInResult(isSucceeded: Boolean) =
        presentSequenceResult(SignInRequest.GOOGLE_SIGN_IN, isSucceeded)

    override fun requestUpdateUserInfoResult(isSucceeded: Boolean) =
        presentSequenceResult(SignInRequest.UPDATE_USER_INFO, isSucceeded)

    private fun activity(): Activity? = callback?.getActivity()

    /**
     * リクエストに応じたサインインシーケンスを行う.
     * @param request サインインリクエスト
     */
    private fun presentSequence(request: SignInRequest) {
        when (request) {
            SignInRequest.RUNTIME_PERMISSIONS -> presentRuntimePermissions()
            SignInRequest.GOOGLE_SIGN_IN -> presentGoogleSignIn()
            SignInRequest.UPDATE_USER_INFO -> presentUpdateUserInfo()
            SignInRequest.COMPLETED -> presentMainActivity()
            else -> callback?.requestSignInResult(SignInRequest.UNKNOWN)
        }
    }

    /**
     * リクエストに応じたサインインシーケンスの結果を処理する.
     * @param request サインインリクエスト
     * @param isSucceeded リクエストの処理に成功したか
     */
    private fun presentSequenceResult(request: SignInRequest, isSucceeded: Boolean) {
        if (isSucceeded) {
            logI("Succeeded to request: $request")
            presentSequence(request.next())
        } else {
            logI( "Failed to request: $request")
            callback?.requestSignInResult(request)
        }
    }

    private fun presentRuntimePermissions() {
        if (interactor?.isGrantedRuntimePermissions(RUNTIME_PERMISSIONS) ?: return) {
            logI("All permissions are granted.")
            presentSequence(SignInRequest.GOOGLE_SIGN_IN)
        } else {
            logI("Request runtime permissions.")
            router?.startRuntimePermissions(
                (activity() ?: return), RUNTIME_PERMISSIONS, SignInRequest.RUNTIME_PERMISSIONS.code)
        }
    }

    private fun presentGoogleSignIn() {
        if (interactor?.isGoogleSignedIn() ?: return) {
            logI("Request google silent sign in.")
            interactor?.requestGoogleSilentSignIn()
        } else {
            logI("Request google user sign in.")
            router?.startGoogleSignInActivity((activity() ?: return), SignInRequest.GOOGLE_SIGN_IN.code)
        }
    }

    private fun presentUpdateUserInfo() {
        logI("Request update user info.")
        interactor?.requestUpdateUserInfo()
    }

    private fun presentMainActivity() {
        logI("Start MainActivity.")
        router?.startMainActivity((activity() ?: return))
        callback?.requestSignInResult(SignInRequest.COMPLETED)
    }
}