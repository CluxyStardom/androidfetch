package com.example.androidfetch

import android.app.ActivityManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.util.TypedValue
import android.widget.RemoteViews
import java.io.File
import java.text.DecimalFormat

class MyWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val prefs = context.getSharedPreferences(context.packageName, 0)
            val edit = prefs.edit()
            edit.remove("widget_title_$appWidgetId")
            edit.remove("show_manufacturer_$appWidgetId")
            edit.remove("show_model_$appWidgetId")
            edit.remove("show_android_version_$appWidgetId")
            edit.remove("show_api_level_$appWidgetId")
            edit.remove("show_brand_$appWidgetId")
            edit.remove("show_product_$appWidgetId")
            edit.remove("show_hardware_$appWidgetId")
            edit.remove("show_uptime_$appWidgetId")
            edit.remove("show_kernel_version_$appWidgetId")
            edit.remove("show_ram_$appWidgetId")
            edit.remove("show_storage_$appWidgetId")
            edit.remove("text_color_$appWidgetId")
            edit.remove("background_transparency_$appWidgetId")
            edit.remove("font_size_$appWidgetId")
            edit.apply()
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        val prefs = context.getSharedPreferences(context.packageName, 0)

        // Set up the click intent to launch the configuration activity
        val configIntent = Intent(context, WidgetConfigActivity::class.java)
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val configPendingIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_root, configPendingIntent)

        // Set background transparency
        val transparency = prefs.getInt("background_transparency_$appWidgetId", 128)
        val backgroundColor = Color.argb(transparency, 0, 0, 0)
        views.setInt(R.id.widget_root, "setBackgroundColor", backgroundColor)

        // Set title
        val title = prefs.getString("widget_title_$appWidgetId", "Androidfetch by cluxystardom")
        views.setTextViewText(R.id.widget_title, title)

        views.setImageViewResource(R.id.widget_logo, R.drawable.android_icon)

        val infoBuilder = StringBuilder()
        if (prefs.getBoolean("show_manufacturer_$appWidgetId", true)) {
            infoBuilder.append("Manufacturer: ${Build.MANUFACTURER}\n")
        }
        if (prefs.getBoolean("show_model_$appWidgetId", true)) {
            infoBuilder.append("Model: ${Build.MODEL}\n")
        }
        if (prefs.getBoolean("show_android_version_$appWidgetId", true)) {
            infoBuilder.append("Android Version: ${Build.VERSION.RELEASE}\n")
        }
        if (prefs.getBoolean("show_api_level_$appWidgetId", true)) {
            infoBuilder.append("API Level: ${Build.VERSION.SDK_INT}\n")
        }
        if (prefs.getBoolean("show_brand_$appWidgetId", true)) {
            infoBuilder.append("Brand: ${Build.BRAND}\n")
        }
        if (prefs.getBoolean("show_product_$appWidgetId", true)) {
            infoBuilder.append("Product: ${Build.PRODUCT}\n")
        }
        if (prefs.getBoolean("show_hardware_$appWidgetId", true)) {
            infoBuilder.append("Hardware: ${Build.HARDWARE}\n")
        }
        if (prefs.getBoolean("show_uptime_$appWidgetId", true)) {
            infoBuilder.append("Uptime: ${getUptime()}\n")
        }
        if (prefs.getBoolean("show_kernel_version_$appWidgetId", true)) {
            infoBuilder.append("Kernel: ${System.getProperty("os.version")}\n")
        }
        if (prefs.getBoolean("show_ram_$appWidgetId", true)) {
            infoBuilder.append("RAM: ${getRamUsage(context)}\n")
        }
        if (prefs.getBoolean("show_storage_$appWidgetId", true)) {
            infoBuilder.append("Storage: ${getStorageUsage()}\n")
        }

        val textColor = prefs.getInt("text_color_$appWidgetId", Color.WHITE)

        views.setTextViewText(R.id.widget_info_text, infoBuilder.toString().trim())
        views.setTextColor(R.id.widget_info_text, textColor)

        // Apply font size
        val fontSize = prefs.getFloat("font_size_$appWidgetId", 14f)
        views.setTextViewTextSize(R.id.widget_info_text, TypedValue.COMPLEX_UNIT_SP, fontSize)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getUptime(): String {
        val uptimeMillis = SystemClock.elapsedRealtime()
        val seconds = uptimeMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return "${hours}h ${minutes % 60}m ${seconds % 60}s"
    }

    private fun getRamUsage(context: Context): String {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memoryInfo)
        val totalRam = memoryInfo.totalMem / (1024 * 1024)
        val availableRam = memoryInfo.availMem / (1024 * 1024)
        return "$availableRam MB / $totalRam MB"
    }

    private fun getStorageUsage(): String {
        val path: File = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong

        val totalGb = (totalBlocks * blockSize) / (1024.0 * 1024.0 * 1024.0)
        val availableGb = (availableBlocks * blockSize) / (1024.0 * 1024.0 * 1024.0)
        val df = DecimalFormat("#.##")

        return "${df.format(availableGb)} GB / ${df.format(totalGb)} GB"
    }
}