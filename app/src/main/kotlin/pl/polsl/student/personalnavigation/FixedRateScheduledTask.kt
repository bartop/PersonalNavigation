package pl.polsl.student.personalnavigation

import android.os.Handler

class FixedRateScheduledTask(
        private val intervalMs: Long,
        private val task: () -> Unit
): ScheduledTask {
    private val runnable = Runnable(this::executeAndRescheduleTask)
    private val handler = Handler()

    init {
        runnable.run()
    }

    override fun cancel() {
        handler.removeCallbacks(runnable)
    }

    private fun executeAndRescheduleTask() {
        task()
        handler.postDelayed(runnable, intervalMs)
    }
}