package com.gamevisualenhancer.android;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends android.app.Activity {
    private TextView status;
    private SeekBar saturation;
    private SeekBar contrast;
    private SeekBar brightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUi();
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 24);
        root.setBackgroundColor(Color.rgb(18, 18, 20));

        TextView title = new TextView(this);
        title.setText("Game Visual Enhancer");
        title.setTextColor(Color.WHITE);
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView device = new TextView(this);
        device.setText("Perfil: POCO X6 Pro • ARM64 • HyperOS/Android");
        device.setTextColor(Color.LTGRAY);
        device.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(device, new LinearLayout.LayoutParams(-1, -2));

        status = new TextView(this);
        status.setTextColor(Color.LTGRAY);
        status.setPadding(0, 28, 0, 16);
        root.addView(status, new LinearLayout.LayoutParams(-1, -2));

        saturation = addSlider(root, "Saturação", 0, 200, 120);
        contrast = addSlider(root, "Contraste", 50, 150, 110);
        brightness = addSlider(root, "Brilho", 50, 150, 100);

        Button permission = new Button(this);
        permission.setText("Permitir sobreposição");
        permission.setOnClickListener(v -> openOverlaySettings());
        root.addView(permission, new LinearLayout.LayoutParams(-1, -2));

        Button start = new Button(this);
        start.setText("Ativar filtro");
        start.setOnClickListener(v -> startFilter());
        root.addView(start, new LinearLayout.LayoutParams(-1, -2));

        Button stop = new Button(this);
        stop.setText("Desativar filtro");
        stop.setOnClickListener(v -> stopFilter());
        root.addView(stop, new LinearLayout.LayoutParams(-1, -2));

        TextView note = new TextView(this);
        note.setText("O filtro usa uma camada de sobreposição. Ele altera a aparência da tela, não aumenta FPS nem substitui otimizações do sistema.");
        note.setTextColor(Color.GRAY);
        note.setPadding(0, 20, 0, 0);
        root.addView(note, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
        updateStatus();
    }

    private SeekBar addSlider(LinearLayout root, String label, int min, int max, int value) {
        TextView tv = new TextView(this);
        tv.setText(label);
        tv.setTextColor(Color.WHITE);
        tv.setPadding(0, 12, 0, 0);
        root.addView(tv, new LinearLayout.LayoutParams(-1, -2));

        SeekBar bar = new SeekBar(this);
        bar.setMax(max - min);
        bar.setProgress(value - min);
        bar.setTag(min);
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int progress, boolean fromUser) {
                int actual = (int) s.getTag() + progress;
                tv.setText(label + ": " + actual + "%");
                if (fromUser) OverlayFilterService.updateValues(
                        saturation == null ? 120 : saturation.getProgress() + (int) saturation.getTag(),
                        contrast == null ? 110 : contrast.getProgress() + (int) contrast.getTag(),
                        brightness == null ? 100 : brightness.getProgress() + (int) brightness.getTag());
            }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        tv.setText(label + ": " + value + "%");
        root.addView(bar, new LinearLayout.LayoutParams(-1, -2));
        return bar;
    }

    private void openOverlaySettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void startFilter() {
        if (!Settings.canDrawOverlays(this)) {
            status.setText("Primeiro conceda a permissão de sobreposição.");
            return;
        }
        OverlayFilterService.updateValues(
                saturation.getProgress() + (int) saturation.getTag(),
                contrast.getProgress() + (int) contrast.getTag(),
                brightness.getProgress() + (int) brightness.getTag());
        startService(new Intent(this, OverlayFilterService.class));
        status.setText("Filtro ativo.");
    }

    private void stopFilter() {
        stopService(new Intent(this, OverlayFilterService.class));
        status.setText("Filtro desativado.");
    }

    private void updateStatus() {
        status.setText(Settings.canDrawOverlays(this)
                ? "Permissão de sobreposição: concedida"
                : "Permissão de sobreposição: necessária");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (status != null) updateStatus();
    }
}
