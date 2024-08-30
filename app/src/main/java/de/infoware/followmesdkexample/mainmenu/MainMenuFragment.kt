package de.infoware.followmesdkexample.mainmenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import de.infoware.followmesdkexample.MainActivity
import de.infoware.followmesdkexample.databinding.MainMenuFragmentBinding

/**
 *  Fragment for the MainMenu (currently only "List" Button available)
 */
class MainMenuFragment : Fragment() {

    companion object {
        fun newInstance() = MainMenuFragment()
    }

    private var _binding: MainMenuFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainMenuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainMenuFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // the ViewModelProvider provides a new instance of the ViewModel if there is none, and uses the existing instance of the ViewModel if possible
        viewModel = ViewModelProvider(requireActivity())[MainMenuViewModel::class.java]

        initListener()
    }

    private fun initListener() {
        binding.btnShowList.setOnClickListener { (requireActivity() as MainActivity).switchToFilelistFragment() }
    }

}