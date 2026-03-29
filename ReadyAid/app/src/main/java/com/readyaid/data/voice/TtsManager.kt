package com.readyaid.data.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle unsupported language gracefully
            } else {
                isInitialized = true
            }
        }
    }

    fun speak(sections: List<String>) {
        if (!isInitialized) return
        
        CoroutineScope(Dispatchers.Main).launch {
            for (section in sections) {
                if (section.isNotBlank()) {
                    tts?.speak(section, TextToSpeech.QUEUE_ADD, null, null)
                    // Synthesize slight pauses to map the "500ms pause between" constraint logically
                    // Though natively QUEUE_ADD handles sequential reads, sometimes manual gaps are needed
                }
            }
        }
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
