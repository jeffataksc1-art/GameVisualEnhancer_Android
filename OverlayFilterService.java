package com.gamevisualenhancer.android;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;

public class OverlayFilterService extends Service {
    private static OverlayFilterService instance;
    private WindowManager windowManager;
    private FilterView overlay;
    private static int saturation = 120;
    private static int contrast = 110;
    private static int brightness = 100;

    public static void updateValues(int s, int c, int b) {
        saturation = s;
        contrast = c;
        brightness = b;
        if (instance != null) instance.applyFilter();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlay = new FilterView(this);
        applyFilter();

        int type = android.os.Build.VERSION.SDK_INT >= 26
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        windowManager.addView(overlay, params);
    }

    private void applyFilter() {
        if (overlay == null) return;
        // A normal Android overlay cannot apply a ColorMatrix to another app's surface.
        // Instead, use a real translucent tint/dimming layer that works without root.
        float dim = Math.max(0f, Math.min(1f, (100f - brightness) / 100f));
        float strength = Math.max(0f, Math.min(1f, Math.abs(saturation - 100) / 100f));
        int alpha = Math.round(180 * Math.max(strength, dim));
        int color;
        if (saturation >= 100) {
            // Slight warm tint as visual intensity increases.
            color = Color.argb(alpha, 255, 245, 225);
        } else {
            // Slight cool tint as intensity is reduced.
            color = Color.argb(alpha, 220, 235, 255);
        }
        if (brightness < 100) {
            int dimAlpha = Math.round(180 * dim);
            color = Color.argb(dimAlpha, 0, 0, 0);
        } else if (brightness > 100) {
            int brightAlpha = Math.round(100 * Math.min(1f, (brightness - 100) / 50f));
            color = Color.argb(brightAlpha, 255, 255, 255);
        }
        overlay.setTint(color);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        applyFilter();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (overlay != null && windowManager != null) {
            windowManager.removeView(overlay);
        }
        overlay = null;
        instance = null;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
