package dev.dhiren.common.manager

import dev.dhiren.common.resource.sealed.GlobalEvent
import org.greenrobot.eventbus.EventBus

object EventBusManager {
    fun unRegister(subscriber: Any) {
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber)
        }
    }

    fun register(subscriber: Any) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber)
        }
    }

    fun postSessionTimeOutEvent() {
        EventBus.getDefault().post(GlobalEvent.InteractionTimeOut)
    }

    fun postUserLogoutEvent() {
        EventBus.getDefault().post(GlobalEvent.UserLogout)
    }

    fun postBluetoothStateChangedEvent(isEnabled: Boolean) {
        EventBus.getDefault().post(GlobalEvent.BluetoothStateChanged(isEnabled))
    }

    fun postFirmwareUpdateAvailableEvent() {
        EventBus.getDefault().post(GlobalEvent.FirmwareUpdateAvailable)
    }
}
