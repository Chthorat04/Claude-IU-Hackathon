package com.readyaid.data.voice

enum class VoiceIntent {
    SOS,
    CallEmergency,
    FirstAidScenario,
    Unknown
}

object IntentMapper {
    private val MEDICAL_KEYWORDS = listOf(
        "burn", "burns", "bleeding", "blood", "cut", "wound", "choking", "choke",
        "fracture", "broken bone", "seizure", "convulsion", "asthma", "inhaler",
        "anaphylaxis", "allergy", "allergic", "cardiac", "heart attack", "stroke",
        "unconscious", "faint", "fainting", "hypothermia", "heat stroke", "poisoning",
        "overdose", "shock", "trauma", "hemorrhage", "airway", "breathing", "cpr",
        "resuscitation", "defibrillator", "aed", "sprain", "dislocation", "head injury",
        "concussion", "diabetic", "hypoglycemia", "insulin", "drowning", "electric shock",
        "eye injury", "nose bleed", "nosebleed", "broken", "spinal", "recovery position"
    )

    fun mapIntent(query: String): Pair<VoiceIntent, String> {
        val q = query.lowercase()

        // 1. SOS Intent
        if (q.contains("send sos") || q.contains("call for help") || 
            q.contains("i need help") || q.contains("help me") || q == "sos") {
            return Pair(VoiceIntent.SOS, query)
        }

        // 2. Call Emergency Intent
        if (q.contains("call emergency") || q.contains("call my emergency") || 
            q.contains("call 911") || q.contains("call ambulance") || 
            (q.contains("call") && (q.contains("999") || q.contains("112")))) {
            return Pair(VoiceIntent.CallEmergency, query)
        }

        // 3. First Aid Scenario
        val hasMedicalKeyword = MEDICAL_KEYWORDS.any { q.contains(it) }
        if (hasMedicalKeyword || q.contains("first aid") || q.contains("hurt") || q.contains("pain")) {
            return Pair(VoiceIntent.FirstAidScenario, query)
        }

        // 4. Unknown
        return Pair(VoiceIntent.Unknown, query)
    }
}
