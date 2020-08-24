package de.infoware.followmesdkexample.companionmap

import androidx.lifecycle.ViewModel
import de.infoware.android.api.NavigationListener
import de.infoware.android.api.RouteComparison
import de.infoware.android.api.Task
import de.infoware.android.api.enums.CrosswayStreetType
import de.infoware.android.api.enums.VehicleWarningType
import de.infoware.followmesdkexample.sound.MaptripTTSListener

class CompanionMapViewModel : ViewModel(), NavigationListener, MaptripTTSListener{

    override fun vehicleWarningReceived(p0: VehicleWarningType?, p1: Double) {
        TODO("Not yet implemented")
    }

    override fun routeUpdate(p0: RouteComparison?) {
        TODO("Not yet implemented")
    }

    override fun rerouting(p0: Task<Void>?) {
        TODO("Not yet implemented")
    }

    override fun laneInfoReceived(p0: String?, p1: String?, p2: String?) {
        TODO("Not yet implemented")
    }

    override fun destinationInfoReceived(p0: Double, p1: Double, p2: Double) {
        TODO("Not yet implemented")
    }

    override fun beforeAdviceStarts(p0: String?, p1: String?, p2: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun navigationStarted() {
        TODO("Not yet implemented")
    }

    override fun crossingInfoReceived(
        p0: String?,
        p1: String?,
        p2: String?,
        p3: CrosswayStreetType?,
        p4: Double,
        p5: Double
    ) {
        TODO("Not yet implemented")
    }

    override fun destinationReached(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun speedLimitReceived(p0: Double) {
        TODO("Not yet implemented")
    }

    override fun ttsInitSuccessful() {
        TODO("Not yet implemented")
    }

    override fun ttsInitError(missingData: Boolean, notSupported: Boolean) {
        TODO("Not yet implemented")
    }
    // TODO: Implement the ViewModel



}