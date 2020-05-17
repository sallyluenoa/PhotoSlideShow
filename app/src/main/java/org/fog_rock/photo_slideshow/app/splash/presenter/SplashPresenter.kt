package org.fog_rock.photo_slideshow.app.splash.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.app.splash.interactor.SplashInteractor
import org.fog_rock.photo_slideshow.app.splash.router.SplashRouter
import org.fog_rock.photo_slideshow.core.webapi.entity.PhotoScope
import org.fog_rock.photo_slideshow.app.splash.entity.SignInRequest
import org.fog_rock.photo_slideshow.core.webapi.client.GoogleSignInClientHolder
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl

class SplashPresenter(
    private val context: Context,
    private val callback: SplashContract.PresenterCallback
): SplashContract.Presenter, SplashContract.InteractorCallback {

    private val TAG = SplashPresenter::class.java.simpleName

    private val clientHolder = GoogleSignInClientHolder(
        context, listOf(PhotoScope.READ_ONLY), requestIdToken = false, requestServerAuthCode = true)

    private val interactor: SplashContract.Interactor =
        SplashInteractor(context, GoogleSignInApiImpl(clientHolder), this)

    private val router: SplashContract.Router = SplashRouter()

    override fun destroy() {
        interactor.destroy()
    }

    override fun requestSignIn() {
        presentSequence(SignInRequest.RUNTIME_PERMISSIONS)
    }

    override fun evaluateActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(TAG, "evaluateActivityResult() " +
                "requestCode: $requestCode, resultCode: $resultCode")

        when (requestCode) {
            SignInRequest.GOOGLE_SIGN_IN.code -> {
                if (interactor.isSucceededGoogleUserSignIn(data)) {
                    Log.i(TAG, "Succeeded google sign in.")
                    presentSequence(SignInRequest.COMPLETED)
                } else {
                    Log.i(TAG, "Failed google sign in.")
                    callback.requestSignInResult(SignInRequest.GOOGLE_SIGN_IN)
                }
            }
            else -> {
                Log.e(TAG, "Unknown requestCode: $requestCode")
            }
        }
    }

    override fun evaluateRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.i(TAG, "evaluateRequestPermissionsResult() requestCode: $requestCode")

        when (requestCode) {
            SignInRequest.RUNTIME_PERMISSIONS.code -> {
                if (interactor.isGrantedRuntimePermissions(permissions)) {
                    Log.i(TAG, "Runtime permissions is granted.")
                    presentSequence(SignInRequest.GOOGLE_SIGN_IN)
                } else {
                    Log.i(TAG, "Runtime permissions is denied.")
                    callback.requestSignInResult(SignInRequest.RUNTIME_PERMISSIONS)
                }
            }
            else -> {
                Log.e(TAG, "Unknown requestCode: $requestCode")
            }
        }
    }

    override fun requestGoogleSilentSignInResult(isSucceeded: Boolean) {
        if (isSucceeded) {
            Log.i(TAG, "Succeeded silent sign in.")
            presentSequence(SignInRequest.COMPLETED)
        } else {
            Log.i(TAG, "Failed silent sign in. Might be signed out. Present user sign in.")
            router.startGoogleSignInActivity(activity(), clientHolder, SignInRequest.GOOGLE_SIGN_IN.code)
        }
    }

    private fun activity(): Activity = callback.getActivity()

    /**
     * リクエストに応じたサインインシーケンスを行う.
     * @param request サインインリクエスト
     */
    private fun presentSequence(request: SignInRequest) {
        if (request <= SignInRequest.RUNTIME_PERMISSIONS) {
            Log.i(TAG, "Check runtime permissions.")
            if (presentRuntimePermissions()) {
                Log.i(TAG, "Presented runtime permissions.")
                return
            }
        }
        if (request <= SignInRequest.GOOGLE_SIGN_IN) {
            Log.i(TAG, "Request google silent sign in.")
            interactor.requestGoogleSilentSignIn()
            return
        }
        presentMainActivity()
        Log.i(TAG, "Presented main activity.")
    }

    /**
     * ランタイムパーミッション許可の表示.
     * @return 許可確認を表示した場合はtrue, そうでない場合はfalse
     */
    private fun presentRuntimePermissions(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (interactor.isGrantedRuntimePermissions(permissions)) {
            Log.i(TAG, "Runtime permissions are granted.")
            return false
        }
        Log.i(TAG, "Request runtime permissions.")
        router.startRuntimePermissions(activity(), permissions, SignInRequest.RUNTIME_PERMISSIONS.code)
        return true
    }

    /**
     * メイン画面の表示.
     */
    private fun presentMainActivity() {
        router.startMainActivity(activity())
        callback.requestSignInResult(SignInRequest.COMPLETED)
    }
}