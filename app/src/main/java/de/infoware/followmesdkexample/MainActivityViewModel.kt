package de.infoware.followmesdkexample

import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.concurrent.schedule

/**
 *  ViewModel for the MainActivity
 *  Currently responsible for the timeout between the initialization of the API and the fragment-change,
 *  and saving the currently used fragment
 */
class MainActivityViewModel : ViewModel() {

    enum class Fragment(val index : Int) {
        MainFragment(0),
        MainMenuFragment(1),
        FileListFragment(2),
        CompanionMapFragment(3)
    }

    private var currentFragment : Fragment = Fragment.MainFragment

    val timer = MutableLiveData<Any>()

    fun initSplashScreenTimer() {
        Timer("SplashScreen", false).schedule(1000) {
            timer.postValue(Any())
        }
    }

    fun setCurrentFragment(fragmentName:Fragment) {
        this.currentFragment = fragmentName
    }

    fun getCurrentFragment() : Fragment {
        return this.currentFragment
    }

}