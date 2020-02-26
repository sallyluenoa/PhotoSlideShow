package org.fog_rock.photo_slideshow.app.select.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_sharing_list.*
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.core.entity.AlbumData

class SharingListFragment : Fragment() {

    private val TAG = SharingListFragment::class.java.simpleName

    companion object {

        private const val ARGS_ALBUM_LIST = "album_list"

        fun newInstance(albumList: Array<AlbumData>): Fragment {
            val args = Bundle().apply {
                putSerializable(ARGS_ALBUM_LIST, albumList)
            }
            return SharingListFragment().apply {
                arguments = args
            }
        }
    }

    private val args: Bundle by lazy {
        arguments ?: run {
            Log.e(TAG, "Not found arguments.")
            Bundle()
        }
    }
    private val albumList: Array<AlbumData> by lazy {
        args.getSerializable(ARGS_ALBUM_LIST) as Array<AlbumData>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sharing_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumListAdapter = AlbumListAdapter(albumList,
            object : AlbumListAdapter.OnItemClickListener {
                override fun onItemClick(view: View, position: Int, album: AlbumData) {
                    Log.i(TAG, "onItemClick: ${position}, ${album.title}")
                }
            })
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = albumListAdapter
        }
    }
}