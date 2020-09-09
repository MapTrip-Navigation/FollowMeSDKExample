package de.infoware.followmesdkexample.filelist

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

/**
 *  Fragment for the file selection screen
 *  Manages the RecyclerView and the 'Start Tour'-Dialog
 */
class FileListFragment : Fragment() {

    private val TAG = "FileListFragment"

    companion object {
        fun newInstance() = FileListFragment()
    }

    // The FileListAdapter for the RecyclerView
    private lateinit var adapter : FileListAdapter

    // The ViewModel used in this Fragment
    private lateinit var viewModel: FilelistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filelist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // the ViewModelProvider provides a new instance of the ViewModel if there is none, and uses the existing instance of the ViewModel if possible
        viewModel = ViewModelProvider(requireActivity()).get(FilelistViewModel::class.java)

        rvFileList.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        // Creates the FileListAdapter with an empty list
        adapter = FileListAdapter(listOf())
        rvFileList.adapter = adapter

        initListener()
    }

    private fun initListener() {

        /**
         *  Observer for the loaded FollowMeFiles.
         *  When the FileListRepo is finished, the ViewModel sends the list via the Observer to the Fragment
         *  When the list is empty, a Toast gets shown and the Adapter does not get updated
         */
        val availableFollowMeFilesObserver = Observer<List<FollowMeTour>> { availableFollowMeFiles ->
            if(availableFollowMeFiles.isEmpty()) {
                this.showNoFilesFoundToast()
            } else {
                adapter.setList(availableFollowMeFiles)
            }
        }
        viewModel.availableFollowMeFiles.observe(this.viewLifecycleOwner, availableFollowMeFilesObserver)

        /**
         *  Observer for the selected FollowMeFile. Gets triggered when the user clicks on one of the List-items
         */
        val selectedFollowMeFileObserver = Observer<FollowMeTour> { selectedFile ->
            openStartNavigationDialog(selectedFile)
        }

        adapter.followMeTourObservable.observe(this.viewLifecycleOwner, selectedFollowMeFileObserver)
    }

    /**
     *  Shows a Toast that no files were found in the /user/routes/directory
     */
    private fun showNoFilesFoundToast() {
        Toast.makeText(requireActivity(), "No files found in directory /user/routes", Toast.LENGTH_LONG).show()
    }

    /**
     *  Starts an AlertDialog to Ask if the Navigation should be started, and if it should be started as an simulation
     *  @param selectedFile the selected FollowMeTour
     */
    private fun openStartNavigationDialog(selectedFile: FollowMeTour) {
        AlertDialog.Builder(this.context)
            .setTitle("Start Navigation?")
            .setMessage("Do you want to start the navigation from File ${selectedFile.fileName}?")
            .setPositiveButton("Start Guidence") { _, _ ->
                setFragmentResult(
                    "selectedFile",
                    bundleOf(
                        "selectedFileBundle" to selectedFile.fileName,
                        "simulate" to false
                    )
                )
                (activity as MainActivity).switchToCompanionMapFragment()

            }
            .setNegativeButton("Start Simulation") { _, _ ->
                setFragmentResult(
                    "selectedFile",
                    bundleOf(
                        "selectedFileBundle" to selectedFile.fileName,
                        "simulate" to true
                    )
                )
                (activity as MainActivity).switchToCompanionMapFragment()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }


}