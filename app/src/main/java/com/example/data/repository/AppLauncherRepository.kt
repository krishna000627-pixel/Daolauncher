package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.example.data.model.AppInfo
import com.example.data.model.AppRestriction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppLauncherRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    suspend fun getInstalledApps(
        pinnedPackageNames: Set<String>,
        restrictionsMap: Map<String, AppRestriction> = emptyMap()
    ): List<AppInfo> = withContext(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(intent, 0)
        }

        val myPackageName = context.packageName

        resolveInfos
            .filter { it.activityInfo.packageName != myPackageName }
            .map { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                val actName = resolveInfo.activityInfo.name
                val label = resolveInfo.loadLabel(packageManager).toString()
                val icon = try {
                    resolveInfo.loadIcon(packageManager)
                } catch (e: Exception) {
                    null
                }
                val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val category = categorizeApp(label, pkgName)

                val restriction = restrictionsMap[pkgName]
                val isExplicitDistraction = restriction?.isDistraction
                val isExplicitStudy = restriction?.isStudy
                val isHidden = restriction?.isHidden ?: false
                val unlockExpiresAt = restriction?.unlockExpiresAt ?: 0L

                // If user hasn't explicitly set, use intelligent default heuristics
                val isDistraction = isExplicitDistraction ?: (isDefaultDistractionApp(label, pkgName) && (isExplicitStudy != true))
                val isStudy = isExplicitStudy ?: (isDefaultStudyApp(label, pkgName) && (isExplicitDistraction != true))

                AppInfo(
                    packageName = pkgName,
                    activityName = actName,
                    label = label,
                    icon = icon,
                    isPinned = pinnedPackageNames.contains(pkgName),
                    isSystemApp = isSystem,
                    category = category,
                    isDistraction = isDistraction,
                    isStudy = isStudy,
                    isHidden = isHidden,
                    unlockExpiresAt = unlockExpiresAt
                )
            }
            .sortedBy { it.label.lowercase() }
    }

    fun launchApp(app: AppInfo): Boolean {
        return launchPackage(app.packageName)
    }

    fun launchPackage(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun openAppDetails(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun openDefaultLauncherSettings() {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else {
                Intent(Settings.ACTION_HOME_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (ignored: Exception) {}
        }
    }

    fun isDefaultLauncher(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
        } else {
            @Suppress("DEPRECATION")
            packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        return resolveInfo?.activityInfo?.packageName == context.packageName
    }

    private fun isDefaultDistractionApp(label: String, pkg: String): Boolean {
        val lower = "$label $pkg".lowercase()
        return lower.contains("youtube") || lower.contains("tiktok") || lower.contains("instagram") ||
                lower.contains("twitter") || lower.contains("reddit") || lower.contains("facebook") ||
                lower.contains("netflix") || lower.contains("twitch") || lower.contains("snapchat") ||
                lower.contains("game") || lower.contains("roblox") || lower.contains("steam") ||
                lower.contains("disney") || lower.contains("primevideo")
    }

    private fun isDefaultStudyApp(label: String, pkg: String): Boolean {
        val lower = "$label $pkg".lowercase()
        return lower.contains("book") || lower.contains("kindle") || lower.contains("duolingo") ||
                lower.contains("anki") || lower.contains("coursera") || lower.contains("edx") ||
                lower.contains("study") || lower.contains("learn") || lower.contains("notion") ||
                lower.contains("read") || lower.contains("docs") || lower.contains("dictionary") ||
                lower.contains("code") || lower.contains("canvas") || lower.contains("classroom")
    }

    private fun categorizeApp(label: String, pkg: String): String {
        val lower = "$label $pkg".lowercase()
        return when {
            lower.contains("phone") || lower.contains("dialer") || lower.contains("call") -> "Communication"
            lower.contains("message") || lower.contains("sms") || lower.contains("chat") || lower.contains("telegram") || lower.contains("whatsapp") -> "Communication"
            lower.contains("chrome") || lower.contains("browser") || lower.contains("firefox") || lower.contains("web") -> "Browser"
            lower.contains("camera") || lower.contains("photo") || lower.contains("gallery") -> "Media"
            lower.contains("note") || lower.contains("task") || lower.contains("calendar") || lower.contains("clock") || lower.contains("cultivat") -> "Productivity"
            lower.contains("setting") || lower.contains("system") || lower.contains("file") -> "System"
            else -> "General"
        }
    }
}
