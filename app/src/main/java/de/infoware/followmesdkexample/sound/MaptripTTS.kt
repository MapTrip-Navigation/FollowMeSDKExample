package de.infoware.followmesdkexample.sound

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class MaptripTTS : TTSInterface{
    private val TAG = "MaptripTTS"

    private lateinit var tts: TextToSpeech
    private lateinit var listener: MaptripTTSListener
    private var ready = false
    private lateinit var context: Context
    private var isMute = false

    constructor (context: Context, locale: Locale?, listen: MaptripTTSListener) {
        listener = listen
        this.context = context
        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
            ready = false
            if (status == TextToSpeech.SUCCESS) {
                var defaultOrPassedIn = locale
                if (locale == null) {
                    defaultOrPassedIn = Locale.getDefault()
                }
                // check if language is available
                when (tts.isLanguageAvailable(defaultOrPassedIn)) {
                    TextToSpeech.LANG_AVAILABLE,
                    TextToSpeech.LANG_COUNTRY_AVAILABLE,
                    TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> {
                        Log.d(TAG, "supported")
                        tts.language = locale
                        listener.ttsInitSuccessful()
                        ready = true
                    }

                    TextToSpeech.LANG_MISSING_DATA -> {
                        Log.d(TAG, "require data...")
                        listener.ttsInitError(true, false)
                    }

                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        Log.d(TAG, "not supported")
                        listener.ttsInitError(false, true)
                    }
                }
            }
        })
    }

    override fun speak(text: String, queue: Boolean): Boolean {
        if (isMute) {
            return false
        }

        if (!ready) {
            return false
        }

        var ret = TextToSpeech.ERROR

        val splitText = text.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        try {
            for (i in splitText.indices) {

                if (i == 0) {
                    ret = ttsSpeak(splitText[i], if (queue) TextToSpeech.QUEUE_ADD else
                        TextToSpeech.QUEUE_FLUSH)
                } else {
                    if (splitText[i].contains(",")) {
                        val addSplitText = splitText[i].split(
                            ",".toRegex()).dropLastWhile { it.isEmpty()
                        }.toTypedArray()
                        for (j in addSplitText.indices) {
                            ret = ttsSpeak(addSplitText[j], TextToSpeech.QUEUE_ADD)
                            ttsSilence(50, TextToSpeech.QUEUE_ADD)
                        }
                    } else {
                        ret = ttsSpeak(splitText[i], TextToSpeech.QUEUE_ADD)
                    }
                }
                ttsSilence(250, TextToSpeech.QUEUE_ADD)
            }
        } catch (exc: Exception) {
        }

        return ret == TextToSpeech.SUCCESS
    }

    private fun ttsSpeak(text: String, queueMode: Int): Int {
        return tts.speak(text, queueMode, null)
    }

    private fun ttsSilence(delay: Int, queueMode: Int): Int {
        return tts.playSilence(delay.toLong(), queueMode, null)
    }

    override fun stop() {
        if (!ready) {
            return
        }

        try {
            tts.stop()
        } catch (exc: Exception) {
        }

    }

    override fun setMute(mute: Boolean) {
        isMute = mute
    }

    override fun unInit() {
        stop()
        try {
            tts.shutdown()
        } catch (exc: Exception) {
        }

    }

    override val installIntent: Intent?
        get() {
            val installIntent = Intent()
            installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val pm = context.packageManager
            val resolveInfo = pm.resolveActivity(installIntent, PackageManager.MATCH_DEFAULT_ONLY)

            return if (resolveInfo == null) {
                // Not able to find the activity which should be started for this intent
                null
            } else {
                installIntent
            }
        }
}