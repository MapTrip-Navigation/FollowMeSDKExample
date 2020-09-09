package de.infoware.followmesdkexample.companionmap

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.infoware.android.api.*
import de.infoware.android.api.enums.*
import de.infoware.followmesdkexample.followme.FollowMeFileRepo
import de.infoware.followmesdkexample.followme.data.FollowMeTour
import de.infoware.followmesdkexample.sound.MaptripTTSListener
import de.infoware.followmesdkexample.sound.MaptripTTSManager
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.concurrent.timer

/**
 *  ViewModel for CompanionMapFragment and MapControlsFragment
 *  CompanionMapFragment, MapControlsFragment and FollowMeControlsFragment all use the same instance, so it can be used to pass information between them
 *  Handles all logic related to the Map & MapViewer
 *  Handles all Listener-Events for Navigation, TTS, FollowMe, and Tasks (such as calculating a route)
 */
class CompanionMapViewModel : ViewModel(), NavigationListener, MaptripTTSListener,
    FollowMeRouteListener, TaskListener {

    companion object {
        private const val TAG = "CompanionMapViewModel"
    }

    // The currently running FollowMeRoute
    private var currentFollowMeRoute: FollowMeRoute? = null
    // The in the FileListFragment selected File (from the FollowMeFileRepo)
    private var selectedFile: FollowMeTour? = null
    // Simulation-Option for the FollowMeRoute
    private var isSimulation = false
    // Boolean if there is a running navigation
    var navigationRunning = false

    // Timer for alternating the collection-state arrows
    private lateinit var blinkTimer : Timer

    // LiveData for the Crossing-Information available via the crossingInfoReceived NavigationLister event
    val currentStreetName = MutableLiveData<String>()
    val nextStreetName = MutableLiveData<String>()
    val metersToCrossing = MutableLiveData<Int>()
    val secondsToCrossing = MutableLiveData<Int>()
    val pictoFileName = MutableLiveData<String>()

    // LiveData for the calculation of the route
    val progress = MutableLiveData<Int>()
    // LiveData for the state of the task (finished)
    val taskFinished = MutableLiveData<Any>()

    // LiveData for the Destination-Information available via the destinationInfoReceived NavigationLister event
    val metersToDestination = MutableLiveData<Int>()
    val secondsToDestination = MutableLiveData<Int>()
    val destinationReached = MutableLiveData<Int>()

    // LiveData for the current Sound-Option, so that the MapControlsFragment can set the visual accordingly
    val currentMuteOption = MutableLiveData<Boolean>()

    // LiveData for the MapPerspective. Gets called via a button in the MapControlsFragment and sends the next perspective to the CompanionMapFragment
    val currentPerspective = MutableLiveData<MapPerspective>()
    // LiveData for the 'Zoom to Vehicle'-Button. Gets called when the button (MapControlsFragment) is clicked and triggers the Observer in the CompanionMapFragment
    val autoZoomToPosition = MutableLiveData<Any>()

    // LiveData for the current collection state (left, right, both, none)
    val currentCollectionState = MutableLiveData<FmrActionType>()
    // LiveData for switching the collection-side images (making them 'blink')
    val switchCollectionImage = MutableLiveData<Boolean>()

    /**
     *  Initialises the MapPerspective. Needed for calculating the next perspective on button-click
     *  @param perspective The current MapPerspective
     */
    fun initPerspective(perspective: MapPerspective) {
        this.currentPerspective.postValue(perspective)
    }

    /**
     *  Starts the calculation of a FollowMeTour and saves the Tour
     *  Registers the TaskListener for the Calculation-Progress
     *  @param filename The FileName in the /user/routes/ directory. Gets used to fetch the selected File from the FileListRepo
     *  @param simulating Option if the Tour should be simulation or a normal navigation
     */
    fun startFollowMeTour(filename:String, simulating: Boolean = false) {
        if(filename != "") {

            /**
             *  Allows to create a runnable that will execute on the MapTrip SDK thread and bundle multiple consecutive calls to be executed in a single Task
             */
            ApiHelper.Instance().queueApiCall(Runnable {
                FollowMeRoute.registerFollowMeRouteListener(this)

                // fetches the selected File from the Repo
                selectedFile = FollowMeFileRepo.getFileByName(filename)

                if(selectedFile != null) {
                    // Initialises the FollowMeRoute via the filePath. The initialised Route gets returned in the .init() - Function and saved as a global variable
                    currentFollowMeRoute = FollowMeRoute.init(selectedFile!!.filePath)

                    // Logs an Error if the FollowMeRoute could not be initialised
                    if(currentFollowMeRoute == null) {
                        Log.e(TAG, Api.getLastError().toString())
                        return@Runnable
                    }

                    // Registers the ViewModel as TaskListener for the FollowMeRoute tasks (e.g. calculating the tour)
                    currentFollowMeRoute!!.registerTaskListener(this)
                    currentFollowMeRoute!!.calculate(true, false)

                    // sets the global variable
                    isSimulation = simulating
                }
            })
        }
    }

    /**
     *  Triggers the Observable to send an Any-Object to the CompanionMapFragment
     *  CompanionMapFragment set the MapViewer -Center on the Vehicle
     *  Gets called from the MapControlsFragment on Button-Click
     */
    fun autoZoomToCurrentPosition() {
        this.autoZoomToPosition.postValue(Any())
    }

    /**
     *  Calculates the new map-perspective and sends the result via an Observable to the CompanionMapFragment
     *  Gets called from the MapControlsFragment on Button-Click
     *  Needs the initPerspective() - Function to be called before, to set the initial value
     */
    fun switchPerspective() {
        val perspective = this.currentPerspective.value
        val newPerspective: MapPerspective?

        Log.d(TAG, "Current perspective: $perspective")

        // Rotates through the available map-perspectives
        newPerspective = when (perspective) {
            MapPerspective.PERSPECTIVE_2D_DRIVING_DIRECTION ->
                MapPerspective.PERSPECTIVE_2D_NORTHWARD

            MapPerspective.PERSPECTIVE_2D_NORTHWARD ->
                MapPerspective.PERSPECTIVE_3D

            MapPerspective.PERSPECTIVE_3D ->
                MapPerspective.PERSPECTIVE_2D_DRIVING_DIRECTION

            else -> return
        }

        this.currentPerspective.postValue(newPerspective)
    }

    /**
     *  Creates and starts the blink timer responsible for alternating the collection-state arrows
     *  Sends a new value every seconds, which gets used in the FollowMeControlsFragment
     */
    private fun startBlinkTimer() {
        var blink = false
        blinkTimer = timer("blink", true, 0.toLong(), 1000, action = {
            switchCollectionImage.postValue(blink)
            blink = !blink
        })

    }

    /**
     *  Stops the already created blink timer
     *  Used when the destination is reached or the navigation is stopped
     */
    private fun stopBlinkTimer() {
        blinkTimer.cancel()
    }

    /**
     *  Switches the MuteOption of MapTripTTS and send the result to the MapControlsFragment
     *  Gets called from the MapControlsFragment on Button-Click
     */
    fun switchMuteOption() {
        val isMute = MaptripTTSManager.Instance()?.isMute()
        currentMuteOption.postValue(isMute!!.not())
        MaptripTTSManager.Instance()?.setMute(isMute.not())
    }

    /* TaskListener Callbacks */

    /**
     *  Gets called when a task is finished (e.g. the Tour is finished calculating)
     *  @param task The finished Task
     */
    override fun taskFinished(task: BaseTask) {
        if(task.returnValue == ApiError.OK) {
            // Start the route
            currentFollowMeRoute!!.start(isSimulation)
            // Boolean to prevent start of the same tour on orientation change
            this.navigationRunning = true
            // LiveData to enable the Navigation-info
            taskFinished.postValue(Any())
            // Zoom to current position
            this.autoZoomToCurrentPosition()
            // Starts the followme-collect arrows
            this.startBlinkTimer()
        }
    }

    /**
     *  Gets updated when a new task progress is made
     *  @param task The currently running Task
     */
    override fun taskProgress(task: BaseTask) {
        if(task.returnValue == ApiError.OK) {
            val roundedProgress = BigDecimal(task.progress).setScale(0, RoundingMode.HALF_EVEN).toInt()
            this.progress.postValue(roundedProgress)
        }
    }

    /* FollowMeRouteListener callbacks */

    /**
     *  Gets called on FollowMeAction (e.g. 'collect left', 'transfer', etc)
     *  @param actionType the Type of the FollowMeAction
     *  @param eventString the text which gets used by the TTS
     */
    override fun followMeAction(actionType: FmrActionType?, eventString: String?): Boolean {
        if(eventString != null) {
            this.currentCollectionState.postValue(actionType)
            MaptripTTSManager.Instance()?.speak(eventString, false)
        }
        return true
    }

    /**
     *  Gets called on FollowMeEvent (e.g. 'backwards', 'forwards', etc; user generated events)
     *  @param eventType the ID if the Event
     *  @param eventString the text which gets used by the TTS
     */
    override fun followMeEvent(eventType: Int, eventString: String?): Boolean {
        if(eventString != null) {
            MaptripTTSManager.Instance()?.speak(eventString, false)
        }
        return true
    }

    /* NavigationListener callbacks */

    /**
     *  Gets called on receiving a vehicle warning
     *  @param restrictionType Specifies the type of restriction
     *  @param restrictionValue the values of the specified restriction
     */
    override fun vehicleWarningReceived(restrictionType: VehicleWarningType?, restrictionValue: Double) {

    }

    /**
     *  Gets called when a new / better Route is possible
     *  @param routeComparison Object to get the differences between the current route and the new route. Can also be used to start the proposed new route
     */
    override fun routeUpdate(routeComparison: RouteComparison?) {

    }

    /**
     *  Gets called when an automatic rerouting occurs; Needs a currently active Navigation
     *  @param task Contains the rerouting process
     */
    override fun rerouting(task: Task<Void>?) {

    }

    /**
     *  Gets called when a new lane information is available
     *  @param allArrows all available arrows at the next crossing
     *  @param divider lane divider
     *  @param relevantArrows the relevant arrows at he next crossing
     */
    override fun laneInfoReceived(allArrows: String?, divider: String?, relevantArrows: String?) {

    }

    /**
     *  Gets called when new information about the destination is available
     *  @param secondsToDestination The E.T.A in seconds
     *  @param metersToDestination the estimated meters to the destination
     *  @param energyToDestination the estimated energy (kwh) needed to reach the destination
     */
    override fun destinationInfoReceived(secondsToDestination: Double, metersToDestination: Double, energyToDestination: Double) {
        val metersAsInt = BigDecimal(metersToDestination).setScale(0, RoundingMode.HALF_EVEN).toInt()
        this.metersToDestination.postValue(metersAsInt)

        val secondsAsInt = BigDecimal(secondsToDestination).setScale(0, RoundingMode.HALF_EVEN).toInt()
        this.secondsToDestination.postValue(secondsAsInt)
    }

    /**
     *  Gets Called before a advice / direction indication is needed
     *  @param speechIndependentSentence language independent speech sentence
     *  @param additionalAdviceInfo street-name / route-number responses
     *  @param sentence speechIndependentSentence in target language including the additionalAdviceInfo
     */
    override fun beforeAdviceStarts(speechIndependentSentence: String?, additionalAdviceInfo: String?, sentence: String?): Boolean {

        if(sentence != null) {
            MaptripTTSManager.Instance()?.speak(sentence, false)
        }

        return true
    }

    /**
     *  Gets called when the navigation started successfully
     */
    override fun navigationStarted() {

    }

    /**
     *  Gets called when new info about the next crossing is available
     *  @param actualStreetName The street name the device is currently on
     *  @param nextStreetName The next street name after the crossing
     *  @param pictoFileName The name of the pictogram picture file
     *  @param streetType Type of street / crossing
     *  @param metersToCrossing distance to the crossing in meters
     *  @param secondsToCrossing E.T.A to crossing in seconds
     */
    override fun crossingInfoReceived(
        actualStreetName: String?,
        nextStreetName: String?,
        pictoFileName: String?,
        streetType: CrosswayStreetType?,
        metersToCrossing: Double,
        secondsToCrossing: Double
    ) {
        // Rounds the meters to 2 decimal places
        val roundedMeters = BigDecimal(metersToCrossing).setScale(0, RoundingMode.HALF_EVEN).toInt()
        // Cuts of the milliseconds of the secondsToCrossing
        val roundedSeconds = secondsToCrossing.toInt()

        // Triggers the Observers and posts new data for the MapControlFragment to handle
        this.pictoFileName.postValue(pictoFileName)
        this.nextStreetName.postValue(nextStreetName)
        this.currentStreetName.postValue(actualStreetName)
        this.metersToCrossing.postValue(roundedMeters)
        this.secondsToCrossing.postValue(roundedSeconds)
    }

    /**
     *  Gets called when a destination is reached
     *  @param index The index of the destination (0 based; e.g. returns 2 if it was the third destination reached)
     */
    override fun destinationReached(index: Int) {
        // Posts new data so the MapControlFragment knows the Navigation has been finished
        this.destinationReached.postValue(index)

        // Posts collect_unknown to hide the collection-arrows
        this.currentCollectionState.postValue(FmrActionType.COLLECT_UNKNOWN)
        // Stops the blinking-arrows timer
        this.stopBlinkTimer()

        this.navigationRunning = false
        Log.d(TAG, "DestinationReached: $index")
    }

    /**
     *  Gets called when a new speedLimit is available
     *  @param limit the speed limit in km/h (a value <= 0 signals that no speed limit is available at the moment)
     */
    override fun speedLimitReceived(limit: Double) {

    }

    /* MapTripTTSListener callbacks */

    /**
     *  Gets called when the initialization was successful
     */
    override fun ttsInitSuccessful() {
        Log.d(TAG, "ttsInit Successful")
    }

    /**
     *  Gets called when there was an error in the initialization
     *  @param missingData boolean if there was missing data
     *  @param notSupported boolean if the TTS is not supported
     */
    override fun ttsInitError(missingData: Boolean, notSupported: Boolean) {
        Log.e(TAG, "ttsInit error: MissingData: $missingData - notSupported: $notSupported")
    }
}