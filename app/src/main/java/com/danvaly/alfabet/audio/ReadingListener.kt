package com.danvaly.alfabet.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

/**
 * Listens to the child reading a page ("Citesc eu") with Android's speech recogniser.
 * Must be created and used on the main thread. Every failure is reported through
 * [Callbacks.onFinished] with a friendly Romanian message instead of an exception.
 */
class ReadingListener(context: Context) {

    interface Callbacks {
        /** The recogniser is ready: the child can start reading. */
        fun onReady() {}

        /** Everything heard so far in this session (partial results). */
        fun onHeard(text: String)

        /**
         * The session ended. [texts] holds the final hypotheses (best first, may be empty);
         * [problem] is a child-friendly message when something went wrong, null otherwise.
         */
        fun onFinished(texts: List<String>, problem: String?)
    }

    private val appContext = context.applicationContext
    private var recognizer: SpeechRecognizer? = null
    private var callbacks: Callbacks? = null
    private var active = false

    /** False when the device has no speech recognition service. */
    val isAvailable: Boolean = try {
        SpeechRecognizer.isRecognitionAvailable(appContext)
    } catch (e: Exception) {
        false
    }

    val isListening: Boolean get() = active

    /** Starts listening in Romanian. Returns false if the recogniser could not be started. */
    fun start(callbacks: Callbacks): Boolean {
        cancel()
        if (!isAvailable) return false
        return try {
            val engine = recognizer ?: SpeechRecognizer.createSpeechRecognizer(appContext).also { recognizer = it }
            this.callbacks = callbacks
            engine.setRecognitionListener(listener)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ro-RO")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ro-RO")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, appContext.packageName)
                // Beginners read slowly, with pauses between words: wait longer before stopping.
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2500L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 4000L)
            }
            active = true
            engine.startListening(intent)
            true
        } catch (e: Exception) {
            active = false
            this.callbacks = null
            destroyRecognizer()
            false
        }
    }

    /** Stops listening; the words heard so far still arrive through onFinished. */
    fun stop() {
        if (!active) return
        try {
            recognizer?.stopListening()
        } catch (e: Exception) {
            finish(emptyList(), null)
        }
    }

    /** Stops without reporting anything. */
    fun cancel() {
        active = false
        callbacks = null
        try {
            recognizer?.cancel()
        } catch (e: Exception) {
            // ignore
        }
    }

    /** Call when the screen goes away. */
    fun destroy() {
        cancel()
        destroyRecognizer()
    }

    private fun destroyRecognizer() {
        try {
            recognizer?.destroy()
        } catch (e: Exception) {
            // ignore
        }
        recognizer = null
    }

    private fun finish(texts: List<String>, problem: String?) {
        if (!active) return
        active = false
        val target = callbacks
        callbacks = null
        target?.onFinished(texts, problem)
    }

    private val listener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            if (active) callbacks?.onReady()
        }

        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onPartialResults(partialResults: Bundle?) {
            if (!active) return
            val text = texts(partialResults).firstOrNull() ?: return
            if (text.isNotBlank()) callbacks?.onHeard(text)
        }

        override fun onResults(results: Bundle?) {
            finish(texts(results), null)
        }

        override fun onError(error: Int) {
            finish(emptyList(), messageFor(error))
        }
    }

    private fun texts(bundle: Bundle?): List<String> = try {
        bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.filterNotNull() ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    companion object {
        /** A gentle message for each recogniser error; null means "nothing heard", which is not a problem. */
        fun messageFor(error: Int): String? = when (error) {
            SpeechRecognizer.ERROR_NO_MATCH,
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> null
            SpeechRecognizer.ERROR_NETWORK,
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
            SpeechRecognizer.ERROR_SERVER,
            11 /* ERROR_SERVER_DISCONNECTED */ ->
                "Nu mă pot conecta acum. Încearcă mai târziu."
            SpeechRecognizer.ERROR_AUDIO -> "Nu pot folosi microfonul acum."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Am nevoie de voie să folosesc microfonul."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY,
            10 /* ERROR_TOO_MANY_REQUESTS */ -> "Sunt ocupat. Mai încearcă puțin mai târziu."
            12 /* ERROR_LANGUAGE_NOT_SUPPORTED */,
            13 /* ERROR_LANGUAGE_UNAVAILABLE */ -> "Nu știu încă să ascult în limba română pe acest dispozitiv."
            else -> "Nu te-am putut asculta. Mai încearcă."
        }
    }
}
