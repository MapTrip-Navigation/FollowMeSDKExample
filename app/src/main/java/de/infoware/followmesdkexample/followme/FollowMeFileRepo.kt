package de.infoware.followmesdkexample.followme

import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import de.infoware.followmesdkexample.followme.data.FollowMeTour
import java.io.File
import java.util.*

object FollowMeFileRepo {

    private val TAG = "FollowMeFileRepo"

    val routeFiles = MutableLiveData<List<FollowMeTour>>()
    var loadedFiles = mutableListOf<FollowMeTour>()

    fun getFileByName(filename:String) : FollowMeTour? {
        loadedFiles.forEach { file ->
            if(file.file.nameWithoutExtension === filename) {
                return file
            }
        }
        return null
    }

    fun getAllLoadedFiles():List<FollowMeTour> {
        return loadedFiles;
    }

    fun loadAllFilesInRoute() {
        val followMeFiles = mutableListOf<FollowMeTour>()

        var tour: FollowMeTour

        val path = Environment.getExternalStorageDirectory().toString() + "/FollowMeSDKExample/user/routes"
        val directory = File(path)
        if(!directory.exists()) {
            // TODO no directory found
            return
        }
        val files = directory.listFiles()

        for (i in files!!.indices) {
            if(files[i].isDirectory) {
                val subFiles = files[i].listFiles()
                for(file in subFiles!!.indices) {
                    tour = FollowMeTour(subFiles[i].name, subFiles[i].absolutePath, subFiles[i])
                    followMeFiles.add(tour)
                }
            } else {
                tour = FollowMeTour(files[i].name, files[i].absolutePath, files[i])
                followMeFiles.add(tour)
            }
        }

        if(followMeFiles.isEmpty()) {
            // TODO no files found
            return
        }
        loadedFiles = followMeFiles
        routeFiles.postValue(followMeFiles)
    }
}