package de.infoware.followmesdkexample.followme

import android.os.Environment
import de.infoware.android.api.ApiHelper
import de.infoware.followmesdkexample.followme.data.FollowMeTour
import java.io.File

/**
 *  Singleton class to read all available files in the /user/routes/ directory
 *
 */
object FollowMeFileRepo {

    private val TAG = "FollowMeFileRepo"

    const val local = "Local"
    const val server = "Server"

    private val routeFolder = server

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
    fun loadAllFilesInRoute(): List<FollowMeTour> {
        val directory = File("${ApiHelper.Instance().userDataPath}/followMeRoutes/$routeFolder")
        if (!directory.exists()) return emptyList() // Early return if directory doesn't exist

        val followMeFiles = directory.walkTopDown() // Use walkTopDown for recursive traversal
            .filter { it.isFile && (it.extension == "nmea" || it.extension == "csv") } // Filter for files with correct extensions
            .map { FollowMeTour(it.nameWithoutExtension, it.extension, it.absolutePath) } // Map files to FollowMeTour objects
            .toMutableList()

        loadedFiles = followMeFiles// Update loadedFiles if necessary
        return followMeFiles
    }
}