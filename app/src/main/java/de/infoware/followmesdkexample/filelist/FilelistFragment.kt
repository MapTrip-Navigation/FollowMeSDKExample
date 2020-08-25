package de.infoware.followmesdkexample.filelist

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import de.infoware.followmesdkexample.MainActivity
import de.infoware.followmesdkexample.R
import de.infoware.followmesdkexample.filelist.adapter.FileListAdapter
import de.infoware.followmesdkexample.followme.data.FollowMeTour
import kotlinx.android.synthetic.main.filelist_fragment.*

class FilelistFragment : Fragment() {

    private val TAG = "FileListFragment"

    companion object {
        fun newInstance() = FilelistFragment()
    }


    private lateinit var adapter : FileListAdapter
    private lateinit var viewModel: FilelistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filelist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(FilelistViewModel::class.java)

        rvFileList.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        adapter = FileListAdapter(listOf())
        rvFileList.adapter = adapter

        initListener()
    }

    fun initListener() {
        val availableFollowMeFilesObserver = Observer<List<FollowMeTour>> { availableFollowMeFiles ->
            adapter.setList(availableFollowMeFiles)
        }

        viewModel.availableFollowMeFiles.observe(this.viewLifecycleOwner, availableFollowMeFilesObserver)

        val selectedFollowMeFileObserver = Observer<FollowMeTour> { selectedFile ->
            AlertDialog.Builder(this.context)
                .setTitle("Start Navigation?")
                .setMessage("Do you want to start the navigation from File ${selectedFile.file.nameWithoutExtension}?")
                .setPositiveButton("Start Guidence", DialogInterface.OnClickListener {
                        dialog, which ->
                    setFragmentResult("selectedFile", bundleOf("selectedFileBundle" to selectedFile.file.nameWithoutExtension))
                    (activity as MainActivity).switchToCompanionMapFragment()

                })
                .setNeutralButton("Cancel", null)
                .show()
        }

        adapter.followMeTourObservable.observe(this.viewLifecycleOwner, selectedFollowMeFileObserver)
    }

}