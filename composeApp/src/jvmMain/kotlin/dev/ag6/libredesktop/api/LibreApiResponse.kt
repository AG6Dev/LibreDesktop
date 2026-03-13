package dev.ag6.libredesktop.api

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlin.reflect.KClass

class LibreApiResponseSerializer<S>(
    private val dataSerializer: KSerializer<S>
) : JsonContentPolymorphicSerializer<LibreApiResponse<S>>(responseClass()) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<LibreApiResponse<S>> {
        val json = element.jsonObject
        val data = json["data"] as? JsonObject
        return when {
            data?.containsKey("redirect") == true -> LibreApiResponse.Redirect.serializer()
            json.containsKey("data") -> LibreApiResponse.Success.serializer(dataSerializer)
            json.containsKey("message") || json.containsKey("error") -> LibreApiResponse.Error.serializer()
            else -> throw IllegalStateException("Unknown LibreApiResponse shape: ${json.keys}")
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private fun <S> responseClass(): KClass<LibreApiResponse<S>> =
            LibreApiResponse::class as KClass<LibreApiResponse<S>>
    }
}

sealed class LibreApiResponse<out S> {
    @Serializable
    data class Success<S>(val status: Int, val data: S) : LibreApiResponse<S>()

    @Serializable
    data class Error(val status: Int, val message: Message? = null, val error: Message? = null) :
        LibreApiResponse<Nothing>() {
        @Serializable
        data class Message(val message: String)
    }

    @Serializable
    data class Redirect(val status: Int? = null, val data: RedirectData) : LibreApiResponse<Nothing>() {
        val redirect: Boolean get() = data.redirect
        val region: String get() = data.region

        @Serializable
        data class RedirectData(val redirect: Boolean = true, val region: String)
    }
}
