package pl.polsl.student.personalnavigation.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData


inline fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, crossinline callback: (T) -> Unit) {
    this.observe(
            owner,
            android.arch.lifecycle.Observer {
                it?.apply{ callback(this) }
            }
    )
}