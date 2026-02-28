package main.triptip.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object RouteUtils {

    fun getRouteData(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): JSONObject? {

        val client = OkHttpClient()

        val url =
            "https://router.project-osrm.org/route/v1/driving/" +
                    "$startLng,$startLat;$endLng,$endLat?overview=full&geometries=geojson"

        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            val json = JSONObject(response.body!!.string())
            return json.getJSONArray("routes").getJSONObject(0)
        }
    }
}