package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.data.model.CultivationProfile
import com.example.data.model.CultivationRealm
import com.example.ui.components.RealmBadge
import com.example.ui.theme.CultivationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun realm_badge_screenshot() {
        val sampleProfile = CultivationProfile(
            id = 1,
            realmLevel = 2,
            realmSubStage = 2,
            currentQi = 620L,
            maxQi = 800L,
            spiritStones = 250L,
            daoTitle = "Core Disciple",
            consecutiveDaoDays = 7,
            totalTasksCompleted = 24
        )

        composeTestRule.setContent {
            CultivationTheme(realm = CultivationRealm.FOUNDATION_ESTABLISHMENT) {
                Box(modifier = Modifier.padding(16.dp)) {
                    RealmBadge(
                        profile = sampleProfile
                    )
                }
            }
        }

        composeTestRule.waitForIdle()
        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/realm_badge.png")
    }
}
