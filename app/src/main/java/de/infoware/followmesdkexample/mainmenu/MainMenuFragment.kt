package de.infoware.followmesdkexample.mainmenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import de.infoware.followmesdkexample.MainActivity
import de.infoware.followmesdkexample.R
import kotlinx.android.synthetic.main.main_menu_fragment.*

/**
 *  Fragment for the MainMenu (currently only "List" Button available)
 */
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

        // the ViewModelProvider provides a new instance of the ViewModel if there is none, and uses the existing instance of the ViewModel if possible
        viewModel = ViewModelProvider(requireActivity()).get(MainMenuViewModel::class.java)

        initListener()
    }

    private fun initListener() {
        btnShowList.setOnClickListener { (activity as MainActivity).switchToFilelistFragment() }
    }

}