package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class TaskGrade(val displayName: String, val qiReward: Int, val stoneReward: Int, val color: Color) {
    MORTAL("Mortal Grade", 25, 10, Color(0xFF94A3B8)),
    EARTH("Earth Grade", 60, 25, Color(0xFF10B981)),
    HEAVEN("Heaven Grade", 150, 70, Color(0xFF38BDF8)),
    DIVINE("Divine Grade", 350, 180, Color(0xFFF59E0B))
}

enum class DaoCategory(val displayName: String, val iconName: String, val color: Color) {
    DAO_STUDY("Dao Study & Learning", "MenuBook", Color(0xFF38BDF8)),
    SWORD_BODY("Sword & Body Tempering", "FitnessCenter", Color(0xFFEF4444)),
    MIND_MEDITATION("Mind Clarity & Meditation", "SelfImprovement", Color(0xFF8B5CF6)),
    SECT_DUTY("Sect Duty & Work", "Assignment", Color(0xFF10B981)),
    ALCHEMY("Alchemy & Creation", "Science", Color(0xFFF59E0B)),
    MUNDANE("Mundane Errand", "Home", Color(0xFF94A3B8))
}

enum class CultivationRealm(
    val level: Int,
    val title: String,
    val chineseTitle: String,
    val maxQi: Long,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val auraGlowColor: Color,
    val bgStart: Color,
    val bgEnd: Color,
    val runeColor: Color
) {
    MORTAL(
        level = 0,
        title = "Mortal Spark",
        chineseTitle = "凡人筑体",
        maxQi = 100L,
        description = "A mortal body awakening to the primordial spiritual energy of heaven and earth.",
        primaryColor = Color(0xFF78716C),
        secondaryColor = Color(0xFFA8A29E),
        accentColor = Color(0xFFD6D3D1),
        auraGlowColor = Color(0x3378716C),
        bgStart = Color(0xFF0F172A),
        bgEnd = Color(0xFF1E293B),
        runeColor = Color(0xFFCBD5E1)
    ),
    QI_CONDENSATION(
        level = 1,
        title = "Qi Condensation",
        chineseTitle = "炼气期",
        maxQi = 300L,
        description = "Drawing pure spiritual Qi into the meridians, refining focus and vitality.",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF34D399),
        accentColor = Color(0xFF6EE7B7),
        auraGlowColor = Color(0x5510B981),
        bgStart = Color(0xFF062016),
        bgEnd = Color(0xFF0B1B26),
        runeColor = Color(0xFFA7F3D0)
    ),
    FOUNDATION_ESTABLISHMENT(
        level = 2,
        title = "Foundation Establishment",
        chineseTitle = "筑基期",
        maxQi = 750L,
        description = "Forging a crystal-clear Daoist spiritual foundation in the Dantian sea.",
        primaryColor = Color(0xFF06B6D4),
        secondaryColor = Color(0xFF38BDF8),
        accentColor = Color(0xFF7DD3FC),
        auraGlowColor = Color(0x5506B6D4),
        bgStart = Color(0xFF072430),
        bgEnd = Color(0xFF0B192E),
        runeColor = Color(0xFFBAE6FD)
    ),
    CORE_FORMATION(
        level = 3,
        title = "Golden Core Formation",
        chineseTitle = "金丹期",
        maxQi = 1600L,
        description = "Condensing vast spiritual energy into an indestructible Golden Core.",
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFFBBF24),
        accentColor = Color(0xFFFDE68A),
        auraGlowColor = Color(0x66F59E0B),
        bgStart = Color(0xFF261904),
        bgEnd = Color(0xFF1A132F),
        runeColor = Color(0xFFFEF08A)
    ),
    NASCENT_SOUL(
        level = 4,
        title = "Nascent Soul",
        chineseTitle = "元婴期",
        maxQi = 3200L,
        description = "Birthing an ethereal spiritual avatar from the core, transcending mortal bounds.",
        primaryColor = Color(0xFF8B5CF6),
        secondaryColor = Color(0xFFA78BFA),
        accentColor = Color(0xFFC4B5FD),
        auraGlowColor = Color(0x668B5CF6),
        bgStart = Color(0xFF1C0E36),
        bgEnd = Color(0xFF0D0B24),
        runeColor = Color(0xFFDDD6FE)
    ),
    SOUL_FORMATION(
        level = 5,
        title = "Soul Formation",
        chineseTitle = "化神期",
        maxQi = 6000L,
        description = "Merging consciousness with the cosmic Dao, manipulating spiritual laws.",
        primaryColor = Color(0xFF6366F1),
        secondaryColor = Color(0xFF818CF8),
        accentColor = Color(0xFFA5B4FC),
        auraGlowColor = Color(0x666366F1),
        bgStart = Color(0xFF11133D),
        bgEnd = Color(0xFF170C2E),
        runeColor = Color(0xFFC7D2FE)
    ),
    VOID_REFINEMENT(
        level = 6,
        title = "Void Refinement",
        chineseTitle = "炼虚期",
        maxQi = 11000L,
        description = "Refining the primordial void, shattering spatial illusions with pure willpower.",
        primaryColor = Color(0xFFEC4899),
        secondaryColor = Color(0xFFF472B6),
        accentColor = Color(0xFFFBCFE8),
        auraGlowColor = Color(0x66EC4899),
        bgStart = Color(0xFF2A081D),
        bgEnd = Color(0xFF0F0B24),
        runeColor = Color(0xFFFCE7F3)
    ),
    BODY_INTEGRATION(
        level = 7,
        title = "Body & Dao Integration",
        chineseTitle = "合体期",
        maxQi = 20000L,
        description = "Harmonizing mortal vessel and universal laws into an invincible divine form.",
        primaryColor = Color(0xFFEF4444),
        secondaryColor = Color(0xFFF97316),
        accentColor = Color(0xFFFDBA74),
        auraGlowColor = Color(0x66EF4444),
        bgStart = Color(0xFF2B0A0A),
        bgEnd = Color(0xFF1D0B2B),
        runeColor = Color(0xFFFED7AA)
    ),
    GREAT_TRIBULATION(
        level = 8,
        title = "Great Tribulation",
        chineseTitle = "大乘渡劫",
        maxQi = 35000L,
        description = "Enduring the Nine Heavenly Lightning Tribulations to shed mortal karma.",
        primaryColor = Color(0xFF9333EA),
        secondaryColor = Color(0xFFC084FC),
        accentColor = Color(0xFFE9D5FF),
        auraGlowColor = Color(0x779333EA),
        bgStart = Color(0xFF1D0636),
        bgEnd = Color(0xFF071B2B),
        runeColor = Color(0xFFF3E8FF)
    ),
    IMMORTAL_ASCENSION(
        level = 9,
        title = "Immortal Ascension",
        chineseTitle = "飞升真仙",
        maxQi = 60000L,
        description = "Shattering the celestial gates, ascending to the Heavenly Realm of True Immortals.",
        primaryColor = Color(0xFFFBBF24),
        secondaryColor = Color(0xFF38BDF8),
        accentColor = Color(0xFFFFFFFF),
        auraGlowColor = Color(0x88FBBF24),
        bgStart = Color(0xFF1E1702),
        bgEnd = Color(0xFF041B26),
        runeColor = Color(0xFFFEF3C7)
    );

    companion object {
        fun fromLevel(level: Int): CultivationRealm {
            return entries.find { it.level == level.coerceIn(0, 9) } ?: MORTAL
        }
    }
}
