package de.infoware.followmesdkexample.filelist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import de.infoware.followmesdkexample.databinding.LayoutListitemFilelistBinding
import de.infoware.followmesdkexample.followme.data.FollowMeTour

/**
 *  Adapter for the RecyclerView responsible for displaying the available FollowMeTour Files
 */
class FileListAdapter(private var files : List<FollowMeTour>) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    private var _binding: LayoutListitemFilelistBinding? = null
    private val binding get() = _binding!!

    // LiveData for the selected FollowMeTour
    val followMeTourObservable: MutableLiveData<FollowMeTour> by lazy {
        MutableLiveData<FollowMeTour>()
    }

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = LayoutListitemFilelistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = ViewHolder(binding.root)

        // item click-listener to post the selected FollowMeTour
        binding.tvFileName.setOnClickListener { followMeTourObservable.postValue(files[holder.adapterPosition]) }

        return holder
    }

    override fun getItemCount() = files.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]

        binding.tvFileName.text = file.fileName
    }

    /**
     *  Method to set a new list to display
     *  @param list a List of FollowMeTour (data class) elements
     */
    fun setList(list: List<FollowMeTour>) {
        this.files = list
        notifyDataSetChanged()
    }
}