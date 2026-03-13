package dev.ag6.libredesktop.api

import kotlin.test.Test
import kotlin.test.assertEquals

class LibreApiConfigTest {
    @Test
    fun buildsDefaultApiUrlWhenRegionIsMissing() {
        assertEquals(
            "https://api.libreview.io/llu/auth/login",
            buildLibreApiUrl(null, "llu/auth/login")
        )
    }

    @Test
    fun buildsRegionalApiUrlWhenRegionIsPresent() {
        assertEquals(
            "https://api-eu.libreview.io/llu/connections",
            buildLibreApiUrl("EU", "llu/connections")
        )
    }
}
