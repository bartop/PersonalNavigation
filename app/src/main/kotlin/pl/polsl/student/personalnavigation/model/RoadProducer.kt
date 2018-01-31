package pl.polsl.student.personalnavigation.model

import java8.util.concurrent.CompletableFuture
import java8.util.function.Supplier
import org.osmdroid.api.IGeoPoint
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint
import java.util.concurrent.Executor


class RoadProducer(
        private val executor: Executor,
        private val roadManager: RoadManager
) {

    fun roadBetween(start: GeoPoint, end: GeoPoint): CompletableFuture<Road> {
        return CompletableFuture.supplyAsync(
                Supplier{ roadManager.getRoad(ArrayList(listOf(start, end))) },
                executor
        )
    }
}