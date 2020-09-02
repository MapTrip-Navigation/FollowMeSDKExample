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
import kotlinx.android.synthetic.main.follow_me_controls_fragment.*

/**
 *  Fragment for the FollowMe specific control- and UI-elements
 *  Handles the collection-state and the image resources for the blinking arrows
 */
class FollowMeControlsFragment : Fragment() {

    companion object {
        fun newInstance() = FollowMeControlsFragment()
    }

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
        return inflater.inflate(R.layout.follow_me_controls_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // the ViewModelProvider provides a new instance of the ViewModel if there is none, and uses the existing instance of the ViewModel if possible
        viewModel = ViewModelProvider(requireActivity()).get(CompanionMapViewModel::class.java)

        initListener()
    }

    private fun initListener() {

        // Observer for the collection-state
        // changes the visibility of the collection-arrows accordingly
        val collectionStateObserver = Observer<FmrActionType> { collectionState ->
            when (collectionState) {

                FmrActionType.COLLECT_BOTH -> {
                    ivCollectLeft.visibility = View.VISIBLE
                    ivCollectRight.visibility = View.VISIBLE
                }

                FmrActionType.COLLECT_LEFT -> {
                    ivCollectLeft.visibility = View.VISIBLE
                    ivCollectRight.visibility = View.GONE
                }

                FmrActionType.COLLECT_RIGHT -> {
                    ivCollectLeft.visibility = View.GONE
                    ivCollectRight.visibility = View.VISIBLE
                }

                FmrActionType.COLLECT_UNKNOWN -> {
                    ivCollectLeft.visibility = View.GONE
                    ivCollectRight.visibility = View.GONE
                }

                FmrActionType.TRANSFER_START -> {
                    ivCollectLeft.visibility = View.GONE
                    ivCollectRight.visibility = View.GONE
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
                ivCollectLeft.setImageResource(leftArrowColored)
                ivCollectRight.setImageResource(rightArrowColored)
            } else {
                ivCollectLeft.setImageResource(leftArrowGrey)
                ivCollectRight.setImageResource(rightArrowGrey)
            }
        }
        this.viewModel.switchCollectionImage.observe(this.viewLifecycleOwner, blinkObserver)
    }

}