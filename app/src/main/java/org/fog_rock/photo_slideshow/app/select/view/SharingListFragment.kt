package org.fog_rock.photo_slideshow.app.select.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.photos.types.proto.Album
import kotlinx.android.synthetic.main.fragment_sharing_list.*
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.AppDialogFragment

class SharingListFragment : Fragment(), AppDialogFragment.Callback, AlbumListAdapter.OnItemClickListener {

    private val TAG = SharingListFragment::class.java.simpleName

    private val CODE_CONFIRM_SELECT = 1000

    companion object {

        private const val ARGS_ALBUM_LIST = "album_list"

        fun newInstance(albumList: Array<Album>): Fragment {
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
    private val albumList: Array<Album> by lazy {
        args.getSerializable(ARGS_ALBUM_LIST) as Array<Album>
    }

    private var selectedView: View? = null
    private var selectedAlbum: Album? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sharing_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumListAdapter = AlbumListAdapter(albumList, this)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = albumListAdapter
        }
    }

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.i(TAG, "onDialogResult() requestCode: $requestCode, resultCode: $resultCode")

        when (requestCode) {
            CODE_CONFIRM_SELECT -> {
                selectedView?.setSelected(false)
                selectedView = null

                if (resultCode == AppDialogFragment.BUTTON_POSITIVE) {
                    val album = selectedAlbum ?: run {
                        Log.e(TAG, "There are no selected albums.")
                        return
                    }
                    val activity = requireActivity()
                    if (activity is SelectActivity) {
                        activity.decidedAndFinishAlbum(album)
                    } else {
                        Log.e(TAG, "Activity is not SelectActivity.")
                    }
                }
                selectedAlbum = null
            }
            else -> {
                Log.e(TAG, "Unknown requestCode: $requestCode")
            }
        }
    }

    override fun onItemClick(view: View, position: Int, album: Album) {
        Log.i(TAG, "Item clicked. position: $position")
        selectedView = view
        selectedAlbum = album

        AppDialogFragment.Builder(requireContext()).apply {
            setTitle(R.string.confirm_select_album_title)
            setMessage(getString(R.string.confirm_select_album_message, album.title))
            setPositiveLabel(R.string.start)
            setNegativeLabel(R.string.cancel)
            setCancelable(false)
        }.show(childFragmentManager, CODE_CONFIRM_SELECT)
    }
}