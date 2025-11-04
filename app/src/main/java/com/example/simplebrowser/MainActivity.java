package com.example.simplebrowser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText urlEdit;
    private Button shortcutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        urlEdit = new EditText(this);
        shortcutBtn = new Button(this);
        shortcutBtn.setText("生成快捷方式");

        shortcutBtn.setOnClickListener(v -> {
            String url = urlEdit.getText().toString();
            if (!url.isEmpty()) {
                android.content.pm.ShortcutManager shortcutManager =
                        (android.content.pm.ShortcutManager) getSystemService(Context.SHORTCUT_SERVICE);
                android.content.pm.ShortcutInfo shortcut = new android.content.pm.ShortcutInfo.Builder(this, url)
                        .setShortLabel("网页快捷方式")
                        .setLongLabel(url)
                        .setIcon(android.graphics.drawable.Icon.createWithResource(this, android.R.drawable.ic_menu_view))
                        .setIntent(new Intent(this, WebViewActivity.class)
                                .setAction(Intent.ACTION_VIEW)
                                .putExtra("url", url))
                        .build();
                shortcutManager.requestPinShortcut(shortcut, null);
            }
        });

        // 简单线性布局
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.addView(urlEdit, new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(shortcutBtn, new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
        setContentView(layout);
    }
}