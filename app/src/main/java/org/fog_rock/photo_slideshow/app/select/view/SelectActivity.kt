package org.fog_rock.photo_slideshow.app.select.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.AppDatabase
import org.fog_rock.photo_slideshow.app.module.AppSimpleFragment
import org.fog_rock.photo_slideshow.app.module.GoogleWebApis
import org.fog_rock.photo_slideshow.app.select.contract.SelectContract
import org.fog_rock.photo_slideshow.app.select.entity.SelectAlbumsResult
import org.fog_rock.photo_slideshow.app.select.interactor.SelectInteractor
import org.fog_rock.photo_slideshow.app.select.presenter.SelectPresenter
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleOAuth2ApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.GoogleSignInApiImpl
import org.fog_rock.photo_slideshow.core.webapi.impl.PhotosLibraryApiImpl

class SelectActivity : AppCompatActivity(), SelectContract.PresenterCallback {

    companion object {
        const val RESULT_DECIDE_ALBUMS = "decided_albums"
    }

    private val fragmentManager = supportFragmentManager

    private var presenter: SelectContract.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select)
        replaceFragment(AppSimpleFragment.newInstance(AppSimpleFragment.Layout.PROGRESS))

        presenter = SelectPresenter(SelectInteractor(AppDatabase(),
            GoogleWebApis(GoogleSignInApiImpl(this), GoogleOAuth2ApiImpl(), PhotosLibraryApiImpl()))
        )
        presenter?.create(this)

        presenter?.requestLoadSharedAlbums()
    }

    override fun requestLoadSharedAlbumsResult(albums: List<Album>) {
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
            putExtra(SelectAlbumsResult.DECIDED_ALBUMS.key(), albums)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    /**
     * 新しいフラグメントに置換する.
     */
    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction().apply {
            replace(R.id.fragment_container, fragment)
        }.commit()
    }
}