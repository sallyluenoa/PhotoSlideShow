package org.fog_rock.photo_slideshow.app.splash.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.AppDialogFragment
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.app.splash.presenter.SplashPresenter
import org.fog_rock.photo_slideshow.core.entity.SignInRequest

class SplashActivity : AppCompatActivity(), SplashContract.View, SplashContract.PresenterCallback, AppDialogFragment.Callback {

    private val TAG = SplashActivity::class.java.simpleName

    private val fragmentManager = supportFragmentManager

    private lateinit var presenter: SplashContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        replaceFragment(LogoFragment.newInstance())

        presenter = SplashPresenter(this, this)

        Handler().postDelayed({
            Log.d(TAG, "postDelayed 2 sec.")

            replaceFragment(SignInFragment.newInstance())
            presenter.requestSignIn()
        }, 2000)
    }

    override fun onDestroy() {
        presenter.destroy()

        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        presenter.evaluateActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        presenter.evaluateRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        finish()
    }

    override fun succeededSignIn() {
        finish()
    }

    override fun failedSignIn(request: SignInRequest) {
        AppDialogFragment.Builder(this).apply {
            setTitle(request.failedTitle)
            setMessage(request.failedMessage)
            setPositiveLabel(R.string.ok)
            setCancelable(false)
        }.show(fragmentManager, request.code)
    }

    /**
     * 新しいフラグメントに置換する.
     */
    private fun replaceFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}
