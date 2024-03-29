package org.fog_rock.photo_slideshow.app.select.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.app.module.lib.impl.AppDatabaseImpl
import org.fog_rock.photo_slideshow.app.module.lib.impl.GoogleWebApisImpl
import org.fog_rock.photo_slideshow.app.module.ui.AppSimpleFragment
import org.fog_rock.photo_slideshow.app.module.ui.extension.replaceFragment
import org.fog_rock.photo_slideshow.app.select.contract.SelectContract
import org.fog_rock.photo_slideshow.app.select.entity.SelectAlbumsResult
import org.fog_rock.photo_slideshow.app.select.interactor.SelectInteractor
import org.fog_rock.photo_slideshow.app.select.presenter.SelectPresenter
import org.fog_rock.photo_slideshow.core.extension.putListExtra
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl
import org.fog_rock.photo_slideshow.databinding.ActivitySelectBinding

class SelectActivity : AppCompatActivity(), SelectContract.PresenterCallback {

    private lateinit var binding: ActivitySelectBinding

    private var presenter: SelectContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(AppSimpleFragment.newInstance(AppSimpleFragment.Layout.PROGRESS))

        presenter = SelectPresenter(SelectInteractor(
            AppDatabaseImpl(),
            GoogleWebApisImpl(this, GoogleSignInApiImpl(), GoogleOAuth2ApiImpl(), PhotosLibraryApiImpl()))
        )
        presenter?.create(this)
    }

    override fun createLoadResult(albums: List<Album>) {
        if (albums.isNotEmpty()) {
            replaceFragment(SharingAlbumsFragment.newInstance(albums))
        } else {
            finish()
        }
    }

    override fun getActivity(): Activity = this

    /**
     * アルバムを確定し、Activityを終了する
     * Fragmentから呼び出す想定なのでpublic定義にしている.
     */
    fun decidedAndFinishAlbum(album: Album) {
        // TODO: 今は１アルバム選択だが、複数選択を実装予定.
        val albums = arrayListOf(album)
        val intent = Intent().apply {
            putListExtra(SelectAlbumsResult.DECIDED_ALBUMS.key(), albums)
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}