package com.childrenbooks.core.audio

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.childrenbooks.data.content.AudioClip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/** The app's single narrator voice. Call from the main thread. */
interface Narrator {
    val isSpeaking: StateFlow<Boolean>

    /** False once the device's text-to-speech engine is known to be missing or broken. */
    val isVoiceAvailable: StateFlow<Boolean>

    fun speak(clip: AudioClip)

    fun stop()

    /** Bedtime mode: slower and calmer speech. */
    fun setBedtime(enabled: Boolean)
}

/**
 * Plays recorded clips with [MediaPlayer] and spoken text with one tuned [TextToSpeech] profile,
 * so the voice no longer changes between screens. App-scoped: one engine for the whole app.
 */
class AndroidNarrator(context: Context) : Narrator {
    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val queue = ArrayDeque<AudioClip>()

    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isVoiceAvailable = MutableStateFlow(true)
    override val isVoiceAvailable: StateFlow<Boolean> = _isVoiceAvailable.asStateFlow()

    private var player: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var waitingText: String? = null
    private var currentUtterance: String? = null
    private var utteranceCounter = 0
    private var bedtime = false

    init {
        tts = TextToSpeech(appContext) { status -> mainHandler.post { onTtsInit(status) } }
    }

    override fun speak(clip: AudioClip) {
        stopPlayback()
        queue.addAll(flatten(clip))
        _isSpeaking.value = queue.isNotEmpty()
        playNext()
    }

    override fun stop() {
        stopPlayback()
        _isSpeaking.value = false
    }

    override fun setBedtime(enabled: Boolean) {
        bedtime = enabled
        applyVoiceProfile()
    }

    private fun stopPlayback() {
        queue.clear()
        waitingText = null
        currentUtterance = null
        player?.let { mediaPlayer ->
            runCatching { mediaPlayer.stop() }
            mediaPlayer.release()
        }
        player = null
        tts?.stop()
    }

    private fun playNext() {
        val clip = queue.removeFirstOrNull()
        if (clip == null) {
            _isSpeaking.value = false
            return
        }
        when (clip) {
            is AudioClip.Recorded -> playRecorded(clip.resId)
            is AudioClip.Spoken -> playSpoken(clip.text)
            is AudioClip.Sequence -> {
                flatten(clip).asReversed().forEach { queue.addFirst(it) }
                playNext()
            }
        }
    }

    private fun playRecorded(resId: Int) {
        val mediaPlayer = runCatching { MediaPlayer.create(appContext, resId) }.getOrNull()
        if (mediaPlayer == null) {
            playNext()
            return
        }
        player = mediaPlayer
        mediaPlayer.setOnCompletionListener { finished ->
            finished.release()
            if (player === finished) player = null
            playNext()
        }
        mediaPlayer.setOnErrorListener { failed, _, _ ->
            failed.release()
            if (player === failed) player = null
            playNext()
            true
        }
        mediaPlayer.start()
    }

    private fun playSpoken(text: String) {
        val engine = tts
        when {
            engine == null || !_isVoiceAvailable.value -> playNext()
            !ttsReady -> waitingText = text
            else -> {
                val id = "cb-${++utteranceCounter}"
                currentUtterance = id
                val result = engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
                if (result != TextToSpeech.SUCCESS) {
                    currentUtterance = null
                    playNext()
                }
            }
        }
    }

    private fun onTtsInit(status: Int) {
        val engine = tts ?: return
        if (status != TextToSpeech.SUCCESS) {
            markVoiceUnavailable()
            return
        }
        val language = engine.setLanguage(Locale.US)
        if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
            markVoiceUnavailable()
            return
        }
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit

            override fun onDone(utteranceId: String?) {
                mainHandler.post { onUtteranceFinished(utteranceId) }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                mainHandler.post { onUtteranceFinished(utteranceId) }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                mainHandler.post { onUtteranceFinished(utteranceId) }
            }
        })
        ttsReady = true
        applyVoiceProfile()
        waitingText?.let { text ->
            waitingText = null
            playSpoken(text)
        }
    }

    private fun onUtteranceFinished(utteranceId: String?) {
        if (utteranceId != null && utteranceId == currentUtterance) {
            currentUtterance = null
            playNext()
        }
    }

    private fun markVoiceUnavailable() {
        _isVoiceAvailable.value = false
        ttsReady = false
        if (waitingText != null) {
            waitingText = null
            playNext()
        }
    }

    private fun applyVoiceProfile() {
        val engine = tts ?: return
        if (!ttsReady) return
        engine.setPitch(if (bedtime) VoicePitchBedtime else VoicePitch)
        engine.setSpeechRate(if (bedtime) VoiceRateBedtime else VoiceRate)
    }

    private fun flatten(clip: AudioClip): List<AudioClip> = when (clip) {
        is AudioClip.Sequence -> clip.clips.flatMap(::flatten)
        else -> listOf(clip)
    }

    private companion object {
        const val VoicePitch = 1.3f
        const val VoiceRate = 0.9f
        const val VoicePitchBedtime = 1.15f
        const val VoiceRateBedtime = 0.8f
    }
}
