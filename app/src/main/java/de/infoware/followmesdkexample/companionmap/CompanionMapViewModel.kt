package de.infoware.followmesdkexample.companionmap

import android.util.Log
import androidx.lifecycle.ViewModel
import de.infoware.android.api.*
import de.infoware.android.api.enums.ApiError
import de.infoware.android.api.enums.CrosswayStreetType
import de.infoware.android.api.enums.FmrActionType
import de.infoware.android.api.enums.VehicleWarningType
import de.infoware.followmesdkexample.followme.FollowMeFileRepo
import de.infoware.followmesdkexample.followme.data.FollowMeTour
import de.infoware.followmesdkexample.sound.MaptripTTSListener
import de.infoware.followmesdkexample.sound.MaptripTTSManager
import java.io.File

class CompanionMapViewModel : ViewModel(), NavigationListener, MaptripTTSListener,
    FollowMeRouteListener, TaskListener {

    private val TAG = "CompanionMapViewModel"
    private var currentFollowMeRoute: FollowMeRoute? = null
    private var selectedFile: FollowMeTour? = null
    private var isSimulation = false;

    fun startFollowMeTour(filename:String, simulating: Boolean = true) {
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

    override fun taskFinished(task: BaseTask) {
        if(task.returnValue == ApiError.OK) {
            currentFollowMeRoute!!.start(isSimulation)
        }
    }

    override fun taskProgress(p0: BaseTask) {

    }

    override fun followMeAction(p0: FmrActionType?, p1: String?): Boolean {
        Log.e(TAG, "followMeAction")
        Log.e(TAG, p0.toString())
        Log.e(TAG, p1)

        if(p1 != null) {
            MaptripTTSManager.Instance()?.speak(p1, true)
        }
        return true
    }

    override fun followMeEvent(p0: Int, p1: String?): Boolean {
        Log.e(TAG, "followMeEvent")
        Log.e(TAG, p0.toString())
        Log.e(TAG, p1)

        if(p1 != null) {
            MaptripTTSManager.Instance()?.speak(p1, true)
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
            MaptripTTSManager.Instance()?.speak(sentence, true)
        }

        return true
    }

    override fun navigationStarted() {

    }

    override fun crossingInfoReceived(
        p0: String?,
        p1: String?,
        p2: String?,
        p3: CrosswayStreetType?,
        p4: Double,
        p5: Double
    ) {

    }

    override fun destinationReached(p0: Int) {

    }

    override fun speedLimitReceived(p0: Double) {

    }

    override fun ttsInitSuccessful() {
        Log.d(TAG, "ttsInit Successful")
    }

    override fun ttsInitError(missingData: Boolean, notSupported: Boolean) {
        Log.d(TAG, "ttsInit error: MissingData: ${missingData} - notSupportet: ${notSupported}")
    }


}