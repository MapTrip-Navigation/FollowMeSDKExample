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

class CompanionMapViewModel : ViewModel(), NavigationListener, MaptripTTSListener,
    FollowMeRouteListener, TaskListener {

    private val TAG = "CompanionMapViewModel"
    private var currentFollowMeRoute: FollowMeRoute? = null
    private var selectedFile: FollowMeTour? = null
    private var isSimulation = false

    // welches Fragment welcher Observer
    val currentStreetName = MutableLiveData<String>()
    val nextStreetName = MutableLiveData<String>()
    val metersToCrossing = MutableLiveData<Double>()
    val secondsToCrossing = MutableLiveData<Int>()
    val progress = MutableLiveData<Double>()
    val pictoFileName = MutableLiveData<String>()

    val metersToDestination = MutableLiveData<Int>()
    val destinationReached = MutableLiveData<Int>()

    val currentMuteOption = MutableLiveData<Boolean>()
    val currentPerspective = MutableLiveData<MapPerspective>()
    val autozoomToPosition = MutableLiveData<Any>()

    fun initPerspective(perspective: MapPerspective) {
        this.currentPerspective.postValue(perspective)
    }

    fun startFollowMeTour(filename:String, simulating: Boolean = false) {
        if(filename != "") {
            // SDK Funktion
            ApiHelper.Instance().queueApiCall(Runnable {
                FollowMeRoute.registerFollowMeRouteListener(this)

                selectedFile = FollowMeFileRepo.getFileByName(filename)

                if(selectedFile != null) {
                    currentFollowMeRoute = FollowMeRoute.init(selectedFile!!.filePath)

                    if(currentFollowMeRoute == null) {
                        Log.e(TAG, Api.getLastError().toString())
                        return@Runnable
                    }

                    // SDK Funktion
                    currentFollowMeRoute!!.registerTaskListener(this)
                    currentFollowMeRoute!!.calculate(true, false)
                    isSimulation = simulating
                }
            })
        }
    }

    fun autoZoomToCurrentPosition() {
        this.autozoomToPosition.postValue(Any())
    }

    fun switchPerspective() {
        val perspective = this.currentPerspective.value
        val newPerspective: MapPerspective?

        Log.d(TAG, "Current perspective: $perspective")

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

    fun switchMuteOption() {
        val isMute = MaptripTTSManager.Instance()?.isMute()
        currentMuteOption.postValue(isMute!!.not())
        MaptripTTSManager.Instance()?.setMute(isMute.not())
    }


    // LISTENER FUNKTIONEN
    // TODO VAR UMBENNEN
    override fun taskFinished(task: BaseTask) {
        if(task.returnValue == ApiError.OK) {
            currentFollowMeRoute!!.start(isSimulation)
            this.autoZoomToCurrentPosition()
        }
    }

    override fun taskProgress(task: BaseTask) {
        if(task.returnValue == ApiError.OK) {
            val roundedProgress = BigDecimal(task.progress).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            this.progress.postValue(roundedProgress)
        }
    }

    override fun followMeAction(actionType: FmrActionType?, eventString: String?): Boolean {
        if(eventString != null) {
            Log.d(TAG, "followMeAction")
            Log.d(TAG, actionType.toString())
            Log.d(TAG, eventString)
            MaptripTTSManager.Instance()?.speak(eventString, false)
        }
        return true
    }

    override fun followMeEvent(eventType: Int, eventString: String?): Boolean {
        if(eventString != null) {
            Log.d(TAG, "followMeEvent")
            Log.d(TAG, eventType.toString())
            Log.d(TAG, eventString)
            MaptripTTSManager.Instance()?.speak(eventString, false)
        }
        return true
    }

    override fun vehicleWarningReceived(restrictionType: VehicleWarningType?, restrictionValue: Double) {

    }

    override fun routeUpdate(routeComparison: RouteComparison?) {

    }

    override fun rerouting(task: Task<Void>?) {

    }

    override fun laneInfoReceived(allArrows: String?, divider: String?, relevantArrows: String?) {

    }

    override fun destinationInfoReceived(secondsToDestination: Double, metersToDestination: Double, energyToDestination: Double) {
        val metersAsInt = BigDecimal(metersToDestination).setScale(0, RoundingMode.HALF_EVEN).toInt()
        this.metersToDestination.postValue(metersAsInt)
    }

    override fun beforeAdviceStarts(speechIndependentSentence: String?, additionalAdviceInfo: String?, sentence: String?): Boolean {

        if(sentence != null) {
            MaptripTTSManager.Instance()?.speak(sentence, false)
        }

        return true
    }

    override fun navigationStarted() {

    }

    override fun crossingInfoReceived(
        actualStreetName: String?,
        nextStreetName: String?,
        pictoFileName: String?,
        streetType: CrosswayStreetType?,
        metersToCrossing: Double,
        secondsToCrossing: Double
    ) {
        val roundedMeters = BigDecimal(metersToCrossing).setScale(2, RoundingMode.HALF_EVEN).toDouble()
        val roundedSeconds = secondsToCrossing.toInt()

        this.pictoFileName.postValue(pictoFileName)
        this.nextStreetName.postValue(nextStreetName)
        this.currentStreetName.postValue(actualStreetName)
        this.metersToCrossing.postValue(roundedMeters)
        this.secondsToCrossing.postValue(roundedSeconds)
    }

    override fun destinationReached(index: Int) {
        this.destinationReached.postValue(index)
        Log.d(TAG, "DestinationReached: $index")
    }

    override fun speedLimitReceived(limit: Double) {

    }

    override fun ttsInitSuccessful() {
        Log.d(TAG, "ttsInit Successful")
    }

    override fun ttsInitError(missingData: Boolean, notSupported: Boolean) {
        Log.e(TAG, "ttsInit error: MissingData: $missingData - notSupportet: $notSupported")
    }


}