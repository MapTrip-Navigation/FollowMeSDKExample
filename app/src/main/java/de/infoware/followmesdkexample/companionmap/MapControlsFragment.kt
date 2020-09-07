package de.infoware.followmesdkexample.companionmap

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.infoware.followmesdkexample.R
import kotlinx.android.synthetic.main.map_controls_fragment.*
import java.util.*

/**
 *  Fragment for the Control-Elements and UI besides the map itself
 *  E.g. Buttons, TextViews for Navigation-Info, etc.
 *  Handles button-clicks and displays information
 */
class MapControlsFragment : Fragment() {

    private val TAG = "MapControlsFragment"

    companion object {
        fun newInstance() = MapControlsFragment()
    }

    // the ViewModel used for this Fragment
    private lateinit var viewModel: CompanionMapViewModel
    // boolean to check if the route is done calculating
    private var routeCalculated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.map_controls_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // the ViewModelProvider provides a new instance of the ViewModel if there is none, and uses the existing instance of the ViewModel if possible
        viewModel = ViewModelProvider(requireActivity()).get(CompanionMapViewModel::class.java)

        initListener()
    }

    /**
     *  Initialises the Listener for ViewModel Observables and Button-Clicks
     */
    @SuppressLint("SetTextI18n")
    private fun initListener() {

        // Observer for the crossingInfoReceived Callback: Pictogram filename
        // Sets the ImageResource for the next crossing
        val crossingPictoFile = Observer<String> { pictoFile ->
            val drawableId = resources.getIdentifier(pictoFile.toLowerCase(Locale.ROOT) + "_black", "drawable", context?.packageName)
            ivPictogram.setImageResource(drawableId)
        }

        // Observer for the crossingInfoReceived Callback: actualStreetName
        // Sets the tvProgress TextView to the currentStreetName, if the route has finished calculating
        val currentStreetObserver = Observer<String> { currentStreetName ->
            if(routeCalculated) tvProgress.text = currentStreetName
        }

        // Observer for the crossingInfoReceived Callback: nextStreetName
        val nextStreetObserver = Observer<String> { nextStreetName ->
            tvNextStreet.text = nextStreetName
        }

        // Observer for the crossingInfoReceived Callback: metersToCrossing
        val metersToCrossingObserver = Observer<Int> { metersToCrossing ->
            tvMetersToCrossing.text = "$metersToCrossing m"
        }

        // Observer for the crossingInfoReceived Callback: secondsToCrossing
        val secondsToCrossingObserver = Observer<Int> { secondsToCrossing ->
            tvSecondsToCrossing.text = "$secondsToCrossing s"
        }

        // Observer for the destinationInfoReceived Callback: metersToDestination
        val metersToDestinationObserver = Observer<Int> { metersToDestination ->
            tvMetersToDestination.text = "$metersToDestination" + "m"
        }

        // Observer for the destinationReached Callback: int currently not used (index of the reached destination, always 0 if there is only one)
        // Hides the Navigation-Information when the destination is reached
        val destinationReachedObserver = Observer<Int> { _ ->
            clNavigationInfo.visibility = View.GONE
            tvProgress.visibility = View.GONE
        }

        // Observer for the taskProgress Callback
        // Shows the current progress of the task
        val progressObserver = Observer<Double> { progress ->
            if(progress >= 100) {
                clNavigationInfo.visibility = View.VISIBLE
                routeCalculated = true
            } else {
                tvProgress.text = "Calculating Route.. ${progress}"
            }
        }

        // Observer for the currentMuteOption
        // Sets the Button BackgroundResource
        val muteObserver = Observer<Boolean> { isMute ->
            if(isMute) {
                btnChangeMute.setBackgroundResource(R.drawable.button_mute)
            } else {
                btnChangeMute.setBackgroundResource(R.drawable.button_no_mute)
            }
        }

        viewModel.pictoFileName.observe(this.viewLifecycleOwner, crossingPictoFile)

        viewModel.currentStreetName.observe(this.viewLifecycleOwner, currentStreetObserver)
        viewModel.nextStreetName.observe(this.viewLifecycleOwner, nextStreetObserver)
        viewModel.metersToCrossing.observe(this.viewLifecycleOwner, metersToCrossingObserver)
        viewModel.secondsToCrossing.observe(this.viewLifecycleOwner, secondsToCrossingObserver)

        viewModel.metersToDestination.observe(this.viewLifecycleOwner, metersToDestinationObserver)
        viewModel.destinationReached.observe(this.viewLifecycleOwner, destinationReachedObserver)

        viewModel.progress.observe(this.viewLifecycleOwner, progressObserver)

        viewModel.currentMuteOption.observe(this.viewLifecycleOwner, muteObserver)

        btnCenterMap.setOnClickListener {
                v ->
            viewModel.autoZoomToCurrentPosition()
        }

        btnChangePerspective.setOnClickListener {
                v ->
            viewModel.switchPerspective()
        }

        btnChangeMute.setOnClickListener {
                v ->
            viewModel.switchMuteOption()
        }
    }

}