package main.triptip.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

object GeocodingUtils {

    fun getCoordinates(place: String): Pair<Double, Double>? {

        val client = OkHttpClient()

        val url =
            "https://nominatim.openstreetmap.org/search?q=$place&format=json&limit=1"

        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "TripTipApp")
            .build()

        client.newCall(request).execute().use { response ->

            val body = response.body?.string() ?: return null
            val jsonArray = JSONArray(body)

            if (jsonArray.length() > 0) {
                val obj = jsonArray.getJSONObject(0)
                return Pair(
                    obj.getDouble("lat"),
                    obj.getDouble("lon")
                )
            }
        }

        return null
    }
}