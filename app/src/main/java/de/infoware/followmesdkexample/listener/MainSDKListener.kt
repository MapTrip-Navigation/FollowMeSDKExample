package de.infoware.followmesdkexample.listener

import android.content.Context
import android.util.Log
import de.infoware.android.api.*
import de.infoware.android.api.enums.ApiError
import de.infoware.android.api.enums.ComputationSite
import de.infoware.followmesdkexample.companionmap.MapViewTester
import de.infoware.followmesdkexample.utils.Constants

class MainSDKListener(var applicationContext: Context) : ApiInitListener, ApiLicenseListener {

    private val TAG = "FollowMeSDKExample"

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
/*
        Log.d(TAG, "onApiInitialized")

        Navigation.setAllowedComputationSite(ComputationSite.ONBOARD)

        //must be called first after successful api init
        Navigation.registerNavigationListener(navTester)
        navTester.startGPSProcessing(applicationContext)
        registerApiListener()

        val string = Licence.getHardwareKey()

        //init media queue with correct speech file path

        //must be called first after successful api init
        //registerApiListener()

        mapTester.setViewingCenter(mapView.mapviewer)*/
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