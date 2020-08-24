package de.infoware.followmesdkexample.sound

import android.content.Intent

interface TTSInterface {

    val installIntent: Intent?

    fun speak(text: String, queue: Boolean): Boolean

    fun stop()

    fun setMute(mute: Boolean)

    fun unInit()
}