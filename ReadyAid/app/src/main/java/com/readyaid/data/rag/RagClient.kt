package com.readyaid.data.rag

import com.readyaid.data.profile.UserProfile

import kotlinx.coroutines.flow.Flow

import com.google.gson.annotations.SerializedName

data class RagResponse(
    @SerializedName("response") val response: String,
    @SerializedName("sources") val sources: List<String> = emptyList(),
    @SerializedName("is_first_aid") val isFirstAid: Boolean = true,
    @SerializedName("condition_detected") val conditionDetected: String = "general",
    @SerializedName("is_finished") val isFinished: Boolean = false
)

interface RagClient {
    fun ask(query: String, profile: UserProfile): Flow<RagResponse>
}
