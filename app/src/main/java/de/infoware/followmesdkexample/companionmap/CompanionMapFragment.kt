package de.infoware.followmesdkexample.companionmap

import androidx.lifecycle.ViewModelProviders
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
import de.infoware.followmesdkexample.R
import de.infoware.followmesdkexample.sound.MaptripTTSManager
import kotlinx.android.synthetic.main.companion_map_fragment.*
import kotlin.properties.Delegates

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

    private val bundleObserver: MutableLiveData<String> = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener("selectedFile") { key, bundle ->
            selectedFileName = if(bundle.getString("selectedFileBundle") != null) bundle.getString("selectedFileBundle")!! else ""
            isSimulating = bundle.getBoolean("simulate")
            bundleObserver.postValue(selectedFileName)
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
        val filenameBundleObserver = Observer<String> { filename ->
            Log.e(TAG, "filename gotten")
        }

        this.bundleObserver.observe(this.viewLifecycleOwner, filenameBundleObserver)

        mapView.setOnMapviewerReadyListener {
            Log.e(TAG, "mapviewer ready")
            mapViewer = mapView.mapviewer
            viewModel.setMapViewer(mapViewer)
            if(isSimulating != null) {
                viewModel.startFollowMeTour(selectedFileName, isSimulating!!)
            } else {
                viewModel.startFollowMeTour(selectedFileName)
            }
        }
    }

}