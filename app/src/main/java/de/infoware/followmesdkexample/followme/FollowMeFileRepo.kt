package de.infoware.followmesdkexample.followme

import android.os.Environment
import de.infoware.followmesdkexample.followme.data.FollowMeTour
import java.io.File

/**
 *  Singleton class to read all available files in the /user/routes/ directory
 *
 */
object FollowMeFileRepo {

    private val TAG = "FollowMeFileRepo"

    // List of FollowMeTour files
    private var loadedFiles = mutableListOf<FollowMeTour>()

    /**
     *  Searches the loaded files for a given filename
     *  @param filename Name of the file
     *  @return [FollowMeTour] if the file was found, NULL if no file was found
     */
    fun getFileByName(filename:String) : FollowMeTour? {
        loadedFiles.forEach { file ->
            if(file.fileName == filename) {
                return file
            }
        }
        return null
    }

    fun getAllLoadedFiles():List<FollowMeTour> {
        return loadedFiles
    }

    /**
     *  Loads all available files in the /user/routes/ directory with up to one subdirectory
     *  @return [List]<[FollowMeTour]> a list of all found FollowMeTour files. Returns empty list if no files were found
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
                    if(subFiles[i].extension != "nmea" && subFiles[i].extension != "csv") {
                        continue
                    }
                    tour = FollowMeTour(subFiles[i].nameWithoutExtension, subFiles[i].extension, subFiles[i].absolutePath)
                    followMeFiles.add(tour)
                }
            } else {
                if(files[i].extension != "nmea" && files[i].extension != "csv") {
                    continue
                }
                tour = FollowMeTour(files[i].nameWithoutExtension, files[i].extension, files[i].absolutePath)
                followMeFiles.add(tour)
            }
        }

        if(followMeFiles.isEmpty()) {
            return followMeFiles
        }
        loadedFiles = followMeFiles
        return followMeFiles
    }
}