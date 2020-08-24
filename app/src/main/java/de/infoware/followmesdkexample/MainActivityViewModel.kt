package de.infoware.followmesdkexample

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.infoware.android.api.*
import de.infoware.android.api.enums.ApiError
import de.infoware.android.api.enums.ComputationSite
import de.infoware.followmesdkexample.utils.Constants

class MainActivityViewModel : ViewModel(), ApiLicenseListener, ApiInitListener {
    private val TAG = "FollowMeSDKExample"
    val isInitialized: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    var alreadyInitalized = false

    fun initSDK(context: Context) {
        val compParams: ComputationSiteParameters

        compParams = ComputationSiteParameters(
            ComputationSite.ONBOARD,
            ComputationSite.ONBOARD, ComputationSite.ONBOARD, ComputationSite.ONBOARD,
            ComputationSite.NONE)

        val appPath = Environment.getExternalStorageDirectory().toString() + "/FollowMeSDKExample"
        val dataPath = "${appPath}/data"

        ApiHelper.Instance().addInitListener(this)
        ApiHelper.Instance().addLicenseListener(this)
        ApiHelper.Instance().initPaths(appPath, dataPath)

        var initErr: ApiError? = null

        try {
            initErr = ApiHelper.Instance().initialize(context, compParams, this)
        } catch (exc: RuntimeException) {
            Log.e(TAG, "ApiHelper initialize failed. RuntimeException: " + exc.message)
        }

        if (initErr != ApiError.OK) {
            Log.e(TAG, "ApiHelper initialize failed. Error: $initErr")
        }
    }

    private fun registerApiListener() {
        Log.d(TAG, "GPS Listener registered")
        Gps.registerGpsListener {
                latitude, longitude, altitude, speedMetersPerSecond,
                course, horizontalAccuracy, gpstime ->
            val info = String
                .format(
                    "lat: %f, long: %f, alt: %f, speed: %f, course: %f, acc: %f, time %s",
                    latitude, longitude, altitude,
                    speedMetersPerSecond, course,
                    horizontalAccuracy, gpstime.toString()
                )

            Log.d(TAG, "GPS-Callback: positionUpdate " + info)

        }
    }

    override fun onApiInitialized() {

        Log.d(TAG, "onApiInitialized")
        isInitialized.postValue(true)
        alreadyInitalized = true

        /*
        Navigation.setAllowedComputationSite(ComputationSite.ONBOARD)

        //must be called first after successful api init
        Navigation.registerNavigationListener(navTester)
        navTester.startGPSProcessing(applicationContext)
        registerApiListener()

        val string = Licence.getHardwareKey()

        //init media queue with correct speech file path

        //must be called first after successful api init
        //registerApiListener()

        mapTester.setViewingCenter(mapView.mapviewer)
        */
    }

    override fun onApiInitError(p0: ApiError?, p1: String?) {
        Log.d(TAG, "onApiInitError")
        Log.e(TAG, p0.toString())
    }

    override fun onApiUninitialized() {
        Log.e(TAG, "onApiUninitialized")
    }

    override fun onApiLicenseError(p0: ApiError?) {
        Log.e(TAG, p0.toString())
    }

    override fun onApiLicenseWaiting() {
        Log.e(TAG, "onApiLicenseWaiting")
    }

    override fun onApiLicenseChanged() {
        Log.e(TAG, "onApiLicenseWaiting")
    }

}