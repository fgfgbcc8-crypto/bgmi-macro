package com.hawk.bgmimacro

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 96, 48, 48)
        }

        val title = TextView(this).apply {
            text = "BGMI MACRO — 909"
            textSize = 22f
            setTextColor(0xFFFF4444.toInt())
        }

        val btnOverlay = Button(this).apply {
            text = "1. منح إذن الأوفرلاي"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startActivity(Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    ))
                }
            }
        }

        val btnAccess = Button(this).apply {
            text = "2. تشغيل خدمة الوصول"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        val btnStart = Button(this).apply {
            text = "3. تشغيل الأوفرلاي"
            setOnClickListener {
                startService(Intent(this@MainActivity, OverlayService::class.java))
            }
        }

        val btnStop = Button(this).apply {
            text = "إيقاف الأوفرلاي"
            setOnClickListener {
                stopService(Intent(this@MainActivity, OverlayService::class.java))
            }
        }

        layout.addView(title)
        layout.addView(btnOverlay)
        layout.addView(btnAccess)
        layout.addView(btnStart)
        layout.addView(btnStop)
        setContentView(layout)
    }
}
