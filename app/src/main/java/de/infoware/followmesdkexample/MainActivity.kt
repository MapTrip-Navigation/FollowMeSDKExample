package de.infoware.followmesdkexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import de.infoware.android.api.*
import de.infoware.android.api.enums.ApiError
import de.infoware.android.api.enums.ComputationSite
import de.infoware.followmesdkexample.companionmap.CompanionMapFragment
import de.infoware.followmesdkexample.companionmap.MapControlsFragment
import de.infoware.followmesdkexample.dialog.DialogFragment
import de.infoware.followmesdkexample.filelist.FilelistFragment
import de.infoware.followmesdkexample.mainmenu.MainMenuFragment
import de.infoware.followmesdkexample.ui.main.MainFragment
import java.io.File

class MainActivity : AppCompatActivity(), ApiLicenseListener, ApiInitListener {

    private val TAG = "FollowMeSDKExample"

    private var alreadyInitalized = false

    val isInitialized: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.supportActionBar?.hide()

        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        val viewModel: MainActivityViewModel by viewModels()

        initListener()

        this.initSDK()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    private fun initListener() {
        val initObserver = Observer<Boolean> { isInitialized ->
            if(isInitialized) {
                switchToMainMenuFragment()
            }
        }

        this.isInitialized.observe(this, initObserver)
    }

    fun switchToFilelistFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, FilelistFragment.newInstance())
            .commitNow()
    }

    fun switchToCompanionMapFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CompanionMapFragment.newInstance())
            .replace(R.id.mapControlContainer, MapControlsFragment.newInstance())
            .commitNow()
    }

    fun switchToDialogFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, DialogFragment.newInstance())
            .commitNow()
    }

    fun switchToMainMenuFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainMenuFragment.newInstance())
            .commitNow()
    }

    fun initSDK() {

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
            initErr = ApiHelper.Instance().initialize(applicationContext, compParams, this)
        } catch (exc: RuntimeException) {
            Log.e(TAG, "ApiHelper initialize failed. RuntimeException: " + exc.message)
        }

        if (initErr != ApiError.OK) {
            Log.e(TAG, "ApiHelper initialize failed. Error: $initErr")
        }
    }

    private fun startGPSProcessing() {
        Gps.useLogForSimulation("")
        ApiHelper.Instance().locationManager = IwLocationManagerGPS(this)
        ApiHelper.Instance().locationManager.enableLocationUpdates()
    }

    private fun registerGPSListener() {
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
        registerGPSListener()
        startGPSProcessing()
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