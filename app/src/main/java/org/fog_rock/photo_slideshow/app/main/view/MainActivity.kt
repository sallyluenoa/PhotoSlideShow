package org.fog_rock.photo_slideshow.app.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.main.contract.MainContract
import org.fog_rock.photo_slideshow.app.main.presenter.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.PresenterCallback {

    private val TAG = MainActivity::class.java.simpleName

    private lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        presenter = MainPresenter(this)

        val account = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (account != null) {
            Log.i(TAG, "idToken: ${account.idToken}\nserverAuthCode: ${account.serverAuthCode}")
        }
        presenter.requestAlbums()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        presenter.evaluateActivityResult(requestCode, resultCode, data)
    }

    override fun getActivity(): Activity = this

}