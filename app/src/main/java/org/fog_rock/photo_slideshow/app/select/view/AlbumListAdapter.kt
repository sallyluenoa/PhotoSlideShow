package org.fog_rock.photo_slideshow.app.select.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.fog_rock.photo_slideshow.R
import org.fog_rock.photo_slideshow.core.entity.AlbumData

class AlbumListAdapter(
    private val albumList: Array<AlbumData>,
    private val listener: OnItemClickListener
): RecyclerView.Adapter<AlbumListAdapter.AlbumListViewHolder>() {

    interface OnItemClickListener {

        fun onItemClick(view: View, position: Int, album: AlbumData)
    }

    class AlbumListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleView = view.findViewById<TextView?>(R.id.textView_title)
        val itemCountView = view.findViewById<TextView?>(R.id.textView_item_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumListViewHolder =
        AlbumListViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_album, parent, false))

    override fun onBindViewHolder(holder: AlbumListViewHolder, position: Int) {
        holder.titleView?.text = albumList[position].title
        holder.itemCountView?.text = albumList[position].mediaItemCount.toString()
        holder.itemView.setOnClickListener {
            it.setSelected(true)
            listener.onItemClick(it, position, albumList[position])
        }
    }

    override fun getItemCount(): Int = albumList.size
}