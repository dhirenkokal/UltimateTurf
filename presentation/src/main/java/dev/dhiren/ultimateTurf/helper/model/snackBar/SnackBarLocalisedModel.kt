package dev.dhiren.ultimateTurf.helper.model.snackBar

import dev.dhiren.ultimateTurf.R
import dev.dhiren.ultimateTurf.helper.enums.snackbar.SnackBarType

open class SnackBarLocalisedModel(
    incrementId: Int = 0,
    val title: Int = R.string.snack_bar_title,
    val message: Int = R.string.snack_bar_message,
    type: SnackBarType = SnackBarType.NONE,
    cancelPreviousSnackBar: Boolean = true,
) : SnackBarModel(incrementId, type, cancelPreviousSnackBar) {
    companion object {
        fun success(
            title: Int,
            message: Int,
            cancelPreviousToast: Boolean = true,
        ): SnackBarLocalisedModel {
            return SnackBarLocalisedModel(
                title = title,
                message = message,
                type = SnackBarType.SUCCESS,
                cancelPreviousSnackBar = cancelPreviousToast
            )
        }

        fun error(
            title: Int,
            message: Int,
            cancelPreviousToast: Boolean = true,
        ): SnackBarLocalisedModel {
            return SnackBarLocalisedModel(
                title = title,
                message = message,
                type = SnackBarType.ERROR,
                cancelPreviousSnackBar = cancelPreviousToast
            )
        }

        fun info(
            title: Int,
            message: Int,
            cancelPreviousToast: Boolean = true,
        ): SnackBarLocalisedModel {
            return SnackBarLocalisedModel(
                title = title,
                message = message,
                type = SnackBarType.INFO,
                cancelPreviousSnackBar = cancelPreviousToast
            )
        }

    }
}
