package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AlchemyPill
import com.example.data.model.AppRestriction
import com.example.data.model.CultivationProfile
import com.example.data.model.CultivationTask
import com.example.data.model.FocusSession
import com.example.data.model.PinnedApp
import kotlinx.coroutines.flow.Flow

@Dao
interface CultivationDao {
    // --- Profile ---
    @Query("SELECT * FROM cultivation_profile WHERE id = 1")
    fun getProfile(): Flow<CultivationProfile?>

    @Query("SELECT * FROM cultivation_profile WHERE id = 1")
    suspend fun getProfileDirect(): CultivationProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: CultivationProfile)

    @Query("UPDATE cultivation_profile SET calculatorPasscode = :passcode WHERE id = 1")
    suspend fun updateCalculatorPasscode(passcode: String)

    // --- Tasks / Quests ---
    @Query("SELECT * FROM cultivation_tasks ORDER BY isCompleted ASC, priority DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<CultivationTask>>

    @Query("SELECT * FROM cultivation_tasks WHERE isCompleted = 0 ORDER BY priority DESC, createdAt DESC")
    fun getActiveTasks(): Flow<List<CultivationTask>>

    @Query("SELECT * FROM cultivation_tasks WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTasks(): Flow<List<CultivationTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: CultivationTask): Long

    @Update
    suspend fun updateTask(task: CultivationTask)

    @Query("DELETE FROM cultivation_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)

    @Query("UPDATE cultivation_tasks SET isCompleted = :completed, completedAt = :timestamp WHERE id = :taskId")
    suspend fun setTaskCompleted(taskId: Long, completed: Boolean, timestamp: Long?)

    // --- Alchemy Inventory ---
    @Query("SELECT * FROM alchemy_inventory ORDER BY tier ASC")
    fun getAllPills(): Flow<List<AlchemyPill>>

    @Query("SELECT * FROM alchemy_inventory WHERE id = :pillId")
    suspend fun getPill(pillId: String): AlchemyPill?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPills(pills: List<AlchemyPill>)

    @Update
    suspend fun updatePill(pill: AlchemyPill)

    // --- Focus Sessions ---
    @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC LIMIT 30")
    fun getRecentFocusSessions(): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSession)

    // --- Pinned Apps ---
    @Query("SELECT * FROM pinned_apps ORDER BY orderIndex ASC")
    fun getPinnedApps(): Flow<List<PinnedApp>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun pinApp(pinnedApp: PinnedApp)

    @Query("DELETE FROM pinned_apps WHERE packageName = :packageName")
    suspend fun unpinApp(packageName: String)

    // --- App Restrictions (Distraction & Study & Hidden) ---
    @Query("SELECT * FROM app_restrictions")
    fun getAllRestrictions(): Flow<List<AppRestriction>>

    @Query("SELECT * FROM app_restrictions WHERE packageName = :packageName")
    suspend fun getRestriction(packageName: String): AppRestriction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRestriction(restriction: AppRestriction)

    @Query("UPDATE app_restrictions SET unlockExpiresAt = :expiresAt WHERE packageName = :packageName")
    suspend fun updateUnlockExpiry(packageName: String, expiresAt: Long)

    @Query("UPDATE app_restrictions SET isDistraction = :isDistraction WHERE packageName = :packageName")
    suspend fun setDistractionStatus(packageName: String, isDistraction: Boolean)

    @Query("UPDATE app_restrictions SET isStudy = :isStudy WHERE packageName = :packageName")
    suspend fun setStudyStatus(packageName: String, isStudy: Boolean)

    @Query("UPDATE app_restrictions SET isHidden = :isHidden WHERE packageName = :packageName")
    suspend fun setHiddenStatus(packageName: String, isHidden: Boolean)
}
