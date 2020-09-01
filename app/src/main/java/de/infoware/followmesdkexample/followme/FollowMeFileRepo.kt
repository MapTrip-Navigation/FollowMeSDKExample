package de.infoware.followmesdkexample.followme

import android.os.Environment
import android.util.Log
import androidx.lifecycle.MutableLiveData
import de.infoware.followmesdkexample.followme.data.FollowMeTour
import java.io.File
import java.util.*

/**
 *  Singleton class to read all available files in the /user/routes/ directory
 *
 */
object FollowMeFileRepo {

    private val TAG = "FollowMeFileRepo"

    // LiveData for the laoded FollowMeTour files
    val routeFiles = MutableLiveData<List<FollowMeTour>>()
    // List of FollowMeTour files
    var loadedFiles = mutableListOf<FollowMeTour>()

    /**
     *  Searches the loaded files for a given filename
     *  @param filename Name of the file
     *  @return FollowMeTour if the file was found, NULL if no file was found
     */
    fun getFileByName(filename:String) : FollowMeTour? {
        loadedFiles.forEach { file ->
            if(file.file.nameWithoutExtension == filename) {
                return file
            }
        }
        return null
    }

    fun getAllLoadedFiles():List<FollowMeTour> {
        return loadedFiles;
    }

    /**
     *  Loads all available files in the /user/routes/ directory with up to one subdirectory
     *  @return List<FollowMeTour> a list of all found FollowMeTour files. Returns empty list if no files were found
     */
    fun loadAllFilesInRoute() : List<FollowMeTour> {
        val followMeFiles = mutableListOf<FollowMeTour>()

        var tour: FollowMeTour

        val path = Environment.getExternalStorageDirectory().toString() + "/FollowMeSDKExample/user/routes"
        val directory = File(path)
        if(!directory.exists()) {
            return followMeFiles
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
            return followMeFiles
        }
        loadedFiles = followMeFiles
        routeFiles.postValue(followMeFiles)
        return followMeFiles
    }
}