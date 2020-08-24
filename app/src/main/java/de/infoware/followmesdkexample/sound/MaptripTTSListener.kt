package de.infoware.followmesdkexample.sound

interface MaptripTTSListener {

    fun ttsInitSuccessful();

    fun ttsInitError (missingData: Boolean, notSupported: Boolean);
}