package dev.reclaim.app.domain.domains

import dev.reclaim.app.domain.model.DomainCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class DomainMatcherTest {
    private val matcher = DomainMatcher(
        categories = mapOf(
            DomainCategory.SOCIAL to setOf("instagram.com", "youtube.com", "x.com"),
            DomainCategory.ADULT to setOf("example-adult.test"),
            DomainCategory.GAMBLING to setOf("bet.example"),
        ),
        customDomains = setOf("example.org", "shop.example.co"),
    )

    @Test
    fun matchesExactHostAndSubdomains() {
        assertEquals(DomainCategory.SOCIAL, matcher.match("instagram.com")?.category)
        assertEquals(DomainCategory.SOCIAL, matcher.match("www.instagram.com")?.category)
        assertEquals(DomainCategory.SOCIAL, matcher.match("edits.instagram.com")?.category)
    }

    @Test
    fun normalizationHandlesCaseDotsAndWww() {
        assertEquals(DomainCategory.SOCIAL, matcher.match("  WWW.Instagram.COM.  ")?.category)
        assertEquals(DomainCategory.SOCIAL, matcher.match("x.com")?.category)
    }

    @Test
    fun customDomainsWinOverCategoriesAndHaveNoCategory() {
        val match = matcher.match("news.example.org")

        assertNotNull(match)
        assertNull(match?.category)
        assertEquals("example.org", match?.domain)
    }

    @Test
    fun siblingAndLookalikeHostsDoNotMatch() {
        assertNull(matcher.match("notinstagram.com"))
        assertNull(matcher.match("instagram.com.evil.test"))
        assertNull(matcher.match("example.org.uk"))
    }

    @Test
    fun urlsAndIpLiteralsNeverMatch() {
        assertNull(matcher.match("https://instagram.com/reels"))
        assertNull(matcher.match("10.0.0.1"))
        assertNull(matcher.match("instagram.com:443"))
        assertNull(matcher.match(""))
        assertNull(matcher.match("   "))
        assertNull(matcher.match("-bad.example"))
        assertNull(matcher.match("a..b"))
    }

    @Test
    fun categoryGatingRespectsEnabledSwitches() {
        assertNull(matcher.match("bet.example", enabledCategories = setOf(DomainCategory.SOCIAL)))
        assertNotNull(matcher.match("bet.example", enabledCategories = setOf(DomainCategory.GAMBLING)))
        assertNotNull(matcher.match("custom.example.org", enabledCategories = emptySet()))
    }

    @Test
    fun countsUniqueDomainsOnly() {
        val duplicated = DomainMatcher(
            categories = mapOf(
                DomainCategory.SOCIAL to setOf("instagram.com", "www.instagram.com", "instagram.com"),
            ),
        )

        assertEquals(1, duplicated.domainCount)
    }

    @Test
    fun normalizeRejectsGarbageAndKeepsHostnames() {
        assertNull(DomainMatcher.normalize("javascript:alert(1)"))
        assertNull(DomainMatcher.normalize("192.168.0.1"))
        assertEquals("example.co.uk", DomainMatcher.normalize("Example.CO.UK."))
    }
}
