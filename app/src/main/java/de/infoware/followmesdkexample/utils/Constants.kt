package de.infoware.followmesdkexample.utils

import android.os.Environment

class Constants {
    companion object {
        val appPath = Environment.getExternalStorageState().toString() + "/FollowMeSDKExample"

        val dataPath = "$appPath/data"
    }
}