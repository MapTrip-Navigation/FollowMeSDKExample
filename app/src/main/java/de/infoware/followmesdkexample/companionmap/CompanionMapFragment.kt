package de.infoware.followmesdkexample.companionmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.infoware.android.api.IwMapView
import de.infoware.android.api.Mapviewer
import de.infoware.android.api.Navigation
import de.infoware.android.api.enums.MapPerspective
import de.infoware.followmesdkexample.databinding.CompanionMapFragmentBinding
import de.infoware.followmesdkexample.sound.MaptripTTSManager

/**
 *  Fragment for the CompanionMap
 *  includes the mapviewer and the map-element
 *  Listens to changes about the map (perspective changes, setting the center, etc.)
 */
class CompanionMapFragment : Fragment() {

    companion object {
        fun newInstance() = CompanionMapFragment()
        private const val TAG = "CompanionMapFragment"
    }

    private val binding: CompanionMapFragmentBinding get() = _binding!!

    var _binding : CompanionMapFragmentBinding? = null


    // The ViewModel used for this Fragment
    private lateinit var viewModel: CompanionMapViewModel
    // The Filename which was selected in the FileListFragment
    private lateinit var selectedFileName: String
    // Option if the FollowMeTour should start as a simulation
    private var isSimulating: Boolean? = null
    // The Map-Element
    private lateinit var mapView: IwMapView
    private lateinit var mapViewer: Mapviewer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         *  Getting the extra from the previous fragment (FileListFragment) and sets the selected file & options accordingly
         */
        setFragmentResultListener("selectedFile") { _, bundle ->
            selectedFileName = if(bundle.getString("selectedFileBundle") != null) bundle.getString("selectedFileBundle")!! else ""
            isSimulating = bundle.getBoolean("simulate")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CompanionMapFragmentBinding.inflate(inflater, container, false)
        mapView = binding.map

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[CompanionMapViewModel::class.java]

        initListener()

        /**
         *  Registers the ViewModel as the Listener for Navigation and Text-to-Speech Events
         *  ViewModel needs to implement the NavigationListener & MapTripTTSListener Interface
         *  TTS needs to be enabled explicitly, and can be disabled in the same way
         */
        Navigation.addNavigationListener(viewModel)
        MaptripTTSManager.Instance()?.setListener(viewModel)
        MaptripTTSManager.Instance()?.enableTTS(requireActivity().applicationContext)
    }

    /**
     *  Initialises the Listener for ViewModel Observables and MapViewer-Ready
     */
    private fun initListener() {

        /**
         *  Observer for changes on the MapPerspective
         *  When a new perspective is received, it gets set via the IwMapViewer
         */
        val perspectiveListener = Observer<MapPerspective> { newPerspective ->
            if(this::mapViewer.isInitialized) {
                mapViewer.perspective = newPerspective
            }
        }
        viewModel.currentPerspective.observe(this.viewLifecycleOwner, perspectiveListener)

        /**
         *  Observer for Click-Event of the 'Center to Vehicle'-Button in the MapControlsFragment
         *  Gets called anytime the Button is pressed and passed through the ViewModel
         */
        val autoZoomObserver = Observer<Any> {
            if(this::mapViewer.isInitialized) {
                mapViewer.resumeLocationTracking()
            }
        }
        viewModel.autoZoomToPosition.observe(this.viewLifecycleOwner, autoZoomObserver)

        /**
         *  Listener which gets called as soon as the MapViewer is ready to use
         */
        mapView.setOnMapviewerReadyListener {
            mapViewer = mapView.mapviewer
            viewModel.initPerspective(mapViewer.perspective)
            if(!viewModel.navigationRunning && this::selectedFileName.isInitialized) {
                if(isSimulating != null) {
                    viewModel.startFollowMeTour(selectedFileName, isSimulating!!)
                } else {
                    viewModel.startFollowMeTour(selectedFileName)
                }
            }
        }
    }

}