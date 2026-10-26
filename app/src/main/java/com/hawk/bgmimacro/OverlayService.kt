package com.hawk.bgmimacro

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button

class OverlayService : Service() {

    private lateinit var wm: WindowManager
    private lateinit var panel: View
    private lateinit var params: WindowManager.LayoutParams

    override fun onCreate() {
        super.onCreate()
        startForeground(1, buildNotification())

        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        panel = LayoutInflater.from(this).inflate(R.layout.overlay_panel, null)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 60
            y = 300
        }

        wm.addView(panel, params)
        makeDraggable()

        panel.findViewById<Button>(R.id.btnAkm).setOnClickListener {
            TouchAccessibilityService.instance?.startRecoil(Gun.AKM)
        }
        panel.findViewById<Button>(R.id.btnM14).setOnClickListener {
            TouchAccessibilityService.instance?.startRecoil(Gun.M14)
        }
        panel.findViewById<Button>(R.id.btnRec).setOnClickListener {
            TouchAccessibilityService.instance?.stopRecoil()
        }
        panel.findViewById<Button>(R.id.btnHide).setOnClickListener {
            panel.visibility = View.GONE
        }
    }

    private fun makeDraggable() {
        var initialX = 0; var initialY = 0
        var touchX = 0f; var touchY = 0f

        panel.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x; initialY = params.y
                    touchX = event.rawX; touchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - touchX).toInt()
                    params.y = initialY + (event.rawY - touchY).toInt()
                    wm.updateViewLayout(panel, params)
                    true
                }
                else -> false
            }
        }
    }

    private fun buildNotification(): Notification {
        val channelId = "macro_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel(channelId, "Macro", NotificationManager.IMPORTANCE_LOW)
            )
        }
        return Notification.Builder(this, channelId)
            .setContentTitle("BGMI Macro")
            .setContentText("Overlay running")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
