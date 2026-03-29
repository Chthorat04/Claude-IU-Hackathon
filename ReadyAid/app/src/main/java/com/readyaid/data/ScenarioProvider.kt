package com.readyaid.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.readyaid.R

data class FirstAidScenario(
    val id: String,
    val title: String,
    val category: String,
    val severity: String,
    val icon: String,
    val check_first: List<String>,
    val do_now: List<String>,
    val do_not: List<String>,
    val when_to_call: List<String>,
    val sources: List<String>
) {
    val isEmergency: Boolean get() = severity == "emergency_risk"
    val severityLabel: String get() = if (isEmergency) "Emergency Risk" else "Guidance Only"
}

object ScenarioProvider {
    private var cache: List<FirstAidScenario>? = null

    fun loadAll(context: Context): List<FirstAidScenario> {
        cache?.let { return it }
        val inputStream = context.resources.openRawResource(R.raw.firstaid_scenarios)
        val json = inputStream.bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<FirstAidScenario>>() {}.type
        val loaded: List<FirstAidScenario> = Gson().fromJson(json, type)
        cache = loaded
        return loaded
    }

    fun loadByCategory(context: Context, category: String): FirstAidScenario? {
        return loadAll(context).firstOrNull { it.category == category }
    }

    fun loadById(context: Context, id: String): FirstAidScenario? {
        return loadAll(context).firstOrNull { it.id == id }
    }
}
