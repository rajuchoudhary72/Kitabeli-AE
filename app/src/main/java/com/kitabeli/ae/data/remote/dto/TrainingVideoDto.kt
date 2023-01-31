package com.kitabeli.ae.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrainingVideoDto(
    @SerialName("code")
    val code: String? = null,
    @SerialName("message")
    val message: String? = null,
    @SerialName("payload")
    val payload: Payload? = null
) {
    @Serializable
    data class Payload(
        @SerialName("Items")
        val items: List<Item>? = null,
        @SerialName("totalItems")
        val totalItems: Int? = null,
        @SerialName("totalPages")
        val totalPages: Int? = null
    ) {
        @Serializable
        data class Item(
            @SerialName("category")
            val category: String? = null,
            @SerialName("description")
            val description: String? = null,
            @SerialName("durationInSec")
            val durationInSec: Int? = null,
            @SerialName("id")
            val id: Int? = null,
            @SerialName("isRecommended")
            val isRecommended: Boolean? = null,
            @SerialName("thumbnail")
            val thumbnail: String? = null,
            @SerialName("title")
            val title: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("video")
            val video: String? = null,
            @SerialName("videoDTO")
            val videoDTO: VideoDTO? = null,
            @SerialName("weight")
            val weight: Int? = null
        ) {
            @Serializable
            data class VideoDTO(
                @SerialName("urls")
                val urls: Urls? = null
            ) {
                @Serializable
                data class Urls(
                    @SerialName("mp4")
                    val mp4: String? = null
                )
            }
        }
    }
}