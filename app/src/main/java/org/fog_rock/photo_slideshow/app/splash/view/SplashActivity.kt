package org.fog_rock.photo_slideshow.app.splash.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.AppDatabase
import org.fog_rock.photo_slideshow.app.module.AppDialogFragment
import org.fog_rock.photo_slideshow.app.module.AppSimpleFragment
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.app.splash.entity.SignInRequest
import org.fog_rock.photo_slideshow.app.splash.interactor.SplashInteractor
import org.fog_rock.photo_slideshow.app.splash.presenter.SplashPresenter
import org.fog_rock.photo_slideshow.app.splash.router.SplashRouter
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl

class SplashActivity : AppCompatActivity(), SplashContract.PresenterCallback, AppDialogFragment.Callback {

    companion object {
        private const val SHOW_LOGO_TIME_MILLIS = 2000L
    }

    private val fragmentManager = supportFragmentManager

    private var presenter: SplashContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        replaceFragment(AppSimpleFragment.newInstance(AppSimpleFragment.Layout.LOGO))

        presenter = SplashPresenter(
            SplashInteractor(this, AppDatabase(), GoogleSignInApiImpl(this), GoogleOAuth2ApiImpl()),
            SplashRouter()
        )
        presenter?.create(this)

        Handler().postDelayed({
            replaceFragment(AppSimpleFragment.newInstance(AppSimpleFragment.Layout.EMPTY))
            requestSignIn()
        }, SHOW_LOGO_TIME_MILLIS)
    }

    override fun onDestroy() {
        presenter?.destroy()
        presenter = null

        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        presenter?.evaluateActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        presenter?.evaluateRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == AppDialogFragment.BUTTON_POSITIVE) {
            requestSignIn()
        } else {
            finish()
        }
    }

    override fun getActivity(): Activity = this

    override fun requestSignInResult(request: SignInRequest) {
        logI("requestSignInResult: $request")

        if (request == SignInRequest.COMPLETED) {
            finish()
        } else {
            AppDialogFragment.Builder(this).apply {
                setTitle(request.failedTitle)
                setMessage(request.failedMessage)
                setPositiveLabel(R.string.retry)
                setNegativeLabel(R.string.cancel)
                setCancelable(false)
            }.show(fragmentManager, request.code)
        }
    }

    /**
     * 新しいフラグメントに置換する.
     */
    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
        }.commit()
    }

    /**
     * サインインを要求する.
     */
    private fun requestSignIn() {
        logI("Request sign in.")
        presenter?.requestSignIn()
    }
}