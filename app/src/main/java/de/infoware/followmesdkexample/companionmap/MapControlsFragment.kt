package de.infoware.followmesdkexample.companionmap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.infoware.followmesdkexample.R
import kotlinx.android.synthetic.main.map_controls_fragment.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

class MapControlsFragment : Fragment() {

    private val TAG = "MapControlsFragment"

    companion object {
        fun newInstance() = MapControlsFragment()
    }

    private lateinit var viewModel: CompanionMapViewModel
    private var routeCalculated = false;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.map_controls_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(CompanionMapViewModel::class.java)

        initListener()
    }

    private fun initListener() {

        val crossingPictoFile = Observer<String> { pictoFile ->
            val drawableId = resources.getIdentifier(pictoFile.toLowerCase() + "_black", "drawable", context?.packageName)
            ivPictogram.setImageResource(drawableId)
        }

        val currentStreetObserver = Observer<String> { currentStreetName ->
            if(routeCalculated) tvProgress.text = currentStreetName
        }

        val nextStreetObserver = Observer<String> { nextStreetName ->
            tvCurrentStreet.text = nextStreetName
        }

        val metersToCrossingObserver = Observer<Double> { metersToCrossing ->
            tvMetersToCrossing.text = "$metersToCrossing m"
        }

        val secondsToCrossingObserver = Observer<Int> { secondsToCrossing ->
            tvSecondsToCrossing.text = "$secondsToCrossing s"
        }

        val metersToDestinationObserver = Observer<Int> { metersToDestination ->
            tvMetersToDestination.text = "$metersToDestination" + "m"
        }

        val destinationReachedObserver = Observer<Int> { index ->
            clNavigationInfo.visibility = View.GONE
            tvProgress.visibility = View.GONE
        }

        val progressObserver = Observer<Double> { progress ->
            if(progress >= 100) {
                clNavigationInfo.visibility = View.VISIBLE
                routeCalculated = true
            } else {
                tvProgress.text = "Calculating Route... ${progress}"
            }
        }

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
            viewModel.autozoomToCurrentPosition()
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