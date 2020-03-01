package org.fog_rock.photo_slideshow.app.select.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.R

class SelectActivity : AppCompatActivity() {

    private val TAG = SelectActivity::class.java.simpleName

    private val fragmentManager = supportFragmentManager

    private val albumList: Array<Album> by lazy {
        intent.getSerializableExtra("album_list") as Array<Album>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select)
        replaceFragment(SharingListFragment.newInstance(albumList))
    }

    /**
     * アルバムを確定し、Activityを終了する
     * Fragmentから呼び出す想定なのでpublic定義にしている.
     */
    fun decidedAndFinishAlbum(album: Album) {
        val intent = Intent().apply {
            putExtra("decided_album", album)
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