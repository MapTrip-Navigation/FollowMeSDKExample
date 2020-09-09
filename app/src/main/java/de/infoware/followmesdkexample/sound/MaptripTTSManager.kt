package de.infoware.followmesdkexample.sound

import android.content.Context
import android.util.Log
import java.util.*

class MaptripTTSManager : MaptripTTSListener {
    private var tts: TTSInterface? = null
    private var isTTSInitialized = false
    private var listener: MaptripTTSListener? = null

    private var context: Context? = null

    companion object {

        private var instance: MaptripTTSManager? = null

        fun Instance(): MaptripTTSManager? {
            if (instance == null) {
                instance = MaptripTTSManager()
            }
            return instance
        }

    }
    fun isInitialized(): Boolean {
        return isTTSInitialized
    }

    fun setMute(mute: Boolean) {
        if (tts != null) {
            tts!!.setMute(mute)
        }
    }

    fun isMute() : Boolean {
        return tts!!.isMute()
    }

    fun setListener(l: MaptripTTSListener) {
        listener = l
    }

    fun enableTTS(appContext: Context) {
        if (tts == null) {
            tts = MaptripTTS(appContext, Locale.getDefault(), this)
        }
    }

    fun speak(text: String, queue: Boolean): Boolean {
        return if (tts != null) {
            tts!!.speak(text, queue)
        } else false
    }

    /*
	 * Uninit TTS
	 */
    fun disableTTS() {
        if (tts != null) {
            tts!!.unInit()
            isTTSInitialized = false
            tts = null
        }
    }

    /**
     * Stop Speaking
     */
    fun stop() {
        if (tts != null) {
            tts!!.stop()
        }
    }

    override fun ttsInitSuccessful() {
        isTTSInitialized = true
        //reset context after init because it can be an activity
        context = null
        if (listener != null) {
            listener!!.ttsInitSuccessful()
        }
    }

    override fun ttsInitError(missingData: Boolean, notSupported: Boolean) {
        var notSupported = notSupported

        if (tts == null) {
            notSupported = true
        } else if (tts!!.installIntent == null) {
            notSupported = true
        }

        if (notSupported) {
            Log.e("MapTripTTSManager", "TTS not supported")

        } else if (missingData) {
            Log.e("MapTripTTSManager", "TTS missing DATA")
        }
        //reset context after init because it can be an activity
        context = context!!.applicationContext

        if (listener != null) {
            listener!!.ttsInitError(missingData, notSupported)
        }

    }
}