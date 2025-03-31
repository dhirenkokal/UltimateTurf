package dev.dhiren.ultimateTurf.helper.model.snackBar

import dev.dhiren.ultimateTurf.R
import dev.dhiren.ultimateTurf.helper.enums.snackbar.SnackBarType

open class SnackBarModel(
    var incrementId: Int = 0,
    val type: SnackBarType = SnackBarType.NONE,
    private val cancelPreviousSnackBar: Boolean = true,
) {
    fun copyLocalisedSnackBarModel(
        incrementId: Int,
    ): SnackBarLocalisedModel {
        return SnackBarLocalisedModel(
            incrementId = incrementId,
            title = (this as SnackBarLocalisedModel).title,
            message = this.message,
            type = this.type,
            cancelPreviousSnackBar = this.cancelPreviousSnackBar
        )
    }

    fun copyStringSnackBarModel(
        incrementId: Int,
    ): SnackBarStringModel {
        return SnackBarStringModel(
            incrementId = incrementId,
            title = (this as SnackBarStringModel).title,
            message = this.message,
            type = this.type,
            cancelPreviousSnackBar = this.cancelPreviousSnackBar
        )
    }
}



