package de.infoware.followmesdkexample.filelist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.infoware.followmesdkexample.followme.FollowMeFileRepo
import de.infoware.followmesdkexample.followme.data.FollowMeTour

/**
 *  ViewModel for the FileListFragment
 *  Handles the FollowMeFileRepo and sends the file-list to the Fragment
 */
class FilelistViewModel : ViewModel() {

    private val TAG = "FilelistViewModel"

    // LiveData for the loaded FollowMeFiles
    val availableFollowMeFiles: MutableLiveData<List<FollowMeTour>> by lazy {
        MutableLiveData<List<FollowMeTour>>()
    }

    var followMeFiles = listOf<FollowMeTour>()

    /**
     *  On creating the ViewModel the Repo (singleton class) loads all available Files
     *  The ViewModel then posts the Files via the LiveData and the Fragment receives the list
     */
    init {
        followMeFiles = FollowMeFileRepo.loadAllFilesInRoute()
        availableFollowMeFiles.postValue(followMeFiles)
    }
}