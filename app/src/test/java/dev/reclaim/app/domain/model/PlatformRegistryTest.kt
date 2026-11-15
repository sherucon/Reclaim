package dev.reclaim.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlatformRegistryTest {
    @Test
    fun everyKeyResolvesBackToItsPlatform() {
        Platform.entries.forEach { platform ->
            assertEquals(platform, PlatformRegistry.byKey(platform.key))
        }
    }

    @Test
    fun keysAreCaseAndWhitespaceInsensitive() {
        assertEquals(Platform.INSTAGRAM, PlatformRegistry.byKey("  Instagram "))
        assertNull(PlatformRegistry.byKey("myspace"))
        assertNull(PlatformRegistry.byKey(null))
    }

    @Test
    fun packagesMapToTheirPlatform() {
        assertEquals(Platform.INSTAGRAM, PlatformRegistry.byPackage("com.instagram.android"))
        assertEquals(Platform.TIKTOK, PlatformRegistry.byPackage("com.ss.android.ugc.trill"))
        assertNull(PlatformRegistry.byPackage("com.example.note"))
    }

    @Test
    fun hostsMatchTheirSubdomains() {
        assertEquals(Platform.REDDIT, PlatformRegistry.byHost("old.reddit.com"))
        assertEquals(Platform.YOUTUBE, PlatformRegistry.byHost("www.youtube.com."))
        assertEquals(Platform.X, PlatformRegistry.byHost("twitter.com"))
        assertNull(PlatformRegistry.byHost("notreddit.com"))
    }

    @Test
    fun detectionStrengthMatchesThePlan() {
        assertEquals(DetectionStrength.Strong, Platform.INSTAGRAM.detection)
        assertEquals(DetectionStrength.Strong, Platform.YOUTUBE.detection)
        assertEquals(DetectionStrength.Heuristic, Platform.TIKTOK.detection)
        assertEquals(DetectionStrength.None, Platform.REDDIT.detection)
    }

    @Test
    fun everyPlatformHasAPackageAndAHomeUrl() {
        Platform.entries.forEach { platform ->
            assertTrue(platform.key, platform.packages.isNotEmpty())
            assertTrue(platform.key, platform.webHosts.isNotEmpty())
            assertTrue(platform.key, platform.homeUrl.startsWith("https://"))
        }
    }

    @Test
    fun everyKeyIsLowercaseAndUrlSafe() {
        Platform.entries.forEach { platform ->
            assertEquals(platform.key, platform.key.lowercase())
            assertTrue(platform.key, platform.key.matches(Regex("^[a-z0-9]+$")))
        }
    }
}
