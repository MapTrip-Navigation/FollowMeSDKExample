package de.infoware.followmesdkexample

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import de.infoware.android.api.*
import de.infoware.android.api.enums.ApiError
import de.infoware.android.api.enums.ComputationSite
import de.infoware.followmesdkexample.companionmap.CompanionMapFragment
import de.infoware.followmesdkexample.companionmap.FollowMeControlsFragment
import de.infoware.followmesdkexample.companionmap.MapControlsFragment
import de.infoware.followmesdkexample.filelist.FileListFragment
import de.infoware.followmesdkexample.mainmenu.MainMenuFragment
import de.infoware.followmesdkexample.ui.main.MainFragment

/**
 *
 *
 *  Please read the README file for the necessary folder structure and file paths
 *
 *
 */

/**
 *  MainActivity for the FollowMeSDKExample
 *  Handles the licencing and SDK init
 *  Implements the licencing and SDK callbacks
 */
class MainActivity : AppCompatActivity(), ApiLicenseListener, ApiInitListener {

    private val TAG = "FollowMeSDKExample"

    private lateinit var viewModel: MainActivityViewModel

    // LiveData to wait for the SDK to be initialized
    private val isInitialized = MutableLiveData<Boolean>()

    // LiveData to wait for all necessary permissions to be granted
    private val permissionsGranted = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.supportActionBar?.hide()

        setContentView(R.layout.main_activity)

        val viewModelByModels: MainActivityViewModel by viewModels()
        viewModel = viewModelByModels

        if (savedInstanceState == null) {
            viewModel.setCurrentFragment(MainActivityViewModel.Fragment.MainFragment)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }


        this.initListener()
        this.checkPermissions()
    }

    /**
     *  Disabled the default back-button
     */
    override fun onBackPressed() {
        //super.onBackPressed()
    }

    /**
     *  Method to check all Permissions, and requests them if needed
     *  Uses the permissionsGranted LiveData to send either true or false to continue with the App
     *  If one of the requested permissions is denied, the app will not continue
     */
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

    /**
     *  Initialises the permission Observer and the SDK-init Observer
     */
    private fun initListener() {
        /**
         *  Observer for the Initialisation
         */
        val initObserver = Observer<Boolean> { isInitialized ->
            if(isInitialized) {
                switchToMainMenuFragment()
            }
        }
        this.isInitialized.observe(this, initObserver)

        /**
         *  Observer for the required permissions
         */
        val permissionObserver = Observer<Boolean> { permissionGranted ->
            if(permissionGranted) {
                this.initSDK()
            }
        }
        this.permissionsGranted.observe(this, permissionObserver)

        /**
         *  Observer for the Timer, which gets triggered 1 second after the Api is initialized
         */
        val timerObserver = Observer<Any> {
            this.isInitialized.postValue(true)
        }
        viewModel.timer.observe(this, timerObserver)

    }

    /**
     *  Initialises the SDK and sets the necessary Paths
     */
    private fun initSDK() {

        /**
         *  Sets the ComputationSites for
         *      geocoding
         *      navigation
         *      map
         *      pois
         *      traffic
         */
        val compParams = ComputationSiteParameters(
            ComputationSite.ONBOARD,
            ComputationSite.ONBOARD, ComputationSite.ONBOARD, ComputationSite.ONBOARD,
            ComputationSite.NONE)

        // registers Listener for License & SDK-init
        ApiHelper.Instance().addInitListener(this)
        ApiHelper.Instance().addLicenseListener(this)

        var initErr: ApiError? = null

        try {
            initErr = ApiHelper.Instance().initialize(applicationContext, compParams, this)
        } catch (exc: RuntimeException) {
            Log.e(TAG, "ApiHelper initialize failed. RuntimeException: " + exc.message)
            Toast.makeText(this, "ApiHelper initialize failed with RuntimeException", Toast.LENGTH_LONG).show()
        }

        if (initErr != ApiError.OK) {
            Log.e(TAG, "ApiHelper initialize failed. Error: $initErr")
            Toast.makeText(this, "ApiHelper initialize failed with $initErr", Toast.LENGTH_LONG).show()
        }
    }

    /**
     *  Needed to use GPS in your App
     *  enableLocationUpdates() is needed to get any GPS updates for your App
     */
    private fun startGPSProcessing() {
        Gps.useLogForSimulation("")
        ApiHelper.Instance().locationManager = IwLocationManagerGPS(this)
        ApiHelper.Instance().locationManager.enableLocationUpdates()
    }

    /**
     *  Listener to get updates of the current position of the device
     */
    private fun registerGPSListener() {
        Log.d(TAG, "GPS Listener registered")
        Gps.addGpsListener {
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

    /**
     *  Methods to switch between the Fragments
     */
    fun switchToFilelistFragment() {
        if(viewModel.getCurrentFragment() == MainActivityViewModel.Fragment.MainMenuFragment) {
            viewModel.setCurrentFragment(MainActivityViewModel.Fragment.FileListFragment)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FileListFragment.newInstance())
                .commitNow()
        }
    }

    fun switchToCompanionMapFragment() {
        if(viewModel.getCurrentFragment() == MainActivityViewModel.Fragment.FileListFragment) {
            viewModel.setCurrentFragment(MainActivityViewModel.Fragment.CompanionMapFragment)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CompanionMapFragment.newInstance())
                .replace(R.id.mapControlContainer, MapControlsFragment.newInstance())
                .replace(R.id.followMeControlContainer, FollowMeControlsFragment.newInstance())
                .commitNow()
        }
    }

    fun switchToMainMenuFragment() {
        if(viewModel.getCurrentFragment() == MainActivityViewModel.Fragment.MainFragment) {
            viewModel.setCurrentFragment(MainActivityViewModel.Fragment.MainMenuFragment)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainMenuFragment.newInstance())
                .commitNow()
        }
    }

    /* Api Listener Callbacks */

    /**
     *  Gets called when the Api is successfully initialized
     *
     *  After the Api is initialized, the GPS listener and processing can be enabled
     */
    override fun onApiInitialized() {
        Log.d(TAG, "onApiInitialized")
        registerGPSListener()
        startGPSProcessing()
        /**
         *  ViewModel sets a timeout after the API is initialized, to keep the SplashScreen up longer
         *  Set in the ViewModel to prevent the timer to be deleted on orientation-change
         */
        viewModel.initSplashScreenTimer()
    }

    /**
     *  Gets called when the Api could not be initialized
     *  @param err ApiError
     *  @param description a Description of the ApiError
     */
    override fun onApiInitError(err: ApiError?, description: String?) {
        Log.e(TAG, "onApiInitError")
        Log.e(TAG, err.toString() + " $description")
    }

    /**
     *  Gets called when the Api was uninitialized
     */
    override fun onApiUninitialized() {
        Log.d(TAG, "onApiUninitialized")
    }

    /* Licence Listener Callback */

    /**
     *  Gets called when the Licence is is not found, or could not be used
     *  @param error ApiError
     */
    override fun onApiLicenseError(error: ApiError?) {
        Log.e(TAG, "onApiLicenseError $error Licence.getLastError ${Licence.getLastError()}")
    }

    /**
     *  Gets called periodically while the licence gets checked
     */
    override fun onApiLicenseWaiting() {
        Log.d(TAG, "onApiLicenseWaiting")
    }

    /**
     *  Gets called when the Api notices a change in your licence
     */
    override fun onApiLicenseChanged() {
        Log.d(TAG, "onApiLicenseChanged")
    }
}