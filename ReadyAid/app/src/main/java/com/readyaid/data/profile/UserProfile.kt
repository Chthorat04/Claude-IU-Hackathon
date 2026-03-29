package com.readyaid.data.profile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1, // Singleton
    val fullName: String,
    val age: Int,
    val bloodType: String,
    val conditions: String, // JSON array
    val allergies: String, // JSON array
    val medications: String, // JSON array
    val medicalHistory: String,
    val emergencyContact1Name: String,
    val emergencyContact1Phone: String,
    val emergencyContact2Name: String? = null,
    val emergencyContact2Phone: String? = null,
    val profileCompleted: Boolean = false,
    val disclaimerAccepted: Boolean = false
) {
    companion object {
        fun empty() = UserProfile(
            fullName = "",
            age = 0,
            bloodType = "",
            conditions = "[]",
            allergies = "[]",
            medications = "[]",
            medicalHistory = "",
            emergencyContact1Name = "",
            emergencyContact1Phone = "",
            profileCompleted = false,
            disclaimerAccepted = false
        )
    }
}
