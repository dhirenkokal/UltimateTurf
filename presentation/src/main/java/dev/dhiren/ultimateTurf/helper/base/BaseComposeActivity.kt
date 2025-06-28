package dev.dhiren.ultimateTurf.helper.base

import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import dev.dhiren.common.manager.PermissionManager
import dev.dhiren.domain.enums.SecurityType

abstract class BaseComposeActivity<navigator>: FragmentActivity() {

    private val securityFailedType = mutableStateOf<SecurityType?>(value = null)
    lateinit var permissionsManager: PermissionManager
    val isLocked = mutableStateOf(false)
}