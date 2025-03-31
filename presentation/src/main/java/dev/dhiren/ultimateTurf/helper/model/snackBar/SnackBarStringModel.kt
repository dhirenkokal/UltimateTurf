package dev.dhiren.ultimateTurf.helper.model.snackBar

import dev.dhiren.ultimateTurf.helper.enums.snackbar.SnackBarType

open class SnackBarStringModel(
    incrementId: Int = 0,
    val title: String?,
    val message: String?,
    type: SnackBarType = SnackBarType.NONE,
    cancelPreviousSnackBar: Boolean = true,
) : SnackBarModel(incrementId, type, cancelPreviousSnackBar) {
    companion object {

        fun success(
            title: String,
            message: String,
            cancelPreviousToast: Boolean = true,
        ): SnackBarStringModel {
            return SnackBarStringModel(
                title = title,
                message = message,
                type = SnackBarType.SUCCESS,
                cancelPreviousSnackBar = cancelPreviousToast
            )
        }

        fun error(
            title: String,
            message: String,
            cancelPreviousToast: Boolean = true,
        ): SnackBarStringModel {
            return SnackBarStringModel(
                title = title,
                message = message,
                type = SnackBarType.ERROR,
                cancelPreviousSnackBar = cancelPreviousToast
            )
        }

        fun info(
            title: String,
            message: String,
            cancelPreviousToast: Boolean = true,
        ): SnackBarStringModel {
            return SnackBarStringModel(
                title = title,
                message = message,
                type = SnackBarType.INFO,
                cancelPreviousSnackBar = cancelPreviousToast
            )
        }
    }
}
