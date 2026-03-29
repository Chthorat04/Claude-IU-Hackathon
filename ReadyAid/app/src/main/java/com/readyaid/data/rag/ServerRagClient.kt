package com.readyaid.data.rag

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.readyaid.BuildConfig
import com.readyaid.data.profile.UserProfile
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader

class ServerRagClient @Inject constructor() : RagClient {

    private val gson = Gson()
    // Uses Gradle-configured endpoint so emulator/device URLs can be changed without code edits.
    private val baseUrl = BuildConfig.RAG_BASE_URL.removeSuffix("/")

    private data class ProfilePayload(
        val age: Int?,
        val conditions: List<String>,
        val allergies: List<String>,
        val medications: List<String>
    )

    private data class QueryRequest(
        val query: String,
        val profile: ProfilePayload?
    )

    override fun ask(query: String, profile: UserProfile): Flow<RagResponse> = flow {
        try {
            val url = URL("$baseUrl/ask")
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/x-ndjson")
            connection.doOutput = true

            val typeList = object : TypeToken<List<String>>() {}.type

            val profilePayload = ProfilePayload(
                age = if (profile.age > 0) profile.age else null,
                conditions = try { gson.fromJson(profile.conditions, typeList) } catch (e: Exception) { listOf(profile.conditions) },
                allergies = try { gson.fromJson(profile.allergies, typeList) } catch (e: Exception) { listOf(profile.allergies) },
                medications = try { gson.fromJson(profile.medications, typeList) } catch (e: Exception) { listOf(profile.medications) }
            )

            val request = QueryRequest(query = query, profile = profilePayload)
            val jsonBody = gson.toJson(request)

            OutputStreamWriter(connection.outputStream).use { it.write(jsonBody) }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                
                // Read line by line from the stream
                for (line in reader.lineSequence()) {
                    if (line.isNotBlank()) {
                        val chunk = gson.fromJson(line, RagResponse::class.java)
                        emit(chunk)
                    }
                }
            } else {
                emit(RagResponse("Error: $responseCode", isFinished = true, conditionDetected = "error"))
            }

        } catch (e: Exception) {
            Log.e("ServerRagClient", "Stream Exception: ${e.message}", e)
            val guidance = if (BuildConfig.RAG_BASE_URL.contains("10.0.2.2")) {
                "If using a physical device, update RAG_BASE_URL to your laptop IP."
            } else {
                "Ensure backend is running and reachable from this network."
            }
            emit(
                RagResponse(
                    "Connection failed to ${BuildConfig.RAG_BASE_URL}. $guidance",
                    isFinished = true,
                    conditionDetected = "error"
                )
            )
        }
    }.flowOn(Dispatchers.IO)
}
