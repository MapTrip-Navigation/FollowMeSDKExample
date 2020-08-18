package de.infoware.followmesdkexample.companionmap

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.infoware.followmesdkexample.R

class CompanionMapFragment : Fragment() {

    companion object {
        fun newInstance() = CompanionMapFragment()
    }

    private lateinit var viewModel: CompanionMapViewModel

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