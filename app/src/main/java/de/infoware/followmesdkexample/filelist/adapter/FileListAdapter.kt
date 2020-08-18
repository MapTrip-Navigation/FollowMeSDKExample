package de.infoware.followmesdkexample.filelist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.infoware.followmesdkexample.R
import kotlinx.android.synthetic.main.layout_listitem_filelist.view.*

class FileListAdapter(private var files : List<String>) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_listitem_filelist, parent, false)
        val holder = ViewHolder(v)

        return holder
    }

    override fun getItemCount() = files.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]

        holder.itemView.tvFileName.text = file
    }

    fun setList(list: List<String>) {
        this.files = list
        notifyDataSetChanged()
    }
}