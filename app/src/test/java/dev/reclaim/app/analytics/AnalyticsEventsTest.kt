package dev.reclaim.app.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyticsEventsTest {
    private val snakeCase = Regex("^[a-z][a-z0-9_]*$")

    private val prohibitedKeyParts = listOf(
        "package",
        "domain",
        "url",
        "uri",
        "text",
        "title",
        "query",
        "email",
        "location",
        "message",
        "notification",
        "display_name",
        "device_name",
        "host",
        "app_label",
    )

    @Test
    fun eventNamesAreSnakeCaseAndUnique() {
        AnalyticsEvents.allNames.forEach { name ->
            assertTrue(name, snakeCase.matches(name))
        }
        assertEquals(AnalyticsEvents.allNames.size, AnalyticsEvents.allNames.toSet().size)
    }

    @Test
    fun eventNamesFitTheFirebaseLimit() {
        AnalyticsEvents.allNames.forEach { name ->
            assertTrue("$name is longer than 40 characters", name.length <= 40)
        }
    }

    @Test
    fun everyDeclaredConstantIsInTheList() {
        assertEquals(declaredStrings(AnalyticsEvents::class.java).toSet(), AnalyticsEvents.allNames)
    }

    @Test
    fun everyDeclaredParamIsInTheList() {
        assertEquals(
            declaredStrings(AnalyticsEvents.Params::class.java).toSet(),
            AnalyticsEvents.Params.allKeys,
        )
    }

    @Test
    fun noParamCanCarryPrivateContent() {
        AnalyticsEvents.Params.allKeys.forEach { key ->
            prohibitedKeyParts.forEach { part ->
                assertFalse("$key looks like it can carry $part", key.contains(part))
            }
        }
    }

    @Test
    fun paramKeysAreSnakeCase() {
        AnalyticsEvents.Params.allKeys.forEach { key ->
            assertTrue(key, snakeCase.matches(key))
        }
    }

    private fun declaredStrings(type: Class<*>): List<String> =
        type.declaredFields
            .filter { it.type == String::class.java }
            .mapNotNull { field ->
                field.isAccessible = true
                field.get(null) as? String
            }
}
