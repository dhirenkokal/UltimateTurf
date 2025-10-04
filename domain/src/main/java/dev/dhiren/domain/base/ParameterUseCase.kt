package dev.dhiren.domain.base

import android.os.CountDownTimer
import dev.dhiren.common.resource.enums.DataType
import dev.dhiren.common.resource.interfaces.UseCaseResponse  
import dev.dhiren.common.resource.model.DataResource
import dev.dhiren.domain.utils.ErrorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class ParameterUseCase<in Parameter, T> {

    abstract suspend fun run(params: Parameter, dataType: DataType): T
    private var job: Job? = null

    /**
     * @param [dataType] - will help us in kind of data we want fetch. we have to pass this to repository and take a decision there.
     * @param [scope] - useCase runs entire code inside this scope, we can use jobs and discuss the API at our will.
     * @param [params] - you can pass any kind of DataType inside and you will get it back in [run] implementation.
     * @param [startTimer] - whether to start an OTP timer.
     * @param [timerDurationInMs] - duration of the OTP timer in milliseconds.
     */
    operator fun invoke(
        scope: CoroutineScope = CoroutineScope(IO),
        params: Parameter,
        dataType: DataType = DataType.FORCE_CACHE,
        dataResourceValue: (DataResource<T>) -> Unit = {},
        callback: UseCaseResponse<T>? = null,
        startTimer: Boolean = true,
        timerDurationInMs: Long = 120000,
    ) {
        val timerScope = CoroutineScope(IO)
        job = scope.launch {
            dataResourceValue.invoke(DataResource.loading())
            callback?.onLoading()
            if (startTimer) {
                startTimer(duration = timerDurationInMs, timerScope)
            }
            try {
                val result = run(params, dataType)
                dataResourceValue.invoke(DataResource.success(result))
                callback?.onSuccess(result)
            } catch (throwable: Throwable) {
                val error = ErrorUtils.traceErrorException(throwable)
                dataResourceValue.invoke(DataResource.error(error))
                callback?.onError(error)
            }
        }
    }

    private fun startTimer(duration: Long, timerScope: CoroutineScope) {
        timerScope.launch(Dispatchers.Main) {
            object : CountDownTimer(duration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                }

                override fun onFinish() {
                    cancelJob()
                }
            }.start()
        }
    }

    fun cancelJob() {
        job?.cancel()
    }

    fun isJobNull() = job == null
}
