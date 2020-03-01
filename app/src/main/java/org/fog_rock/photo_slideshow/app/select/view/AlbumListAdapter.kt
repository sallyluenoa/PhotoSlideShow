package org.fog_rock.photo_slideshow.app.select.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.R

class AlbumListAdapter(
    private val albumList: Array<Album>,
    private val listener: OnItemClickListener
): RecyclerView.Adapter<AlbumListAdapter.AlbumListViewHolder>() {

    interface OnItemClickListener {

        fun onItemClick(view: View, position: Int, album: Album)
    }

    class AlbumListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleView = view.findViewById<TextView?>(R.id.textView_title)
        val itemCountView = view.findViewById<TextView?>(R.id.textView_item_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumListViewHolder =
        AlbumListViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_album, parent, false))

    override fun onBindViewHolder(holder: AlbumListViewHolder, position: Int) {
        val album = albumList[position]
        holder.titleView?.text = album.title
        holder.itemCountView?.text = album.mediaItemsCount.toString()
        holder.itemView.setOnClickListener {
            it.setSelected(true)
            listener.onItemClick(it, position, album)
        }
    }

    override fun getItemCount(): Int = albumList.size
}