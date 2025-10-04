package dev.dhiren.ultimateTurf.helper.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import dev.dhiren.common.manager.AppConnectivityManager
import dev.dhiren.common.manager.EventBusManager
import dev.dhiren.common.manager.PermissionManager
import dev.dhiren.common.manager.SecurityManager
import dev.dhiren.common.resource.sealed.GlobalEvent
import dev.dhiren.domain.enums.SecurityType
import dev.dhiren.ultimateTurf.manager.CurrentActivityManager
import dev.dhiren.ultimateTurf.helper.base.SecurityFailedComposeScreen
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseComposeActivity<navigator> : FragmentActivity() {

    private val securityFailedType = mutableStateOf<SecurityType?>(value = null)
    lateinit var permissionsManager: PermissionManager
    val isLocked = mutableStateOf(false)
    private var wasAppComingFromBackground = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        WindowCompat.setDecorFitsSystemWindows(window, false)
        permissionsManager = PermissionManager(this)
        EventBusManager.register(this)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        onActivityCreated(savedInstanceState)

        setContent {
            val securityFailedType = remember { this.securityFailedType }.value
            if (securityFailedType == null) {
                SetComposeUI()
            } else {
                SecurityFailedComposeScreen.SecurityFailedComposeUI(securityType = securityFailedType) {
                    finishAffinity()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        CurrentActivityManager.setCurrentActivity(this)
        
        // Check if app just came from background to foreground
        if (CurrentActivityManager.didAppJustComeFromBackground()) {
            wasAppComingFromBackground = true
        }
        
        handleOnResumeTimeOutEventState()
        handlePermissionsAndServicesInitialValues()
    }

    override fun onPause() {
        super.onPause()
        if (CurrentActivityManager.isActivityActive(this)) {
            CurrentActivityManager.setCurrentActivity(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusManager.unRegister(this)
    }

    @Composable
    abstract fun SetComposeUI()
    abstract fun onActivityCreated(savedInstanceState: Bundle?)
    abstract fun navigationImpl(): navigator

    private fun handlePermissionsAndServicesInitialValues() {
        AppConnectivityManager.updateInitialValues(
            blePermissionValue = permissionsManager.isBlePermissionGranted(),
            locationPermissionValue = permissionsManager.isLocationPermissionsGranted(),
            bleServiceValue = permissionsManager.isBluetoothEnabled(),
            locationServiceValue = permissionsManager.isLocationEnabled(),
            pushNotificationPermissionValue = permissionsManager.isPushNotificationPermissionGranted(),
            cameraPermissionValue = permissionsManager.isCameraPermissionGranted()
        )
    }

    private fun handleOnResumeTimeOutEventState() {
        if (listenToInteractionTimeoutEvents()) {
            if (wasAppComingFromBackground) {
                wasAppComingFromBackground = false
                isLocked.value = true
            } else if (!SecurityManager.interactionActiveInLast60Seconds()) {
                isLocked.value = true
            } else {
                SecurityManager.updateTimeStamp(System.currentTimeMillis())
            }
        }
    }

    open fun continueAfterPermissions() {}
    open fun onPermissionCancelled() {}
    open fun onDeviceBackPressedCallback() { finish() }
    open fun enableSecurityFeatures() = true
    open fun listenToLogoutEvents(): Boolean = true
    open fun listenToInteractionTimeoutEvents(): Boolean = true
    open fun shouldShowBanner(): Boolean = true

    private val permissionActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val isContinuePressedFromPermission =
                    data?.getBooleanExtra("isContinuePressed", false) ?: false
                if (isContinuePressedFromPermission) {
                    continueAfterPermissions()
                }
            } else {
                onPermissionCancelled()
            }
        }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onDeviceBackPressedCallback()
        }
    }

    fun <T> navigateAway(
        destination: Class<T>,
        finish: Boolean = false,
        checkForInternet: Boolean = false,
    ) {
        startActivity(Intent(this, destination))
        if (finish) {
            finish()
        }
    }

    fun navigateAway(
        intent: Intent,
        finish: Boolean = false,
        checkForInternet: Boolean = false,
    ) {
        startActivity(intent).apply {
            if (finish) {
                finish()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGlobalEvent(event: GlobalEvent) {
        when (event) {
            is GlobalEvent.UserLogout -> {
                // Handle logout
            }

            is GlobalEvent.InteractionTimeOut -> {
                if (listenToInteractionTimeoutEvents().not() || lifecycle.currentState != Lifecycle.State.RESUMED) return
                isLocked.value = true
            }

            is GlobalEvent.BluetoothStateChanged -> {
                // Handle bluetooth state change
            }

            GlobalEvent.FirmwareUpdateAvailable -> {
                // Handle firmware update
            }
        }
    }

    fun unlockApp() {
        SecurityManager.updateTimeStamp(System.currentTimeMillis())
        isLocked.value = false
    }
}