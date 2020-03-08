package com.minskrotterdam.airquality.handlers

import com.minskrotterdam.airquality.common.getSafeLaunchRanges
import com.minskrotterdam.airquality.common.safeLaunch
import com.minskrotterdam.airquality.services.AirMeasurementsService
import io.vertx.core.MultiMap
import io.vertx.core.json.Json
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.http.endAwait
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.gildor.coroutines.retrofit.await
import java.time.Instant
import java.time.temporal.ChronoUnit

class AirMeasurementsHandler {

    private fun extractStationId(requestParameters: MultiMap): List<String> {
        val stationId = requestParameters.getAll("station_number")
        if (stationId.isEmpty()) {
            //return listOf("NL01487", "NL49014", "NL01492")
            return emptyList()
        }
        return stationId
    }

    private fun extractFormula(requestParameters: MultiMap?): String {
        val formula = requestParameters?.get("formula")
        if (formula.isNullOrEmpty()) {
            return ""
        }
        return formula
    }

    private fun extractEndTime(requestParameters: MultiMap): String {
        val endTime = requestParameters.get("end")
        if (endTime.isNullOrEmpty()) {
            return Instant.now().toString()
        }
        return endTime
    }

    private fun extractStartTime(requestParameters: MultiMap): String {
        val startTime = requestParameters.get("start")
        if (startTime.isNullOrEmpty()) {
            return Instant.now().truncatedTo(ChronoUnit.DAYS).toString()
        }
        return startTime
    }


    suspend fun airMeasurementsHandler(ctx: RoutingContext) {
        safeLaunch(ctx) {

            ctx.response().headers().set("Content-Type", "application/json")
            getMeasurements(ctx)
        }
    }

    private suspend fun getMeasurements(ctx: RoutingContext) {
        val response = ctx.response()
        val requestParameters = ctx.request().params()
        val stationId = extractStationId(requestParameters)
        val formula = extractFormula(requestParameters)
        val endTime = extractEndTime(requestParameters)
        val startTime = extractStartTime(requestParameters)
        response.isChunked = true
        val firstPage = AirMeasurementsService().getMeasurement(stationId, formula, startTime, endTime, 1).await()
        val pagination = firstPage.pagination
        val mutex = Mutex()
        mutex.withLock {
            response.write("[")
            response.write(Json.encode(firstPage))
        }
        //When too many coroutines are waiting for server response concurrently, the server may respond with 504 code
        //Use segmentation to smaller ranges as workaround
        getSafeLaunchRanges(pagination.last_page).forEach {
            it.map {
                CoroutineScope(Dispatchers.Default).async {
                    val measurement = AirMeasurementsService().getMeasurement(stationId, formula, startTime,
                            endTime, it).await()
                    mutex.withLock {
                        response.write(",")
                        response.write(Json.encode(measurement))
                    }
                }
            }.awaitAll()
        }
        mutex.withLock {
            response.write("]")
            response.endAwait()
        }
    }
}