package wang.xiunian.pexelsdemo.ui.main.entity

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PexelsResponse(
    @SerializedName("page") val page: Long,
    @SerializedName("per_page") val per_page: Long,
    @SerializedName("total_results") val total_results: Long,
    @SerializedName("next_page") val next_page: String,
    val photos: List<PhotosResponse>
)

@Keep
data class PhotosResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("url") val url: String,
    @SerializedName("photographer") val photographer: String,
    @SerializedName("photographer_url") val photographerUrl: String,
    @SerializedName("photographer_id") val photographerId: Int,
    @SerializedName("avg_color") val avgColor: String,
    @SerializedName("src") val src: Src,
    @SerializedName("liked") val liked: Boolean,
    @SerializedName("alt") val alt: String
)

data class Src(
    @SerializedName("original") val original: String,
    @SerializedName("large2x") val large2x: String,
    @SerializedName("large") val large: String,
    @SerializedName("medium") val medium: String,
    @SerializedName("small") val small: String,
    @SerializedName("portrait") val portrait: String,
    @SerializedName("landscape") val landscape: String,
    @SerializedName("tiny") val tiny: String
)