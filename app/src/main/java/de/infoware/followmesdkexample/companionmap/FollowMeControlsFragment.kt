package de.infoware.followmesdkexample.companionmap

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.infoware.android.api.enums.FmrActionType
import de.infoware.followmesdkexample.R
import de.infoware.followmesdkexample.databinding.FollowMeControlsFragmentBinding

/**
 *  Fragment for the FollowMe specific control- and UI-elements
 *  Handles the collection-state and the image resources for the blinking arrows
 */
class FollowMeControlsFragment : Fragment() {

    companion object {
        fun newInstance() = FollowMeControlsFragment()
    }

    private var _binding: FollowMeControlsFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: CompanionMapViewModel

    // constant for the left arrow
    private val leftArrowColored = R.drawable.button_fmr_collect_left_dark
    private val leftArrowGrey = R.drawable.button_fmr_collect_left_light
    // constant for the right arrow
    private val rightArrowColored = R.drawable.button_fmr_collect_right_dark
    private val rightArrowGrey = R.drawable.button_fmr_collect_right_light

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FollowMeControlsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // the ViewModelProvider provides a new instance of the ViewModel if there is none, and uses the existing instance of the ViewModel if possible
        viewModel = ViewModelProvider(requireActivity())[CompanionMapViewModel::class.java]

        initListener()
    }

    private fun initListener() {

        // Observer for the collection-state
        // changes the visibility of the collection-arrows accordingly
        val collectionStateObserver = Observer<FmrActionType> { collectionState ->
            when (collectionState) {

                FmrActionType.COLLECT_BOTH -> {
                    binding.ivCollectLeft.visibility = View.VISIBLE
                    binding.ivCollectRight.visibility = View.VISIBLE
                }

                FmrActionType.COLLECT_LEFT -> {
                    binding.ivCollectLeft.visibility = View.VISIBLE
                    binding.ivCollectRight.visibility = View.GONE
                }

                FmrActionType.COLLECT_RIGHT -> {
                    binding.ivCollectLeft.visibility = View.GONE
                    binding.ivCollectRight.visibility = View.VISIBLE
                }

                FmrActionType.COLLECT_UNKNOWN -> {
                    binding.ivCollectLeft.visibility = View.GONE
                    binding.ivCollectRight.visibility = View.GONE
                }

                FmrActionType.TRANSFER_START -> {
                    binding.ivCollectLeft.visibility = View.GONE
                    binding.ivCollectRight.visibility = View.GONE
                }

                else -> {

                }
            }
        }
        this.viewModel.currentCollectionState.observe(this.viewLifecycleOwner, collectionStateObserver)

        // Observer for the blink-timer
        // alternates the image resources
        val blinkObserver = Observer<Boolean> { blink ->
            if(blink) {
                binding.ivCollectLeft.setImageResource(leftArrowColored)
                binding.ivCollectRight.setImageResource(rightArrowColored)
            } else {
                binding.ivCollectLeft.setImageResource(leftArrowGrey)
                binding.ivCollectRight.setImageResource(rightArrowGrey)
            }
        }
        this.viewModel.switchCollectionImage.observe(this.viewLifecycleOwner, blinkObserver)
    }

}