package org.fog_rock.photo_slideshow.app.select.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.app.module.ui.AppDialogFragment
import org.fog_rock.photo_slideshow.core.extension.getListExtra
import org.fog_rock.photo_slideshow.core.extension.logE
import org.fog_rock.photo_slideshow.core.extension.logI
import org.fog_rock.photo_slideshow.core.extension.putListExtra
import org.fog_rock.photo_slideshow.databinding.FragmentSharingAlbumsBinding

class SharingAlbumsFragment : Fragment(), AppDialogFragment.Callback, AlbumsAdapter.OnItemClickListener {

    companion object {

        private const val CODE_CONFIRM_SELECT = 1000
        private const val ARGS_ALBUMS = "albums"

        fun newInstance(albums: List<Album>): Fragment = SharingAlbumsFragment().apply {
            arguments = Bundle().apply {
                putListExtra(ARGS_ALBUMS, albums)
            }
        }
    }

    private val args: Bundle by lazy {
        arguments ?: run {
            logE("Not found arguments.")
            Bundle()
        }
    }
    private val albums: List<Album> by lazy {
        args.getListExtra(ARGS_ALBUMS) ?: emptyList()
    }

    private var _binding: FragmentSharingAlbumsBinding? = null
    private val binding get() = _binding!!

    private var selectedView: View? = null
    private var selectedAlbum: Album? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSharingAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val albumsAdapter = AlbumsAdapter(albums, this)
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = albumsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent) {
        logI("onDialogResult() requestCode: $requestCode, resultCode: $resultCode")

        when (requestCode) {
            CODE_CONFIRM_SELECT -> {
                selectedView?.setSelected(false)
                selectedView = null

                if (resultCode == AppDialogFragment.BUTTON_POSITIVE) {
                    val album = selectedAlbum ?: run {
                        logE("There are no selected albums.")
                        return
                    }
                    val activity = requireActivity()
                    if (activity is SelectActivity) {
                        activity.decidedAndFinishAlbum(album)
                    } else {
                        logE("Activity is not SelectActivity.")
                    }
                }
                selectedAlbum = null
            }
            else -> {
                logE("Unknown requestCode: $requestCode")
            }
        }
    }

    override fun onItemClick(view: View, position: Int, album: Album) {
        logI("Item clicked. position: $position")
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