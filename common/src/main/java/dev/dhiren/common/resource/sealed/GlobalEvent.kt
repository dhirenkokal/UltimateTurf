package dev.dhiren.common.resource.sealed

sealed class GlobalEvent {
    object UserLogout : GlobalEvent()
    object InteractionTimeOut : GlobalEvent()
    data class BluetoothStateChanged(val isEnabled: Boolean) : GlobalEvent()
    object FirmwareUpdateAvailable : GlobalEvent()
}
