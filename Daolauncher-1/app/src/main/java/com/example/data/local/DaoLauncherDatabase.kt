package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AlchemyPill
import com.example.data.model.AppRestriction
import com.example.data.model.CultivationProfile
import com.example.data.model.CultivationTask
import com.example.data.model.DaoCategory
import com.example.data.model.FocusSession
import com.example.data.model.PinnedApp
import com.example.data.model.TaskGrade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CultivationProfile::class,
        CultivationTask::class,
        AlchemyPill::class,
        FocusSession::class,
        PinnedApp::class,
        AppRestriction::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(CultivationConverters::class)
abstract class DaoLauncherDatabase : RoomDatabase() {

    abstract fun cultivationDao(): CultivationDao

    companion object {
        @Volatile
        private var INSTANCE: DaoLauncherDatabase? = null

        fun getDatabase(context: Context): DaoLauncherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DaoLauncherDatabase::class.java,
                    "dao_launcher_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getDatabase(context)
                    val dao = database.cultivationDao()

                    // Seed default profile
                    dao.insertOrUpdateProfile(
                        CultivationProfile(
                            id = 1,
                            realmLevel = 1, // Start at Qi Condensation
                            realmSubStage = 1,
                            currentQi = 80L,
                            maxQi = 300L,
                            spiritStones = 120L,
                            totalTasksCompleted = 0,
                            totalFocusMinutes = 0L,
                            consecutiveDaoDays = 1,
                            lastActiveDate = "",
                            tribulationBreakthroughCount = 0,
                            daoTitle = "Dao Apprentice"
                        )
                    )

                    // Seed initial cultivation quests
                    val starterTasks = listOf(
                        CultivationTask(
                            title = "Morning Dantian Breathing & Intention",
                            description = "Sit facing East, gather pure solar Qi and set the 3 primary Daoist objectives of the day.",
                            grade = TaskGrade.EARTH,
                            category = DaoCategory.MIND_MEDITATION,
                            priority = 3,
                            isDailyRecurring = true
                        ),
                        CultivationTask(
                            title = "Deep Work: Complete Core Sect Project",
                            description = "Maintain 90 minutes of undistracted focus without yielding to earthly digital temptations.",
                            grade = TaskGrade.HEAVEN,
                            category = DaoCategory.SECT_DUTY,
                            priority = 2
                        ),
                        CultivationTask(
                            title = "Body Tempering: Physical Training",
                            description = "Refine the mortal sinews with 30 minutes of martial tempering or cardiovascular exertion.",
                            grade = TaskGrade.EARTH,
                            category = DaoCategory.SWORD_BODY,
                            priority = 1,
                            isDailyRecurring = true
                        ),
                        CultivationTask(
                            title = "Study Sacred Texts & Code Architecture",
                            description = "Inscribe sacred scrolls, read 20 pages of wisdom or review design blueprints.",
                            grade = TaskGrade.EARTH,
                            category = DaoCategory.DAO_STUDY,
                            priority = 1
                        )
                    )
                    starterTasks.forEach { dao.insertTask(it) }

                    // Seed Alchemy Pavilion recipes
                    val starterPills = listOf(
                        AlchemyPill(
                            id = "qi_gathering_pill",
                            name = "Qi Gathering Pill",
                            chineseName = "聚气丹",
                            description = "Distilled from Thousand-Year Spirit Grass. Doubles all Qi harvested from tasks for 2 hours.",
                            stoneCost = 60,
                            qiCost = 30,
                            count = 2,
                            effectDescription = "+100% Qi Bonus on Quests",
                            durationMinutes = 120,
                            tier = 1
                        ),
                        AlchemyPill(
                            id = "mind_clearing_elixir",
                            name = "Mind-Clearing Elixir",
                            chineseName = "清心灵露",
                            description = "Refined morning dew from Heavenly Jade Peak. Accelerates focus meditation Qi absorption rate by 1.5x.",
                            stoneCost = 90,
                            qiCost = 50,
                            count = 1,
                            effectDescription = "+50% Focus Meditation Qi Yield",
                            durationMinutes = 60,
                            tier = 2
                        ),
                        AlchemyPill(
                            id = "breakthrough_pill",
                            name = "Foundation Solidifying Pill",
                            chineseName = "筑基破境丹",
                            description = "Protects meridians during Heavenly Tribulation breakthroughs, guaranteeing 100% breakthrough success.",
                            stoneCost = 200,
                            qiCost = 150,
                            count = 1,
                            effectDescription = "Guaranteed Tribulation Success",
                            durationMinutes = 0,
                            tier = 3
                        ),
                        AlchemyPill(
                            id = "heavenly_tea",
                            name = "Nine-Leaf Enlightenment Tea",
                            chineseName = "九叶悟道茶",
                            description = "Brews ancient celestial wisdom, instantly bestowing 120 Spirit Stones and clarity.",
                            stoneCost = 150,
                            qiCost = 80,
                            count = 0,
                            effectDescription = "+120 Spirit Stones instantly",
                            durationMinutes = 0,
                            tier = 4
                        )
                    )
                    dao.insertPills(starterPills)
                }
            }
        }
    }
}
