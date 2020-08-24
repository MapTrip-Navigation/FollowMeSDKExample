package de.infoware.followmesdkexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import de.infoware.followmesdkexample.companionmap.CompanionMapFragment
import de.infoware.followmesdkexample.dialog.DialogFragment
import de.infoware.followmesdkexample.filelist.FilelistFragment
import de.infoware.followmesdkexample.mainmenu.MainMenuFragment
import de.infoware.followmesdkexample.ui.main.MainFragment
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private val TAG = "FollowMeSDKExample"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        val viewModel: MainActivityViewModel by viewModels()
        Log.e(TAG, viewModel.alreadyInitalized.toString())

        initListener(viewModel)

        viewModel.initSDK(applicationContext)
    }

    private fun initListener(viewModel:MainActivityViewModel) {
        val initObserver = Observer<Boolean> { isInitialized ->
            if(isInitialized) {
                switchToMainMenuFragment()
            }
        }

        viewModel.isInitialized.observe(this, initObserver)
    }

    fun switchToFilelistFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, FilelistFragment.newInstance())
            .commitNow()
    }

    fun switchToCompanionMapFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CompanionMapFragment.newInstance())
            .commitNow()
    }

    fun switchToDialogFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, DialogFragment.newInstance())
            .commitNow()
    }

    fun switchToMainMenuFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainMenuFragment.newInstance())
            .commitNow()
    }
}