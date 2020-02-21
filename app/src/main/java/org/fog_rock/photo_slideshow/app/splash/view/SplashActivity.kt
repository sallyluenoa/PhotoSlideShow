package org.fog_rock.photo_slideshow.app.splash.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.splash.contract.SplashContract
import org.fog_rock.photo_slideshow.app.splash.presenter.SplashPresenter

class SplashActivity : AppCompatActivity(), SplashContract.View {

    private val TAG = SplashActivity::class.java.simpleName

    private val fragmentManager = supportFragmentManager

    private var presenter: SplashContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        replaceFragment(LogoFragment.newInstance())

        presenter = SplashPresenter()

        Handler().postDelayed({
            Log.d(TAG, "postDelayed 2 sec.")

            replaceFragment(SignInFragment.newInstance())

        }, 2000)
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        presenter = null

        super.onDestroy()
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
