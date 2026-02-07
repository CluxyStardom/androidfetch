package com.example.androidfetch

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText

class WidgetConfigActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var imageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            findViewById<ImageView>(R.id.image_preview).setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_config_activity)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val context = this
        val prefs = context.getSharedPreferences(context.packageName, 0)

        // Load existing preferences
        val titleEditText = findViewById<TextInputEditText>(R.id.edittext_title)
        titleEditText.setText(prefs.getString("widget_title_$appWidgetId", "Androidfetch by cluxystardom"))

        findViewById<MaterialCheckBox>(R.id.checkbox_model).isChecked = prefs.getBoolean("show_model_$appWidgetId", true)
        findViewById<MaterialCheckBox>(R.id.checkbox_android_version).isChecked = prefs.getBoolean("show_android_version_$appWidgetId", true)
        findViewById<MaterialCheckBox>(R.id.checkbox_api_level).isChecked = prefs.getBoolean("show_api_level_$appWidgetId", true)
        findViewById<MaterialCheckBox>(R.id.checkbox_manufacturer).isChecked = prefs.getBoolean("show_manufacturer_$appWidgetId", true)
        findViewById<MaterialCheckBox>(R.id.checkbox_brand).isChecked = prefs.getBoolean("show_brand_$appWidgetId", true)
        findViewById<MaterialCheckBox>(R.id.checkbox_product).isChecked = prefs.getBoolean("show_product_$appWidgetId", true)
        findViewById<MaterialCheckBox>(R.id.checkbox_hardware).isChecked = prefs.getBoolean("show_hardware_$appWidgetId", true)
        findViewById<MaterialCheckBox>(R.id.checkbox_uptime).isChecked = prefs.getBoolean("show_uptime_$appWidgetId", true)
        findViewById<MaterialCheckBox>(R.id.checkbox_kernel_version).isChecked = prefs.getBoolean("show_kernel_version_$appWidgetId", true)
        findViewById<MaterialCheckBox>(R.id.checkbox_ram).isChecked = prefs.getBoolean("show_ram_$appWidgetId", true)
        findViewById<MaterialCheckBox>(R.id.checkbox_storage).isChecked = prefs.getBoolean("show_storage_$appWidgetId", true)

        val imagePreview = findViewById<ImageView>(R.id.image_preview)
        val imageUriString = prefs.getString("image_uri_$appWidgetId", null)
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString)
            imagePreview.setImageURI(imageUri)
        }

        findViewById<Button>(R.id.button_select_image).setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        val colorNames = resources.getStringArray(R.array.color_names)
        val colorDropdown = findViewById<AutoCompleteTextView>(R.id.color_dropdown)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, colorNames)
        colorDropdown.setAdapter(adapter)

        val selectedColorName = prefs.getString("text_color_name_$appWidgetId", "White")
        colorDropdown.setText(selectedColorName, false)

        val fontSizeSlider = findViewById<Slider>(R.id.slider_font_size)
        fontSizeSlider.value = prefs.getFloat("font_size_$appWidgetId", 14f)

        val transparencySlider = findViewById<Slider>(R.id.slider_transparency)
        transparencySlider.value = prefs.getInt("background_transparency_$appWidgetId", 128).toFloat()

        // Save button listener
        findViewById<Button>(R.id.button_save).setOnClickListener {
            val edit = prefs.edit()

            edit.putString("widget_title_$appWidgetId", titleEditText.text.toString())

            edit.putBoolean("show_model_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_model).isChecked)
            edit.putBoolean("show_android_version_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_android_version).isChecked)
            edit.putBoolean("show_api_level_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_api_level).isChecked)
            edit.putBoolean("show_manufacturer_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_manufacturer).isChecked)
            edit.putBoolean("show_brand_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_brand).isChecked)
            edit.putBoolean("show_product_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_product).isChecked)
            edit.putBoolean("show_hardware_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_hardware).isChecked)
            edit.putBoolean("show_uptime_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_uptime).isChecked)
            edit.putBoolean("show_kernel_version_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_kernel_version).isChecked)
            edit.putBoolean("show_ram_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_ram).isChecked)
            edit.putBoolean("show_storage_$appWidgetId", findViewById<MaterialCheckBox>(R.id.checkbox_storage).isChecked)

            if (imageUri != null) {
                edit.putString("image_uri_$appWidgetId", imageUri.toString())
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(imageUri!!, takeFlags)
            } else {
                edit.remove("image_uri_$appWidgetId")
            }

            val selectedColor = when (colorDropdown.text.toString()) {
                "Red" -> ContextCompat.getColor(context, R.color.text_red)
                "Green" -> ContextCompat.getColor(context, R.color.text_green)
                "Blue" -> ContextCompat.getColor(context, R.color.text_blue)
                "Yellow" -> ContextCompat.getColor(context, R.color.text_yellow)
                "Pink" -> ContextCompat.getColor(context, R.color.text_pink)
                "Cyan" -> ContextCompat.getColor(context, R.color.text_cyan)
                "Orange" -> ContextCompat.getColor(context, R.color.text_orange)
                else -> ContextCompat.getColor(context, R.color.text_white)
            }
            edit.putInt("text_color_$appWidgetId", selectedColor)
            edit.putString("text_color_name_$appWidgetId", colorDropdown.text.toString())

            edit.putFloat("font_size_$appWidgetId", findViewById<Slider>(R.id.slider_font_size).value)

            edit.putInt("background_transparency_$appWidgetId", findViewById<Slider>(R.id.slider_transparency).value.toInt())

            edit.apply()

            // Trigger widget update
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val myWidget = Intent(context, MyWidgetProvider::class.java)
            myWidget.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = intArrayOf(appWidgetId)
            myWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            sendBroadcast(myWidget)

            // Finish the activity
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(RESULT_OK, resultValue)
            finish()
        }
    }
}