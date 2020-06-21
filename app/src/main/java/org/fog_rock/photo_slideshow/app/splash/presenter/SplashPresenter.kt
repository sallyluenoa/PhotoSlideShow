package org.fog_rock.photo_slideshow.app.splash.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.app.splash.entity.SignInRequest
import org.fog_rock.photo_slideshow.app.splash.interactor.SplashInteractor
import org.fog_rock.photo_slideshow.app.splash.router.SplashRouter
import org.fog_rock.photo_slideshow.core.database.impl.UserInfoDatabaseImpl
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.file.impl.AssetsFileReaderImpl
import org.fog_rock.photo_slideshow.core.webapi.client.GoogleSignInClientHolder
import org.fog_rock.photo_slideshow.core.webapi.entity.PhotoScope
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl

class SplashPresenter(
    context: Context,
    private val callback: SplashContract.PresenterCallback
): SplashContract.Presenter, SplashContract.InteractorCallback {

    companion object {
        private val RUNTIME_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private val clientHolder = GoogleSignInClientHolder(
        context, listOf(PhotoScope.READ_ONLY), requestIdToken = false, requestServerAuthCode = true)

    private val interactor: SplashContract.Interactor = SplashInteractor(
        context,
        GoogleSignInApiImpl(clientHolder),
        GoogleOAuth2ApiImpl(AssetsFileReaderImpl(context)),
        UserInfoDatabaseImpl(context),
        this)

    private val router: SplashContract.Router = SplashRouter()

    override fun destroy() {
        interactor.destroy()
    }

    override fun requestSignIn() = presentSequence(SignInRequest.RUNTIME_PERMISSIONS)

    override fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        logI("evaluateActivityResult() " +
                "requestCode: $requestCode, resultCode: $resultCode")

        when (requestCode) {
            SignInRequest.GOOGLE_SIGN_IN.code -> {
                val isSucceeded = interactor.isSucceededGoogleUserSignIn(data)
                presentSequenceResult(SignInRequest.GOOGLE_SIGN_IN, isSucceeded)
            }
            else -> {
                logE("Unknown requestCode: $requestCode")
                callback.requestSignInResult(SignInRequest.UNKNOWN)
            }
        }
    }

    override fun evaluateRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        logI("evaluateRequestPermissionsResult() requestCode: $requestCode")

        when (requestCode) {
            SignInRequest.RUNTIME_PERMISSIONS.code -> {
                val isSucceeded = interactor.isGrantedRuntimePermissions(permissions)
                presentSequenceResult(SignInRequest.RUNTIME_PERMISSIONS, isSucceeded)
            }
            else -> {
                logE("Unknown requestCode: $requestCode")
                callback.requestSignInResult(SignInRequest.UNKNOWN)
            }
        }
    }

    override fun requestGoogleSilentSignInResult(isSucceeded: Boolean) =
        presentSequenceResult(SignInRequest.GOOGLE_SIGN_IN, isSucceeded)

    override fun requestUpdateUserInfoResult(isSucceeded: Boolean) =
        presentSequenceResult(SignInRequest.UPDATE_USER_INFO, isSucceeded)

    private fun activity(): Activity = callback.getActivity()

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
            else -> callback.requestSignInResult(SignInRequest.UNKNOWN)
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
            callback.requestSignInResult(request)
        }
    }

    private fun presentRuntimePermissions() {
        if (interactor.isGrantedRuntimePermissions(RUNTIME_PERMISSIONS)) {
            logI("All permissions are granted.")
            presentSequence(SignInRequest.GOOGLE_SIGN_IN)
        } else {
            logI("Request runtime permissions.")
            router.startRuntimePermissions(activity(), RUNTIME_PERMISSIONS, SignInRequest.RUNTIME_PERMISSIONS.code)
        }
    }

    private fun presentGoogleSignIn() {
        if (interactor.isGoogleSignedIn()) {
            logI("Request google silent sign in.")
            interactor.requestGoogleSilentSignIn()
        } else {
            logI("Request google user sign in.")
            router.startGoogleSignInActivity(activity(), clientHolder, SignInRequest.GOOGLE_SIGN_IN.code)
        }
    }

    private fun presentUpdateUserInfo() {
        logI("Request update user info.")
        interactor.requestUpdateUserInfo()
    }

    private fun presentMainActivity() {
        logI("Start MainActivity.")
        router.startMainActivity(activity())
        callback.requestSignInResult(SignInRequest.COMPLETED)
    }
}