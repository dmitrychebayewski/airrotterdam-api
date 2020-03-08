package com.minskrotterdam.airquality.services

import com.google.gson.Gson
import com.minskrotterdam.airquality.models.stations.Data
import org.testng.Assert
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import org.testng.annotations.Test
import java.io.ByteArrayOutputStream

class StationsServiceIT : AbstractHttpServiceIT() {

    val STATIONS_SERVICE_URL = "${TEST_API_ENDPOINT}/stations"

    @BeforeTest
    fun setUp() {
        setupTests()
    }

    @Test
    fun testGetStationsGivingValidResponse() {
        val response = httpGet(STATIONS_SERVICE_URL)
        Assert.assertEquals(response.statusLine.statusCode, 200)
        Assert.assertEquals(response.getFirstHeader("content-type").value, "application/json")
    }

    @Test
    fun testGetStationsGivingValidResponseBody() {
        val entity = httpGet(STATIONS_SERVICE_URL).entity
        val content = ByteArrayOutputStream()
        entity.writeTo(content)
        val stations = Gson().fromJson(content.toString(), Array<Array<Data>>::class.java)
        Assert.assertEquals(stations.size, 4)
    }

    @AfterTest
    fun tearDown() {
        tearDownTests()
    }

}