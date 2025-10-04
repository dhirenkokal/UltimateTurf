package dev.dhiren.common.manager

import android.content.Context
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File
import java.util.Locale

@OptIn(FlowPreview::class)
object SecurityManager {
    private val _lastInteractionTimeStamp = MutableStateFlow(System.currentTimeMillis())
    private val lastInteractionTimeStamp: StateFlow<Long> = _lastInteractionTimeStamp

    fun updateTimeStamp(value: Long) {
        _lastInteractionTimeStamp.value = value
    }

    fun interactionActiveInLast60Seconds(): Boolean {
        val difference = System.currentTimeMillis() - _lastInteractionTimeStamp.value
        return difference < 60000L
    }

    init {
        lastInteractionTimeStamp.debounce(60000L).onEach {
            EventBusManager.postSessionTimeOutEvent()
        }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    fun isDeviceRooted(context: Context): Boolean {
        return checkRootFiles() || checkRootCommands() || checkBuildTags()
    }

    fun isEmulator(): Boolean {
        return checkBuildConfig() || checkEmulatorFiles()
    }

    private fun checkBuildConfig(): Boolean {
        return Build.MANUFACTURER.contains("Genymotion")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.lowercase(Locale.US).contains("droid4x")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.HARDWARE == "goldfish"
                || Build.HARDWARE == "vbox86"
                || Build.HARDWARE.lowercase(Locale.US).contains("nox")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.PRODUCT == "sdk"
                || Build.PRODUCT == "google_sdk"
                || Build.PRODUCT == "sdk_x86"
                || Build.PRODUCT == "vbox86p"
                || Build.PRODUCT.lowercase(Locale.US).contains("nox")
                || Build.BOARD.lowercase(Locale.US).contains("nox")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
    }

    private fun checkEmulatorFiles(): Boolean {
        return (checkFiles(GENY_FILES)
                || checkFiles(ANDY_FILES)
                || checkFiles(NOX_FILES)
                || checkFiles(X86_FILES)
                || checkFiles(PIPES))
    }

    private fun checkRootFiles(): Boolean {
        return checkFiles(ROOT_FILES)
    }

    private fun checkRootCommands(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            process.destroy()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun checkBuildTags(): Boolean {
        return Build.TAGS != null && Build.TAGS.contains("test-keys")
    }

    private val GENY_FILES = arrayOf(
        "/dev/socket/genyd",
        "/dev/socket/baseband_genyd"
    )
    
    private val PIPES = arrayOf(
        "/dev/socket/qemud",
        "/dev/qemu_pipe"
    )
    
    private val X86_FILES = arrayOf(
        "ueventd.android_x86.rc",
        "x86.prop",
        "ueventd.ttVM_x86.rc",
        "init.ttVM_x86.rc",
        "fstab.ttVM_x86",
        "fstab.vbox86",
        "init.vbox86.rc",
        "ueventd.vbox86.rc"
    )
    
    private val ANDY_FILES = arrayOf(
        "fstab.andy",
        "ueventd.andy.rc"
    )
    
    private val NOX_FILES = arrayOf(
        "fstab.nox",
        "init.nox.rc",
        "ueventd.nox.rc"
    )

    private val ROOT_FILES = arrayOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su"
    )

    private fun checkFiles(targets: Array<String>): Boolean {
        for (pipe in targets) {
            val file = File(pipe)
            if (file.exists()) {
                return true
            }
        }
        return false
    }
}
