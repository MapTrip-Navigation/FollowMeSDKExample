package de.infoware.followmesdkexample.companionmap

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
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
    private var mapviewer: Mapviewer? = null

    val currentStreetName = MutableLiveData<String>()
    val nextStreetName = MutableLiveData<String>()
    val metersToCrossing = MutableLiveData<Double>()
    val secondsToCrossing = MutableLiveData<Double>()
    val progress = MutableLiveData<Double>()

    val currentMuteOption = MutableLiveData<Boolean>()

    fun setMapViewer(mapviewer: Mapviewer) {
        this.mapviewer = mapviewer
    }

    fun startFollowMeTour(filename:String, simulating: Boolean = false) {
        if(filename != "") {
            ApiHelper.Instance().queueApiCall(Runnable {
                FollowMeRoute.registerFollowMeRouteListener(this)

                selectedFile = FollowMeFileRepo.getFileByName(filename)

                if(selectedFile != null) {
                    currentFollowMeRoute = FollowMeRoute.init(selectedFile!!.filePath)

                    if(currentFollowMeRoute == null) {
                        Log.e(TAG, Api.getLastError().toString())
                        return@Runnable
                    }

                    currentFollowMeRoute!!.registerTaskListener(this)
                    currentFollowMeRoute!!.calculate(true, false)
                    isSimulation = simulating
                }
            })
        }
    }

    fun autozoomToCurrentPosition() {
        mapviewer?.resumeLocationTracking()
    }

    fun switchPerspective() {
        val perspective = mapviewer?.perspective
        var newPerspective: MapPerspective? = null

        Log.d(TAG, "Current perspective: " + perspective)

        when (perspective) {
            MapPerspective.PERSPECTIVE_2D_DRIVING_DIRECTION ->
                newPerspective = MapPerspective.PERSPECTIVE_2D_NORTHWARD

            MapPerspective.PERSPECTIVE_2D_NORTHWARD ->
                newPerspective = MapPerspective.PERSPECTIVE_3D

            MapPerspective.PERSPECTIVE_3D ->
                newPerspective = MapPerspective.PERSPECTIVE_2D_DRIVING_DIRECTION

            else -> return
        }

        mapviewer?.perspective = newPerspective
    }

    fun switchMuteOption() {
        val isMute = MaptripTTSManager.Instance()?.isMute()
        currentMuteOption.postValue(isMute!!.not())
        MaptripTTSManager.Instance()?.setMute(isMute.not())
    }

    override fun taskFinished(task: BaseTask) {
        if(task.returnValue == ApiError.OK) {
            currentFollowMeRoute!!.start(isSimulation)
            this.autozoomToCurrentPosition()
        }
    }

    override fun taskProgress(task: BaseTask) {
        if(task.returnValue == ApiError.OK) {
            val roundedProgress = BigDecimal(task.progress).setScale(2, RoundingMode.HALF_EVEN).toDouble()
            this.progress.postValue(roundedProgress)
        }
    }

    override fun followMeAction(p0: FmrActionType?, p1: String?): Boolean {
        Log.d(TAG, "followMeAction")
        Log.d(TAG, p0.toString())
        Log.d(TAG, p1)

        if(p1 != null) {
            MaptripTTSManager.Instance()?.speak(p1, false)
        }
        return true
    }

    override fun followMeEvent(p0: Int, p1: String?): Boolean {
        Log.d(TAG, "followMeEvent")
        Log.d(TAG, p0.toString())
        Log.d(TAG, p1)

        if(p1 != null) {
            MaptripTTSManager.Instance()?.speak(p1, false)
        }
        return true
    }

    override fun vehicleWarningReceived(p0: VehicleWarningType?, p1: Double) {

    }

    override fun routeUpdate(p0: RouteComparison?) {

    }

    override fun rerouting(p0: Task<Void>?) {

    }

    override fun laneInfoReceived(p0: String?, p1: String?, p2: String?) {

    }

    override fun destinationInfoReceived(p0: Double, p1: Double, p2: Double) {

    }

    override fun beforeAdviceStarts(speechIndependentSentence: String?, additionalAdviceInfo: String?, sentence: String?): Boolean {

        if(sentence != null) {
            MaptripTTSManager.Instance()?.speak(sentence, false)
        }

        return true
    }

    override fun navigationStarted() {
        Log.d(TAG, "navigationStarted")
    }

    override fun crossingInfoReceived(
        actualStreetName: String?,
        nextStreetName: String?,
        pictoFileName: String?,
        streetType: CrosswayStreetType?,
        metersToCrossing: Double,
        secondsToCrossing: Double
    ) {
        this.nextStreetName.postValue(nextStreetName)
        this.currentStreetName.postValue(actualStreetName)
        this.metersToCrossing.postValue(metersToCrossing)
        this.secondsToCrossing.postValue(secondsToCrossing)
    }

    override fun destinationReached(p0: Int) {
        Log.d(TAG, "DestinationReached: ${p0}")
    }

    override fun speedLimitReceived(p0: Double) {

    }

    override fun ttsInitSuccessful() {
        Log.d(TAG, "ttsInit Successful")
    }

    override fun ttsInitError(missingData: Boolean, notSupported: Boolean) {
        Log.e(TAG, "ttsInit error: MissingData: ${missingData} - notSupportet: ${notSupported}")
    }


}