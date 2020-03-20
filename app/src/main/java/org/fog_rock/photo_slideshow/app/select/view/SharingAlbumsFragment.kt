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
import kotlinx.android.synthetic.main.fragment_sharing_albums.*
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.AppDialogFragment

class SharingAlbumsFragment : Fragment(), AppDialogFragment.Callback, AlbumsAdapter.OnItemClickListener {

    private val TAG = SharingAlbumsFragment::class.java.simpleName

    private val CODE_CONFIRM_SELECT = 1000

    companion object {

        private const val ARGS_ALBUMS = "albums"

        fun newInstance(albums: Array<Album>): Fragment {
            val args = Bundle().apply {
                putSerializable(ARGS_ALBUMS, albums)
            }
            return SharingAlbumsFragment().apply {
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
    private val albums: Array<Album> by lazy {
        args.getSerializable(ARGS_ALBUMS) as Array<Album>
    }

    private var selectedView: View? = null
    private var selectedAlbum: Album? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_sharing_albums, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumsAdapter = AlbumsAdapter(albums, this)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = albumsAdapter
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