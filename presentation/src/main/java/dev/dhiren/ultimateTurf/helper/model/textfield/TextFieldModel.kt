package dev.dhiren.ultimateTurf.helper.model.textfield

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * purpose: to help stateManagement of TextField in Compose.
 * @param currentText represents the text value displayed/edited by the user.
 * @param serverText represents the value we got from server. useful in editProfile.
 * @param isError represents if the input is valid. Useful to show error.
 */

data class TextFieldModel(
    private val currentText: MutableStateFlow<String> = MutableStateFlow(""),
    private val serverText: MutableStateFlow<String> = MutableStateFlow(""),
    private val isError: MutableStateFlow<Boolean> = MutableStateFlow(false),
) {

    fun updateBothCurrentAndServer(value: String): TextFieldModel {
        this.currentText.value = value
        this.currentText.value = value
        return this
    }

    fun updateCurrentText(currentText: String): TextFieldModel {
        this.currentText.value = currentText
        updateIsError(isError = false)
        /** resets the errorState when user enters something */
        return this
    }

    fun updateServerText(serverText: String): TextFieldModel {
        this.serverText.value = serverText
        return this
    }

    fun updateIsError(isError: Boolean): TextFieldModel {
        this.isError.value = isError
        return this
    }

    fun currentTextAsStateFlow(): StateFlow<String> = currentText.asStateFlow()
    fun serverTextAsStateFlow(): StateFlow<String> = currentText.asStateFlow()
    fun isErrorAsStateFlow(): StateFlow<Boolean> = isError.asStateFlow()

    fun currentTextAsString(): String = this.currentText.value
    fun serverTextAsString(): String = this.serverText.value
    fun isErrorAsBoolean(): Boolean = this.isError.value

    @Composable
    fun collectCurrentTextAsStateValue() = currentText.collectAsState().value

    @Composable
    fun collectServerTextAsStateValue() = serverText.collectAsState().value

    @Composable
    fun collectIsErrorAsStateValue() = isError.collectAsState().value


    @OptIn(FlowPreview::class)
    fun currentTextDebounce(
        delayedScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        delayedDebounce: (result: String) -> Unit = {},
        instantScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
        instantDebounce: (result: String) -> Unit = {},
        delay: Long = 2000,
    ) {
        currentText.debounce(delay).onEach {
            delayedDebounce.invoke(it)
        }.launchIn(delayedScope)

        currentText.debounce(10).onEach {
            instantDebounce.invoke(it)
        }.launchIn(instantScope)
    }

}
