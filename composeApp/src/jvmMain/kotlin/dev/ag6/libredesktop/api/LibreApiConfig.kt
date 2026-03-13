package dev.ag6.libredesktop.api

import com.russhwolf.settings.Settings

private const val DEFAULT_BASE_URL = "https://api.libreview.io"
private const val API_REGION_KEY = "api_region"

fun Settings.getLibreApiRegion(): String? = getStringOrNull(API_REGION_KEY)

fun Settings.setLibreApiRegion(region: String) {
    putString(API_REGION_KEY, region.lowercase())
}

fun buildLibreApiBaseUrl(region: String?): String =
    if (region.isNullOrBlank()) DEFAULT_BASE_URL else "https://api-${region.lowercase()}.libreview.io"

fun buildLibreApiUrl(region: String?, path: String): String =
    "${buildLibreApiBaseUrl(region)}/${path.trimStart('/')}"
