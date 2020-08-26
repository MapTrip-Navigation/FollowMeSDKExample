package de.infoware.followmesdkexample.companionmap

import android.util.Log
import de.infoware.android.api.*
import de.infoware.android.api.enums.ApiError
import de.infoware.android.api.enums.DayNightMode
import de.infoware.android.api.enums.MapPerspective

class MapViewTester {
    private var userWpt: Waypoint? = null

    companion object {
        private val TAG = "MapviewerTester"
    }

    /**
     * switches the perspective modes
     * @param mapView
     */
    fun switchPerspective(mapView: Mapviewer) {
        val perspective = mapView.perspective
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
        // activate chosen perspective
        mapView.perspective = newPerspective
    }

    fun changeDayNighMode() {
        val dn = Mapviewer.getActiveDayNightStatus()

        when (dn) {
            DayNightMode.DAY_MODE -> Mapviewer.setDayNightMode(DayNightMode.NIGHT_MODE)

            DayNightMode.NIGHT_MODE -> Mapviewer.setDayNightMode(DayNightMode.DAY_MODE)
        }

    }

    /**
     * Sets the viewing center to infoware and deactivates automatic car centering
     */
    fun setViewingCenter(mapviewer: Mapviewer): ApiError {
        var retVal = ApiError.OK

        // WGS coordinate of Bonn Riemenschneiderstr. 11
        val posWGS84 = Position(7.140774, 50.701506)
        // Convert WGS coordinate to internal coordinates
        val posInternal = Projection.WGS84ToInternalCoordinates(posWGS84)

        // set the new map center to the calculated internal coordinate
        retVal = mapviewer.setCenterPoint(posInternal)
        return retVal
    }

    /**
     * zooms at 4000 meters height
     */
    fun setViewingDistance(mapviewer: Mapviewer): ApiError {
        return mapviewer.setViewingDistance(4000.0, false)
    }

    fun addUserPoi(mapviewer: Mapviewer) {
        var retVal = ApiError.OK

        // if already shown, hide the poi
        removeUserPoi(mapviewer)

        // create the coordinate wapyoint to set the icon
        userWpt = Waypoint()
        val infowareWGS = Position(6.957987, 50.941772)
        val projPos = Projection.WGS84ToInternalCoordinates(infowareWGS)
        retVal = userWpt!!.setPosition(projPos)

        // specify the icon
        val filename = ApiHelper.Instance().resourcePath + "poi_icons/pin_blau.png"

        userWpt!!.poiIcon = filename

        mapviewer.showWaypoint(userWpt, true)

        mapviewer.centerPoint = projPos
    }

    fun removeUserPoi(mapviewer: Mapviewer) {
        if (userWpt != null) {
            mapviewer.showWaypoint(userWpt, false)
            userWpt = null
        }
    }
}