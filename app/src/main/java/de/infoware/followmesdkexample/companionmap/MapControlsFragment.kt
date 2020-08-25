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

        val currentStreetObserver = Observer<String> { currentStreetName ->
            tvCurrentStreet.text = currentStreetName
        }

        val nextStreetObserver = Observer<String> { nextStreetName ->

        }

        val metersToCrossingObserver = Observer<Double> { metersToCrossing ->

        }

        val secondsToCrossingObserver = Observer<Double> { secondsToCrossing ->

        }

        val progressObserver = Observer<Double> { progress ->
            if(progress >= 100) {
                tvProgress.visibility = View.GONE
            } else {
                tvProgress.visibility = View.VISIBLE
            }  // progress.roundToInt()
            tvProgress.text = "Calculating Route... ${progress}"
        }

        viewModel.currentStreetName.observe(this.viewLifecycleOwner, currentStreetObserver)
        viewModel.nextStreetName.observe(this.viewLifecycleOwner, nextStreetObserver)
        viewModel.metersToCrossing.observe(this.viewLifecycleOwner, metersToCrossingObserver)
        viewModel.secondsToCrossing.observe(this.viewLifecycleOwner, secondsToCrossingObserver)

        viewModel.progress.observe(this.viewLifecycleOwner, progressObserver)

        btnCenterMap.setOnClickListener {
                v ->
            viewModel.autozoomToCurrentPosition()
        }

        btnChangePerspective.setOnClickListener {
            v ->
            viewModel.switchPerspective()
        }
    }

}