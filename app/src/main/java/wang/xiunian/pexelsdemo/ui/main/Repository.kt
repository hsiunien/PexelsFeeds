package wang.xiunian.pexelsdemo.ui.main

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import wang.xiunian.pexelsdemo.ui.main.entity.PexelsResponse
import wang.xiunian.pexelsdemo.ui.main.entity.PhotosResponse


object PexelsImageRepository {
    const val KEY_PREFIX = "key_for_page"
    private val apiKey =
        "replace to your api key"
    private val client = OkHttpClient()
    private val gson = Gson()
    private var kv = MMKV.defaultMMKV()
    suspend fun getImageRespFromCached(page: Int, perPage: Int): List<PhotosResponse>? {

        return withContext(Dispatchers.IO) {
            //only deal with the first page, so we don't need to replace ,just clear
            val savedMsg = kv.getString("${KEY_PREFIX}_${page}_${perPage}", "")
            if (savedMsg.isNullOrEmpty()) {
                return@withContext null
            }
            val rs: List<PhotosResponse>
            try {
                rs = gson.fromJson(savedMsg, PexelsResponse::class.java).photos
                return@withContext rs
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    suspend fun getImageResps(page: Int, perPage: Int): List<PhotosResponse> {
        return withContext(Dispatchers.IO) {
            val url = HttpUrl.Builder()
                .scheme("https")
                .host("api.pexels.com")
                .addPathSegment("v1")
                .addPathSegment("curated")
                .addQueryParameter("page", page.toString())
                .addQueryParameter("per_page", perPage.toString())
                .build()

            val request = Request.Builder()
                .url(url)
                .header("Authorization", apiKey)
                .build()
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    var rs = listOf<PhotosResponse>()
                    try {
                        rs = gson.fromJson(responseBody, PexelsResponse::class.java).photos
                        kv.encode("${KEY_PREFIX}_${page}_${perPage}", responseBody)
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                    }
                    return@withContext rs
                } else {
                    throw IOException("Unexpected response code: ${response.code}")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }

        }
    }
}