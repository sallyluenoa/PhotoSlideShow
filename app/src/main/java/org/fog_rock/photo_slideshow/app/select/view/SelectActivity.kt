package org.fog_rock.photo_slideshow.app.select.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_select.*
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.core.entity.AlbumData

class SelectActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_select)
        replaceFragment(SharingListFragment.newInstance(
            arrayOf( // TODO: dummy
                AlbumData("0", "sample0", "https://", 0),
                AlbumData("1", "sample1", "https://", 10),
                AlbumData("2", "sample2", "https://", 20),
                AlbumData("3", "sample3", "https://", 30),
                AlbumData("4", "sample4", "https://", 40),
                AlbumData("5", "sample5", "https://", 50),
                AlbumData("6", "sample6", "https://", 60),
                AlbumData("7", "sample7", "https://", 70),
                AlbumData("8", "sample8", "https://", 80)
            )
        ))

        button_ok.setOnClickListener {
            finish()
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
}