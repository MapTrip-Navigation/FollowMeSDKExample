package de.infoware.followmesdkexample.filelist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import de.infoware.followmesdkexample.MainActivity
import de.infoware.followmesdkexample.followme.FollowMeFileRepo
import de.infoware.followmesdkexample.followme.data.FollowMeTour

class FilelistViewModel : ViewModel {

    private val TAG = "FilelistViewModel"

    val availableFollowMeFiles: MutableLiveData<List<FollowMeTour>> by lazy {
        MutableLiveData<List<FollowMeTour>>()
    }

    constructor() {

    }

    private fun initListener() {

    }
}