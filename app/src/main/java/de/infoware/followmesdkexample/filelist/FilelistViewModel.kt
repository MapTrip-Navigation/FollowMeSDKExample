package de.infoware.followmesdkexample.filelist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.infoware.followmesdkexample.followme.FollowMeFileRepo
import de.infoware.followmesdkexample.followme.data.FollowMeTour

class FilelistViewModel : ViewModel() {

    private val TAG = "FilelistViewModel"

    val availableFollowMeFiles: MutableLiveData<List<FollowMeTour>> by lazy {
        MutableLiveData<List<FollowMeTour>>()
    }

    var followMeFiles = listOf<FollowMeTour>()

    init {
        followMeFiles = FollowMeFileRepo.loadAllFilesInRoute()
        availableFollowMeFiles.postValue(followMeFiles)
    }
}