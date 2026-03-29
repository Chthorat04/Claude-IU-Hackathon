package com.readyaid.data.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log

enum class VoiceState {
    Idle,
    Listening,
    Processing,
    Error
}

class VoiceController(private val context: Context) : RecognitionListener {
    
    // We only initialize if permission is granted conceptually
    private var speechRecognizer: SpeechRecognizer? = null
    
    private var onResultAction: ((String) -> Unit)? = null
    private var onErrorAction: ((String) -> Unit)? = null
    private var onStateChangeAction: ((VoiceState) -> Unit)? = null

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(this)
        }
    }

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onStateChange: (VoiceState) -> Unit
    ) {
        onResultAction = onResult
        onErrorAction = onError
        onStateChangeAction = onStateChange

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechRecognizer?.startListening(intent)
        onStateChangeAction?.invoke(VoiceState.Listening)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        onStateChangeAction?.invoke(VoiceState.Idle)
    }

    fun destroy() {
        speechRecognizer?.destroy()
    }

    override fun onReadyForSpeech(params: Bundle?) {}

    override fun onBeginningOfSpeech() {}

    override fun onRmsChanged(rmsdB: Float) {}

    override fun onBufferReceived(buffer: ByteArray?) {}

    override fun onEndOfSpeech() {
        onStateChangeAction?.invoke(VoiceState.Processing)
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "Error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
        Log.e("VoiceController", errorMessage)
        onErrorAction?.invoke(errorMessage)
        onStateChangeAction?.invoke(VoiceState.Error)
    }

    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            val result = matches[0]
            onResultAction?.invoke(result)
        }
        onStateChangeAction?.invoke(VoiceState.Idle)
    }

    override fun onPartialResults(partialResults: Bundle?) {}

    override fun onEvent(eventType: Int, params: Bundle?) {}
}
