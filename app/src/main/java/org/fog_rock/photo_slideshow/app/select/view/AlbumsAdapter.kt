package org.fog_rock.photo_slideshow.app.select.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.photos.types.proto.Album
import org.fog_rock.photo_slideshow.R

class AlbumsAdapter(
    private val albums: List<Album>,
    private val listener: OnItemClickListener
): RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder>() {

    interface OnItemClickListener {

        fun onItemClick(view: View, position: Int, album: Album)
    }

    class AlbumsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleView = view.findViewById<TextView?>(R.id.textView_title)
        val itemCountView = view.findViewById<TextView?>(R.id.textView_item_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumsViewHolder =
        AlbumsViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_album, parent, false))

    override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) {
        val album = albums[position]
        holder.titleView?.text = album.title
        holder.itemCountView?.text = album.mediaItemsCount.toString()
        holder.itemView.setOnClickListener {
            it.isSelected = true
            listener.onItemClick(it, position, album)
        }
    }

    override fun getItemCount(): Int = albums.size
}