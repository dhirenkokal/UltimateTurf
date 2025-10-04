package dev.dhiren.ultimateTurf.manager

import android.app.Activity
import java.lang.ref.WeakReference

object CurrentActivityManager {
    private var currentActivity: WeakReference<Activity>? = null
    private var wasInBackground = false
    private var lastBackgroundTime = 0L

    fun setCurrentActivity(activity: Activity?) {
        if (activity == null) {
            wasInBackground = true
            lastBackgroundTime = System.currentTimeMillis()
            currentActivity = null
        } else {
            currentActivity = WeakReference(activity)
        }
    }

    fun getCurrentActivity(): Activity? {
        return currentActivity?.get()
    }

    fun isActivityActive(activity: Activity): Boolean {
        return currentActivity?.get() == activity
    }

    fun didAppJustComeFromBackground(): Boolean {
        val result = wasInBackground
        if (wasInBackground) {
            wasInBackground = false
        }
        return result
    }

    fun getTimeSinceBackground(): Long {
        return if (lastBackgroundTime > 0) {
            System.currentTimeMillis() - lastBackgroundTime
        } else {
            0L
        }
    }

    fun isAppInForeground(): Boolean {
        return currentActivity?.get() != null
    }
}
