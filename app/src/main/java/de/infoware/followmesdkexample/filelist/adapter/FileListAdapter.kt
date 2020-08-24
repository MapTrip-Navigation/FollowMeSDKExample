package de.infoware.followmesdkexample.filelist.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import de.infoware.followmesdkexample.R
import de.infoware.followmesdkexample.followme.data.FollowMeTour
import kotlinx.android.synthetic.main.layout_listitem_filelist.view.*

class FileListAdapter(private var files : List<FollowMeTour>) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    val followMeTourObservable: MutableLiveData<FollowMeTour> by lazy {
        MutableLiveData<FollowMeTour>()
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_listitem_filelist, parent, false)
        val holder = ViewHolder(v)

        holder.itemView.tvFileName.setOnClickListener { followMeTourObservable.postValue(files[holder.adapterPosition]) }

        return holder
    }

    override fun getItemCount() = files.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]

        holder.itemView.tvFileName.text = file.file.nameWithoutExtension
    }

    fun setList(list: List<FollowMeTour>) {
        this.files = list
        notifyDataSetChanged()
    }
}