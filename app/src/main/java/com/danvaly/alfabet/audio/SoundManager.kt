package com.danvaly.alfabet.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class SoundManager(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false
    private val romanianLocale = Locale("ro", "RO")

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(romanianLocale)
                isReady = result != TextToSpeech.LANG_MISSING_DATA
                        && result != TextToSpeech.LANG_NOT_SUPPORTED
                tts?.setSpeechRate(0.85f)
                tts?.setPitch(1.15f)
            }
        }
    }

    fun speakLetter(letter: String) {
        if (!isReady) return
        tts?.speak(letter, TextToSpeech.QUEUE_FLUSH, null, "letter_$letter")
    }

    fun speakPhonetic(sound: String) {
        if (!isReady) return
        tts?.setSpeechRate(0.7f)
        tts?.speak(sound, TextToSpeech.QUEUE_FLUSH, null, "phonetic_$sound")
        tts?.setSpeechRate(0.85f)
    }

    fun speakWord(word: String) {
        if (!isReady) return
        tts?.setSpeechRate(0.8f)
        tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "word_$word")
        tts?.setSpeechRate(0.85f)
    }

    fun speakSyllable(syllable: String) {
        if (!isReady) return
        tts?.setSpeechRate(0.65f)
        tts?.speak(syllable, TextToSpeech.QUEUE_FLUSH, null, "syllable_$syllable")
        tts?.setSpeechRate(0.85f)
    }

    fun speakSentence(sentence: String) {
        if (!isReady) return
        tts?.setSpeechRate(0.85f)
        tts?.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, "sentence")
    }

    fun speakLetterIntro(letter: String, sound: String) {
        if (!isReady) return
        val text = "Aceasta este litera $letter. $letter face $sound"
        tts?.setSpeechRate(0.8f)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "intro_$letter")
    }

    fun speakEncouragement(isCorrect: Boolean) {
        if (!isReady) return
        val phrases = if (isCorrect) {
            listOf("Bravo!", "Excelent!", "Foarte bine!", "Super!", "Felicitări!")
        } else {
            listOf("Mai încearcă!", "Aproape!", "Nu renunța!")
        }
        tts?.speak(phrases.random(), TextToSpeech.QUEUE_ADD, null, "encouragement")
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

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
