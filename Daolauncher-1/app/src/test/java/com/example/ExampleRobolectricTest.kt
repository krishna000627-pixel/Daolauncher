package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AlchemyPill
import com.example.data.model.CultivationProfile
import com.example.data.model.CultivationRealm
import com.example.data.model.CultivationTask
import com.example.data.model.DaoCategory
import com.example.data.model.TaskGrade
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DaoLauncher", appName)
    }

    @Test
    fun `test cultivation realm progression and sub-stages`() {
        val profile = CultivationProfile(
            id = 1,
            realmLevel = 1,
            realmSubStage = 1,
            currentQi = 350L,
            maxQi = 300L,
            spiritStones = 50L
        )

        assertEquals(CultivationRealm.QI_CONDENSATION, profile.currentRealm)
        assertEquals("Early Stage", profile.stageName)
        assertTrue(profile.isReadyForBreakthrough)
    }

    @Test
    fun `test quest grades and dao categories`() {
        val task = CultivationTask(
            id = 1,
            title = "Morning Sword Form",
            grade = TaskGrade.EARTH,
            category = DaoCategory.SWORD_BODY,
            isCompleted = false
        )

        assertEquals(60, task.grade.qiReward)
        assertEquals(DaoCategory.SWORD_BODY, task.category)
        assertFalse(task.isCompleted)
    }

    @Test
    fun `test alchemy pill effects and costs`() {
        val pill = AlchemyPill(
            id = "breakthrough_pill",
            name = "Foundation Solidifying Pill",
            chineseName = "筑基丹",
            description = "Solidifies mortal meridians to guarantee 100% breakthrough success in Heavenly Tribulation.",
            stoneCost = 80,
            qiCost = 60,
            count = 2,
            effectDescription = "100% Breakthrough Chance",
            durationMinutes = 0,
            tier = 2
        )

        assertEquals(80, pill.stoneCost)
        assertEquals(60, pill.qiCost)
        assertEquals(2, pill.count)
    }
}
