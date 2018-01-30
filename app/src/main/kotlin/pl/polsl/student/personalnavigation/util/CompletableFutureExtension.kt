package pl.polsl.student.personalnavigation.util

import android.content.Context
import java8.util.concurrent.CompletableFuture
import org.jetbrains.anko.runOnUiThread


fun <T> CompletableFuture<T>.thenAcceptOnUiThread(context: Context, consumer: (T) -> Unit) =
    this.thenAccept { sth ->
        context.runOnUiThread {
            consumer(sth)
        }
    }!!