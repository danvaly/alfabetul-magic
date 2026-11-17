package com.danvaly.alfabet.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.danvaly.alfabet.data.RomanianAlphabet
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

/** Whether a Romanian text-to-speech voice can be used. */
enum class VoiceState { LOADING, READY, MISSING }

class SoundManager(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false

    private val _voiceState = MutableStateFlow(VoiceState.LOADING)

    /** LOADING until the engine answers, then READY or MISSING (no engine / no Romanian voice). */
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()
    private val romanianLocale = Locale("ro", "RO")

    // Romanian phonetic pronunciation hints for more natural speech
    private val phoneticHints = mapOf(
        "ă" to "ăă",
        "â" to "îî",
        "î" to "îî",
        "ș" to "șșă",
        "ț" to "țță",
        "ce" to "che",
        "ci" to "chi",
        "ge" to "ghe",
        "gi" to "ghi",
    )

    // More varied and enthusiastic encouragement for children
    private val correctPhrases = listOf(
        "Bravo! Ești minunat!",
        "Excelent! Foarte tare!",
        "Foarte bine! Continuă tot așa!",
        "Super! Ești un campion!",
        "Felicitări! Ești foarte deștept!",
        "Wow! Incredibil!",
        "Perfect! Ai reușit!",
        "Magnific! Ești o stea!",
        "Genial! Așa se face!",
        "Uau! Ce bine te descurci!"
    )

    private val wrongPhrases = listOf(
        "Mai încearcă! Poți să reușești!",
        "Aproape! Mai încearcă o dată!",
        "Nu renunța! Ești pe drumul bun!",
        "Hai, mai încearcă! Tu poți!",
        "Nu-i nimic! Încearcă din nou!"
    )

    init {
        tts = try {
            TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(romanianLocale)
                    isReady = result != null
                            && result != TextToSpeech.LANG_MISSING_DATA
                            && result != TextToSpeech.LANG_NOT_SUPPORTED
                    // Natural conversational rate - slightly slower for children
                    tts?.setSpeechRate(0.82f)
                    // Warmer, more natural pitch
                    tts?.setPitch(1.1f)
                } else {
                    isReady = false
                }
                _voiceState.value = if (isReady) VoiceState.READY else VoiceState.MISSING
            }
        } catch (e: Exception) {
            _voiceState.value = VoiceState.MISSING
            null
        }
    }

    fun speakLetter(letter: String) {
        if (!isReady) return
        // Add slight emphasis for single letters
        val text = letter.lowercase()
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }
        tts?.setSpeechRate(0.75f)
        tts?.setPitch(1.1f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "letter_$letter")
        tts?.setSpeechRate(0.82f)
    }

    fun speakPhonetic(sound: String) {
        if (!isReady) return
        // Use phonetic hints for more natural pronunciation
        val enhanced = phoneticHints[sound.lowercase()] ?: sound
        tts?.setSpeechRate(0.6f)
        tts?.setPitch(1.05f)
        tts?.speak(enhanced, TextToSpeech.QUEUE_FLUSH, null, "phonetic_$sound")
        tts?.setSpeechRate(0.82f)
        tts?.setPitch(1.1f)
    }

    fun speakWord(word: String) {
        if (!isReady) return
        // Natural word pronunciation with proper Romanian cadence
        tts?.setSpeechRate(0.75f)
        tts?.setPitch(1.05f)
        tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "word_$word")
        tts?.setSpeechRate(0.82f)
        tts?.setPitch(1.1f)
    }

    fun speakSyllable(syllable: String) {
        if (!isReady) return
        // Very clear, deliberate syllable pronunciation
        tts?.setSpeechRate(0.55f)
        tts?.setPitch(1.0f)
        tts?.speak(syllable, TextToSpeech.QUEUE_FLUSH, null, "syllable_$syllable")
        tts?.setSpeechRate(0.82f)
        tts?.setPitch(1.1f)
    }

    fun speakSentence(sentence: String) {
        if (!isReady) return
        tts?.setSpeechRate(0.82f)
        tts?.setPitch(1.1f)
        tts?.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, "sentence")
    }

    fun speakLetterIntro(letter: String, sound: String) {
        if (!isReady) return
        // More natural intro with pauses using punctuation
        val enhancedSound = phoneticHints[sound.lowercase()] ?: sound
        val text = "Aceasta este litera $letter. $letter, face sunetul: $enhancedSound."
        tts?.setSpeechRate(0.75f)
        tts?.setPitch(1.1f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "intro_$letter")
    }

    fun speakEncouragement(isCorrect: Boolean) {
        if (!isReady) return
        val phrases = if (isCorrect) correctPhrases else wrongPhrases
        tts?.setSpeechRate(0.9f)
        tts?.setPitch(if (isCorrect) 1.2f else 1.0f)
        tts?.speak(phrases.random(), TextToSpeech.QUEUE_ADD, null, "encouragement")
        tts?.setSpeechRate(0.82f)
        tts?.setPitch(1.1f)
    }

    suspend fun speakAndWait(text: String): Boolean = suspendCancellableCoroutine { cont ->
        if (!isReady) {
            cont.resume(false)
            return@suspendCancellableCoroutine
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                if (cont.isActive) cont.resume(true)
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                if (cont.isActive) cont.resume(false)
            }
        })

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "await_$text")
    }

    private var pendingStep: CancellableContinuation<Boolean>? = null
    private var stepCounter = 0

    /**
     * Speaks [text] and suspends until it has been spoken, so the screen can
     * highlight letters, syllables and words in step with the voice.
     * Returns false when nothing was spoken (no Romanian voice, or interrupted).
     */
    suspend fun speakStep(text: String, rate: Float = 0.75f, pitch: Float = 1.05f): Boolean {
        if (!isReady || text.isBlank()) return false
        return suspendCancellableCoroutine { cont ->
            val engine = tts
            if (engine == null) {
                cont.resume(false)
                return@suspendCancellableCoroutine
            }
            // A newer step replaces the listener, so release whoever was still waiting.
            pendingStep?.takeIf { it.isActive }?.resume(false)
            pendingStep = cont
            val id = "step_${stepCounter++}"

            engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) = finish(utteranceId, true)
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) = finish(utteranceId, false)
                override fun onStop(utteranceId: String?, interrupted: Boolean) =
                    finish(utteranceId, false)

                private fun finish(utteranceId: String?, spoken: Boolean) {
                    if (utteranceId == id && cont.isActive) cont.resume(spoken)
                }
            })
            cont.invokeOnCancellation { engine.stop() }

            engine.setSpeechRate(rate)
            engine.setPitch(pitch)
            val result = engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
            engine.setSpeechRate(0.82f)
            engine.setPitch(1.1f)
            if (result != TextToSpeech.SUCCESS && cont.isActive) cont.resume(false)
        }
    }

    /** The sound a letter tile makes, e.g. "m" -> "mă"; letter groups are read as written. */
    fun soundOf(unit: String): String {
        val key = unit.lowercase()
        phoneticHints[key]?.let { return it }
        if (key.length > 1) return key
        return RomanianAlphabet.getLetterByUppercase(unit.uppercase())?.phoneticSound ?: key
    }

    /**
     * Opens the system screen where the parent can install the Romanian voice data,
     * falling back to the general text-to-speech settings.
     */
    fun openVoiceSettings(context: Context) {
        val install = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val settings = Intent("com.android.settings.TTS_SETTINGS")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val voice = Intent(android.provider.Settings.ACTION_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        for (intent in listOf(install, settings, voice)) {
            try {
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                // try the next option
            }
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
        _voiceState.value = VoiceState.MISSING
    }
}
