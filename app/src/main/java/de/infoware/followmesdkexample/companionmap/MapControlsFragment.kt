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
import de.infoware.followmesdkexample.databinding.MapControlsFragmentBinding
import java.util.*

/**
 *  Fragment for the Control-Elements and UI besides the map itself
 *  E.g. Buttons, TextViews for Navigation-Info, etc.
 *  Handles button-clicks and displays information
 */
class MapControlsFragment : Fragment() {

    companion object {
        fun newInstance() = MapControlsFragment()
        private const val TAG = "MapControlsFragment"
    }

    private var _binding: MapControlsFragmentBinding? = null
    private val binding get() = _binding!!

    // the ViewModel used for this Fragment
    private lateinit var viewModel: CompanionMapViewModel
    // boolean to check if the route is done calculating
    private var routeCalculated = false
    // boolean to save if the route is already finished (for orientation-changes)
    private var routeFinished = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MapControlsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CompanionMapViewModel::class.java]

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
            binding.ivPictogram.setImageResource(drawableId)
        }

        // Observer for the crossingInfoReceived Callback: actualStreetName
        // Sets the tvProgress TextView to the currentStreetName, if the route has finished calculating
        val currentStreetObserver = Observer<String> { currentStreetName ->
            if(routeCalculated && !routeFinished) binding.tvProgress.text = currentStreetName
        }
        // Observer for the crossingInfoReceived Callback: nextStreetName
        val nextStreetObserver = Observer<String> { nextStreetName ->
            binding.tvNextStreet.text = nextStreetName
        }
        // Observer for the crossingInfoReceived Callback: metersToCrossing
        val metersToCrossingObserver = Observer<Int> { metersToCrossing ->
            if(metersToCrossing >= 0) binding.tvMetersToCrossing.text = "$metersToCrossing m"
        }
        // Observer for the crossingInfoReceived Callback: secondsToCrossing
        val secondsToCrossingObserver = Observer<Int> { secondsToCrossing ->
            if(secondsToCrossing >= 0) binding.tvSecondsToCrossing.text = "$secondsToCrossing s"
        }

        // Observer for the destinationInfoReceived Callback: metersToDestination
        val metersToDestinationObserver = Observer<Int> { metersToDestination ->
            if(metersToDestination >= 0) binding.tvMetersToDestination.text = "$metersToDestination" + "m"
        }
        // Obsever for the destinationInfoReceived Callback: secondsToDestination
        val secondsToDestinationObserver = Observer<Int> { secondsToDestination ->
            //if(secondsToDestination >= 0)
        }

        // Observer for the destinationReached Callback: int currently not used (index of the reached destination, always 0 if there is only one)
        // Hides the Navigation-Information when the destination is reached
        val destinationReachedObserver = Observer<Int> {
            this.routeFinished = true
            binding.clNavigationInfo.visibility = View.GONE
            binding.tvProgress.visibility = View.GONE
        }

        // Observer for the taskProgress Callback
        // Shows the current progress of the task
        val progressObserver = Observer<Int> { progress ->
            if(progress >= 100) {
                routeCalculated = true
            } else {
                binding.tvProgress.text = "Calculating Route.. ${progress}%"
            }
        }

        // Observer for the task-state
        // Sets the Navigation-Info to visible, after the route is calculated
        val taskFinishedObserver = Observer<Any> {
            if(!routeFinished) binding.clNavigationInfo.visibility = View.VISIBLE
        }

        // Observer for the currentMuteOption
        // Sets the Button BackgroundResource
        val muteObserver = Observer<Boolean> { isMute ->
            if(isMute) {
                binding.btnChangeMute.setBackgroundResource(R.drawable.button_mute)
            } else {
                binding.btnChangeMute.setBackgroundResource(R.drawable.button_no_mute)
            }
        }

        viewModel.pictoFileName.observe(this.viewLifecycleOwner, crossingPictoFile)

        viewModel.currentStreetName.observe(this.viewLifecycleOwner, currentStreetObserver)
        viewModel.nextStreetName.observe(this.viewLifecycleOwner, nextStreetObserver)
        viewModel.metersToCrossing.observe(this.viewLifecycleOwner, metersToCrossingObserver)
        viewModel.secondsToCrossing.observe(this.viewLifecycleOwner, secondsToCrossingObserver)

        viewModel.metersToDestination.observe(this.viewLifecycleOwner, metersToDestinationObserver)
        viewModel.secondsToDestination.observe(this.viewLifecycleOwner, secondsToDestinationObserver)
        viewModel.destinationReached.observe(this.viewLifecycleOwner, destinationReachedObserver)

        viewModel.progress.observe(this.viewLifecycleOwner, progressObserver)
        viewModel.taskFinished.observe(this.viewLifecycleOwner, taskFinishedObserver)

        viewModel.currentMuteOption.observe(this.viewLifecycleOwner, muteObserver)

        binding.btnCenterMap.setOnClickListener {
                v ->
            viewModel.autoZoomToCurrentPosition()
        }

        binding.btnChangePerspective.setOnClickListener {
                v ->
            viewModel.switchPerspective()
        }

        binding.btnChangeMute.setOnClickListener {
                v ->
            viewModel.switchMuteOption()
        }
    }

}