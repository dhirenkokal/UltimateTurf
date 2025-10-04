package dev.dhiren.common.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

object GenerateUniqueUtil {
    fun generateUUID(name: String = ""): String {
        val trimmedName = name.trim()
        val randomSixDigitNumber = (100000..999999).random()
        val timestamp = getCurrentTimestamp()
        val combinedString = "$trimmedName$timestamp$randomSixDigitNumber"
        val uuid = UUID.nameUUIDFromBytes(combinedString.toByteArray())
        return uuid.toString()
            .replace("-", "").take(16)
    }

    fun generateUUIDForUser(name: String = ""): String {
        val trimmedName = name.trim()
        val randomSixDigitNumber = (100000..999999).random()
        val timestamp = getCurrentTimestamp()
        val combinedString = "$trimmedName$timestamp$randomSixDigitNumber"
        val uuid = UUID.nameUUIDFromBytes(combinedString.toByteArray())
        return uuid.toString()
            .replace("-", "").take(15)
    }

    fun generateUUIDForProperty(name: String = ""): String {
        val trimmedName = name.trim()
        val randomSixDigitNumber = (100000..999999).random()
        val timestamp = getCurrentTimestamp()
        val combinedString = "$trimmedName$timestamp$randomSixDigitNumber"
        val uuid = UUID.nameUUIDFromBytes(combinedString.toByteArray())
        return uuid.toString()
            .replace("-", "").take(14)
    }

    fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val timestamp = sdf.format(Date())
        Log.d("Time", "UTC Timestamp: $timestamp")
        return timestamp
    }

    fun formatTimestampToDate(timestamp: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(timestamp) ?: return "Invalid date"
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            outputFormat.format(date)
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    fun formatTimestampToTime(timestamp: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(timestamp) ?: return "Invalid time"
            val outputFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            outputFormat.format(date)
        } catch (e: Exception) {
            "Invalid time"
        }
    }
}
