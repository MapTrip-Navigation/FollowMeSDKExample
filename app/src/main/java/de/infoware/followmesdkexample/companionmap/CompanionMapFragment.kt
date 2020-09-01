package de.infoware.followmesdkexample.companionmap

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.infoware.android.api.IwMapView
import de.infoware.android.api.Mapviewer
import de.infoware.android.api.Navigation
import de.infoware.android.api.enums.MapPerspective
import de.infoware.followmesdkexample.R
import de.infoware.followmesdkexample.sound.MaptripTTSManager
import kotlinx.android.synthetic.main.companion_map_fragment.*

class CompanionMapFragment : Fragment() {

    private val TAG = "CompanionMapFragment"

    companion object {
        fun newInstance() = CompanionMapFragment()
    }

    private lateinit var viewModel: CompanionMapViewModel
    private lateinit var selectedFileName: String
    private var isSimulating: Boolean? = null
    private lateinit var mapView: IwMapView
    private lateinit var mapViewer: Mapviewer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("selectedFile") { _, bundle ->
            selectedFileName = if(bundle.getString("selectedFileBundle") != null) bundle.getString("selectedFileBundle")!! else ""
            isSimulating = bundle.getBoolean("simulate")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.companion_map_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(CompanionMapViewModel::class.java)

        mapView = map

        initListener()

        Navigation.registerNavigationListener(viewModel)
        MaptripTTSManager.Instance()?.setListener(viewModel)
        MaptripTTSManager.Instance()?.enableTTS(requireActivity().applicationContext)
    }

    private fun initListener() {
        val perspectiveListener = Observer<MapPerspective> { newPerspective ->
            mapViewer.perspective = newPerspective
        }

        viewModel.currentPerspective.observe(this.viewLifecycleOwner, perspectiveListener)

        val autoZoomObserver = Observer<Any> {
            mapViewer.resumeLocationTracking()
        }

        viewModel.autozoomToPosition.observe(this.viewLifecycleOwner, autoZoomObserver)

        mapView.setOnMapviewerReadyListener {
            mapViewer = mapView.mapviewer
            viewModel.initPerspective(mapViewer.perspective)
            if(isSimulating != null) {
                viewModel.startFollowMeTour(selectedFileName, isSimulating!!)
            } else {
                viewModel.startFollowMeTour(selectedFileName)
            }
        }
    }

}