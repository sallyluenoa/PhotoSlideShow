package org.fog_rock.photo_slideshow.app.select.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.R

class SelectActivity : AppCompatActivity() {

    private val TAG = SelectActivity::class.java.simpleName

    companion object {

        const val REQUEST_ALBUMS = "albums"
        const val RESULT_DECIDE_ALBUM = "decide_album"
    }

    private val fragmentManager = supportFragmentManager

    private val albums: Array<Album> by lazy {
        intent.getSerializableExtra(REQUEST_ALBUMS) as Array<Album>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select)
        replaceFragment(SharingAlbumsFragment.newInstance(albums))
    }

    /**
     * アルバムを確定し、Activityを終了する
     * Fragmentから呼び出す想定なのでpublic定義にしている.
     */
    fun decidedAndFinishAlbum(album: Album) {
        val intent = Intent().apply {
            putExtra(RESULT_DECIDE_ALBUM, album)
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