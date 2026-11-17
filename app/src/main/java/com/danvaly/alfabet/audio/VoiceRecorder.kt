package com.danvaly.alfabet.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import java.io.File

/**
 * Records the child reading a page and plays it back ("Mă înregistrez" / "Ascultă-mă").
 * One file per [key] in the cache folder, overwritten on every new recording.
 * Every call is wrapped in try/catch: a missing or busy microphone must never crash the app.
 * All methods are meant to be called from the main thread.
 */
class VoiceRecorder(context: Context) {

    private val appContext = context.applicationContext
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var recordingFile: File? = null
    private var startedAt = 0L

    val isRecording: Boolean get() = recorder != null
    val isPlaying: Boolean get() = player != null

    fun fileFor(key: String): File {
        val safe = key.map { if (it.isLetterOrDigit() || it == '_') it else '_' }.joinToString("")
        return File(appContext.cacheDir, "voice_$safe.m4a")
    }

    fun hasRecording(key: String): Boolean = try {
        fileFor(key).let { it.exists() && it.length() > 0 }
    } catch (e: Exception) {
        false
    }

    /**
     * Starts recording into the file for [key]. [onLimitReached] is called (on the main thread)
     * if the recording is stopped automatically after [maxSeconds].
     * Returns false if the microphone could not be started.
     */
    fun startRecording(key: String, maxSeconds: Int = 60, onLimitReached: () -> Unit = {}): Boolean {
        stopPlaying()
        cancelRecording()
        val file = fileFor(key)
        return try {
            if (file.exists()) file.delete()
            val mediaRecorder = newRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder.setAudioSamplingRate(44100)
            mediaRecorder.setAudioEncodingBitRate(96000)
            mediaRecorder.setMaxDuration(maxSeconds * 1000)
            mediaRecorder.setOutputFile(file.absolutePath)
            mediaRecorder.setOnInfoListener { _, what, _ ->
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    stopRecording()
                    onLimitReached()
                }
            }
            mediaRecorder.setOnErrorListener { _, _, _ -> cancelRecording() }
            mediaRecorder.prepare()
            mediaRecorder.start()
            recorder = mediaRecorder
            recordingFile = file
            startedAt = System.currentTimeMillis()
            true
        } catch (e: Exception) {
            cancelRecording()
            try {
                file.delete()
            } catch (ignored: Exception) {
            }
            false
        }
    }

    /** Stops and keeps the recording. Returns true if a usable recording was saved. */
    fun stopRecording(): Boolean {
        val mediaRecorder = recorder ?: return false
        val file = recordingFile
        recorder = null
        recordingFile = null
        var ok = try {
            // A recording shorter than a moment has no data and stop() throws.
            mediaRecorder.stop()
            System.currentTimeMillis() - startedAt > 300
        } catch (e: Exception) {
            false
        }
        try {
            mediaRecorder.release()
        } catch (e: Exception) {
            // ignore
        }
        if (file != null) {
            if (!ok || !file.exists() || file.length() == 0L) {
                ok = false
                try {
                    file.delete()
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
        return ok
    }

    /** Stops without keeping anything. */
    fun cancelRecording() {
        val mediaRecorder = recorder ?: return
        val file = recordingFile
        recorder = null
        recordingFile = null
        try {
            mediaRecorder.stop()
        } catch (e: Exception) {
            // nothing was recorded yet
        }
        try {
            mediaRecorder.release()
        } catch (e: Exception) {
            // ignore
        }
        try {
            file?.delete()
        } catch (e: Exception) {
            // ignore
        }
    }

    /** Plays the recording for [key]; [onDone] runs when it ends or fails. Returns false if nothing plays. */
    fun play(key: String, onDone: () -> Unit): Boolean {
        stopPlaying()
        if (isRecording) return false
        val file = fileFor(key)
        if (!hasRecording(key)) return false
        return try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(file.absolutePath)
            mediaPlayer.setOnCompletionListener {
                releasePlayer(it)
                onDone()
            }
            mediaPlayer.setOnErrorListener { mp, _, _ ->
                releasePlayer(mp)
                onDone()
                true
            }
            mediaPlayer.prepare()
            mediaPlayer.start()
            player = mediaPlayer
            true
        } catch (e: Exception) {
            stopPlaying()
            false
        }
    }

    fun stopPlaying() {
        val mediaPlayer = player ?: return
        player = null
        try {
            mediaPlayer.stop()
        } catch (e: Exception) {
            // ignore
        }
        try {
            mediaPlayer.release()
        } catch (e: Exception) {
            // ignore
        }
    }

    private fun releasePlayer(mediaPlayer: MediaPlayer) {
        if (player === mediaPlayer) player = null
        try {
            mediaPlayer.release()
        } catch (e: Exception) {
            // ignore
        }
    }

    /** Call when the screen goes away. Keeps finished recordings, drops an unfinished one. */
    fun release() {
        cancelRecording()
        stopPlaying()
    }

    @Suppress("DEPRECATION")
    private fun newRecorder(): MediaRecorder =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(appContext) else MediaRecorder()
}
