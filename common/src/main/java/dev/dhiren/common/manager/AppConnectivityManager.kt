package dev.dhiren.common.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dev.dhiren.common.util.NetworkUtil
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow

object AppConnectivityManager {
    private val _bleServiceFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _locationServiceFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _locationPermissionFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _blePermissionFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _pushNotificationPermissionFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _cameraPermissionFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val bleServiceFlow: StateFlow<Boolean> = _bleServiceFlow
    val locationServiceFlow: StateFlow<Boolean> = _locationServiceFlow
    val locationPermissionFlow: StateFlow<Boolean> = _locationPermissionFlow
    val blePermissionFlow: StateFlow<Boolean> = _blePermissionFlow
    val pushNotificationPermissionFlow: StateFlow<Boolean> = _pushNotificationPermissionFlow
    val cameraPermissionFlow: StateFlow<Boolean> = _cameraPermissionFlow

    fun updateBleServiceFlow(value: Boolean) {
        _bleServiceFlow.value = value
    }

    fun updateLocationServiceFlow(value: Boolean) {
        _locationServiceFlow.value = value
    }

    fun updateLocationPermissionFlow(value: Boolean) {
        _locationPermissionFlow.value = value
    }

    fun updateBlePermissionFlow(value: Boolean) {
        _blePermissionFlow.value = value
    }

    fun updatePushNotificationPermissionFlow(value: Boolean) {
        _pushNotificationPermissionFlow.value = value
    }

    fun updateCameraPermissionFlow(value: Boolean) {
        _cameraPermissionFlow.value = value
    }


    fun updateInitialValues(
        blePermissionValue: Boolean,
        bleServiceValue: Boolean,
        locationPermissionValue: Boolean,
        locationServiceValue: Boolean,
        pushNotificationPermissionValue: Boolean,
        cameraPermissionValue: Boolean
    ) {
        updateBlePermissionFlow(blePermissionValue)
        updateBleServiceFlow(bleServiceValue)
        updateLocationPermissionFlow(locationPermissionValue)
        updateLocationServiceFlow(locationServiceValue)
        updatePushNotificationPermissionFlow(pushNotificationPermissionValue)
        updateCameraPermissionFlow(cameraPermissionValue)
    }
}

fun Context.observeConnectivityAsFlow() = callbackFlow {
    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            trySend(true)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            trySend(false)
        }
    }

    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()

    connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

    // Set current state
    val currentState = NetworkUtil.isInternetConnected(this@observeConnectivityAsFlow)
    trySend(currentState)

    // Remove callback when not used
    awaitClose {
        // Remove listeners
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
