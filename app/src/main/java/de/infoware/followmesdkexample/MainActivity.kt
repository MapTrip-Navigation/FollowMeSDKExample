package de.infoware.followmesdkexample

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import de.infoware.android.api.*
import de.infoware.android.api.enums.ApiError
import de.infoware.android.api.enums.ComputationSite
import de.infoware.followmesdkexample.companionmap.CompanionMapFragment
import de.infoware.followmesdkexample.companionmap.MapControlsFragment
import de.infoware.followmesdkexample.filelist.FilelistFragment
import de.infoware.followmesdkexample.mainmenu.MainMenuFragment
import de.infoware.followmesdkexample.ui.main.MainFragment

class MainActivity : AppCompatActivity(), ApiLicenseListener, ApiInitListener {

    private val TAG = "FollowMeSDKExample"

    private val isInitialized = MutableLiveData<Boolean>()
    private val permissionsGranted = MutableLiveData<Boolean>()

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

        this.initListener()
        this.checkPermissions()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    private fun checkPermissions() {
        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results: Map<String, Boolean> ->
            var allPermissionsGranted = true
            results.forEach{
                if(!it.value) allPermissionsGranted = false
            }

            if (allPermissionsGranted) {
                this.permissionsGranted.postValue(allPermissionsGranted)
            }
        }

        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED-> {

                this.permissionsGranted.postValue(true)
            }
            else -> {
                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE)
                )
            }
        }
    }

    // TODO COMMENT
    private fun initListener() {
        val initObserver = Observer<Boolean> { isInitialized ->
            if(isInitialized) {
                switchToMainMenuFragment()
            }
        }

        this.isInitialized.observe(this, initObserver)

        val permissionObserver = Observer<Boolean> { permissionGranted ->
            if(permissionGranted) {
                this.initSDK()
            }
        }

        this.permissionsGranted.observe(this, permissionObserver)
    }

    // TODO COMMENT
    private fun initSDK() {

        val compParams = ComputationSiteParameters(
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

    // TODO COMMENT

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

            Log.d(TAG, "GPS-Callback: positionUpdate $info")

        }
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

    fun switchToMainMenuFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainMenuFragment.newInstance())
            .commitNow()
    }

    override fun onApiInitialized() {
        Log.d(TAG, "onApiInitialized")
        isInitialized.postValue(true)
        registerGPSListener()
        startGPSProcessing()
    }

    override fun onApiInitError(err: ApiError?, description: String?) {
        Log.e(TAG, "onApiInitError")
        Log.e(TAG, err.toString() + " $description")
    }

    override fun onApiUninitialized() {
        Log.d(TAG, "onApiUninitialized")
    }

    /**
     * @param error
     */
    override fun onApiLicenseError(error: ApiError?) {
        Log.e(TAG, error.toString())
    }

    override fun onApiLicenseWaiting() {
        Log.d(TAG, "onApiLicenseWaiting")
    }

    override fun onApiLicenseChanged() {
        Log.d(TAG, "onApiLicenseChanged")
    }
}