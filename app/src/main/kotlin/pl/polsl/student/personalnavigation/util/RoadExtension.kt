package pl.polsl.student.personalnavigation.util

import android.content.Context
import org.osmdroid.bonuspack.routing.Road


fun Road.calculateLength() {
    mLength = mNodes.sumByDouble { it.mLength } - (this.mNodes.lastOrNull()?.mLength ?: 0.0)
}

fun Road.calculateDuration() {
    mDuration = mNodes.sumByDouble { it.mDuration } - (this.mNodes.lastOrNull()?.mDuration ?: 0.0)
}

fun Road.totalLengthDurationText(context: Context): String {
    return this.getLengthDurationText(context, -1)
}