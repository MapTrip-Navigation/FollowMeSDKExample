package de.infoware.followmesdkexample.companionmap

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import de.infoware.followmesdkexample.R
import de.infoware.followmesdkexample.followme.data.FollowMeTour

class CompanionMapFragment : Fragment() {

    private val TAG = "CompanionMapFragment"

    companion object {
        fun newInstance() = CompanionMapFragment()
    }

    private lateinit var viewModel: CompanionMapViewModel
    private lateinit var selectedFollowMeFile: FollowMeTour

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("selectedFile") { key, bundle ->
            Log.e(TAG, "File: " + bundle.getString("selectedFileBundle"))
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
        viewModel = ViewModelProviders.of(this).get(CompanionMapViewModel::class.java)
        // TODO: Use the ViewModel
    }

}