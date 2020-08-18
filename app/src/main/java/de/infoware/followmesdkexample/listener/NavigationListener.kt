package de.infoware.followmesdkexample.listener

import android.content.Context
import android.util.Log
import de.infoware.android.api.*
import de.infoware.android.api.NavigationListener
import de.infoware.android.api.enums.*

class NavigationListener : NavigationListener, TaskListener {
    private var logFile: String? = null
    private val useTTS = true
    private var nav: Navigation? = null
    private var calculateRouteTask: Task<Void>? = null
    private var calculateAlternativeRouteTask: Task<Void>? = null

    private var altNav1: Navigation? = null
    private var altNav2: Navigation? = null
    private var altNav3: Navigation? = null

    fun setGPSLogFile(filePath: String) {
        logFile = filePath
    }

    /***
     * enable hybrid routing for routing with flows
     * ATTENTION: only enable if it is licensed
     */
    fun setUseHybridRouting() {
        Navigation.setAllowedComputationSite(ComputationSite.BOTH)
    }

    fun startNavigationWithGps(c: Context) {
        startGPSProcessing(c)
        startNavigation()
    }

    fun startLogFileBasedNavigation() {
        startLogFileBasedGPS()
        startNavigation()
    }

    fun startSimulatedNavigation() {
        startNavigation()
    }

    fun stopNavigation() {
        Navigation.stopNavigation()
        val mapview = IwMapViewManager.Instance().getMapviewer(1)

        if (mapview != null) {
            if (altNav1 != null) {
                mapview.showRoute(altNav1, false, 0, 0)
            }
            if (altNav2 != null) {
                mapview.showRoute(altNav2, false, 0, 0)
            }
            if (altNav3 != null) {
                mapview.showRoute(altNav3, false, 0, 0)
            }
        }
    }

    //! [NavigationTester_StartGpsProcessing]
    fun startGPSProcessing(c: Context): ApiError {
        Gps.useLogForSimulation("")
        ApiHelper.Instance().locationManager = IwLocationManagerGPS(c)
        // center map based on the location
        //return Mapviewer.setLocationProcessingMode(LocationProcessingMode.CENTER_MAP.getIntVal() |LocationProcessingMode.CAR_ICON.getIntVal());
        return ApiError.OK
    }
    //! [NavigationTester_StartGpsProcessing]

    /**
     * uses a log file as GPS signal
     */
    fun startLogFileBasedGPS(): ApiError {
        var retVal = ApiError.OK
        if (ApiHelper.Instance().locationManager != null) {
            ApiHelper.Instance().locationManager.disableLocationUpdates()
        }
        // use a log file for simulate a navigation
        retVal = Gps.useLogForSimulation(logFile)
        return if (retVal != ApiError.OK) {
            retVal
        } else ApiError.OK

        // center map based on the GPS signal
        //return Gps.setLocationProcessingMode(  LocationProcessingMode.CENTER_MAP.getIntVal() | LocationProcessingMode.CAR_ICON.getIntVal() );
    }

    fun pauseLocationUpdates() {
        if (ApiHelper.Instance().locationManager != null) {
            ApiHelper.Instance().locationManager.disableLocationUpdates()
        }
    }

    fun resumeLocationUpdates() {
        if (ApiHelper.Instance().locationManager != null) {
            Log.d(TAG, "resumeLocationnUpdates")
            ApiHelper.Instance().locationManager.enableLocationUpdates()
        }
    }

    fun stopGPSProcessing() {
        Gps.useLogForSimulation("")
        if(ApiHelper.Instance().locationManager != null) {
            ApiHelper.Instance().locationManager.disableLocationUpdates()
        }
    }

    fun showLanguages() {

        val curLang = Language.getActiveLanguage()
        Log.d(TAG, langtoString(curLang))

        val it = Language.getAvailableLanguages()

        var i = 0

        for (lang in it) {
            Log.d(TAG, String.format("%d: %s", i++, langtoString(lang)))
        }

    }


    //Helper
    /**
     * The the navigation is started by the following operations
     *
     * 1. select a vehicle profile for routing
     * 2. create and set the navigation itinerary containing start pose and destinations
     * 3. calculate a route
     * 4. start the navigation
     */
    private fun startNavigation(): ApiError? {
        var retVal: ApiError? = ApiError.OK

        // select a vehicle profile for routing
        retVal = selectVehicleProfile()
        if (retVal != ApiError.OK) {
            return retVal
        }

        // specify start, goal and intermediate goals
        val itinerary = createItinerary()
        if (retVal == null) {
            return ApiError.ROUTING
        }

        nav = Navigation()

        // specify that callbacks are implemented in this class
        nav!!.registerTaskListener(this)

        //  set the itinerary
        retVal = nav!!.setItinerary(itinerary)
        if (retVal != ApiError.OK) {
            return retVal
        }

        // calculate a route based on the itinerary and vehicle profile
        calculateRouteTask = nav!!.calculateRoute()

        // activate poi notifications ( see POITester.java )
        //POITester._initPoiNotification()

        // finally start the navigation in task callback
        return ApiError.OK
    }


    /**
     * Iterate over all available vehicle profiles in dataset and
     * activate the first one with the type VEHICLE_CAR
     */
    private fun selectVehicleProfile(): ApiError {
        // get iterator over all available profiles
        val it = VehicleProfile.getVehicleProfileIterator()

        // iterate vehicle profiles
        for (vhp in it) {
            // search first car profile
            if (vhp.vehicleType == VehicleType.CAR) {
                // set as active for routig
                return vhp.setAsActiveProfile()
            }
        }
        return ApiError.OUT_OF_RANGE
    }

    /**
     * create an itinerary consisting of the following coordinates:
     * 1. current GPS position
     * 2. via point in Bonn near center
     * 3. destination infoware
     */
    private fun createItinerary(): Itinerary? {

        var retVal = ApiError.OK
        val itinerary = Itinerary()

        // TODO
        return itinerary
    }


    fun showAlternativeRoute(): ApiError {
        if (this.nav == null) {
            return ApiError.OBJ_NOT_FOUND
        }
        this.calculateAlternativeRouteTask = nav!!.calculateRoutes(3)
        return ApiError.OK

    }

    private fun langtoString(lang: Language): String {
        return String.format(
            "%s, %s, Code: %s, Country: %s, TTS: %s",
            lang.languageName,
            lang.languageNameEnglish,
            lang.languageCode,
            lang.countryCode,
            lang.isTTSLanguage
        )
    }


    // NavigationListener
    override fun navigationStarted() {
        Log.d(TAG, "Navigation-Callback: navigationtarted")
    }

    override fun destinationReached(index: Int) {
        Log.d(TAG, "Navigation-Callback: destinationReached ind: $index")
    }

    override fun crossingInfoReceived(actualStreetName: String, nextStreetName: String,
                                      pictoFileName: String, streetType: CrosswayStreetType,
                                      metersToCrossing: Double, secondsToCrossing: Double) {

        val info = String.format(
            "actual street: %s, next street: %s, picto: %s, metersToCrossing: %f",
            actualStreetName,
            nextStreetName,
            pictoFileName,
            metersToCrossing
        )

        Log.d(TAG, "Navigation-Callback: crossingInfoReceived " + info)

    }

    override fun destinationInfoReceived(secondsToDestination: Double,
                                         metersToDestination: Double, energyToDestination: Double) {
        Log.d(
            TAG,
            "Navigation-Callback: destinationInfoReceived ToDest - sec: " +
                    "$secondsToDestination, meters: $metersToDestination"
        )

    }

    override fun speedLimitReceived(limit: Double) {
        Log.d(TAG, "Navigation-Callback: speedLimitReceived limit: $limit")

    }

    override fun laneInfoReceived(allArrows: String, divider: String,
                                  relevantArrows: String) {
        Log.d(TAG, "Navigation-Callback: laneInfoReceived")

    }

    override fun vehicleWarningReceived(restrictionType: VehicleWarningType,
                                        restrictionValue: Double) {
        Log.d(
            TAG,
            "Navigation-Callback: vehicleWarningReceived type: " +
                    "$restrictionType, val: $restrictionValue"
        )

    }

    override fun beforeAdviceStarts(speechIndependentSentence: String,
                                    additionalAdviceInfo: String, sentence: String): Boolean {

        // return false to inhibit speech output
        return true
    }

    override fun rerouting(task: Task<Void>) {

        Log.e(TAG, "Navigation-Callback: rerouting")
    }

    override fun taskFinished(task: BaseTask?) {

        // if the task was not successful, print an error
        if (task!!.returnValue.intVal < 0) {
            Log.e(
                TAG,
                "Task-Callback: Route could not be calculated. " +
                        "Errorcode: ${task.returnValue.toString()}"
            )
            return
        }
        if (task != null) {
            // if the task originated from calculateRoute, start a navigation
            if (task == this.calculateRouteTask) {
                val navReturnValue = nav!!.startNavigation()
                if (navReturnValue != ApiError.OK) {
                    Log.e(
                        TAG,
                        "Navigation could not be started. " +
                                "Errorcode: ${navReturnValue.toString()}"
                    )
                } else {
                    Log.e(TAG, "Navigation started.")
                }
            }
        }
        if (calculateAlternativeRouteTask != null) {
            // if the task originated from calculateAlternativeRoute, store the navigation and draw the route
            if (task == this.calculateAlternativeRouteTask) {
                // alternative navigation task contains a return value
                altNav1 = nav!!.getRouteNumber(0)
                altNav2 = nav!!.getRouteNumber(1)
                altNav3 = nav!!.getRouteNumber(2)

                val routeAltColorR = 0
                val routeAltColorG = 255
                val routeAltColorB = 0

                val color = (
                        (routeAltColorR shl 24) +
                                (routeAltColorG shl 16) +
                                (routeAltColorB shl 8) +
                                0xff
                        ).toLong()

                val mapview = IwMapViewManager.Instance().getMapviewer(1)

                if (mapview != null) {

                    mapview.showRoute(altNav1, true, color, color)
                    mapview.showRoute(altNav2, true, color, color)
                    mapview.showRoute(altNav3, true, color, color)
                }
            }
        }
    }

    fun startRefRouteNavigation(strFile: String?,
                                simulate: Boolean, ignoreStart: Boolean) {
        nav = Navigation()

        if (strFile == null) {
            Log.i(TAG, "start Reference route error no file")
            return
        }


        val it = Waypoint.getWaypointsFromFile(strFile)

        val error = Api.getLastError()


        if (error != ApiError.OK) {
            Log.i(TAG, "start Reference route error " + error)
            return
        }

        val wptFirst = it.iterator().next()
        if (wptFirst != null) {
            wptFirst.routeGuidanceToRouteMode = RouteGuidanceToRouteMode.NEARBY_START_POINT
        }

        var wptLast: Waypoint? = null

        for (wpt in it) {
            wptLast = wpt
        }

        if (wptLast == null) {
            Log.e(TAG, "start Reference route error missing waypoint")
            return
        }

        wptLast.initialRoutingMode = RoutingMode.REFERENCE_ROUTE

        val itn = Itinerary()

        if (!simulate) {
            itn.appendWaypoint(Waypoint.createCurrentGPSPosWaypoint())
        }

        if (!ignoreStart || simulate) {
            itn.appendWaypoint(wptFirst)
        }
        itn.appendWaypoint(wptLast)
        nav!!.itinerary = itn

        nav!!.registerTaskListener(object : TaskListener {

            override fun taskProgress(task: BaseTask) {
                Log.i(TAG, "start Reference taskProgress " + task.progress)
            }

            override fun taskFinished(task: BaseTask) {
                Log.i("NavigationController", "refRouteNavigationTask")

                val err: ApiError

                if (!simulate) {
                    err = nav!!.startNavigation()
                } else {
                    err = nav!!.startSimulatedNavigation()
                }

                val ok = isRouteOK(err)

                if (!ok) {
                    Log.i("NavigationController", "start Ref route navigation error $err")
                }

            }
        })

        nav!!.calculateRoute()
    }


    override fun taskProgress(task: BaseTask) {
        val progress = task.progress
        Log.e(TAG, "Task-Callback: routing progress " + progress.toString())
    }

    override fun routeUpdate(routeComparison: RouteComparison) {
        Log.e(TAG, "Task-Callback: routeUpdate - new / better route available")

        //use new route
        routeComparison.useUpdatedRoute()

    }

    companion object {

        private val TAG = "NavigationTester"

        fun isRouteOK(error: ApiError): Boolean {

            if (error == ApiError.OK) {
                return true
            }

            if (error == ApiError.WARNING_ROUTE_OBST_DEST_IN_BLOCKED_LEZ) {
                return true
            }

            if (error == ApiError.WARNING_ROUTE_OBST_DEST_IN_RESIDENT_ONLY) {
                return true
            }

            if (error == ApiError.WARNING_ROUTE_OBST_TRUCKATT_VIOLATED) {
                return true
            }

            if (error == ApiError.WARNING_ROUTE_OBST_DEST_MOVED) {
                return true
            }

            if (error == ApiError.WARNING_ROUTE_OBST_VEHICLETYPE_VIOLATED) {
                return true
            }

            return error == ApiError.WARNING_ROUTE_OBST_FOUND

        }
    }
}