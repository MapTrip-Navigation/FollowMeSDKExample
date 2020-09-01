package de.infoware.followmesdkexample.mainmenu

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import de.infoware.followmesdkexample.MainActivity
import de.infoware.followmesdkexample.R
import de.infoware.followmesdkexample.followme.FollowMeFileRepo
import kotlinx.android.synthetic.main.main_menu_fragment.*

class MainMenuFragment : Fragment() {

    companion object {
        fun newInstance() = MainMenuFragment()
    }

    private lateinit var viewModel: MainMenuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_menu_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainMenuViewModel::class.java)

        initListener()
    }

    private fun initListener() {
        btnShowList.setOnClickListener { (activity as MainActivity).switchToFilelistFragment() }
    }

}