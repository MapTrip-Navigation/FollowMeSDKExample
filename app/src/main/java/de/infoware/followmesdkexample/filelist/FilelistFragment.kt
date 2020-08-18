package de.infoware.followmesdkexample.filelist

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.infoware.followmesdkexample.R

class FilelistFragment : Fragment() {

    companion object {
        fun newInstance() = FilelistFragment()
    }

    private lateinit var viewModel: FilelistViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.filelist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FilelistViewModel::class.java)
        // TODO: Use the ViewModel
    }

}